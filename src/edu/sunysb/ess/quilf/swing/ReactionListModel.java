package edu.sunysb.ess.quilf.swing;
/*
part of QUIlF

Copyright (c) 1998 by David Andersen

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

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractListModel;

import edu.sunysb.ess.quilf.model.TRxn;

public class ReactionListModel extends AbstractListModel {
	ArrayList<TRxn> list;

	public ReactionListModel(ArrayList<TRxn> list) {
		this.list = list;
	}

	public int getSize() {
		if (list == null)
			return 0;
		else
			return list.size();
	}

	public String getReaction(int index) {
		if (list == null)
			return null;
		else if (index < list.size()) {
			TRxn trxn = list.get(index);
			return trxn.NR + ":  " + trxn.PR;
		} else
			return null;

	}

	public void fireContentsChanges() {
		Collections.sort(list);
		this.fireContentsChanged(this, 0, list.size());
	}

	public void removeElement(int i, boolean fire) {
		if (list != null) {
			if (i < list.size())
				list.remove(i);
			if (fire)
				this.fireIntervalRemoved(this, i, i);
		}
	}

	public void addElement(TRxn trxn, boolean fire) {
		if (list != null)
			list.add(trxn);
		if (fire) {
			Collections.sort(list);
			this.fireContentsChanged(this, 0, list.size());
		}
	}

	public Object getElementAt(int index) {
		if (list == null)
			return null;
		else if (index < list.size()) {
			TRxn trxn = list.get(index);
			return trxn;
		} else
			return null;
	}

}
