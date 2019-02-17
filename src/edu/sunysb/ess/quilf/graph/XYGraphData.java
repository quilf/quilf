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
public class XYGraphData {
	private String title;
	private int xDec, xWidth;
	private String xLabel;
	private double xMin, xMax, xTic;
	private int yDec, yWidth;
	private String yLabel;
	private double yMin, yMax, yTic;

	public String getTitle() {
		return title;
	}

	public int getXDec() {
		return xDec;
	}

	public String getXLabel() {
		return xLabel;
	}

	public double getXMax() {
		return xMax;
	}

	public double getXMin() {
		return xMin;
	}

	public double getXTic() {
		return xTic;
	}

	public int getXWidth() {
		return xWidth;
	}

	public int getYDec() {
		return yDec;
	}

	public String getYLabel() {
		return yLabel;
	}

	public double getYMax() {
		return yMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYTic() {
		return yTic;
	}

	public int getYWidth() {
		return yWidth;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setXDec(int dec) {
		xDec = dec;
	}

	public void setXLabel(String label) {
		xLabel = label;
	}

	public void setXMax(double max) {
		xMax = max;
	}

	public void setXMin(double min) {
		xMin = min;
	}

	public void setXTic(double tic) {
		xTic = tic;
	}

	public void setXWidth(int width) {
		xWidth = width;
	}

	public void setYDec(int dec) {
		yDec = dec;
	}

	public void setYLabel(String label) {
		yLabel = label;
	}

	public void setYMax(double max) {
		yMax = max;
	}

	public void setYMin(double min) {
		yMin = min;
	}

	public void setYTic(double tic) {
		yTic = tic;
	}

	public void setYWidth(int width) {
		yWidth = width;
	}

}
