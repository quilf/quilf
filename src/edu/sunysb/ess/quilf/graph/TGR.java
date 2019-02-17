package edu.sunysb.ess.quilf.graph;

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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import edu.sunysb.ess.quilf.swing.DataRow;
import edu.sunysb.ess.quilf.swing.print.PrintPreviewS;

public class TGR extends JFrame implements ActionListener {
	private static final String CLEAR = "Clear";
	private static final String PRINT = "Print";

	private static final String EXIT = "Exit";
	private static final String SETTINGS = "Settings..";
	private TGRXYWindow tgrXYWindow = null;
	private Axis xAxis;
	private Axis yAxis;
	private String graphTitle;

	public TGR(String title, Axis xAxis, Axis yAxis) {
		super();
		this.graphTitle = title;
		this.setTitle(title);
		this.setJMenuBar(getMenu());
		tgrXYWindow = new TGRXYWindow(graphTitle, xAxis, yAxis);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		add(tgrXYWindow);
		pack();
	}

	public void redraw() {
		tgrXYWindow.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		// public boolean action(Event e, Object arg) {
		String arg = e.getActionCommand();
		if (EXIT.equals(arg)) {
			setVisible(false);
			dispose();
		} else if (CLEAR.equals(arg)) {
			clear();
		} else if (PRINT.equals(arg)) {
			print();
		} else if (SETTINGS.equals(arg)) {
			String value = TAxisOptionsDlg.showDialog(this, xAxis, yAxis, graphTitle);
			if (value != null) {
				setGraphTitle(value);
				redraw();
			}
		}
	}
	private void print() {
		JFrame frame2 = new JFrame();
		Container con2 = frame2.getContentPane();
		PrintPreviewS pp = new PrintPreviewS(tgrXYWindow);
		con2.add(pp, BorderLayout.CENTER);
		frame2.pack();
		frame2.setVisible(true);
		frame2.toFront();
	}

	private void addMenuItem(JMenu menu, String name, int keyEvent, String accessibleDescription) {
		JMenuItem menuItem = new JMenuItem(name, keyEvent);
		menuItem.getAccessibleContext().setAccessibleDescription(accessibleDescription);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.ALT_MASK));
		menu.add(menuItem);
		menuItem.addActionListener(this);
	}

	public void addObject(TGraphicsObject obj) {
		tgrXYWindow.addObject(obj);
	}

	public void clear() {
		tgrXYWindow.clear();
	}

	public void setDefaults(Axis axis) {
		axis.setLab(DataRow.getName(axis.getIndex()));
		axis.setN(0);
		axis.setM(0);
		switch (axis.getIndex()) {
		case DataRow.TC: // tk
			axis.setMin(400);
			axis.setMax(1500);
			axis.setTicint(100);
			break;
		case DataRow.PBAR: // p
			axis.setMin(0);
			axis.setMax(20000);
			axis.setTicint(5000);
			break;
		case DataRow.FO2: // fo2
			axis.setMin(-20);
			axis.setMax(-10);
			axis.setTicint(1);
			break;
		case DataRow.DFMQ: // dfmq
			axis.setMin(-6);
			axis.setMax(5);
			axis.setTicint(1);
			break;
		case DataRow.NTI: // nti
			axis.setMin(0);
			axis.setMax(2);
			axis.setTicint(0.5);
			break;
		case DataRow.NMG: // nmg
			axis.setMin(0);
			axis.setMax(2);
			axis.setTicint(0.5);
			break;
		case DataRow.NMN: // nmn
			axis.setMin(0);
			axis.setMax(2);
			axis.setTicint(0.5);
			break;
		case DataRow.XIL: // xil
			axis.setMin(0);
			axis.setMax(1);
			axis.setTicint(0.2);
			break;
		case DataRow.XHEM: // xhem
			axis.setMin(0);
			axis.setMax(0.2);
			axis.setTicint(0.05);
			break;
		case DataRow.XGK: // xgk
		case DataRow.XPY: // xpy
			axis.setMin(0);
			axis.setMax(1);
			axis.setTicint(0.2);
			break;

		case DataRow.XFO: // xfo (ol)
		case DataRow.XENAUG: // xen (augite)
		case DataRow.XENPIG: // xen (pig)
		case DataRow.XENOPX: // xen (opx)
			axis.setMin(0);
			axis.setMax(20000);
			axis.setTicint(5000);
			break;
		case DataRow.XLA: // xla (ol)
		case DataRow.XWOAUG: // xwo (augite)
		case DataRow.XWOPIG: // xwo (pig)
		case DataRow.XWOOPX: // xwo (opx)
			axis.setMin(0);
			axis.setMax(0.5);
			axis.setTicint(0.1);
			break;
		case DataRow.XFA: // xfa (ol)
		case DataRow.XFSAUG: // xfs (augite)
		case DataRow.XFSPIG: // xfs (pig)
		case DataRow.XFSOPX: // xfs (opx)
			axis.setMin(0);
			axis.setMax(1);
			axis.setTicint(0.2);
			break;
		case DataRow.XFEOL: // xfe (ol)
		case DataRow.XFEAUG: // xfe (augite)
		case DataRow.XFEPIG: // xfe (pig)
		case DataRow.XFEOPX: // xfe (opx)
			axis.setMin(0);
			axis.setMax(1);
			axis.setTicint(0.2);
			break;
		case DataRow.ASIO2: // asio2
		case DataRow.AFE: // afe
		case DataRow.ATIO2: // atio2
		case DataRow.ACATISIO5: // catisio5
		case DataRow.AMGTI2O5: // mgti2o5
			axis.setMin(0);
			axis.setMax(1.0);
			axis.setTicint(0.2);
			break;
		default:
			axis.setMin(0);
			axis.setMax(1);
			axis.setTicint(0.5);
			break;
		}
		axis.setLabint(axis.getTicint());

		tgrXYWindow.getformat(axis);
	}

	private JMenuBar getMenu() {
		JMenuBar menuBar;
		JMenu menu;
		menuBar = new JMenuBar();
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		menu.getAccessibleContext().setAccessibleDescription("Options");
		menuBar.add(menu);

		addMenuItem(menu, SETTINGS, KeyEvent.VK_S, "Graph Settings");
		addMenuItem(menu, PRINT, KeyEvent.VK_P, "Print the Screen");
		addMenuItem(menu, CLEAR, KeyEvent.VK_C, "Clear the Screen");
		menu.addSeparator();
		addMenuItem(menu, EXIT, KeyEvent.VK_X, "Exit");
		return menuBar;
	}

	public Axis getXAxis() {
		return xAxis;
	}

	public void setXAxis(Axis axis) {
		xAxis = axis;
	}

	public Axis getYAxis() {
		return yAxis;
	}

	public void setYAxis(Axis axis) {
		yAxis = axis;
	}

	public String getGraphTitle() {
		return graphTitle;
	}

	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
		tgrXYWindow.setGraphTitle(graphTitle);
	}

}
