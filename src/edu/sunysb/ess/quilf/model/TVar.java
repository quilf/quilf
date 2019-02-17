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
public class TVar {
public	TPhase phase;
public	int Ix;


/**
 * This method was created by a SmartGuide.
 * @param phase TPhase
 * @param Ix int
 */
public TVar ( TPhase phase, int Ix) {
	this.phase = phase;
	this.Ix = Ix;
}
}