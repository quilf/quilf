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
/*
 calculates thermodynamic standard state data, part of QUIlF

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

// ------------------------------ TDimitriData ------------------------------
// A[0] = dS(298,1)
// A[1] = a1
// ..
// A[7] = a7
// A[8] = dG(298,1)
// B[0] = b1
// B[1] = b2
// B[2] = b3
// B[3] = b4
// B[4] = b5
public class TDimitri extends THaas {
	private  static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TDimitri.class);
	// double A[];
	// double B[];

	TDimitri() {
		super();
		/*
		 * int I; A = new double[13]; B = new double[5]; for (I = 0; I <= 8; I++) A[I] = 0; for (I = 0; I <= 4; I++)
		 * B[I] = 0;
		 */
	}

	TDimitri(String AName, double AMin, double AMax, double AA[], int AJa, int AJb, double AB[], TTherm ANext) {
		super(AName, AMin, AMax, AA, AJa, AJb, AB, ANext);
		/*
		 * int I;
		 * 
		 * A = new double[13]; B = new double[5]; for (I = 0; I <= 8; I++) A[I] = AA[I]; for (I = 0; I <= 4; I++) B[I] =
		 * AB[I];
		 */
	}

	int dMu0(double ATK, double APBar) { // , double dT, double dP) {
		// Return dG/dt, dg/dp at T, PBar

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
			dUdT = -(A[1] / 6.0) * (-2.0 * (1.0 / (Tk * Tk * Tk) - 1.0 / (T2REF * TREF))) - (A[2] / 2.0) * (-(1.0 / (Tk * Tk) - 1.0 / T2REF)) + A[3] * (1.0 / Tk - 1.0 / TREF) + 2.0 * A[4]
					* ((1.0 / Math.sqrt(Tk) - 1.0 / Math.sqrt(TREF))) + A[5] * (-Math.log(Tk / TREF)) + (A[6] / 2.0) * (-2.0 * (Tk - TREF)) + (A[7] / 6.0) * (-3.0 * (Tk * Tk - T2REF)) - A[0];
			// for now 7/13/98 we will ignore the t, p derivatives of Dimitris lambda transition
			// i.e. the Hlro, Hsro, Ssro, Slro parameters in the ::Mu0 routine
			dUdT = dUdT + (B[1] + B[2] / 300.0 * Math.exp(-Tk / 300)) * (PBar - 1.0);
			dUdP = B[0] + B[1] * Tk - (B[2] / 300.0) * Math.exp(-Tk / 300.0) + B[3] * PBar + B[4] * Math.exp(-PBar / 35000.0) / 35000.0 / 35000.0;
		}

		/*
		 * else { Tk = ATK; PBar = APBar; if (Tk < MinT) Error=Errors.TP_Err; dUdT =
		 * -(A[1]/6.0)*(-2.0*(1.0/(Tk*Tk*Tk)-1.0/(T2REF*TREF))) - (A[2]/2.0)*(- (1.0/(Tk*Tk)-1.0/T2REF)) + A[3] *
		 * (1.0/Tk-1.0/TREF) + 2.0*A[4]*((1.0/Math.sqrt(Tk)-1.0/Math.sqrt(TREF))) + A[5]*(-Math.log(Tk/TREF)) +
		 * (A[6]/2.0)*(-2.0*(Tk-TREF)) + (A[7]/6.0)*(-3.0*(Tk*Tk-T2REF)) -A[0]; dUdT = dUdT + (B[1] + B[2]/300.0 *
		 * Math.exp(-Tk/300)) * (PBar - 1.0); dUdP = B[0] + B[1] * Tk - (B[2]/300.0) * Math.exp(-Tk/300.0) + B[3]*PBar +
		 * B[4] * Math.exp(-PBar/35000.0)/35000.0/35000.0; // dT = dUdT; // dP = dUdP; }
		 */
		return Error;
	}

	int mu0(double ATK, double APBar) {
		/*
		 * Returns G at T, PBar Returns 0, error=tp_err, if t<=0. If T is outside of the temperature range, returns
		 * extrapolation, sets error=tp_err, if warning writes error message to screen else error = no_err
		 */
		double T2;
		int Error = TErrors.No_Err;
		if (ATK > MaxT && Next != null) {
			Error = Next.mu0(ATK, APBar); // , Error);
			U0 = Next.u0();
		} else {
			Tk = ATK;
			PBar = APBar;
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

			// new part 7/13/98 from Dimitris
			if (A[11] != 0.0) {
				double tc = A[11] - 0.0053 * (PBar - PREF);
				double Hlro = (A[12] / ((Ja + 1) * Math.pow(tc, Ja))) * (Math.pow(tc, Ja + 1) - Math.pow(TREF, Ja + 1)) + (A[12] / (3 * (3 * Ja + 1) * Math.pow(tc, Ja * 3)))
						* (Math.pow(tc, 3 * Ja + 1) - Math.pow(TREF, 3 * Ja + 1));
				double Slro = (A[12] / (Ja * Math.pow(tc, Ja)) * (Math.pow(tc, Ja) - Math.pow(TREF, Ja))) + (A[12] / (3 * (3 * Ja) * Math.pow(tc, 3 * Ja)))
						* (Math.pow(tc, 3 * Ja) - Math.pow(TREF, 3 * Ja));
				U0 += Hlro - Tk * Slro;

				double Hsro = (A[13] / ((Jb + 1) * Math.pow(tc, Jb))) * (Math.pow(Tk, Jb + 1) - Math.pow(tc, Jb + 1)) + (A[13] / (3 * (3 * Jb + 1) * Math.pow(tc, Jb * 3)))
						* (Math.pow(Tk, 3 * Jb + 1) - Math.pow(tc, 3 * Jb + 1));
				double Ssro = (A[13] / (Jb * Math.pow(tc, Jb)) * (Math.pow(Tk, Jb * 3) - Math.pow(tc, Jb))) + (A[13] / (3 * (3 * Jb) * Math.pow(tc, 3 * Jb)))
						* (Math.pow(Tk, 3 * Jb) - Math.pow(tc, 3 * Jb));
				U0 += Hsro - Tk * Ssro;
			}
			// end of new

			U0 = U0 + (B[0] + Tk * B[1] - (B[2] / 300.0) * Math.exp(-Tk / 300.0)) * (PBar - 1.0) + (B[3] / 2.0) * (PBar * PBar - 1.0) - (B[4] / 35000.0)
					* (Math.exp(-PBar / 35000.0) - Math.exp(-1.0 / 35000.0));
		}
		// return U0;
		return Error;
	}

	public void readFile(StreamTokenizer F) {
		super.readFile(F);
		/*
		 * try { int I; for (I = 0; I <= 8; I++) { F.nextToken(); A[I] = F.nval; //readDouble(); } for (I = 0; I <= 4;
		 * I++) { F.nextToken(); B[I] = F.nval; //readDouble(); } } catch (IOException e) { log.error("Write
		 * error: " + e); }
		 */
	}

	public void writeFile(DataOutputStream F) {
		super.writeFile(F);
		/*
		 * try { int I; for (I = 0; I <= 8; I++) F.writeDouble(A[I]); for (I = 0; I <= 4; I++) F.writeDouble(B[I]); }
		 * catch (IOException e) { log.error("Write error: " + e); }
		 */
	}

	public void writeType(DataOutputStream F) {
		try {
			F.writeChar(dimitridata);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}

	}
}