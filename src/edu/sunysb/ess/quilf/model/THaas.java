package edu.sunysb.ess.quilf.model;

/*
 calculates thermodynamic standard state data, part of QUIlF

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
/*
 1/19/97
 As written the "current" variables can't be used because of the multiple equations for
 some of the phases. Say the values are calculated for a phase at one temperature with
 one equation. The next time through, the routine may incorrectly assume that the
 temperature hasn't changed if a different equation is used. This was noticed when
 using the quartz values from 800 to 1200. The second time through, the standard state
 value at 1200 was used even though the temperature was 800. The Pascal version
 correctly handles it though.
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StreamTokenizer;

// ------------------------------ THaasData ------------------------------
class THaas extends TTherm {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TTherm.class);
	double A[];
	int Ja, Jb;
	double B[];

	THaas() {
		super();
		int I;
		A = new double[14];
		B = new double[5];
		for (I = 0; I <= 13; I++)
			A[I] = 0;
		Ja = 0;
		Jb = 0;
		for (I = 0; I <= 4; I++)
			B[I] = 0;
	}

	THaas(String AName, double AMin, double AMax, double AA[], int AJa, int AJb, double AB[], TTherm ANext) {
		super(AName, AMin, AMax, ANext);
		int I;
		A = new double[14];
		B = new double[5];
		for (I = 0; I <= 13; I++)
			A[I] = AA[I];
		Ja = AJa;
		Jb = AJb;
		for (I = 0; I <= 4; I++)
			B[I] = AB[I];
	}

	int dMu0(double ATK, double APBar) {// , double dT, double dP) {
		// Return dG/dt, dg/dp at T, PBar
		double Tau;
		int Error = TErrors.No_Err;
		if ((ATK > MaxT) && (Next != null)) {
			Error = Next.dMu0(ATK, APBar); // ,dT,dP);
			dUdT = Next.dUdT;
			dUdP = Next.dUdP;
		} else {
			Tk = ATK;
			PBar = APBar;
			if (Tk < MinT)
				Error = TErrors.TP_Err;
			dUdT = -A[0] - A[10] + A[1] / (3.0 * Tk * Tk * Tk) + A[2] / (2.0 * Tk * Tk) + A[3] / Tk + 2.0 * A[4] / Math.sqrt(Tk) - A[5] * Math.log(Tk) - A[6] * Tk - A[7] * Tk * Tk / 2.0;
			if (A[11] != 0.0) {
				Tau = Tk / A[11];
				if (Tau < 1.0)
					dUdT = dUdT - A[12] * lambda(Ja, Tau);
				else {
					dUdT = dUdT - A[13] * lambda(Jb, Tau);
					dUdT = dUdT - A[12] * lambdaT(Ja); // , Tau);
					dUdT = dUdT + A[13] * lambdaT(Jb); // , Tau);
				}
			}
			dUdT = dUdT + (B[1] - B[2] / 300.0 * Math.exp(-Tk / 300)) * (PBar - PREF);
			dUdP = B[0] + B[1] * Tk + B[2] * Math.exp(-Tk / 300.0) + B[3] * PBar + B[4] * Math.exp(-PBar / 35000.0) / 35000.0 / 35000.0;
			// dT = dUdT;
			// dP = dUdP;
			return Error;
		}
		return Error;
	}

	double lambda(int J, double Tau) {
		int K, Kp;
		double Sum;

		Sum = 0.0;
		for (K = 1; K <= 7; K++) {
			Kp = K * 2 - 1;
			Sum = Sum + Math.pow(Tau, J * Kp) / (J * Kp * Kp);
		}
		return Sum;
	}

	double lambda_(int J, double Tau, double T, double A) {
		int K, Kp;
		double Sum;

		Sum = 0.0;
		for (K = 1; K <= 7; K++) {
			Kp = K * 2 - 1;
			Sum = Sum + A * Math.pow(Tau, J * Kp + 1) / (Kp * (J * Kp + 1)) - T * Math.pow(Tau, J * Kp) / (J * Kp * Kp);
		}
		return Sum;
	}

	double lambdaT(int J) {// , double Tau) {
		int K, Kp;
		double Sum;

		Sum = 0.0;
		for (K = 1; K <= 7; K++) {
			Kp = K * 2 - 1;
			Sum = Sum + 1.0 / (J * Kp * Kp);
		}
		return Sum;
	}

	double lambdaT_(int J, double T, double A) {
		int K, Kp;
		double Sum;

		Sum = 0.0;
		for (K = 1; K <= 7; K++) {
			Kp = K * 2 - 1;
			Sum = Sum + A / (Kp * (J * Kp + 1)) - T / (J * Kp * Kp);
		}
		return Sum;
	}

	int mu0(double ATK, double APBar) { // , int Error) {
		/*
		 * Returns G at T, PBar Returns 0, error=tp_err, if t<=0. If T is outside of the temperature range, returns
		 * extrapolation, sets error=tp_err, if warning writes error message to screen else error = no_err
		 */
		double Tau;
		int Error = TErrors.No_Err;
		if (ATK > MaxT && Next != null) {
			Error = Next.mu0(ATK, APBar);
			U0 = Next.u0();
		} else {
			double lnt = Math.log(ATK);
			Tk = ATK;
			PBar = APBar;
			if (Tk < MinT)
				Error = TErrors.TP_Err;
			U0 = -A[1] / (6.0 * Tk * Tk);
			U0 += -A[2] / (2.0 * Tk);
			U0 += A[3] * (1.0 + lnt);
			U0 += 4.0 * A[4] * Math.sqrt(Tk);
			U0 += A[5] * Tk * (1.0 - lnt);
			U0 -= A[6] * Tk * Tk / 2.0;
			U0 -= A[7] * Tk * Tk * Tk / 6.0;
			lnt = A[9] - Tk * (A[0] + A[10]);
			U0 += lnt;
			if (A[11] != 0.0) {
				Tau = Tk / A[11];
				if (Tau < 1.0)
					U0 = U0 + A[12] * lambda_(Ja, Tau, Tk, A[11]);
				else {
					U0 = U0 + A[13] * lambda_(Jb, Tau, Tk, A[11]);
					U0 = U0 + A[12] * lambdaT_(Ja, Tk, A[11]);
					U0 = U0 - A[13] * lambdaT_(Jb, Tk, A[11]);
				}
			}
			U0 = U0 + (B[0] + Tk * B[1] + B[2] * Math.exp(-Tk / 300.0)) * (PBar - PREF) + B[3] * (PBar * PBar - PREF * PREF) / 2.0 - B[4] * (Math.exp(-PBar / 35000.0) - Math.exp(-PREF / 35000.0))
					/ 35000.0;
		}
		return Error;
	}

	public void readFile(StreamTokenizer F) {
		super.readFile(F);
		int I;
		try {
			for (I = 0; I <= 13; I++) {
				F.nextToken();
				// log.debug("A["+I+"]" + F.ttype + ":" + F.nval);
				A[I] = F.nval; // readDouble();
			}
			F.nextToken();
			// log.debug("Ja" + F.ttype + ";" + F.nval);
			Ja = (int) F.nval; // readInt();
			F.nextToken();
			// log.debug("Jb" + F.ttype + ";" + F.nval);
			Jb = (int) F.nval; // readInt();
			for (I = 0; I <= 4; I++) {
				F.nextToken();
				B[I] = F.nval; // readDouble();
				// log.debug("B["+I+"]" + F.ttype + ";" + F.nval);
			}
		} catch (IOException e) {
			log.error("Read error: " + e);
		} catch (Exception e) {
			log.error("Read error: " + e);
		}
	}

	public void writeFile(DataOutputStream F) {
		super.writeFile(F);
		try {
			int I;
			for (I = 0; I <= 13; I++)
				F.writeDouble(A[I]);
			F.writeInt(Ja);
			F.writeInt(Jb);
			for (I = 0; I <= 4; I++)
				F.writeDouble(B[I]);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}

	public void writeType(DataOutputStream F) {
		try {
			F.writeChar(haasdata);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}
}