package edu.sunysb.ess.quilf.swing;
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
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import edu.sunysb.ess.quilf.model.TErrors;
import edu.sunysb.ess.quilf.model.TPhase;
import edu.sunysb.ess.quilf.model.TRCoef;
import edu.sunysb.ess.quilf.model.TRxn;
import edu.sunysb.ess.quilf.model.TRxns;
import edu.sunysb.ess.quilf.model.TSln;

public class TestTable extends AbstractTableModel {
	private String[] columnNames = { "Rctn", "dG", "lnK", "DeltaG%", "Tol%", "Within" };
	private Object[][] data = null;
	private int nRows;

	private boolean TestEqn(TPhase[] slns, TRxn Ri, double Tolerance) {
		double temp;

		double dG = 0.0;
		double lnK = 0.0;
		double deltaG = 0;
		for (int J = 0; J < Ri.RN; J++) {
			TRCoef WITH = Ri.RC[J];
			TPhase phase = WITH.Ph; // phase.u0Array[TSln.G]
			temp = WITH.X * phase.u0Array[TPhase.G][WITH.C];
			dG = dG + temp;
			temp = WITH.X * phase.activities[WITH.C];
			lnK = lnK + temp;
		}
		if (Math.abs(dG) > TPhase.ASSUMEDZERO)
			deltaG = 100.0 * (dG + lnK) / dG;
		else
			deltaG = dG + lnK;
		Ri.setDeltaG(deltaG);
		Ri.setDG(dG);
		Ri.setLnK(lnK);
		return (Math.abs(deltaG) < Tolerance);
	}

	final static double Tolerance = 1.0E-01;

	public void update(TRxns reactions, TPhase[] slns) {
		boolean Pass = false;
		nRows = 0;
		Vector<TRxn> rctnList = reactions.RctnList;
		Iterator<TRxn> iter = rctnList.iterator();
		while (iter.hasNext()) {
			TRxn pr = iter.next();
			if (pr.Selected)
				nRows++;
		}
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				data[i][j] = "";
		int Error = TErrors.No_Err;
		double Tk = slns[TSln.Rctns].getTk();
		double P = slns[TSln.Rctns].getP();
		for (int Ph = TSln.Ol; Ph <= TSln.Oxygen; Ph++) {
			double rt = TPhase.R * slns[Ph].tk;
			TPhase phase = (TPhase) slns[Ph];
			int error = 0;
			if (phase.isPhasePresent())
				error = phase.act();
			if (error != TErrors.No_Err) {
				String s = phase.getPhaseName();
				s = "Error in Activities of" + phase.getPhaseName();
				// WinMessageBox (HWND_DESKTOP, 0, (PCSZ) s, (PCSZ) "Error", 0, MB_OK);
				break;
			}
		}
		// StandardStates(Tk, P, Error);
		// dU0(Tk, P, Error);
		DecimalFormat nf = new DecimalFormat("0.######E00");
		DecimalFormat df = new DecimalFormat("0.######");
		int row = 0;
		for (int Ph = TSln.Ol; Ph <= TSln.Oxygen; Ph++) {
			slns[Ph].setTP(Tk, P);
			Error = slns[Ph].act();
			if (Error!=TErrors.No_Err) break;
		}
		if (Error != TErrors.No_Err) {
			// MessageBox (HWND_DESKTOP, "Error in calculating the derivatives", ProgName, MB_ICONEXCLAMATION);
		} else {
			// sprintf(s, "TK = %g, P(Bar) = %g", Tk, P);
			// AddStr(s);
			rctnList = reactions.RctnList;
			iter = rctnList.iterator();
			while (iter.hasNext()) {
				TRxn pr = iter.next();
				if (pr.Selected) {
					Pass = TestEqn(slns, pr, Tolerance);
					data[row][0] = pr.NR;
					data[row][1] = nf.format(pr.getDG());
					data[row][2] = nf.format(pr.getLnK());
					data[row][3] = nf.format(pr.getDeltaG());
					data[row][4] = nf.format(Tolerance);
					if (Pass)
						data[row][5] = "";
					else
						data[row][5] = "?";
					row++;
				}
			}
		}
		this.fireTableDataChanged();
	}

	public TestTable(TRxns reactions, TSln solution) {

		nRows = reactions.NumRctns;
		int nCols = columnNames.length;
		data = new String[reactions.RctnList.size()][nCols];
		DecimalFormat nf = new DecimalFormat("0.######E00");
		nf.setMaximumFractionDigits(5);
		nf.setMinimumFractionDigits(5);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(1);
		update(reactions, solution.Slns);
	}

	/*
	 * public void update(TPhase[] slns) { int row = 0; String sg, sa; double a, rt; int error; String s = "";
	 * DecimalFormat nf = new DecimalFormat("0.######E00"); DecimalFormat df = new DecimalFormat("0.######"); //
	 * ClearText (); for (int Ph = TSln.Ol; Ph <= TSln.Oxygen; Ph++) { rt = TPhase.R * slns[Ph].tk; TPhase phase =
	 * (TPhase) slns[Ph]; s = phase.getPhaseName(); error = 0; if (phase.isPhasePresent()) phase.act(error); if (error !=
	 * 0) { s = "Error in Activities of" + phase.getPhaseName(); // WinMessageBox (HWND_DESKTOP, 0, (PCSZ) s, (PCSZ)
	 * "Error", 0, MB_OK); } data[row][0] = s; data[row][1] = ""; data[row][2] = ""; row++;
	 * 
	 * for (int c = phase.lowerComponent; c <= phase.upperComponent; c++) { sg = nf.format(phase.u0Array[TSln.G][c]); if
	 * ((error == 0) && (phase.isPhasePresent())) { a = phase.activities[c] / rt; if (a < ExpZero) a = 0.0; else a =
	 * Math.exp(a); sa = df.format(a); } else sa = ""; data[row][0] = slns[Ph].componentName[c]; data[row][1] = sg;
	 * data[row][2] = sa; row++; } } this.fireTableDataChanged(); }
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return nRows; // data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
}
