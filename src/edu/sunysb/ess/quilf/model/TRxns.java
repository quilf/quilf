package edu.sunysb.ess.quilf.model;

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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class TRxns {
	private  static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TRxns.class);
	public static int NumRctns; //total number of reactions
	public static int NumSelected; //number of possible reactions
	public static Vector<TRxn> RctnList; //Contains the master list

	public TRxns() { //URL url) {
		RctnList = new Vector<TRxn>(100);
		//	readReactions(url);
	}

	boolean addReaction(TRxn r) {
		RctnList.addElement(r);
		return true;
	}

	int cIndex(String n, TPhase P) {
		int CI = P.lowerComponent;
		while (CI <= P.upperComponent) {
			if (n.equals(P.componentName[CI])) {
				return CI;
			}
			CI++;
		}
		return (-1);
	}

	TRxn getReaction(DataInputStream ts) {
		int sign = -1;
		TRxn r = new TRxn();
		try {
			int type = ts.read();
			while (type == ';' || type == '\n' || type == '\r') {
				ts.readLine();
				type = ts.read();
			}
			type = ts.read(); // the name comes between two single quotes, terminated by a ;
			if (type == -1 || type == 13)
				return null; // why the 13?
			StringBuffer sb = new StringBuffer();
			while (type != '\'') {
				sb.append((char) type);
				type = ts.read();
			}
			r.NR = new String(sb.toString());
			type = ts.read(); // this should be a ;
			String s = ts.readLine();
			r.PR = s;
			int i = 0;
			int len = s.length();
			if (i < len)
				type = s.charAt(i++);
			else
				type = -1;
			double coeff = 1.0;
			while (i < len) {
				while (type == ' ' || type == '\t') { // skip over blanks
					if (i < len)
						type = s.charAt(i++);
					else
						type = -1;
				}
				if (Character.isDigit((char) type)) {
					coeff = 0; // have to start from 0, not 1
					while (Character.isDigit((char) type)) {
						coeff = 10 * coeff + (type - '0');
						if (i < len)
							type = s.charAt(i++);
						else
							type = -1;
					}
				} else if (Character.isLetterOrDigit((char) type)) {
					StringBuffer w = new StringBuffer();
					while (Character.isLetterOrDigit((char) type)) {
						w.append((char) type);
						if (i < len)
							type = s.charAt(i++);
						else
							type = -1;
					}
					TRCoef rc = pcIndex(w.toString());
					if (rc != null) {
						rc.X = sign * coeff;
						r.addTerm(rc);
						coeff = 1.0; // set up for the NEXT term 
					}
				} else if (type == '=') {
					sign = +1;
					if (i < len)
						type = s.charAt(i++);
					else
						type = -1;
				} else if (type == '+') {
					if (i < len)
						type = s.charAt(i++);
					else
						type = -1;
				} else {
					//				log.error("now what do I do? " + (char) type + " " + type);
					return null;
				}
			}
		} catch (IOException ioe) {
			log.error("I/O Error");
			return null;
		}
		if (r.RN > 0)
			return r;
		return null;
	}

	TRCoef pcIndex(String n) {
		int pi;
		int CI;
		pi = TSln.Ol;
		while (pi < TSln.NUMPH) {
			CI = cIndex(n, TSln.Slns[pi]);
			if (CI >= 0) {
				TRCoef rc = new TRCoef();
				rc.Ph = TSln.Slns[pi];
				rc.C = CI;
				return (rc);
			}
			pi++;
		}
		log.error("pcIndex: Failed:" + n);
		return (null);
	}

	boolean readReactions(File file) {
		boolean success = false;
		try {
			DataInputStream ts = new DataInputStream(new FileInputStream(file));
			success = readReactions(ts);
		} catch (IOException ioe) {
			log.error("I/O Error closing:" + file);
			return false;
		}
		return success;
	}

	boolean readReactions(InputStream file) {
		/*
		Read reaction file and form reaction list.
		
		'reaction name'; v1 + v2 = v3 + v4
		EOF
		*/
		if (file != null)
			try {
				DataInputStream ts = new DataInputStream(file);
				if (ts != null) {
					TRxn r = getReaction(ts);
					while (r != null) {
						addReaction(r);
						r = getReaction(ts);
					}
					ts.close();
				} else
					log.error("Can't readReactions");
			} catch (IOException ioe) {
				log.error(ioe.toString());
				return false;
			}
		return true;
	}
}