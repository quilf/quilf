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
import edu.sunysb.ess.quilf.swing.TQPanel;

public class TOpx extends TPx {

	public TOpx() {
		super("orthopyroxene");
		// orthopyroxene = this;
	}

	public int setRows(String type, TQPanel p, int row) {
		return super.setRows(type, p, row);
	}

	protected int stdStates(int Err) {
		// Calculates the standard state for orthopyroxene and model values at T and P.

		double Temp[] = new double[12];
		double DFAQ = deltaFAQ();
		for (int U = lowerVariable; U <= upperVariable; U++)
			sln[G][U] = sln[H][U] - tk * sln[S][U] + p * sln[V][U];
		for (int I = lowerComponent; I <= upperComponent; I++) {
			if (u0P[I] == null)
				u0Array[G][I] = 0.0;
			else {
				Err = u0P[I].updateU0(tk, p);
				u0Array[G][I] = u0P[I].U0;
			}
		}
		for (int U = TSln.DelDi; U <= TSln.DelHd; U++)
			Temp[U] = TSln.Slns[TSln.Rctns].sln[H][U] - tk * TSln.Slns[TSln.Rctns].sln[S][U] + p * TSln.Slns[TSln.Rctns].sln[V][U];
		Err = TSln.Slns[TSln.Aug].u0P[di].updateU0(tk, p);
		u0Array[G][di] = -Temp[TSln.DelDi] + TSln.Slns[TSln.Aug].u0P[di].U0;
		Err = TSln.Slns[TSln.Aug].u0P[hd].updateU0(tk, p);
		u0Array[G][hd] = -Temp[TSln.DelHd] + TSln.Slns[TSln.Aug].u0P[hd].U0;
		sln[G][f01] = 2.0 * (u0Array[G][di] - u0Array[G][hd]) + u0Array[G][FS] - u0Array[G][en];
		for (int I = en; I <= hd; I++)
			u0Array[G][I] = u0Array[G][I] + DFAQ;
		return Err;
	}
}