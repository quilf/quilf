package edu.sunysb.ess.quilf.model;

import edu.sunysb.ess.quilf.swing.TField;
import edu.sunysb.ess.quilf.swing.TLabel;
import edu.sunysb.ess.quilf.swing.TQPanel;
import edu.sunysb.ess.quilf.swing.TRow;

/*
 part of QUIlF

 Copyright (c) 1998 by David Andersen

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

public class TSpinel extends TPhase {
	public static final int Mt = 0;
	public static final int Usp = 1;
	public static final int Mf = 2;
	public static final int Jac = 3; // spinel components
	public static final int Qan = 4;
	public static final int nTi = 0;
	public static final int nMg = 1;
	public static final int nMn = 2; // spinel variables

	public static final int g22 = 0;
	public static final int g23 = 1;
	public static final int g24 = 2;
	public static final int g34 = 3; // reduced spinel solution
	public static final int g222 = 4;
	public static final int g223 = 5;
	public static final int g224 = 6;
	public static final int g234 = 7;
	public static final int g334 = 8;
	public static final int g344 = 9;

	double X2, X3, X4;
	double Oct_Mg, Oct_Mn, Oct_Fe3, Tet_Fe3, Oct_Fe2, Tet_Fe2, Tet_Mg;
	TRow NTi, NMg, NMn;

	TSpinel() {
		super("spinel");
		// spinel = this;
	}

	public int act() {
		/*
		 * Calculate the activities for the Spinel components, Sln^[G] contains the model parameters at T and P.
		 */
		int J;
		double AxSp[] = new double[12];

		if (!actCurrent) {
			idealSp(activities);
			for (J = lowerComponent; J <= upperComponent; J++) {
				if (activities[J] > ASSUMEDZERO) {
					wGSp(J, AxSp);
					activities[J] = R * tk * Math.log(activities[J]) + dotV(lowerVariable, upperVariable, AxSp, sln[G]);
				} else
					activities[J] = EXP0;
			}
			actCurrent = true;
		}
		return TErrors.No_Err;
	}

	public int actdTdP() {
		/*
		 * Return the Activities(in aSp) and temperature and pressure derivatives of the activities(in dT and dP) for
		 * the Spinel components, dG contains the model parameters at T and P, dS the temperature derivatives and dV the
		 * pressure derivatives.
		 */
		double AX;
		int J;
		double AxSp[] = new double[12];

		if (!dTdPCurrent) {
			for (J = lowerComponent; J <= upperComponent; J++) {
				dAdT[J] = 0.0;
				dAdP[J] = 0.0;
			}
			idealSp(activities);
			for (J = lowerComponent; J <= upperComponent; J++) {
				if (activities[J] > ASSUMEDZERO) {
					wGSp(J, AxSp);
					AX = Math.log(activities[J]);
					activities[J] = R * tk * AX + dotV(lowerVariable, upperVariable, AxSp, sln[G]);
					dAdT[J] = R * AX - dotV(lowerVariable, upperVariable, AxSp, sln[S]);
					dAdP[J] = dotV(lowerVariable, upperVariable, AxSp, sln[V]);
				} else
					activities[J] = EXP0;
			}
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
	 *            double
	 */
	public void changeX(int I, double dX) {
		switch (I) {
		case nTi:
			xCalcComposition[nTi] = newX(xCalcComposition[nTi], dX, ALMOSTONE);
			break;

		case nMg:
			xCalcComposition[nMg] = newX2(xCalcComposition[nMg], dX, xCalcComposition[nTi], xCalcComposition[nMn]);
			break;

		case nMn:
			xCalcComposition[nMn] = newX2(xCalcComposition[nMn], dX, xCalcComposition[nTi], xCalcComposition[nMg]);
			break;
		}
		setComp(xCalcComposition);
	}

	public int deriv(double A[]) {
		/*
		 * Calculates the compositional derivatives for spinels which are returned in dX2, dX3 and dX4. Assumes Sln^[G]
		 * contains the model parameters at T and P.
		 */
		int J;
		double dW[] = new double[upperVariable + 1];

		if (!dXCurrent) {
			idealSp(A);
			idealDeriv(A); // &V);

			for (J = lowerComponent; J <= upperComponent; J++) {
				if (A[J] > ASSUMEDZERO) {
					if (dWGdX2(J, X2, X3, X4, A[J], dW))
						dX[nTi][J] = R * tk * dX[nTi][J] + dotV(lowerVariable, upperVariable, dW, sln[G]);
					if (dWGdX3(J, X2, X3, X4, A[J], dW))
						dX[nMg][J] = R * tk * dX[nMg][J] + dotV(lowerVariable, upperVariable, dW, sln[G]);
					if (dWGdX4(J, X2, X3, X4, A[J], dW))
						dX[nMn][J] = R * tk * dX[nMn][J] + dotV(lowerVariable, upperVariable, dW, sln[G]);
				}
			}
			dXCurrent = true;
		}
		return TErrors.No_Err;
	}

	boolean dWGdX2(int EI, double X2, double X3, double X4, double A, double W[]) {
		if (A <= ASSUMEDZERO) {
			A = -INFINITY;
			return false;
		}
		for (int i = lowerVariable; i <= upperVariable; i++)
			W[i] = 0.0;
		switch (EI) {
		case Mt:
			W[g22] = -2.0 * X2;
			W[g23] = -X3;
			W[g24] = -X4;
			W[g222] = -6.0 * X2 * X2;
			W[g223] = -4.0 * X2 * X3;
			W[g224] = -4.0 * X2 * X4;
			W[g234] = -2.0 * X3 * X4;
			break;

		case Usp:
			W[g22] = 2.0 - 2.0 * X2;
			W[g23] = -X3;
			W[g24] = -X4;
			W[g222] = 6.0 * X2 - 6.0 * X2 * X2;
			W[g223] = 2.0 * X3 - 4.0 * X2 * X3;
			W[g224] = 2.0 * X4 - 4.0 * X2 * X4;
			W[g234] = -2.0 * X3 * X4;
			break;

		case Mf:
			W[g22] = -2.0 * X2;
			W[g23] = 1 - X3;
			W[g24] = -X4;
			W[g222] = -6.0 * X2 * X2;
			W[g223] = (1.0 - 2.0 * X3) * 2.0 * X2;
			W[g224] = -4.0 * X2 * X4;
			W[g234] = (1.0 - 2.0 * X3) * X4;
			break;

		case Jac:
			W[g22] = -2.0 * X2;
			W[g23] = -X3;
			W[g24] = 1.0 - X4;
			W[g222] = -6.0 * X2 * X2;
			W[g223] = -4.0 * X2 * X3;
			W[g224] = (1.0 - 2.0 * X4) * 2.0 * X2;
			W[g234] = (1.0 - 2.0 * X4) * X3;
			break;
		case Qan:
			W[g22] = 2 * X2 + 2 * (1.0 - X2) - 2 * X2;
			W[g23] = X3 + -X3 - X3;
			W[g24] = X4 + -X4 - X4;
			W[g34] = 0;
			W[g222] = 3 * X2 * X2 + 6.0 * X2 - 9.0 * X2 * X2;
			W[g223] = 2 * X2 * X3 + 2 * X3 - 4.0 * X2 * X3 + (2.0 - X3) * 2 * X2;
			W[g224] = 2 * X2 * X4 + 2 * X4 - 4.0 * X2 * X4 - 2 * X4 * X2;
			W[g234] = X3 * X4 - X3 * X4 + (2.0 - X3) * X4 - X4 * X3;
			W[g334] = 0;
			W[g344] = 0;
			break;
		}
		return true;
	}

	boolean dWGdX3(int EI, double X2, double X3, double X4, double A, double W[]) {
		if (A <= ASSUMEDZERO) {
			A = -INFINITY;
			return false;
		}
		for (int i = lowerVariable; i <= upperVariable; i++)
			W[i] = 0.0;
		switch (EI) {
		case Mt:
			W[g23] = -X2;
			W[g223] = -2.0 * X2 * X2;
			W[g34] = -X4;
			W[g234] = -2.0 * X2 * X4;
			W[g334] = -4.0 * X3 * X4;
			W[g344] = -2.0 * X4 * X4;
			W[g344] = 2.0 * X3 - 4.0 * X3 * X4;
			break;

		case Usp:
			W[g23] = 1.0 - X2;
			W[g223] = (1.0 - X2) * 2.0 * X2;
			W[g34] = -X4;
			W[g234] = (1.0 - 2.0 * X2) * X4;
			W[g334] = -4.0 * X3 * X4;
			W[g344] = -2.0 * X4 * X4;
			break;

		case Mf:
			W[g23] = -X2;
			W[g223] = -2.0 * X2 * X2;
			W[g34] = -X4;
			W[g234] = -2.0 * X2 * X4;
			W[g334] = 2.0 * X4 - 4.0 * X3 * X4;
			W[g344] = -2.0 * X4 * X4;
			break;

		case Jac:
			W[g23] = -X2;
			W[g223] = -2.0 * X2 * X2;
			W[g34] = -X4;
			W[g234] = -2.0 * X2 * X4;
			W[g334] = 2.0 * (1.0 - 2.0 * X4) * X3;
			W[g344] = 2.0 * X3 - 4.0 * X3 * X4;
			break;

		case Qan:
			W[g22] = 0;
			W[g23] = X2 + (1.0 - X2) - X2;
			W[g24] = 0;
			W[g34] = X4 - X4 - X4;
			W[g222] = 0;
			W[g223] = X2 * X2 + (1.0 - X2) * 2.0 * X2 - X2 * X2;
			W[g224] = 0;
			W[g234] = X2 * X4 + (1.0 - X2) * X4 + -X2 * X4 - X4 * X2;
			W[g334] = 2 * X3 * X4 + 2 * X4 - 4.0 * X3 * X4 - 2.0 * X4 * X3;
			W[g344] = X4 * X4 + -X4 * X4 - X4 * 2.0 * X4;
			break;
		}
		return true;
	}

	boolean dWGdX4(int EI, double X2, double X3, double X4, double A, double W[]) {
		if (A <= ASSUMEDZERO) {
			A = -INFINITY;
			return false;
		}
		for (int i = lowerVariable; i <= upperVariable; i++)
			W[i] = 0.0;
		switch (EI) {
		case Mt:
			W[g24] = -X2;
			W[g34] = -X3;
			W[g224] = -2.0 * X2 * X2;
			W[g234] = -2.0 * X2 * X3;
			W[g334] = -2.0 * X3 * X3;
			W[g344] = -4.0 * X3 * X4;
			break;

		case Usp:
			W[g24] = 1.0 - X2;
			W[g34] = -X3;
			W[g224] = (1.0 - X2) * 2.0 * X2;
			W[g234] = (1.0 - 2.0 * X2) * X3;
			W[g334] = -2.0 * X3 * X3;
			W[g344] = -4.0 * X3 * X4;
			break;

		case Mf:
			W[g24] = -X2;
			W[g34] = 1.0 - X3;
			W[g224] = -2.0 * X2 * X2;
			W[g234] = (1.0 - 2.0 * X3) * X2;
			W[g334] = 2.0 * X3 * (1.0 - X3);
			W[g344] = 2.0 * X4 * (1.0 - 2.0 * X3);
			break;

		case Jac:
			W[g24] = -X2;
			W[g34] = -X3;
			W[g224] = -2.0 * X2 * X2;
			W[g234] = -2.0 * X2 * X3;
			W[g334] = -2.0 * X3 * X3;
			W[g344] = 2.0 * X3 - 4.0 * X3 * X4;
			break;

		case Qan:
			W[g22] = 0;
			W[g23] = 0;
			W[g24] = X2 + (1.0 - X2) - X2;
			W[g34] = X3 + (2.0 - X3) - X3;
			W[g222] = 0;
			W[g223] = 0;
			W[g224] = X2 * X2 + (1.0 - X2) * 2.0 * X2 - X2 * X2;
			W[g234] = X2 * X3 + (1.0 - X2) * X3 + (2.0 - X3) * X2 - X2 * X3;
			W[g334] = X3 * X3 + (2.0 - X3) * 2.0 * X3 - X3 * X3;
			W[g344] = 2 * X3 * X4 + 2.0 * (2.0 - X3) * X4 - X3 * 4.0 * X4;
			break;
		}
		return true;
	}

	protected String getCompStr() {
		return "TK = " + Double.toString(tk) + " P(Bars) = " + Double.toString(p) + " NTi = " + Double.toString(X2) + "NMg = " + Double.toString(X3) + "NMn = " + Double.toString(X4);
	}

	void idealDeriv(double A[]) {
		double dOct_Mg, dOct_Mn, dOct_Fe3, dTet_Fe3, dOct_Fe2, dTet_Fe2, dTet_Mg;
		int I;
		int J;
		double TEMP;

		for (I = 0; I < MAXCOMPONENTS; I++) {
			for (J = lowerComponent; J <= upperComponent; J++)
				dX[I][J] = 0.0;
		}
		if (X2 > ASSUMEDZERO) {
			TEMP = 1.0 + X2;
			dTet_Fe2 = (1.0 + 2.0 * X2 - X3 - X4 + X2 * X2) / (TEMP * TEMP);
			dTet_Fe3 = -1.0;
			dTet_Mg = 0.0;
			dOct_Fe2 = (X3 - X2 + X4 - X2 * X2) / (TEMP * TEMP);
			dOct_Fe3 = -1.0;
			dOct_Mg = -(X3 / (TEMP * TEMP));
			dOct_Mn = -(X4 / (TEMP * TEMP));
			if (A[Mt] > ASSUMEDZERO)
				dX[nTi][Mt] = dTet_Fe3 / Tet_Fe3 + dOct_Fe2 / Oct_Fe2 + dOct_Fe3 / Oct_Fe3;
			else
				dX[nTi][Mt] = 0.0;
			if (A[Usp] > ASSUMEDZERO)
				dX[nTi][Usp] = dTet_Fe2 / Tet_Fe2 + dOct_Fe2 / Oct_Fe2 + 1.0 / X2;
			else
				dX[nTi][Usp] = 0.0;
			if (A[Mf] > ASSUMEDZERO)
				dX[nTi][Mf] = dTet_Fe3 / Tet_Fe3 + dOct_Mg / Oct_Mg + dOct_Fe3 / Oct_Fe3;
			else
				dX[nTi][Mf] = 0.0;
			if (A[Jac] > ASSUMEDZERO)
				dX[nTi][Jac] = dTet_Fe3 / Tet_Fe3 + dOct_Mn / Oct_Mn + dOct_Fe3 / Oct_Fe3;
			else
				dX[nTi][Jac] = 0.0;
			if (A[Qan] > ASSUMEDZERO)
				dX[nTi][Qan] = dTet_Mg / Tet_Mg + dOct_Mg / Oct_Mg + 1.0 / X2;
			else
				dX[nTi][Qan] = 0.0;
		}
		if (X3 > ASSUMEDZERO) {
			dTet_Fe2 = -(X2 / (1.0 + X2));
			dTet_Mg = 0.0;
			dOct_Fe2 = X2 / (1.0 + X2) - 1.0;
			dOct_Mg = 1.0 - X2 / (1.0 + X2);
			if (A[Mt] > ASSUMEDZERO)
				dX[nMg][Mt] = dOct_Fe2 / Oct_Fe2;
			else
				dX[nMg][Mt] = 0.0;
			if (A[Usp] > ASSUMEDZERO)
				dX[nMg][Usp] = dTet_Fe2 / Tet_Fe2 + dOct_Fe2 / Oct_Fe2;
			// +dX2/X2
			else
				dX[nMg][Usp] = 0.0;
			if (A[Mf] > ASSUMEDZERO)
				dX[nMg][Mf] = dOct_Mg / Oct_Mg;
			else
				dX[nMg][Mf] = 0.0;
			if (A[Jac] > ASSUMEDZERO)
				dX[nMg][Jac] = 0.0;
			else
				dX[nMg][Jac] = 0.0;
			if (A[Qan] > ASSUMEDZERO)
				dX[nMg][Qan] = dTet_Mg / Tet_Mg + dOct_Mg / Oct_Mg;
			else
				dX[nMg][Qan] = 0.0;
		}
		if (X4 > ASSUMEDZERO) {
			dTet_Fe2 = -(X2 / (1.0 + X2));
			dTet_Mg = 0.0;
			dOct_Fe2 = X2 / (1.0 + X2) - 1.0;
			dOct_Mn = 1.0 - X2 / (1.0 + X2);
			if (A[Mt] > ASSUMEDZERO)
				dX[nMn][Mt] = dOct_Fe2 / Oct_Fe2;
			else
				dX[nMn][Mt] = 0.0;
			if (A[Usp] > ASSUMEDZERO)
				dX[nMn][Usp] = dTet_Fe2 / Tet_Fe2 + dOct_Fe2 / Oct_Fe2;
			// +dX2/X2
			else
				dX[nMn][Usp] = 0.0;
			if (A[Mf] > ASSUMEDZERO)
				dX[nMn][Mf] = 0.0;
			else
				dX[nMn][Mf] = 0.0;
			if (A[Jac] > ASSUMEDZERO)
				dX[nMn][Jac] = dOct_Mn / Oct_Mn;
			else
				dX[nMn][Jac] = 0.0;
			if (A[Qan] > ASSUMEDZERO)
				dX[nMn][Qan] = 0.0;
			else
				dX[nMn][Qan] = 0.0;
		}
	}

	void idealSp(double A[]) { // sum Oct = 2

		Tet_Fe2 = (1.0 + X2 - X3 - X4) * X2 / (1.0 + X2);
		Tet_Fe3 = 1.0 - X2;
		Tet_Mg = 1.0 - Tet_Fe2 - Tet_Fe3;
		Oct_Fe2 = (1.0 + X2 - X3 - X4) * (1.0 - X2 / (1.0 + X2));
		Oct_Fe3 = 1.0 - X2;
		Oct_Mg = X3 * (1.0 - X2 / (1.0 + X2));
		Oct_Mn = X4 * (1.0 - X2 / (1.0 + X2));

		A[Mt] = Tet_Fe3 * Oct_Fe2 * Oct_Fe3;
		A[Usp] = Tet_Fe2 * Oct_Fe2 * X2;
		A[Mf] = Tet_Fe3 * Oct_Mg * Oct_Fe3;
		A[Jac] = Tet_Fe3 * Oct_Mn * Oct_Fe3;
		A[Qan] = Tet_Mg * Oct_Mg * X2;
	}

	void setComp(double AX[]) {
		X2 = AX[nTi];
		X3 = AX[nMg];
		X4 = AX[nMn];
		super.setComp(AX);
	}

	public int setRows(TQPanel p, int row) {
		p.add(new TField(new TLabel("Spinel")), row++, 0);
		NTi = addComponent(p, "NTi", row++, 0);
		NMg = addComponent(p, "NMg", row++, 0);
		NMn = addComponent(p, "NMn", row++, 0);
		return row;
	}

	void wGSp(int EI, double AxSp[]) {
		/*
		 * Calculate the coefficients for Spinel (Fe3O4,Fe2TiO4,MgFe2O4,MnFe2O4) returns aXSp=array of gstar
		 * coefficients for each endmember
		 */
		double SqrX2;

		SqrX2 = X2 * X2;
		switch (EI) {
		case Mt:
			AxSp[g22] = -SqrX2;
			AxSp[g23] = -X3 * X2;
			AxSp[g24] = -X2 * X4;
			AxSp[g34] = -X3 * X4;
			AxSp[g222] = -2.0 * X2 * SqrX2;
			AxSp[g223] = -2.0 * SqrX2 * X3;
			AxSp[g224] = -2.0 * SqrX2 * X4;
			AxSp[g234] = -2.0 * X2 * X3 * X4;
			AxSp[g334] = -2.0 * X3 * X3 * X4;
			AxSp[g344] = -2.0 * X3 * X4 * X4;
			break;

		case Usp:
			AxSp[g22] = 2.0 * X2 - SqrX2 - 1;
			AxSp[g23] = (1.0 - X2) * X3;
			AxSp[g24] = (1.0 - X2) * X4;
			AxSp[g34] = -X3 * X4;
			AxSp[g222] = 3.0 * SqrX2 - 2.0 * X2 * SqrX2 - 1.0;
			AxSp[g223] = 2.0 * (1.0 - X2) * X2 * X3;
			AxSp[g224] = 2.0 * (1.0 - X2) * X2 * X4;
			AxSp[g234] = (1.0 - 2.0 * X2) * X3 * X4;
			AxSp[g334] = -2.0 * X3 * X3 * X4;
			AxSp[g344] = -2.0 * X3 * X4 * X4;
			break;

		case Mf:
			AxSp[g22] = -SqrX2;
			AxSp[g23] = (1.0 - X3) * X2;
			AxSp[g24] = -X2 * X4;
			AxSp[g34] = (1.0 - X3) * X4;
			AxSp[g222] = -2.0 * X2 * SqrX2;
			AxSp[g223] = (1.0 - 2.0 * X3) * SqrX2;
			AxSp[g224] = -2.0 * SqrX2 * X4;
			AxSp[g234] = (1.0 - 2.0 * X3) * X2 * X4;
			AxSp[g334] = 2.0 * X3 * X4 * (1.0 - X3);
			AxSp[g344] = (1.0 - 2.0 * X3) * X4 * X4;
			break;

		case Jac:
			AxSp[g22] = -SqrX2;
			AxSp[g23] = -X2 * X3;
			AxSp[g24] = (1.0 - X4) * X2;
			AxSp[g34] = (1.0 - X4) * X3;
			AxSp[g222] = -2.0 * X2 * SqrX2;
			AxSp[g223] = -2.0 * SqrX2 * X3;
			AxSp[g224] = (1.0 - 2.0 * X4) * SqrX2;
			AxSp[g234] = (1.0 - 2.0 * X4) * X2 * X3;
			AxSp[g334] = (1.0 - 2.0 * X4) * X3 * X3;
			AxSp[g344] = 2.0 * (1.0 - X4) * X3 * X4;
			break;

		case Qan:// not optimized = g + (1-x2)dgdx2 + (2-x3)dgdx3 - (x4)*dgdx4 - gmgusp
			AxSp[g22] = X2 * X2 + (1.0 - X2) * 2 * X2 - 1.0;
			AxSp[g23] = X2 * X3 + (1.0 - X2) * X3 + (2.0 - X3) * X2 - 2.0;
			AxSp[g24] = X2 * X4 + (1.0 - X2) * X4 - X4 * X2;
			AxSp[g34] = X3 * X4 + (2.0 - X3) * X4 - X4 * X3;
			AxSp[g222] = X2 * X2 * X2 + (1.0 - X2) * 3.0 * X2 * X2 - 1.0;
			AxSp[g223] = X2 * X2 * X3 + (1.0 - X2) * 2.0 * X2 * X3 + (2.0 - X3) * X2 * X2 - 2.0;
			AxSp[g224] = X2 * X2 * X4 + (1.0 - X2) * 2.0 * X2 * X4 - X4 * X2 * X2;
			AxSp[g234] = X2 * X3 * X4 + (1.0 - X2) * X3 * X4 + (2.0 - X3) * X2 * X4 - X4 * X2 * X3;
			AxSp[g334] = X3 * X3 * X4 + (2.0 - X3) * 2.0 * X3 * X4 - X4 * X3 * X3;
			AxSp[g344] = X3 * X4 * X4 + (2.0 - X3) * X4 * X4 - X4 * X3 * 2.0 * X4;
		}
	}
}