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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StreamTokenizer;

// ------------------------------ TPxOl ------------------------------
public class TPxOl extends TPhase {
	private  static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TPxOl.class);
	public static final int MgMg = 0;
	public static final int FeFe = 1;
	public static final int CaMg = 2;
	public static final int CaFe = 3;
	public static final int w12 = 0;
	public static final int w21 = 1; // ternary margules solution parameters
	public static final int w13 = 2;
	public static final int w31 = 3;
	public static final int w23 = 4;
	public static final int w32 = 5;
	public static final int f01 = 6;
	public static final int g0 = 7;
	public static final int ge0 = 8; // ol,px solution parameters
	public static final int wm1 = 0;
	public static final int wm2 = 1;
	public static final int XMg = 0;
	public static final int YCa = 1;
	public static final int XFa = 2; // has to equal XFs, used in setCalc
	public static final int XFs = 2; // has to equal XFa, used in setCalc
	public static final int XFe = 3; // ol/px variables
	public static final int MaxIter = 25;
	public static final double Tolerance = 1.0e-12; // 1.0E-17;
	public static final double NearZero = 1.0e-08;
	public static final int Found = 0;
	public static final int Limit = 1;
	public static final int Iterating = 2;
	public static final int TooNearZero = 3;
	public static final int TooFlat = 4;
	double X, Y, T;

	public TPxOl(String name) {
		super(name);
		T = 0.0;
		X = 0.0;
		Y = 0.0;
	}

	public int act() {
		/*
		 * Calculate the activities for pyroxenes and olivines. X and Y as defined by Davidson and Lindsley. Sln^[G]
		 * contains the model parameters at T and P. U0Array is the standard state values for the endmembers. The
		 * variable Error will be set if the site routine doesn't converge. Z=dummy parameter
		 */
		int SiteErr = TErrors.No_Err;
		char I;
		double dMu1 = 0;
		double dMu2 = 0;
		double dMu3 = 0;
		double dMu[] = new double[3];
		int Error = TErrors.No_Err;
		if (!actCurrent) {
			Error = stdStates(Error);
			for (I = MgMg; I <= CaFe; I++)
				activities[I] = EXP0;
			if (X >= ONE && Y <= ZERO)
				activities[MgMg] = 0.0;
			else if (X <= ZERO && Y <= ZERO)
				activities[FeFe] = 0.0;
			else if (Math.abs(X - 0.5) <= ZERO && Math.abs(Y - 0.5) <= ZERO)
				activities[CaMg] = 0.0;
			else if (X <= ZERO && Math.abs(Y - 0.5) <= ZERO)
				activities[CaFe] = 0.0;
			else {
				SiteErr = site(sln[G]);
				if (SiteErr == TErrors.No_Err) {
					mu1(R * tk, dMu, sln[G]);
					dMu1 = dMu[0];
					dMu2 = dMu[1];
					dMu3 = dMu[2];
					if (X + Y < 1.0)
						activities[FeFe] = dMu2 - u0Array[G][FeFe];
					if (X > ZERO)
						activities[MgMg] = dMu1 - u0Array[G][MgMg];
					if (Y > ZERO) {
						if (X > ZERO)
							activities[CaMg] = (dMu1 + dMu3) / 2.0 - u0Array[G][CaMg];
						if (X + Y < 1.0)
							activities[CaFe] = (dMu2 + dMu3) / 2.0 - u0Array[G][CaFe];
					}
				} else
					Error = TErrors.Site_Err;
			}
			actCurrent = true;
		}
		return Error;
	}

	public int actdTdP() {
		/*
		 * Calculates the temperature and pressure derivatives for pyroxene and olivine using a Richardson
		 * approximation, since dt/dx<>0, dt/dy<>0. Z=dummy parameter
		 */
		final double HT = 0.5;
		final double HT2 = 0.25; // HT/2.0
		final double HT6 = -0.33333333333333333; // -1/(6HT)
		final double HP = 0.5;
		final double HP2 = 0.25; // HP/2.0
		final double HP6 = -0.33333333333333333; // -1/(6HP)

		int IC;
		double A1[] = new double[4];
		double A2[] = new double[4];
		double A3[] = new double[4];
		double A4[] = new double[4];

		int Error = TErrors.No_Err;
		if (dTdPCurrent)
			return Error;
		Error = ssact(tk + HT, p, A1);
		Error = ssact(tk + HT2, p, A2);
		Error = ssact(tk - HT, p, A3);
		Error = ssact(tk - HT2, p, A4);
		if (Error == TErrors.No_Err) {
			for (IC = MgMg; IC <= CaFe; IC++) {
				dAdT[IC] = HT6 * ((A1[IC] - A3[IC]) + 8.0 * (A4[IC] - A2[IC]));
			}
		}
		Error = ssact(tk, p + HP, A1);
		Error = ssact(tk, p + HP2, A2);
		Error = ssact(tk, p - HP, A3);
		Error = ssact(tk, p - HP2, A4);
		if (Error == TErrors.No_Err) {
			for (IC = MgMg; IC <= CaFe; IC++)
				dAdP[IC] = HP6 * (A1[IC] - 8.0 * A2[IC] - A3[IC] + 8.0 * A4[IC]);
		}
		Error = act();
		dTdPCurrent = true;
		return Error;
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param I
	 *            int
	 * @param dX
	 *            int
	 */
	public void changeX(int I, double dX) {
		switch (I) {
		case XMg:
			xCalcComposition[XMg] = newX1(xCalcComposition[XMg], dX, xCalcComposition[YCa]);
			break;

		case YCa:
			xCalcComposition[YCa] = newY(xCalcComposition[YCa], dX, xCalcComposition[XMg]);
			break;
		}
		setComp(xCalcComposition);
	}

	private int copyAct(double Ax, double Ay, double A[]) {
		char IC;
		double NewX[] = new double[4];

		NewX[XMg] = Ax;
		NewX[YCa] = Ay;
		setComp(NewX);
		int Error = act();
		for (IC = MgMg; IC <= CaFe; IC++)
			A[IC] = activities[IC];
		return Error;
	}

	public double deltaFAQ() {
		int Err = TErrors.No_Err;
		double U0_Qtz_Haas, U0_Fa_Haas, U0_Fa_Helg, U0_Qtz_Helg;

		Err = TQuartz.P_Qtz_Haas.updateU0(tk, p);
		U0_Qtz_Haas = TQuartz.P_Qtz_Haas.U0;
		Err = TOlivine.P_Fa_Haas.updateU0(tk, p);
		U0_Fa_Haas = TOlivine.P_Fa_Haas.U0;
		Err = TQuartz.P_Qtz_Helg.updateU0(tk, p);
		U0_Qtz_Helg = TQuartz.P_Qtz_Helg.U0;
		Err = TOlivine.P_Fa_Helg.updateU0(tk, p);
		U0_Fa_Helg = TOlivine.P_Fa_Helg.U0;
		return (-U0_Fa_Helg - U0_Qtz_Helg + U0_Fa_Haas + U0_Qtz_Haas);
	}

	public int deriv(double A[]) {
		/*
		 * Calculates the compositional derivatives for olivine and pyroxene using a Richardson approximation, since
		 * dt/dx<>0, dt/dy<>0. The derivatives are returned in dX and dY, Sln^[G] contains the model parameters and
		 * U0Array then standard state values at T and P. Returns error=site_err only if error otherwise error is not
		 * changed.
		 */
		final double HX = 1.0e-05;
		final double HX2 = 5.0e-06; // HX/2
		final double HX6 = -16666.66667; // -1/(6HX)

		char IC;
		double Act1[] = new double[4];
		double Act2[] = new double[4];
		double Act3[] = new double[4];
		double Act4[] = new double[4];
		double SaveX[] = new double[4];
		int Error = TErrors.No_Err;
		if (!dXCurrent) {
			SaveX[XMg] = X;
			SaveX[YCa] = Y;
			if (X > 0.0) {
				copyAct(SaveX[XMg] + HX, SaveX[YCa], Act1);
				copyAct(SaveX[XMg] + HX2, SaveX[YCa], Act2);
				copyAct(SaveX[XMg] - HX, SaveX[YCa], Act3);
				copyAct(SaveX[XMg] - HX2, SaveX[YCa], Act4);
				for (IC = MgMg; IC <= CaFe; IC++)
					dX[XMg][IC] = HX6 * (Act1[IC] - 8.0 * Act2[IC] - Act3[IC] + 8.0 * Act4[IC]);
			} else {
				for (IC = MgMg; IC <= CaFe; IC++)
					dX[XMg][IC] = 0.0;
			}
			if (Y > 0.0) {
				Error = copyAct(SaveX[XMg], SaveX[YCa] + HX, Act1);
				Error = copyAct(SaveX[XMg], SaveX[YCa] + HX2, Act2);
				Error = copyAct(SaveX[XMg], SaveX[YCa] - HX, Act3);
				Error = copyAct(SaveX[XMg], SaveX[YCa] - HX2, Act4);
				for (IC = MgMg; IC <= CaFe; IC++)
					dX[YCa][IC] = HX6 * (Act1[IC] - 8.0 * Act2[IC] - Act3[IC] + 8.0 * Act4[IC]);
			} else {
				for (IC = MgMg; IC <= CaFe; IC++)
					dX[YCa][IC] = 0.0;
			}
			dXCurrent = true;
			setComp(SaveX);
			Error = act();
		}

		return Error;
	}

	protected String getCompStr() {
		return "TK = " + Double.toString(tk) + " P(Bars) = " + Double.toString(p) + " X = " + Double.toString(X) + " Y = " + Double.toString(Y) + " T = " + Double.toString(T);
	}

	private void mu1(double RT, double dMu[], double dG[]) {
		/*
		 * 5/12/91 see the expressions for wM1, and wM2. PMD apparantly uses wM2/2, wM1/2 which is different from her
		 * paper
		 */
		double GTotal, A, B, C, D, CaM2, MgM1, MgM2, FeM1, FeM2, Ts, dGdX, dGdY, T2;

		T2 = T / 2.0;
		MgM1 = X + Y + T2;
		MgM2 = X - Y - T2;
		FeM1 = 1.0 - X - Y - T2;
		CaM2 = 2.0 * Y;
		FeM2 = 1.0 - X - Y + T2;
		GTotal = dG[g0] * (X * (1.0 - X - Y) + T2 * (Y + T2));
		GTotal = GTotal + dG[f01] * Y * MgM1;
		GTotal = GTotal - dG[ge0] * (Y * (1.0 - X - Y) + T2 * (1.0 - Y));
		GTotal = GTotal + (dG[wm2] * MgM2 * FeM2 + dG[wm1] * MgM1 * FeM1) / 2.0;
		GTotal = GTotal + X * (u0Array[G][MgMg] - u0Array[G][FeFe]) + u0Array[G][FeFe];
		if (Y > 0.0) {
			GTotal = GTotal + CaM2 * (u0Array[G][CaFe] - u0Array[G][FeFe]);
			GTotal = GTotal + CaM2 * MgM2 * (dG[w13] * (MgM2 + FeM2 / 2.0) + dG[w31] * (CaM2 + FeM2 / 2.0));
			GTotal = GTotal + CaM2 * FeM2 * (dG[w23] * (FeM2 + MgM2 / 2.0) + dG[w32] * (CaM2 + MgM2 / 2.0));
		}
		Ts = xlnX(MgM1) + xlnX(MgM2) + xlnX(FeM1) + xlnX(FeM2) + xlnX(CaM2);
		GTotal = GTotal + RT * Ts;
		A = dG[w13] - dG[w31] + dG[w23] - dG[w32];
		B = dG[w13] - dG[w31] - dG[w23] + dG[w32];
		C = dG[w13] + dG[w31] - 3.0 * dG[w23] + dG[w32];
		D = dG[w13] + dG[w31] + 5.0 * dG[w23] - 3.0 * dG[w32];
		if (X < ZERO || X > ONE)
			dGdX = 0.0;
		else {
			Ts = FeM1 * FeM2;
			if (Math.abs(Ts) < ZERO)
				dGdX = EXP0;
			else {
				Ts = MgM1 * MgM2 / Ts;
				if (Ts < ZERO)
					dGdX = -EXP0;
				else {
					dGdX = dG[g0] * (1.0 - 2.0 * X - Y);
					dGdX = dGdX + dG[f01] * Y;
					dGdX = dGdX + dG[ge0] * Y;
					dGdX = dGdX + Y * (C + A * (2.0 * X - T) - 4.0 * B * Y);
					dGdX = dGdX + dG[wm2] * (FeM2 - MgM2) / 2.0;
					dGdX = dGdX + dG[wm1] * (FeM1 - MgM1) / 2.0;
					dGdX = dGdX + u0Array[G][MgMg] - u0Array[G][FeFe];
					dGdX = dGdX + RT * Math.log(Ts);
				}
			}
		}
		if (Y < ZERO)
			dGdY = 0.0;
		else {
			Ts = FeM1 * FeM2;
			if (X > ZERO)
				Ts = MgM2 * Ts;
			if (Math.abs(Ts) < ZERO)
				dGdY = EXP0;
			else {
				Ts = CaM2 * CaM2 / Ts;
				if (X > ZERO)
					Ts = Ts * MgM1;
				if (Ts < ZERO)
					dGdY = -EXP0;
				else {
					dGdY = dG[g0] * (T2 - X);
					dGdY = dGdY + dG[f01] * (X + 2.0 * Y + T2);
					dGdY = dGdY + dG[ge0] * (X - 1.0 + 2.0 * Y + T2);
					dGdY = dGdY + C * (X - T2) - 2.0 * Y * D + B * Y * (4.0 * T - 8.0 * X);
					dGdY = dGdY + A * (X * X + 9.0 * (Y * Y) + T * T / 4.0 - X * T);
					dGdY = dGdY + 0.5 * (D - C) - A + B;
					dGdY = dGdY + 2.0 * (u0Array[G][CaFe] - u0Array[G][FeFe]);
					dGdY = dGdY + dG[wm2] * (-FeM2 - MgM2) / 2.0 + dG[wm1] * (FeM1 - MgM1) / 2.0;
					dGdY = dGdY + RT * Math.log(Ts);
				}
			}
		}
		dMu[0] = GTotal + (1.0 - X) * dGdX - Y * dGdY;
		dMu[1] = GTotal - X * dGdX - Y * dGdY;
		dMu[2] = GTotal - X * dGdX + (1.0 - Y) * dGdY;
	}

	protected void randomFeMg() {
		// Sets the order parameter t, to a random fe/mg distribution.
		T = 2.0 * X - 2.0 * Y - 2.0 * X * (1 - 2.0 * Y) / (1.0 - Y);
	}

	public boolean readFile(StreamTokenizer F) {
		super.readFile(F);
		try {
			F.nextToken();
			T = F.nval; // readDouble();
		} catch (IOException e) {
			log.error("Read error: " + e);
			return false;
		} catch (Exception e) {
			log.error("Read error: " + e);
			return false;
		}
		return true;
	}

	/**
	 * This method was created by a SmartGuide.
	 */
	public void setCalcValues() {
		xCalcComposition[XFa] = 1.0 - xCalcComposition[XMg] - xCalcComposition[YCa];
		if (xCalcComposition[YCa] < ONE)
			xCalcComposition[XFe] = (1.0 - xCalcComposition[XMg] - xCalcComposition[YCa]) / (1.0 - xCalcComposition[YCa]);
		else
			xCalcComposition[XFe] = 0.0;
		super.setCalcValues();
	}

	void setComp(double AX[]) {
		super.setComp(AX);
		X = AX[XMg];
		Y = AX[YCa];
		if (X < 0.0)
			X = 0.0; // safeguards
		else if (X > 1.0)
			X = 1.0;
		if (Y < 0.0)
			Y = 0.0; // safeguards
		else if (Y > 0.5)
			Y = 0.5;
		if (X + Y > 1.0)
			X = 1.0 - ZERO - Y;
		randomFeMg();
	}

	private int site(double dG[]) {
		/*
		 * The site distribution parameter for olivine and pyroxene based on the site mixing model of Davidson and
		 * Lindsley.
		 */
		int State;
		int Iter;
		// double CaM2;
		double AX, MgM1, MgM2, FeM1, FeM2, dT, dT2, TInc, Kd, MaxT, MinT, T2;

		int Error = TErrors.No_Err;
		if (Math.abs(1.0 - X - Y) < NearZero) {
			T = 0.0; // Di-En
			return Error;
		}
		if (X < NearZero) {
			T = -2.0 * Y; // Hd-Fs
			return Error;
		}
		if (Math.abs(Y - 0.5) < NearZero) {
			T = 2.0 * X - 1.0; // Di-Hd
			return Error;
		}
		// double CaM2 = 2.0 * Y;
		FeM1 = 1.0 - 2.0 * X;
		if (FeM1 < 0.0) // min FeM1
			FeM1 = 0.0;
		FeM2 = 2.0 * (1.0 - X - Y);
		if (FeM2 > 1.0 - 2.0 * Y) // max FeM2
			FeM2 = 1.0 - 2.0 * Y;
		MaxT = FeM2 - FeM1 - NearZero;
		FeM2 = 1.0 - 2.0 * X - 2.0 * Y; // min FeM2
		if (FeM2 < 0.0)
			FeM2 = 0.0;
		FeM1 = 2.0 * (1.0 - X - Y);
		if (FeM1 > 1.0) // max FeM1
			FeM1 = 1.0;
		MinT = FeM2 - FeM1 + NearZero;
		if (T > MaxT)
			T = MaxT;
		else if (T < MinT)
			T = MinT;
		AX = dG[w13] - dG[w31] + dG[w23] - dG[w32];
		Iter = 0;
		State = Iterating;
		while (State == Iterating) {
			T2 = T / 2.0;
			MgM1 = X + Y + T2;
			MgM2 = X - Y - T2;
			FeM1 = 1.0 - X - Y - T2;
			FeM2 = 1.0 - X - Y + T2;
			dT2 = FeM1 * MgM2;
			dT = MgM1 * FeM2;
			if (dT2 < ASSUMEDZERO || dT < ASSUMEDZERO) {
				randomFeMg();
			} else {
				Kd = dT / dT2;
				if (Kd < ASSUMEDZERO)
					State = TooNearZero;
				else {
					dT2 = dG[g0] + Y * AX + 0.5 * (R * tk * (1.0 / MgM1 + 1.0 / MgM2 + 1.0 / FeM1 + 1.0 / FeM2) - dG[wm1] - dG[wm2]);
					if (Math.abs(dT2) < ASSUMEDZERO)
						State = TooFlat;
					else {
						dT = Y * dG[f01] + dG[g0] * (Y + T);
						dT = dT + dG[ge0] * (Y - 1.0);
						dT = dT + dG[w13] * Y * (T - 1.0 - 2.0 * X + 4.0 * Y);
						dT = dT + dG[w31] * Y * (2.0 * X - T - 1.0 - 4.0 * Y);
						dT = dT + dG[w23] * Y * (3.0 + T - 2.0 * X - 4.0 * Y);
						dT = dT + dG[w32] * Y * (2.0 * X - T - 1.0 + 4.0 * Y);
						dT = dT + dG[wm1] * (0.5 - X - Y - T2);
						dT = dT + dG[wm2] * (X - 0.5 - T2) + R * tk * Math.log(Kd);

						TInc = dT / dT2;
						if (Math.abs(TInc) < Tolerance)
							State = Found;
						else {
							T = T - TInc;
							if (T > MaxT)
								T = MaxT;
							else if (T < MinT)
								T = MinT;
						}
					}
				}
			}
			if (State != Iterating)
				break;
			if (Iter < MaxIter)
				Iter++;
			else
				State = Limit;
		}
		if (State != Found)
			Error = TErrors.Site_Err;
		return Error;
	}

	private int ssact(double TK1, double P1, double A[]) {
		char IC;
		double SaveT, SaveP;

		SaveT = tk;
		SaveP = p;
		setTP(TK1, P1);
		int Error = act();
		for (IC = MgMg; IC <= CaFe; IC++)
			A[IC] = activities[IC];
		setTP(SaveT, SaveP);
		return Error;
	}

	protected int stdStates(int Err) {
		return Err;
	}

	public void writeFile(DataOutputStream F) {
		super.writeFile(F);
		try {
			F.writeDouble(T);
		} catch (IOException e) {
			log.error("Read error: " + e);
		}
	}

	private double xlnX(double X) {
		// Return the value of x*ln(x) if x is not too close to zero.

		if (X > ASSUMEDZERO)
			return (X * Math.log(X));
		else
			return 0.0;
	}
}