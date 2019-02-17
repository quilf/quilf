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

// ------------------------------ THelgesonQtz ------------------------------
class THelgQtz extends TTherm {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(THelgQtz.class);
	double S_Alpha, A1_Alpha, B1_Alpha, C1_Alpha, S_Beta, A1_Beta, B1_Beta, C1_Beta, V_Beta, T_Quartz, K_Quartz, A_Alpha, B_Alpha, C_Alpha, V_Alpha;

	THelgQtz() {
		super();
	}

	THelgQtz(String AName) {
		super(AName, 298.15, 2000.0, null);
	}

	private double cps(double TRef, double TK, double s, double A1, double B1, double C1) {
		return (4.184 * (B1 * (TRef - TK) - A1 * Math.log(TK / TRef) - s - C1 * (1.0 / (TRef * TRef) - 1.0 / (TK * TK)) / 2.0));
	}

	private double cpt(double TRef, double TK, double S_, double A1, double B1, double C1) {
		double TEMP;

		TEMP = TK - TRef;
		return (4.184 * (S_ * (TRef - TK) + A1 * (TK - TRef - TK * Math.log(TK / TRef)) + (C1 - B1 * TK * TRef * TRef) * (TEMP * TEMP) / (2.0 * TK * TRef * TRef)));
	}

	int dMu0(double ATK, double APBar) { // , double dT, double dP) { // pmd's equations
		// Return dG/dt, dg/dp at T

		double PTrans;

		int Error = TErrors.No_Err;
		Tk = ATK;
		PBar = APBar;
		dUdT = 0.0;
		dUdP = 0.0;
		if (Tk > T_Quartz) {
			dUdT = cps(T_Quartz, Tk, S_Beta, A1_Beta, B1_Beta, C1_Beta);
			PTrans = K_Quartz * (Tk - T_Quartz) + 1.0;
			if (PTrans > PBar)
				dUdP = V_Beta;
			else {
				dUdT = dUdT
						+ K_Quartz
						* (V_Beta - V_Alpha + C_Alpha * (K_Quartz * (Tk - TREF + 1.0) - PBar) + (B_Alpha + A_Alpha * C_Alpha * K_Quartz)
								* (Math.log((A_Alpha * K_Quartz + PBar) / (A_Alpha * K_Quartz + PTrans)) - (Tk - TREF) * K_Quartz / (A_Alpha * K_Quartz + PTrans)));
				dUdP = V_Alpha + C_Alpha * (PBar - 1.0) + K_Quartz * (Tk - TREF) * (B_Alpha - C_Alpha * PBar) / (A_Alpha * K_Quartz + PBar);
			}
		} else {
			dUdT = cps(TREF, Tk, S_Alpha, A1_Alpha, B1_Alpha, C1_Alpha);
			dUdT = dUdT + K_Quartz * (B_Alpha + A_Alpha * C_Alpha * K_Quartz) * Math.log((A_Alpha * K_Quartz + PBar) / (A_Alpha * K_Quartz + 1.0)) - C_Alpha * K_Quartz * (PBar - 1.0);
			dUdP = V_Alpha + C_Alpha * (PBar - 1.0) + K_Quartz * (Tk - TREF) * (B_Alpha - C_Alpha * PBar) / (A_Alpha * K_Quartz + PBar);
		}
		// dT = dUdT;
		// dP = dUdP;
		return Error;
	}

	int mu0(double ATK, double APBar) { // Mu0, pmd's equations
		/*
		 * Returns G at T, pbar Returns 0, error=tp_err, if t<=0. If T is outside of the temperature range, returns
		 * extrapolation, sets error=tp_err, if warning writes error message to screen else error = no_err
		 */
		double Sum, PTrans;

		int Error = TErrors.No_Err;
		if (ATK > MaxT && Next != null) {
			Error = Next.mu0(ATK, APBar);
			U0 = Next.u0();
			return Error;
		} else {
			U0 = 0.0;
			Tk = ATK;
			PBar = APBar;
			if (Tk < MinT)
				Error = TErrors.TP_Err;
			if (Tk > T_Quartz) {
				U0 = cpt(TREF, T_Quartz, S_Alpha, A1_Alpha, B1_Alpha, C1_Alpha);
				U0 = U0 + cpt(T_Quartz, Tk, S_Beta, A1_Beta, B1_Beta, C1_Beta);
				PTrans = K_Quartz * (Tk - T_Quartz) + 1.0;
				if (PTrans > PBar)
					U0 = U0 + V_Beta * (PBar - 1.0);
				else {
					U0 = U0 + V_Alpha * (PBar - PTrans) + V_Beta * (PTrans - 1.0) - C_Alpha * (PTrans - PBar) * (PTrans + PBar - 2.0) / 2.0 - C_Alpha * K_Quartz * (Tk - TREF) * (PBar - PTrans);
					Sum = Math.log((A_Alpha * K_Quartz + PBar) / (A_Alpha * K_Quartz + PTrans));
					U0 = U0 + K_Quartz * (B_Alpha + A_Alpha * C_Alpha * K_Quartz) * (Tk - TREF) * Sum;
				}
			} else {
				U0 = cpt(TREF, Tk, S_Alpha, A1_Alpha, B1_Alpha, C1_Alpha);
				U0 = U0 + V_Alpha * (PBar - 1.0) + 0.5 * C_Alpha * (1.0 - PBar) * (1.0 - PBar);
				U0 = U0 - C_Alpha * K_Quartz * (Tk - TREF) * (PBar - 1.0);
				Sum = Math.log((A_Alpha * K_Quartz + PBar) / (A_Alpha * K_Quartz + 1.0));
				U0 = U0 + K_Quartz * (B_Alpha + A_Alpha * C_Alpha * K_Quartz) * (Tk - TREF) * Sum;
			}
			U0 = U0 - 856238.864;
		}
		return Error; // U0;
	}

	public void readFile(StreamTokenizer F) {
		super.readFile(F);
		try {
			F.nextToken();
			S_Alpha = F.nval; // readDouble();
			F.nextToken();
			A1_Alpha = F.nval; // readDouble();
			F.nextToken();
			B1_Alpha = F.nval; // readDouble();
			F.nextToken();
			C1_Alpha = F.nval; // readDouble();
			F.nextToken();
			S_Beta = F.nval; // readDouble();
			F.nextToken();
			A1_Beta = F.nval; // readDouble();
			F.nextToken();
			B1_Beta = F.nval; // readDouble();
			F.nextToken();
			C1_Beta = F.nval; // readDouble();
			F.nextToken();
			V_Beta = F.nval; // readDouble();
			F.nextToken();
			T_Quartz = F.nval; // readDouble();
			F.nextToken();
			K_Quartz = F.nval; // readDouble();
			F.nextToken();
			A_Alpha = F.nval; // readDouble();
			F.nextToken();
			B_Alpha = F.nval; // readDouble();
			F.nextToken();
			C_Alpha = F.nval; // readDouble();
			F.nextToken();
			V_Alpha = F.nval; // readDouble();
		} catch (IOException e) {
			log.error("Read error: " + e);
		} catch (Exception e) {
			log.error("Read error: " + e);
		}
	}

	public void writeFile(DataOutputStream F) {
		super.writeFile(F);
		try {
			F.writeDouble(S_Alpha);
			F.writeDouble(A1_Alpha);
			F.writeDouble(B1_Alpha);
			F.writeDouble(C1_Alpha);
			F.writeDouble(S_Beta);
			F.writeDouble(A1_Beta);
			F.writeDouble(B1_Beta);
			F.writeDouble(C1_Beta);
			F.writeDouble(V_Beta);
			F.writeDouble(T_Quartz);
			F.writeDouble(K_Quartz);
			F.writeDouble(A_Alpha);
			F.writeDouble(B_Alpha);
			F.writeDouble(C_Alpha);
			F.writeDouble(V_Alpha);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}

	public void writeType(DataOutputStream F) {
		try {
			F.writeChar(helgesonqtz);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}
}