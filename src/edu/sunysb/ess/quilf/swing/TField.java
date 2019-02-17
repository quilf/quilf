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
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JPanel;

public class TField extends JPanel {
	private FlowLayout flow;
	private TNumFld tf;
	private TTextF ttf;

	public TField() {
		setlayout();
	}

	public TField(TLabel label) {
		setlayout();
		add(label);
	}

	public TField(TPlainL label1, TPlainL label2, TPlainL label3) {
		setlayout();
		add(new TPlainL(""));
		add(label1);
		add(label2);
		add(label3);
	}

	public TField(TTextF edit, TTextLab calc, TTextLab err) {
		setlayout();
		add(new TTextLab(""));
		add(edit);
		add(calc);
		add(err);
	}

	public TField(TTextLab label, TLabel edit) {
		setlayout();
		add(label);
		add(edit);
	}

	public TField(TTextLab lab, TNumFld tf, TTextLab lf, TTextLab cf, TTextLab ef) {
		setlayout();
		add(lab);
		if (tf != null) {
			add(tf);
			this.tf = tf;
		}
		if (lf != null) {
			add(lf);
		}
		if (cf != null) {
			add(cf);
			add(ef);
		}
	}

	public TField(TTextLab label, TTextF tf) {
		setlayout();
		add(label);
		add(tf);
		ttf = tf;
	}

	public TField(TTextLab label1, TTextLab label2, TTextLab label3) {
		setlayout();
		add(new TTextLab(""));
		add(label1);
		add(label2);
		add(label3);
	}

	public TField(TTextLab lab, TTextLab tl, TTextLab cf, TTextLab ef) {
		setlayout();
		add(lab);
		if (tl != null) {
			add(tl);
		}
		if (cf != null) {
			add(cf);
		}
		if (ef != null) {
			add(ef);
		}
	}

	public void setlayout() {
		flow = new FlowLayout(0, 0, 0);
		setFont(new Font("Helvetica", Font.PLAIN, 12));
		setLayout(flow);
		tf = null;
		ttf = null;
	}

	public TNumFld getTf() {
		return tf;
	}

	public TTextF getTtf() {
		return ttf;
	}
}