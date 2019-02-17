package edu.sunysb.ess.quilf.model;

import edu.sunysb.ess.quilf.swing.TQPanel;
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

public class TKarooite extends TPhase {
	public static final int AMgTi2O5 = 0;

	public TKarooite() {
		super("Karooite");
	}

	public int setRows(TQPanel p, int row) {
		/*TRow	aCaTiSiO5 =*/ addComponent(p, "aMgTi2O5", row, 1);
		return row;
	}
}