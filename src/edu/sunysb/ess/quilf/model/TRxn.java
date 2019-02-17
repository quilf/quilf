package edu.sunysb.ess.quilf.model;

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

//		TRctnCollection is a collection of Reactions.
public class TRxn implements Comparable {
	static final int MaxRC = 6;
	// one reaction
	public boolean Selected;
	public boolean Possible;
	private double dG;
	private double lnK;
	private double deltaG;
	public int RN; // number of phases in the reaction
	public String NR; // the reaction name
	public String PR; // ASCII representation of the reaction
	int[] PS = new int[TSln.NUMPH]; // phases in the reaction
	public TRCoef[] RC = new TRCoef[MaxRC]; // each term in the reaction

	public int compareTo(Object obj) {
		TRxn trxn = (TRxn) obj;
		return NR.compareTo(trxn.NR);
	}

	public TRxn() {
		Selected = false;
		Possible = false;
		RN = 0;
		NR = null;
		PR = null;
	}

	/**
	 * 
	 * @return boolean
	 * @param RCoeff
	 *            rc
	 */
	public boolean addTerm(TRCoef rc) {
		if (RC == null)
			RC = new TRCoef[MaxRC];
		if (RN >= MaxRC)
			return false;
		RC[RN++] = rc;
		return true;
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return NR;
	}

	public double getDG() {
		return dG;
	}

	public void setDG(double dg) {
		dG = dg;
	}

	public double getLnK() {
		return lnK;
	}

	public void setLnK(double lnK) {
		this.lnK = lnK;
	}

	public double getDeltaG() {
		return deltaG;
	}

	public void setDeltaG(double deltaG) {
		this.deltaG = deltaG;
	}
}