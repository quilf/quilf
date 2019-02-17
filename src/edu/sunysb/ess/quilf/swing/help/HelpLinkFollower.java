package edu.sunysb.ess.quilf.swing.help;

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
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpLinkFollower implements HyperlinkListener {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HelpLinkFollower.class);
	private JEditorPane pane;
	private HelpFrame helpPanel;

	public HelpLinkFollower(HelpFrame panel, JEditorPane pane) {
		this.pane = pane;
		this.helpPanel = panel;
	}

	public void hyperlinkUpdate(HyperlinkEvent evt) {

		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				URL url = evt.getURL();
				if (url != null) {
					pane.setPage(url);
					helpPanel.setCurrent(url.getFile());
				}
			} catch (Exception e) {
				log.error(e.toString());
			}
		}

	}
}
