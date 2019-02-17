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
public class Axis {
	private boolean changed;
	private double height;
	private int index;
	private String lab;
	private double labint;
	private double max;
	private double min;
	private int n, m;
	private double ticint;
	private double width;

	public Axis() {
		super();
	}

	public Axis(int index) {
		this.index = index;
	}

	public double getHeight() {
		return height;
	}

	public int getIndex() {
		return index;
	}

	public String getLab() {
		return lab;
	}

	public double getLabint() {
		return labint;
	}

	public int getM() {
		return m;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public int getN() {
		return n;
	}

	public double getTicint() {
		return ticint;
	}

	public double getWidth() {
		return width;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setIndex(int index) {
		changed = (index != this.index);
		this.index = index;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	public void setLabint(double labint) {
		this.labint = labint;
	}

	public void setM(int m) {
		this.m = m;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void setN(int n) {
		this.n = n;
	}

	public void setTicint(double ticint) {
		this.ticint = ticint;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String toString() {
		return "lab=" + lab + ":min=" + min + ":max=" + max + ":ticint=" + ticint + ":labint=" + labint + ":width=" + width + ":height=" + height + ":n=" + n + ":m=" + m + ":index=" + index;
	}
}