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
import org.eventb.core.IConvergenceElement.Convergence;
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
	protected String BEGIN_MASTER_KEYWORD = "";
	protected String BEGIN_KEYWORD_1 = "";
	protected String END_MASTER_KEYWORD = "";
	protected String END_KEYWORD_1 = "";
	protected String BEGIN_LEVEL_0 = "";
	protected String BEGIN_LEVEL_1 = "";
	protected String BEGIN_LEVEL_2 = "";
	protected String BEGIN_LEVEL_3 = "";
	protected String END_LEVEL_0 = "";
	protected String END_LEVEL_1 = "";
	protected String END_LEVEL_2 = "";
	protected String END_LEVEL_3 = "";
	protected String EMPTY_LINE = "";
	protected String BEGIN_MULTILINE = "";
	protected String END_MULTILINE = "";
	protected String BEGIN_LINE = "";
	protected String END_LINE = "";
	protected String BEGIN_COMPONENT_NAME = "";
	protected String END_COMPONENT_NAME = "";
	protected String BEGIN_COMPONENT_NAME_SEPARATOR = null;
	protected String END_COMPONENT_NAME_SEPARATOR = null;
	protected String BEGIN_COMMENT = "";
	protected String END_COMMENT = "";
	protected String BEGIN_COMMENT_SEPARATOR = "//";
	protected String END_COMMENT_SEPARATOR = null;
	protected String BEGIN_VARIABLE_IDENTIFIER = "";
	protected String END_VARIABLE_IDENTIFIER = "";
	protected String BEGIN_VARIABLE_IDENTIFIER_SEPARATOR = null;
	protected String END_VARIABLE_IDENTIFIER_SEPARATOR = null;
	protected String BEGIN_INVARIANT_LABEL = "";
	protected String END_INVARIANT_LABEL = "";
	protected String BEGIN_INVARIANT_LABEL_SEPARATOR = null;
	protected String END_INVARIANT_LABEL_SEPARATOR = ":";
	protected String BEGIN_INVARIANT_PREDICATE = "";
	protected String END_INVARIANT_PREDICATE = "";
	protected String BEGIN_INVARIANT_PREDICATE_SEPARATOR = null;
	protected String END_INVARIANT_PREDICATE_SEPARATOR = null;
	protected String BEGIN_THEOREM_LABEL = "";
	protected String END_THEOREM_LABEL = "";
	protected String BEGIN_THEOREM_LABEL_SEPARATOR = null;
	protected String END_THEOREM_LABEL_SEPARATOR = ":";
	protected String BEGIN_THEOREM_PREDICATE = "";
	protected String END_THEOREM_PREDICATE = "";
	protected String BEGIN_THEOREM_PREDICATE_SEPARATOR = null;
	protected String END_THEOREM_PREDICATE_SEPARATOR = null;
	protected String BEGIN_EVENT_LABEL = "";
	protected String END_EVENT_LABEL = "";
	protected String BEGIN_EVENT_LABEL_SEPARATOR = null;
	protected String END_EVENT_LABEL_SEPARATOR = "\u2259";
	protected String BEGIN_ABSTRACT_EVENT_LABEL = "";
	protected String END_ABSTRACT_EVENT_LABEL = "";
	protected String BEGIN_ABSTRACT_EVENT_LABEL_SEPARATOR = null;
	protected String END_ABSTRACT_EVENT_LABEL_SEPARATOR = null;
	protected String BEGIN_INHERITED = "";
	protected String END_INHERITED = "";
	protected String BEGIN_INHERITED_SEPARATOR = null;
	protected String END_INHERITED_SEPARATOR = null;
	protected String BEGIN_CONVERGENCE = "";
	protected String END_CONVERGENCE = "";
	protected String BEGIN_CONVERGENCE_SEPARATOR = null;
	protected String END_CONVERGENCE_SEPARATOR = null;
	protected String BEGIN_PARAMETER_IDENTIFIER = "";
	protected String END_PARAMETER_IDENTIFIER = "";
	protected String BEGIN_PARAMETER_IDENTIFIER_SEPARATOR = null;
	protected String END_PARAMETER_IDENTIFIER_SEPARATOR = null;
	protected String BEGIN_GUARD_LABEL = "";
	protected String END_GUARD_LABEL = "";
	protected String BEGIN_GUARD_LABEL_SEPARATOR = null;
	protected String END_GUARD_LABEL_SEPARATOR = ":";
	protected String BEGIN_GUARD_PREDICATE = "";
	protected String END_GUARD_PREDICATE = "";
	protected String BEGIN_GUARD_PREDICATE_SEPARATOR = null;
	protected String END_GUARD_PREDICATE_SEPARATOR = null;
	protected String BEGIN_WITNESS_LABEL = "";
	protected String END_WITNESS_LABEL = "";
	protected String BEGIN_WITNESS_LABEL_SEPARATOR = null;
	protected String END_WITNESS_LABEL_SEPARATOR = ":";
	protected String BEGIN_WITNESS_PREDICATE = "";
	protected String END_WITNESS_PREDICATE = "";
	protected String BEGIN_WITNESS_PREDICATE_SEPARATOR = null;
	protected String END_WITNESS_PREDICATE_SEPARATOR = null;
	protected String BEGIN_ACTION_LABEL = "";
	protected String END_ACTION_LABEL = "";
	protected String BEGIN_ACTION_LABEL_SEPARATOR = null;
	protected String END_ACTION_LABEL_SEPARATOR = ":";
	protected String BEGIN_ACTION_ASSIGNMENT = "";
	protected String END_ACTION_ASSIGNMENT = "";
	protected String BEGIN_ACTION_ASSIGNMENT_SEPARATOR = null;
	protected String END_ACTION_ASSIGNMENT_SEPARATOR = null;
	protected String BEGIN_VARIANT_EXPRESSION = "";
	protected String END_VARIANT_EXPRESSION = "";
	protected String BEGIN_VARIANT_EXPRESSION_SEPARATOR = null;
	protected String END_VARIANT_EXPRESSION_SEPARATOR = null;
	protected String BEGIN_SET_IDENTIFIER = "";
	protected String END_SET_IDENTIFIER = "";
	protected String BEGIN_SET_IDENTIFIER_SEPARATOR = null;
	protected String END_SET_IDENTIFIER_SEPARATOR = null;
	protected String BEGIN_AXIOM_LABEL = "";
	protected String END_AXIOM_LABEL = "";
	protected String BEGIN_AXIOM_LABEL_SEPARATOR = null;
	protected String END_AXIOM_LABEL_SEPARATOR = ":";
	protected String BEGIN_AXIOM_PREDICATE = "";
	protected String END_AXIOM_PREDICATE = "";
	protected String BEGIN_AXIOM_PREDICATE_SEPARATOR = null;
	protected String END_AXIOM_PREDICATE_SEPARATOR = null;
	protected String BEGIN_CONSTANT_IDENTIFIER = "";
	protected String END_CONSTANT_IDENTIFIER = "";
	protected String BEGIN_CONSTANT_IDENTIFIER_SEPARATOR = null;
	protected String END_CONSTANT_IDENTIFIER_SEPARATOR = null;
	
	// The content string of the form text
	private StringBuilder htmlString;
	
	public AstConverter() {
		htmlString = new StringBuilder("");
	}
	
	
	public String getText(IProgressMonitor monitor, IRodinFile rodinFile) {
		htmlString.setLength(0);
		htmlString.append(HEADER);
		addDeclaration(rodinFile);
		addDependencies(rodinFile, monitor);
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
		masterKeyword("END");
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
	private void addDeclaration(IRodinFile rodinFile) {
		// Print the Machine/Context name
		beginLevel0();
		if (rodinFile instanceof IMachineFile) {
			masterKeyword("MACHINE");
		} else if (rodinFile instanceof IContextFile) {
			masterKeyword("CONTEXT");
		}
		endLevel0();
		beginLevel1();
		final String handle = rodinFile.getHandleIdentifier();
		final String bareName = rodinFile.getBareName();
		appendComponentName(makeHyperlink(handle, wrapString(bareName)));
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
	private void addDependencies(IRodinFile rodinFile,
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
					masterKeyword("REFINES");
					beginLevel1();
					appendComponentName(makeHyperlink(EventBPlugin
							.getMachineFileName(name), wrapString(name)));
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
					masterKeyword("EXTENDS");
					beginLevel1();
					appendComponentName(makeHyperlink(EventBPlugin
							.getContextFileName(name), wrapString(name)));
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
			masterKeyword("SEES");
			for (int i = 0; i < length; i++) {
				try {
					beginLevel1();
					appendComponentName(
							makeHyperlink(rodinFile.getHandleIdentifier(),
									wrapString(((SeesContext) seeContexts[i])
											.getSeenContextName())));
					endLevel1();
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get seen context name of "
									+ seeContexts[i].getElementName());
				}
			}
		}
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
			masterKeyword("VARIABLES");
			for (IVariable var : vars) {
				beginLevel1();
				try {
					appendVariableIdentifier(makeHyperlink(var
							.getHandleIdentifier(), wrapString(var
							.getIdentifierString())));
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
			masterKeyword("INVARIANTS");
			for (IInvariant inv: invs) {
				beginLevel1();
				try {
					appendInvariantLabel(makeHyperlink(inv.getHandleIdentifier(), wrapString(inv
							.getLabel())));
					appendInvariantPredicate(wrapString(inv.getPredicateString()));
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
			masterKeyword("SETS");
			for (ICarrierSet set: sets) {
				beginLevel1();
				try {
					appendSetIdentifier(makeHyperlink(
							set.getHandleIdentifier(), wrapString(set
									.getIdentifierString())));
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
			masterKeyword("CONSTANTS");
			for (IConstant cst: csts) {
				beginLevel1();
				try {
					appendConstantIdentifier(makeHyperlink(cst.getHandleIdentifier(), wrapString(cst
							.getIdentifierString())));
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
			masterKeyword("AXIOMS");
			for (IAxiom axm: axms) {
				beginLevel1();
				try {
					appendAxiomLabel(makeHyperlink(axm.getHandleIdentifier(), wrapString(axm
							.getLabel())));
					appendAxiomPredicate(wrapString(axm.getPredicateString()));
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
			masterKeyword("THEOREMS");
			for (ITheorem thm: thms) {
				beginLevel1();
				try {
					appendTheoremLabel(makeHyperlink(thm.getHandleIdentifier(),
							wrapString(thm.getLabel())));
					appendTheoremPredicate(wrapString(thm.getPredicateString()));
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
			masterKeyword("EVENTS");
			for (IEvent evt: evts) {
				try {
					emptyLine();
					beginLevel1();
					appendEventLabel(makeHyperlink(evt.getHandleIdentifier(),
							wrapString(evt.getLabel())));
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
						appendInherited();
						endLevel2();
						continue;
					}
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleGetAttributeException(e);
					continue;
				}
				IVariable[] params;
				IGuard[] guards;
				IAction[] actions;
				IRefinesEvent[] refinesEvents;
				IWitness[] witnesses;
				try {
					refinesEvents = evt
							.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
					params = evt.getChildrenOfType(IVariable.ELEMENT_TYPE);
					guards = evt.getChildrenOfType(IGuard.ELEMENT_TYPE);
					witnesses = evt.getChildrenOfType(IWitness.ELEMENT_TYPE);
					actions = evt.getChildrenOfType(IAction.ELEMENT_TYPE);
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the children for event "
									+ evt.getElementName());
					continue;
				}

				try {
					Convergence convergence = evt.getConvergence();
					keyword("WHICH IS", 1);
					beginLevel3();
					appendConvergence(convergence);
					endLevel3();
				}
				catch (RodinDBException e) {
					// Do nothing
				}
				
				if (refinesEvents.length != 0) {
					keyword("REFINES", 1);
					for (IRefinesEvent refinesEvent: refinesEvents) {
						beginLevel3();
						try {
							appendAbstractEventLabel(makeHyperlink(refinesEvent
									.getHandleIdentifier(), wrapString(refinesEvent
									.getAbstractEventLabel())));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the abstract event label for refines event "
											+ refinesEvent.getElementName());
						}

						endLevel3();
					}
				}

				if (params.length != 0) {
					keyword("ANY", 1);
					for (IVariable param: params) {
						beginLevel3();
						try {
							appendParameterIdentifier(makeHyperlink(param
									.getHandleIdentifier(), wrapString(param
									.getIdentifierString())));
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the identifier string for local variable "
											+ param.getElementName());
						}
						addComment(param);
						endLevel3();
					}
					keyword("WHERE", 1);
				} else {
					if (guards.length != 0) {
						keyword("WHEN", 1);
					} else if (witnesses.length != 0) {
						keyword("WITH", 1);
					} else {
						keyword("BEGIN", 1);
					}
					
				}

				for (IGuard guard: guards) {
					beginLevel3();
					try {
						appendGuardLabel(makeHyperlink(guard
										.getHandleIdentifier(), wrapString(guard
										.getLabel())));
						appendGuardPredicate(wrapString(guard
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
					if (params.length != 0 || guards.length != 0) {
						keyword("WITH", 1);
					}
					for (IWitness witness: witnesses) {
						beginLevel3();
						try {
							appendWitnessLabel(makeHyperlink(witness
									.getHandleIdentifier(), wrapString(witness.getLabel())));
							appendWitnessPredicate(wrapString(witness
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

				if (params.length != 0 || guards.length != 0
						|| witnesses.length != 0) {
					keyword("THEN", 1);
				}

				if (actions.length == 0) {
					beginLevel3();
					appendActionAssignment("skip");
					endLevel3();
				} else {
					for (IAction action: actions) {
						beginLevel3();
						try {
							appendActionLabel(makeHyperlink(action
									.getHandleIdentifier(), wrapString(action
									.getLabel())));
							appendActionAssignment(wrapString(action
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
				keyword("END", 1);
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
			masterKeyword("VARIANT");
			for (IVariant variant: variants) {
				beginLevel1();
				try {
					appendVariantExpression(makeHyperlink(variant.getHandleIdentifier(),
							wrapString(variant.getExpressionString())));
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
					appendComment(wrapString(comment));
			}
		} catch (RodinDBException e) {
			// ignore
			if (UIUtils.DEBUG)
				e.printStackTrace();
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
	
	private void masterKeyword(String str) {
		htmlString.append(BEGIN_MASTER_KEYWORD);
		htmlString.append(str);
		htmlString.append(END_MASTER_KEYWORD);
	}
	
	private void keyword(String str, int level) {
		switch (level) {
		case 0:
			htmlString.append(BEGIN_MASTER_KEYWORD);
			break;
		case 1:
			htmlString.append(BEGIN_KEYWORD_1);
			break;
		}
		
		htmlString.append(str);
		
		switch (level) {
		case 0:
			htmlString.append(END_MASTER_KEYWORD);
			break;
		case 1:
			htmlString.append(END_KEYWORD_1);
			break;
		}
	}

	private void appendComponentName(String label) {
		append(label, BEGIN_COMPONENT_NAME, END_COMPONENT_NAME,
				BEGIN_COMPONENT_NAME_SEPARATOR, END_COMPONENT_NAME_SEPARATOR);
	}

	private void appendVariableIdentifier(String identifier) {
		append(identifier, BEGIN_VARIABLE_IDENTIFIER, END_VARIABLE_IDENTIFIER,
				BEGIN_VARIABLE_IDENTIFIER_SEPARATOR,
				END_VARIABLE_IDENTIFIER_SEPARATOR);
	}

	private void appendParameterIdentifier(String identifier) {
		append(identifier, BEGIN_PARAMETER_IDENTIFIER, END_PARAMETER_IDENTIFIER,
				BEGIN_PARAMETER_IDENTIFIER_SEPARATOR,
				END_PARAMETER_IDENTIFIER_SEPARATOR); 
	}

	private void appendSetIdentifier(String identifier) {
		append(identifier, BEGIN_SET_IDENTIFIER, END_SET_IDENTIFIER,
				BEGIN_SET_IDENTIFIER_SEPARATOR,
				END_SET_IDENTIFIER_SEPARATOR);
	}

	private void appendConstantIdentifier(String identifier) {
		append(identifier, BEGIN_CONSTANT_IDENTIFIER, END_CONSTANT_IDENTIFIER,
				BEGIN_CONSTANT_IDENTIFIER_SEPARATOR,
				END_CONSTANT_IDENTIFIER_SEPARATOR); 
	}

	private void appendInvariantLabel(String label) {
		append(label, BEGIN_INVARIANT_LABEL, END_INVARIANT_LABEL,
				BEGIN_INVARIANT_LABEL_SEPARATOR, END_INVARIANT_LABEL_SEPARATOR);
	}

	private void appendTheoremLabel(String label) {
		append(label, BEGIN_THEOREM_LABEL, END_THEOREM_LABEL,
				BEGIN_THEOREM_LABEL_SEPARATOR, END_THEOREM_LABEL_SEPARATOR);
	}

	private void appendEventLabel(String label) {
		append(label, BEGIN_EVENT_LABEL, END_EVENT_LABEL,
				BEGIN_EVENT_LABEL_SEPARATOR, END_EVENT_LABEL_SEPARATOR);
	}

	private void appendGuardLabel(String label) {
		append(label, BEGIN_GUARD_LABEL, END_GUARD_LABEL,
				BEGIN_GUARD_LABEL_SEPARATOR, END_GUARD_LABEL_SEPARATOR);
	}

	private void appendWitnessLabel(String label) {
		append(label, BEGIN_WITNESS_LABEL, END_WITNESS_LABEL,
				BEGIN_WITNESS_LABEL_SEPARATOR, END_WITNESS_LABEL_SEPARATOR);
	}

	private void appendAxiomLabel(String label) {
		append(label, BEGIN_AXIOM_LABEL, END_AXIOM_LABEL,
				BEGIN_AXIOM_LABEL_SEPARATOR, END_AXIOM_LABEL_SEPARATOR);
	}

	private void appendAbstractEventLabel(String label) {
		append(label, BEGIN_ABSTRACT_EVENT_LABEL, END_ABSTRACT_EVENT_LABEL,
				BEGIN_ABSTRACT_EVENT_LABEL_SEPARATOR,
				END_ABSTRACT_EVENT_LABEL_SEPARATOR);
	}

	private void appendActionLabel(String label) {
		append(label, BEGIN_ACTION_LABEL, END_ACTION_LABEL,
				BEGIN_ACTION_LABEL_SEPARATOR, END_ACTION_LABEL_SEPARATOR);
	}

	private void appendInvariantPredicate(String predicate) {
		append(predicate, BEGIN_INVARIANT_PREDICATE, END_INVARIANT_PREDICATE,
				BEGIN_INVARIANT_PREDICATE_SEPARATOR,
				END_INVARIANT_PREDICATE_SEPARATOR);
	}

	private void appendTheoremPredicate(String predicate) {
		append(predicate, BEGIN_THEOREM_PREDICATE, END_THEOREM_PREDICATE,
				BEGIN_THEOREM_PREDICATE_SEPARATOR,
				END_THEOREM_PREDICATE_SEPARATOR);
	}

	private void appendGuardPredicate(String predicate) {
		append(predicate, BEGIN_GUARD_PREDICATE, END_GUARD_PREDICATE,
				BEGIN_GUARD_PREDICATE_SEPARATOR,
				END_GUARD_PREDICATE_SEPARATOR);
	}

	private void appendWitnessPredicate(String predicate) {
		append(predicate, BEGIN_WITNESS_PREDICATE, END_WITNESS_PREDICATE,
				BEGIN_WITNESS_PREDICATE_SEPARATOR,
				END_WITNESS_PREDICATE_SEPARATOR);
	}

	private void appendAxiomPredicate(String predicate) {
		append(predicate, BEGIN_AXIOM_PREDICATE, END_AXIOM_PREDICATE,
				BEGIN_AXIOM_PREDICATE_SEPARATOR,
				END_AXIOM_PREDICATE_SEPARATOR);
	}

	private void appendActionAssignment(String assignment) {
		append(assignment, BEGIN_ACTION_ASSIGNMENT, END_ACTION_ASSIGNMENT, BEGIN_ACTION_ASSIGNMENT_SEPARATOR,
				END_ACTION_ASSIGNMENT_SEPARATOR); 
	}

	private void appendVariantExpression(String predicate) {
		append(predicate, BEGIN_VARIANT_EXPRESSION, END_VARIANT_EXPRESSION,
				BEGIN_VARIANT_EXPRESSION_SEPARATOR,
				END_VARIANT_EXPRESSION_SEPARATOR);
	}

	private void appendComment(String comment) {
		append(comment, BEGIN_COMMENT, END_COMMENT, BEGIN_COMMENT_SEPARATOR,
				END_COMMENT_SEPARATOR);
	}

	private void appendInherited() {
		append("inherited", BEGIN_INHERITED, END_INHERITED,
				BEGIN_INHERITED_SEPARATOR, END_INHERITED_SEPARATOR);
	}

	private void appendConvergence(Convergence convergence) {
		String string = "ordinary";
		if (convergence == Convergence.ORDINARY) {
			string = "ordinary";
		}
		else if (convergence == Convergence.ANTICIPATED) {
			string = "anticipated";
		}
		else if (convergence == Convergence.CONVERGENT) {
			string = "convergent";
		}
		append(string, BEGIN_CONVERGENCE, END_CONVERGENCE,
				BEGIN_CONVERGENCE_SEPARATOR, END_CONVERGENCE_SEPARATOR);
		
	}

	private void append(String s, String begin, String end,
			String beginSeparator, String endSeparator) {
		StringTokenizer stringTokenizer = new StringTokenizer(s, "\n");
		if (stringTokenizer.countTokens() <= 1) {
			htmlString.append(begin);
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
			htmlString.append(end);
		}
		else {
			// Printing multi-line
			htmlString.append(BEGIN_MULTILINE);
			while (stringTokenizer.hasMoreTokens()) {
				String text = stringTokenizer.nextToken();
				htmlString.append(BEGIN_LINE);
				htmlString.append(begin);
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
				htmlString.append(end);
				htmlString.append(END_LINE);
			}
			htmlString.append(END_MULTILINE);
		}
		
	}
	
	protected abstract String makeHyperlink(String link, String text);

	protected abstract String wrapString(String text);

}
