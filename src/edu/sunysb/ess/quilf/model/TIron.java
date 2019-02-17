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

public class TIron extends TPhase {
	// private static final int Fe0 = 0;
	public static final int aFe = 0;

	// private TRow AFe;

	public TIron() {
		super("Iron");
		// iron = this;
	}

	public int setRows(TQPanel p, int row) {
		/* TRow AFe = */addComponent(p, "aFe", row, 1);
		return row;
	}
}