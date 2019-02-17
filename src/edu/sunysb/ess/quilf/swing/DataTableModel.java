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

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel {
	private ArrayList<DataRow> data;// = ...//same as before...

	public DataTableModel() {
		super();
	}

	public void addRow(DataRow dataRow) {
		if (data == null)
			data = new ArrayList<DataRow>();
		data.add(dataRow);
		this.fireTableDataChanged();
		// int firstRow = data.size()-1;
		// int lastRow = firstRow;
		// this.fireTableRowsInserted(firstRow, lastRow);
	}

	public void clear() {
		if (data != null)
			data.clear();
		fireTableDataChanged();
	}

	public Class getColumnClass(int c) {
		return Double.class; // return getValueAt(0, c).getClass();
	}

	public int getColumnCount() {
		return DataRow.getNames().length;
	}

	public String getColumnName(int col) {
		return DataRow.getName(col);
	}

	public int getRowCount() {
		if (data != null)
			return data.size();
		return 0;
	}

	public Object getValueAt(int row, int column) {
		if (data != null) {
			if (row < data.size()) {
				DataRow dr = data.get(row);
				return dr.getValue(column);
			}
		}
		return null;
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		if (data == null)
			data = new ArrayList<DataRow>();
		while (row > data.size())
			data.add(new DataRow());
		DataRow dr = data.get(row);
		dr.setValue(col, (Double) value);
		this.fireTableDataChanged();
		// fireTableCellUpdated(row, col);
	}

}
