package edu.sunysb.ess.quilf.wtpt;

import edu.sunysb.ess.quilf.swing.TNumFld;

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
public class TOpxWtPt extends TPxWtPt {

    public TOpxWtPt() {
    	super();
    }

    public TOpxWtPt(TNumFld xWo, TNumFld xEn) {
        super(Opx, xWo, xEn);
        init("Orthopyroxene");
        DisplayComponents(null, null);
    }

    void CalcComponents(double[] W, double[] XC) {
        //          Given the formula in W, calculate the components of orthopyroxene

        int I;
        int J;
        double Sum, F2, M2, R2, AlIV, AlVI, N1, Ti4, TiAl2, NaTiAl, R3;
        double ExcessAl, ExcessNa;
        double NaR3, Cats;

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
        //project from mg,fe others
        AlIV = 2 - W[SiO2];
        if (AlIV < 0) {
            AlIV = W[Al2O3];
        }
        if (AlIV > W[Al2O3]) {
            AlIV = W[Al2O3];
        }
        AlVI = W[Al2O3] - AlIV;
        R3 = AlVI + W[Fe2O3] + W[Cr2O3];
        N1 = W[Na2O];
        // NaR3 = (VIAl, Fe3+, Cr3+)
        if (N1 > R3) {
            NaR3 = R3;
        } else {
            NaR3 = N1;
        }
        R3 = R3 - NaR3;
        N1 = N1 - NaR3;
        Ti4 = W[TiO2];
        //          NATiAl component
        if (N1 < Ti4) {
            NaTiAl = N1;
        } else {
            NaTiAl = Ti4;
        }
        if (AlIV < NaTiAl) {
            NaTiAl = AlIV;
        }
        N1 = N1 - NaTiAl;
        Ti4 = Ti4 - NaTiAl;
        AlIV = AlIV - NaTiAl;
        //          TiAl2 component
        TiAl2 = AlIV / 2;
        if (TiAl2 > Ti4) {
            TiAl2 = Ti4;
        }
        Ti4 = Ti4 - TiAl2;
        AlIV = AlIV - 2 * TiAl2;
        //          cats:=r3cats comonents
        if (R3 > AlIV) {
            Cats = AlIV;
        } else {
            Cats = R3;
        }
        AlIV = AlIV - Cats;
        R3 = R3 - Cats;
        F2 = W[FeO];
        M2 = W[MgO];
        R2 = F2 + M2;
        //          fmtial2 component
        if (TiAl2 > R2) {
            TiAl2 = R2;
        }
        if (R2 > Zero) {
            F2 = F2 * (1 - TiAl2 / R2);
            M2 = M2 * (1 - TiAl2 / R2);
            R2 = R2 - TiAl2;
            //          fmr3al component
            if (Cats > R2) {
                Cats = R2;
            }
            F2 = F2 * (1 - Cats / R2);
            M2 = M2 * (1 - Cats / R2);
        }
        if (AlIV > Zero) {
            ExcessAl = AlIV;
        } else {
            ExcessAl = -R3;
        }
        ExcessNa = N1;
        // wo:=projected from Mg, Fe2+ others
        XC[YCa] = W[CaO];
        XC[XMg] = M2;
        XC[XFs] = F2;
        Normalize(XC);
    }
}
