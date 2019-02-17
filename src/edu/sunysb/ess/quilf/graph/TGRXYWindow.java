package edu.sunysb.ess.quilf.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Formatter;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JPanel;
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
public class TGRXYWindow extends JPanel {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TGRXYWindow.class);
	private double axglb;
	private double ayglb;
	private double bxglb;
	private double byglb;
	private int charheight;
	private int charwidth;
	private Vector<TGraphicsObject> PGO;
	private String graphTitle;
	private Axis xAxis;
	private int xmaxwin;
	private int xminwin;
	private Axis yAxis;
	private int ymaxwin;
	private int yminwin;

	public TGRXYWindow(String title, Axis xAxis, Axis yAxis) {
		super();
		this.graphTitle = title;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		SetDefaults();
	}

	public void addObject(TGraphicsObject TGO) {
		if (PGO == null)
			PGO = new Vector<TGraphicsObject>();
		PGO.add(TGO);
	}

	private double bottom(double r) {
		double f;

		if (r >= 0.0)
			return ((int) r);
		else {
			f = (int) r; /* whole number */
			if (f == r)
				return r;
			else
				return (f - 1.0);
		}
	} /* bottom */

	private D calcdx(Axis xaxis, Axis yaxis, double minwx, double minwy, double maxwx, double maxwy, double dpx, double dpy, double dx, double dy) {
		dx = dpy / dpx * (dy - 0.0125 * charheight - 0.025 * xaxis.getHeight()) + 0.005 * xaxis.getN() * xaxis.getWidth() + 0.01 * yaxis.getN() * yaxis.getWidth() + 0.015 * yaxis.getHeight();
		if ((minwx < 0.0) && (maxwx > 0.0))
			minwx = maxwx - dx;
		if ((minwx > 0.0) && (maxwx < 0.0))
			maxwx = minwx + dx;
		else {
			minwx = minwy + (dy - dx) / 2.0;
			maxwx = minwx + dx;
		}
		return new D(dx, minwx, maxwx);
	} /* calcdx */

	private D calcdy(Axis xaxis, Axis yaxis, double minwx, double minwy, double maxwx, double maxwy, double dpx, double dpy, double dx, double dy) {
		dy = dpx / dpy * (dx - 0.005 * xaxis.getN() * xaxis.getWidth() - 0.01 * yaxis.getN() * yaxis.getWidth() - 0.015 * yaxis.getHeight()) + 0.0125 * charheight + 0.025 * xaxis.getHeight();
		if ((minwy < 0.0) && (maxwy > 0.0))
			minwy = maxwy - dy;
		else if ((minwy > 0.0) && (maxwy < 0.0))
			maxwy = minwy + dy;
		else {
			minwy = minwx + (dx - dy) / 2.0;
			maxwy = minwy + dy;
		}
		return new D(dy, minwy, maxwy);
		// return dy;
	} /* calcdy */

	public void clear() {
		if (PGO!=null)
		PGO.clear();
		this.repaint();
	}

	private boolean clip(Graphics g, int xx1, int yy1, int xx2, int yy2, int xmin, int ymin, int xmax, int ymax, XYLine xyLine) {
		int ix1, iy1, ix2, iy2, dummy, x1, x2, y1, y2;

		x1 = xx1;
		y1 = yy1;
		x2 = xx2;
		y2 = yy2;
		ix1 = inside(x1, xmin, xmax);
		iy1 = inside(y1, ymin, ymax);
		ix2 = inside(x2, xmin, xmax);
		iy2 = inside(y2, ymin, ymax);
		if ((iy1 == iy2) && (iy1 != 0))
			return (false);
		if ((ix1 == ix2) && (ix1 != 0))
			return (false);
		if ((ix1 | ix2 | iy1 | iy2) != 0) {
			if (x1 != x2) {
				if (ix1 != 0) {
					if (ix1 < 0)
						dummy = xmin;
					else
						dummy = xmax;
					if (y2 != y1) {
						y1 += (int) Math.floor((double) (y2 - y1) / (x2 - x1) * (dummy - x1) + 0.5);
						iy1 = inside(y1, ymin, ymax);
					}
					x1 = dummy;
					ix1 = 0;
				}
				if (ix2 != 0 && x1 != x2) {
					if (ix2 < 0)
						dummy = xmin;
					else
						dummy = xmax;
					if (y2 != y1) {
						y2 = y1 + (int) Math.floor((double) (y2 - y1) / (x2 - x1) * (dummy - x1) + 0.5);
						iy2 = inside(y2, ymin, ymax);
					}
					x2 = dummy;
					ix2 = 0;
				}
			}
			if (y1 != y2) {
				if (iy1 != 0) {
					if (iy1 < 0)
						dummy = ymin;
					else
						dummy = ymax;
					if (x1 != x2) {
						x1 += (int) Math.floor((double) (x2 - x1) / (y2 - y1) * (dummy - y1) + 0.5);
						ix1 = inside(x1, xmin, xmax);
					}
					y1 = dummy;
					iy1 = 0;
				}
				if (iy2 != 0) {
					if (iy2 < 0)
						dummy = ymin;
					else
						dummy = ymax;
					if (x1 != x2) {
						x2 = x1 + (int) Math.floor((double) (x2 - x1) / (y2 - y1) * (dummy - y1) + 0.5);
						ix2 = inside(x2, xmin, xmax);
					}
					y2 = dummy;
					iy2 = 0;
				}
			}
		}
		xyLine.setX1(x1);
		xyLine.setY1(y1);
		xyLine.setX2(x2);
		xyLine.setY2(y2);
		return (ix1 == 0 && ix2 == 0 && iy1 == 0 && iy2 == 0);
	}

	private void definewindow(int x1, int y1, int x2, int y2) {
		xminwin = x1;
		xmaxwin = x2;
		yminwin = Math.min(y1, y2);
		ymaxwin = Math.max(y1, y2);
	}

	private void defineworld(double x1, double y1, double x2, double y2) {
		if ((x1 == x2) || (y1 == y2))
			return;
		bxglb = (xmaxwin - xminwin) / (x2 - x1);
		axglb = xmaxwin - x2 * bxglb;
		byglb = (ymaxwin - yminwin) / (y1 - y2);
		ayglb = ymaxwin - y1 * byglb;
	}

	private boolean divisible(double r, double n) {
		return (Math.abs(r / n - (int) Math.floor(r / n + 0.5)) < TGraph.ZERO);
	}

	public void drawline(Graphics g, double x1, double y1, double x2, double y2) {
		XYPoint ptl1, ptl2;

		ptl1 = new XYPoint(windowx(x1), windowy(y1));
		ptl2 = new XYPoint(windowx(x2), windowy(y2));
		XYLine xyLine = new XYLine();
		if (!clip(g, ptl1.getX(), ptl1.getY(), ptl2.getX(), ptl2.getY(), xminwin, yminwin, xmaxwin, ymaxwin, xyLine))
			return;
		//log.debug(x1 + "," + y1 + "-" + x2 + "," + y2 + " = " + xyLine.getX1() + "," + xyLine.getY1() + "-" + xyLine.getX2() + "," + xyLine.getY2());

		g.drawLine(xyLine.getX1(), xyLine.getY1(), xyLine.getX2(), xyLine.getY2());
	}

	private void drawxyaxes(Graphics g) {

		double dx, dy, dpx, dpy, minwx, minwy, maxwx, maxwy;

		minwx = xAxis.getMin();
		minwy = yAxis.getMin();
		maxwx = xAxis.getMax();
		maxwy = yAxis.getMax();
		dx = maxwx - minwx;
		dy = maxwy - minwy;

		xAxis.setWidth(charwidth);
		xAxis.setHeight(charheight);

		yAxis.setWidth(charwidth);
		yAxis.setHeight(charheight);
		D calcDY = null;
		D calcDX = null;
		if (((minwx < 0.0) || (maxwx < 0.0) || (dx < TGraph.ZERO)) && (dy > TGraph.ZERO)) {
			dpx = 640.0;
			dpy = 594.0;
			calcDX = calcdx(xAxis, yAxis, minwx, minwy, maxwx, maxwy, dpx, dpy, dx, dy);
			if (dx > dy) {
				minwx = minwy;
				maxwx = maxwy;
				calcDY = calcdy(xAxis, yAxis, minwx, minwy, maxwx, maxwy, dpx, dpy, dy, dx);
			}
		} else if (((minwy < 0.0) || (maxwy < 0.0) || (dy < TGraph.ZERO)) && (dx > TGraph.ZERO)) {
			dpx = 640.0;
			dpy = 594.0;
			calcDY = calcdy(xAxis, yAxis, minwx, minwy, maxwx, maxwy, dpx, dpy, dx, dy);
			if (dy > dx) {
				minwy = minwx;
				maxwy = maxwx;
				calcDX = calcdx(xAxis, yAxis, minwx, minwy, maxwx, maxwy, dpx, dpy, dy, dx);
			}
		}
		xyaxis(g, xAxis, yAxis, graphTitle);
	} /* drawxyaxes */

	public void getformat(Axis axis) {

		/* real:n:m format */
		double range;
		boolean negative;
		double max = axis.getMax();
		double min = axis.getMin();
		int n = 0;
		int m = 0;
		range = max - min;
		if (range <= 0.0) {
			n = 6;
			m = 4;
		} else {
			negative = (min < 0.0 || max < 0.0);
			if (min < 0.0)
				min = -min;
			if (max < 0.0)
				max = -max;
			if (min > max) /* because of prev line */
				max = min;
			n = (int) (Math.log(max) / TGraph.LN10);
			if (n < 0)
				n = -n;
			m = 2 - (int) (Math.log(range) / TGraph.LN10);
			if (m < 0)
				m = 0;
			n += m + 2;
			if (negative)
				n++;
			if (n > 10)
				n = 10;
			if (m > 10)
				m = 4;
		}
		axis.setN(n);
		axis.setM(m);
	} /* getformat */

	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}

	private String getString(double num, int n, int m) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format("%" + n + "." + m + "f", new Double(num));
		return sb.toString();
	}

	private int inside(int x, int min, int max) {
		if (x < min)
			return -1;
		else if (x > max)
			return 1;
		else
			return 0;
	}

	protected  void paintComponent(Graphics g) {
		// note 0,0 is the upper left
		super.paintComponent(g);
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		drawxyaxes(g);
		if (PGO != null) {
			for (int i = 0; i < PGO.size(); i++) {
				TGraphicsObject go = (TGraphicsObject)PGO.get(i);
				if (go != null)
					go.draw(g, this);
				
			}
//			Iterator<TGraphicsObject> iter = PGO.iterator();
//			while (iter.hasNext()) {
//				TGraphicsObject go = (TGraphicsObject) iter.next();
//				if (go != null)
//					go.draw(g, this);
//			}
		}
	}

	private void paintTextVertical(Graphics g, String text, int x, int y, boolean isDown) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform original = g2d.getTransform();
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(-90));

		g2d.setColor(getForeground());
		int offset = isDown ? 1 : 0;
		g2d.drawString(text, offset, offset);
		g2d.setTransform(original);// put it back?
	} // end of paintTextVertical()

	private void scaletics(int maxtics,Axis axis ) {
		double min=axis.getMin(); double max=axis.getMax(); double mintic=axis.getMin(); double maxtic=axis.getMax(); double interval=axis.getTicint(); int numtics;
		double nf, uppertic, lowertic, range, scale;
		int n, powerof10, m4;
		boolean done;

		if (maxtics < 2 || min > max) { /* garbage */
			mintic = min;
			interval = max - min;
			return;
		}
		powerof10 = 0;
		uppertic = max;
		lowertic = min;
		scale = 1.0;
		m4 = 0;
		range = uppertic - lowertic;
		if (range < 10.0) {
			scale = 10.0;
			m4 = -1;
		} else if (range >= 100.0) {
			scale = 0.1;
			m4 = 1;
		}
		while (range < 10.0 || range >= 100.0) {
			uppertic *= scale;
			lowertic *= scale;
			powerof10 += m4;
			range = uppertic - lowertic;
		}
		mintic = bottom(lowertic);
		range = (int) Math.floor(uppertic + 0.5) - mintic;
		if (Math.abs(range - 10.0) < TGraph.ZERO) { /* 2 parts */
			lowertic = 10.0 * lowertic;
			uppertic = 10.0 * uppertic;
			mintic = bottom(lowertic);
			powerof10--;
			range = (int) Math.floor(uppertic + 0.5) - mintic;
		}
		done = false;
		do {
			n = maxtics;
			do {
				nf = n - 1.0;
				if (divisible(range, nf)) { /* n parts */
					interval = range / nf;
					numtics = n;
					done = true;
				}
				n--;
			} while (!(done || n == 2));
			if (done)
				done = (mintic + range - uppertic >= -TGraph.ZERO);
			if (!done)
				range += 1.0; /* multiple of n */
		} while (!done);
		mintic *= Math.exp(powerof10 * TGraph.LN10);
		interval *= Math.exp(powerof10 * TGraph.LN10);
		maxtic = mintic + range * Math.exp(powerof10 * TGraph.LN10);
		axis.setMin(mintic);
		axis.setMax(maxtic);
		axis.setTicint(interval);
	} /* scaletics */

	private void SetDefaults() {
//		graphTitle = "QUIlF";
		getformat(xAxis);
		getformat(yAxis);
	}

	public int windowx(double x) {
		return (int) (axglb + bxglb * x);
	}

	public int windowy(double y) {
		return (int) (ayglb + byglb * y);
	}

	private void xtic(Graphics g, double tn, double tp, double x, double y, double dy) {
		/* draws a vertical tic at x,y+tn x,y+tp */
		double y1, y2;

		y1 = y + tn * dy;
		y2 = y + tp * dy;
		drawline(g, x, y1, x, y2);
	}

	private void xyaxis(Graphics g, Axis xaxis, Axis yaxis, String title) {
		XYPoint ptl = new XYPoint();
		XYPoint ptl2 = new XYPoint();
		FontMetrics fm = g.getFontMetrics();
		charwidth = fm.getMaxAdvance();
		charheight = fm.getHeight();
		if (xaxis.getLabint() == 0)
			scaletics(6,xaxis); 
		if (xaxis.getTicint() == 0)
			xaxis.setTicint(xaxis.getLabint() / 2.0);
		if (xaxis.getN() == 0) {
			getformat(xaxis);
		}
		if (yaxis.getLabint() == 0)
			scaletics(10, yaxis);
		if (yaxis.getTicint() == 0)
			yaxis.setTicint(yaxis.getLabint() / 2.0);
		if (yaxis.getN() == 0) {
			getformat(yaxis);
		}
		log.debug("xyaxis:xaxis=" + xaxis.toString());
		log.debug("xyaxis:yaxis=" + yaxis.toString());
		Dimension rcl = this.getSize();
		int minscreenx = 0;
		int maxscreenx = rcl.width;
		int maxscreeny = rcl.height;
		int minscreeny = 0;

		int mingraphx = minscreenx + charheight * (1 + yaxis.getN()); // (yaxis.n * charwidth + 3 * charheight / 2);
		int maxgraphx = maxscreenx - charwidth * (xaxis.getN() / 2); // (xaxis.n * charwidth);

		int mingraphy = minscreeny + charheight + charheight / 2; /* 2.5/100 */
		int maxgraphy = maxscreeny - 2 * charheight; /* 1.5/100 */

		definewindow(mingraphx, mingraphy, maxgraphx, maxgraphy);
		defineworld(xaxis.getMin(), yaxis.getMin(), xaxis.getMax(), yaxis.getMax());

		ptl.setX(windowx(xaxis.getMin()));
		ptl.setY(windowy(yaxis.getMin()));

		// MoveTo(g, ptl.getX(), ptl.getY());

		ptl2.setX(windowx(xaxis.getMax()));
		ptl2.setY(windowy(yaxis.getMax()));
		g.setColor(Color.BLACK);
		g.drawRect(ptl.getX(), ptl2.getY(), (ptl2.getX() - ptl.getX()), (ptl.getY() - ptl2.getY()));
		Rectangle2D rec = fm.getStringBounds(title, g);

		int xs = (maxgraphx + mingraphx) / 2;

		ptl.setX(xs - (int) rec.getCenterX());
		int ys = minscreeny + (int) rec.getHeight(); // - (minscreeny - mingraphy) / 2;
		ptl.setY(ys); // + aptl.cy;
		g.drawString(title, ptl.getX(), ptl.getY());

		double dx = xaxis.getMax() - xaxis.getMin();
		double dy = yaxis.getMax() - yaxis.getMin();
		double x = xaxis.getMin() + xaxis.getTicint();
		while (x < xaxis.getMax()) {
			xtic(g, -TGraph.XTICSIZE, 0.0, x, yaxis.getMax(), dy);
			x += xaxis.getTicint();
		}
		double y = yaxis.getMin() + yaxis.getTicint();
		while (y < yaxis.getMax()) {
			ytic(g, -TGraph.YTICSIZE, 0.0, xaxis.getMax(), y, dx);
			y += yaxis.getTicint();
		}
		x = xaxis.getMin() + xaxis.getTicint();
		while (x < xaxis.getMax()) {
			xtic(g, 0.0, TGraph.XTICSIZE, x, yaxis.getMin(), dy);
			x += xaxis.getTicint();
		}
		y = yaxis.getMin() + yaxis.getTicint();
		while (y < yaxis.getMax()) {
			ytic(g, 0.0, TGraph.YTICSIZE, xaxis.getMin(), y, dx);
			y += yaxis.getTicint();
		}

		y = yaxis.getMin() + yaxis.getLabint();
		while (y < yaxis.getMax()) {
			ytic(g, -TGraph.YLABSIZE, 0.0, xaxis.getMax(), y, dx);
			y += yaxis.getLabint();
		}

		xs = windowx(xaxis.getMin() - TGraph.YLABSIZE);
		y = yaxis.getMin();
		// Send all output to the Appendable object sb
		while (y <= yaxis.getMax()) {
			ytic(g, 0.0, TGraph.YLABSIZE, xaxis.getMin(), y, dx);
			ys = windowy(y);
			String s = getString(y, yaxis.getN(), yaxis.getM());
			if (s.length() > 0) {
				rec = fm.getStringBounds(s, g);
				ptl.setX(xs - (int) rec.getWidth());
				ptl.setY(ys + (int) rec.getHeight() / 2);
				g.drawString(s, ptl.getX(), ptl.getY());

			}
			y = y + yaxis.getLabint();
		}

		rec = fm.getStringBounds(yaxis.getLab(), g);
		xs = minscreenx + fm.getHeight();
		ys = (mingraphy + maxgraphy) / 2;
		ys += (rec.getWidth() / 2);
		paintTextVertical(g, yaxis.getLab(), xs, ys, true);
		x = xaxis.getMin() + xaxis.getLabint();
		while (x < xaxis.getMax()) {
			xtic(g, -TGraph.XLABSIZE, 0.0, x, yaxis.getMax(), dy);
			x = x + xaxis.getLabint();
		}
		ys = windowy(yaxis.getMin());
		x = xaxis.getMin();
		while (x <= xaxis.getMax()) {
			xtic(g, 0.0, TGraph.XLABSIZE, x, yaxis.getMin(), dy);
			String s = getString(x, xaxis.getN(), xaxis.getM());
			if (s.length() > 0) {
				xs = windowx(x);

				rec = fm.getStringBounds(s, g);
				ptl.setX(xs - (int) rec.getWidth() / 2);
				ptl.setY(ys + (int) rec.getHeight()); // - aptl.cy;
				g.drawString(s, ptl.getX(), ptl.getY());

			}
			x = x + xaxis.getLabint();
		}

		xs = (mingraphx + maxgraphx) / 2;
		ys = minscreeny;

		rec = fm.getStringBounds(xaxis.getLab(), g);
		ptl.setX(xs - (int) rec.getWidth());
		ptl.setY(maxscreeny); // - (int) rec.getHeight()); // ys;
		g.drawString(xaxis.getLab(), ptl.getX(), ptl.getY());

	} /* xyAxis */

	private void ytic(Graphics g, double tn, double tp, double x, double y, double dx) {
		/* draws a horizontal tic at x+tn,y x+tp,y */
		double x1, x2;

		x1 = x + tn * dx;
		x2 = x + tp * dx;
		drawline(g, x1, y, x2, y);
	}

	public String getGraphTitle() {
		return graphTitle;
	}

	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
	}
}