package edu.sunysb.ess.quilf.utilities;

public class Utilities {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Utilities.class);

	private Utilities() {
		// prevent construction
	}

	public static double getDouble(String s) {
		double x = 0;
		try {
			if (s != null) {
				x = Double.parseDouble(s);
			}
		} catch (NumberFormatException e) {
			log.error(e.toString());
		}
		return x;
	}

	public static int getInt(String s) {
		int x = 0;
		try {
			if (s != null) {
				x = Integer.parseInt(s);
			}
		} catch (NumberFormatException e) {
			log.error(e.toString());
		}
		return x;
	}

}
