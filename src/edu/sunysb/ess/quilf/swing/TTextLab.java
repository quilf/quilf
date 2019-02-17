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

import javax.swing.JTextField;

public class TTextLab extends JTextField {
	final int MinimumSize = 80;

	/**
	 * TextLabel constructor comment.
	 */
	public TTextLab() {
		super();
		setEditable(false);
		setBackground(new Color(192, 192, 192));
		setForeground(Color.darkGray);
	}

	/**
	 * TextLabel constructor comment.
	 * @param text java.lang.String
	 */
	public TTextLab(String text) {
		super(text);
		setEditable(false);
		setBackground(new Color(224, 224, 224));
		setForeground(Color.darkGray);
	}

	/**
	 * TextLabel constructor comment.
	 * @param text java.lang.String
	 * @param alignment int
	 */
	public TTextLab(String text, int alignment) {
		super(text, alignment);
		setEditable(false);
		setBackground(new Color(224, 224, 224));
		setForeground(Color.darkGray);
	}

	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		return new Dimension(MinimumSize, d.height);
	}

	public Dimension getPreferredSize() {
		Dimension d = getMinimumSize();
		return d;
	}

	public String toString() {
		return getText();
	}
}