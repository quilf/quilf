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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import edu.sunysb.ess.quilf.swing.SpringUtilities;
import edu.sunysb.ess.quilf.utilities.Utilities;

public class TAxisOptionsDlg extends JDialog implements ActionListener {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TAxisOptionsDlg.class);
	private static final String CANCEL = "Cancel";
	private static final String DEFAULTS = "Defaults";
	private static final String OK = "Ok";
	private JTextField xLabel;
	private JTextField xMin;
	private JTextField xWidth;
	private JTextField xMax;
	private JTextField xDecimals;
	private JTextField xTicInterval;
	private JTextField yLabel;
	private JTextField yMin;
	private JTextField yWidth;
	private JTextField yMax;
	private JTextField yDecimals;
	private JTextField yTicInterval;
	private Axis xAxis;
	private Axis yAxis;
	private static TAxisOptionsDlg dialog;
	private static String value = null;
	private TGR tgr;
	private JTextField titleField;

	public static String showDialog(TGR frameComp, Axis xAxis, Axis yAxis, String title) {
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new TAxisOptionsDlg(frame, frameComp, xAxis, yAxis, title);
		dialog.pack();
		dialog.setVisible(true);
		return value;
	}

	public TAxisOptionsDlg(Frame owner, TGR tgr, Axis xAxis, Axis yAxis, String title) {
		super(owner, true);
		this.tgr = tgr;
		this.setTitle("Select Axes");
		this.xAxis = xAxis;
		this.yAxis = yAxis;

		JPanel xAxisPanel = new JPanel();
		SpringLayout xLayout = new SpringLayout();
		xAxisPanel.setLayout(xLayout);
		xAxisPanel.setBorder(BorderFactory.createTitledBorder("X Axis"));
		xAxisPanel.add(new JLabel("Label:"));
		xLabel = new JTextField(xAxis.getLab());
		xAxisPanel.add(xLabel);
		xAxisPanel.add(new JLabel("Format"));
		xAxisPanel.add(new JLabel(""));

		xAxisPanel.add(new JLabel("Min:"));
		xMin = new JTextField(Double.toString(xAxis.getMin()));
		xAxisPanel.add(xMin);
		xAxisPanel.add(new JLabel("Width:"));
		xWidth = new JTextField(Integer.toString(xAxis.getN()));
		xAxisPanel.add(xWidth);

		xAxisPanel.add(new JLabel("Max:"));
		xMax = new JTextField(Double.toString(xAxis.getMax()));
		xAxisPanel.add(xMax);
		xAxisPanel.add(new JLabel("Decimals:"));
		xDecimals = new JTextField(Integer.toString(xAxis.getM()));
		xAxisPanel.add(xDecimals);

		xAxisPanel.add(new JLabel("Tic Interval:"));
		xTicInterval = new JTextField(Double.toString(xAxis.getTicint()));
		xAxisPanel.add(xTicInterval);
		xAxisPanel.add(new JLabel(""));// brain dead
		xAxisPanel.add(new JLabel(""));

		SpringUtilities.makeCompactGrid(xAxisPanel, 4, 4, // rows, cols
				5, 5, // initialX, initialY
				5, 5);// xPad, yPad
		JPanel yAxisPanel = new JPanel(new SpringLayout());
		yAxisPanel.setBorder(BorderFactory.createTitledBorder("Y Axis"));
		yAxisPanel.add(new JLabel("Label:"));
		yLabel = new JTextField(yAxis.getLab());
		yAxisPanel.add(yLabel);
		yAxisPanel.add(new JLabel("Format"));
		yAxisPanel.add(new JLabel(""));

		yAxisPanel.add(new JLabel("Min:"));
		yMin = new JTextField(Double.toString(yAxis.getMin()));
		yAxisPanel.add(yMin);
		yAxisPanel.add(new JLabel("Width:"));
		yWidth = new JTextField(Integer.toString(yAxis.getN()));
		yAxisPanel.add(yWidth);

		yAxisPanel.add(new JLabel("Max:"));
		yMax = new JTextField(Double.toString(yAxis.getMax()));
		yAxisPanel.add(yMax);
		yAxisPanel.add(new JLabel("Decimals:"));
		yDecimals = new JTextField(Integer.toString(yAxis.getM()));
		yAxisPanel.add(yDecimals);

		yAxisPanel.add(new JLabel("Tic Interval:"));
		yTicInterval = new JTextField(Double.toString(yAxis.getTicint()));
		yAxisPanel.add(yTicInterval);
		yAxisPanel.add(new JLabel(""));// brain dead
		yAxisPanel.add(new JLabel(""));
		SpringUtilities.makeCompactGrid(yAxisPanel, 4, 4, // rows, cols
				5, 5, // initialX, initialY
				5, 5);// xPad, yPad
		JPanel titlePanel = new JPanel(new FlowLayout());
		titlePanel.add(new JLabel("title:"));
		titleField = new JTextField(title, 20);
		titlePanel.add(titleField);
		JPanel center = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		center.setLayout(gridbag);

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;

		gridbag.setConstraints(titlePanel, c);
		center.add(titlePanel);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 4;
		c.gridwidth = 1;
		gridbag.setConstraints(xAxisPanel, c);
		center.add(xAxisPanel);

		c.gridx = 0;
		c.gridy = 6;
		c.gridheight = 4;
		c.gridwidth = 1;
		gridbag.setConstraints(yAxisPanel, c);
		center.add(yAxisPanel);

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(OK);
		JButton cancelButton = new JButton(CANCEL);
		JButton defaultButton = new JButton(DEFAULTS);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(defaultButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		defaultButton.addActionListener(this);
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, buttonPanel);
		add(BorderLayout.CENTER, center);
	}
	  public Dimension getPreferredSize() {
		  return new Dimension(300,400);
	  }
	public void actionPerformed(ActionEvent e) {
		// public boolean action(Event e, Object arg) {
		String arg = e.getActionCommand();
		if (CANCEL.equals(arg)) {
			setVisible(false);
		} else if (DEFAULTS.equals(arg)) {
			defaults();
		} else if (OK.equals(arg)) {
			log.debug(xAxis.toString());
			xAxis.setLab(xLabel.getText());
			xAxis.setMin(Utilities.getDouble(xMin.getText()));
			xAxis.setMax(Utilities.getDouble(xMax.getText()));
			xAxis.setN(Utilities.getInt(xWidth.getText()));
			xAxis.setM(Utilities.getInt(xDecimals.getText()));
			xAxis.setTicint(Utilities.getDouble(xTicInterval.getText()));
			log.debug(xAxis.toString());
			yAxis.setLab(yLabel.getText());
			yAxis.setMin(Utilities.getDouble(yMin.getText()));
			yAxis.setMax(Utilities.getDouble(yMax.getText()));
			yAxis.setN(Utilities.getInt(yWidth.getText()));
			yAxis.setM(Utilities.getInt(yDecimals.getText()));
			yAxis.setTicint(Utilities.getDouble(yTicInterval.getText()));
			value = titleField.getText();
			setVisible(false);
		}
	}

	private void setValues(Axis xAxis, Axis yAxis) {
		xLabel.setText(xAxis.getLab());
		xMin.setText(Double.toString(xAxis.getMin()));
		xWidth.setText(Integer.toString(xAxis.getN()));
		xMax.setText(Double.toString(xAxis.getMax()));
		xDecimals.setText(Integer.toString(xAxis.getM()));
		xTicInterval.setText(Double.toString(xAxis.getTicint()));
		yLabel.setText(yAxis.getLab());
		yMin.setText(Double.toString(yAxis.getMin()));
		yWidth.setText(Integer.toString(yAxis.getN()));
		yMax.setText(Double.toString(yAxis.getMax()));
		yDecimals.setText(Integer.toString(yAxis.getM()));
		yTicInterval.setText(Double.toString(yAxis.getTicint()));

	}

	private void defaults() {
		Axis xAxis2 = new Axis(xAxis.getIndex());
		Axis yAxis2 = new Axis(yAxis.getIndex());
		tgr.setDefaults(xAxis2);
		tgr.setDefaults(yAxis2);
		setValues(xAxis2, yAxis2);
	}
}
