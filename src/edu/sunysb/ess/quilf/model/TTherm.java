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

import edu.sunysb.ess.quilf.swing.Quilf;

// ------------------------------ TThermData ------------------------------
public class TTherm {
	private  static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TTherm.class);
	protected final static int nullpointer = 0;
	protected final static int thermdata = 1;
	protected final static int haasdata = 2;
	protected final static int helgesondata = 3;
	protected final static int helgesonqtz = 4;
	protected final static int dimitridata = 5;
	protected final static int karooitedata = 6;
	protected final double PREF = 1.01325;
	protected final double TREF = 298.15;
	protected final double T2REF = 88893.4225; // 298.15^2
	protected final double T3REF = (TREF * T2REF);
	protected String VName;
	protected boolean UCurrent, dUCurrent;
	public double Tk, PBar, U0, dUdT, dUdP, MinT, MaxT;
	protected TTherm Next;

	public TTherm() {
		VName = new String();
		MinT = 0;
		MaxT = 0;
		Next = null;
		Tk = 0.0;
		PBar = 0.0;
		dUdT = 0.0;
		dUdP = 0.0;
		U0 = 0.0;
		dUCurrent = false;
		UCurrent = false;
	}

	TTherm(String AName, double AMin, double AMax, TTherm ANext) {
		VName = new String(AName);
		MinT = AMin;
		MaxT = AMax;
		Next = ANext;
		Tk = 0.0;
		PBar = 0.0;
		dUdT = 0.0;
		dUdP = 0.0;
		U0 = 0.0;
		dUCurrent = false;
		UCurrent = false;
	}

	int dMu0(double ATK, double APBar) { // , double dT, double dP) {
		int Error = TErrors.No_Err;
		this.Tk = ATK;
		this.PBar = APBar;
		// dT = dT;
		// dP = dP; // needed for derived classes
		return Error;
	}

	double dP() {
		return dUdP;
	}

	double dT() {
		return dUdT;
	}

	void errorMessage(int Error) {
		String ErrStr;
		if (Error == TErrors.TP_Err)
			ErrStr = new String("Temperature[" + Double.toString(Tk) + "]/pressure[" + Double.toString(PBar) + "] outside of allowable range in " + VName);
		else
			ErrStr = new String("Error " + Integer.toString(Error) + " in " + VName);
		Quilf.showStatus(ErrStr, Error);
		// MessageBox (HWND_DESKTOP, ErrStr, "Error", MB_OK);
	}

	int mu0(double ATK, double APBar) {
		int Error = TErrors.No_Err;
		this.Tk = ATK;
		this.PBar = APBar; // need it for derived classes
		U0 = 0;
		return Error;
	}

	public void readFile(StreamTokenizer F) {
		try {
			// F.nextToken();
			// len =(int) F.nval; //readInt();
			F.nextToken();
			VName = new String(F.sval);
			F.nextToken();
			MinT = F.nval; // readDouble();
			F.nextToken();
			MaxT = F.nval; // readDouble();
		} catch (IOException e) {
			log.error("Read error: " + e);
		}
		Next = null;
		Tk = 0.0;
		PBar = 0.0;
		dUdT = 0.0;
		dUdP = 0.0;
		U0 = 0.0;
		dUCurrent = false;
		UCurrent = false;
	}

	int readType(StreamTokenizer F) {
		int c = nullpointer;
		try {
			F.nextToken();
			c = (int) F.nval;
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
		return c;
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		if (VName != null)
			return VName;
		return "TThermData";
	}

	double u0() {
		return U0;
	}

	public int updateU0(double ATK, double APBar) {
		/*
		 * ignore possible error in mu0 if tk is outside range mu0 will exit if tk is too close to zero if an error
		 * occurs and Warning is TRUE or err=no_err on entry then print an error message
		 */
		int Error = mu0(ATK, APBar);
		if (Error != TErrors.No_Err) {
			errorMessage(Error);
		}
		return Error;
	}

	public void writeFile(DataOutputStream F) {
		int i;
		try {
			// F.writeInt(VName.length());
			for (i = 0; i < VName.length(); i++)
				F.writeChar(VName.charAt(i));
			F.writeDouble(MinT);
			F.writeDouble(MaxT);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}

	public void writeType(DataOutputStream F) {
		try {
			F.writeChar(thermdata);
		} catch (IOException e) {
			log.error("Write error: " + e);
		}
	}
}