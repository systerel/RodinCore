/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed tests about LaTeX translation
 *******************************************************************************/
package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.TestCase;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.eventBKeyboard.EventBKeyboardPlugin;
import org.eventb.eventBKeyboard.EventBTextModifyListener;
import org.eventb.eventBKeyboard.internal.views.EventBKeyboardView;

/**
 * @author htson
 *         <p>
 *         This class contains some simple test cases for Event-B Keyboard. This
 *         test all the symbols separately.
 * @deprecated use org.eventb.keyboard.tests instead
 */
@Deprecated
public class EventBKeyboardSimpleTestCase extends TestCase {

	private Text formula;

	private EventBTextModifyListener listener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		IWorkbenchPage page = EventBKeyboardPlugin.getActivePage();

		EventBKeyboardView view = (EventBKeyboardView) page
				.findView(EventBKeyboardPlugin.EventBKeyboardView_ID);

		if (view == null)
			view = (EventBKeyboardView) page
					.showView(EventBKeyboardPlugin.EventBKeyboardView_ID);

		formula = view.getFormula();
		listener = view.getListener();

		// Remove the listener
		// In order to simulate user's input, we have to manually setup the
		// listener.
		formula.removeModifyListener(listener);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		formula.addModifyListener(listener);
	}

	/**
	 * We use this method to similate the action of typing a character into the
	 * text area
	 */
	private void insert(String s) {
		formula.insert(s);
		Event e = new Event();
		e.widget = formula;
		// Force the listener to modify the text then remove it again
		listener.modifyText(new ModifyEvent(e));
		formula.removeModifyListener(listener);
	}

	public void testNAT() {
		formula.setText("");
		insert("N");
		insert("A");
		insert("T");
		insert(" ");
		String expect = "\u2115 ";
		String actual = formula.getText();
		assertEquals("NAT ", expect, actual);
	}

	public void testNAT1() {
		formula.setText("");
		insert("N");
		insert("A");
		insert("T");
		insert("1");
		insert(" ");
		String expect = "\u2115\u0031 ";
		String actual = formula.getText();
		assertEquals("NAT1 ", expect, actual);
	}

	public void testPOW() {
		formula.setText("");
		insert("P");
		insert("O");
		insert("W");
		insert(" ");
		String expect = "\u2119 ";
		String actual = formula.getText();
		assertEquals("POW ", expect, actual);
	}

	public void testPOW1() {
		formula.setText("");
		insert("P");
		insert("O");
		insert("W");
		insert("1");
		insert(" ");
		String expect = "\u2119\u0031 ";
		String actual = formula.getText();
		assertEquals("POW1 ", expect, actual);
	}

	public void testINT() {
		formula.setText("");
		insert("I");
		insert("N");
		insert("T");
		insert(" ");
		String expect = "\u2124 ";
		String actual = formula.getText();
		assertEquals("INT ", expect, actual);
	}

	public void testLogicalEquivalent() {
		formula.setText("");
		insert("<");
		insert("=");
		insert(">");
		String expect = "\u21d4";
		String actual = formula.getText();
		assertEquals("<=> ", expect, actual);
	}

	public void testImply() {
		formula.setText("");
		insert("=");
		insert(">");
		String expect = "\u21d2";
		String actual = formula.getText();
		assertEquals("=> ", expect, actual);
	}

	public void testAnd() {
		formula.setText("");
		insert("&");
		String expect = "\u2227";
		String actual = formula.getText();
		assertEquals("& ", expect, actual);
	}

	public void testOr() {
		formula.setText("");
		insert("o");
		insert("r");
		insert(" ");
		String expect = "\u2228 ";
		String actual = formula.getText();
		assertEquals("or ", expect, actual);
	}

	public void testNot() {
		formula.setText("");
		insert("n");
		insert("o");
		insert("t");
		insert(" ");
		String expect = "\u00ac ";
		String actual = formula.getText();
		assertEquals("not ", expect, actual);
	}

	public void testTrue() {
		formula.setText("");
		insert("t");
		insert("r");
		insert("u");
		insert("e");
		insert(" ");
		String expect = "\u22a4 ";
		String actual = formula.getText();
		assertEquals("true ", expect, actual);
	}

	public void testFalse() {
		formula.setText("");
		insert("f");
		insert("a");
		insert("l");
		insert("s");
		insert("e");
		insert(" ");
		String expect = "\u22a5 ";
		String actual = formula.getText();
		assertEquals("false ", expect, actual);
	}

	public void testForall() {
		formula.setText("");
		insert("!");
		String expect = "\u2200";
		String actual = formula.getText();
		assertEquals("! ", expect, actual);
	}

	public void testThereExists() {
		formula.setText("");
		insert("#");
		String expect = "\u2203";
		String actual = formula.getText();
		assertEquals("# ", expect, actual);
	}

	public void testMiddleDot() {
		formula.setText("");
		insert(".");
		String expect = "\u00b7";
		String actual = formula.getText();
		assertEquals(". ", expect, actual);
	}

	public void testNotEqual() {
		formula.setText("");
		insert("/");
		insert("=");
		String expect = "\u2260";
		String actual = formula.getText();
		assertEquals("/= ", expect, actual);
	}

	public void testLessThanEqual() {
		formula.setText("");
		insert("<");
		insert("=");
		String expect = "\u2264";
		String actual = formula.getText();
		assertEquals("<= ", expect, actual);
	}

	public void testGreaterThanEqual() {
		formula.setText("");
		insert(">");
		insert("=");
		String expect = "\u2265";
		String actual = formula.getText();
		assertEquals(">= ", expect, actual);
	}

	public void testElementOf() {
		formula.setText("");
		insert(":");
		String expect = "\u2208";
		String actual = formula.getText();
		assertEquals(": ", expect, actual);
	}

	public void testNotAnElementOf() {
		formula.setText("");
		insert("/");
		insert(":");
		String expect = "\u2209";
		String actual = formula.getText();
		assertEquals("/: ", expect, actual);
	}

	public void testSubsetOf() {
		formula.setText("");
		insert("<");
		insert("<");
		insert(":");
		String expect = "\u2282";
		String actual = formula.getText();
		assertEquals("<<: ", expect, actual);
	}

	public void testNotASubsetOf() {
		formula.setText("");
		insert("/");
		insert("<");
		insert("<");
		insert(":");
		String expect = "\u2284";
		String actual = formula.getText();
		assertEquals("/<<: ", expect, actual);
	}

	public void testSubsetOrEqualTo() {
		formula.setText("");
		insert("<");
		insert(":");
		String expect = "\u2286";
		String actual = formula.getText();
		assertEquals("<: ", expect, actual);
	}

	public void testNotASubsetOfNorEqualTo() {
		formula.setText("");
		insert("/");
		insert("<");
		insert(":");
		String expect = "\u2288";
		String actual = formula.getText();
		assertEquals("/<: ", expect, actual);
	}

	public void testRelation() {
		formula.setText("");
		insert("<");
		insert("-");
		insert(">");
		String expect = "\u2194";
		String actual = formula.getText();
		assertEquals("<-> ", expect, actual);
	}

	public void testTotalRelation() {
		formula.setText("");
		insert("<");
		insert("<");
		insert("-");
		insert(">");
		String expect = "\ue100";
		String actual = formula.getText();
		assertEquals("<<-> ", expect, actual);
	}

	public void testSurjectiveRelation() {
		formula.setText("");
		insert("<");
		insert("-");
		insert(">");
		insert(">");
		String expect = "\ue101";
		String actual = formula.getText();
		assertEquals("<->> ", expect, actual);
	}

	public void testTotalSurjectiveRelation() {
		formula.setText("");
		insert("<");
		insert("<");
		insert("-");
		insert(">");
		insert(">");
		String expect = "\ue102";
		String actual = formula.getText();
		assertEquals("<<->> ", expect, actual);
	}

	public void testPartialFunction() {
		formula.setText("");
		insert("+");
		insert("-");
		insert(">");
		String expect = "\u21f8";
		String actual = formula.getText();
		assertEquals("+-> ", expect, actual);
	}

	public void testTotalFunction() {
		formula.setText("");
		insert("-");
		insert("-");
		insert(">");
		String expect = "\u2192";
		String actual = formula.getText();
		assertEquals("--> ", expect, actual);
	}

	public void testPartialInjectiveFunction() {
		formula.setText("");
		insert(">");
		insert("+");
		insert(">");
		String expect = "\u2914";
		String actual = formula.getText();
		assertEquals(">+> ", expect, actual);
	}

	public void testTotalInjectiveFunction() {
		formula.setText("");
		insert(">");
		insert("-");
		insert(">");
		String expect = "\u21a3";
		String actual = formula.getText();
		assertEquals(">-> ", expect, actual);
	}

	public void testPartialSurjectiveFunction() {
		formula.setText("");
		insert("+");
		insert(">");
		insert(">");
		String expect = "\u2900";
		String actual = formula.getText();
		assertEquals("+>> ", expect, actual);
	}

	public void testTotalSurjectiveFunction() {
		formula.setText("");
		insert("-");
		insert(">");
		insert(">");
		String expect = "\u21a0";
		String actual = formula.getText();
		assertEquals("->> ", expect, actual);
	}

	public void testBijectiveFunction() {
		formula.setText("");
		insert(">");
		insert("-");
		insert(">");
		insert(">");
		String expect = "\u2916";
		String actual = formula.getText();
		assertEquals(">->> ", expect, actual);
	}

	public void testMaplet1() {
		formula.setText("");
		insert("|");
		insert("-");
		insert(">");
		String expect = "\u21a6";
		String actual = formula.getText();
		assertEquals("|-> ", expect, actual);
	}

	public void testMaplet2() {
		formula.setText("");
		insert(",");
		insert(",");
		String expect = "\u21a6";
		String actual = formula.getText();
		assertEquals(",, ", expect, actual);
	}

	public void testEmptySet() {
		formula.setText("");
		insert("{");
		insert("}");
		String expect = "\u2205";
		String actual = formula.getText();
		assertEquals("{} ", expect, actual);
	}

	public void testIntersection() {
		formula.setText("");
		insert("/");
		insert("\\");
		String expect = "\u2229";
		String actual = formula.getText();
		assertEquals("/\\ ", expect, actual);
	}

	public void testUnion() {
		formula.setText("");
		insert("\\");
		insert("/");
		String expect = "\u222a";
		String actual = formula.getText();
		assertEquals("\\/ ", expect, actual);
	}

	public void testSetMinus() {
		formula.setText("");
		insert("\\");
		String expect = "\u2216";
		String actual = formula.getText();
		assertEquals("\\ ", expect, actual);
	}

	public void testCartesianProduct() {
		formula.setText("");
		insert("*");
		insert("*");
		String expect = "\u00d7";
		String actual = formula.getText();
		assertEquals("** ", expect, actual);
	}

	public void testRelationOverriding() {
		formula.setText("");
		insert("<");
		insert("+");
		String expect = "\ue103";
		String actual = formula.getText();
		assertEquals("<+ ", expect, actual);
	}

	public void testBackwardComposition() {
		formula.setText("");
		insert("c");
		insert("i");
		insert("r");
		insert("c");
		insert(" ");
		String expect = "\u2218 ";
		String actual = formula.getText();
		assertEquals("circ ", expect, actual);
	}

	public void testDirectProduct() {
		formula.setText("");
		insert(">");
		insert("<");
		String expect = "\u2297";
		String actual = formula.getText();
		assertEquals(">< ", expect, actual);
	}

	public void testParallelProduct() {
		formula.setText("");
		insert("|");
		insert("|");
		String expect = "\u2225";
		String actual = formula.getText();
		assertEquals("|| ", expect, actual);
	}

	public void testTildeOperator() {
		formula.setText("");
		insert("~");
		String expect = "\u223c";
		String actual = formula.getText();
		assertEquals("~ ", expect, actual);
	}
	
	public void testDomainRestriction() {
		formula.setText("");
		insert("<");
		insert("|");
		String expect = "\u25c1";
		String actual = formula.getText();
		assertEquals("<| ", expect, actual);
	}

	public void testDomainSubstraction() {
		formula.setText("");
		insert("<");
		insert("<");
		insert("|");
		String expect = "\u2a64";
		String actual = formula.getText();
		assertEquals("<<| ", expect, actual);
	}

	public void testRangeRestriction() {
		formula.setText("");
		insert("|");
		insert(">");
		String expect = "\u25b7";
		String actual = formula.getText();
		assertEquals("|> ", expect, actual);
	}

	public void testRangeSubstraction() {
		formula.setText("");
		insert("|");
		insert(">");
		insert(">");
		String expect = "\u2a65";
		String actual = formula.getText();
		assertEquals("|>> ", expect, actual);
	}

	public void testLambda() {
		formula.setText("");
		insert("%");
		String expect = "\u03bb";
		String actual = formula.getText();
		assertEquals("% ", expect, actual);
	}

	public void testINTER() {
		formula.setText("");
		insert("I");
		insert("N");
		insert("T");
		insert("E");
		insert("R");
		insert(" ");
		String expect = "\u22c2 ";
		String actual = formula.getText();
		assertEquals("INTER ", expect, actual);
	}

	public void testUNION() {
		formula.setText("");
		insert("U");
		insert("N");
		insert("I");
		insert("O");
		insert("N");
		insert(" ");
		String expect = "\u22c3 ";
		String actual = formula.getText();
		assertEquals("UNION ", expect, actual);
	}

	public void testUptoOperator() {
		formula.setText("");
		insert(".");
		insert(".");
		String expect = "\u2025";
		String actual = formula.getText();
		assertEquals(".. ", expect, actual);
	}

	public void testMinus() {
		formula.setText("");
		insert("-");
		String expect = "\u2212";
		String actual = formula.getText();
		assertEquals("- ", expect, actual);
	}

	public void testAsterisk() {
		formula.setText("");
		insert("*");
		String expect = "\u2217";
		String actual = formula.getText();
		assertEquals("* ", expect, actual);
	}

	public void testDivision() {
		formula.setText("");
		insert("/");
		String expect = "\u00f7";
		String actual = formula.getText();
		assertEquals("/ ", expect, actual);
	}

	public void testBecomesEqual() {
		formula.setText("");
		insert(":");
		insert("=");
		String expect = "\u2254";
		String actual = formula.getText();
		assertEquals(":= ", expect, actual);
	}

	public void testBecomesAnElementOf() {
		formula.setText("");
		insert(":");
		insert(":");
		String expect = ":\u2208";
		String actual = formula.getText();
		assertEquals(":: ", expect, actual);
	}

	public void testBecomesSuchThat() {
		formula.setText("");
		insert(":");
		insert("|");
		String expect = ":\u2223";
		String actual = formula.getText();
		assertEquals(":| ", expect, actual);
	}

	public void testMid() {
		formula.setText("");
		insert("|");
		String expect = "\u2223";
		String actual = formula.getText();
		assertEquals("| ", expect, actual);
	}

	public void testOfType() {
		formula.setText("");
		insert("o");
		insert("f");
		insert("t");
		insert("y");
		insert("p");
		insert("e");
		insert(" ");
		String expect = "\u2982 ";
		String actual = formula.getText();
		assertEquals("oftype ", expect, actual);
	}

}
