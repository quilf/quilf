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

// ------------------------------ THelgesonData ------------------------------
class THelg extends TTherm {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(THelg.class);

	double A[];
	double B[];

	THelg() {
		super();
		int I;
		A = new double[5];
		B = new double[3];
		for (I = 0; I <= 4; I++)
			A[I] = 0;
		for (I = 0; I <= 2; I++)
			B[I] = 0;
	}

	THelg(String AName, double AMin, double AMax, double AA[], double AB[], TTherm ANext) {
		super(AName, AMin, AMax, ANext);
		int I;
		A = new double[5];
		B = new double[3];
		for (I = 0; I <= 4; I++)
			A[I] = AA[I];
		for (I = 0; I <= 2; I++)
			B[I] = AB[I];
	}

	int dMu0(double ATK, double APBar) {// , double dT, double dP) {
		// Return dG/dt, dg/dp at T, PBar
		double t;
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
			t = Tk / TREF;
			dUdT = -A[4] - A[0] * Math.log(t) - A[1] * (Tk - TREF) - A[2] * (1.0 / (Tk * Tk)) / 2.0 - A[2] * (-1.0 / T2REF) / 2.0 + B[0] * B[1] * (1.0 + B[2]) * (PBar - 1.0);
			dUdP = B[0] * (1.0 + B[1] * (Tk - TREF)) * (1.0 + B[2] * (1.0 - PBar));
			// dT = dUdT;
			// dP = dUdP;
		}
		return Error;
	}

	int mu0(double ATK, double APBar) { // , int Error) {
		/*
		 * Returns G at T, pbar Returns 0, error=tp_err, if t<=0. If T is outside of the temperature range, returns
		 * extrapolation, sets error=tp_err, if warning writes error message to screen else error = no_err
		 */
		int Error = TErrors.No_Err;
		if (ATK > MaxT && Next != null) {
			Error = Next.mu0(ATK, APBar);
			U0 = Next.u0(); // set current = next
			return Error;
		} else {
			Tk = ATK;
			PBar = APBar;
			if (Tk < MinT)
				Error = TErrors.TP_Err;
			U0 = A[3] - A[4] * (Tk - TREF);
			U0 = U0 + A[0] * (Tk - TREF - Tk * Math.log(Tk / TREF));
			U0 = U0 + (A[2] - A[1] * Tk * T2REF) * (Tk - TREF) * (Tk - TREF) / (2.0 * Tk * T2REF);
			U0 = U0 + B[0] * (1.0 + B[1] * (Tk - TREF)) * ((1.0 + B[2]) * (PBar - 1.0) - 0.5 * B[2] * (PBar * PBar - 1.0));
		}
		return Error;
	}

	public void readFile(StreamTokenizer F) {
		super.readFile(F);
		int I;
		try {
			for (I = 0; I <= 4; I++) {
				F.nextToken();
				A[I] = F.nval; // readDouble();
			}
			for (I = 0; I <= 2; I++) {
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
		super.writeFile(F);
		try {
			int I;
			for (I = 0; I <= 4; I++)
				F.writeDouble(A[I]);
			for (I = 0; I <= 2; I++)
				F.writeDouble(B[I]);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}

	public void writeType(DataOutputStream F) {
		try {
			F.writeChar(helgesondata);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}
}