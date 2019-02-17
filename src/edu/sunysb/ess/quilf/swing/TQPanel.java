package edu.sunysb.ess.quilf.swing;
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class TQPanel extends JPanel implements Printable {
	private JComponent rows[][];
	public final static int MAXROWS = 22;
	public final static int MAXCOLS = 2;

	public int print(Graphics g, PageFormat pf, int pageIndex) {
		if (pageIndex >= 1) {
			return Printable.NO_SUCH_PAGE;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(pf.getImageableX(), pf.getImageableY());
		paint(g2);
		return Printable.PAGE_EXISTS;

	}

	public TQPanel() {
		super();
		rows = new JComponent[MAXROWS][MAXCOLS];
		for (int i = 0; i < MAXROWS; i++)
			for (int j = 0; j < MAXCOLS; j++)
				rows[i][j] = null;
	}

	public JComponent get(int r, int c) {
		if (r < MAXROWS && c < MAXCOLS)
			return rows[r][c];
		else
			return null;
	}

	public void add(JComponent component, int index) {
		int r = (index) % MAXROWS;
		int c = (index / MAXROWS);
		add(component, r, c);
	}

	public void add(JComponent component, int r, int c) {
		if (r < MAXROWS && c < MAXCOLS)
			rows[r][c] = component;
	}
}