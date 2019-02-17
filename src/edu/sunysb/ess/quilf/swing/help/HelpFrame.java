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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

public class HelpFrame extends JFrame implements ActionListener {
 private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HelpFrame.class);
	private final static String BACK = "Back";
	private final static String CLOSE = "Close";
	private final static String CONTENTS = "Contents";
	private final static String HELPDIR = "/help/";
	private final static String INDEX = "index.html";
	public final static String WHATSNEW = "whatsNew.html";
	private final static String NEXT = ">>";
	private final static String PREVIOUS = "<<";
	private JButton backButton;
	private String currentURL = "";
	private JEditorPane editorPane;
	private JPanel helpPanel;
	private Stack<String> history;
	private JButton indexButton;
	private JButton nextButton;
	private JButton previousButton;
	private ArrayList<String> sequence;
	private final static int MAXSTACKSIZE = 100;

	public HelpFrame() {
		super("Quilf - Help");
		readSequence();
		helpPanel = new JPanel();
		history = new Stack<String>();
		helpPanel.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton closeButton = new JButton(CLOSE);
		closeButton.addActionListener(this);
		indexButton = new JButton(CONTENTS);
		indexButton.addActionListener(this);
		nextButton = new JButton(NEXT);
		nextButton.addActionListener(this);
		previousButton = new JButton(PREVIOUS);
		previousButton.addActionListener(this);
		backButton = new JButton(BACK);
		backButton.addActionListener(this);
		backButton.setEnabled(false);
		buttonPanel.add(indexButton);
		buttonPanel.add(backButton);
		buttonPanel.add(previousButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(closeButton);
		// Create an editor pane.
		editorPane = createEditorPane();
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(600, 500));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));
		helpPanel.add(buttonPanel, BorderLayout.NORTH);
		helpPanel.add(editorScrollPane, BorderLayout.CENTER);
		add(helpPanel);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (CONTENTS.equals(e.getActionCommand())) {
			setContents(INDEX);
		} else if (NEXT.equals(e.getActionCommand())) {
			int i = lookup(currentURL);
			if ((i >= 0) && (i < sequence.size() - 1)) {
				String s = sequence.get(i + 1);
				setContents(s);
				push(s);
			}
		} else if (PREVIOUS.equals(e.getActionCommand())) {
			int i = lookup(currentURL);
			if ((i > 0) && (i < sequence.size())) {
				String s = sequence.get(i - 1);
				setContents(s);
				push(s);
			}
		} else if (BACK.equals(e.getActionCommand())) {
			String s = pop();
			if (s != null) {
				setContents(s);
				backButton.setEnabled(!history.empty());
			}
		} else if (CLOSE.equals(e.getActionCommand())) {
			this.setVisible(false);
		}
	}

	private JEditorPane createEditorPane() {
		editorPane = new JEditorPane();
		HTMLEditorKit htmlkit = new HTMLEditorKit();
		editorPane.setEditorKit(htmlkit);
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(new HelpLinkFollower(this, editorPane));
		setContents(INDEX);

		return editorPane;
	}

	private String extractFileName(String s) {
		int i = s.indexOf(HELPDIR);
		if (i > 0) {
			String fileName = s.substring(i + HELPDIR.length());
			return fileName;
		}
		return null;
	}

	private int lookup(String fileName) {
		if (fileName.length() == 0)
			return 0;// first page
		int i = -1;
		String s = null;
		int j = fileName.indexOf("#");
		if (j >= 0)
			s = fileName.substring(0, j);
		else
			s = fileName;
		for (int k = 0; k < sequence.size(); k++) {
			if (s.equals(sequence.get(k))) {
				i = k;
				break;
			}
		}
		return i;
	}

	private String pop() {
		if (history.empty())
			return null;
		return (String) history.pop();

	}

	private void push(String s) {
		// if (!s.equals(currentURL)) {
		// currentURL = s;
		if (history.size() > MAXSTACKSIZE)
			history.removeElementAt(0);
		history.push(currentURL);
		backButton.setEnabled(true);
		// }

	}

	private boolean readSequence() {
		sequence = new ArrayList<String>();
		boolean success = true;
		try {
			InputStream file = HelpFrame.class.getResourceAsStream(HELPDIR + "sequence.txt");
			BufferedReader pw = new BufferedReader(new InputStreamReader(file));
			String line = null;
			while ((line = pw.readLine()) != null) {
				if (!line.startsWith("#")) {
					StringTokenizer st = new StringTokenizer(line, "|");
					String fileName = null;
					String desc = null;
					if (st.hasMoreTokens())
						fileName = st.nextToken();
					if (st.hasMoreTokens())
						desc = st.nextToken();
					if (fileName != null) {
						sequence.add(fileName);
					}
				}
			}
		} catch (IOException e) {
			log.error(e.toString());
			success = false;
		}
		return success;
	}

	public void setContents(String fileName) {
		log.debug("setContents:" + fileName);
		if (fileName.length() > 0) {
			java.net.URL url = HelpFrame.class.getResource(HELPDIR + fileName);
			if (url != null) {
				try {
					editorPane.setPage(url);
					/*
					 * String s = extractFileName(fileName); if (s != null)
					 */currentURL = fileName;
				} catch (IOException e) {
					log.error("Attempted to read a bad URL: " + fileName);
				}
			} else {
				log.error("Couldn't find file: " + fileName);
			}
		}
	}

	public void setCurrent(String fileName) {
		if (currentURL != null)
			if (currentURL.length() > 0)
				push(currentURL);
		String s = extractFileName(fileName);
		if (s != null)
			currentURL = s;
		// push(s);
	}

}
