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
import javax.swing.JLabel;

public class TLabel extends JLabel {

	/**
	 * TLabel constructor comment.
	 */
	public TLabel() {
		super();
	}

	/**
	 * TLabel constructor comment.
	 * @param text java.lang.String
	 */
	public TLabel(String text) {
		super(text);
	}

	/**
	 * TLabel constructor comment.
	 * @param text java.lang.String
	 * @param alignment int
	 */
	public TLabel(String text, int alignment) {
		super(text, alignment);
	}
}