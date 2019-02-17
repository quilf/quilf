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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DataWindow extends JFrame implements ActionListener {
	private static final String CLEAR = "Clear";
	private static final String EXIT = "Exit";
	private static final String EXPORT = "Export to Excel";
	DataTableModel dataTableModel = null;
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataWindow.class);

	public DataWindow() {
		this.setTitle("QUIlF - iteration values");
		JPanel panel = new JPanel(new GridLayout(1, 1));
		dataTableModel = new DataTableModel();
		JTable table = new JTable(dataTableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);
		add(panel);
		this.setJMenuBar(getMenu());
	}

	public void actionPerformed(ActionEvent e) {
		String arg = e.getActionCommand();
		if (EXIT.equals(arg)) {
			setVisible(false);
			dispose();
		} else if (CLEAR.equals(arg)) {
			dataTableModel.clear();
		} else if (EXPORT.equals(arg)) {
			export();
		}
	}

	private void export() {
		JFileChooser fc = new JFileChooser();
		QuilfFileFilter filter = new QuilfFileFilter("xls", "Excel Files");
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			export(file);
		}

	}

	private void export(File file) {
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("QUIlF", 0);
			int nCols = dataTableModel.getColumnCount();
			int row = 0;
			for (int i = 0; i < nCols; i++)
				sheet.addCell(new Label(i, row,  dataTableModel.getColumnName(i)));
			row++;
			int nRows = dataTableModel.getRowCount();
			for (int i = 0; i < nRows; i++) {
				for (int col = 0; col < nCols; col++) {
					Double d = (Double) dataTableModel.getValueAt(row, col);
					if (d != null)
						sheet.addCell(new Number(col, row, d.doubleValue()));
				}
				row++;
			}
			workbook.write();
			workbook.close();
		} catch (RowsExceededException e) {
			log.error(e.toString());
		} catch (WriteException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}

	}

	public void addDataRow(DataRow dataRow) {
		dataTableModel.addRow(dataRow);
	}

	private void addMenuItem(JMenu menu, String name, int keyEvent, String accessibleDescription) {
		JMenuItem menuItem = new JMenuItem(name, keyEvent);
		menuItem.getAccessibleContext().setAccessibleDescription(accessibleDescription);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.ALT_MASK));
		menu.add(menuItem);
		menuItem.addActionListener(this);
	}

	public void clear() {
		dataTableModel.clear();
	}

	private JMenuBar getMenu() {
		JMenuBar menuBar;
		JMenu menu;
		menuBar = new JMenuBar();
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		menu.getAccessibleContext().setAccessibleDescription("Options");
		menuBar.add(menu);

		addMenuItem(menu, CLEAR, KeyEvent.VK_C, "Clear the Screen");
		addMenuItem(menu, EXPORT, KeyEvent.VK_E, "Export To Excel");
		menu.addSeparator();
		addMenuItem(menu, EXIT, KeyEvent.VK_X, "Exit");
		return menuBar;
	}
}
