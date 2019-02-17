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

import edu.sunysb.ess.quilf.swing.TQPanel;

public class TQuartz extends TPhase {
	public static final int AQtz = 0;
	public static final int Qtz = 0;
	public static THelgQtz P_Qtz_Helg;
	public static THaas P_Qtz_Haas;

	// private TRow ASiO2;

	public TQuartz() {
		super("quartz");
		// quartz = this;
	}

	public boolean readFile(StreamTokenizer F) {
		super.readFile(F);
		P_Qtz_Helg = (THelgQtz) u0P[lowerComponent];
		P_Qtz_Haas = new THaas();
		P_Qtz_Haas = (THaas) readu0P(F);
		return true;
	}

	public int setRows(TQPanel p, int r) {
		/*TRow ASiO2 =*/ addComponent(p, "aSiO2", r, 1);
		return r;
	}

	public void writeFile(DataOutputStream F) {
		super.writeFile(F);
		// if (P_Qtz_Haas == null)
		// MessageBox(HWND_DESKTOP, "null pointer in TQuartz::WriteFile", "QUILF", MB_ICONEXCLAMATION);
		// else
		writeu0P(F, P_Qtz_Haas);
	}
}