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

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class QuilfFileFilter extends FileFilter {
	private String defaultExt;
	private String description;

	QuilfFileFilter(String defaultExt, String description) {
		super();
		this.defaultExt = defaultExt;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equalsIgnoreCase(defaultExt))
				return true;
		} else {
			return false;
		}
		return false;
	}

}
