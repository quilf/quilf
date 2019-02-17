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
import java.awt.GridLayout;
import java.awt.Label;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TOlWtPt extends TWeight {
    //------------------------------ TOlWtPt ------------------------------
    private JLabel IDD_FO,  IDD_FA,  IDD_XFO,  IDD_XFA,  IDD_XLA;
    static final int XMg = 0;
    static final int YCa = 1;
    static final int XFa = 2;
    private TNumFld xFo;
    private TNumFld xLa;

    public TOlWtPt() {
        super();
    }

    public TOlWtPt(TNumFld xFo, TNumFld xLa) {
        super();
        IDD_FO = new JLabel();
        IDD_FA = new JLabel();
        IDD_XFO = new JLabel();
        IDD_XLA = new JLabel();
        IDD_XFA = new JLabel();
        OxNo = 4;
        NumSites = 3;
        for (int I = SiO2; I <= Na2O; I++) {
            OxSet[I] = false;
        }
        OxSet[SiO2] = true;
        OxSet[FeO] = true;
        OxSet[MnO] = true;
        OxSet[MgO] = true;
        OxSet[CaO] = true;
        init("Olivine");
        DisplayComponents(null, null);
        this.xFo = xFo;
        this.xLa = xLa;
    }

    public void ok() {
        xFo.setText(IDD_XFO.getText());
        xLa.setText(IDD_XLA.getText());
    }

    void CalcComponents(double[] W, double[] XC) {
        //          Given the formula in W, calculate the components of Olivine;

        int J;
        double Sum;

        Sum = W[CaO] + W[MgO] + W[FeO];
        if (Sum <= Zero) {
            for (J = 0; J < MaxComp; J++) {
                XC[J] = 0.0;
            }
            return;
        }
        XC[YCa] = W[CaO] / Sum;
        XC[XMg] = W[MgO] / Sum;
        XC[XFa] = W[FeO] / Sum;
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
        p.add(new Label("Fo"));
        p.add(IDD_FO);
        p.add(new Label("Fa"));
        p.add(IDD_FA);
        return p;
    }

    void DisplayComponents(double[] W, double[] XC) {
        double Sum;
        String s;

        if (W == null) {

            IDD_FO.setText("");
            IDD_FA.setText("");
        } else {
            Sum = XC[XMg] + XC[XFa];
            if (Sum <= Zero) {
                Sum = 1.0;
            }
            s = format(XC[XMg] / Sum);
            IDD_FO.setText(s);
            s = format(XC[XFa] / Sum);
            IDD_FA.setText(s);
        }
        if (XC == null) {
            IDD_XFO.setText("");
            IDD_XLA.setText("");
            IDD_XFA.setText("");
        } else {
            s = format(XC[XMg]);
            IDD_XFO.setText(s);
            s = format(XC[YCa]);
            IDD_XLA.setText(s);
            s = format(XC[XFa]);
            IDD_XFA.setText(s);
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
        p.add(new Label("XFo"));
        p.add(IDD_XFO);
        p.add(new Label("XLa"));
        p.add(IDD_XLA);
        p.add(new Label("XFa"));
        p.add(IDD_XFA);
        return p;
    }
}
