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

public class TIlWtPt extends TWeight {

   private JLabel IDD_ILM, IDD_HEM, IDD_XHEM, IDD_XGK, IDD_XPY;
    final static int XHem = 0;
    final static int XGk = 1;
    final static int XPy = 2;
    final static int XIl = 3;
    private TNumFld xhem;
    private TNumFld xgk;
    private TNumFld xpy;

    public TIlWtPt() {
        super();
    }

    public TIlWtPt(TNumFld xhem, TNumFld xgk, TNumFld xpy) {
        super();
        IDD_ILM = new JLabel("");
        IDD_HEM = new JLabel("");
        IDD_XHEM = new JLabel("");
        IDD_XGK = new JLabel("");
        IDD_XPY = new JLabel("");
        OxNo = 3;
        NumSites = 2;
        for (int I = SiO2; I <= Na2O; I++) {
            OxSet[I] = false;
        }
        OxSet[Al2O3] = true;
        OxSet[TiO2] = true;
        OxSet[Fe2O3] = true;
        OxSet[FeO] = true;
        OxSet[MnO] = true;
        OxSet[MgO] = true;
        init("Ilmenite");
        DisplayComponents(null, null);
        this.xhem = xhem;
        this.xgk = xgk;
        this.xpy = xpy;
    }

    public void ok() {
        xhem.setText(IDD_XHEM.getText());
        xgk.setText(IDD_XGK.getText());
        xpy.setText(IDD_XPY.getText());
    }

    void CalcComponents(double[] W, double[] XC) {
        //          Given the formula in W, calculate the components of ilmenite

        double Fe, Ti1, Sum;

        Fe = W[FeO] + W[Fe2O3];
        Ti1 = W[TiO2] - W[MgO] - W[MnO];
        XC[XPy] = W[MnO];
        XC[XGk] = W[MgO];
        XC[XIl] = Ti1 + W[Al2O3] / 2.0;
        Fe = Fe - Ti1;
        XC[XHem] = Fe / 2.0;
        // Xc[XCor]:=W[Al2O3]/2.0;
        W[Fe2O3] = 2.0 * XC[XHem];
        // For Quilf
        Sum = XC[XIl] + XC[XHem] + XC[XGk] + XC[XPy];
        if (Sum <= Zero) {
            Sum = 1.0;
        }
        XC[XHem] = XC[XHem] / Sum;
        XC[XGk] = XC[XGk] / Sum;
        XC[XPy] = XC[XPy] / Sum;
    }

    /**
     * This method was created by a SmartGuide.
     * @return java.awt.Panel
     * */
    public JPanel componentPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 2));
        p.add(new Label("For Geothermometry"));
        p.add(new Label(""));	// to keep above on a single line
        p.add(new Label("Mole Fraction"));
        p.add(new Label(""));	// to keep above on a single line
//	Label IDD_ILM, IDD_HEM, IDD_XHEM, IDD_XGK, IDD_XPY;
        p.add(new Label("Ilm"));
        p.add(IDD_ILM);
        p.add(new Label("Hem"));
        p.add(IDD_HEM);
        return p;
    }

    void DisplayComponents(double[] W, double[] XC) {
        double Sum;
        String s;

        if (W == null) {
            IDD_ILM.setText("");
            IDD_HEM.setText("");
        } else {
            Sum = XC[XIl] + XC[XHem];
            if (Sum <= Zero) {
                Sum = 1.0;
            }
            s = format(XC[XIl] / Sum);
            IDD_ILM.setText(s);
            s = format(XC[XHem] / Sum);
            IDD_HEM.setText(s);
        }
        if (XC == null) {
            IDD_XHEM.setText("");
            IDD_XGK.setText("");
            IDD_XPY.setText("");
        } else {
            s = format(XC[XHem]);
            IDD_XHEM.setText(s);
            s = format(XC[XGk]);
            IDD_XGK.setText(s);
            s = format(XC[XPy]);
            IDD_XPY.setText(s);
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
//	Label IDD_ILM, IDD_HEM, IDD_XHEM, IDD_XGK, IDD_XPY;
        p.add(new Label("XHem"));
        p.add(IDD_XHEM);
        p.add(new Label("XGk"));
        p.add(IDD_XGK);
        p.add(new Label("XPy"));
        p.add(IDD_XPY);
        return p;
    }
}
