package edu.sunysb.ess.quilf.junit;

import junit.framework.TestCase;
import edu.sunysb.ess.quilf.swing.TNumFld;

public class TestTNumFld extends TestCase {
	public void testInitial() {
		TNumFld tNumFld =new TNumFld(0.,1);
		tNumFld.setText("1000");
		tNumFld.parse();
		assertTrue("Initial", (tNumFld.getXInitialValue()==1000));
	}
	public void testInitialFinal() {
		TNumFld tNumFld =new TNumFld(0.,1);
		tNumFld.setText("1000,1100");
		tNumFld.parse();
		assertTrue("Initial", (tNumFld.getXInitialValue()==1000));
		assertTrue("For", (tNumFld.getXForValue()==1100));
	}
	public void testInitialFinalStep() {
		TNumFld tNumFld =new TNumFld(0.,1);
		tNumFld.setText("1000,1100,10");
		tNumFld.parse();
		assertTrue("Initial", (tNumFld.getXInitialValue()==1000));
		assertTrue("For", (tNumFld.getXForValue()==1100));
		assertTrue("Sep", (tNumFld.getXStepValue()==10));
	}
}
