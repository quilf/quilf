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
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import edu.sunysb.ess.quilf.graph.Axis;

public class TSelectAxesDlg extends JDialog implements ActionListener {
	private final static String CANCEL = "Cancel";
	private final static String OK = "Ok";
	private static TSelectAxesDlg dialog;
	private static String value = null;

	private Axis xAxis;
	private JComboBox xCB;
	private Axis yAxis;
	private JComboBox yCB;

	public static String showDialog(Component frameComp,  String[] axisStrings, Axis xAxis, Axis yAxis) {
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new TSelectAxesDlg(frame, axisStrings, xAxis, yAxis);
		dialog.pack();
		dialog.setVisible(true);
		return value;
	}

	private TSelectAxesDlg(Frame owner, String[] axisStrings, Axis xAxis, Axis yAxis) {
		super(owner,"Select Axis",true);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.setTitle("XY");
		// SpinnerListModel axisModel = new SpinnerListModel(axisStrings);
		JPanel panel = new JPanel(new SpringLayout());
		panel.add(new JLabel("X Axis"));
		panel.add(new JLabel("Y Axis"));
		xCB = new JComboBox(axisStrings);
		xCB.setSelectedIndex(xAxis.getIndex());
		panel.add(xCB);
		yCB = new JComboBox(axisStrings);
		yCB.setSelectedIndex(yAxis.getIndex());
		panel.add(yCB);

		JButton okButton = new JButton(OK);
		JButton cancelButton = new JButton("Cancel");
		panel.add(okButton);
		panel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		SpringUtilities.makeCompactGrid(panel, 3, 2, // rows, cols
				5, 5, // initialX, initialY
				5, 5);// xPad, yPad
		add(panel);
	}

	public void actionPerformed(ActionEvent e) {
		String arg = e.getActionCommand();
		if (OK.equals(arg)) {
			xAxis.setIndex(xCB.getSelectedIndex());
			yAxis.setIndex(yCB.getSelectedIndex());
			value = arg;
			this.setVisible(false);
		}
		if (CANCEL.equals(arg)) {
			this.setVisible(false);
		}
	}
}
