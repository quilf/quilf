package edu.sunysb.ess.quilf.swing;
/*
part of QUIlF

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

import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import edu.sunysb.ess.quilf.model.TPhase;
import edu.sunysb.ess.quilf.model.TSln;

public class ActivityTable extends AbstractTableModel {
	private String[] columnNames = { "", "Std.St.(Norm.)", "Activity" };
	private Object[][] data = null;

	public ActivityTable(TSln solution) {

		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		TPhase[] phases = solution.Slns;
		int nRows = 0;
		for (int ph = TSln.Ol; ph <= TSln.Oxygen; ph++) {
			TPhase phase = phases[ph];
			nRows++;
			nRows += (phase.upperComponent - phase.lowerComponent + 1);
		}
		int nCols = columnNames.length;
		data = new String[nRows][nCols];
		DecimalFormat nf = new DecimalFormat("0.######E00");
		nf.setMaximumFractionDigits(5);
		nf.setMinimumFractionDigits(5);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(1);
		update(solution.Slns);
	}
	private static final double ExpZero = -1.0E+03;

	public void update(TPhase[] slns) {
		int row = 0;
		String sg, sa;
		double a, rt;
		int error;
		String s = "";
		DecimalFormat nf = new DecimalFormat("0.######E00");
		DecimalFormat df = new DecimalFormat("0.######");
		//	  ClearText ();
		for (int Ph = TSln.Ol; Ph <= TSln.Oxygen; Ph++) {
			rt = TPhase.R * slns[Ph].tk;
			TPhase phase = (TPhase) slns[Ph];
			s = phase.getPhaseName();
			error = 0;
			if (phase.isPhasePresent())
				error = phase.act();
			if (error != 0) {
				s = "Error in Activities of" + phase.getPhaseName();
				//WinMessageBox (HWND_DESKTOP, 0, (PCSZ) s, (PCSZ) "Error", 0, MB_OK);
			}
			data[row][0] = s;
			data[row][1] = "";
			data[row][2] = "";
			row++;

			for (int c = phase.lowerComponent; c <= phase.upperComponent; c++) {
				sg = nf.format(phase.u0Array[TSln.G][c]);
				if ((error == 0) && (phase.isPhasePresent())) {
					a = phase.activities[c] / rt;
					if (a < ExpZero)
						a = 0.0;
					else
						a = Math.exp(a);
					sa = df.format(a);
				} else
					sa = "";
				data[row][0] = slns[Ph].componentName[c];
				data[row][1] = sg;
				data[row][2] = sa;
				row++;
			}
		}
		this.fireTableDataChanged();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
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
	 * Don't need to implement this method unless your table's
	 * editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's
	 * data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
}
