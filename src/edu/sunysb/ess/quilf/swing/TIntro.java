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
/*
  draws a title and name centered in a canvas
*/
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class TIntro extends Canvas {
  private Color titleColor = Color.red;
  private Color bkdColor = Color.white;
  private Color borderColor = Color.black;

  private int w, h;
  private int edge = 24;
  private static final String title = "QUIlF"; // vers. 6";
  private static final String name =
 "Copyright 1998 - David Andersen";
  private Font namefont, titlefont, msgfont;


  public TIntro() {
	setBackground(borderColor);
	titlefont = new Font("TimesRoman", Font.ITALIC, 50);
	namefont = new Font("SansSerif", Font.BOLD, 20);
	msgfont = new Font("SansSerif", Font.PLAIN, 10);
  }    
  private void d(Graphics g, String s, Color c, Font f, int y, int off) {
	g.setFont(f);
	FontMetrics fm = g.getFontMetrics();
	g.setColor(c);
	g.drawString(s, (w - fm.stringWidth(s)) / 2 + off, y + off);
  }    
  public void paint(Graphics g) {
	Dimension d = size();
	w = d.width;
	h = d.height;
	g.setColor(bkdColor);
	g.fill3DRect(edge, edge, w - 2 * edge, h - 2 * edge, true);
	d(g, title, Color.black, titlefont, h / 2, 1);
	d(g, title, Color.lightGray, titlefont, h / 2, -1);
	d(g, title, titleColor, titlefont, h / 2, 0);
	d(g, name, Color.black, namefont, h * 3 / 4, 0);
	d(g, "please wait, reading files...", Color.darkGray, msgfont, h * 7 / 8, 0);
  }    
}