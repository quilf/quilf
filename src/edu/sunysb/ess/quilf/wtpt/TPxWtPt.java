package edu.sunysb.ess.quilf.wtpt;

/*
part of QUIlF
Copyright (c) 1998,2008 by David Andersen
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import edu.sunysb.ess.quilf.swing.SpringUtilities;
import edu.sunysb.ess.quilf.swing.TNumFld;

class TPxWtPt extends TWeight {
    //------------------------------ TPxWtPt ------------------------------
    private JLabel IDD_NCA,  IDD_NMG,  IDD_NFE,  IDD_XEN,  IDD_XWO,  IDD_XFS;
    final static int Aug = 0;
    final static int Pig = 1;
    final static int Opx = 2;
    final static int XMg = 0;
    final static int YCa = 1;
    final static int XFs = 2;
    private int Ph;
    private double E,  D;
    protected TNumFld xWo;
    protected TNumFld xEn;

    public TPxWtPt() {
        super();
    }

    public void ok() {
        xEn.setText(IDD_XEN.getText());
        xWo.setText(IDD_XWO.getText());
    }

    public TPxWtPt(int p, TNumFld xWo, TNumFld xEn) {
        super();
        IDD_NCA = new JLabel("");
        IDD_NMG = new JLabel("");
        IDD_NFE = new JLabel("");
        IDD_XEN = new JLabel("");
        IDD_XWO = new JLabel("");
        IDD_XFS = new JLabel("");
        Ph = p;
        OxNo = 6;
        NumSites = 4;
        for (int I = SiO2; I <= Na2O; I++) {
            OxSet[I] = true;
        }
        DisplayComponents(null, null);
        this.xWo = xWo;
        this.xEn = xEn;
    }

    void CalcComponents(double[] W, double[] XC) {						//defaults to Aug and Pig

        //  Given the formula in W, calculate the components of pyroxene

        int I;
        int J;
        double Sum, AlIV, AlVI, N1, F3, Jadeite, Acmite, MgNum, ExcessAl, ExcessNa;

        Sum = 0.0;
        for (I = SiO2; I <= Na2O; I++) {
            Sum = Sum + W[I];
        }
        if (Sum <= Zero) {
            for (J = 0; J < MaxComp; J++) {
                XC[J] = 0.0;
            }
            return;
        }
        //calculate acmite, jadeite, tetrahedal Al (charge bal.)
        ExcessAl = 0.0;
        ExcessNa = 0.0;
        MgNum = W[MgO] + W[FeO];
        if (MgNum > Zero) {
            MgNum = W[MgO] / (W[MgO] + W[FeO]);
        } else {
            MgNum = 0.0;
        }
        //AlIV= IVAl defined by charge balance
        AlIV = (W[Al2O3] - W[Na2O] + 2 * W[TiO2] +
                W[Fe2O3] + W[Cr2O3]) / 2;
        if (AlIV < 0) {
            AlIV = 0.0;
        }
        if (AlIV > W[Al2O3]) {
            ExcessAl = AlIV - W[Al2O3];      //excess Al
            AlIV = W[Al2O3];
        }
        AlVI = W[Al2O3] - AlIV;
        N1 = W[Na2O];
        F3 = W[Fe2O3];
        //                  acmite
        if (N1 > F3) {
            Acmite = F3;
        } else {
            Acmite = N1;
        }
        N1 = N1 - Acmite;
        F3 = F3 - Acmite;
        //                  jadeite
        if (N1 > AlVI) {
            Jadeite = AlVI;
        } else {
            Jadeite = N1;
        }
        N1 = N1 - Jadeite;
        AlVI = AlVI - Jadeite;
        ExcessNa = N1;				//excess Na

        if (Ph == Aug) {
            // wo=(ca+(acmite)-(fe3cats)-(alcats)-(crcats))/2
            // assume all cr is crcats
            XC[YCa] = (W[CaO] + Acmite - F3 - AlVI - W[Cr2O3]) / 2;
            if (XC[YCa] < 0) {
                XC[YCa] = 0.0;
            }
        } else if (Ph == Pig) {
            // wo=((2-(fe2+mg))+(acmite-(fe3cats)-(alcats)-(crcats))/2
            XC[YCa] = (2 - W[FeO] - W[MgO] +
                    Acmite - F3 - AlVI - W[Cr2O3]) / 2;
        }
        XC[XMg] = MgNum * (1 - XC[YCa]);
        XC[XFs] = (1 - MgNum) * (1 - XC[YCa]);
        Normalize(XC);
    }

    void CalcFormulaChargeBalance(double[] Wt, double[] W) {
        /*
        Calculate the pyroxene formula based on charge balance
        Al(IV)+Na = Al(VI) + 2Ti + Fe3+ + Cr3+
         */
        double Tolerance = 1.0e-05;
        boolean Balanced;
        double Fe2, High, Low, Mid;
        Formula(Wt, W);
        if (Wt[Fe2O3] >= Zero) {
            return;
        }
        Fe2 = Wt[FeO];
        if ((!OxSet[Fe2O3]) || E >= D) {
            return;
        }
        High = Wt[FeO];
        Low = 0.0;
        Wt[FeO] = 0.0;
        Balanced = false;
        do {
            Wt[Fe2O3] = (Fe2 - Wt[FeO]) * MolWt[Fe2O3] / (2.0 * MolWt[FeO]);
            Formula(Wt, W);
            Mid = E - D;
            if (Math.abs(Mid) < Tolerance) {
                Balanced = true;
            } else {
                if (Mid < 0.0) {
                    High = Wt[FeO];
                } else {
                    Low = Wt[FeO];
                }
                if (High == Low) {
                    Balanced = true;
                } else {
                    Wt[FeO] = (High + Low) / 2.0;
                }
            }
        } while (!Balanced);
    }

    /**
     * This method was created by a SmartGuide.
     * @return java.awt.Panel
     */
    public JPanel componentPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(5, 2));
        p.add(new Label("For Geothermometry"));
        p.add(new Label(""));	// to keep above on a single line
        p.add(new Label("Mole Fraction"));
        p.add(new Label(""));	// to keep above on a single line
        p.add(new Label("Ca"));
        p.add(IDD_NCA);
        p.add(new Label("Mg"));
        p.add(IDD_NMG);
        p.add(new Label("Fe"));
        p.add(IDD_NFE);
        return p;
    }

    void DisplayComponents(double[] W, double[] XC) {
        double Sum;
        String s;

        if (W == null) {
            IDD_NCA.setText("");
            IDD_NMG.setText("");
            IDD_NFE.setText("");
        } else {
            Sum = W[CaO] + W[MgO] + W[FeO];
            if (Sum <= Zero) {
                Sum = 1.0;
            }

            s = format(W[CaO] / Sum);
            IDD_NCA.setText(s);
            s = format(W[MgO] / Sum);
            IDD_NMG.setText(s);
            s = format(W[FeO] / Sum);
            IDD_NFE.setText(s);
        }
        if (XC == null) {
            IDD_XEN.setText("");
            IDD_XWO.setText("");
            IDD_XFS.setText("");
        } else {
            s = format(XC[XMg]);
            IDD_XEN.setText(s);
            s = format(XC[YCa]);
            IDD_XWO.setText(s);
            s = format(XC[XFs]);
            IDD_XFS.setText(s);
        }
    }

    void Formula(double[] Wt, double[] W) {
        int I;
        double Sum, AlIV, AlVI;

        Sum = 0.0;
        for (I = SiO2; I <= Na2O; I++) {
            W[I] = Wt[I] * NAnions[I] / MolWt[I];
            Sum = Sum + W[I];
        }
        if (Sum <= Zero) {
            E = 0.0;
            D = 0.0;
            return;
        }
        Sum = 6.0 / Sum;
        for (I = SiO2; I <= Na2O; I++) {
            W[I] = (W[I] * Sum * NCations[I]) / NAnions[I];
        }
        AlIV = 2.0 - W[SiO2];
        if (AlIV > W[Al2O3]) {
            AlIV = W[Al2O3];
        }
        if (AlIV < 0.0) {
            AlIV = 0.0;
        }
        AlVI = W[Al2O3] - AlIV;
        D = AlIV + W[Na2O];
        E = AlVI + 2.0 * W[TiO2] + W[Fe2O3] + W[Cr2O3];
    }

    void Normalize(double[] XC) {
        int I;
        double Sum;

        Sum = 0.0;
        for (I = XMg; I <= XFs; I++) {
            Sum = Sum + XC[I];
        }
        if (Sum > Zero) {
            for (I = XMg; I <= XFs; I++) {
                XC[I] = XC[I] / Sum;
            }
        }
    }

    /**
     * This method was created by a SmartGuide.
     * @return java.awt.Panel
     */
    public JPanel quilfPanel() {
        JPanel p = new JPanel(new SpringLayout());

//        p.setLayout(new GridLayout(5, 2));
        p.add(new JLabel("for QUIlF"));
        p.add(new Label(""));	// to keep above on a single line
//        p.add(new Label(""));	// to keep   a single line
//        p.add(new Label(""));	// to keep above on a single line
        p.add(new JLabel("XEn"));
        p.add(IDD_XEN);
        p.add(new JLabel("XWo"));
        p.add(IDD_XWO);
        p.add(new JLabel("XFs"));
        p.add(IDD_XFS);
        SpringUtilities.makeGrid(p,
                4, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad
        p.setOpaque(true); //content panes must be opaque
        return p;
    }
}
