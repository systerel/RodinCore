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
package org.eventb.internal.ui.eventbeditor.prettyprint;

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
 * @author Markus Gaisbauer
 * @deprecated replaced by
 *             {@link org.eventb.internal.ui.eventbeditor.htmlpage.AstConverter}
 */
@Deprecated
public abstract class AstConverter {
	
	private static final String SPACE = " ";

	protected String BOLD = "";
	protected String END_BOLD = "";
	protected String ITALIC = "";
	protected String END_ITALIC = "";
	protected String BEGIN_LEVEL_0 = "";
	protected String BEGIN_LEVEL_1 = "";
	protected String BEGIN_LEVEL_2 = "";
	protected String BEGIN_LEVEL_3 = "";
	protected String END_LEVEL = "";
	protected String EMPTY_LINE = "";
	
	// The content string of the form text
	private StringBuilder formString;
	
	public AstConverter() {
		formString = new StringBuilder("<form>");
	}
	
	
	public String  getText(IProgressMonitor monitor, IRodinFile rodinFile) {
		formString.setLength(0);
		formString.append("<form>");
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
		append("</form>");

		return formString.toString();
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
			bold("MACHINE");
		} else if (rodinFile instanceof IContextFile) {
			bold("CONTEXT");
		}
		append(SPACE);
		final String handle = rodinFile.getHandleIdentifier();
		final String bareName = rodinFile.getBareName();
		append(makeHyperlink(handle, bareName));
		endLevel();
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
					emptyLine();
					beginLevel0();
					bold("REFINES");
					append(" ");
					append(makeHyperlink(EventBPlugin
							.getMachineFileName(name), name));
					endLevel();
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
					emptyLine();
					beginLevel0();
					bold("EXTENDS");
					append(SPACE);
					append(makeHyperlink(EventBPlugin
							.getContextFileName(name), name));
					endLevel();
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
			emptyLine();
			beginLevel0();
			bold("SEES");
			append(" ");
			for (int i = 0; i < length; i++) {
				try {
					if (i != 0)
						append(", ");
					append(
							makeHyperlink(rodinFile.getHandleIdentifier(),
									((SeesContext) seeContexts[i])
											.getSeenContextName()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get seen context name of "
									+ seeContexts[i].getElementName());
				}
			}
			endLevel();
		}
	}

	private void section(String s) {
		emptyLine();
		beginLevel0();
		bold(s);
		endLevel();
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
					append(makeHyperlink(var.getHandleIdentifier(), var
							.getIdentifierString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for variable "
									+ var.getElementName());
				}
				addComment(var);
				endLevel();
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
					append(makeHyperlink(inv.getHandleIdentifier(), inv
							.getLabel()));
					append(": " + UIUtils.XMLWrapUp(inv.getPredicateString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for invariant "
									+ inv.getElementName());
				}
				addComment(inv);
				endLevel();
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
					append(makeHyperlink(set.getHandleIdentifier(), set
							.getIdentifierString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for carrier set "
									+ set.getElementName());
					e.printStackTrace();
				}
				addComment(set);
				endLevel();
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
					append(makeHyperlink(cst.getHandleIdentifier(), cst
							.getIdentifierString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for constant "
									+ cst.getElementName());
					e.printStackTrace();
				}
				addComment(cst);
				endLevel();
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
					append(makeHyperlink(axm.getHandleIdentifier(), axm
							.getLabel()));
					append(": " + UIUtils.XMLWrapUp(axm.getPredicateString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for axiom "
									+ axm.getElementName());
				}
				addComment(axm);
				endLevel();
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
					append(makeHyperlink(thm.getHandleIdentifier(), thm
							.getLabel()));
					append(": " + UIUtils.XMLWrapUp(thm.getPredicateString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for theorem "
									+ thm.getElementName());
				}
				addComment(thm);
				endLevel();
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
					append(makeHyperlink(evt.getHandleIdentifier(), evt
							.getLabel()));
					endLevel();
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the label for event "
									+ evt.getElementName());
					continue;
				}
				addComment(evt);
				
				try {
					if (evt.isInherited()) {
						beginLevel2();
						italic("inherited");
						endLevel();
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
					beginLevel2();
					bold("REFINES");
					endLevel();
					for (IRefinesEvent refinesEvent: refinesEvents) {
						beginLevel3();
						try {
							append(makeHyperlink(refinesEvent
									.getHandleIdentifier(), refinesEvent
									.getAbstractEventLabel()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the abstract event label for refines event "
											+ refinesEvent.getElementName());
						}

						endLevel();
					}
				}

				if (lvars.length != 0) {
					beginLevel2();
					bold("ANY");
					endLevel();
					for (IVariable var: lvars) {
						beginLevel3();
						try {
							append(makeHyperlink(var
									.getHandleIdentifier(), var
									.getIdentifierString()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the identifier string for local variable "
											+ var.getElementName());
						}
						addComment(var);
						endLevel();
					}
					beginLevel2();
					bold("WHERE");
					endLevel();
				} else {
					beginLevel2();
					if (guards.length != 0) {
						bold("WHEN");
					} else {
						bold("BEGIN");
					}
					endLevel();
				}

				for (IGuard guard: guards) {
					beginLevel3();
					try {
						formString
								.append(makeHyperlink(guard
										.getHandleIdentifier(), guard
										.getLabel())
										+ ": "
										+ UIUtils.XMLWrapUp(guard
												.getPredicateString()));
					} catch (RodinDBException e) {
						EventBEditorUtils.debugAndLogError(e,
								"Cannot get details for guard "
										+ guard.getElementName());
					}
					addComment(guard);
					endLevel();
				}

				if (witnesses.length != 0) {
					beginLevel2();
					bold("WITNESSES");
					endLevel();
					for (IWitness witness: witnesses) {
						beginLevel3();
						try {
							append(makeHyperlink(witness
									.getHandleIdentifier(), witness.getLabel())
									+ ": "
									+ UIUtils.XMLWrapUp(witness
											.getPredicateString()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get details for guard "
											+ witness.getElementName());
						}
						addComment(witness);
						endLevel();
					}
				}

				if (guards.length != 0) {
					beginLevel2();
					bold("THEN");
					endLevel();
				}

				if (actions.length == 0) {
					beginLevel3();
					append("skip");
					endLevel();
				} else {
					for (IAction action: actions) {
						beginLevel3();
						try {
							append(makeHyperlink(action
									.getHandleIdentifier(), action.getLabel())
									+ ": "
									+ UIUtils.XMLWrapUp(action
											.getAssignmentString()));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get details for action "
									+ action.getElementName());
						}
						addComment(action);
						endLevel();
					}
				}
				beginLevel2();
				bold("END");
				endLevel();
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
					append(makeHyperlink(variant.getHandleIdentifier(),
							variant.getExpressionString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the expression string for variant "
									+ variant.getElementName());
				}
				addComment(variant);
				endLevel();
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
					append("          /* " + UIUtils.XMLWrapUp(comment) + " */");
			}
		} catch (RodinDBException e) {
			// ignore
			if (UIUtils.DEBUG) e.printStackTrace();
		}
	}

	private void beginLevel0() {
		formString.append(BEGIN_LEVEL_0);
	}

	private void beginLevel1() {
		formString.append(BEGIN_LEVEL_1);
	}

	private void beginLevel2() {
		formString.append(BEGIN_LEVEL_2);
	}

	private void beginLevel3() {
		formString.append(BEGIN_LEVEL_3);
	}

	private void endLevel() {
		formString.append(END_LEVEL);
	}

	private void emptyLine() {
		formString.append(EMPTY_LINE);
	}
	
	private void bold(String str) {
		formString.append(BOLD);
		formString.append(str);
		formString.append(END_BOLD);
	}
	
	private void italic(String str) {
		formString.append(ITALIC);
		formString.append(str);
		formString.append(END_ITALIC);
	}

	private void append(String s) {
		formString.append(s);
	}
	
	protected abstract String makeHyperlink(String link, String text);
}
