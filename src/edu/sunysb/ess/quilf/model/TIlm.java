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

import edu.sunysb.ess.quilf.swing.TField;
import edu.sunysb.ess.quilf.swing.TLabel;
import edu.sunysb.ess.quilf.swing.TQPanel;
import edu.sunysb.ess.quilf.swing.TRow;

// ------------------------------ TIlmenite ------------------------------
public class TIlm extends TPhase {
	public static final int Il = 0;
	public static final int Gk = 1;
	public static final int Hm = 2;
	public static final int Py = 3; // ilmenite components
	public static final int XGk = 0; // has to match order on screen?
	public static final int XHem = 1;
	public static final int XPy = 2;
	public static final int XIl = 3; // Ilmenite variables
	private final int MaxWG = 12;
	private double XXIl, XXGk, XXHem, XXPy;
	private TRow xIl, xGk, xHem, xPy;

	public TIlm() {
		super("ilmenite");
		// ilmenite = this;
	}

	public int act() {
		/*
		 * Calculate the Activities for the ilmenite components, Sln^[G] contains the model parameters at T and P.
		 */
		int J;
		double AxIl[] = new double[MaxWG + 1];
		double Xi[] = new double[upperVariable];

		if (!actCurrent) {
			Xi[Il] = 1.0 - XXGk - XXHem - XXPy;
			Xi[Hm] = XXHem;
			Xi[Gk] = XXGk;
			Xi[Py] = XXPy;
			idealIlm(activities);
			for (J = lowerComponent; J <= upperComponent; J++) {
				if (activities[J] > ASSUMEDZERO) {
					FormWg(J, Xi, AxIl);
					activities[J] = R * tk * Math.log(activities[J]) + dotV(lowerVariable, upperVariable, sln[G], AxIl);
				} else
					activities[J] = EXP0;
			}
			actCurrent = true;
		}
		return TErrors.No_Err;
	}

	public int actdTdP() {
		/*
		 * Calculate the activities and temperature and pressure derivatives of the activities(in dT and dP) for the
		 * ilmenite components, Sln^[G] contains the model parameters at T and P, Sln^[S] the temperature derivatives
		 * and Sln^[V] the pressure derivatives.
		 */
		double AX;
		int I;
		double W[] = new double[MaxWG + 1];
		double Xi[] = new double[upperVariable];
		if (dTdPCurrent)
			return TErrors.No_Err;
		for (I = lowerComponent; I <= upperComponent; I++) {
			dAdT[I] = 0.0;
			dAdP[I] = 0.0;
		}
		Xi[Il] = 1.0 - XXGk - XXHem - XXPy;
		Xi[Hm] = XXHem;
		Xi[Gk] = XXGk;
		Xi[Py] = XXPy;
		idealIlm(activities);
		for (I = lowerComponent; I <= upperComponent; I++) {
			if (activities[I] > ASSUMEDZERO) {
				FormWg(I, Xi, W);
				AX = Math.log(activities[I]);
				activities[I] = R * tk * AX + dotV(lowerVariable, upperVariable, W, sln[G]);
				dAdT[I] = R * AX - dotV(lowerVariable, upperVariable, W, sln[S]);
				dAdP[I] = dotV(lowerVariable, upperVariable, W, sln[V]);
			} else
				activities[I] = EXP0;
		}
		dTdPCurrent = true;
		// Error = Error; // need it for derived classes
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
		case XGk:
			xCalcComposition[XGk] = newX2(xCalcComposition[XGk], dX, xCalcComposition[XHem], xCalcComposition[XPy]);
			break;

		case XPy:
			xCalcComposition[XPy] = newX2(xCalcComposition[XPy], dX, xCalcComposition[XGk], xCalcComposition[XHem]);
			break;

		case XHem:
			xCalcComposition[XHem] = newX2(xCalcComposition[XHem], dX, xCalcComposition[XGk], xCalcComposition[XPy]);
			break;
		}
		xCalcComposition[XIl] = 1.0 - xCalcComposition[XGk] - xCalcComposition[XHem] - xCalcComposition[XPy];
		setComp(xCalcComposition);
	}

	public int deriv( double A[]) {
		/*
		 * Calculates the compositional derivatives for ilmenites which are returned in dGk, dHem and dPy, assumes
		 * Sln^[G] contains the model parameters at T and P.
		 */
		int IC;
		double Xi[] = new double[4]; // , A;
		double dW[] = new double[12];

		if (!dXCurrent) {
		Xi[Gk] = XXGk;
		Xi[Hm] = XXHem;
		Xi[Py] = XXPy;
		Xi[Il] = 1.0 - Xi[Gk] - Xi[Hm] - Xi[Py];
		idealIlm(A);
		idealDeriv();
		for (IC = lowerComponent; IC <= upperComponent; IC++) {
			if (A[IC] > 0.0) {
				dWIldK(IC, Gk, Xi, dW);
				dX[XGk][IC] = R * tk * dX[XGk][IC] / A[IC] + dotV(lowerVariable, upperVariable, sln[G], dW);
				dWIldK(IC, Hm, Xi, dW);
				dX[XHem][IC] = R * tk * dX[XHem][IC] / A[IC] + dotV(lowerVariable, upperVariable, sln[G], dW);
				dWIldK(IC, Py, Xi, dW);
				dX[XPy][IC] = R * tk * dX[XPy][IC] / A[IC] + dotV(lowerVariable, upperVariable, sln[G], dW);
			} else {
				dX[XPy][IC] = 0.0;
				dX[XGk][IC] = 0.0;
				dX[XHem][IC] = 0.0;
			}
		}
		dXCurrent = true;
		}
		return TErrors.No_Err;
	}

	double dWIJdK(int L, int K, int I, int J, double Xi[]) {
		double Sum, TEMP, TEMP1;

		if (I == L) {
			if (I == 0) {
				if (J == K) {
					TEMP = Xi[J];
					Sum = 3.0 * (Xi[J] - Xi[I] * Xi[J]) + (Xi[J] - 2.0 * Xi[I] + 1.0) * (1.0 - Xi[I] + Xi[J]) - 2.0 * Xi[I] * Xi[J] + TEMP * TEMP;
				} else {
					TEMP = Xi[J];
					Sum = 2.0 * (Xi[J] - Xi[I] * Xi[J]) + Xi[J] * (Xi[J] - 2.0 * Xi[I] + 1.0) + TEMP * TEMP;
				}
			} else if (J == 0) {
				if (I == K) {
					TEMP = Xi[J];
					Sum = (Xi[J] - 2.0 * Xi[I] + 1.0) * (Xi[I] - Xi[J] - 1) - 3.0 * (Xi[J] - Xi[I] * Xi[J]) + 2.0 * Xi[I] * Xi[J] - TEMP * TEMP;
				} else
					Sum = Xi[I] * Xi[J] - Xi[J] + (Xi[I] - 1.0) * (Xi[J] - 2.0 * Xi[I] + 1.0) + 2.0 * Xi[I] * Xi[J];
			} else if (I == K) {
				TEMP = Xi[J];
				Sum = Xi[J] * (2.0 * Xi[I] - Xi[J] - 1.0) - 2.0 * (Xi[J] - Xi[I] * Xi[J]) - TEMP * TEMP;
			} else if (J == K)
				Sum = Xi[J] - Xi[I] * Xi[J] + (Xi[J] - 2.0 * Xi[I] + 1.0) * (1.0 - Xi[I]) - 2.0 * Xi[I] * Xi[J];
			else
				Sum = 0.0;
		} else if (J == L) {
			if (I == 0) {
				if (J == K) {
					TEMP = Xi[I];
					Sum = 3.0 * (Xi[I] - Xi[I] * Xi[J]) + (2.0 * Xi[J] - Xi[I] + 1.0) * (Xi[J] - Xi[I] - 1.0) + TEMP * TEMP - 2.0 * Xi[I] * Xi[J];
				} else
					Sum = Xi[I] - Xi[I] * Xi[J] + (2.0 * Xi[J] - Xi[I] + 1.0) * (Xi[J] - 1.0) - 2.0 * Xi[I] * Xi[J];
			} else if (J == 0) {
				if (I == K) {
					TEMP = Xi[I];
					Sum = (2.0 * Xi[J] - Xi[I] + 1.0) * (1.0 + Xi[I] - Xi[J]) - 3.0 * (Xi[I] - Xi[I] * Xi[J]) - TEMP * TEMP + 2.0 * Xi[I] * Xi[J];
				} else {
					TEMP = Xi[I];
					Sum = Xi[I] * (2.0 * Xi[J] - Xi[I] + 1.0) - 2.0 * (Xi[I] - Xi[I] * Xi[J]) - TEMP * TEMP;
				}
			} else if (I == K)
				Sum = Xi[I] * Xi[J] - Xi[I] + (2.0 * Xi[J] - Xi[I] + 1.0) * (1.0 - Xi[J]) + 2.0 * Xi[I] * Xi[J];
			else if (J == K) {
				TEMP = Xi[I];
				Sum = 2.0 * (Xi[I] - Xi[I] * Xi[J]) + Xi[I] * (Xi[I] - 2.0 * Xi[J] - 1.0) + TEMP * TEMP;
			} else
				Sum = 0.0;
		} else {
			if (I == 0) {
				if (J == K) {
					TEMP = Xi[I];
					TEMP1 = Xi[J];
					Sum = 2.0 * (TEMP * TEMP - 2.0 * Xi[I] * Xi[J]) - 2.0 * (2.0 * Xi[I] * Xi[J] - TEMP1 * TEMP1) - Xi[I] + Xi[J];
				} else {
					TEMP = Xi[J];
					Sum = 2.0 * (TEMP * TEMP) - 4.0 * Xi[I] * Xi[J] + Xi[J];
				}
			} else if (J == 0) {
				if (I == K) {
					TEMP = Xi[I];
					TEMP1 = Xi[J];
					Sum = 2.0 * (2.0 * Xi[I] * Xi[J] - TEMP * TEMP) - 2.0 * (TEMP1 * TEMP1 - 2.0 * Xi[I] * Xi[J]) + Xi[I] - Xi[J];
				} else {
					TEMP = Xi[I];
					Sum = 4.0 * Xi[I] * Xi[J] - 2.0 * (TEMP * TEMP) + Xi[I];
				}
			} else if (I == K) {
				TEMP = Xi[J];
				Sum = 4.0 * Xi[I] * Xi[J] - 2.0 * (TEMP * TEMP) - Xi[J];
			} else if (J == K) {
				TEMP = Xi[I];
				Sum = 2.0 * (TEMP * TEMP) - 4.0 * Xi[I] * Xi[J] - Xi[I];
			} else
				Sum = 0.0;
		}
		Sum = Sum / 2.0;
		return Sum;
	}

	void dWIldK(int L, int K, double Xi[], double dG[]) {
		// Form the partial derivative of wil(l)/x(k)

		int I, J;
		int U;

		U = lowerVariable;
		for (I = 0; I < 4; I++) {
			for (J = 0; J < 4; J++) {
				if (I != J) {
					dG[U] = dWIJdK(L, K, I, J, Xi);
					if (U < upperVariable)
						U++;
				}
			}
		}
	}

	void FormWg(int L, double WX[], double WG[]) {
		// Calculate the coefficients for quaternary Ilmenite::

		int I, J;
		int U;

		U = lowerVariable;
		for (I = 0; I < 4; I++) {
			for (J = 0; J < 4; J++) {
				if (I != J) {
					WG[U] = wIJ(L, I, J, WX);
					if (U < upperVariable)
						U++;
				}
			}
		}
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return boolean
	 * @param FList
	 *            int
	 * @param FL
	 *            FVector[]
	 * @param Nv
	 *            int
	 * @param VL
	 *            VariableList[] can't use TPhase routine because it is not done (here) in order
	 */
	public boolean getComposition(Vector<TVar> FL, Vector<TVar> VL) {
		// Set the compositional parameters, return true if everything is okay
		// else return false.
		boolean Ok = true;
		setPhasePresent(false);
		setPhaseVariable(false);
		for (int i = 0; i < components.size(); i++) {
			xInitialComposition[i] = 0;
			xFinalComposition[i] = 0;
			xStep[i] = 0;
			xWeight[i] = 0;
			xBestFitComposition[i] = 0;
			xCalcError[i] = 0;
		}
		setNumVar(0); // incremented in getValues
		Ok = getValues(FL, VL, xHem, XHem);
		if (Ok)
			Ok = getValues(FL, VL, xGk, XGk);
		if (Ok)
			Ok = getValues(FL, VL, xPy, XPy);
		if (!Ok) {
			setPhasePresent(false);
			setPhaseVariable(false);
			clearCalc();
		} else
			setComp(xInitialComposition);
		// NumVar = VL.size();
		return Ok;
	}

	protected String getCompStr() {
		return "TK = " + Double.toString(tk) + " P(Bars) = " + Double.toString(p) + "XIl = " + Double.toString(XXIl) + "XGk = " + Double.toString(XXGk) + "XPy = " + Double.toString(XXPy);
	}

	void idealDeriv() {
		// given the compositional variables in x, calculate the ideal activities
		dX[XGk][Il] = XXHem - 1.0; // dA/dXGk

		dX[XGk][Gk] = 1.0 - XXHem;
		dX[XGk][Hm] = 0.0;
		dX[XGk][Py] = 0.0;

		dX[XHem][Il] = 2.0 * XXHem + XXGk + XXPy - 2.0; // dA/dX3

		dX[XHem][Gk] = -XXGk;
		dX[XHem][Hm] = 2.0 * XXHem;
		dX[XHem][Py] = -XXPy;

		dX[XPy][Il] = XXHem - 1.0; // dA/dX4

		dX[XPy][Gk] = 0.0;
		dX[XPy][Hm] = 0.0;
		dX[XPy][Py] = 1.0 - XXHem;
	}

	void idealIlm(double A[]) {
		// given the compositional variables in X, calculate the ideal activities

		double BSite_Ti;

		BSite_Ti = 1.0 - XXHem;
		A[Il] = (1.0 - XXHem - XXGk - XXPy) * BSite_Ti;
		A[Gk] = XXGk * BSite_Ti;
		A[Py] = XXPy * BSite_Ti;
		A[Hm] = XXHem * XXHem;
	}

	/**
	 * This method was created by a SmartGuide.
	 */
	// Row XIlrow, XGkrow, XHemrow, XPyrow;
	public void setCalcValues() {
		xCalcComposition[XIl] = 1.0 - xCalcComposition[XGk] - xCalcComposition[XHem] - xCalcComposition[XPy];
		if (isPhasePresent()) {
			xIl.setCalc(xCalcComposition[XIl]);
			if (xGk.isPresent())
				xGk.setCalc(xCalcComposition[XGk]);
			if (xHem.isPresent())
				xHem.setCalc(xCalcComposition[XHem]);
			if (xPy.isPresent())
				xPy.setCalc(xCalcComposition[XPy]);
		} else
			clear();
	}

	void setComp(double AX[]) {
		XXGk = AX[XGk];
		XXHem = AX[XHem];
		XXPy = AX[XPy];
		XXIl = 1.0 - XXGk - XXHem - XXPy;
		super.setComp(AX);
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
		switch (I) {
		case XGk:
			xGk.setErr(X);
			break;
		case XHem:
			xHem.setErr(X);
			break;
		case XPy:
			xPy.setErr(X);
			break;
		case XIl:
			xIl.setErr(X);
			break;
		}
		xCalcError[I] = X;
	}

	/**
	 * This method was created by a SmartGuide.
	 */
	public void setErrValues() {
		for (int i = 0; i < components.size(); i++) {
			TRow pc;
			switch (i) {
			case XGk:
				pc = xGk;
				break;
			case XHem:
				pc = xHem;
				break;
			case XPy:
				pc = xPy;
				break;
			case XIl:
				pc = xIl;
				break;
			default:
				return;
			}
			if (isPhasePresent()) {
				if (xCalcError[i] == 0.0)
					pc.clearErr();
				else
					pc.setErr(xCalcError[i]);
			} else
				pc.clearAll();
		}
	}

	public int setRows(TQPanel p, int row) {
		p.add(new TField(new TLabel("Ilmenite")), row++, 0);
		xIl = addTextComponent(p, "XIl", row++, 0);
		xHem = addComponent(p, "XHem", row++, 0);
		xGk = addComponent(p, "XGk", row++, 0);
		xPy = addComponent(p, "XPy", row++, 0);
		return row;
	}

	double wIJ(int L, int I, int J, double Xi[]) {
		double Sum, TEMP;

		if (I == L) {
			TEMP = Xi[J];
			Sum = (Xi[J] - Xi[I] * Xi[J]) * (Xi[J] - 2.0 * Xi[I] + 1.0) - Xi[I] * (TEMP * TEMP);
		} else if (J == L) {
			TEMP = Xi[I];
			Sum = (Xi[I] - Xi[I] * Xi[J]) * (2.0 * Xi[J] - Xi[I] + 1.0) + TEMP * TEMP * Xi[J];
		} else
			Sum = Xi[I] * Xi[J] * (2.0 * Xi[I] - 2.0 * Xi[J] - 1.0);
		return (Sum / 2.0);
	}
}