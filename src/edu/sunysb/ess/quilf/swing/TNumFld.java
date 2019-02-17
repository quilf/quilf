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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import edu.sunysb.ess.quilf.model.TErrors;
import edu.sunysb.ess.quilf.model.TPhase;

public class TNumFld extends JTextField {
	private  static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TNumFld.class);
	public class FocusListener extends FocusAdapter {
		boolean showingDialog = false;

		public void focusGained(FocusEvent evt) {
			final JTextComponent c = (JTextComponent) evt.getSource();
			Quilf.setCurrentComponent(c);
			if (hasError) {
				//log.debug("Number error:" + getText());
				Quilf.showStatus("Number error", TErrors.No_Err);
			} else
				Quilf.showStatus("", TErrors.No_Err);
		}

		public void focusLost(FocusEvent evt) {
			if (evt.isTemporary()) {
				return;
			}
			final JTextComponent c = (JTextComponent) evt.getSource();
			String s = c.getText();
			if (s.length() > 0) {
				changed = true;
				if (valid()) {
					// nothing to do
				} else {
					c.requestFocus();
				}
			}
		}
	}

	//private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TNumFld.class);
	final static int MINIMUMSIZE = 80;
	private boolean changed;
	private boolean forNext;
	private boolean hasError;
	private double min, max, x;
	private String saveValue;
	private boolean variable;
	private double xInitialValue, xForValue, xStepValue;

	private double xWeightValue;

	public TNumFld(double min, double max) {
		super();
		this.min = min;
		this.max = max;
		saveValue = "";
		changed = true;
		forNext = false;
		variable = false;
		hasError = false;
		xInitialValue = 0;
		xForValue = 0;
		xStepValue = 0;
		xWeightValue = 0;
		addFocusListener(new FocusListener());
	}

	public void clear() {
		setText("");
		hasError = false;
		setForeground(Color.black);
		return;
	}

	public double getDouble(String s) {
		try {
			if (s != null) {
				Double f = Double.valueOf(s);
				return f.doubleValue();
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		hasError = true;
		return 0;
	}

	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		return new Dimension(MINIMUMSIZE, d.height);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public double getXForValue() {
		return xForValue;
	}

	public double getXInitialValue() {
		return xInitialValue;
	}

	public double getXStepValue() {
		return xStepValue;
	}

	public double getXWeightValue() {
		return xWeightValue;
	}

	public boolean isChanged() {
		return !saveValue.equals(getText());
	}

	public boolean isForNext() {
		return forNext;
	}

	public boolean isHasError() {
		return hasError;
	}

	public boolean isVariable() {
		return variable;
	}

	public void parse() {
		int i;

		hasError = false;
		if (changed) {
			variable = false;
			forNext = false;
			xInitialValue = 0.0;
			xForValue = 0.0;
			xStepValue = 0.0;
			xWeightValue = 0.0;
			String s = getText();
			i = 0;
			int len = s.length();
			int i2;
			char c;
			if (i < len)
				c = s.charAt(i);
			else
				c = 0;
			while (c == ' ') {
				i++;
				if (i < len)
					c = s.charAt(i);
				else
					c = 0;
			}
			if (i < len) {
				i2 = i;
				while (i2 < len && (c == '.' || c == '-' || Character.isDigit(c))) {
					i2++;
					if (i2 < len)
						c = s.charAt(i2);
					else
						c = 0;
				}
				xInitialValue = getDouble(s.substring(i, i2));
				i = i2;
				x = xInitialValue;
				if (xInitialValue < min || xInitialValue > max) {
					hasError = true;
					// showStatus("error in line");
				}

				xForValue = xInitialValue;
				if (i < len)
					c = s.charAt(i);
				else
					c = 0;
				while (c == ' ') {
					i++;
					if (i < len)
						c = s.charAt(i);
					else
						c = 0;
				}
				if (c == 0)
					return;
				if (c == '(') {
					variable = true;
					i++;
					c = s.charAt(i);
					i2 = i;
					while (i2 < len && (c == '.' || c == '-' || Character.isDigit(c))) {
						i2++;
						if (i2 < len)
							c = s.charAt(i2);
						else
							c = 0;
					}
					xWeightValue = getDouble(s.substring(i, i2));
					i = i2;
					if (i < len)
						c = s.charAt(i);
					else
						c = 0;
					if (c == ')')
						i++;
					else {
						hasError = true;
						//log.debug("missing ) in " + s);
						// showStatus("Missing )");
					}
				} else if (c == '?') {
					i++;
					variable = true;
					xWeightValue = 0.0;
				} else {
					if (c == ',') {
						i++;
						i2 = i;
						c = s.charAt(i2);
						while (i2 < len && (c == '.' || c == '-' || Character.isDigit(c))) {
							i2++;
							if (i2 < len)
								c = s.charAt(i2);
							else
								c = 0;
						}
						xForValue = getDouble(s.substring(i, i2));
						i = i2;
						if (xForValue < min || xForValue > max) {
							hasError = true;
							//log.debug("xForValue < min or xForValue > max" + xForValue + ":" + min + "," + max);
							// LineError ();
						}

						if (i < len)
							c = s.charAt(i);
						else
							c = 0;
						while (c == ' ') {
							i++;
							if (i < len)
								c = s.charAt(i);
							else
								c = 0;
						}
						if (c == 0)
							return;
						if (c == ',') {
							i++;
							i2 = i;
							c = s.charAt(i2);
							while (i2 < len && (c == '.' || c == '-' || Character.isDigit(c))) {
								i2++;
								if (i2 < len)
									c = s.charAt(i2);
								else
									c = 0;
							}
							xStepValue = getDouble(s.substring(i, i2));
						}
						i = i2;
					} else {
						xStepValue = xForValue - xInitialValue;
						hasError = true;// extra characters at the end?
						//log.debug(s + ":c=" + c);
					}
					if (Math.abs(xStepValue) < TPhase.ZERO) {
						if (xStepValue < 0.0)
							xStepValue = -TPhase.ZERO;
						else
							xStepValue = TPhase.ZERO;
					}
					forNext = true;
				}
			}
		} else {
			// hasn't changed
		}
		changed = hasError;
	}

	public void saveValue() {
		saveValue = getText();
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public void setXForValue(double forValue) {
		xForValue = forValue;
	}

	public void setXInitialValue(double initialValue) {
		xInitialValue = initialValue;
	}

	public void setXStepValue(double stepValue) {
		xStepValue = stepValue;
	}

	public void setXWeightValue(double weightValue) {
		xWeightValue = weightValue;
	}

	public String toString() {
		return getText();
	}

	public boolean valid() {
		boolean hadError = hasError;
		parse();
		if (x > min && x < max) {
			if (hadError) {
				hasError = false;
				setForeground(Color.black);
				repaint();
			}
			return true;
		}
		Quilf.showStatus("Invalid Number", TErrors.CompError);
		setForeground(Color.red);
		repaint(); // only if the error is different
		//log.debug("not valid:" + x);
		hasError = true;
		return false;
	}

	public boolean variableDifferent() {
		return (!saveValue.equals(getText()));
	}
}