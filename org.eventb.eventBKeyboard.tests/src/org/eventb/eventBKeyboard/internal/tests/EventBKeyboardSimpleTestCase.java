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
 */
@SuppressWarnings("deprecation")
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
	
	public void testNATLaTeX() {
		formula.setText("");
		insert("n");
		insert("a");
		insert("t");
		insert(" ");
		String expect = "\u2115 ";
		String actual = formula.getText();
		assertEquals("nat ", expect, actual);
	}

	public void testNAT1() {
		formula.setText("");
		insert("N");
		insert("A");
		insert("T");
		insert("1");
		insert(" ");
		String expect = "\u2115\u2081 ";
		String actual = formula.getText();
		assertEquals("NAT1 ", expect, actual);
	}
	
	public void testNAT1LaTeX() {
		formula.setText("");
		insert("n");
		insert("a");
		insert("t");
		insert("n");
		insert(" ");
		String expect = "\u2115\u2081 ";
		String actual = formula.getText();
		assertEquals("natn ", expect, actual);
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
	
	public void testPOWLaTeX() {
		formula.setText("");
		insert("p");
		insert("o");
		insert("w");
		insert(" ");
		String expect = "\u2119 ";
		String actual = formula.getText();
		assertEquals("pow ", expect, actual);
	}

	public void testPOW1() {
		formula.setText("");
		insert("P");
		insert("O");
		insert("W");
		insert("1");
		insert(" ");
		String expect = "\u2119\u2081 ";
		String actual = formula.getText();
		assertEquals("POW1 ", expect, actual);
	}
	
	public void testPOW1LaTeX() {
		formula.setText("");
		insert("p");
		insert("o");
		insert("w");
		insert("n");
		insert(" ");
		String expect = "\u2119\u2081 ";
		String actual = formula.getText();
		assertEquals("pown ", expect, actual);
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
	
	public void testINTLaTeX() {
		formula.setText("");
		insert("i");
		insert("n");
		insert("t");
		insert("g");
		insert(" ");
		String expect = "\u2124 ";
		String actual = formula.getText();
		assertEquals("intg ", expect, actual);
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
	
	public void testLogicalEquivalentLaTeX() {
		formula.setText("");
		insert("l");
		insert("e");
		insert("q");
		insert("v");
		insert(" ");
		String expect = "\u21d4 ";
		String actual = formula.getText();
		assertEquals("leq ", expect, actual);
	}

	public void testImply() {
		formula.setText("");
		insert("=");
		insert(">");
		String expect = "\u21d2";
		String actual = formula.getText();
		assertEquals("=> ", expect, actual);
	}
	
	public void testImplyLaTeX() {
		formula.setText("");
		insert("l");
		insert("i");
		insert("m");
		insert("p");
		insert(" ");
		String expect = "\u21d2 ";
		String actual = formula.getText();
		assertEquals("limp ", expect, actual);
	}

	public void testAnd() {
		formula.setText("");
		insert("&");
		String expect = "\u2227";
		String actual = formula.getText();
		assertEquals("& ", expect, actual);
	}
	
	public void testAndLaTeX() {
		formula.setText("");
		insert("l");
		insert("a");
		insert("n");
		insert("d");
		insert(" ");
		String expect = "\u2227 ";
		String actual = formula.getText();
		assertEquals("land ", expect, actual);
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
	
	public void testOrLaTeX() {
		formula.setText("");
		insert("l");
		insert("o");
		insert("r");
		insert(" ");
		String expect = "\u2228 ";
		String actual = formula.getText();
		assertEquals("lor ", expect, actual);
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
	
	public void testNotLaTeX() {
		formula.setText("");
		insert("l");
		insert("n");
		insert("o");
		insert("t");
		insert(" ");
		String expect = "\u00ac ";
		String actual = formula.getText();
		assertEquals("lnot ", expect, actual);
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
	
	public void testTrueLaTeX() {
		formula.setText("");
		insert("b");
		insert("t");
		insert("r");
		insert("u");
		insert("e");
		insert(" ");
		String expect = "\u22a4 ";
		String actual = formula.getText();
		assertEquals("btrue ", expect, actual);
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
	
	public void testFalseLaTeX() {
		formula.setText("");
		insert("b");
		insert("f");
		insert("a");
		insert("l");
		insert("s");
		insert("e");
		insert(" ");
		String expect = "\u22a5 ";
		String actual = formula.getText();
		assertEquals("bfalse ", expect, actual);
	}

	public void testForall() {
		formula.setText("");
		insert("!");
		String expect = "\u2200";
		String actual = formula.getText();
		assertEquals("! ", expect, actual);
	}
	
	public void testForallLaTeX() {
		formula.setText("");
		insert("f");
		insert("o");
		insert("r");
		insert("a");
		insert("l");
		insert("l");
		insert(" ");
		String expect = "\u2200 ";
		String actual = formula.getText();
		assertEquals("forall ", expect, actual);
	}

	public void testThereExists() {
		formula.setText("");
		insert("#");
		String expect = "\u2203";
		String actual = formula.getText();
		assertEquals("# ", expect, actual);
	}
	
	public void testThereExistsLaTeX() {
		formula.setText("");
		insert("e");
		insert("x");
		insert("i");
		insert("s");
		insert("t");
		insert("s");
		insert(" ");
		String expect = "\u2203 ";
		String actual = formula.getText();
		assertEquals("exists ", expect, actual);
	}

	public void testMiddleDot() {
		formula.setText("");
		insert(".");
		String expect = "\u00b7";
		String actual = formula.getText();
		assertEquals(". ", expect, actual);
	}
	
	public void testMiddleDotLaTeX() {
		formula.setText("");
		insert("q");
		insert("d");
		insert("o");
		insert("t");
		insert(" ");
		String expect = "\u00b7 ";
		String actual = formula.getText();
		assertEquals("qdot ", expect, actual);
	}

	public void testNotEqual() {
		formula.setText("");
		insert("/");
		insert("=");
		String expect = "\u2260";
		String actual = formula.getText();
		assertEquals("/= ", expect, actual);
	}
	
	public void testNotEqualLaTeX() {
		formula.setText("");
		insert("n");
		insert("e");
		insert("q");
		insert(" ");
		String expect = "\u2260 ";
		String actual = formula.getText();
		assertEquals("neq ", expect, actual);
	}

	public void testLessThanEqual() {
		formula.setText("");
		insert("<");
		insert("=");
		String expect = "\u2264";
		String actual = formula.getText();
		assertEquals("<= ", expect, actual);
	}
	
	public void testLessThanEqualLaTeX() {
		formula.setText("");
		insert("l");
		insert("e");
		insert("q");
		insert(" ");
		String expect = "\u2264 ";
		String actual = formula.getText();
		assertEquals("leq ", expect, actual);
	}

	public void testGreaterThanEqual() {
		formula.setText("");
		insert(">");
		insert("=");
		String expect = "\u2265";
		String actual = formula.getText();
		assertEquals(">= ", expect, actual);
	}
	
	public void testGreaterThanEqualLaTeX() {
		formula.setText("");
		insert("g");
		insert("e");
		insert("q");
		insert(" ");
		String expect = "\u2265 ";
		String actual = formula.getText();
		assertEquals("geq ", expect, actual);
	}

	public void testElementOf() {
		formula.setText("");
		insert(":");
		String expect = "\u2208";
		String actual = formula.getText();
		assertEquals(": ", expect, actual);
	}
	
	public void testElementOfLaTeX() {
		formula.setText("");
		insert("i");
		insert("n");
		insert(" ");
		String expect = "\u2208 ";
		String actual = formula.getText();
		assertEquals("in ", expect, actual);
	}

	public void testNotAnElementOf() {
		formula.setText("");
		insert("/");
		insert(":");
		String expect = "\u2209";
		String actual = formula.getText();
		assertEquals("/: ", expect, actual);
	}
	
	public void testNotAnElementOfLaTeX() {
		formula.setText("");
		insert("n");
		insert("o");
		insert("t");
		insert("i");
		insert("n");
		insert(" ");
		String expect = "\u2209 ";
		String actual = formula.getText();
		assertEquals("notin ", expect, actual);
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
	
	public void testSubsetOfLaTeX() {
		formula.setText("");
		insert("s");
		insert("u");
		insert("b");
		insert("s");
		insert("e");
		insert("t");
		insert(" ");
		String expect = "\u2282 ";
		String actual = formula.getText();
		assertEquals("subset ", expect, actual);
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
	
	public void testNotASubsetOfLaTeX() {
		formula.setText("");
		insert("n");
		insert("o");
		insert("t");
		insert("s");
		insert("u");
		insert("b");
		insert("s");
		insert("e");
		insert("t");
		insert(" ");
		String expect = "\u2284 ";
		String actual = formula.getText();
		assertEquals("notsubset ", expect, actual);
	}

	public void testSubsetOrEqualTo() {
		formula.setText("");
		insert("<");
		insert(":");
		String expect = "\u2286";
		String actual = formula.getText();
		assertEquals("<: ", expect, actual);
	}
	
	public void testSubsetOrEqualToLaTeX() {
		formula.setText("");
		insert("s");
		insert("u");
		insert("b");
		insert("s");
		insert("e");
		insert("t");
		insert("e");
		insert("q");
		insert(" ");
		String expect = "\u2286 ";
		String actual = formula.getText();
		assertEquals("subseteq ", expect, actual);
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
	
	public void testNotASubsetOfNorEqualToLaTeX() {
		formula.setText("");
		insert("n");
		insert("o");
		insert("t");
		insert("s");
		insert("u");
		insert("b");
		insert("s");
		insert("e");
		insert("t");
		insert("e");
		insert("q");
		insert(" ");
		String expect = "\u2288 ";
		String actual = formula.getText();
		assertEquals("notsubseteq ", expect, actual);
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
	
	public void testRelationLaTeX() {
		formula.setText("");
		insert("r");
		insert("e");
		insert("l");
		insert(" ");
		String expect = "\u2194 ";
		String actual = formula.getText();
		assertEquals("rel ", expect, actual);
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
	
	public void testTotalRelationLaTeX() {
		formula.setText("");
		insert("t");
		insert("r");
		insert("e");
		insert("l");
		insert(" ");
		String expect = "\ue100 ";
		String actual = formula.getText();
		assertEquals("trel ", expect, actual);
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
	
	public void testSurjectiveRelationLaTeX() {
		formula.setText("");
		insert("s");
		insert("r");
		insert("e");
		insert("l");
		insert(" ");
		String expect = "\ue101 ";
		String actual = formula.getText();
		assertEquals("srel ", expect, actual);
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
	
	public void testTotalSurjectiveRelationLaTeX() {
		formula.setText("");
		insert("s");
		insert("t");
		insert("r");
		insert("e");
		insert("l");
		insert(" ");
		String expect = "\ue102 ";
		String actual = formula.getText();
		assertEquals("strel ", expect, actual);
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
	
	public void testPartialFunctionLaTeX() {
		formula.setText("");
		insert("p");
		insert("f");
		insert("u");
		insert("n");
		insert(" ");
		String expect = "\u21f8 ";
		String actual = formula.getText();
		assertEquals("pfun ", expect, actual);
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
	
	public void testTotalFunctionLaTeX() {
		formula.setText("");
		insert("t");
		insert("f");
		insert("u");
		insert("n");
		insert(" ");
		String expect = "\u2192 ";
		String actual = formula.getText();
		assertEquals("tfun ", expect, actual);
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
	
	public void testPartialInjectiveFunctionLaTeX() {
		formula.setText("");
		insert("p");
		insert("i");
		insert("n");
		insert("j");
		insert(" ");
		String expect = "\u2914 ";
		String actual = formula.getText();
		assertEquals("pinj ", expect, actual);
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
	
	public void testTotalInjectiveFunctionLaTeX() {
		formula.setText("");
		insert("t");
		insert("i");
		insert("n");
		insert("j");
		insert(" ");
		String expect = "\u21a3 ";
		String actual = formula.getText();
		assertEquals("tinj ", expect, actual);
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
	
	public void testPartialSurjectiveFunctionLaTeX() {
		formula.setText("");
		insert("p");
		insert("s");
		insert("u");
		insert("r");
		insert(" ");
		String expect = "\u2900 ";
		String actual = formula.getText();
		assertEquals("psur ", expect, actual);
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
	
	public void testTotalSurjectiveFunctionLaTeX() {
		formula.setText("");
		insert("t");
		insert("s");
		insert("u");
		insert("r");
		insert(" ");
		String expect = "\u21a0 ";
		String actual = formula.getText();
		assertEquals("tsur ", expect, actual);
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
	
	public void testBijectiveFunctionLaTeX() {
		formula.setText("");
		insert("t");
		insert("b");
		insert("i");
		insert("j");
		insert(" ");
		String expect = "\u2916 ";
		String actual = formula.getText();
		assertEquals("tbij ", expect, actual);
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
	
	public void testMapletLaTeX() {
		formula.setText("");
		insert("m");
		insert("a");
		insert("p");
		insert("s");
		insert("t");
		insert("o");
		insert(" ");
		String expect = "\u21a6 ";
		String actual = formula.getText();
		assertEquals("mapsto ", expect, actual);
	}

	public void testEmptySet() {
		formula.setText("");
		insert("{");
		insert("}");
		String expect = "\u2205";
		String actual = formula.getText();
		assertEquals("{} ", expect, actual);
	}
	
	public void testEmptySetLaTeX() {
		formula.setText("");
		insert("e");
		insert("m");
		insert("p");
		insert("t");
		insert("y");
		insert("s");
		insert("e");
		insert("t");
		insert(" ");
		String expect = "\u2205 ";
		String actual = formula.getText();
		assertEquals("emptyset ", expect, actual);
	}

	public void testIntersection() {
		formula.setText("");
		insert("/");
		insert("\\");
		String expect = "\u2229";
		String actual = formula.getText();
		assertEquals("/\\ ", expect, actual);
	}
	
	public void testIntersectionLaTeX() {
		formula.setText("");
		insert("b");
		insert("i");
		insert("n");
		insert("t");
		insert("e");
		insert("r");
		insert(" ");
		String expect = "\u2229 ";
		String actual = formula.getText();
		assertEquals("binter ", expect, actual);
	}

	public void testUnion() {
		formula.setText("");
		insert("\\");
		insert("/");
		String expect = "\u222a";
		String actual = formula.getText();
		assertEquals("\\/ ", expect, actual);
	}
	
	public void testUnionLaTeX() {
		formula.setText("");
		insert("b");
		insert("u");
		insert("n");
		insert("i");
		insert("o");
		insert("n");
		insert(" ");
		String expect = "\u222a ";
		String actual = formula.getText();
		assertEquals("bunion ", expect, actual);
	}

	public void testSetMinus() {
		formula.setText("");
		insert("\\");
		String expect = "\u2216";
		String actual = formula.getText();
		assertEquals("\\ ", expect, actual);
	}
	
	public void testSetMinusLaTeX() {
		formula.setText("");
		insert("s");
		insert("e");
		insert("t");
		insert("m");
		insert("i");
		insert("n");
		insert("u");
		insert("s");
		insert(" ");
		String expect = "\u2216 ";
		String actual = formula.getText();
		assertEquals("setminus ", expect, actual);
	}

	public void testCartesianProduct() {
		formula.setText("");
		insert("*");
		insert("*");
		String expect = "\u00d7";
		String actual = formula.getText();
		assertEquals("** ", expect, actual);
	}
	
	public void testCartesianProductLaTeX() {
		formula.setText("");
		insert("c");
		insert("p");
		insert("r");
		insert("o");
		insert("d");
		insert(" ");
		String expect = "\u00d7 ";
		String actual = formula.getText();
		assertEquals("cprod ", expect, actual);
	}

	public void testRelationOverriding() {
		formula.setText("");
		insert("<");
		insert("+");
		String expect = "\ue103";
		String actual = formula.getText();
		assertEquals("<+ ", expect, actual);
	}
	
	public void testRelationOverridingLaTeX() {
		formula.setText("");
		insert("o");
		insert("v");
		insert("l");
		insert(" ");
		String expect = "\ue103 ";
		String actual = formula.getText();
		assertEquals("ovl ", expect, actual);
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
	
	public void testBackwardCompositionLaTeX() {
		formula.setText("");
		insert("b");
		insert("c");
		insert("o");
		insert("m");
		insert("p");
		insert(" ");
		String expect = "\u2218 ";
		String actual = formula.getText();
		assertEquals("bcomp ", expect, actual);
	}

	public void testDirectProduct() {
		formula.setText("");
		insert(">");
		insert("<");
		String expect = "\u2297";
		String actual = formula.getText();
		assertEquals(">< ", expect, actual);
	}
	
	public void testDirectProductLaTeX() {
		formula.setText("");
		insert("d");
		insert("p");
		insert("r");
		insert("o");
		insert("d");
		insert(" ");
		String expect = "\u2297 ";
		String actual = formula.getText();
		assertEquals("dprod ", expect, actual);
	}

	public void testParallelProduct() {
		formula.setText("");
		insert("|");
		insert("|");
		String expect = "\u2225";
		String actual = formula.getText();
		assertEquals("|| ", expect, actual);
	}
	
	public void testParallelProductLaTeX() {
		formula.setText("");
		insert("p");
		insert("p");
		insert("r");
		insert("o");
		insert("d");
		insert(" ");
		String expect = "\u2225 ";
		String actual = formula.getText();
		assertEquals("pprod ", expect, actual);
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
	
	public void testDomainRestrictionLaTeX() {
		formula.setText("");
		insert("d");
		insert("o");
		insert("m");
		insert("r");
		insert("e");
		insert("s");
		insert(" ");
		String expect = "\u25c1 ";
		String actual = formula.getText();
		assertEquals("domres ", expect, actual);
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
	
	public void testDomainSubstractionLaTeX() {
		formula.setText("");
		insert("d");
		insert("o");
		insert("m");
		insert("s");
		insert("u");
		insert("b");
		insert(" ");
		String expect = "\u2a64 ";
		String actual = formula.getText();
		assertEquals("domsub ", expect, actual);
	}

	public void testRangeRestriction() {
		formula.setText("");
		insert("|");
		insert(">");
		String expect = "\u25b7";
		String actual = formula.getText();
		assertEquals("|> ", expect, actual);
	}
	
	public void testRangeRestrictionLaTeX() {
		formula.setText("");
		insert("r");
		insert("a");
		insert("n");
		insert("r");
		insert("e");
		insert("s");
		insert(" ");
		String expect = "\u25b7 ";
		String actual = formula.getText();
		assertEquals("ranres ", expect, actual);
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

	public void testRangeSubstractionLaTeX() {
		formula.setText("");
		insert("r");
		insert("a");
		insert("n");
		insert("s");
		insert("u");
		insert("b");
		insert(" ");
		String expect = "\u2a65 ";
		String actual = formula.getText();
		assertEquals("ransub ", expect, actual);
	}
	
	public void testLambda() {
		formula.setText("");
		insert("%");
		String expect = "\u03bb";
		String actual = formula.getText();
		assertEquals("% ", expect, actual);
	}
	
	public void testLambdaLaTeX() {
		formula.setText("");
		insert("l");
		insert("a");
		insert("m");
		insert("b");
		insert("d");
		insert("a");
		insert(" ");
		String expect = "\u03bb ";
		String actual = formula.getText();
		assertEquals("lambda ", expect, actual);
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
	
	public void testINTERLaTeX() {
		formula.setText("");
		insert("I");
		insert("n");
		insert("t");
		insert("e");
		insert("r");
		insert(" ");
		String expect = "\u22c2 ";
		String actual = formula.getText();
		assertEquals("Inter ", expect, actual);
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
	
	public void testUNIONLaTeX() {
		formula.setText("");
		insert("U");
		insert("n");
		insert("i");
		insert("o");
		insert("n");
		insert(" ");
		String expect = "\u22c3 ";
		String actual = formula.getText();
		assertEquals("Union ", expect, actual);
	}

	public void testUptoOperator() {
		formula.setText("");
		insert(".");
		insert(".");
		String expect = "\u2025";
		String actual = formula.getText();
		assertEquals(".. ", expect, actual);
	}
	
	public void testUptoOperatorLaTeX() {
		formula.setText("");
		insert("u");
		insert("p");
		insert("t");
		insert("o");
		insert(" ");
		String expect = "\u2025 ";
		String actual = formula.getText();
		assertEquals("upto ", expect, actual);
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
	
	public void testDivisionLaTeX() {
		formula.setText("");
		insert("d");
		insert("i");
		insert("v");
		insert(" ");
		String expect = "\u00f7 ";
		String actual = formula.getText();
		assertEquals("div ", expect, actual);
	}

	public void testBecomesEqual() {
		formula.setText("");
		insert(":");
		insert("=");
		String expect = "\u2254";
		String actual = formula.getText();
		assertEquals(":= ", expect, actual);
	}
	
	public void testBecomesEqualLaTeX() {
		formula.setText("");
		insert("b");
		insert("c");
		insert("m");
		insert("e");
		insert("q");
		insert(" ");
		String expect = "\u2254 ";
		String actual = formula.getText();
		assertEquals("bcmeq ", expect, actual);
	}

	public void testBecomesAnElementOf() {
		formula.setText("");
		insert(":");
		insert(":");
		String expect = ":\u2208";
		String actual = formula.getText();
		assertEquals(":: ", expect, actual);
	}
	
	public void testBecomesAnElementOfLaTeX() {
		formula.setText("");
		insert("b");
		insert("c");
		insert("m");
		insert("i");
		insert("n");
		insert(" ");
		String expect = ":\u2208 ";
		String actual = formula.getText();
		assertEquals("bcmin ", expect, actual);
	}

	public void testBecomesSuchThat() {
		formula.setText("");
		insert(":");
		insert("|");
		String expect = ":\u2223";
		String actual = formula.getText();
		assertEquals(":| ", expect, actual);
	}
	
	public void testBecomesSuchThatLaTeX() {
		formula.setText("");
		insert("b");
		insert("c");
		insert("m");
		insert("s");
		insert("u");
		insert("c");
		insert("h");
		insert(" ");
		String expect = ":\u2223 ";
		String actual = formula.getText();
		assertEquals("bcmsuch ", expect, actual);
	}

	public void testMid() {
		formula.setText("");
		insert("|");
		String expect = "\u2223";
		String actual = formula.getText();
		assertEquals("| ", expect, actual);
	}
	
	public void testMidLaTeX() {
		formula.setText("");
		insert("m");
		insert("i");
		insert("d");
		insert(" ");
		String expect = "\u2223 ";
		String actual = formula.getText();
		assertEquals("mid ", expect, actual);
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
	
	public void testExpLaTeX() {
		formula.setText("");
		insert("e");
		insert("x");
		insert("p");
		insert("n");
		insert(" ");
		String expect = "\u005e ";
		String actual = formula.getText();
		assertEquals("expn ", expect, actual);
	}
	
	public void testForwardCompositionLaTeX() {
		formula.setText("");
		insert("f");
		insert("c");
		insert("o");
		insert("m");
		insert("p");
		insert(" ");
		String expect = "\u003b ";
		String actual = formula.getText();
		assertEquals("fcomp ", expect, actual);
	}
}
