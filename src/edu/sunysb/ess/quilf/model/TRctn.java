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

//------------------------------ TRctn ------------------------------
public class TRctn extends TPhase {
	public final double MaxT = 2000.0; //maximum temperature
	public final double MaxP = 100000.0; //maximum pressure	final int Temperature = 0;
	public static final int Temperature = 0;
	public static final int Pressure = 1;
	public TRow xT, xP; //, fo2, dfmq;

	public TRctn() {
		super("rctn");
	}
	public String getFormatString() {
		return "######";
	}

	/**
	 * This method was created by a SmartGuide.
	 * @param I int
	 * @param dX double
	 */
	public void changeX(int I, double dX) {
		switch (I) {
		case Temperature:
			xCalcComposition[Temperature] = newX(xCalcComposition[Temperature], dX, MaxT);
			break;
		case Pressure:
			xCalcComposition[Pressure] = newX(xCalcComposition[Pressure], dX, MaxP);
			break;
		}
		setTP(xCalcComposition[Temperature], xCalcComposition[Pressure]);
	}

	/**
	 * This method was created by a SmartGuide.
	 * @return boolean
	 * @param Flist int
	 * @param FL FVector[]
	 * @param Nv int
	 * @param VL VariableList[]
	 */
	public boolean getComposition(Vector<TVar> FL, Vector<TVar> VL) {
		boolean OK;
		OK = super.getComposition(FL, VL);
		if (OK) {
			xInitialComposition[Temperature] += 273.15;
			xFinalComposition[Temperature] += 273.15;
			if (xInitialComposition[Temperature] <= 100 || xInitialComposition[Pressure] == 0.0) {
				Quilf.showStatus("The temperature and pressure must be specified", TErrors.TP_Err);
				OK = false;
			} else
				setTP(xInitialComposition[Temperature], xInitialComposition[Pressure]);
		}
		return OK;
	}

	/**
	 * This method was created by a SmartGuide.
	 * @return double
	 */
	public double getP() {
		return getX(Pressure);
	}

	/**
	 */
	public double getTk() {
		return getX(Temperature);
	}

	/**
	 * This method was created by a SmartGuide.
	 */
	public void setCalcValues() {
		if (isPhasePresent()) {
			if (xCalcComposition[Temperature] == 0.0)
				xT.clearCalc();
			else
				xT.setCalc(xCalcComposition[Temperature] - 273.15);
			if (xCalcComposition[Pressure] == 0.0)
				xP.clearCalc();
			else
				xP.setCalc(xCalcComposition[Pressure]);
		} else {
			xT.clearAll();
			xP.clearAll();
		}
	}

	void setComp(double AX[]) {
		super.setComp(AX);
		tk = AX[Temperature];
		p = AX[Pressure];
	}

	public int setRows(TQPanel p, int row) {
		xT = addComponent(p, "T(C)", row++, 0, 300, 2000);
		xP = addComponent(p, "P(bars)", row++, 0, 0.9, 30000);
		return row;
	}

	/**
	 * This method was created by a SmartGuide.
	 * @param I int
	 * @param X double
	 */
	public void setX(int I, double X) {
		super.setX(I, X);
		setTP(xCalcComposition[Temperature], xCalcComposition[Pressure]);
	}
}