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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.sunysb.ess.quilf.swing.Quilf;
import edu.sunysb.ess.quilf.swing.TField;
import edu.sunysb.ess.quilf.swing.TNumFld;
import edu.sunysb.ess.quilf.swing.TQPanel;
import edu.sunysb.ess.quilf.swing.TRow;
import edu.sunysb.ess.quilf.swing.TTextLab;

// ------------------------------ TPhase ------------------------------
public class TPhase {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TPhase.class);

	public static final int G = 0;
	public static final int H = 1;
	public static final int S = 2;
	public static final int V = 3;
	public static final double ZERO = 1.0e-12;
	public static final double ASSUMEDZERO = 1.0e-20;
	public static final double INFINITY = 1.0e+20;
	public static final double EXP0 = -1.0e+99;
	public static final int MAXCOMPONENTS = 6; // Maximum number of components in a phase
	public static final int MAXNAME = 12; // Maximum length of a name
	public static final int MAXVARIABLES = 16; // Maximum number of variables possible
	public static final double ONE = 0.9999999;

	public static final double R = 8.3143;
	public static final double ONEHALF = 0.49999;
	public static final double ALMOSTONE = 0.99999;
	public static final double MAXACT = 1.000001;
	public static final double LOG10 = 2.302585093;
	public static final int MAXCOLS = 16;
	private int numVar;
	public int lowerComponent, upperComponent; // indices of the components
	private String phaseName = new String(); // MaxName+1); //name of phase
	public String componentName[]; // name of components
	public int lowerVariable, upperVariable; // indices of the solution parameters
	public double sln[][]; // points to solution values
	public double tk, p;
	boolean actCurrent, dTdPCurrent, dXCurrent;
	public double activities[], dAdT[], dAdP[];
	public double dX[][];
	public TTherm u0P[]; // standard state entries
	public double u0Array[][]; // points to standard state values

	public Vector<TRow> components;
	private boolean phasePresent, phaseVariable;
	private boolean findBest;
	public double xCalcComposition[]; // calculated compositions
	public double xCalcError[]; // calculated errors
	public double xInitialComposition[]; // initial compositions
	public double xFinalComposition[]; // final compositions (for/next loops)
	public double xStep[]; // step compositions (for/next loops)
	public double xWeight[]; // weight for best fit compositions
	public double xBestFitComposition[]; // best fit compositions

	// public static final int Rctns = 0;
	// public static final int Ol = 1;
	// public static final int Aug = 2;
	// public static final int Pig = 3;
	// public static final int Opx = 4;
	// public static final int Sp = 5;
	// public static final int Ilm = 6;
	// public static final int Iron = 7;
	// public static final int Quartz = 8;
	// public static final int Rutile = 9;
	// public static final int Titanite = 10;
	// public static final int Oxygen = 11;
	// public static final int Karooite = 12;
	// public static final int NUMPH = 13;

	public TPhase() {
		this("noname");

	}

	public TPhase(String name) {
		numVar = 0;
		// NumCells = 0;
		/*
		 * clinopyroxene = null; ilmenite = null; iron = null; olivine = null; orthopyroxene = null; oxygen = null;
		 * quartz = null; rctn = null; rutile = null; spinel = null; titanite = null;
		 */
		xCalcComposition = new double[MAXCOMPONENTS];
		xCalcError = new double[MAXCOMPONENTS];
		xInitialComposition = new double[MAXCOMPONENTS];
		xFinalComposition = new double[MAXCOMPONENTS];
		xStep = new double[MAXCOMPONENTS];
		xWeight = new double[MAXCOMPONENTS];
		xBestFitComposition = new double[MAXCOMPONENTS];
		initialize(name);
		components = new Vector<TRow>();
	}

	public int act() {
		if (!actCurrent) {
			if (xCalcComposition[0] > 0.0)
				activities[0] = R * tk * Math.log(xCalcComposition[0]);
			else
				activities[0] = EXP0;
			actCurrent = true;
		}
		return TErrors.No_Err; // need it for derived classes
	}

	public int actdTdP() {
		if (!dTdPCurrent) {
			if (xCalcComposition[0] > 0.0) {
				activities[0] = R * tk * Math.log(xCalcComposition[0]);
				dAdT[0] = R * Math.log(xCalcComposition[0]);
			} else {
				activities[0] = EXP0;
				dAdT[0] = EXP0;
			}
			dAdP[0] = 0.0;
			dTdPCurrent = true;
		}
		return TErrors.No_Err;
	}

	public TRow addComponent(TQPanel p, String name, int r, int c) {
		return addComponent(p, name, r, c, 0, 1.00001);
	}
	public String getFormatString() {
		return "0.000";
	}

	public TRow addComponent(TQPanel p, String name, int r, int c, double min, double max) {
		TRow row = new TRow(getFormatString());
		row.setLab(new TTextLab(name));
		row.setInput(new TNumFld(min, max));
		row.setErrorField(new TTextLab(" "));
		row.setCalulatedValue(new TTextLab(" "));
		p.add(new TField(row.getLab(), row.getInput(), null, row.getCalculatedValue(), row.getErrorField()), r, c);
		components.addElement(row);
		return row;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public TRow addTextComponent(TQPanel p, String name, int r, int c) {
		TRow row = new TRow(getFormatString());
		row.setLab(new TTextLab(name));
		row.setTl(new TTextLab(" "));
		row.setErrorField(new TTextLab(" "));
		row.setCalulatedValue(new TTextLab(" "));
		p.add(new TField(row.getLab(), row.getTl(), row.getCalculatedValue(), row.getErrorField()), r, c);
		components.addElement(row);
		return row;
	}

	void calcdG(double ATk, double AP, int V1, int V2) {
		/*
		 * Have to pass V1 and V2 because of differences in reference pressures pyroxenes and olivine use P, oxides use
		 * P-1
		 */
		int U;

		for (U = V1; U <= V2; U++)
			sln[G][U] = sln[H][U] - ATk * sln[S][U] + AP * sln[V][U];
	}

	public void changeX(int I, double dX) {
		xCalcComposition[I] = xCalcComposition[I] + dX;
		if (xCalcComposition[I] < ZERO)
			xCalcComposition[I] = ZERO;
		else if (xCalcComposition[I] > MAXACT)
			xCalcComposition[I] = MAXACT;
		setComp(xCalcComposition);
	}

	public void clear() {
		for (int i = 0; i < components.size(); i++) {
			TRow r = (TRow) components.elementAt(i);
			r.clearAll();
		}
		return;
	}

	public void clearCalc() {
		int I;
		for (I = 0; I < components.size(); I++) {
			xCalcComposition[I] = 0.0;
			xCalcError[I] = 0.0;
			TRow r = (TRow) components.elementAt(I);
			r.clearCalc();
			r.clearErr();

		}
		return;
	}

	public void clearErrors() {
		int I;
		for (I = 0; I < numVar; I++) {
			xCalcError[I] = 0.0;
		}
		return;
	}

	public void copyToBestFit() {
		int I;

		for (I = 0; I < components.size(); I++)
			xBestFitComposition[I] = xCalcComposition[I];
		return;
	}

	public int deriv(double A[]) {
		if (!dXCurrent) {
			if (xCalcComposition[0] > 0.0)
				dX[0][0] = R * tk / xCalcComposition[0];
			else
				dX[0][0] = EXP0;
			dXCurrent = true;
		}
		return TErrors.No_Err;
	}

	double dotV(int Lo, int Hi, double U[], double g[]) {
		int I;
		double X;

		X = 0.0;
		for (I = Lo; I <= Hi; I++)
			X = X + U[I] * g[I];
		return X;
	}

	public boolean readComposition(BufferedReader pw) {
		boolean success = false;
		try {
			String pn = pw.readLine();
			if (phaseName.equals(pn)) {
				for (int i = 0; i < components.size(); i++) {
					TRow pC = (TRow) components.elementAt(i);
					TTextLab lab = pC.getLab();
					String expected = lab.getText();
					TNumFld numFld = pC.getInput();
					String s = pw.readLine();
					StringTokenizer st = new StringTokenizer(s, "=");
					String label = null;
					String value = null;
					if (st.hasMoreTokens())
						label = st.nextToken();
					if (st.hasMoreTokens())
						value = st.nextToken();
					if (label != null) {
						if (expected.equals(label)) {
							if (numFld == null) {
								if ((value != null) && (value.length() > 0)) {
									log.error("expected no value for:" + label + " instead got:" + value);
									success = false;
								}
							} else {
								if (value == null)
									numFld.setText("");
								else
									numFld.setText(value);
								numFld.setChanged(true);
								numFld.setHasError(false);
							}
						} else {
							log.error("read:" + label + " expecting " + expected);
							success = false;
						}
					}
				}
				success = true;
			} else
				log.error("read :" + pn + " expecting " + phaseName);
		} catch (IOException e) {
			log.error(e.toString());
		}
		return success;
	}

	public boolean writeComposition(PrintWriter pw) {
		boolean success = false;
		pw.println(this.phaseName);
		for (int i = 0; i < components.size(); i++) {
			TRow pC = (TRow) components.elementAt(i);
			TTextLab lab = pC.getLab();
			TNumFld numFld = pC.getInput();
			pw.print(lab.getText());
			pw.print("=");
			if (numFld != null) {
				String value = numFld.getText();
				if (value != null)
					pw.print(value);
			}
			pw.println();
		}
		success = true;
		return success;
	}

	public boolean getComposition(Vector<TVar> forNextVariables, Vector<TVar> variableList) {
		// Set the compositional parameters, return true if everything is okay
		// else return false.
		boolean Ok = true;
		phasePresent = false;
		phaseVariable = false;
		for (int i = 0; i < components.size(); i++) {
			xInitialComposition[i] = 0;
			xFinalComposition[i] = 0;
			xStep[i] = 0;
			xWeight[i] = 0;
			xBestFitComposition[i] = 0;
			xCalcError[i] = 0;
		}
		numVar = 0;
		for (int i = 0; i < components.size(); i++) {
			TRow pC = (TRow) components.elementAt(i);
			Ok = getValues(forNextVariables, variableList, pC, i);
			if (!Ok)
				break;
		}
		if (!Ok) {
			phasePresent = false;
			phaseVariable = false;
			clearCalc();
		} else
			setComp(xInitialComposition);
		return Ok;
	}

	protected String getCompStr() {
		return "TK = " + Double.toString(tk) + " P(Bars) = " + Double.toString(p) + " X = " + Double.toString(xCalcComposition[0]);
	}

	public double getP() {
		return p;
	}

	public double getTk() {
		return tk;
	}

	double getU0(int i, int c) {
		return u0Array[c][i];
	}

	public boolean getValues(Vector<TVar> forNextVariables, Vector<TVar> variableList, TRow pc, int i) {
		// Set the compositional parameters, return true if everything is okay
		// else return false.
		boolean Ok = !pc.hasError();
		if (!Ok)
			log.error("pc.hasError:" + pc.getLab());

		if (Ok && pc.isPresent()) {
			pc.parse();
			int index = i; // PC.index;
			xInitialComposition[index] = pc.getInitial(); // XI;
			xFinalComposition[index] = pc.getFinal(); // Xf;
			xStep[index] = pc.getStep(); // Xs;
			xWeight[index] = pc.getWeight(); // Xw;
			boolean Variable = pc.isVariable();
			boolean ForNextVar = pc.isForNext();
			phasePresent = true;
			if (xWeight[index] != 0.0)
				findBest = true;
			if (Variable) {
				phaseVariable = true;
				numVar++;
				if (variableList.size() >= MAXCOLS) {
					Quilf.showStatus("Too many variable compositions.\nThe maximum is " + Integer.toString(MAXCOLS), TErrors.TooManyVar_Err);
					Ok = false;
				} else {
					variableList.addElement(new TVar(this, index));
				}
			}
			if (ForNextVar) {
				if (forNextVariables.size() > MAXCOLS) {
					Quilf.showStatus("Too Many For/Next Variables.\nThe maximum is " + Integer.toString(MAXCOLS), TErrors.TooManyVar_Err);
					Ok = false;
				} else {
					forNextVariables.addElement(new TVar(this, index));
				}
			}
		}
		return Ok;
	}

	public double getX(int I) {
		return (xCalcComposition[I]);
	}

	void initialize(String name) {
		int i;
		int k;
		lowerComponent = 0;
		upperComponent = 0;
		phaseName = new String(name);
		lowerVariable = 0;
		upperVariable = -1;
		sln = null;
		dX = new double[MAXCOMPONENTS][MAXCOMPONENTS];
		u0P = new TTherm[MAXCOMPONENTS]; // standard state entries
		u0Array = new double[V + 1][MAXCOMPONENTS]; // points to standard state values
		componentName = new String[MAXCOMPONENTS];
		dAdT = new double[MAXCOMPONENTS];
		dAdP = new double[MAXCOMPONENTS];
		activities = new double[MAXCOMPONENTS];
		for (i = 0; i < MAXCOMPONENTS; i++) {
			for (k = G; k <= V; k++)
				u0Array[k][i] = 0.0;
			u0P[i] = null;
			componentName[i] = new String();
			activities[i] = EXP0;
			dAdT[i] = 0.0;
			dAdP[i] = 0.0;
		}
		xCalcComposition[0] = 0.0;
		actCurrent = false;
		dTdPCurrent = false;
		dXCurrent = false;
	}

	public double newX(double X, double dX, double MaxX) {
		X = X + dX;
		if (X < ZERO)
			X = ZERO;
		else if (X > MaxX)
			X = MaxX;
		return X;
	}

	public double newX1(double X, double dX, double Y) {
		X = newX(X, dX, ALMOSTONE);
		if (X + Y > ALMOSTONE)
			X = ALMOSTONE - Y;
		return X;
	}

	public double newX2(double X, double dX, double Y, double Z) {
		X = newX(X, dX, ALMOSTONE);
		if (X + Y + Z > ALMOSTONE)
			X = ALMOSTONE - Y - Z;
		return X;
	}

	public double newY(double Y, double dY, double X) {
		Y = newX(Y, dY, ONEHALF);
		if (X + Y > ALMOSTONE)
			Y = ALMOSTONE - X;
		return Y;
	}

	protected void randomFeMg() {
		// intentionally left blank
	}

	public boolean readFile(StreamTokenizer F) {
		int J;
		int I;
		int k;
		for (I = 0; I < MAXCOMPONENTS; I++) {
			for (k = G; k <= V; k++)
				u0Array[k][I] = 0.0;
			activities[I] = EXP0;
			dAdT[I] = 0.0;
			dAdP[I] = 0.0;
		}
		try {
			int type = (int) F.nextToken();
			lowerComponent = (int) F.nval; // F.readInt();
			type = F.nextToken();
			upperComponent = (int) F.nval; // F.readInt();
			type = F.nextToken();
			phaseName = new String(F.sval);
			for (J = lowerComponent; J <= upperComponent; J++) {
				F.nextToken();
				componentName[J] = new String(F.sval);
			}
			type = F.nextToken();
			lowerVariable = (int) F.nval; // readInt();
			type = F.nextToken();
			upperVariable = (int) F.nval; // readInt();
			if (upperVariable < lowerVariable)
				sln = null;
			else {
				sln = new double[V + 1][upperVariable + 1]; // (WVector *) calloc (1, sizeof (UArray));
				for (J = lowerVariable; J <= upperVariable; J++) {
					for (k = G; k <= V; k++) {
						type = F.nextToken();
						sln[k][J] = F.nval; // readDouble();
					}
				}
			}
		} catch (IOException e) {
			log.error("Read error: " + e);
			return false;
		}
		for (J = lowerComponent; J <= upperComponent; J++) { // :=PThermData(S.Get);
			u0P[J] = readu0P(F);
		}
		xCalcComposition[0] = 0.0;
		actCurrent = false;
		dTdPCurrent = false;
		dXCurrent = false;
		return true;
	}

	TTherm readu0P(StreamTokenizer f) {
		int dataType = TTherm.nullpointer;
		TTherm pThermData;
		THelg pHelgesonData;
		THaas pHaasData;
		THelgQtz pHelgesonQtz;
		TDimitri pDimitriData;
		TKarooiteData pKarooiteData;
		try {
			f.nextToken();
			dataType = (int) f.nval; // readChar();
		} catch (IOException e) {
			log.error("Read error: " + e);
		}
		if (dataType == TTherm.thermdata) {
			pThermData = new TTherm();
			pThermData.readFile(f);
			pThermData.Next = readu0P(f);
			return /* (TThermData *) */pThermData;
		} else if (dataType == TTherm.haasdata) {
			pHaasData = new THaas();
			pHaasData.readFile(f);
			pHaasData.Next = readu0P(f);
			return /* (TThermData *) */pHaasData;
		} else if (dataType == TTherm.helgesondata) {
			pHelgesonData = new THelg();
			pHelgesonData.readFile(f);
			pHelgesonData.Next = readu0P(f);
			return /* (TThermData *) */pHelgesonData;
		} else if (dataType == TTherm.helgesonqtz) {
			pHelgesonQtz = new THelgQtz();
			pHelgesonQtz.readFile(f);
			pHelgesonQtz.Next = readu0P(f);
			return /* (TThermData *) */pHelgesonQtz;
		} else if (dataType == TTherm.dimitridata) {
			pDimitriData = new TDimitri();
			pDimitriData.readFile(f);
			pDimitriData.Next = readu0P(f);
			return /* (TThermData *) */pDimitriData;
		} else if (dataType == TTherm.karooitedata) {
			pKarooiteData = new TKarooiteData();
			pKarooiteData.readFile(f);
			pKarooiteData.Next = readu0P(f);
			return /* (TThermData *) */pKarooiteData;
		}
		return null;
	}

	public void resetInitial() {
		int i;

		for (i = 0; i < components.size(); i++) {
			xCalcComposition[i] = xInitialComposition[i];
			xBestFitComposition[i] = 0.0;
		}
		setComp(xInitialComposition);
		setCalcValues();
	}

	public void setBestFit() {
		int i;

		for (i = 0; i < components.size(); i++) {
			xCalcComposition[i] = xBestFitComposition[i];
		}
		setComp(xCalcComposition);
	}

	public void setCalcValues() {
		for (int i = 0; i < components.size(); i++) {
			TRow pc = (TRow) components.elementAt(i);
			if (phasePresent) {
				if (xCalcComposition[i] == 0.0)
					pc.clearCalc();
				else
					pc.setCalc(xCalcComposition[i]);
			} else
				pc.clearAll();
		}
	}

	void setComp(double AX[]) {
		actCurrent = false;
		dTdPCurrent = false;
		dXCurrent = false;
		for (int i = lowerComponent; i <= upperComponent; i++)
			xCalcComposition[i] = AX[i];
	}

	public void setErrI(int I, double X) {
		TRow pc = (TRow) components.elementAt(I);
		xCalcError[I] = X;
		pc.setErr(X);
	}

	public void setErrValues() {
		for (int i = 0; i < components.size(); i++) {
			TRow pc = (TRow) components.elementAt(i);
			if (phasePresent) {
//				if (xCalcError[i] == 0.0)
				if (!pc.isVariable())
					pc.clearErr();
				else
					pc.setErr(xCalcError[i]);
			} else
				pc.clearAll();
		}
	}

	void setParam(double ATk, double AP, double AX[]) {
		tk = ATk;
		p = AP;
		setComp(AX);
	}

	public int setRows(String name, TQPanel p, int row) {
		return row;
	}

	public int setRows(TQPanel p, int row) {
		return row;
	}

	public void setTP(double aTk, double aP) {
		actCurrent = false;
		dTdPCurrent = false;
		dXCurrent = false;
		tk = aTk;
		p = aP;
	}

	void setU0(int i, int c, double x) {
		u0Array[c][i] = x;
	}

	public void setX(int I, double X) {
		xCalcComposition[I] = X;
		setComp(xCalcComposition);
	}

	public double sumSq() {
		int ix;
		double sum, delta;

		sum = 0.0;
		for (ix = 0; ix < numVar; ix++) {
			if (xWeight[ix] > 0.0) {
				delta = xInitialComposition[ix] - xCalcComposition[ix];
				if (Math.abs(delta) > xWeight[ix])
					sum = sum + xWeight[ix] * delta * delta;
			}
		}
		return sum;
	}

	public String toString() {
		if (phaseName == null)
			return "TPhase";
		else
			return phaseName;
	}

	public void writeFile(DataOutputStream F) {
		int I;
		int J;
		int k;

		try {
			F.writeInt(lowerComponent);
			F.writeInt(upperComponent);
			// F.writeInt(Pn.length());
			for (I = 0; I < phaseName.length(); I++)
				F.writeChar(phaseName.charAt(I));

			for (I = lowerComponent; I <= upperComponent; I++) {
				F.writeInt(componentName[I].length());
				for (J = 0; J < componentName[I].length(); J++)
					F.writeChar(componentName[I].charAt(J));
			}
			F.writeInt(lowerVariable);
			F.writeInt(upperVariable);

			if (upperVariable >= lowerVariable) {
				for (J = lowerVariable; J <= upperVariable; J++) {
					for (k = G; k <= V; k++) {
						F.writeDouble(sln[k][J]);
					}
				}
			}
		} catch (IOException e) {
			log.error("Write error: " + e);
		}

		for (I = lowerComponent; I <= upperComponent; I++)
			writeu0P(F, u0P[I]);
	}

	void writeu0P(DataOutputStream F, TTherm u0P) {
		if (u0P == null) {
			try {
				F.writeChar(TTherm.nullpointer);
			} catch (IOException e) {
				log.error("Write Error " + e);
			}
		} else {
			u0P.writeType(F);
			u0P.writeFile(F);
			TTherm p = u0P.Next;
			writeu0P(F, p);
		}
	}

	public int getNumVar() {
		return numVar;
	}

	public void setNumVar(int numVar) {
		this.numVar = numVar;
	}

	public boolean isPhasePresent() {
		return phasePresent;
	}

	public void setPhasePresent(boolean phasePresent) {
		this.phasePresent = phasePresent;
	}

	public boolean isPhaseVariable() {
		return phaseVariable;
	}

	public void setPhaseVariable(boolean phaseVariable) {
		this.phaseVariable = phaseVariable;
	}

	public boolean isFindBest() {
		return findBest;
	}

	public void setFindBest(boolean findBest) {
		this.findBest = findBest;
	}
}