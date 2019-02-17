package edu.sunysb.ess.quilf.model;
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
import java.io.DataOutputStream;
import java.io.StreamTokenizer;

import edu.sunysb.ess.quilf.swing.TField;
import edu.sunysb.ess.quilf.swing.TLabel;
import edu.sunysb.ess.quilf.swing.TQPanel;
import edu.sunysb.ess.quilf.swing.TRow;

// ------------------------------ TOlivine ------------------------------
public class TOlivine extends TPxOl {
	public static final int fo = 0;
	public static final int fa = 1;
	public static final int mo = 2;
	public static final int ks = 3;
	static THelg P_Fa_Helg;
	static THaas P_Fa_Haas;
	TRow xFo, xFa, xLa, xFe;

	TOlivine() {
		super("olivine");
		// olivine = this;
	}

	public boolean readFile(StreamTokenizer F) {
		super.readFile(F);
		P_Fa_Helg = (THelg) u0P[fa];
		P_Fa_Haas = new THaas();
		P_Fa_Haas = (THaas) readu0P(F);
		return true;
	}

	public int setRows(TQPanel p, int row) {
		p.add(new TField(new TLabel("Olivine")), row++, 0);
		xFo = addComponent(p, "XFo", row++, 0);
		xLa = addComponent(p, "XLa", row++, 0);
		xFa = addTextComponent(p, "XFa", row++, 0);
		xFe = addTextComponent(p, "XFe", row++, 0);
		return row;
	}

	int StdStates(int Err) {
		// Calculates olivine standard state and model values at T and P.

		int U;
		int I;
		double DFa, U0_Fa_Haas;

		Err = P_Fa_Haas.updateU0(tk, p);
		U0_Fa_Haas = P_Fa_Haas.U0;
		Err = u0P[fa].updateU0(tk, p);
		DFa = U0_Fa_Haas - u0P[fa].U0;
		for (U = lowerVariable; U <= upperVariable; U++) {
			sln[G][U] = sln[H][U] - tk * sln[S][U] + p * sln[V][U];
		}
		for (I = lowerComponent; I <= upperComponent; I++) {
			if (u0P[I] == null)
				u0Array[G][I] = 0.0;
			else {
				Err = u0P[I].updateU0(tk, p);
				u0Array[G][I] = u0P[I].U0;
			}
		}
		u0Array[G][fo] += DFa;
		u0Array[G][fa] = U0_Fa_Haas;
		u0Array[G][mo] += DFa;
		u0Array[G][ks] += DFa;
		sln[G][f01] = 2.0 * (u0Array[G][mo] - u0Array[G][ks]) + u0Array[G][fa] - u0Array[G][fo];
		return Err;
	}

	public void writeFile(DataOutputStream F) {
		super.writeFile(F);
		// if (P_Fa_Haas == null)
		// MessageBox(HWND_DESKTOP, "null pointer in TOlivine::WriteFile", "QUILF", MB_ICONEXCLAMATION);
		// else
		writeu0P(F, P_Fa_Haas);
	}
}