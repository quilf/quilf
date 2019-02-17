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

import edu.sunysb.ess.quilf.swing.TField;
import edu.sunysb.ess.quilf.swing.TLabel;
import edu.sunysb.ess.quilf.swing.TQPanel;
import edu.sunysb.ess.quilf.swing.TRow;

public class TPx extends TPxOl {
	public static final int en = 0;
	public static final int FS = 1;
	public static final int di = 2;
	public static final int hd = 3; // pyroxene components
	public TRow xEn, xWo, xFs, xFe;

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param name
	 *            java.lang.String
	 */
	public TPx(String name) {
		super(name);
	}

	public int setRows(String type, TQPanel p, int row) {
		p.add(new TField(new TLabel(type)), row++, 1);
		xEn = addComponent(p, "XEn", row++, 1);
		xWo = addComponent(p, "XWo", row++, 1);
		xFs = addTextComponent(p, "XFs", row++, 1);
		xFe = addTextComponent(p, "XFe", row++, 1);
		return row;
	}
}