/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.junit.Assert.assertTrue;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.junit.Test;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
@SuppressWarnings("synthetic-access")
public class TestOptionalAttributes extends BasicSCTestWithFwdConfig {
	
	private abstract class OptAttrTest<F extends IInternalElement> {
		protected F f;
		public abstract void createFile() throws Exception;
		public abstract void removeAttr() throws Exception;
		public void saveFile() throws Exception {
			saveRodinFileOf(f);
		}
		public abstract void checkAttr() throws Exception;
	}
	
	private abstract class MachineOptAttrTest extends OptAttrTest<IMachineRoot> {

		@Override
		public void createFile() throws Exception {
			f = createMachine();
		}
		
		protected IEvent e() throws Exception {
			return f.getEvents()[0];
		}
		
	}
	
	private abstract class ContextOptAttrTest extends OptAttrTest<IContextRoot> {

		@Override
		public void createFile() throws Exception {
			f = createContext();
		}
		
	}

	private IMachineRoot createMachine() throws Exception {
		IMachineRoot a = createMachine("abs");
		addInitialisation(a);
		addEvent(a, "e", 
				makeSList("a"), 
				makeSList("G"), makeSList("a∈ℤ"), 
				makeSList(), makeSList());
		IContextRoot c = createContext("ctx");
		IMachineRoot m = createMachine("mch");
		addMachineRefines(m, "abs");
		addMachineSees(m, "ctx");
		addVariables(m, "v");
		addInvariants(m, makeSList("I"), makeSList("v∈ℤ"), false);
		addInvariants(m, makeSList("T"), makeSList("⊤"), true);
		addVariant(m, "1");
		IEvent e = addEvent(m, "e", 
				makeSList("b"), 
				makeSList("G"), makeSList("b∈ℤ"), 
				makeSList("A"), makeSList("v≔b"));
		IEvent f = addEvent(m, "f");
		setConvergent(f);
		addEventRefines(e, "e");
		addEventWitnesses(e, makeSList("a"), makeSList("⊤"));
		addInitialisation(m, "v");
		
		saveRodinFileOf(a);
		saveRodinFileOf(c);
		saveRodinFileOf(m);
		
		return m;
	}
	
	private IContextRoot createContext() throws Exception {
		IContextRoot a = createContext("abs");
		IContextRoot c = createContext("ctx");
		addContextExtends(c, "abs");
		addCarrierSets(c, "S");
		addConstants(c, "C");
		addAxioms(c, makeSList("A"), makeSList("C∈S"), false);
		addAxioms(c, makeSList("T"), makeSList("⊤"), true);
		
		saveRodinFileOf(a);
		saveRodinFileOf(c);
		
		return c;
	}
	
	/**
	 * precondition of proper test:
	 * check if machine is ok
	 */
	@Test
	public void testMachine() throws Exception {
		createMachine();

		runBuilderCheck();
	}
	
	/**
	 * precondition of proper test:
	 * check if context is ok
	 */
	@Test
	public void testContext() throws Exception {
		createContext();
		
		runBuilderCheck();
	}
	
	private OptAttrTest<?>[] tests = new OptAttrTest[] {
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getRefinesClauses()[0],EventBAttributes.TARGET_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getRefinesClauses()[0].hasAttribute(EventBAttributes.TARGET_ATTRIBUTE));
					f.getRefinesClauses()[0].removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getSeesClauses()[0], EventBAttributes.TARGET_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getSeesClauses()[0].hasAttribute(EventBAttributes.TARGET_ATTRIBUTE));
					f.getSeesClauses()[0].removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getVariables()[0], EventBAttributes.IDENTIFIER_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getVariables()[0].hasAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE));
					f.getVariables()[0].removeAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getInvariants()[0], EventBAttributes.LABEL_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getInvariants()[0].hasAttribute(EventBAttributes.LABEL_ATTRIBUTE));
					f.getInvariants()[0].removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getInvariants()[0], EventBAttributes.PREDICATE_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getInvariants()[0].hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE));
					f.getInvariants()[0].removeAttribute(EventBAttributes.PREDICATE_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getVariants()[0], EventBAttributes.EXPRESSION_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getVariants()[0].hasAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE));
					f.getVariants()[0].removeAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getEvents()[0], EventBAttributes.LABEL_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getEvents()[0].hasAttribute(EventBAttributes.LABEL_ATTRIBUTE));
					f.getEvents()[0].removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getEvents()[0], EventBAttributes.CONVERGENCE_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getEvents()[0].hasAttribute(EventBAttributes.CONVERGENCE_ATTRIBUTE));
					f.getEvents()[0].removeAttribute(EventBAttributes.CONVERGENCE_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getEvents()[0], EventBAttributes.EXTENDED_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getEvents()[0].hasAttribute(EventBAttributes.EXTENDED_ATTRIBUTE));
					f.getEvents()[0].removeAttribute(EventBAttributes.EXTENDED_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getParameters()[0], EventBAttributes.IDENTIFIER_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getParameters()[0].hasAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE));
					e().getParameters()[0].removeAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getGuards()[0], EventBAttributes.LABEL_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getGuards()[0].hasAttribute(EventBAttributes.LABEL_ATTRIBUTE));
					e().getGuards()[0].removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getGuards()[0], EventBAttributes.PREDICATE_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getGuards()[0].hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE));
					e().getGuards()[0].removeAttribute(EventBAttributes.PREDICATE_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getActions()[0], EventBAttributes.LABEL_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getActions()[0].hasAttribute(EventBAttributes.LABEL_ATTRIBUTE));
					e().getActions()[0].removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getActions()[0], EventBAttributes.ASSIGNMENT_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getActions()[0].hasAttribute(EventBAttributes.ASSIGNMENT_ATTRIBUTE));
					e().getActions()[0].removeAttribute(EventBAttributes.ASSIGNMENT_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getWitnesses()[0], EventBAttributes.LABEL_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getWitnesses()[0].hasAttribute(EventBAttributes.LABEL_ATTRIBUTE));
					e().getWitnesses()[0].removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getWitnesses()[0], EventBAttributes.PREDICATE_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getWitnesses()[0].hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE));
					e().getWitnesses()[0].removeAttribute(EventBAttributes.PREDICATE_ATTRIBUTE, null);
				}
				
			},
			new MachineOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(e().getRefinesClauses()[0], EventBAttributes.TARGET_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(e().getRefinesClauses()[0].hasAttribute(EventBAttributes.TARGET_ATTRIBUTE));
					e().getRefinesClauses()[0].removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, null);
				}
				
			},
			new ContextOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getExtendsClauses()[0],
							EventBAttributes.TARGET_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getExtendsClauses()[0]
							.hasAttribute(EventBAttributes.TARGET_ATTRIBUTE));
					f.getExtendsClauses()[0].removeAttribute(
							EventBAttributes.TARGET_ATTRIBUTE, null);
				}
				
			},
			new ContextOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getCarrierSets()[0],
							EventBAttributes.IDENTIFIER_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getCarrierSets()[0]
							.hasAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE));
					f.getCarrierSets()[0].removeAttribute(
							EventBAttributes.IDENTIFIER_ATTRIBUTE, null);
				}
				
			},
			new ContextOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getConstants()[0],
							EventBAttributes.IDENTIFIER_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getConstants()[0]
							.hasAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE));
					f.getConstants()[0].removeAttribute(
							EventBAttributes.IDENTIFIER_ATTRIBUTE, null);
				}

			}, new ContextOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getAxioms()[0],
							EventBAttributes.LABEL_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getAxioms()[0]
							.hasAttribute(EventBAttributes.LABEL_ATTRIBUTE));
					f.getAxioms()[0].removeAttribute(
							EventBAttributes.LABEL_ATTRIBUTE, null);
				}
				
			},
			new ContextOptAttrTest() {

				@Override
				public void checkAttr() throws Exception {
					hasMarker(f.getAxioms()[0],
							EventBAttributes.PREDICATE_ATTRIBUTE);
				}

				@Override
				public void removeAttr() throws Exception {
					assertTrue(f.getAxioms()[0]
							.hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE));
					f.getAxioms()[0].removeAttribute(
							EventBAttributes.PREDICATE_ATTRIBUTE, null);
				}
				
			},
	};
	
	@Test
	public void test() throws Exception {
		
		for (OptAttrTest<?> test : tests) {
			test.createFile();
			test.removeAttr();
			test.saveFile();
			runBuilder();
			test.checkAttr();
		}
	}

}
