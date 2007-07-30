/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.basis.SeesContext;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common implementation for conversion from a Rodin file to a string (used for
 * the pretty print page of event-B editors).
 * 
 * @author htson
 */
public abstract class AstConverter {
	
	protected String SPACE = "";

	protected String HEADER = "";
	protected String FOOTER = "";
	protected String BEGIN_KEYWORD_0 = "";
	protected String BEGIN_KEYWORD_1 = "";
	protected String BEGIN_KEYWORD_2 = "";
	protected String BEGIN_KEYWORD_3 = "";
	protected String END_KEYWORD_0 = "";
	protected String END_KEYWORD_1 = "";
	protected String END_KEYWORD_2 = "";
	protected String END_KEYWORD_3 = "";
	protected String ITALIC = "";
	protected String END_ITALIC = "";
	protected String BEGIN_LEVEL_0 = "";
	protected String BEGIN_LEVEL_1 = "";
	protected String BEGIN_LEVEL_2 = "";
	protected String BEGIN_LEVEL_3 = "";
	protected String END_LEVEL_0 = "";
	protected String END_LEVEL_1 = "";
	protected String END_LEVEL_2 = "";
	protected String END_LEVEL_3 = "";
	protected String EMPTY_LINE = "";
	protected String BEGIN_ATTRIBUTE = "";
	protected String END_ATTRIBUTE = "";
	protected String BEGIN_MULTILINE = "";
	protected String END_MULTILINE = "";
	protected String BEGIN_LINE = "";
	protected String END_LINE = "";
	protected String BEGIN_NAME_SEPARATOR = null;
	protected String END_NAME_SEPARATOR = null;
	protected String BEGIN_LABEL_SEPARATOR = null;
	protected String END_LABEL_SEPARATOR = ":";
	protected String BEGIN_PREDICATE_SEPARATOR = null;
	protected String END_PREDICATE_SEPARATOR = null;
	protected String BEGIN_ASSIGNMENT_SEPARATOR = null;
	protected String END_ASSIGNMENT_SEPARATOR = null;
	protected String BEGIN_EXPRESSION_SEPARATOR = null;
	protected String END_EXPRESSION_SEPARATOR = null;
	protected String BEGIN_COMMENT_SEPARATOR = "//";
	protected String END_COMMENT_SEPARATOR = null;
	
	// The content string of the form text
	private StringBuilder htmlString;
	
	public AstConverter() {
		htmlString = new StringBuilder("");
	}
	
	
	public String getText(IProgressMonitor monitor, IRodinFile rodinFile) {
		htmlString.setLength(0);
		htmlString.append(HEADER);
		addComponentName(rodinFile);
		addComponentDependencies(rodinFile, monitor);
		if (rodinFile instanceof IMachineFile) {
			addVariables(rodinFile, monitor);
			addInvariants(rodinFile, monitor);
			addTheorems(rodinFile, monitor);
			addEvents(rodinFile, monitor);
			addVariant(rodinFile, monitor);
		} else if (rodinFile instanceof IContextFile) {
			addCarrierSets(rodinFile, monitor);
			addConstants(rodinFile, monitor);
			addAxioms(rodinFile, monitor);
			addTheorems(rodinFile, monitor);
		}
		section("END");
		htmlString.append(FOOTER);

		return htmlString.toString();
	}
	
	
	/**
	 * This private helper method adds the component name to the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 */
	private void addComponentName(IRodinFile rodinFile) {
		// Print the Machine/Context name
		beginLevel0();
		if (rodinFile instanceof IMachineFile) {
			keyword("MACHINE", 0);
		} else if (rodinFile instanceof IContextFile) {
			keyword("CONTEXT", 0);
		}
		endLevel0();
		beginLevel1();
		final String handle = rodinFile.getHandleIdentifier();
		final String bareName = rodinFile.getBareName();
		appendName(makeHyperlink(handle, bareName));
		if (rodinFile instanceof ICommentedElement) {
			addComment((ICommentedElement) rodinFile);
		}
		endLevel1();
		return;
	}

	/**
	 * This private helper method adds component's dependency information to the
	 * content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addComponentDependencies(IRodinFile rodinFile,
			IProgressMonitor monitor) {
		if (rodinFile instanceof IMachineFile) {
			// REFINES clause
			IRodinElement[] refines;
			try {
				refines = rodinFile
						.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
				if (refines.length != 0) {
					IRefinesMachine refine = (IRefinesMachine) refines[0];
					String name = refine.getAbstractMachineName();
					section("REFINES");
					beginLevel1();
					appendName(makeHyperlink(EventBPlugin
							.getMachineFileName(name), name));
					endLevel1();
				}
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get refines machine of "
								+ rodinFile.getElementName());
			}

		} else if (rodinFile instanceof IContextFile) {
			// EXTENDS clause
			IRodinElement[] extendz;
			try {
				extendz = rodinFile
						.getChildrenOfType(IExtendsContext.ELEMENT_TYPE);
				if (extendz.length != 0) {
					IExtendsContext extend = (IExtendsContext) extendz[0];
					String name = extend.getAbstractContextName();
					section("EXTENDS");
					beginLevel1();
					appendName(makeHyperlink(EventBPlugin
							.getContextFileName(name), name));
					endLevel1();
				}
			} catch (RodinDBException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Cannot get extends context of "
								+ rodinFile.getElementName());
			}

		}

		// SEES clause for both context and machine
		IRodinElement[] seeContexts;
		try {
			seeContexts = rodinFile
					.getChildrenOfType(ISeesContext.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get sees machine of "
					+ rodinFile.getElementName());
			return;
		}

		int length = seeContexts.length;
		if (length != 0) {
			section("SEES");
			for (int i = 0; i < length; i++) {
				try {
					beginLevel1();
					appendName(
							makeHyperlink(rodinFile.getHandleIdentifier(),
									((SeesContext) seeContexts[i])
											.getSeenContextName()));
					endLevel1();
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get seen context name of "
									+ seeContexts[i].getElementName());
				}
			}
		}
	}

	private void section(String s) {
		keyword(s, 0);
	}

	/**
	 * This private helper method adds component's information about variables
	 * to the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addVariables(IRodinFile rodinFile, IProgressMonitor monitor) {
		IVariable[] vars;
		try {
			vars = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variables for "
					+ rodinFile.getElementName());
			return;
		}
		if (vars.length != 0) {
			section("VARIABLES");
			for (IVariable var: vars) {
				beginLevel1();
				try {
					appendName(makeHyperlink(var.getHandleIdentifier(), var
							.getIdentifierString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for variable "
									+ var.getElementName());
				}
				addComment(var);
				endLevel1();
			}
		}
	}

	/**
	 * This private helper method adds component's information about invariants
	 * to the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addInvariants(IRodinFile rodinFile, IProgressMonitor monitor) {
		IInvariant[] invs;
		try {
			invs = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get invariants for "
					+ rodinFile.getElementName());
			return;
		}
		if (invs.length != 0) {
			section("INVARIANTS");
			for (IInvariant inv: invs) {
				beginLevel1();
				try {
					appendLabel(makeHyperlink(inv.getHandleIdentifier(), inv
							.getLabel()));
					appendPredicate(UIUtils.XMLWrapUp(inv.getPredicateString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for invariant "
									+ inv.getElementName());
				}
				addComment(inv);
				endLevel1();
			}
		}
	}

	/**
	 * This private helper method adds component's information about carrier
	 * sets to the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addCarrierSets(IRodinFile rodinFile, IProgressMonitor monitor) {
		ICarrierSet[] sets;
		try {
			sets = rodinFile.getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils
					.debugAndLogError(e, "Cannot get carrier sets for "
							+ rodinFile.getElementName());
			return;
		}
		if (sets.length != 0) {
			section("SETS");
			for (ICarrierSet set: sets) {
				beginLevel1();
				try {
					appendName(makeHyperlink(set.getHandleIdentifier(), set
							.getIdentifierString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for carrier set "
									+ set.getElementName());
					e.printStackTrace();
				}
				addComment(set);
				endLevel1();
			}
		}
	}

	/**
	 * This private helper method adds component's information about constants
	 * to the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addConstants(IRodinFile rodinFile, IProgressMonitor monitor) {
		IConstant[] csts;
		try {
			csts = rodinFile.getChildrenOfType(IConstant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get constants for "
					+ rodinFile.getElementName());
			return;
		}
		if (csts.length != 0) {
			section("CONSTANTS");
			for (IConstant cst: csts) {
				beginLevel1();
				try {
					appendName(makeHyperlink(cst.getHandleIdentifier(), cst
							.getIdentifierString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for constant "
									+ cst.getElementName());
					e.printStackTrace();
				}
				addComment(cst);
				endLevel1();
			}
		}
	}

	/**
	 * This private helper method adds component's information about axioms to
	 * the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addAxioms(IRodinFile rodinFile, IProgressMonitor monitor) {
		IAxiom[] axms;
		try {
			axms = rodinFile.getChildrenOfType(IAxiom.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get axioms for "
					+ rodinFile.getElementName());
			return;
		}
		if (axms.length != 0) {
			section("AXIOMS");
			for (IAxiom axm: axms) {
				beginLevel1();
				try {
					appendLabel(makeHyperlink(axm.getHandleIdentifier(), axm
							.getLabel()));
					appendPredicate(UIUtils.XMLWrapUp(axm.getPredicateString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for axiom "
									+ axm.getElementName());
				}
				addComment(axm);
				endLevel1();
			}
		}
	}


	/**
	 * This private helper method adds component's information about theorems to
	 * the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addTheorems(IRodinFile rodinFile, IProgressMonitor monitor) {
		ITheorem[] thms;
		try {
			thms = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get theorems for "
					+ rodinFile.getElementName());
			return;
		}
		if (thms.length != 0) {
			section("THEOREMS");
			for (ITheorem thm: thms) {
				beginLevel1();
				try {
					appendLabel(makeHyperlink(thm.getHandleIdentifier(), thm
							.getLabel()));
					appendPredicate(UIUtils.XMLWrapUp(thm.getPredicateString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for theorem "
									+ thm.getElementName());
				}
				addComment(thm);
				endLevel1();
			}
		}
	}

	/**
	 * This private helper method adds component's information about events to
	 * the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addEvents(IRodinFile rodinFile, IProgressMonitor monitor) {
		IEvent[] evts;
		try {
			evts = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get events for "
					+ rodinFile.getElementName());
			return;
		}

		if (evts.length != 0) {
			section("EVENTS");
			for (IEvent evt: evts) {
				try {
					emptyLine();
					beginLevel1();
					appendName(makeHyperlink(evt.getHandleIdentifier(), evt
							.getLabel()));
					addComment(evt);
					endLevel1();
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the label for event "
									+ evt.getElementName());
					continue;
				}
				
				try {
					if (evt.isInherited()) {
						beginLevel2();
						append("inherited", "", "");
						endLevel2();
						continue;
					}
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleGetAttributeException(e);
					continue;
				}
				IVariable[] lvars;
				IGuard[] guards;
				IAction[] actions;
				IRefinesEvent[] refinesEvents;
				IWitness[] witnesses;
				try {
					refinesEvents = evt
							.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
					lvars = evt.getChildrenOfType(IVariable.ELEMENT_TYPE);
					guards = evt.getChildrenOfType(IGuard.ELEMENT_TYPE);
					witnesses = evt.getChildrenOfType(IWitness.ELEMENT_TYPE);
					actions = evt.getChildrenOfType(IAction.ELEMENT_TYPE);
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the children for event "
									+ evt.getElementName());
					continue;
				}

				if (refinesEvents.length != 0) {
					keyword("REFINES", 2);
					for (IRefinesEvent refinesEvent: refinesEvents) {
						beginLevel3();
						try {
							appendName(makeHyperlink(refinesEvent
									.getHandleIdentifier(), refinesEvent
									.getAbstractEventLabel()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the abstract event label for refines event "
											+ refinesEvent.getElementName());
						}

						endLevel3();
					}
				}

				if (lvars.length != 0) {
					keyword("ANY", 2);
					for (IVariable var: lvars) {
						beginLevel3();
						try {
							appendName(makeHyperlink(var
									.getHandleIdentifier(), var
									.getIdentifierString()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the identifier string for local variable "
											+ var.getElementName());
						}
						addComment(var);
						endLevel3();
					}
					keyword("WHERE", 2);
				} else {
					if (guards.length != 0) {
						keyword("WHEN", 2);
					} else {
						keyword("BEGIN", 2);
					}
				}

				for (IGuard guard: guards) {
					beginLevel3();
					try {
						appendLabel(makeHyperlink(guard
										.getHandleIdentifier(), guard
										.getLabel()));
						appendPredicate(UIUtils.XMLWrapUp(guard
												.getPredicateString()));
					} catch (RodinDBException e) {
						EventBEditorUtils.debugAndLogError(e,
								"Cannot get details for guard "
										+ guard.getElementName());
					}
					addComment(guard);
					endLevel3();
				}

				if (witnesses.length != 0) {
					keyword("WITH", 2);
					for (IWitness witness: witnesses) {
						beginLevel3();
						try {
							appendLabel(makeHyperlink(witness
									.getHandleIdentifier(), witness.getLabel()));
							appendPredicate(UIUtils.XMLWrapUp(witness
											.getPredicateString()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get details for guard "
											+ witness.getElementName());
						}
						addComment(witness);
						endLevel3();
					}
				}

				if (guards.length != 0) {
					keyword("THEN", 2);
				}

				if (actions.length == 0) {
					beginLevel3();
					appendAssignment("skip");
					endLevel3();
				} else {
					for (IAction action: actions) {
						beginLevel3();
						try {
							appendLabel(makeHyperlink(action
									.getHandleIdentifier(), action.getLabel()));
							appendAssignment(UIUtils.XMLWrapUp(action
											.getAssignmentString()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get details for action "
									+ action.getElementName());
						}
						addComment(action);
						endLevel3();
					}
				}
				keyword("END", 2);
			}
		}
	}



	/**
	 * This private helper method adds component's information about variants to
	 * the content string
	 * <p>
	 * public static String makeHyperlink(String link, String text) {
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addVariant(IRodinFile rodinFile, IProgressMonitor monitor) {
		IVariant[] variants;
		try {
			variants = rodinFile.getChildrenOfType(IVariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variants for "
					+ rodinFile.getElementName());
			return;
		}
		if (variants.length != 0) {
			section("VARIANT");
			for (IVariant variant: variants) {
				beginLevel1();
				try {
					appendExpression(makeHyperlink(variant.getHandleIdentifier(),
							variant.getExpressionString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the expression string for variant "
									+ variant.getElementName());
				}
				addComment(variant);
				endLevel1();
			}
		}
	}

	/**
	 * Append the comment attached to this element, if any.
	 * 
	 * @param element the commented element
	 */
	private void addComment(ICommentedElement element) {
		try {
			if (element.hasComment()) {
				String comment = element.getComment();
				if (comment.length() != 0)
					appendComment(UIUtils.XMLWrapUp(comment));
			}
		} catch (RodinDBException e) {
			// ignore
			if (UIUtils.DEBUG) e.printStackTrace();
		}
	}

	private void beginLevel0() {
		htmlString.append(BEGIN_LEVEL_0);
	}

	private void beginLevel1() {
		htmlString.append(BEGIN_LEVEL_1);
	}

	private void beginLevel2() {
		htmlString.append(BEGIN_LEVEL_2);
	}

	private void beginLevel3() {
		htmlString.append(BEGIN_LEVEL_3);
	}

	private void endLevel0() {
		htmlString.append(END_LEVEL_0);
	}

	private void endLevel1() {
		htmlString.append(END_LEVEL_1);
	}

	private void endLevel2() {
		htmlString.append(END_LEVEL_2);
	}

	private void endLevel3() {
		htmlString.append(END_LEVEL_3);
	}

	private void emptyLine() {
		htmlString.append(EMPTY_LINE);
	}
	
	private void keyword(String str, int level) {
		switch (level) {
		case 0:
			htmlString.append(BEGIN_KEYWORD_0);
			break;
		case 1:
			htmlString.append(BEGIN_KEYWORD_1);
			break;
		case 2:
			htmlString.append(BEGIN_KEYWORD_2);
			break;
		case 3:
			htmlString.append(BEGIN_KEYWORD_3);
			break;
		}
		
		htmlString.append(str);
		
		switch (level) {
		case 0:
			htmlString.append(END_KEYWORD_0);
			break;
		case 1:
			htmlString.append(END_KEYWORD_1);
			break;
		case 2:
			htmlString.append(END_KEYWORD_2);
			break;
		case 3:
			htmlString.append(END_KEYWORD_3);
			break;
		}
	}
	
	private void appendName(String label) {
		append(label, BEGIN_NAME_SEPARATOR, END_NAME_SEPARATOR); 
	}

	private void appendLabel(String label) {
		append(label, BEGIN_LABEL_SEPARATOR, END_LABEL_SEPARATOR); 
	}
	
	private void appendPredicate(String predicate) {
		append(predicate, BEGIN_PREDICATE_SEPARATOR, END_PREDICATE_SEPARATOR); 
	}

	private void appendAssignment(String predicate) {
		append(predicate, BEGIN_ASSIGNMENT_SEPARATOR, END_ASSIGNMENT_SEPARATOR); 
	}

	private void appendExpression(String predicate) {
		append(predicate, BEGIN_EXPRESSION_SEPARATOR, END_EXPRESSION_SEPARATOR); 
	}

	private void appendComment(String predicate) {
		append(predicate, BEGIN_COMMENT_SEPARATOR, END_COMMENT_SEPARATOR); 
	}

	private void append(String s, String beginSeparator, String endSeparator) {
		htmlString.append(BEGIN_ATTRIBUTE);
		// Printing multi-line
		StringTokenizer stringTokenizer = new StringTokenizer(s, "\n");
		if (stringTokenizer.countTokens() <= 1) {
			if (beginSeparator != null) {
				htmlString.append(SPACE);
				htmlString.append(beginSeparator);
				htmlString.append(SPACE);
			}
			htmlString.append(s);
			if (endSeparator != null) {
				htmlString.append(SPACE);
				htmlString.append(endSeparator);
				htmlString.append(SPACE);
			}
		}
		else {
			htmlString.append(BEGIN_MULTILINE);
			while (stringTokenizer.hasMoreTokens()) {
				String text = stringTokenizer.nextToken();
				htmlString.append(BEGIN_LINE);
				if (beginSeparator != null) {
					htmlString.append(SPACE);
					htmlString.append(beginSeparator);
					htmlString.append(SPACE);
				}
				htmlString.append(text);
				if (endSeparator != null) {
					htmlString.append(SPACE);
					htmlString.append(endSeparator);
					htmlString.append(SPACE);
				}
				htmlString.append(END_LINE);
			}
			htmlString.append(END_MULTILINE);
		}
		
		htmlString.append(END_ATTRIBUTE);
		
	}
	
	protected abstract String makeHyperlink(String link, String text);
}
