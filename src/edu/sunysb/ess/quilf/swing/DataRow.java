package edu.sunysb.ess.quilf.swing;
/*
part of QUIlF

Copyright (c) 1998 by David Andersen

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

public class DataRow {
	public DataRow() {
		super();
		values = new Double[ACATISIO5 + 1];
	}

	public void clear() {
		for (int i = 0; i <= ACATISIO5; i++)
			values[i] = null;
	}
	public final static int TC = 0;
	public final static int PBAR = 1;
	public final static int FO2 = 2;
	public final static int DFMQ = 3;
	public final static int NTI = 4;
	public final static int NMG = 5;
	public final static int NMN = 6;
	public final static int XIL = 7;
	public final static int XHEM = 8;
	public final static int XGK = 9;
	public final static int XPY = 10;
	public final static int XFO = 11;
	public final static int XLA = 12;
	public final static int XFA = 13;
	public final static int XFEOL = 14;
	public final static int XENAUG = 15;
	public final static int XWOAUG = 16;
	public final static int XFSAUG = 17;
	public final static int XFEAUG = 18;
	public final static int XENPIG = 19;
	public final static int XWOPIG = 20;
	public final static int XFSPIG = 21;
	public final static int XFEPIG = 22;
	public final static int XENOPX = 23;
	public final static int XWOOPX = 24;
	public final static int XFSOPX = 25;
	public final static int XFEOPX = 26;
	public final static int ASIO2 = 27;
	public final static int AFE = 28;
	public final static int ATIO2 = 29;
	public final static int ACATISIO5 = 30;
	public final static int AMGTI2O5 = 31;
	private final static String[] names = { "T(C)", "P(bars)", "fO2", "DFMQ", "NTi", "NMg", "NMn", "XIl", "XHem", "XGk", "XPy", "XFo", "XLa", "XFa", "XFe(Ol)", "XEn(Aug)", "XWo(Aug)", "XFs(Aug)",
			"XFe(Aug)", "XEn(Pig)", "XWo(Pig)", "XFs(Pig)", "XFe(Pig", "XEn(Opx)", "XWo(Opx)", "XFs(Opx)", "xFe(Opx)", "aSiO2", "aFe",
			"aTiO2", "aCaTiSiO5" ,"aMgTi2O5"};

	public static String[] getNames() {
		return names;
	}

	public static String getName(int i) {
		if (i < names.length)
			return names[i];
		else
			return null;
	}
	private Double[] values;

	public Double getValue(int i) {
		if (i <= ACATISIO5)
			return values[i];
		else
			return null;
	}

	public void setValue(int i, Double d) {
		if (i <= ACATISIO5)
			values[i] = d;

	}

	public void setValue(int i, double d) {
		if (i <= ACATISIO5)
			values[i] = new Double(d);

	}

}
