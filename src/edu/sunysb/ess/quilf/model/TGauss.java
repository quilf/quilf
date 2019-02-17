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
public class TGauss {
	static int MaxN2 =           32;	       //2*MaxCols
	static double ZERO =             1.0e-16;
	static double MINZ =             1.0e-15;


public TGauss (int n, TRows eqn[])
{
	//          Initialize matrices for PointerGauss.

	int I;
	for (I = 0; I < n; I++)
		eqn[I] = (TRows) new TRows();
}
double dotPrd (int NRows, int I, int J, TRows Eqn[])
{
	double Sum;
	int K;

	Sum = 0.0;
	for (K = 0; K < NRows; K++)
		Sum = Sum + Eqn[K].Coeff[I] * Eqn[K].Coeff[J];
	return Sum;
}
public int leastSquares (int NCols, int NRows, TRows Eqn[], double X[], double Err[]) {
//Solve nRows of NCols equations, return solution in X, uncertainties in Err.

	boolean[] IP = new boolean[TRows.MAXCOLS];
	double E, E2, E3;
	int I, J, K, N1, N2;
	int P = 0;
	int Q = 0;
	double[][] A= new double [TRows.MAXCOLS][MaxN2];
	int Error;
	/* this prints the initial matrix
	for (int i = 0; i < NRows; i++) {
		log.error(i);
		for (int j = 0; j < NCols; j++) {
			log.error(" " + Eqn[i].Coeff[j]);
		}
		log.error(" = " + Eqn[i].B);
	}
	*/
	N1 = NCols; // + 1; 0..NCols indices
	N2 = NCols * 2;
	if (N2 >= MaxN2) {
		return TErrors.TooBig_Err;
	}
	for (I = 0; I < NCols; I++) {
		for (J = 0; J < NCols; J++) {
			A[I][J] = dotPrd (NRows, I, J, Eqn); //[I]);
		}
		K = I + NCols;
		for (J = N1; J < N2; J++) {
			if (J == K)
				A[I][J] = 1.0;
			else
				A[I][J] = 0.0;
		}
		IP[I] = true;
	}
	Error = TErrors.No_Err;
	for (K = 0; K < NCols; K++) {
		E = ZERO;
		for (I = 0; I < NCols; I++) {
			if (IP[I]) {
				for (J = 0; J < NCols; J++) {
					if (Math.abs (A[I][J]) > Math.abs (E)) {
						E = A[I][J];
						P = I;
						Q = J;
					}
				}
			}
		}
		if (Math.abs (E) <= MINZ) {
			return TErrors.Lsq_Err;
		}
		IP[P] = false;
		for (J = 0; J < N2; J++)
			A[P][J] = A[P][J] / E;
		for (I = 0; I < NCols; I++) {
			if (I != P) {
				E2 = A[I][Q];
				for (J = 0; J < N2; J++)
					A[I][J] = A[I][J] - A[P][J] * E2;
			}
		}
	}
	for (I = 0; I < NCols; I++) {
		E = 0.0;
		for (J = 0; J < NRows; J++) {
			E2 = 0.0;
			for (K = 0; K < NCols; K++) {
				E2 = E2 + A[I][K + NCols] * Eqn[J].Coeff[K];
			}
			E = E + E2 * Eqn[J].B;
		}
		X[I] = E;
	}
	if (NCols == NRows) {
		for (I = 0; I < NCols; I++)
			Err[I] = 0.0;
		return Error;
	}
	E2 = 0.0;
	for (I = 0; I < NRows; I++) {
		E = Eqn[I].B;
		for (K = 0; K < NCols; K++)
			E = E - Eqn[I].Coeff[K] * X[K];
		E2 = E2 + E * E;
	}
	if (NRows - NCols == 1)
		E3 = E2;
	else
		E3 = E2 / (NRows - NCols - 1);
	for (K = 0; K < NCols; K++) {
		Err[K] = A[K][K + NCols] * E3;
		if (Err[K] > 0.0)
			Err[K] = Math.sqrt (Err[K]);
/* this prints the solution and error values
log.error(K + " " + X[K] + " " + Err[K]);			
*/
	}
	return Error;
}
}