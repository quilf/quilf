package edu.sunysb.ess.quilf.swing.print;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
/**
 * this  is from a forum posting
 * http://forum.java.sun.com/thread.jspa?threadID=601884&messageID=4215335
 * 
 * @author unknown
 *
 */
class PartialLineBorder extends AbstractBorder {
	boolean top, left, bottom, right;

	public PartialLineBorder(boolean t, boolean l, boolean b, boolean r) {
		top = t;
		left = l;
		bottom = b;
		right = r;
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(2, 2, 2, 2);
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1.0f));

		if (top) {
			g2.drawLine(x, y, x + width, y);
		}
		if (left) {
			g2.drawLine(x, y, x, y + height);
		}
		if (bottom) {
			g2.drawLine(x, y + height, x + width, y + height);
		}
		if (right) {
			g2.drawLine(x + width, y, x + width, y + height);
		}
	}
}
