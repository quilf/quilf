package edu.sunysb.ess.quilf.graph;
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
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;

public class TSpline extends TGraphicsObject {
	private final int POINTSIZE = 2;
	private final int POINTSIZE2 = POINTSIZE / 2;
//	private int n;
//	private int ndata;
	private double splinepercent;
	private Vector<DataPoint> xydata;

	public TSpline() {
		this(0.0);
	}

	public TSpline(double percent) {
//		n = 0;
//		ndata = 0;
		if (percent <= 0.0)
			splinepercent = 1.0;
		else
			splinepercent = percent;
		xydata = new Vector<DataPoint>(); //new double[2][TGraph.MAXSPLINE];
	}

	private int addpoint(double x, double y, int ns, double[][] calcxy) {
		if (ns >= TGraph.MAXSPLINE)
			return ns;
		ns++;
		calcxy[0][ns - 1] = x;
		calcxy[1][ns - 1] = y;
		return ns;
	}

	public void addPoint(double x1, double y1) {
		xydata.add(new DataPoint(x1, y1));
		//if (ndata < TGraph.MAXSPLINE) {
//			xydata[0][ndata] = x1;
//			xydata[1][ndata] = y1;
//			ndata++;
//		}
	} /* addpoints */

	private int calcspline(Graphics g, TGRXYWindow tgrXYWindow, int nspline, double[][] splinexy,int n) {
		boolean error;
		double[][] calcxy = new double[2][TGraph.MAXSPLINE];

		int ns = 0;
		boolean swap = false;
		boolean invert = false;
		if (n <= 2 || n > TGraph.MAXSPLINE)
			error = true;
		else {
			error = false;
			for (int i = 2; i <= n; i++) {
				if (splinexy[0][i - 2] >= splinexy[0][i - 1])
					error = true;
			}
			if (error) {
				error = false;
				for (int i = 2; i <= n; i++) {
					if (splinexy[0][i - 2] <= splinexy[0][i - 1])
						error = true;
				}
				if (!error)
					invert = true;
				else { /* x is out of order, try y */
					error = false;
					swap = true;
					for (int i = 2; i <= n; i++) {
						if (splinexy[1][i - 2] >= splinexy[1][i - 1])
							error = true;
					}
					if (error) {
						error = false;
						for (int i = 2; i <= n; i++) {
							if (splinexy[1][i - 2] <= splinexy[1][i - 1])
								error = true;
						}
						if (!error)
							invert = true;
					}
				}
			}
			if (!error) {
				if (invert)
					reverse(n, splinexy, ns, calcxy);
				if (swap)
					ns = dospline(g, tgrXYWindow, n, splinexy[1], splinexy[0], ns, calcxy);
				else
					ns = dospline(g, tgrXYWindow, n, splinexy[0], splinexy[1], ns, calcxy);
			}
		}
		if (error) {
			return n;
		}
		nspline = ns;
		for (int i = 0; i < ns; i++) {
			if (swap) {
				splinexy[0][i] = calcxy[1][i];
				splinexy[1][i] = calcxy[0][i];
			} else {
				splinexy[0][i] = calcxy[0][i];
				splinexy[1][i] = calcxy[1][i];
			}
		}
		if (invert)
			reverse(ns, splinexy, ns, calcxy);
		return nspline;
	}

	private void cubicsplines(int n, double[] h, double[] f, Rows[] s) {
		Rows[] eqn = new Rows[TGraph.MAXSPLINE + 1];
		double bb, cc, hh, ff, fi, hi, nextf;
		double[] bvec = new double[TGraph.MAXSPLINE + 1];
		// Rows WITH;
		// for (int i = 0; i < n; i++)
		// eqn[i] = new Rows();
		hh = h[0];
		ff = f[0];
		nextf = f[1];
		for (int i = 1; i < n; i++) {
			// WITH = eqn[i];
			fi = nextf;
			nextf = f[i + 1];
			hi = h[i];
			eqn[i] = new Rows();
			eqn[i].b = 3 * ((nextf - fi) / hi - (fi - ff) / hh);
			eqn[i].d = 2 * (hh + hi);
			eqn[i].a = hh;
			eqn[i].c = hi;
			hh = hi;
			ff = fi;
		}

		tridiag(eqn, n - 1, bvec);

		bb = 0.0;
		hh = h[0];
		// Rows WITH = s[0];
		s[0].b = bb;
		s[0].d = f[0];
		cc = (f[1] - s[0].d) / hh - bvec[1] * hh / 3;
		s[0].c = cc;
		for (int i = 1; i < n; i++) {
			// WITH = s[i];
			s[i].b = bvec[i];
			s[i].d = f[i];
			cc = (s[i].b + bb) * hh + cc;
			s[i].c = cc;
			s[i - 1].a = (s[i].b - bb) / (3 * hh);
			bb = s[i].b;
			hh = h[i];
		}
		s[n - 1].a = bb / (-3 * hh);
	}

	private int dospline(Graphics g, TGRXYWindow tgrXYWindow, int n, double[] x, double[] y, int ns, double[][] calcxy) {
		double[] h = new double[n + 1];
		double[] f = new double[n + 1];
		Rows[] s = new Rows[n];

		double x1, y1, x2, y2, dh, dx;
		Rows WITH;
		for (int i = 0; i < n; i++)
			s[i] = new Rows();
		for (int i = 0; i <= n - 2; i++) {
			h[i] = x[i + 1] - x[i];
			f[i] = y[i];
		}
		f[n - 1] = y[n - 1];
		cubicsplines(n - 1, h, f, s);
		int m = numsplines(tgrXYWindow, x[0], y[0], x[n - 1], y[n - 1], n);
		dx = (x[n - 1] - x[0]) / m;
		int j = 2;
		x1 = x[0];
		y1 = y[0];
		ns = addpoint(x1, y1, ns, calcxy);
		for (int i = 1; i <= m - 2; i++) {
			x2 = x[0] + dx * i;
			while (x[j - 1] < x2)
				j++;
			dh = x2 - x[j - 2];
			WITH = s[j - 2];
			y2 = WITH.d + dh * (WITH.c + dh * (WITH.b + dh * WITH.a));
			ns = addpoint(x2, y2, ns, calcxy);
		}
		ns = addpoint(x[n - 1], y[n - 1], ns, calcxy);
		return ns;
	}

	public void draw(Graphics g, TGRXYWindow tgrXYWindow) {
		Iterator <DataPoint> iter = xydata.iterator();
		Color color = g.getColor();
		g.setColor(Color.BLUE);
		while (iter.hasNext()) {
			DataPoint dataPoint = iter.next();
			int x = tgrXYWindow.windowx(dataPoint.getX());
			int y= tgrXYWindow.windowy(dataPoint.getY());
			x -= POINTSIZE2;
			y -= POINTSIZE2;
			g.fillRect(x, y, POINTSIZE, POINTSIZE);
		}
		g.setColor(color);
		spline(g, tgrXYWindow);
	}

	private int numsplines(TGRXYWindow tgrXYWindow, double x1, double y1, double x2, double y2, int n) {
		int d;
		double xi, yi, xj, yj, TEMP, TEMP1;

		xi = tgrXYWindow.windowx(x1);
		xj = tgrXYWindow.windowx(x2);
		yi = tgrXYWindow.windowy(y1);
		yj = tgrXYWindow.windowy(y2);
		TEMP = xi - xj;
		TEMP1 = yi - yj;
		d = (int) Math.floor(Math.sqrt((TEMP * TEMP + TEMP1 * TEMP1) * 100.0 / splinepercent + 0.5));
		if (d < n)
			d = n;
		if (d > n * 10)
			d = n * 10;
		return d;
	} /* numsplines */

	private void reverse(int n, double[][] splinexy, int ns, double[][] calcxy) {
		int i, j, k;
		double temp;

		i = 1;
		j = n;
		while (i < j) {
			for (k = 0; k <= 1; k++) {
				temp = splinexy[k][i - 1];
				splinexy[k][i - 1] = splinexy[k][j - 1];
				splinexy[k][j - 1] = temp;
			}
			i++;
			j--;
		}
	} /* reverse */

	private void spline(Graphics g, TGRXYWindow tgrXYWindow) {
		/*
		 * splinexy = vector of coords, n=number of points calculates spline points and plots
		 */
		int nspline = 0;
		double[][] splinexy = new double[2][TGraph.MAXSPLINE];
		Iterator<DataPoint> iter = xydata.iterator();int ndata = 0; while (iter.hasNext()) {
			DataPoint xy= iter.next();
			splinexy[0][ndata] = xy.getX();
			splinexy[1][ndata] = xy.getY();
			ndata++;
		}
		//for (int i = 0; i < ndata; i++) {
//			splinexy[0][i] = xydata[0][i];
			//splinexy[1][i] = xydata[1][i];
		//}
		//n = ndata;
		nspline = calcspline(g, tgrXYWindow, nspline, splinexy, ndata);
		for (int i = 2; i <= nspline; i++)
			tgrXYWindow.drawline(g, splinexy[0][i - 2],  splinexy[1][i - 2],  splinexy[0][i - 1],  splinexy[1][i - 1]);
	} /* spline */

	private void tridiag(Rows[] eqn, int n, double[] x) {
		int i;
		double pivot, mult, ci, bi;
		Rows WITH;

		for (i = 1; i < n; i++) {
			WITH = eqn[i];
			pivot = WITH.d;
			ci = WITH.c;
			bi = WITH.b;
			WITH = eqn[i + 1];
			mult = WITH.a / pivot;
			if (Math.abs(mult) > TGraph.ASSUMEDZERO) {
				WITH.a = mult;
				WITH.d -= mult * ci;
				WITH.b -= mult * bi;
			} else
				WITH.a = 0.0;
		}
		WITH = eqn[n];
		x[n] = WITH.b / WITH.d;
		for (i = n - 1; i >= 1; i--) {
			WITH = eqn[i];
			x[i] = (WITH.b - WITH.c * x[i + 1]) / WITH.d;
		}
	} /* tridiag */
}