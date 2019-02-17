package edu.sunysb.ess.quilf.swing;

import java.text.DecimalFormat;

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
public class TRow {
	static final double Zero = 1.0E-05;
	private TTextLab errorField, calculatedValue;
	private TNumFld input;
	private TTextLab label;
	private TTextLab labelValue;
	private DecimalFormat df;

	public TRow(String formatString) {
		super();
		label = null;
		input = null;
		labelValue = null;
		errorField = null;
		calculatedValue = null;
		df = new DecimalFormat(formatString);
	}

	public void clearAll() {
		if (input != null)
			input.setText("");
		if (labelValue != null)
			labelValue.setText("");
		clearErr();
		clearCalc();
		return;
	}

	public void clearCalc() {
		if (calculatedValue != null)
			calculatedValue.setText("");
		return;
	}

	public void clearErr() {
		if (errorField != null)
			errorField.setText("");
		return;
	}

	/**
	 * This method does a really crude format.
	 * 
	 * @return java.lang.String
	 * @param X
	 *            double
	 */
	public String format(double x) {
		return df.format(x);
		// if (x >= 100) {
		// DecimalFormat df = new DecimalFormat("######");
		// return df.format(x);
		// } else {
		// DecimalFormat df = new DecimalFormat("0.000");
		// return df.format(x);
		// }
	}

	public TTextLab getCalculatedValue() {
		return calculatedValue;
	}

	public TTextLab getErrorField() {
		return errorField;
	}

	public double getFinal() {
		if (input == null)
			return 0;
		return input.getXForValue();
	}

	public double getInitial() {
		if (input == null)
			return 0;
		return input.getXInitialValue();
	}

	public TNumFld getInput() {
		return input;
	}

	public TTextLab getLab() {
		return label;
	}

	public double getStep() {
		if (input == null)
			return 0;
		return input.getXStepValue();
	}

	public TTextLab getTl() {
		return labelValue;
	}

	public double getWeight() {
		if (input == null)
			return 0;
		return input.getXWeightValue();
	}

	public boolean hasChanged() {
		if (input != null) {
			if (input.isChanged())
				return true;
		}
		return false;
	}

	public boolean hasError() {
		if (input == null)
			return false;
		return input.isHasError();
	}

	public boolean isForNext() {
		if (input != null)
			return input.isForNext();
		return false;
	}

	public boolean isPresent() {
		if (input != null) {
			String s = input.getText();
			return (s.length() > 0);
		}
		return false;
	}

	public boolean isVariable() {
		if (input != null)
			return input.isVariable();
		return false;
	}

	public void parse() {
		if (input != null)
			input.parse();
	}

	public void saveValue() {
		if (input != null)
			input.saveValue();
	}

	public void setCalc(double X) {
		if (calculatedValue != null) {
			/*
			 * if (Math.abs(X) < Zero) cf.setText("0"); else
			 */calculatedValue.setText(format(X));
		}
	}

	public void setCalulatedValue(TTextLab cf) {
		this.calculatedValue = cf;
	}

	public void setErr(double X) {
		if (errorField != null) {
			/*
			 * if (X < Zero) ef.setText("0"); else
			 */errorField.setText(format(X));
		}
		return;
	}

	public void setErrorField(TTextLab ef) {
		this.errorField = ef;
	}

	public void setInput(TNumFld tf) {
		this.input = tf;
	}

	public void setLab(TTextLab lab) {
		this.label = lab;
	}

	public void setTl(TTextLab tl) {
		this.labelValue = tl;
	}

	public String toString() {
		if (label != null) {
			if (input != null)
				return (label.toString() + " " + input.toString());
			else if (labelValue != null)
				return (label.toString() + " " + labelValue.toString());
		}
		return "Row";
	}

	public boolean variableDifferent() {
		if (input != null)
			return input.variableDifferent();
		return false;
	}
}