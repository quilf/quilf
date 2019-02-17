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
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TWtPt extends Applet {

    private JPanel cards;
    final static String OlPanel = "Olivine";
    final static String IlPanel = "Ilmenite";
    final static String SpPanel = "Spinel";
    final static String AugPanel = "Aug";
    final static String PigPanel = "Pig";
    final static String OpxPanel = "Opx";
    private JButton olButton,  ilButton,  spButton,  augButton,  pigButton,  opxButton;
    private JButton formulaButton,  clearButton,  okButton;
    private TOlWtPt olWtPt;
    private TAugWtPt augWtPt;
    private TPigWtPt pigWtPt;
    private TOpxWtPt opxWtPt;
    private TIlWtPt ilWtPt;
    private TSpWtPt spWtPt;
    private TWeight wtpt;

    public TWtPt() {
        super();
    }

    public boolean action(Event e, Object arg) {
        Object target = e.target;
        if (target == formulaButton) {
            if (wtpt != null) {
                wtpt.formula();
            }
        } else if (target == clearButton) {
            if (wtpt != null) {
                wtpt.clear();
            }
        } else if (target == okButton) {
            if (wtpt != null) {
                wtpt.ok();
                wtpt.setVisible(false);
            }
        }
        if (target == olButton) {
            if (olWtPt == null) {
                olWtPt = new TOlWtPt();
            }
            wtpt = olWtPt;
            ((CardLayout) cards.getLayout()).show(cards, OlPanel);
            return true;
        } else if (target == augButton) {
            wtpt = augWtPt;
            ((CardLayout) cards.getLayout()).show(cards, AugPanel);
            return true;
        } else if (target == pigButton) {
            wtpt = pigWtPt;
            ((CardLayout) cards.getLayout()).show(cards, PigPanel);
            return true;
        } else if (target == opxButton) {
            wtpt = opxWtPt;
            ((CardLayout) cards.getLayout()).show(cards, OpxPanel);
            return true;
        } else if (target == ilButton) {
            wtpt = ilWtPt;
            ((CardLayout) cards.getLayout()).show(cards, IlPanel);
            return true;
        } else if (target == spButton) {
            wtpt = spWtPt;
            ((CardLayout) cards.getLayout()).show(cards, SpPanel);
            return true;
        }
        return true;
    }

    public void init() {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        olButton = new JButton("Olivine");
        ilButton = new JButton("Ilmenite");
        spButton = new JButton("Spinel");
        augButton = new JButton("Augite");
        pigButton = new JButton("Pigeonite");
        opxButton = new JButton("Orthopyroxene");
        formulaButton = new JButton("Formula");
        clearButton = new JButton("Clear");
        okButton = new JButton("Ok");
        JPanel p1 = new JPanel();
//		p1.add(new Label("Calculate Weight Percents for "));	// no room for this?
        p1.add(olButton);
        p1.add(augButton);
        p1.add(pigButton);
        p1.add(opxButton);
        p1.add(ilButton);
        p1.add(spButton);
        p1.add(new Label(" "));	// for a space
        p1.add(formulaButton);
        p1.add(clearButton);
        p1.add(okButton);
        add("North", p1);

        olWtPt = new TOlWtPt();
        augWtPt = new TAugWtPt();
        pigWtPt = new TPigWtPt();
        opxWtPt = new TOpxWtPt();
        ilWtPt = new TIlWtPt();
        spWtPt = new TSpWtPt();
        wtpt = olWtPt;

        cards = new JPanel();
        cards.setLayout(new CardLayout());
        cards.add(OlPanel, initializePanels(olWtPt, "Olivine"));
        cards.add(AugPanel, initializePanels(augWtPt, "Augite"));
        cards.add(PigPanel, initializePanels(pigWtPt, "Pigeonite"));
        cards.add(OpxPanel, initializePanels(opxWtPt, "Orthopyroxene"));
        cards.add(IlPanel, initializePanels(ilWtPt, "Ilmenite"));
        cards.add(SpPanel, initializePanels(spWtPt, "Spinel"));
        add("Center", cards);
    }

    /**
     * This method was created by a SmartGuide.
     * @return java.awt.Panel
     * @param wtpt TWeightPercent
     * @param title java.lang.String
     */
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
        c.gridwidth = GridBagConstraints.REMAINDER; //end of row	
        gridbag.setConstraints(f, c);
        p.add(f);
        JPanel component = TPx.componentPanel();
        JPanel quilf = TPx.quilfPanel();
        c.gridwidth = 1;
        gridbag.setConstraints(component, c);
        p.add(component);
        c.gridwidth = GridBagConstraints.REMAINDER; //end of row	
        gridbag.setConstraints(quilf, c);
        p.add(quilf);
        return p;
    }
}
