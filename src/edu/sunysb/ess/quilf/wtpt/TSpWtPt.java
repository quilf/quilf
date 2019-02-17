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

import edu.sunysb.ess.quilf.swing.TNumFld;
import java.awt.Label;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TSpWtPt extends TWeight {
    //------------------------------ TSpWtPt ------------------------------
    private JLabel IDD_NTI,  IDD_NMG,  IDD_NMN,  IDD_MT,  IDD_USP;
    final static int nTi = 0;
    final static int nMg = 1;
    final static int nMn = 2;
    private TNumFld nti;
    private TNumFld nmg;
    private TNumFld nmn;

    public TSpWtPt() {
        super();
    }

    public TSpWtPt(TNumFld nti, TNumFld nmg, TNumFld nmn) {
        super();
        IDD_NTI = new JLabel("");
        IDD_NMG = new JLabel("");
        IDD_NMN = new JLabel("");
        IDD_USP = new JLabel("");
        IDD_MT = new JLabel("");
        OxNo = 4;
        NumSites = 3;
        for (int I = SiO2; I <= Na2O; I++) {
            OxSet[I] = false;
        }
        OxSet[Al2O3] = true;
        OxSet[TiO2] = true;
        OxSet[Fe2O3] = true;
        OxSet[FeO] = true;
        OxSet[MnO] = true;
        OxSet[MgO] = true;
        init("Spinel");
        DisplayComponents(null, null);
        this.nti = nti;
        this.nmg = nmg;
        this.nmn = nmn;
    }

    public void ok() {
        nti.setText(IDD_NTI.getText());
        nmg.setText(IDD_NMG.getText());
        nmn.setText(IDD_NMN.getText());
    }

    void CalcComponents(double[] W, double[] XC) {
        //          Given the formula in W, calculate the components of spinel

        double MgNum;

        MgNum = W[FeO] + W[MgO];
        if (MgNum > Zero) {
            MgNum = W[MgO] / (W[FeO] + W[MgO]);
        } else {
            MgNum = 0.0;
        }
        XC[nMn] = W[MnO];
        XC[nMg] = W[MgO] - MgNum * W[Al2O3] / 2;
        XC[nTi] = W[TiO2];
    }

    /**
     * This method was created by a SmartGuide.
     * @return java.awt.Panel
     */
    public JPanel componentPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 2));
        p.add(new Label("For Geothermometry"));
        p.add(new Label(""));	// to keep above on a single line
        p.add(new Label("Mole Fraction"));
        p.add(new Label(""));	// to keep above on a single line
        p.add(new Label("Usp"));
        p.add(IDD_USP);
        p.add(new Label("Mt"));
        p.add(IDD_MT);
        return p;
    }

    void DisplayComponents(double[] W, double[] XC) {
        double MgNum, XSpin, XUsp, XMt, XQan, XJac, Sum;
        String s;

        if (W == null) {
            IDD_USP.setText("");
            IDD_MT.setText("");
        } else {
            MgNum = W[FeO] + W[MgO];
            if (MgNum > Zero) {
                MgNum = W[MgO] / (W[FeO] + W[MgO]);
            } else {
                MgNum = 0.0;
            }
            XSpin = W[Al2O3] / 2;
            XJac = W[MnO] / 2;
            XQan = (W[MgO] - MgNum * XSpin) / 2.0;
            XUsp = W[TiO2] - XQan - XJac;
            XMt = W[Fe2O3] / 2.0;
            Sum = XUsp + XMt + XQan;
            if (Sum <= Zero) {
                Sum = 1.0;
            }
            s = format((XUsp + XQan) / Sum);
            IDD_USP.setText(s);
            s = format(XMt / Sum);
            IDD_MT.setText(s);
        }
        if (XC == null) {
            IDD_NTI.setText("");
            IDD_NMG.setText("");
            IDD_NMN.setText("");
        } else {
            s = format(XC[nTi]);
            IDD_NTI.setText(s);
            s = format(XC[nMg]);
            IDD_NMG.setText(s);
            s = format(XC[nMn]);
            IDD_NMN.setText(s);
        }
    }

    /**
     * This method was created by a SmartGuide.
     * @return java.awt.Panel
     */
    public JPanel quilfPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 2));
        p.add(new Label("for QUIlF"));
        p.add(new Label(""));	// to keep above on a single line
//	Label IDD_NTI, IDD_NMG, IDD_NMN, IDD_MT, IDD_USP;
        p.add(new Label("NTi"));
        p.add(IDD_NTI);
        p.add(new Label("NMg"));
        p.add(IDD_NMG);
        p.add(new Label("NMn"));
        p.add(IDD_NMN);
        return p;
    }
}
