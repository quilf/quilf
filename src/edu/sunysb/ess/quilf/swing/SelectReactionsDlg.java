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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.sunysb.ess.quilf.model.TRxn;

public class SelectReactionsDlg extends JDialog implements ActionListener {
	JButton addOneButton;
	JButton removeOneButton;
	JButton removeAllButton;
	JButton addAllButton;
	private final static String CANCEL = "Cancel";
	private final static String OK = "Ok";
	private final static String ADDONE = ">";
	private final static String ADDALL = ">>";
	private final static String REMOVEONE = "<";
	private final static String REMOVEALL = "<<";
	private static SelectReactionsDlg dialog;
	private static String value = null;

	private ArrayList<TRxn> possibleReactions;
	private ArrayList<TRxn> selectedReactions;
	private ReactionListModel possibleListModel;
	private ReactionListModel selectedListModel;

	public static String showDialog(Component frameComp, ArrayList<TRxn> possibleReactions, ArrayList<TRxn> selectedReactions) {
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new SelectReactionsDlg(frame, possibleReactions, selectedReactions);
		dialog.pack();
		dialog.setVisible(true);
		return value;
	}
	private JList possibleList;
	private JList selectedList;
	private JLabel currentReaction;

	private String getReaction(ReactionListModel model, int i) {
		return model.getReaction(i);
	}

	private SelectReactionsDlg(Frame owner, ArrayList<TRxn> possibleReactions, ArrayList<TRxn> selectedReactions) {
		super(owner, "Select Reactions", true);
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Collections.sort(possibleReactions);
		Collections.sort(selectedReactions);
		this.possibleReactions = possibleReactions;
		this.selectedReactions = selectedReactions;
		this.setTitle("Reactions");

		possibleListModel = new ReactionListModel(possibleReactions);
		possibleList = new JList(possibleListModel);
		possibleList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		possibleList.setLayoutOrientation(JList.VERTICAL);
		possibleList.setVisibleRowCount(10);

		possibleList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int i = possibleList.getSelectedIndex();
					currentReaction.setText(getReaction(possibleListModel, i));
				} else if (e.getClickCount() == 2) {
					addOneButton.doClick(); // emulate button click
				}
			}
		});
		possibleList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				// use keyPressed
			}

			public void keyTyped(KeyEvent keyEvent) {
				// use keyPressed
			}

			public void keyReleased(KeyEvent keyEvent) {
				int keyCode = keyEvent.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_KP_DOWN:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_UP:
				case KeyEvent.VK_UP:
					int i = possibleList.getSelectedIndex();
					currentReaction.setText(getReaction(possibleListModel, i));
					break;
				}

			}
		});

		JScrollPane possibleListScroller = new JScrollPane(possibleList);
		possibleListScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel possiblePanel = new JPanel();
		possibleListScroller.setPreferredSize(new Dimension(150, 100));

		possiblePanel.add(possibleListScroller);
		TitledBorder possibleReactionBorder = BorderFactory.createTitledBorder(blackline, "Possible Reactions");
		possibleReactionBorder.setTitleJustification(TitledBorder.LEFT);
		possiblePanel.setBorder(possibleReactionBorder);

		selectedListModel = new ReactionListModel(selectedReactions);

		selectedList = new JList(selectedListModel);
		selectedList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		selectedList.setLayoutOrientation(JList.VERTICAL);
		selectedList.setVisibleRowCount(10);

		selectedList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int i = selectedList.getSelectedIndex();
					currentReaction.setText(getReaction(selectedListModel, i));
				} else if (e.getClickCount() == 2) {
					removeOneButton.doClick(); // emulate button click
				}
			}
		});
		selectedList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				// use keyPressed
			}

			public void keyTyped(KeyEvent keyEvent) {
				// use keyPressed
			}

			public void keyReleased(KeyEvent keyEvent) {
				int keyCode = keyEvent.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_KP_DOWN:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_UP:
				case KeyEvent.VK_UP:
					int i = selectedList.getSelectedIndex();
					currentReaction.setText(getReaction(selectedListModel, i));
					break;
				}

			}
		});

		JScrollPane selectedListScroller = new JScrollPane(selectedList);
		selectedListScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel selectedPanel = new JPanel();
		selectedListScroller.setPreferredSize(new Dimension(150, 100));
		selectedPanel.add(selectedListScroller);
		TitledBorder selectedReactionBorder = BorderFactory.createTitledBorder(blackline, "Selected Reactions");
		selectedReactionBorder.setTitleJustification(TitledBorder.LEFT);
		selectedPanel.setBorder(selectedReactionBorder);

		JPanel centerPanel = new JPanel(new GridLayout(4, 1));
		addOneButton = new JButton(ADDONE);
		addOneButton.addActionListener(this);
		centerPanel.add(addOneButton);
		addAllButton = new JButton(ADDALL);
		addAllButton.addActionListener(this);
		centerPanel.add(addAllButton);
		removeOneButton = new JButton(REMOVEONE);
		removeOneButton.addActionListener(this);
		centerPanel.add(removeOneButton);
		removeAllButton = new JButton(REMOVEALL);
		removeAllButton.addActionListener(this);
		centerPanel.add(removeAllButton);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton okButton = new JButton(OK);
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel currentReactionPanel = new JPanel();
		currentReaction = new JLabel("");
		currentReaction.setPreferredSize(new Dimension(500, 30));
		currentReactionPanel.add(currentReaction);
		TitledBorder currentReactionBorder = BorderFactory.createTitledBorder(blackline, "Current Reaction");
		currentReactionBorder.setTitleJustification(TitledBorder.LEFT);
		currentReactionPanel.setBorder(currentReactionBorder);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		panel.add(possiblePanel, c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		panel.add(centerPanel, c);
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		panel.add(selectedPanel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 3;
		panel.add(currentReactionPanel, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 3;
		panel.add(buttonPanel, c);
		add(panel);
		enableButtons();
	}

	public void actionPerformed(ActionEvent e) {
		String arg = e.getActionCommand();
		if (OK.equals(arg)) {
			value = arg;
			this.setVisible(false);
		} else if (CANCEL.equals(arg)) {
			this.setVisible(false);
		} else if (ADDONE.equals(arg)) {
			int i = possibleList.getSelectedIndex();
			if (i >= 0) {
				TRxn trxn = (TRxn) possibleListModel.getElementAt(i);
				trxn.Selected = true;
				selectedListModel.addElement(trxn,true);
				possibleListModel.removeElement(i,true);
				enableButtons();
				possibleList.clearSelection();
			}
		} else if (ADDALL.equals(arg)) {
			Iterator<TRxn> iter = possibleReactions.iterator();
			while (iter.hasNext()) {
				TRxn trxn = iter.next();
				trxn.Selected= true;
				selectedReactions.add(trxn);
			}
			possibleReactions.clear();
			selectedListModel.fireContentsChanges();
			possibleListModel.fireContentsChanges();
			enableButtons();
		} else if (REMOVEONE.equals(arg)) {
			int i = selectedList.getSelectedIndex();
			if (i >= 0) {
				TRxn trxn = (TRxn) selectedListModel.getElementAt(i);
				trxn.Selected = false;
				possibleListModel.addElement(trxn,true);
				selectedListModel.removeElement(i,true);
				enableButtons();
				selectedList.clearSelection();
			}
		} else if (REMOVEALL.equals(arg)) {
			Iterator<TRxn> iter = selectedReactions.iterator();
			while (iter.hasNext()) {
				TRxn trxn = iter.next();
				trxn.Selected= false;
				possibleReactions.add(trxn);
			}
			selectedReactions.clear();
			selectedListModel.fireContentsChanges();
			possibleListModel.fireContentsChanges();
			enableButtons();
		}
	}

	private void enableButton(ReactionListModel model, JButton addOne, JButton addAll) {
		if (model.getSize() == 0) {
			addOne.setEnabled(false);
			addAll.setEnabled(false);
		} else {
			addOne.setEnabled(true);
			addAll.setEnabled(true);
		}

	}

	private void enableButtons() {
		enableButton(possibleListModel, addOneButton, addAllButton);
		enableButton(selectedListModel, removeOneButton, removeAllButton);

	}
}
