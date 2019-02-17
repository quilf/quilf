package edu.sunysb.ess.quilf.wtpt;

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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TWeight extends JFrame implements ActionListener {

 private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TWeight.class);
    static final int Al2O3 = 1;
    static final int CaO = 8;
    private static final String CLEAR = "clear";
    static final int Cr2O3 = 7;
    static final int Fe2O3 = 3;
    static final int FeO = 4;
    private static final String FORMULA = "formula";
    private static final String CANCEL = "cancel";
    private static final String OK = "ok";
    static final String[] FormulaNames = {"Si", "Al", "Ti", "Fe3+", "Fe2+", "Mn", "Mg", "Cr3+", "Ca", "Na"};
    static final int MaxComp = 4;
    static final int MgO = 6;
    static final int MnO = 5;
    static final double[] MolWt = {60.0848, 101.961, 79.899, 159.692, 71.846, 70.9374, 40.311, 151.9901, 56.0794, 61.979};
    static final int Na2O = 9;
    static final double[] NAnions = {2.0, 3.0, 2.0, 3.0, 1.0, 1.0, 1.0, 3.0, 1.0, 1.0};
    static final double[] NCations = {1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 2.0, 1.0, 2.0};
    static final String[] OxideNames = {"SiO2", "Al2O3", "TiO2", "Fe2O3", "FeO", "MnO", "MgO", "Cr2O3", "CaO", "Na2O"};
    static final int SiO2 = 0;
    static final int TiO2 = 2;
    final static double Zero = 0.000001;
    private JButton formulaButton,  clearButton,  okButton,  cancelButton;
    private JLabel[] formulaValues;
    protected double NumSites;
    protected double OxNo;
    protected boolean[] OxSet;
    private JLabel sumLabel;
    private double[] W;
    private double[] Wt;
    private JLabel titleLabel;
    private JTextField[] wtValues;
    private double[] XC;

    public boolean write(PrintWriter pw) {
        pw.println(titleLabel.getText());
        for (int i = SiO2; i <= Na2O; i++) {
            pw.println(OxideNames[i] + "=" + Wt[i]);
        }
        return true;
    }

    public boolean read(BufferedReader pw) {
        boolean success = true;
        try {
            String s = pw.readLine();
            if (!s.equals(titleLabel.getText())) {
                success = false;
            } else {
                for (int i = SiO2; i <= Na2O; i++) {
                    wtValues[i].setText("");
                    Wt[i] = 0.0;
                }
                for (int i = SiO2; i <= Na2O; i++) {
                    String t = pw.readLine();
                    StringTokenizer st = new StringTokenizer(t, "=");
                    String l = null;
                    String v = null;
                    if (st.hasMoreTokens()) {
                        l = st.nextToken();
                    }
                    if (st.hasMoreTokens()) {
                        v = st.nextToken();
                    }
                    if (l != null) {
                        if (OxideNames[i].equals(l)) {
                            if (v != null) {
                                wtValues[i].setText(v);
                            }
                        } else {
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                }

            }
        } catch (IOException e) {
        	log.error(e.toString());
        }
        return success;
    }
    // ------------------------------ TWeightPercent ------------------------------
    public TWeight() {
        OxNo = 0;
        NumSites = 0;
        OxSet = new boolean[OxideNames.length];
        sumLabel = new JLabel("");
        Wt = new double[OxideNames.length];
        W = new double[FormulaNames.length];
        XC = new double[MaxComp];
        formulaValues = new JLabel[FormulaNames.length];
        wtValues = new JTextField[OxideNames.length];

        for (int i = SiO2; i <= Na2O; i++) {
            wtValues[i] = new JTextField("");
            wtValues[i].setBackground(Color.white);
            formulaValues[i] = new JLabel("");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (FORMULA.equals(e.getActionCommand())) {
            formula();
        } else if (CLEAR.equals(e.getActionCommand())) {
            clear();
        } else if (OK.equals(e.getActionCommand())) {
            ok();
            this.setVisible(false);
        } else if (CANCEL.equals(e.getActionCommand())) {
            cancel();
        }
    }

     void CalcComponents(double[] W, double[] XC) {
    	 // nothing to do
     }

    void CalcFormula(double[] Wt, double[] W) {
        int I;
        double Sum, Fe2, Fe3;

        Sum = 0.0;
        for (I = SiO2; I <= Na2O; I++) {
            W[I] = Wt[I] * NAnions[I] / MolWt[I];
            Sum = Sum + W[I];
        }
        if (Sum <= 0.0) {
            for (I = SiO2; I <= Na2O; I++) {
                W[I] = 0.0;
            }
            return;
        }
        Sum = OxNo / Sum;
        for (I = SiO2; I <= Na2O; I++) {
            W[I] = W[I] * Sum * NCations[I] / NAnions[I];
        }
        Sum = 0.0;
        for (I = SiO2; I <= Na2O; I++) {
            Sum = Sum + W[I];
        }
        for (I = SiO2; I <= Na2O; I++) {
            W[I] = W[I] * NumSites / Sum;
        }
        Sum = 0.0;
        for (I = SiO2; I <= Na2O; I++) {
            Sum = Sum + W[I] * NAnions[I] / NCations[I];
        }
        if ((!OxSet[Fe2O3]) || Wt[Fe2O3] != 0.0) {
            return;
        }
        if (Sum >= OxNo) {
            return;
        }
        Fe2 = W[FeO] - 2.0 * (OxNo - Sum);
        if (Fe2 < 0.0) {
            Fe2 = 0.0;
            Fe3 = W[FeO];
        } else {
            Fe3 = 2.0 * (OxNo - Sum);
        }
        W[FeO] = Fe2;
        W[Fe2O3] = Fe3;
    }

    public void ok() {
    	// nothing to do
    }

    public void cancel() {
        this.setVisible(false);
    }

    public void clear() {
        ClearData();
        DisplayComponents(null, null);
        DisplayFormulas(null);
    }

    public void ClearComponents() {
        int I;

        for (I = 0; I < MaxComp; I++) {
            XC[I] = 0.0;
        }
        for (I = SiO2; I <= Na2O; I++) {
            W[I] = 0.0;
        }
    }

    public void ClearData() {
        int I;

        for (I = SiO2; I <= Na2O; I++) {
            W[I] = 0.0;
            if (OxSet[I]) {
                formulaValues[I].setText("");
                wtValues[I].setText("");
            }
        }
        ClearComponents();
    }

    /**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.awt.Panel
	 */
    public JPanel componentPanel() {
        return null;
    }

    void DisplayComponents(double[] W, double[] XC) {
    	// nothing to do
    }

    void DisplayFormulas(double[] W) {
        int I;
        double Sum;

        if (W == null) {
            for (I = SiO2; I <= Na2O; I++) {
                formulaValues[I].setText("");
            }
            sumLabel.setText("");
        } else {
            Sum = 0.0;
            for (I = SiO2; I <= Na2O; I++) {
                if (OxSet[I]) {
                    Sum = Sum + W[I];
                    formulaValues[I].setText(format(W[I]));
                }
            }
            sumLabel.setText(format(Sum));
        }
    }

    /**
	 * This method does a really crude format.
	 * 
	 * @return java.lang.String
	 * @param X
	 *            double
	 */
    public String format(double X) {
        double absX = Math.abs(X);
        if (absX < 10.0) {
            long l = (long) Math.rint(X * 10000);
            X = (double) (l) / 10000;
        } else if (absX < 100.0) {
            long l = (long) Math.rint(X * 100);
            X = (double) (l) / 100;
        } else {
            long l = (long) Math.rint(X);
            X = (double) l;
        }
        return Double.toString(X);
    }

    /**
	 * This method was created by a SmartGuide.
	 */
    public void formula() {
        if (GetValues(Wt)) {
            CalcFormula(Wt, W);
            CalcComponents(W, XC);
            DisplayComponents(W, XC);
            DisplayFormulas(W);
        } else {
            ClearComponents();
            DisplayComponents(null, null);
            DisplayFormulas(null);
        }
        return;
    }

    /**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.awt.Panel
	 */
    public JPanel formulaPanel() {
        int n = 0;
        for (int i = SiO2; i <= Na2O; i++) {
            if (OxSet[i]) {
                n++;
            }
        }
        n += 1; // for sum
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(n + 1, 2));
        p.add(new Label("")); // blank line at top to match wt.pt.
        p.add(new Label(""));
        for (int i = SiO2; i <= Na2O; i++) {
            // formulaValues[i] = new Label("");
            if (OxSet[i]) {
                p.add(new Label(FormulaNames[i]));
                p.add(formulaValues[i]);
            }
        }
        p.add(new Label("Sum"));
        p.add(sumLabel);
        return p;
    }

    double getValue(String s) {
        if (s == null) {
            return 0;
        }
        if (s.length() == 0) {
            return 0;
        }
        try {
            Double v = new Double(s);
            return v.doubleValue();
        } catch (NumberFormatException nfe) {
        	log.error(nfe);
            return 0;
        }
    }

    boolean GetValues(double[] W) {
        double Xi;
        String x;
        int I;
        for (I = SiO2; I <= Na2O; I++) {
            if (OxSet[I]) {
                x = wtValues[I].getText();
                Xi = getValue(x);
                W[I] = Xi;
            } else {
                W[I] = 0.0;
            }
        }
        return true;
    }

    public void init(String title) {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        /*
		 * olButton = new JButton("Olivine"); ilButton = new JButton("Ilmenite"); spButton = new JButton("Spinel");
		 * augButton = new JButton("Augite"); pigButton = new JButton("Pigeonite"); opxButton = new
		 * JButton("Orthopyroxene");
		 */
        formulaButton = new JButton("Formula");
        formulaButton.setActionCommand(FORMULA);
        formulaButton.addActionListener(this);
        clearButton = new JButton("Clear");
        clearButton.setActionCommand(CLEAR);
        clearButton.addActionListener(this);
        okButton = new JButton("Ok");
        okButton.setActionCommand(OK);
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand(CANCEL);
        cancelButton.addActionListener(this);

        JPanel p1 = new JPanel();
        // p1.add(new Label("Calculate Weight Percents for ")); // no room for this?
		/*
		 * p1.add(olButton); p1.add(augButton); p1.add(pigButton); p1.add(opxButton); p1.add(ilButton);
		 * p1.add(spButton); p1.add(new Label(" ")); // for a space
		 */
        p1.add(formulaButton);
        p1.add(clearButton);
        p1.add(okButton);
        p1.add(cancelButton);
        add("North", p1);
        /*
		 * olWtPt = new TOlWtPt(); augWtPt = new TAugWtPt(); pigWtPt = new TPigWtPt(); opxWtPt = new TOpxWtPt(); ilWtPt =
		 * new TIlWtPt(); spWtPt = new TSpWtPt(); wtpt = olWtPt;
		 * 
		 * cards = new JPanel(); cards.setLayout(new CardLayout()); cards.add(OlPanel, initializePanels(olWtPt,
		 * "Olivine")); cards.add(AugPanel, initializePanels(augWtPt, "Augite")); cards.add(PigPanel,
		 * initializePanels(pigWtPt, "Pigeonite")); cards.add(OpxPanel, initializePanels(opxWtPt, "Orthopyroxene"));
		 * cards.add(IlPanel, initializePanels(ilWtPt, "Ilmenite")); cards.add(SpPanel, initializePanels(spWtPt,
		 * "Spinel"));
		 */
        add("Center", initializePanels(this, title));
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

    /**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.awt.Panel
	 */
    public JPanel quilfPanel() {
        return null;
    }

    /**
	 * This method was created by a SmartGuide.
	 * 
	 * @return java.awt.Panel
	 */
    public JPanel wtptPanel(String title) {
        int n = 0;
        for (int i = SiO2; i <= Na2O; i++) {
            if (OxSet[i]) {
                n++;
            }
        }
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(n + 1, 2));
        titleLabel = new JLabel(title);
        p.add(titleLabel);
        p.add(new JLabel(""));
        for (int i = SiO2; i <= Na2O; i++) {
            // wtValues[i] = new TextField("");
            if (OxSet[i]) {
                p.add(new Label(OxideNames[i]));
                p.add(wtValues[i]);
            }
        }
        return p;
    }
}
