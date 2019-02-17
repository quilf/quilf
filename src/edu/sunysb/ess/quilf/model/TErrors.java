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
public class TErrors {
	public static final int No_Err = 0;
	public static final int TP_Err = 1;
	public static final int Site_Err = 2;
	public static final int Lsq_Err = 3;
	public static final int TooBig_Err = 4;
	public static final int Iterating = 5;
	public static final int Found = 6;
	public static final int Limit = 7;
	public static final int Abort_Err = 8;
	public static final int TooManyVar_Err = 9;
	public static final int NotEnough_Err = 10;
	public static final int NoVar_Err = 11;
	public static final int TooNearZero = 12;
	public static final int TooFlat = 13;
	public static final int CompError = 14;

	public static String errorStr(int err) {
		switch (err) {
		case No_Err:
			return ("None");
		case TP_Err:
			return ("Temperature/Pressure is/are outside of the allowable range");
		case Site_Err:
			return ("Site calculation error");
		case Lsq_Err:
			return ("Least squares error");
		case TooBig_Err:
			return ("Matrix is too big");
		case Iterating:
			return ("Iterating");
		case Found:
			return ("Found");
		case Limit:
			return ("Iteration Limit");
		case Abort_Err:
			return ("Calculation aborted");
		case TooManyVar_Err:
			return ("Too Many Variables");
		case NotEnough_Err:
			return ("Not enough variables");
		case NoVar_Err:
			return ("No variables");
		case TooNearZero:
			return ("Too near zero");
		case TooFlat:
			return ("Too flat");
		case CompError:
			return ("Composition Error");
		}
		return ("");
	}
}