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
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class TReactions extends JFrame {
	private JTextPane jTextPane = null;
	private JMenu jMenu;
	private JMenuBar jMenuBar;
	private JMenuItem jMenuItem;

	public void setText(String s) {
		jTextPane.setText(s);
	}

	private void jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem1ActionPerformed
		setVisible(false);
		dispose();
	}// GEN-LAST:event_jMenuItem1ActionPerformed

	public TReactions() {
		JScrollPane jScrollPane = new JScrollPane();
		jTextPane = new JTextPane();

		jMenuBar = new javax.swing.JMenuBar();
		jMenu = new javax.swing.JMenu();
		jMenuItem = new javax.swing.JMenuItem();

		jScrollPane.setViewportView(jTextPane);

		jMenu.setText("File");

		jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0));
		jMenuItem.setText("Close");
		jMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemActionPerformed(evt);
			}
		});
		jMenu.add(jMenuItem);

		jMenuBar.add(jMenu);

		setJMenuBar(jMenuBar);
		add(jScrollPane);
		pack();
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 400);
	}

}
