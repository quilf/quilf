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
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

public class TPlainL extends JLabel {
	final int MinimumSize = 80;
	final int MininumHeight = 12;

	/**
	 * TextLabel constructor comment.
	 */
	public TPlainL() {
		super();
		setBackground(Color.white);
		setForeground(Color.darkGray);
	}

	/**
	 * TextLabel constructor comment.
	 * @param text java.lang.String
	 */
	public TPlainL(String text) {
		super(text);
		setBackground(Color.white);
		setForeground(Color.darkGray);
	}

	/**
	 * TextLabel constructor comment.
	 * @param text java.lang.String
	 * @param alignment int
	 */
	public TPlainL(String text, int alignment) {
		super(text, alignment);
		setBackground(Color.white);
		setForeground(Color.darkGray);
	}

	public Dimension getMinimumSize() {
		//Dimension d = super.getMinimumSize();
		return new Dimension(MinimumSize, MininumHeight);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public String toString() {
		return getText();
	}
}