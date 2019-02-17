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
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import edu.sunysb.ess.quilf.graph.Axis;
import edu.sunysb.ess.quilf.graph.TGR;
import edu.sunysb.ess.quilf.graph.TSpline;
import edu.sunysb.ess.quilf.model.TEqIJ;
import edu.sunysb.ess.quilf.model.TErrors;
import edu.sunysb.ess.quilf.model.TGauss;
import edu.sunysb.ess.quilf.model.TIlm;
import edu.sunysb.ess.quilf.model.TIron;
import edu.sunysb.ess.quilf.model.TKarooite;
import edu.sunysb.ess.quilf.model.TOlivine;
import edu.sunysb.ess.quilf.model.TOpx;
import edu.sunysb.ess.quilf.model.TOxygen;
import edu.sunysb.ess.quilf.model.TPhase;
import edu.sunysb.ess.quilf.model.TPx;
import edu.sunysb.ess.quilf.model.TQuartz;
import edu.sunysb.ess.quilf.model.TRctn;
import edu.sunysb.ess.quilf.model.TRdFiles;
import edu.sunysb.ess.quilf.model.TRows;
import edu.sunysb.ess.quilf.model.TRutile;
import edu.sunysb.ess.quilf.model.TRxn;
import edu.sunysb.ess.quilf.model.TRxns;
import edu.sunysb.ess.quilf.model.TSln;
import edu.sunysb.ess.quilf.model.TSphene;
import edu.sunysb.ess.quilf.model.TSpinel;
import edu.sunysb.ess.quilf.model.TVar;
import edu.sunysb.ess.quilf.swing.help.HelpFrame;
import edu.sunysb.ess.quilf.swing.print.PrintPreviewS;
import edu.sunysb.ess.quilf.wtpt.TAugWtPt;
import edu.sunysb.ess.quilf.wtpt.TIlWtPt;
import edu.sunysb.ess.quilf.wtpt.TOlWtPt;
import edu.sunysb.ess.quilf.wtpt.TOpxWtPt;
import edu.sunysb.ess.quilf.wtpt.TPigWtPt;
import edu.sunysb.ess.quilf.wtpt.TSpWtPt;
import edu.sunysb.ess.quilf.wtpt.TWeight;

public class Quilf extends JPanel implements ActionListener, Runnable {
	private static final String ABOUT = "About";
	private static final String ACTIVITIES = "Activities";
	private static final String AUG = "Aug...";
	private static final String AXESDLG = "Select Axes...";
	private static int currentRow, currentCol;
	private static final String DATA = "Data";
	private static final String EXIT = "Exit";
	private static JFrame frame;
	private static final String GRAPH = "Graph";
	private static final String HELP = "Help";
	private static final String HELPCONTENTS = "Help Contents";
	private static final String ILMENITE = "Ilmenite...";
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Quilf.class);
	private static final double MAXCHANGE = 0.5; // max change in any variable = 50 %
	private static final int MaxIt = 75; // max # of iterations
	private static final String NEW = "Clear";
	private static final String OLIVINE = "Olivine...";
	private static final String OPEN = "Open";
	private static final String OPX = "Opx...";
	public static TOpx orthopyroxene; // needed by TClinopyroxene
	private static final String PAGESETUP = "Page Setup";
	private static TQPanel panel;
	private static final String PIG = "Pig...";
	private static final String PRINT = "Print";
	private final static String QUILFDESCRIPTION = "Quilf Files";
	private final static String QUILFEXTENSION = "q95";
	public static TRctn rctn;
	private static final String REACTIONS = "Reactions";
	private static final String RUN = "Start";
	private static final String SAVE = "Save";
	private static final String SAVEAS = "Save As";
	private static final String SELECTREACTIONS = "Select Reactions...";
	private static final String SOLUTION = "Solution";
	private static final String SPINEL = "Spinel...";
	private static JLabel statusLine;
	private static final String TEST = "Test";
	private static final String VERSION = "6.50j";
	private static final String WHATSNEW = "What's New";
	private static final double XTOL = 1.0e-05; // if change in value < XTol then done

	private void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("Quilf");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Quilf quilf = new Quilf(true);
		JMenuBar menuBar = createMenuBar();
		frame.setJMenuBar(menuBar);

		// Add contents to the window.
		frame.add(this);
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private static int getOptionMessage(int err) {
		switch (err) {
		case TErrors.No_Err:
			return JOptionPane.PLAIN_MESSAGE;
		case TErrors.TP_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.Site_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.Lsq_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.TooBig_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.Iterating:
			return JOptionPane.PLAIN_MESSAGE;
		case TErrors.Found:
			return JOptionPane.PLAIN_MESSAGE;
		case TErrors.Limit:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.Abort_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.TooManyVar_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.NotEnough_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.NoVar_Err:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.TooNearZero:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.TooFlat:
			return JOptionPane.ERROR_MESSAGE;
		case TErrors.CompError:
			return JOptionPane.ERROR_MESSAGE;
		default:
			return JOptionPane.PLAIN_MESSAGE;
		}
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				Quilf quilf = new Quilf(true);
				quilf.createAndShowGUI();
			}
		});
	}

	public static void setCurrentComponent(JComponent component) {
		for (int r = 0; r < TQPanel.MAXROWS; r++) {
			for (int c = 0; c < TQPanel.MAXCOLS; c++) {
				JComponent x = panel.get(r, c);
				if (x instanceof TField) {
					TField tf = (TField) x;// [r][c];
					if (tf.getTf() == component || tf.getTtf() == component) {
						currentRow = r;
						currentCol = c;
					}
				}
			}
		}
	}

	public static void showStatus(String statusMsg, int error) {
		if (statusLine != null) {
			statusLine.setText(statusMsg);
			int optionMessage = getOptionMessage(error);
			// log.debug(statusMsg + ":" + error + ":" + optionMessage);
			if (optionMessage != JOptionPane.PLAIN_MESSAGE)
				JOptionPane.showMessageDialog(frame, statusMsg, "QUIlF", optionMessage);
		}
	}
	private boolean abort; // if the calculations have been aborted
	private JFrame activityFrame = null;
	private JFrame testFrame = null;
	private ActivityTable activityTable = null;
	private TestTable testTable = null;
	private boolean afContinueCalc;
	private TAugWtPt augWtPt = null;
	private double bestFit; // current value of residual (findbest true)
	private DataWindow dataWindow = null;
	private boolean debug; // writes debugging information
	private TRows eqnSel[]; // MaxRows]; // matrix to invert
	private TEqIJ equil; // contains a list of selected reactions
	private boolean err; // global error variable
	private boolean findBest; // flag to determine if the best
	private Thread findQuilf = null;
	private Vector<TVar> forNextVariables; // MaxForVar]; // list of for/next variables
	private TIlWtPt ilWtPt = null;
	private Canvas intro = null;
	private TOlWtPt olWtPt = null;
	private TOpxWtPt opxWtPt = null;
	public Vector<TPhase> phases;
	private TPigWtPt pigWtPt = null;
	private boolean quilfFound; // set true if quilfsuccess is set true
	private boolean quilfSuccess; // set true if solution converges
	private TRxns reactions;
	private final String RESETLABEL = "Reset";
	private String saveFile;
	private String savePath;
	private TSln solution;
	private JFrame solutionFrame = null;
	private SolutionTable solutionTable = null;
	private TSpWtPt spWtPt = null;
	private TToolBar tb;
	private TGR tgr;
	public TTextF title;
	private TSpline tSpline;
	private Axis xAxis;
	private Axis yAxis;

	public Quilf() {
		super();
	}

	public Quilf(boolean initialize) {
		this();
		// intro = new TIntro();
		setLayout(new BorderLayout());
		setBackground(Color.white);
		solution = new TSln();
		reactions = new TRxns();
		initializeScreen();
		JToolBar toolBar = new JToolBar("");
		addButtons(toolBar);
		add(toolBar, BorderLayout.PAGE_START);
		showStatus("Please wait..", TErrors.No_Err);
		TRdFiles readFiles = new TRdFiles(solution, reactions);
		readFiles.run();
		showStatus("Ok", TErrors.No_Err);
	}

	public void actionPerformed(ActionEvent e) {
		// public boolean action(Event e, Object arg) {
		String arg = e.getActionCommand();
		// log.debug(arg);
		if (EXIT.equals(arg)) {
			frame.setVisible(false);
			frame.dispose();
		} else if (NEW.equals(arg)) {
			showStatus("new", TErrors.No_Err);
			title.setText("");
			for (int i = 0; i < phases.size(); i++) {
				TPhase ph = (TPhase) phases.elementAt(i);
				ph.clear();
			}
			repaint();
		} else if (RESETLABEL.equals(arg)) {
			for (int i = 0; i < phases.size(); i++) {
				TPhase ph = (TPhase) phases.elementAt(i);
				ph.clearCalc();
			}
			// calculateButton.setText(CALCULATELABEL);
			showStatus("", TErrors.No_Err);
		} else if (RUN.equals(arg)) {
			if (changed()) {
				boolean different = false;
				boolean ok = true;
				if (getComp())
					different = variablesDifferent();
				else {
					ok = false;
					showStatus("Composition errors", TErrors.CompError);
				}
				if (ok) {
					if ((different) || (equil.Rowi.size() == 0)) {
						equil.Rowi.removeAllElements();
						selectReactions(equil.Rowi);
					}
					err = false;
					afContinueCalc = true;
					showStatus("calculating...", TErrors.No_Err);
					saveValues();
					findQuilf = new Thread(this);
					findQuilf.start();
				}
				// calculateButton.setText(ABORTLABEL);
			} else {
				// showStatus("Composition errors", TErrors.No_Err);// previous errors displayed a message box, this is
				// just for the status line
			}
		} else if (SOLUTION.equals(arg)) {
			if (solutionFrame == null) {
				solutionFrame = new JFrame("Solution");
				solutionTable = new SolutionTable(solution);
				JTable table = new JTable(solutionTable);
				JScrollPane scrollPane = new JScrollPane(table);
				solutionFrame.add(scrollPane);
				solutionFrame.setSize(600, 400);
				solutionFrame.setPreferredSize(new Dimension(600, 400));
				solutionFrame.pack();
			}

			solutionFrame.setVisible(true);

		} else if (ACTIVITIES.equals(arg)) {
			if (activityFrame == null) {
				activityFrame = new JFrame("Activities");
				calcAct();
				if (activityTable == null)
					activityTable = new ActivityTable(solution);
				JTable table = new JTable(activityTable);
				JScrollPane scrollPane = new JScrollPane(table);
				activityFrame.add(scrollPane);
				activityFrame.setSize(600, 400);
				activityFrame.setPreferredSize(new Dimension(600, 400));
				activityFrame.pack();
			}
			activityFrame.setVisible(true);

		} else if (TEST.equals(arg)) {
			if (testFrame == null) {
				testFrame = new JFrame("Test");
				calcTests();
				if (testTable == null)
					testTable = new TestTable(reactions, solution);
				JTable table = new JTable(testTable);
				JScrollPane scrollPane = new JScrollPane(table);
				testFrame.add(scrollPane);
				testFrame.setSize(600, 400);
				testFrame.setPreferredSize(new Dimension(600, 400));
				testFrame.pack();
			}
			testFrame.setVisible(true);

		} else if (DATA.equals(arg)) {
			if (dataWindow == null)
				dataWindow = new DataWindow();
			dataWindow.clear();

			dataWindow.pack();
			dataWindow.setVisible(true);

		} else if (REACTIONS.equals(arg)) {
			TReactions reactionsFrame = new TReactions();
			StringBuffer sb = new StringBuffer();
			Iterator<TRxn> iter = reactions.RctnList.iterator();
			while (iter.hasNext()) {
				TRxn trxn = iter.next();
				if (trxn.Possible) {
					sb.append(trxn.NR + "\n");
				}
			}
			reactionsFrame.setText(sb.toString());
			reactionsFrame.setSize(600, 400);
			reactionsFrame.setPreferredSize(new Dimension(600, 400));
			reactionsFrame.pack();
			reactionsFrame.setVisible(true);
		} else if (SAVE.equals(arg)) {
			File file = null;
			if (saveFile == null) {
				JFileChooser fc = new JFileChooser();
				if (savePath != null)
					fc.setCurrentDirectory(new File(savePath));
				QuilfFileFilter filter = new QuilfFileFilter(QUILFEXTENSION, QUILFDESCRIPTION);
				fc.setFileFilter(filter);
				int returnVal = fc.showSaveDialog(Quilf.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
				}
			} else {
				file = new File(savePath);
			}
			if (file != null) {
				// log.debug("Opening: " + file.getName() + ".");
				if (writeCells(file)) {
					saveFile = file.getName();
					savePath = file.getAbsolutePath();
				} else {
					saveFile = null;
					savePath = null;
				}
			} else {
				// log.debug("Open command cancelled by user.");
			}
		} else if (SAVEAS.equals(arg)) {
			File file = null;
			JFileChooser fc = new JFileChooser();
			if (savePath != null)
				fc.setCurrentDirectory(new File(savePath));
			QuilfFileFilter filter = new QuilfFileFilter(QUILFEXTENSION, QUILFDESCRIPTION);
			fc.setFileFilter(filter);
			int returnVal = fc.showSaveDialog(Quilf.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}
			if (file != null) {
				// This is where a real application would open the file.
				// log.debug("Opening: " + file.getName() + ".");
				if (writeCells(file)) {
					saveFile = file.getName();
					savePath = file.getAbsolutePath();
				} else {
					saveFile = null;
					savePath = null;
				}
			} else {
				// log.debug("Open command cancelled by user.");
			}
		} else if (OPEN.equals(arg)) {
			JFileChooser fc = new JFileChooser();
			if (savePath != null)
				fc.setCurrentDirectory(new File(savePath));
			QuilfFileFilter filter = new QuilfFileFilter(QUILFEXTENSION, QUILFDESCRIPTION);
			fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(Quilf.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				// This is where a real application would open the file.
				// log.debug("Opening: " + file.getName() + ".");
				if (readCells(file)) {
					saveFile = file.getName();
					savePath = file.getAbsolutePath();
				}
			} else {
				// log.debug("Open command cancelled by user.");
			}
		} else if (ILMENITE.equals(arg)) {
			ilWtPt.pack();
			ilWtPt.setVisible(true);
		} else if (SPINEL.equals(arg)) {
			spWtPt.pack();
			spWtPt.setVisible(true);
		} else if (OLIVINE.equals(arg)) {
			olWtPt.pack();
			olWtPt.setVisible(true);
		} else if (AUG.equals(arg)) {
			augWtPt.pack();
			augWtPt.setVisible(true);
		} else if (OPX.equals(arg)) {
			opxWtPt.pack();
			opxWtPt.setVisible(true);
		} else if (PIG.equals(arg)) {
			pigWtPt.pack();
			pigWtPt.setVisible(true);
		} else if (AXESDLG.equals(arg)) {
			String value = TSelectAxesDlg.showDialog(frame, DataRow.getNames(), xAxis, yAxis);
			if (value != null) {
				if (tgr != null) {
					tgr.clear();
				}
				if (xAxis.isChanged())
					tgr.setDefaults(xAxis);
				if (yAxis.isChanged())
					tgr.setDefaults(yAxis);
			}
			// log.debug(value);
		} else if (SELECTREACTIONS.equals(arg)) {
			ArrayList<TRxn> possibleReactions = new ArrayList<TRxn>();
			ArrayList<TRxn> selectedReactions = new ArrayList<TRxn>();
			Vector<TRxn> RctnList = reactions.RctnList;
			Iterator<TRxn> iter = RctnList.iterator();
			while (iter.hasNext()) {
				TRxn trxn = iter.next();
				if (trxn.Possible) {
					if (trxn.Selected)
						selectedReactions.add(trxn);
					else
						possibleReactions.add(trxn);
				}
			}
			String value = SelectReactionsDlg.showDialog(frame, possibleReactions, selectedReactions);
			if (value != null) {
				if (equil.Rowi == null)
					equil.Rowi = new Vector<TRxn>();
				else
					equil.Rowi.removeAllElements();
				iter = selectedReactions.iterator();
				while (iter.hasNext()) {
					TRxn trxn = iter.next();
					equil.Rowi.addElement(trxn);
					// log.debug(trxn.NR + ":" + trxn.Selected + ":" + trxn.Possible);
				}
				TRxns.NumSelected = selectedReactions.size();
			}
			// log.debug(value);
		} else if (GRAPH.equals(arg)) {
			tgr.setGraphTitle(title.getText());
			tgr.setVisible(true);
		} else if (ABOUT.equals(arg)) {
			ImageIcon icon = createImageIcon("/images/quilf.png", "QUIlF");
			JOptionPane.showMessageDialog(frame, "QUIlF\nversion " + VERSION + "\nCopyright(c) David J. Andersen (2008)", "About", JOptionPane.INFORMATION_MESSAGE, icon);

		} else if (HELP.equals(arg) || HELPCONTENTS.equals(arg) || WHATSNEW.equals(arg)) {
			if (helpFrame == null) {
				helpFrame = new HelpFrame();
			}
			helpFrame.setVisible(true);
			if (WHATSNEW.equals(arg)) {
				helpFrame.setContents(HelpFrame.WHATSNEW);
			}
		} else if (PRINT.equals(arg)) {
			print();
		} else if (PAGESETUP.equals(arg)) {
			pageSetUp();
		}
	}
	private HelpFrame helpFrame;

	private void pageSetUp() {
		// to be implemented
	}

	private void print() {
		JFrame frame2 = new JFrame();
		Container con2 = frame2.getContentPane();

		PrintPreviewS pp = new PrintPreviewS(panel);
		con2.add(pp, BorderLayout.CENTER);
		frame2.pack();
		frame2.setVisible(true);
		frame2.toFront();

	}

	protected void addButtons(JToolBar toolBar) {
		JButton button = null;

		button = makeNavigationButton("/general/New24", NEW, "new", "New");
		toolBar.add(button);
		button = makeNavigationButton("/general/Open24", OPEN, "open", "Open");
		toolBar.add(button);
		button = makeNavigationButton("/general/Save24", SAVE, "save", "Save");
		toolBar.add(button);
		button = makeNavigationButton("/general/Print24", PRINT, "print", "Print");
		toolBar.add(button);
		// button = makeNavigationButton("/general/PageSetup24", PAGESETUP, "pageSetup", "Page Setup");
		// toolBar.add(button);
		button = makeNavigationButton("/general/Help24", HELP, "help", "HELP");
		toolBar.add(button);
		button = makeNavigationButton("/general/Refresh24", RUN, "run", "Run");
		toolBar.add(button);
	}

	private void addData() {
		DataRow dataRow = new DataRow();
		TPhase pComp = (TPhase) phases.elementAt(TSln.Rctns);
		dataRow.setValue(DataRow.TC, pComp.getTk() - 273.15);
		dataRow.setValue(DataRow.PBAR, pComp.getP());
		pComp = (TPhase) phases.elementAt(TSln.Oxygen);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.FO2, pComp.xCalcComposition[TOxygen.fO2] / TPhase.LOG10);
			dataRow.setValue(DataRow.DFMQ, pComp.xCalcComposition[TOxygen.DFMQ] / TPhase.LOG10);
		}
		pComp = (TPhase) phases.elementAt(TSln.Sp);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.NTI, pComp.xCalcComposition[TSpinel.nTi]);
			dataRow.setValue(DataRow.NMG, pComp.xCalcComposition[TSpinel.nMg]);
			dataRow.setValue(DataRow.NMN, pComp.xCalcComposition[TSpinel.nMn]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Ilm);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.XIL, pComp.xCalcComposition[TIlm.XIl]);
			dataRow.setValue(DataRow.XHEM, pComp.xCalcComposition[TIlm.XHem]);
			dataRow.setValue(DataRow.XGK, pComp.xCalcComposition[TIlm.XGk]);
			dataRow.setValue(DataRow.XPY, pComp.xCalcComposition[TIlm.XPy]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Ol);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.XFO, pComp.xCalcComposition[TOlivine.XMg]);
			dataRow.setValue(DataRow.XFA, pComp.xCalcComposition[TOlivine.YCa]);
			dataRow.setValue(DataRow.XLA, pComp.xCalcComposition[TOlivine.XFs]);
			dataRow.setValue(DataRow.XFEOL, pComp.xCalcComposition[TOlivine.XFe]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Aug);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.XENAUG, pComp.xCalcComposition[TPx.XMg]);
			dataRow.setValue(DataRow.XWOAUG, pComp.xCalcComposition[TPx.YCa]);
			dataRow.setValue(DataRow.XFSAUG, pComp.xCalcComposition[TPx.XFs]);
			dataRow.setValue(DataRow.XFEAUG, pComp.xCalcComposition[TPx.XFe]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Pig);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.XENPIG, pComp.xCalcComposition[TPx.XMg]);
			dataRow.setValue(DataRow.XWOPIG, pComp.xCalcComposition[TPx.YCa]);
			dataRow.setValue(DataRow.XFSPIG, pComp.xCalcComposition[TPx.XFs]);
			dataRow.setValue(DataRow.XFEPIG, pComp.xCalcComposition[TPx.XFe]);
		}
		if (pComp.isPhasePresent()) {
			pComp = (TPhase) phases.elementAt(TSln.Opx);
			dataRow.setValue(DataRow.XENOPX, pComp.xCalcComposition[TPx.XMg]);
			dataRow.setValue(DataRow.XWOOPX, pComp.xCalcComposition[TPx.YCa]);
			dataRow.setValue(DataRow.XFSOPX, pComp.xCalcComposition[TPx.XFs]);
			dataRow.setValue(DataRow.XFEOPX, pComp.xCalcComposition[TPx.XFe]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Quartz);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.ASIO2, pComp.xCalcComposition[TQuartz.AQtz]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Iron);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.AFE, pComp.xCalcComposition[TIron.aFe]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Rutile);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.ATIO2, pComp.xCalcComposition[TRutile.aTiO2]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Titanite);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.ACATISIO5, pComp.xCalcComposition[TSphene.ACaTiSiO5]);
		}
		pComp = (TPhase) phases.elementAt(TSln.Karooite);
		if (pComp.isPhasePresent()) {
			dataRow.setValue(DataRow.AMGTI2O5, pComp.xCalcComposition[TKarooite.AMgTi2O5]);
		}
		dataWindow.addDataRow(dataRow);
	}

	private void addMenuItem(JMenu menu, String name, int keyEvent, String accessibleDescription) {
		JMenuItem menuItem = new JMenuItem(name, keyEvent);
		menuItem.getAccessibleContext().setAccessibleDescription(accessibleDescription);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.ALT_MASK));
		menu.add(menuItem);
		menuItem.addActionListener(this);
	}

	private void addMenuItem(JMenu menu, String name, String accessibleDescription) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.getAccessibleContext().setAccessibleDescription(accessibleDescription);
		menu.add(menuItem);
		menuItem.addActionListener(this);
	}

	private void calcAct() {
		/* int err = */dGDeriv();
	}

	private void calcTests() {
		/* int err = */dGDeriv();
	}

	private int calcQuilf() {
		/*
		 * Calls CalculateQuilf. if findbest checks sumsq, sets xb=xc. Sets quilfsuccess true or false depending on any
		 * error in CalculateQuilf. Error conditions: st<>found.
		 */
		double sumSQ;
		int status;
		int phase;

		err = false;
		status = calculateQuilf();
		if (status == TErrors.Abort_Err) {
			abort = true;
		} else {
			err = (status != TErrors.Found);
			if (!err && findBest) {
				sumSQ = 0.0;
				for (phase = 0; phase < phases.size(); phase++) {
					TPhase pComp = (TPhase) phases.elementAt(phase);
					if (pComp.isPhasePresent()) {
						sumSQ = sumSQ + pComp.sumSq();
					}
				}
				if (sumSQ < bestFit) {
					for (phase = 0; phase < phases.size(); phase++) {
						TPhase pComp = (TPhase) phases.elementAt(phase);
						pComp.copyToBestFit();
					}
					bestFit = sumSQ;
				}
			}
			quilfSuccess = !err;
			if (quilfSuccess) {
				quilfFound = true;
			}
			// if (Echo) PrintQuilf ();
		}
		return status;
	}

	private int calculateQuilf() {
		/*
		 * Find the compositional variables using least-squares or simultaneous equations. If debug is true, print
		 * intermediate compositions.
		 */

		int iter, i, nCols, nRows, j;
		double x[] = new double[TRows.MAXCOLS];
		double e[] = new double[TRows.MAXCOLS];
		int err, lsqErr;
		int ph;
		int iX;
		double newX, tk, p;

		int state = TErrors.Iterating;
		iter = 1;
		err = TErrors.No_Err;
		lsqErr = TErrors.No_Err;
		nCols = equil.ColJ.size();
		nRows = equil.Rowi.size();
		for (ph = 0; ph < phases.size(); ph++) {
			TPhase PComp = (TPhase) phases.elementAt(ph);
			PComp.clearErrors();
		}
		for (i = 0; i < TRows.MAXCOLS; i++) {
			x[i] = 0.0;
			e[i] = 0.0;
		}
		setTP();
		tk = rctn.getTk();
		p = rctn.getP();
		err = solution.standardStates(tk, p);
		err = solution.dU0(tk, p);
		/* if (EqnSel == null) */eqnSel = new TRows[nRows]; // because NRows may change
		TGauss gauss = new TGauss(nRows, eqnSel);
		while (state == TErrors.Iterating) {
			err = dGDeriv(); // Phases_Present, Phases_Variable);
			if (err == TErrors.No_Err) {
				formEqns(equil, eqnSel);
			} else {
				state = TErrors.Site_Err;
				showStatus("site error", state);
			}
			if (state == TErrors.Iterating) {
				if (debug) {
					for (ph = 0; ph < phases.size(); ph++) {
						TPhase PComp = (TPhase) phases.elementAt(ph);
						PComp.setCalcValues();
						PComp.setErrValues();
					}
				}
				lsqErr = gauss.leastSquares(nCols, nRows, eqnSel, x, e);
				if (lsqErr != TErrors.No_Err) {
					state = TErrors.Lsq_Err;
					showStatus("Least squares error", state);
				} else {
					state = TErrors.Found;
					for (i = 0; i < nCols; i++) {
						if ((Math.abs(x[i])) > XTOL) {
							state = TErrors.Iterating;
						}
					}
					for (i = 0; i < equil.ColJ.size(); i++) { // to avoid wild swings in
						TVar v = (TVar) equil.ColJ.elementAt(i);
						TPhase PComp = v.phase; // calculated variables
						iX = v.Ix;
						newX = PComp.getX(iX);
						if (Math.abs(newX) > TPhase.ZERO) {
							if (Math.abs(x[i] / newX) > MAXCHANGE) {
								state = TErrors.Iterating;
								while (Math.abs(x[i] / newX) > MAXCHANGE) {
									for (j = 0; j < nCols; j++) {
										x[j] = x[j] / 2.0;
									}
								}
							}
							// ?? why? PComp.setX (Ix, NewX);
						}
					}
					for (i = 0; i < nCols; i++) {
						TVar v = (TVar) equil.ColJ.elementAt(i);
						TPhase PComp = v.phase;
						iX = v.Ix;
						PComp.changeX(iX, x[i]);
						if (debug) {
							PComp.setErrI(iX, e[i]);
						}
					}
					if (rctn.isPhaseVariable()) {
						setTP();
					}
					if (state == TErrors.Iterating) {
						iter = iter + 1;
						if (iter > MaxIt) {
							state = TErrors.Limit;
							showStatus("iteration limit", state);
						} else if (!afContinueCalc) {
							state = TErrors.Abort_Err;
							abort = true;
						}
					}
				}
			}
		} // while st = iterating

		if (state == TErrors.Found) {
			for (i = 0; i < nCols; i++) {
				TVar v = (TVar) equil.ColJ.elementAt(i);
				TPhase PComp = v.phase;
				iX = v.Ix;
				PComp.setErrI(iX, e[i]);
			}
			for (ph = 0; ph < phases.size(); ph++) {
				TPhase PComp = (TPhase) phases.elementAt(ph);
				PComp.setCalcValues();
				PComp.setErrValues();
			}
			if (tgr != null) {
				double xValue = getValue(xAxis.getIndex());
				double yValue = getValue(yAxis.getIndex());
				// log.debug("adding " + xValue + ":" + yValue);
				tSpline.addPoint(xValue, yValue);
			}
			if (dataWindow != null)
				addData();
			if (solutionFrame != null) {
				updateSolution();
			}
			if (activityFrame != null) {
				updateActivities();
			}
			if (testFrame != null) {
				updateTests();
			}
		} else {
			clearCalc();
			// SendMessage (pcp.hwnd, WM_DRAW, NULL, NULL);
		}
		return state;
	}

	private boolean changed() {
		// if (log.isDebugEnabled())
		// return true;
		for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
			TPhase tPhase = phases.elementAt(ph);
			Vector<TRow> components = tPhase.components;
			Iterator<TRow> iter = components.iterator();
			while (iter.hasNext()) {
				TRow tRow = iter.next();
				// / if (tRow.isPresent()) {
				if (tRow.hasChanged())
					return true;
				// }
			}
		}
		return false;
	}

	private void clearCalc() {
		int Ph;

		for (Ph = 0; Ph < phases.size(); Ph++) {
			TPhase PComp = (TPhase) phases.elementAt(Ph);
			PComp.clearCalc();
		}
	}

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			log.error("Couldn't find file: " + path);
			return null;
		}
	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("File");
		menuBar.add(menu);

		addMenuItem(menu, NEW, KeyEvent.VK_C, "Clear the values");
		addMenuItem(menu, OPEN, KeyEvent.VK_O, "Open a file");
		addMenuItem(menu, SAVE, KeyEvent.VK_S, "Save to a file");
		addMenuItem(menu, SAVEAS, KeyEvent.VK_A, "Save to a file");
		menu.addSeparator();
		addMenuItem(menu, PRINT, KeyEvent.VK_P, "Print");
		// addMenuItem(menu, PAGESETUP, "Page Set up");
		menu.addSeparator();
		addMenuItem(menu, EXIT, KeyEvent.VK_X, "Exit");

		menu = new JMenu("LSQ");
		menu.setMnemonic(KeyEvent.VK_L);
		menu.getAccessibleContext().setAccessibleDescription("LSQ");
		addMenuItem(menu, RUN, "Start");
		menuBar.add(menu);

		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription("Edit");
		addMenuItem(menu, AXESDLG, KeyEvent.VK_X, "Select Axes...");
		menuItem = new JMenuItem(SELECTREACTIONS);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		submenu = new JMenu("Weight Percent");
		submenu.setMnemonic(KeyEvent.VK_W);
		addMenuItem(submenu, AUG, "Aug");
		addMenuItem(submenu, ILMENITE, "Ilmenite...");
		addMenuItem(submenu, OLIVINE, "Olivine...");
		addMenuItem(submenu, OPX, "Opx...");
		addMenuItem(submenu, PIG, "Pig...");
		addMenuItem(submenu, SPINEL, "Spinel...");
		menu.add(submenu);
		menuBar.add(menu);

		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("View");
		menuBar.add(menu);
		addMenuItem(menu, ACTIVITIES, KeyEvent.VK_A, "Activities");
		addMenuItem(menu, DATA, KeyEvent.VK_D, "Data");
		addMenuItem(menu, GRAPH, KeyEvent.VK_G, "Graph");
		addMenuItem(menu, REACTIONS, KeyEvent.VK_R, "Reactions");
		addMenuItem(menu, SOLUTION, KeyEvent.VK_S, "Solution");
		addMenuItem(menu, TEST, KeyEvent.VK_T, "Test");

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Help");
		menuBar.add(menu);
		addMenuItem(menu, HELPCONTENTS, "Help Contents");
		menu.addSeparator();
		addMenuItem(menu, WHATSNEW, "What's New");
		menu.addSeparator();
		addMenuItem(menu, ABOUT, "About");
		return menuBar;
	}

	private int dGDeriv() { // boolean Phases_Present[], boolean Phases_Variable[]) {
		/*
		 * Calculate the derivatives for the phases. Sets Error if px/ol routines fail otherwise returns Error=no_Err.
		 * If phase is present, then activity routines are always called because for/next variables are not uniquely
		 * identified.
		 */
		int err;
		int Ph;
		double A[] = new double[TPhase.MAXCOMPONENTS];

		err = TErrors.No_Err;
		int Error = TErrors.No_Err;
		if (rctn.isPhaseVariable()) {
			err = solution.standardStates(rctn.tk, rctn.p);
			err = solution.dU0(rctn.tk, rctn.p);
		}
		for (Ph = TSln.Ol; Ph < phases.size(); Ph++) {
			TPhase phase = (TPhase) phases.elementAt(Ph);
			if (phase.isPhasePresent()) {
				if (rctn.isPhaseVariable()) {
					err = TSln.Slns[Ph].actdTdP();
				} else {
					err = TSln.Slns[Ph].act();
				}
				if (phase.isPhaseVariable()) {
					err = TSln.Slns[Ph].deriv(A);
				}
				if (err != TErrors.No_Err) {
					Error = err;
				}
			}
		}
		return Error;
	}

	private void formEqn(Vector<TVar> Vl, TRxn Ri, TRows Row) {
		int J, k, n;
		double Delta;
		int Ix;
		TPhase Ph;
		int C;
		double X;

		Delta = 0.0;
		n = Ri.RN;
		for (J = 0; J < n; J++) {
			X = Ri.RC[J].X;
			Ph = Ri.RC[J].Ph;
			C = Ri.RC[J].C;
			Delta = Delta - X * (Ph.u0Array[TSln.G][C] + Ph.activities[C]);
		}
		Row.B = Delta;
		for (k = 0; k < Vl.size(); k++) {
			TVar v = (TVar) Vl.elementAt(k);
			Ix = v.Ix;
			Delta = 0.0;
			if (v.phase == rctn) {
				if (Ix == TRctn.Temperature) {
					for (J = 0; J < n; J++) {
						X = Ri.RC[J].X;
						Ph = Ri.RC[J].Ph;
						C = Ri.RC[J].C;
						Delta = Delta + X * (Ph.u0Array[TSln.S][C] + Ph.dAdT[C]);
					}
				} else if (Ix == TRctn.Pressure) {
					for (J = 0; J < n; J++) {
						X = Ri.RC[J].X;
						Ph = Ri.RC[J].Ph;
						C = Ri.RC[J].C;
						Delta = Delta + X * (Ph.u0Array[TSln.V][C] + Ph.dAdP[C]);
					}
				}
			} else {
				for (J = 0; J < n; J++) {
					X = Ri.RC[J].X;
					Ph = Ri.RC[J].Ph;
					C = Ri.RC[J].C;
					if (Ph == v.phase) {
						Delta = Delta + X * Ph.dX[Ix][C];
					}
				}
			}
			Row.Coeff[k] = Delta;
		}
	}

	/**
	 * @param Equil
	 *            EqIJ
	 * @param Eqn
	 *            Rows[]
	 */
	private void formEqns(TEqIJ Equil, TRows Eqn[]) {
		int I;
		TRxn Temp;
		Vector<TVar> Temp2;

		Temp2 = Equil.ColJ;
		for (I = 0; I < Equil.Rowi.size(); I++) {
			Temp = (TRxn) Equil.Rowi.elementAt(I);
			formEqn(Temp2, Temp, Eqn[I]);
		}
	}

	private boolean getComp() {
		int phase;
		boolean compFound, ok;
		double tk, p;

		findBest = false;
		compFound = true;
		quilfFound = false;
		if (forNextVariables == null) {
			forNextVariables = new Vector<TVar>(10);
		} else {
			forNextVariables.removeAllElements();
		}
		tk = 0.0;
		p = 0.0;
		phase = TSln.Rctns;
		if (equil.ColJ == null) {
			equil.ColJ = new Vector<TVar>(10);
		} else {
			equil.ColJ.removeAllElements();
		}
		for (phase = 0; phase < phases.size(); phase++) {
			TPhase pComp = (TPhase) phases.elementAt(phase);
			pComp.setPhasePresent(false);
			pComp.setPhaseVariable(false);
			ok = pComp.getComposition(forNextVariables, equil.ColJ);
			if (!ok) {
				compFound = false;
				break;
			}
		}
		if (compFound) {
			// TRctn rctn = (TRctn) phases.elementAt(TSolution.Rctns);
			rctn.resetInitial();
			setTP();
			tk = rctn.getTk();
			p = rctn.getP();
			int err = solution.standardStates(tk, p);
			if (err != TErrors.No_Err) {
				showStatus("Error in StandardState Routines", err);
				compFound = false;
			} else {
				for (phase = TSln.Ol; phase < phases.size(); phase++) {
					TPhase pComp = (TPhase) phases.elementAt(phase);
					pComp.resetInitial();
				}
			}
		}
		return compFound;
	}

	private int getSelected(Vector<TRxn> list) {
		int I;
		TRxn pr;

		for (I = 0; I < TRxns.RctnList.size(); I++) {
			pr = (TRxn) TRxns.RctnList.elementAt(I);
			if (pr != null) {
				if (pr.Selected) {
					if (list == null) {
						list = new Vector<TRxn>(10);
					}
					list.addElement(pr);
				}
			}
		}
		if (list == null) {
			return 0;
		}
		return (list.size());
	}

	private double getValue(int i) {
		double value = 0;
		TPhase pComp = null;
		switch (i) {
		case DataRow.TC:
			pComp = (TPhase) phases.elementAt(TSln.Rctns);
			value = pComp.getTk() - 273.15;
			break;
		case DataRow.PBAR:
			pComp = (TPhase) phases.elementAt(TSln.Rctns);
			value = pComp.getP();
			break;
		case DataRow.FO2:
			pComp = (TPhase) phases.elementAt(TSln.Oxygen);
			value = pComp.xCalcComposition[TOxygen.fO2] / TPhase.LOG10;
			break;
		case DataRow.DFMQ:
			pComp = (TPhase) phases.elementAt(TSln.Oxygen);
			value = pComp.xCalcComposition[TOxygen.DFMQ] / TPhase.LOG10;
			break;
		case DataRow.NTI:
		case DataRow.NMG:
		case DataRow.NMN:
			pComp = (TPhase) phases.elementAt(TSln.Sp);
			if (i == DataRow.NTI)
				value = pComp.xCalcComposition[TSpinel.nTi];
			else if (i == DataRow.NMG)
				value = pComp.xCalcComposition[TSpinel.nMg];
			else if (i == DataRow.NMN)
				value = pComp.xCalcComposition[TSpinel.nMn];
			break;
		case DataRow.XIL:
		case DataRow.XHEM:
		case DataRow.XGK:
		case DataRow.XPY:
			pComp = (TPhase) phases.elementAt(TSln.Ilm);
			if (i == DataRow.XIL)
				value = pComp.xCalcComposition[TIlm.XIl];
			else if (i == DataRow.XHEM)
				value = pComp.xCalcComposition[TIlm.XHem];
			else if (i == DataRow.XGK)
				value = pComp.xCalcComposition[TIlm.XGk];
			else if (i == DataRow.XPY)
				value = pComp.xCalcComposition[TIlm.XPy];
			break;

		case DataRow.XFO:
		case DataRow.XLA:
		case DataRow.XFA:
		case DataRow.XFEOL:
			pComp = (TPhase) phases.elementAt(TSln.Ol);
			if (i == DataRow.XFO)
				value = pComp.xCalcComposition[TOlivine.XMg];
			else if (i == DataRow.XFA)
				value = pComp.xCalcComposition[TOlivine.YCa];
			else if (i == DataRow.XLA)
				value = pComp.xCalcComposition[TOlivine.XFs];
			else if (i == DataRow.XFEOL)
				value = pComp.xCalcComposition[TOlivine.XFe];
			break;
		case DataRow.XENAUG:
		case DataRow.XWOAUG:
		case DataRow.XFSAUG:
		case DataRow.XFEAUG:
			pComp = (TPhase) phases.elementAt(TSln.Aug);
			if (i == DataRow.XENAUG)
				value = pComp.xCalcComposition[TPx.XMg];
			else if (i == DataRow.XWOAUG)
				value = pComp.xCalcComposition[TPx.YCa];
			else if (i == DataRow.XFSAUG)
				value = pComp.xCalcComposition[TPx.XFs];
			else if (i == DataRow.XFEAUG)
				value = pComp.xCalcComposition[TPx.XFe];
			break;
		case DataRow.XENPIG:
		case DataRow.XWOPIG:
		case DataRow.XFSPIG:
		case DataRow.XFEPIG:
			pComp = (TPhase) phases.elementAt(TSln.Pig);
			if (i == DataRow.XENPIG)
				value = pComp.xCalcComposition[TPx.XMg];
			else if (i == DataRow.XWOPIG)
				value = pComp.xCalcComposition[TPx.YCa];
			else if (i == DataRow.XFSPIG)
				value = pComp.xCalcComposition[TPx.XFs];
			else if (i == DataRow.XFEPIG)
				value = pComp.xCalcComposition[TPx.XFe];
			break;
		case DataRow.XENOPX:
		case DataRow.XWOOPX:
		case DataRow.XFSOPX:
		case DataRow.XFEOPX:
			pComp = (TPhase) phases.elementAt(TSln.Opx);
			if (i == DataRow.XENOPX)
				value = pComp.xCalcComposition[TPx.XMg];
			else if (i == DataRow.XWOOPX)
				value = pComp.xCalcComposition[TPx.YCa];
			else if (i == DataRow.XFSOPX)
				value = pComp.xCalcComposition[TPx.XFs];
			else if (i == DataRow.XFEOPX)
				value = pComp.xCalcComposition[TPx.XFe];
			break;
		case DataRow.ASIO2:
			pComp = (TPhase) phases.elementAt(TSln.Quartz);
			value = pComp.xCalcComposition[TQuartz.AQtz];
			break;
		case DataRow.AFE:
			pComp = (TPhase) phases.elementAt(TSln.Iron);
			value = pComp.xCalcComposition[TIron.aFe];
			break;
		case DataRow.ATIO2:
			pComp = (TPhase) phases.elementAt(TSln.Rutile);
			value = pComp.xCalcComposition[TRutile.aTiO2];
			break;
		case DataRow.ACATISIO5:
			pComp = (TPhase) phases.elementAt(TSln.Titanite);
			value = pComp.xCalcComposition[TSphene.ACaTiSiO5];
			break;
		case DataRow.AMGTI2O5:
			// pComp = (TPhase) phases.elementAt(TPhase.);
			break;
		}
		return value;
	}

	public JPanel initializePanels(TWeight TPx, String title) {
		JPanel p = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		p.setLayout(gridbag);
		JPanel wt = TPx.wtptPanel(title);
		JPanel f = TPx.formulaPanel();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		gridbag.setConstraints(wt, c);
		p.add(wt);
		c.gridwidth = GridBagConstraints.REMAINDER; // end of row
		gridbag.setConstraints(f, c);
		p.add(f);
		JPanel component = TPx.componentPanel();
		JPanel quilf = TPx.quilfPanel();
		c.gridwidth = 1;
		gridbag.setConstraints(component, c);
		p.add(component);
		c.gridwidth = GridBagConstraints.REMAINDER; // end of row
		gridbag.setConstraints(quilf, c);
		p.add(quilf);
		return p;
	}

	public void initializeScreen() {
		panel = new TQPanel();
		tb = new TToolBar();
		panel.add(tb, 0, 0);
		title = new TTextF("", 20);
		panel.add(new TField(new TTextLab("Title"), title), 1, 0); // 1, 0);
		panel.add(new TField(new TPlainL("Input"), new TPlainL("Calculated"), new TPlainL("Uncertainty")), 2, 0); // 2,
		// 0);
		phases = new Vector<TPhase>(10);
		for (int i = 0; i < TSln.NUMPH; i++) {
			phases.addElement(TSln.Slns[i]);
		}
		rctn = (TRctn) TSln.Slns[TSln.Rctns];
		rctn.setRows(panel, 3);
		TSln.Slns[TSln.Oxygen].setRows(panel, 5);
		TSln.Slns[TSln.Sp].setRows(panel, 7);
		TSln.Slns[TSln.Ilm].setRows(panel, 11);
		TSln.Slns[TSln.Ol].setRows(panel, 16);

		// panel.add(new Field(new TextLabel(""), null, new TextLabel( "Input"),new TextLabel("calc") ,new TextLabel(
		// "err")), 0, 1);
		panel.add(new TField(new TPlainL("Input"), new TPlainL("Calculated"), new TPlainL("Uncertainty")), 0, 1);
		TSln.Slns[TSln.Aug].setRows("Augite", panel, 1);
		TSln.Slns[TSln.Pig].setRows("Pigeonite", panel, 6);
		orthopyroxene = (TOpx) TSln.Slns[TSln.Opx];
		orthopyroxene.setRows("Orthopyroxene", panel, 11);
		panel.add(new TField(new TLabel("Activities")), 16, 1);
		TSln.Slns[TSln.Quartz].setRows(panel, 17);
		TSln.Slns[TSln.Iron].setRows(panel, 18);
		TSln.Slns[TSln.Rutile].setRows(panel, 19);
		TSln.Slns[TSln.Titanite].setRows(panel, 20);
		TSln.Slns[TSln.Karooite].setRows(panel, 21);
		TPhase pComp = (TPhase) phases.elementAt(TSln.Ilm);
		TRow pC0 = (TRow) pComp.components.elementAt(1);
		TRow pC1 = (TRow) pComp.components.elementAt(2);
		TRow pC2 = (TRow) pComp.components.elementAt(3);
		ilWtPt = new TIlWtPt(pC0.getInput(), pC1.getInput(), pC2.getInput());
		pComp = (TPhase) phases.elementAt(TSln.Sp);
		pC0 = (TRow) pComp.components.elementAt(0);
		pC1 = (TRow) pComp.components.elementAt(1);
		pC2 = (TRow) pComp.components.elementAt(2);
		spWtPt = new TSpWtPt(pC0.getInput(), pC1.getInput(), pC2.getInput());

		pComp = (TPhase) phases.elementAt(TSln.Ol);
		pC0 = (TRow) pComp.components.elementAt(0);
		pC1 = (TRow) pComp.components.elementAt(1);
		olWtPt = new TOlWtPt(pC0.getInput(), pC1.getInput());
		pComp = (TPhase) phases.elementAt(TSln.Aug);
		pC0 = (TRow) pComp.components.elementAt(0);
		pC1 = (TRow) pComp.components.elementAt(1);
		augWtPt = new TAugWtPt(pC1.getInput(), pC0.getInput());
		pComp = (TPhase) phases.elementAt(TSln.Opx);
		pC0 = (TRow) pComp.components.elementAt(0);
		pC1 = (TRow) pComp.components.elementAt(1);
		opxWtPt = new TOpxWtPt(pC1.getInput(), pC0.getInput());

		pComp = (TPhase) phases.elementAt(TSln.Pig);
		pC0 = (TRow) pComp.components.elementAt(0);
		pC1 = (TRow) pComp.components.elementAt(1);
		pigWtPt = new TPigWtPt(pC1.getInput(), pC0.getInput());
		xAxis = new Axis(DataRow.TC);
		yAxis = new Axis(DataRow.DFMQ);
		tgr = new TGR(title.getText(), xAxis, yAxis);
		tgr.setDefaults(xAxis);
		tgr.setDefaults(yAxis);

		GridLayout grid = new GridLayout(22, 2, 0, 0);
		panel.setLayout(grid);
		KeyListener keyListener = new KeyListener() {

			public void keyPressed(KeyEvent keyEvent) {
				// use keyReleased
			}

			public void keyReleased(KeyEvent keyEvent) {
				//
				int keyCode = keyEvent.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_KP_DOWN:
				case KeyEvent.VK_DOWN:
					moveDown();
					break;
				case KeyEvent.VK_KP_UP:
				case KeyEvent.VK_UP:
					moveUp();
					break;
				case KeyEvent.VK_TAB:
					moveTab();
					break;
				default:
					break;
				}
			}

			public void keyTyped(KeyEvent keyEvent) {
				// use keyReleased
			}
		};

		for (int r = 0; r < TQPanel.MAXROWS; r++) {
			for (int c = 0; c < TQPanel.MAXCOLS; c++) {
				JComponent component = panel.get(r, c);
				if (component == null) {
					panel.add(new JLabel());
				} else {
					if (component instanceof TField) {
						TField field = (TField) component;
						for (int i = 0; i < field.getComponentCount(); i++) {
							Component comp = field.getComponent(i);
							if ((comp instanceof TNumFld) || (comp instanceof TTextF)) {
								comp.setFocusTraversalKeysEnabled(false);
								comp.addKeyListener(keyListener);
							}
						}
					}
					panel.add(component);
				}
			}
		}
		panel.validate();
		if (intro != null) {
			remove(intro);
		}
		add("Center", panel);
		validate();
		equil = new TEqIJ();
		// calculateButton.setEnabled(true);
		currentRow = 0;
		currentCol = -1; // moveTab starts at currentCol + 1
		while (!moveTab()) {
			// want to move to first editable square
		}
		statusLine = new JLabel();
		add("South", statusLine);
	}

	private int iterateQuilf(int fi) {
		/*
		 * Does recursive for/next loops based on forNextVariablesist, forNextVariables. forNextVariablesist is the
		 * number of entries in the vector fi which contains an index to a for/next variable. Calls calcquilf or
		 * IterateQuilf. Error conditions: CalcQuilf,
		 */
		boolean Down;
		boolean Finished;
		int xv;
		TPhase ph;
		int st = TErrors.No_Err;

		if (fi >= forNextVariables.size()) {
			st = calcQuilf();
		} else {
			TVar v = (TVar) forNextVariables.elementAt(fi);
			ph = v.phase;
			xv = v.Ix;
			// TPhase PComp = (TPhase) phases.elementAt(PH);
			ph.setX(xv, ph.xInitialComposition[xv]);
			Down = (ph.xStep[xv] < 0.0);
			Finished = false;
			if (tgr != null) {
				tgr.redraw();
				tSpline = new TSpline();
				tgr.addObject(tSpline);
			}
			do {
				if (fi < forNextVariables.size()) {
					st = iterateQuilf(fi + 1);
				} else {
					calcQuilf();
				}
				if (abort || err) {
					return st;
				}
				ph.setX(xv, ph.xCalcComposition[xv] + ph.xStep[xv]);
				if (Down) {
					Finished = (ph.xCalcComposition[xv] < ph.xFinalComposition[xv]);
				} else {
					Finished = (ph.xCalcComposition[xv] > ph.xFinalComposition[xv]);
				}
			} while (!Finished);
			ph.setX(xv, ph.xFinalComposition[xv]); // why reset it
		}
		return st;
	}

	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {
		// Look for the image.
		String imgLocation = "/toolbarButtonGraphics" + imageName + ".gif";
		URL imageURL = Quilf.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			log.error("Resource not found: " + imgLocation);
		}

		return button;
	}

	/**
	 * pressed down key, move to nearest TNumField, if reaches the bottom, start at the top
	 */
	public boolean moveDown() {
		for (int i = currentRow + 1; i < TQPanel.MAXROWS; i++) {
			if (moveTo(i, currentCol)) {
				return true;
			}
		}
		// start at the top
		for (int i = 0; i < currentRow; i++) {
			if (moveTo(i, currentCol)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * pressed tab key, move to nearest TNumField (left to right), if reaches the bottom, start at the top
	 */
	public boolean moveTab() {
		int startCol = currentCol + 1;
		for (int r = currentRow; r < TQPanel.MAXROWS; r++) {
			for (int c = startCol; c < TQPanel.MAXCOLS; c++) {
				if (moveTo(r, c)) {
					return true;
				}
			}
			startCol = 0;
		}
		// start at the top
		for (int r = 0; r < TQPanel.MAXROWS; r++) {
			for (int c = 0; c < TQPanel.MAXCOLS; c++) {
				if (moveTo(r, c)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean moveTo(int r, int c) {
		JComponent x = panel.get(r, c);
		if (x instanceof TField) {
			TField tf = (TField) x;
			if (tf != null) {
				if (tf.getTf() != null) {
					setCurrent(r, c);
					tf.getTf().requestFocusInWindow();
					return true;
				} else if (tf.getTtf() != null) {
					setCurrent(r, c);
					tf.getTtf().requestFocusInWindow();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * pressed up key, move up to nearest TNumField, if reaches the top, start at the bottom
	 */
	public boolean moveUp() {
		for (int i = currentRow - 1; i > 0; i--) {
			if (moveTo(i, currentCol)) {
				return true;
			}
		}
		// start at the bottom
		for (int i = TQPanel.MAXROWS - 1; i > currentRow; i--) {
			if (moveTo(i, currentCol)) {
				return true;
			}
		}
		return false;
	}

	private int possibleReactions() {
		int Err;
		int i, NumPoss;
		TRxn Pr;
		Err = TErrors.No_Err;
		NumPoss = 0;
		if (!rctn.isPhaseVariable()) {
			Err = solution.standardStates(rctn.tk, rctn.p);
			Err = solution.dU0(rctn.tk, rctn.p);
		}
		Err = dGDeriv(); // Phases_Present, Phases_Variable);
		if (Err != TErrors.No_Err) {
			showStatus("Error in calculating the derivatives", Err);
			return 0;
		} else {
			for (i = 0; i < TRxns.RctnList.size(); i++) {
				Pr = (TRxn) TRxns.RctnList.elementAt(i);
				Pr.Selected = false;
				Pr.Possible = false;
				if (testReaction(Pr)) {
					Pr.Possible = true;
					NumPoss++;
				}
			}
		}
		return (NumPoss);
	}

	private boolean readCells(File file) {
		boolean success = true;
		try {
			BufferedReader pw = new BufferedReader(new FileReader(file));
			String s = pw.readLine();
			title.setText(s);
			for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
				TPhase pComp = (TPhase) phases.elementAt(ph);
				if (!pComp.readComposition(pw)) {
					log.error("Error reading :" + pComp.getPhaseName());
					success = false;
				}
			}
			if (!ilWtPt.read(pw)) {
				success = false;
			}
			if (!spWtPt.read(pw)) {
				success = false;
			}
			if (!olWtPt.read(pw)) {
				success = false;
			}
			if (!augWtPt.read(pw)) {
				success = false;
			}
			if (!pigWtPt.read(pw)) {
				success = false;
			}
			if (!opxWtPt.read(pw)) {
				success = false;
			}
			pw.close();

		} catch (FileNotFoundException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
		return (success);

	}

	public void reset() {
		// calculateButton.setText(RESETLABEL);
	}

	public void run() {
		/*
		 * Check for error conditions based on the number of variables and rows. If findbest=true then do another
		 * iteration using best values and print it.
		 */
		int St;
		int Ph;

		// printBestFit = false;
		for (Ph = 0; Ph < phases.size(); Ph++) {
			TPhase pComp = (TPhase) phases.elementAt(Ph);
			pComp.resetInitial();
		}
		bestFit = 1.0e+10;
		abort = false;
		quilfFound = false;
		if (equil.ColJ.size() == 0) {
			showStatus("no Variables", TErrors.NoVar_Err);
			afContinueCalc = false;
		} else if (equil.ColJ.size() > equil.Rowi.size()) {
			showStatus("too many variables" + equil.ColJ.size() + " not enough reactions " + equil.Rowi.size(), TErrors.TooManyVar_Err);
			clearCalc();
			afContinueCalc = false;
		} else {
			St = iterateQuilf(0);
			if (afContinueCalc) {
				if (quilfFound) {
					showStatus("Done", TErrors.No_Err);
					// calculateButton.setText(CALCULATELABEL);
				}
				if (!quilfFound) {
					showStatus(TErrors.errorStr(TErrors.NotEnough_Err), TErrors.Limit);
					clearCalc();
					afContinueCalc = false;
				} else if (findBest) { // extra iteration because
					// printBestFit = true;
					for (Ph = 0; Ph < phases.size(); Ph++) {
						TPhase PComp = (TPhase) phases.elementAt(Ph);
						PComp.setBestFit();
					}
					St = calculateQuilf();
					if (St == TErrors.Abort_Err) {
						abort = true;
						afContinueCalc = false;
					} else {
						for (Ph = 0; Ph < phases.size(); Ph++) {
							TPhase PComp = (TPhase) phases.elementAt(Ph);
							PComp.setCalcValues();
							PComp.setErrValues();
						}
					}
				}
			}
		}
		if (afContinueCalc) {
			//          
		} else {
			reset();
		}
		findQuilf = null;
	}

	private void saveValues() {
		/*
		 * copy the values
		 */
		for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
			TPhase tPhase = phases.elementAt(ph);
			Vector<TRow> components = tPhase.components;
			Iterator<TRow> iter = components.iterator();
			while (iter.hasNext()) {
				TRow tRow = iter.next();
				tRow.saveValue();
			}
		}
	}

	private void selectAll() {
		int I;
		TRxn pr;
		for (I = 0; I < TRxns.RctnList.size(); I++) {
			pr = (TRxn) TRxns.RctnList.elementAt(I);
			if (pr.Possible) {
				pr.Selected = true;
			}
		}
	}

	private int selectReactions(Vector<TRxn> List) {
		int NumRctns;
		TRxns.NumSelected = 0;
		NumRctns = possibleReactions();
		if (NumRctns > 0) {
			selectAll();
			TRxns.NumSelected = getSelected(List);
		} else {
			log.error("There are no reactions selected.");
		}
		return (TRxns.NumSelected);
	}

	private void setCurrent(int row, int col) {
		currentRow = row;
		currentCol = col;
		JComponent x = panel.get(row, col);
		x.requestFocus();
	}

	private void setTP() {
		int Ph;
		double Tk, P;

		Tk = rctn.getTk();
		P = rctn.getP();
		for (Ph = 0; Ph < phases.size(); Ph++) {
			TPhase PComp = (TPhase) phases.elementAt(Ph);
			PComp.setTP(Tk, P);
		}

	}

	private boolean testReaction(TRxn pr) {
		int I;
		for (I = 0; I < pr.RN; I++) {
			if (!pr.RC[I].Ph.isPhasePresent()) {
				return (false);
			}
			if (pr.RC[I].Ph.activities[pr.RC[I].C] <= TPhase.EXP0) {
				return (false);
			}
		}
		return (true);
	}

	private void updateActivities() {
		calcAct();
		activityTable.update(solution.Slns);
	}

	private void updateTests() {
		calcTests();
		testTable.update(reactions, solution.Slns);
	}

	private void updateSolution() {
		solutionTable.update(solution);
	}

	private boolean variablesDifferent() {
		/*
		 * Compare the current variables with those in SaveList if different then return true else
		 */
		for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
			TPhase tPhase = phases.elementAt(ph);
			Vector<TRow> components = tPhase.components;
			Iterator<TRow> iter = components.iterator();
			while (iter.hasNext()) {
				TRow tRow = iter.next();
				// if (tRow.isPresent()) {
				if (tRow.variableDifferent())
					return true;
				// }
			}
		}
		return false;
	}

	private boolean writeCells(File file) {
		boolean success = true;
		try {
			PrintWriter pw = new PrintWriter(file);
			String s = title.getText();
			if (s != null) {
				pw.println(s);
			} else {
				pw.println("");
			}
			for (int ph = TSln.Rctns; ph < TSln.NUMPH; ph++) {
				TPhase pComp = (TPhase) phases.elementAt(ph);
				if (!pComp.writeComposition(pw)) {
					log.error("Error writing :" + pComp.getPhaseName());
					success = false;
				}
			}
			if (!ilWtPt.write(pw)) {
				success = false;
			}
			if (!spWtPt.write(pw)) {
				success = false;
			}
			if (!olWtPt.write(pw)) {
				success = false;
			}
			if (!augWtPt.write(pw)) {
				success = false;
			}
			if (!pigWtPt.write(pw)) {
				success = false;
			}
			if (!opxWtPt.write(pw)) {
				success = false;
			}
			pw.close();

		} catch (FileNotFoundException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
		return (success);

	}
}
