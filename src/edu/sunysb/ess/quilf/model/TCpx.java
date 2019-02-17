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
import edu.sunysb.ess.quilf.swing.Quilf;
import edu.sunysb.ess.quilf.swing.TQPanel;

public class TCpx extends TPx {
	public TCpx() {
		super("clinopyroxene");
		//		clinopyroxene = this;
	}

	public int setRows(String type, TQPanel p, int row) {
		return super.setRows(type, p, row);
	}

	protected int stdStates(int Err) {
		//  Calculates pyroxene standard state and model values at T and P.

		int U;
		int I;
		double DFAQ;
		double Temp[] = new double[12];
		DFAQ = deltaFAQ();
		for (U = lowerVariable; U <= upperVariable; U++)
			sln[G][U] = sln[H][U] - tk * sln[S][U] + p * sln[V][U];
		for (I = lowerComponent; I <= upperComponent; I++) {
			if (u0P[I] == null)
				u0Array[G][I] = 0.0;
			else {
				Err = u0P[I].updateU0(tk, p);
				u0Array[G][I] = u0P[I].U0;
			}
		}
		for (U = TSln.DelEn; U <= TSln.DelFs; U++) {
			Temp[U] = Quilf.rctn.sln[H][U] - tk * Quilf.rctn.sln[S][U] + p * Quilf.rctn.sln[V][U];
		}
		Err = Quilf.orthopyroxene.u0P[en].updateU0(tk, p);
		u0Array[G][en] = Temp[TSln.DelEn] + Quilf.orthopyroxene.u0P[en].U0;
		Err = Quilf.orthopyroxene.u0P[FS].updateU0(tk, p);
		u0Array[G][FS] = Temp[TSln.DelFs] + Quilf.orthopyroxene.u0P[FS].U0;
		sln[G][f01] = 2.0 * (u0Array[G][di] - u0Array[G][hd]) + u0Array[G][FS] - u0Array[G][en];
		for (I = en; I <= hd; I++)
			u0Array[G][I] = u0Array[G][I] + DFAQ;
		return Err;
	}
}