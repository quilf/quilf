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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;


/*------------------------------------------------------------*/
public class TSln {
	private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TSln.class);
	public static final int G = 0;
	public static final int H = 1;
	public static final int S = 2;
	public static final int V = 3;

	public static final int OlIl = 0;
	public static final int FeTi = 1;
	public static final int MgFe = 2;
	public static final int MnFe = 3; //ol-il-sp exchange parameters
	public static final int DelEn = 4;
	public static final int DelFs = 5;
	public static final int DelDi = 6;
	public static final int DelHd = 7; //pyroxene exchange parameters

	public static final int Rctns = 0;
	public static final int Ol = 1;
	public static final int Aug = 2;
	public static final int Pig = 3;
	public static final int Opx = 4;
	public static final int Sp = 5;
	public static final int Ilm = 6;
	public static final int Iron = 7;
	public static final int Quartz = 8;
	public static final int Rutile = 9;
	public static final int Titanite = 10;
	public static final int Karooite = 11;
	public static final int Oxygen = 12;
	public static final int NUMPH = 13;
	public static TPhase Slns[] = new TPhase[NUMPH];

	double dT, dP;

	public TSln() {
		Slns[Rctns] = new TRctn();
		Slns[Ol] = new TOlivine();
		Slns[Aug] = new TCpx();
		Slns[Pig] = new TCpx();
		Slns[Opx] = new TOpx();
		Slns[Sp] = new TSpinel();
		Slns[Ilm] = new TIlm();
		Slns[Iron] = new TIron();
		Slns[Quartz] = new TQuartz();
		Slns[Rutile] = new TRutile();
		Slns[Titanite] = new TSphene();
		Slns[Karooite] = new TKarooite();
		Slns[Oxygen] = new TOxygen();
	}

	public void adjustSS(int I, double U0_Qtz_Haas, double U0_Fa_Haas) {
		/*
		Adjust standard state values from Haas and Helgeson to a consistent
		set of values.
		slns[ol]^.u0^[i,fa] and slns[quartz]^.u0^[i,qtz]
		initially have Helgeson values, set to Haas values at exit.
		*/
		double Sign, DFAQ, DFa;
		if (I == S)
			Sign = -1.0;
		else
			Sign = 1.0;
		Slns[Ilm].u0Array[I][TIlm.Gk] = Slns[Ilm].u0Array[I][TIlm.Il]
				+ (Slns[Ol].u0Array[I][TOlivine.fo] - Slns[Ol].u0Array[I][TOlivine.fa] - Sign * Slns[Rctns].sln[I][OlIl]) / 2.0;
		Slns[Sp].u0Array[I][TSpinel.Usp] = Slns[Ilm].u0Array[I][TIlm.Il] + Slns[Sp].u0Array[I][TSpinel.Mt] - Slns[Ilm].u0Array[I][TIlm.Hm] + Sign
				* Slns[Rctns].sln[I][FeTi];
		Slns[Sp].u0Array[I][TSpinel.Mf] = (Slns[Ol].u0Array[I][TOlivine.fo] - Slns[Ol].u0Array[I][TOlivine.fa] - Sign * Slns[Rctns].sln[I][OlIl]) / 2.0
				+ Slns[Sp].u0Array[I][TSpinel.Mt] - Sign * Slns[Rctns].sln[I][MgFe];
		Slns[Sp].u0Array[I][TSpinel.Jac] = Slns[Sp].u0Array[I][TSpinel.Mt] - Slns[Ilm].u0Array[I][TIlm.Il] - Sign * Slns[Rctns].sln[I][MnFe];
		double du23 = -Slns[Sp].sln[I][TSpinel.g23] //- Slns[Sp].Sln[I][TSpinel.g33] not defined == 0?
				- Slns[Sp].sln[I][TSpinel.g223];
		//						- 2.0*Slns[Sp].Sln[I][TSpinel.g233]		not defined == 0?
		//						- 3.0*Slns[Sp].Sln[I][TSpinel.g333];	not defined == 0?
		Slns[Sp].u0Array[I][TSpinel.Qan] = Slns[Sp].u0Array[I][TSpinel.Usp] + 2.0 * (Slns[Sp].u0Array[I][TSpinel.Mf] - Slns[Sp].u0Array[I][TSpinel.Mt] - du23);
		//log.debug("TSolution.AdjustSS " + I + " " + du23 + " " + Slns[Sp].u0Array[I][TSpinel.Qan]);

		for (int Ph = Aug; Ph <= Pig; Ph++) {
			Slns[Ph].u0Array[I][TPx.en] = Sign * Slns[Rctns].sln[I][DelEn] + Slns[Opx].u0Array[I][TPx.en];
			Slns[Ph].u0Array[I][TPx.FS] = Sign * Slns[Rctns].sln[I][DelFs] + Slns[Opx].u0Array[I][TPx.FS];
		}
		Slns[Opx].u0Array[I][TPx.di] = Slns[Aug].u0Array[I][TPx.di] - Sign * Slns[Rctns].sln[I][DelDi];
		Slns[Opx].u0Array[I][TPx.hd] = Slns[Aug].u0Array[I][TPx.hd] - Sign * Slns[Rctns].sln[I][DelHd];
		DFa = U0_Fa_Haas - Slns[Ol].u0Array[I][TOlivine.fa];
		DFAQ = U0_Fa_Haas - Slns[Quartz].u0Array[I][TQuartz.Qtz] - Slns[Ol].u0Array[I][TOlivine.fa] + U0_Qtz_Haas;
		for (int Ph = Aug; Ph <= Opx; Ph++) {
			for (int C = TPx.en; C <= TPx.hd; C++)
				Slns[Ph].u0Array[I][C] = Slns[Ph].u0Array[I][C] + DFAQ;
			Slns[Ph].sln[I][TPxOl.f01] = 2.0 * (Slns[Ph].u0Array[I][TPx.di] - Slns[Ph].u0Array[I][TPx.hd]) + Slns[Ph].u0Array[I][TPx.FS]
					- Slns[Ph].u0Array[I][TPx.en];
		}
		Slns[Ol].u0Array[I][TOlivine.fo] = Slns[Ol].u0Array[I][TOlivine.fo] + DFa;
		Slns[Ol].u0Array[I][TOlivine.fa] = U0_Fa_Haas;
		Slns[Ol].u0Array[I][TOlivine.ks] = Slns[Ol].u0Array[I][TOlivine.ks] + DFa;
		Slns[Ol].u0Array[I][TOlivine.mo] = Slns[Ol].u0Array[I][TOlivine.mo] + DFa;
		Slns[Ol].sln[I][TPxOl.f01] = 2.0 * (Slns[Ol].u0Array[I][TOlivine.mo] - Slns[Ol].u0Array[I][TOlivine.ks]) + Slns[Ol].u0Array[I][TOlivine.fa]
				- Slns[Ol].u0Array[I][TOlivine.fo];
		Slns[Quartz].u0Array[I][TQuartz.Qtz] = U0_Qtz_Haas;
	}

	public int dU0(double TK, double P) {
		/*
			Calculate u0 values and adjust to consistent standard states.
			Returns Err set to DMu0 error if an error occurs else unchanged.
		*/
		int I;
		int Ph;
		double U0_Fa_Haas[] = new double[4];
		double U0_Qtz_Haas[] = new double[4];

		for (Ph = Ol; Ph < NUMPH; Ph++) {
			for (I = Slns[Ph].lowerComponent; I <= Slns[Ph].upperComponent; I++) {
				int Err = updatedU0(Slns[Ph].u0P[I], TK, P);
				Slns[Ph].u0Array[S][I] = dT;
				Slns[Ph].u0Array[V][I] = dP;
				if (Err != TErrors.No_Err)
					return Err;
			}
		}
		int Err1 = updatedU0(TQuartz.P_Qtz_Haas, TK, P);
		U0_Qtz_Haas[S] = dT;
		U0_Qtz_Haas[V] = dP;
		int Err2 = updatedU0(TOlivine.P_Fa_Haas, TK, P);
		U0_Fa_Haas[S] = dT;
		U0_Fa_Haas[V] = dP;
		adjustSS(S, U0_Qtz_Haas[S], U0_Fa_Haas[S]);
		adjustSS(V, U0_Qtz_Haas[V], U0_Fa_Haas[V]);
		if (Err1 != TErrors.No_Err)
			return Err1;
		return Err2;
	}

//	public boolean readSolution7(File file) {
//		boolean success = false;
//		try {
//			FileInputStream br = new FileInputStream(file);
//			success = readSolution(br);
//			br.close();
//		} catch (FileNotFoundException ioe) {
//			log.error(ioe);
//			log.error("Can't find " + file.getAbsolutePath());
//			success = false;
//		} catch (IOException ioe) {
//			log.error("I/O Error closing:" + file);
//			success = false;
//		}
//		return success;
//	}
//
	public boolean readSolution(InputStream br) {
		boolean success = false;
		if (br != null) {
			Reader r = new BufferedReader(new InputStreamReader(br));
			StreamTokenizer ts = new StreamTokenizer(r);
			ts.eolIsSignificant(true);
			ts.commentChar('#');
			ts.slashSlashComments(true);
			ts.slashStarComments(true);
			success = true;
			for (int I = Rctns; I < NUMPH; I++) {
				//log.debug("TSln:i=" + I);
				boolean Ok = Slns[I].readFile(ts);
				if (!Ok) {
					log.error("Error reading file  for phase " + I);
					success = false;
				}
			}
		} else
			log.error("Can't read solution file");
		return success;
	}

	public int standardStates(double TK, double P) {
		/*
			Calculate standard state and model values at T and P.
			Returns Err set to updateu0 error if error occurs, else unchanged.
		*/
		double dP;
		int I;
		int Ph;
		int Err = TErrors.No_Err;
		double U0_Fa_Haas; //[] = new double[4];
		double U0_Qtz_Haas; //[] = new double[4];

		dP = P - 1.0;
		Slns[Rctns].calcdG(TK, dP, OlIl, MnFe);
		Slns[Rctns].calcdG(TK, P, DelEn, DelHd);

		for (Ph = Ol; Ph <= Opx; Ph++)
			Slns[Ph].calcdG(TK, P, Slns[Ph].lowerVariable, Slns[Ph].upperVariable);

		for (Ph = Sp; Ph <= Ilm; Ph++)
			Slns[Ph].calcdG(TK, dP, Slns[Ph].lowerVariable, Slns[Ph].upperVariable);

		for (Ph = Ol; Ph < NUMPH; Ph++)
			for (I = Slns[Ph].lowerComponent; I <= Slns[Ph].upperComponent; I++) {
				if (Slns[Ph].u0P[I] == null) {
					Slns[Ph].u0Array[G][I] = 0.0;
				} else {
					Err = Slns[Ph].u0P[I].updateU0(TK, P);
					if (Err == TErrors.No_Err)
						Slns[Ph].u0Array[G][I] = Slns[Ph].u0P[I].u0();
					else
						log.error("u0P(Error) = " + Err + " " + Ph + " " + I);
				}
			}
		Err = TQuartz.P_Qtz_Haas.updateU0(TK, P);
		if (Err != TErrors.No_Err)
			log.error("error in updateu0 for P_Qtz_Haas");
		U0_Qtz_Haas = TQuartz.P_Qtz_Haas.u0();

		Err = TOlivine.P_Fa_Haas.updateU0(TK, P);
		if (Err != TErrors.No_Err)
			log.error("error in updateu0 for P_Fa_Haas");
		U0_Fa_Haas = TOlivine.P_Fa_Haas.u0();

		adjustSS(G, U0_Qtz_Haas, U0_Fa_Haas);
		return Err;
	}

	public int updatedU0(TTherm U0p, double TK, double P) { //, double dT, double dP) {
		/*
		ignore possible error in dmu0 if tk is outside range
		dmu0 will exit if tk is too close to zero
		*/
		int Err = TErrors.No_Err;
		if (U0p == null) {
			dT = 0.0;
			dP = 0.0;
		} else {
			Err = U0p.dMu0(TK, P);
			dT = U0p.dT();
			dP = U0p.dP();
			if (Err != TErrors.No_Err) {
				U0p.errorMessage(Err);
			}
		}
		return Err;
	}
}