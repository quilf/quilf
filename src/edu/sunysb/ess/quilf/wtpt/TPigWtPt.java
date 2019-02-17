package edu.sunysb.ess.quilf.wtpt;

import edu.sunysb.ess.quilf.swing.TNumFld;
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
public class TPigWtPt extends TPxWtPt {

    public TPigWtPt() {
    	super();
    }

    public TPigWtPt(TNumFld xWo, TNumFld xEn) {
        super(Pig, xWo, xEn);
        init("Pigeonite");
        DisplayComponents(null, null);
    }
}
