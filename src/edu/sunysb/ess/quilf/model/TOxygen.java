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

import java.util.Vector;

import edu.sunysb.ess.quilf.swing.Quilf;
import edu.sunysb.ess.quilf.swing.TQPanel;
import edu.sunysb.ess.quilf.swing.TRow;

// ------------------------------ TOxygen ------------------------------
public class TOxygen extends TPhase {
//	private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Quilf.class);
	public static final int fO2 = 0; // oxygen variable
	public static final int DFMQ = 1;
	private final int Oxy = 0;
	private TRow fo2, dfmq;

	public TOxygen() {
		super("oxygen");
		// oxygen = this;
	}

	public int act() {
		if (!actCurrent) {
			actCurrent = true;
			activities[Oxy] = R * tk * xCalcComposition[Oxy];
		}
		return TErrors.No_Err;
	}

	public int actdTdP() {
		if (!dTdPCurrent) {
			activities[Oxy] = R * tk * xCalcComposition[Oxy];
			dAdT[Oxy] = R * xCalcComposition[Oxy];
			dAdP[Oxy] = 0.0;
			dTdPCurrent = true;
		}
		return TErrors.No_Err;
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param I
	 *            int
	 * @param dX
	 *            double this is different than TPhase in that is it doesn't check for values to large/small
	 */
	public void changeX(int I, double dX) {
		xCalcComposition[I] = xCalcComposition[I] + dX;
		setComp(xCalcComposition);
	}

	public int deriv(double A[]) {
		if (!dXCurrent) {
			dX[fO2][Oxy] = R * tk;
			dXCurrent = true;
		}
		return TErrors.No_Err;
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return double
	 * @param TK
	 *            double
	 */
	public double fO2FMQ(double TK) {
		return ((2.0 * TSln.Slns[TSln.Sp].u0Array[TSln.G][TSpinel.Mt] - TSln.Slns[TSln.Oxygen].u0Array[TSln.G][Oxy] - 3.0 * TSln.Slns[TSln.Ol].u0Array[TSln.G][TOlivine.fa] + 3.0 * TSln.Slns[TSln.Quartz].u0Array[TSln.G][TQuartz.Qtz]) / (R * TK));
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return boolean
	 * @param Flist
	 *            int
	 * @param forNextVariables
	 *            FVector[]
	 * @param Nv
	 *            int
	 * @param variableList
	 *            VariableList[]
	 */
	public boolean getComposition(Vector<TVar> forNextVariables, Vector<TVar> variableList) {
		if (super.getComposition(forNextVariables, variableList)) {
			xInitialComposition[fO2] = LOG10 * xInitialComposition[fO2]; // convert all to base e
			xFinalComposition[fO2] = LOG10 * xFinalComposition[fO2];
			xStep[fO2] = LOG10 * xStep[fO2];
			xWeight[fO2] = LOG10 * xWeight[fO2];
			xCalcComposition[fO2] = xInitialComposition[fO2];
			setComp(xInitialComposition);
			return true;
		} else
			return false;
	}

	/**
	 * This method was created by a SmartGuide.
	 */
	public void setCalcValues() {
		if (fo2.isPresent()) {
			double fmq = fO2FMQ(tk);
			xCalcComposition[DFMQ] = xCalcComposition[fO2] - fmq;
//			log.debug("DFMQ = " + xCalcComposition[DFMQ] + "=" + xCalcComposition[fO2] + "-" + fmq);
			dfmq.setCalc(xCalcComposition[DFMQ] / LOG10);
			fo2.setCalc(xCalcComposition[fO2] / LOG10);
			setComp(xCalcComposition);
		} else {
			fo2.clearAll(); // CalcStr[0] = '\0';
			dfmq.clearAll(); // CalcStr[0] = '\0';
		}
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param I
	 *            int
	 * @param X
	 *            double
	 */
	public void setErrI(int I, double X) {
		xCalcError[fO2] = X;
		fo2.setErr((X / LOG10));
	}

	/**
	 * This method was created by a SmartGuide.
	 */
	public void setErrValues() {
		if (fo2.isPresent()) {
			if (fo2.isVariable())
				fo2.setErr(xCalcError[fO2] / LOG10);
			else
				// if (xCalcError[fO2] == 0.0)
				fo2.clearErr();
		} else
			fo2.clearAll(); // ErrStr[0] = '\0';
	}

	public int setRows(TQPanel p, int row) {
		fo2 = addComponent(p, "fO2", row++, 0, -20, -5);
		dfmq = addTextComponent(p, "DFMQ", row++, 0);
		return row;
	}
}