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
*//*
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

public class TKarooiteData extends THaas {
	private  static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TKarooiteData.class);
	private double G[];
	double s;

	public TKarooiteData() {
		super();
		int I;
		G = new double[2];
		for (I = 0; I <= 8; I++)
			A[I] = 0;
		for (I = 0; I <= 4; I++)
			B[I] = 0;
		G[0] = 0;
		G[1] = 0;
		s = 0.5; /*
					 * int I; A = new double[13]; B = new double[5]; for (I = 0; I <= 8; I++) A[I] = 0; for (I = 0; I <=
					 * 4; I++) B[I] = 0;
					 */
	}

	public TKarooiteData(String AName, double AMin, double AMax, double AA[], double[] G2, double AB[], TTherm ANext) {
		super(AName, AMin, AMax, AA, 0, 0, AB, ANext);
		G[0] = G2[0];
		G[1] = G2[1];
		s = 0;
		/*
		 * int I;
		 * 
		 * A = new double[13]; B = new double[5]; for (I = 0; I <= 8; I++) A[I] = AA[I]; for (I = 0; I <= 4; I++) B[I] =
		 * AB[I];
		 */
	}

	public void readFile(StreamTokenizer F) {
		int I;
		try {
			F.nextToken();
			VName = new String(F.sval);
			F.nextToken();
			MinT = F.nval; // readDouble();
			F.nextToken();
			MaxT = F.nval; // readDouble();

			Next = null;
			Tk = 0.0;
			PBar = 0.0;
			dUdT = 0.0;
			dUdP = 0.0;
			U0 = 0.0;
			dUCurrent = false;
			UCurrent = false;
			for (I = 0; I <= 8; I++) {
				F.nextToken();
				A[I] = F.nval; // readDouble();
			}
			for (I = 0; I < 2; I++) {
				F.nextToken();
				G[I] = F.nval; // readDouble();
			}
			for (I = 0; I <= 4; I++) {
				F.nextToken();
				B[I] = F.nval; // readDouble();
			}
		} catch (IOException e) {
			log.error("Read error: " + e);
		} catch (Exception e) {
			log.error("Read error: " + e);
		}

	}

	public void writeFile(DataOutputStream F) {
		// TTherm.writeFile(F);
		try {
			int I;
			for (I = 0; I <= 8; I++)
				F.writeDouble(A[I]);
			for (I = 0; I < 2; I++)
				F.writeDouble(G[I]);
			for (I = 0; I <= 4; I++)
				F.writeDouble(B[I]);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}

	public void writeType(DataOutputStream F) {
		try {
			F.writeChar(karooitedata);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}

	}

	double SConf() {
		double s2, sconf;
		s2 = s / 2;
		sconf = (0.5 + s2) * Math.log(.5 + s2);
		sconf += (0.5 - s2) * Math.log(0.5 - s2);
		sconf += (0.5 - s2) * Math.log(0.25 - s / 4);
		sconf += (1.5 + s2) * Math.log(0.75 + s / 4);
		return sconf;
	}

	double dGdS(double S) {
		double dgds;
		dgds = G[0] + 3 * G[1] * S + 0.5 * TPhase.R * Tk * Math.log((1 + S) * (3 + S) / (1 - S) / (1 - S));
		return dgds;
	}

	double d2GdS2(double S) {
		double d2;
		d2 = 3 * G[1] + 0.5 * TPhase.R * Tk * (1 / (1 + S) + 1 / (3 + S) + 2 / (1 - S));
		return d2;
	}
	static final int MAXITS = 100;
	static final double TOL = 1.0E-06;

	boolean calcS() {
		boolean iterating = true;
		double x, oldx, fx, fdx;
		int count;
		x = s;
		fx = dGdS(x);
		count = 0;
		while (iterating) {
			if (Math.abs(x) < TPhase.ASSUMEDZERO)
				return false;
			else {
				count++;
				fdx = d2GdS2(x);
				if (Math.abs(fdx) < TPhase.ASSUMEDZERO)
					return false;
				else {
					oldx = x;
					x = x - fx / fdx;
					if (x < 0)
						x = TPhase.ASSUMEDZERO;
					else if (x > TPhase.ALMOSTONE)
						x = TPhase.ALMOSTONE;
					fx = dGdS(x);
					if ((Math.abs(x - oldx) / oldx) <= TOL) {
						s = x;
						return true;
					}
				}
			}
			count++;
			if (count > MAXITS)
				iterating = false;
		}
		return false;
	}

	int dMu0(double ATK, double APBar) {
		// Return dG/dt, dg/dp at T, PBar

		int Error = TErrors.No_Err;
		if ((ATK > MaxT) && (Next != null))
			Error = Next.dMu0(ATK, APBar);
		else {
			Tk = ATK;
			PBar = APBar;
			if (Tk < MinT)
				Error = TErrors.TP_Err;
			dUdT = -(A[1] / 6.0) * (-2.0 * (1.0 / (Tk * Tk * Tk) - 1.0 / (T2REF * TREF))) - (A[2] / 2.0) * (-(1.0 / (Tk * Tk) - 1.0 / T2REF)) + A[3] * (1.0 / Tk - 1.0 / TREF) + 2.0 * A[4]
					* ((1.0 / Math.sqrt(Tk) - 1.0 / Math.sqrt(TREF))) + A[5] * (-Math.log(Tk / TREF)) + (A[6] / 2.0) * (-2.0 * (Tk - TREF)) + (A[7] / 6.0) * (-3.0 * (Tk * Tk - T2REF)) - A[0];
			dUdT -= TPhase.R * SConf();
			dUdT = dUdT + (B[1] + B[2] / 300.0 * Math.exp(-Tk / 300)) * (PBar - 1.0);
			dUdP = B[0] + B[1] * Tk - (B[2] / 300.0) * Math.exp(-Tk / 300.0) + B[3] * PBar + B[4] * Math.exp(-PBar / 35000.0) / 35000.0 / 35000.0;
			// dT = dUdT;
			// dP = dUdP;
		}
		return Error;
	}

	protected int mu0(double ATK, double APBar) {
		/*
		 * Returns G at T, PBar Returns 0, error=tp_err, if t<=0. If T is outside of the temperature range, returns
		 * extrapolation, sets error=tp_err, if warning writes error message to screen else error = no_err
		 */
		double T2;
		int Error = TErrors.No_Err;
		if (ATK > MaxT && Next != null) {
			return Next.mu0(ATK, APBar);
		} else {
			Tk = ATK;
			PBar = APBar;
			if (!calcS())
				Error = TErrors.TP_Err;
			T2 = Tk * Tk;
			if (Tk < MinT)
				Error = TErrors.TP_Err;
			U0 = -(A[1] / 6.0) * (3.0 * (1.0 / T2 - 1.0 / T2REF) - 2.0 * Tk * (1.0 / (T2 * Tk) - 1.0 / T3REF));
			U0 += -(A[2] / 2.0) * (2.0 * (1.0 / Tk - 1.0 / TREF) - Tk * (1.0 / T2 - 1.0 / T2REF));
			U0 += A[3] * (Math.log(Tk / TREF) + Tk * (1.0 / Tk - 1.0 / TREF));
			U0 += 2.0 * A[4] * (Math.sqrt(Tk) - Math.sqrt(TREF) + Tk * (1.0 / Math.sqrt(Tk) - 1.0 / Math.sqrt(TREF)));
			U0 += A[5] * (Tk - TREF - Tk * Math.log(Tk / TREF));
			U0 += (A[6] / 2.0) * ((T2 - T2REF) - 2.0 * Tk * (Tk - TREF));
			U0 += (A[7] / 6.0) * (2.0 * (T2 * Tk - T3REF) - 3.0 * Tk * (T2 - T2REF));
			U0 += A[8] - A[0] * (Tk - TREF);
			U0 += G[0] * s + 0.5 * G[1] * (3.0 * s * s - 1);

			U0 -= TPhase.R * Tk * SConf();
			U0 += (B[0] + Tk * B[1] - (B[2] / 300.0) * Math.exp(-Tk / 300.0)) * (PBar - 1.0) + (B[3] / 2.0) * (PBar * PBar - 1.0) - (B[4] / 35000.0)
					* (Math.exp(-PBar / 35000.0) - Math.exp(-1.0 / 35000.0));
		}
		return Error;
	}

}