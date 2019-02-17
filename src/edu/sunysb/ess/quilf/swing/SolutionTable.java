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
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import edu.sunysb.ess.quilf.model.TPhase;
import edu.sunysb.ess.quilf.model.TSln;

public class SolutionTable extends AbstractTableModel {
	private String[] columnNames = { "", "G", "H", "S", "V" };
	private Object[][] data = null;

	public SolutionTable(TSln solution) {

		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		TPhase[] phases = solution.Slns;
		int nRows = 0;
		for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
			TPhase phase = phases[ph];
			nRows++;
			if (phase.upperVariable >= phase.lowerVariable)
				nRows += (phase.upperVariable - phase.lowerVariable + 1);
		}
		int nCols = columnNames.length;
		data = new String[nRows][nCols];
		update(solution);
	}

	public void update(TSln solution) {
		TPhase[] phases = solution.Slns;
		DecimalFormat nf = new DecimalFormat("0.########E00");
		nf.setMaximumFractionDigits(5);
		nf.setMinimumFractionDigits(5);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(1);
		int nRows = 0;
		for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
			TPhase phase = phases[ph];
			data[nRows][0] = phase.getPhaseName() + "Tk=" + phase.tk + ", P=" + phase.p;
			data[nRows][1] = "";
			data[nRows][2] = "";
			data[nRows][3] = "";
			data[nRows][4] = "";
			nRows++;
			if (phase.upperVariable >= phase.lowerVariable) {
				for (int c = phase.lowerVariable; c <= phase.upperVariable; c++) {
					data[nRows][0] = Integer.toString(c);
					data[nRows][1] = nf.format(phase.sln[TSln.G][c]);
					data[nRows][2] = nf.format(phase.sln[TSln.H][c]);
					data[nRows][3] = nf.format(phase.sln[TSln.S][c]);
					data[nRows][4] = nf.format(phase.sln[TSln.V][c]);
					nRows++;
				}
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
