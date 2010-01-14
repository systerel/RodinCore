/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 * 	   Systerel - fixed bug #2884774 : display guards marked as theorems
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IDerivedPredicateElement;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.basis.SeesContext;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
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
	protected String BEGIN_THEOREM_LABEL = "";
	protected String END_THEOREM_LABEL = "";
	protected String BEGIN_INVARIANT_LABEL_SEPARATOR = null;
	protected String END_INVARIANT_LABEL_SEPARATOR = ":";
	protected String BEGIN_INVARIANT_PREDICATE = "";
	protected String END_INVARIANT_PREDICATE = "";
	protected String BEGIN_INVARIANT_PREDICATE_SEPARATOR = null;
	protected String END_INVARIANT_PREDICATE_SEPARATOR = null;
	protected String BEGIN_EVENT_LABEL = "";
	protected String END_EVENT_LABEL = "";
	protected String BEGIN_EVENT_LABEL_SEPARATOR = null;
	protected String END_EVENT_LABEL_SEPARATOR = "\u2259";
	protected String BEGIN_ABSTRACT_EVENT_LABEL = "";
	protected String END_ABSTRACT_EVENT_LABEL = "";
	protected String BEGIN_ABSTRACT_EVENT_LABEL_SEPARATOR = null;
	protected String END_ABSTRACT_EVENT_LABEL_SEPARATOR = null;
	protected String BEGIN_EXTENDED = "";
	protected String END_EXTENDED = "";
	protected String BEGIN_EXTENDED_SEPARATOR = null;
	protected String END_EXTENDED_SEPARATOR = null;
	protected String BEGIN_CONVERGENCE = "";
	protected String END_CONVERGENCE = "";
	protected String BEGIN_CONVERGENCE_SEPARATOR = null;
	protected String END_CONVERGENCE_SEPARATOR = null;
	protected String BEGIN_PARAMETER_IDENTIFIER = "";
	protected String END_PARAMETER_IDENTIFIER = "";
	protected String BEGIN_IMPLICIT_PARAMETER_IDENTIFIER = "";
	protected String END_IMPLICIT_PARAMETER_IDENTIFIER = "";
	protected String BEGIN_PARAMETER_IDENTIFIER_SEPARATOR = null;
	protected String END_PARAMETER_IDENTIFIER_SEPARATOR = null;
	protected String BEGIN_GUARD_LABEL = "";
	protected String END_GUARD_LABEL = "";
	protected String BEGIN_GUARD_THEOREM_LABEL = "";
	protected String END_GUARD_THEOREM_LABEL = "";
	protected String BEGIN_IMPLICIT_GUARD_LABEL = "";
	protected String END_IMPLICIT_GUARD_LABEL = "";
	protected String BEGIN_IMPLICIT_GUARD_THEOREM_LABEL = "";
	protected String END_IMPLICIT_GUARD_THEOREM_LABEL = "";
	protected String BEGIN_GUARD_LABEL_SEPARATOR = null;
	protected String END_GUARD_LABEL_SEPARATOR = ":";
	protected String BEGIN_GUARD_PREDICATE = "";
	protected String END_GUARD_PREDICATE = "";
	protected String BEGIN_IMPLICIT_GUARD_PREDICATE = "";
	protected String END_IMPLICIT_GUARD_PREDICATE = "";
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
	protected String BEGIN_IMPLICIT_ACTION_LABEL = "";
	protected String END_IMPLICIT_ACTION_LABEL = "";
	protected String BEGIN_ACTION_LABEL_SEPARATOR = null;
	protected String END_ACTION_LABEL_SEPARATOR = ":";
	protected String BEGIN_ACTION_ASSIGNMENT = "";
	protected String END_ACTION_ASSIGNMENT = "";
	protected String BEGIN_IMPLICIT_ACTION_ASSIGNMENT = "";
	protected String END_IMPLICIT_ACTION_ASSIGNMENT = "";
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
	
	
	public String getText(IProgressMonitor monitor, IInternalElement root) {
		htmlString.setLength(0);
		htmlString.append(HEADER);
		addDeclaration(root);
		addDependencies(root, monitor);
		if (root instanceof IMachineRoot) {
			IMachineRoot mch = (IMachineRoot) root ;
			addVariables(mch, monitor);
			addInvariants(mch, monitor);
			addEvents(mch, monitor);
			addVariant(mch, monitor);
		} else if (root instanceof IContextRoot) {
			IContextRoot ctx = (IContextRoot) root;
			addCarrierSets(ctx, monitor);
			addConstants(ctx, monitor);
			addAxioms(ctx, monitor);
		}
		masterKeyword("END");
		htmlString.append(FOOTER);

		return htmlString.toString();
	}
	
	
	/**
	 * This private helper method adds the component name to the content string
	 * <p>
	 * 
	 * @param root
	 *            the root of rodin input file
	 */
	private void addDeclaration(IInternalElement root) {
		// Print the Machine/Context name
		beginLevel0();
		if (root instanceof IMachineRoot) {
			masterKeyword("MACHINE");
		} else if (root instanceof IContextRoot) {
			masterKeyword("CONTEXT");
		}
		endLevel0();
		beginLevel1();
		final String handle = root.getHandleIdentifier();
		final String bareName = root.getRodinFile().getBareName();
		appendComponentName(makeHyperlink(handle, wrapString(bareName)));
		if (root instanceof ICommentedElement) {
			addComment((ICommentedElement) root);
		}
		endLevel1();
		return;
	}

	/**
	 * This private helper method adds component's dependency information to the
	 * content string
	 * <p>
	 * 
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addDependencies(IInternalElement root,
			IProgressMonitor monitor) {
		if (root instanceof IMachineRoot) {
			// REFINES clause
			IRodinElement[] refines;
			try {
				refines = root
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
								+ root.getRodinFile().getElementName());
			}

		} else if (root instanceof IContextRoot) {
			// EXTENDS clause
			IRodinElement[] extendz;
			try {
				extendz = root
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
								+ root.getRodinFile().getElementName());
			}

		}

		// SEES clause for both context and machine
		IRodinElement[] seeContexts;
		try {
			seeContexts = root
					.getChildrenOfType(ISeesContext.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get sees machine of "
					+ root.getRodinFile().getElementName());
			return;
		}

		int length = seeContexts.length;
		if (length != 0) {
			masterKeyword("SEES");
			for (int i = 0; i < length; i++) {
				try {
					beginLevel1();
					appendComponentName(
							makeHyperlink(root.getHandleIdentifier(),
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
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addVariables(IMachineRoot root, IProgressMonitor monitor) {
		IVariable[] vars;
		try {
			vars = root.getChildrenOfType(IVariable.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variables for "
					+ root.getRodinFile().getElementName());
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
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addInvariants(IMachineRoot root, IProgressMonitor monitor) {
		IInvariant[] invs;
		try {
			invs = root.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get invariants for "
					+ root.getRodinFile().getElementName());
			return;
		}
		if (invs.length != 0) {
			masterKeyword("INVARIANTS");
			for (IInvariant inv: invs) {
				beginLevel1();
				try {
					final String handle = inv.getHandleIdentifier();
					final String label = wrapString(inv.getLabel());
					final String hyperlink = makeHyperlink(handle, label);
					final boolean isTheorem = isTheorem(inv);
					appendInvariantLabel(hyperlink, isTheorem);
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
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addCarrierSets(IContextRoot root, IProgressMonitor monitor) {
		ICarrierSet[] sets;
		try {
			sets = root.getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils
					.debugAndLogError(e, "Cannot get carrier sets for "
							+ root.getRodinFile().getElementName());
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
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addConstants(IContextRoot root, IProgressMonitor monitor) {
		IConstant[] csts;
		try {
			csts = root.getChildrenOfType(IConstant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get constants for "
					+ root.getRodinFile().getElementName());
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
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addAxioms(IContextRoot root, IProgressMonitor monitor) {
		IAxiom[] axms;
		try {
			axms = root.getChildrenOfType(IAxiom.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get axioms for "
					+ root.getRodinFile().getElementName());
			return;
		}
		if (axms.length != 0) {
			masterKeyword("AXIOMS");
			for (IAxiom axm: axms) {
				beginLevel1();
				try {
					final String handle = axm.getHandleIdentifier();
					final String label = wrapString(axm.getLabel());
					final boolean isTheorem = isTheorem(axm);
					appendAxiomLabel(makeHyperlink(handle, label), isTheorem);
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

	private boolean isTheorem(IDerivedPredicateElement elem)
			throws RodinDBException {
		return elem.hasTheorem() && elem.isTheorem();
	}

	/**
	 * This private helper method adds component's information about events to
	 * the content string
	 * <p>
	 * 
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addEvents(IMachineRoot root, IProgressMonitor monitor) {
		IEvent[] evts;
		try {
			evts = root.getChildrenOfType(IEvent.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get events for "
					+ root.getRodinFile().getElementName());
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
					if (evt.hasExtended() && evt.isExtended()) {
						beginLevel2();
						appendExtended();
						endLevel2();
					}
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleGetAttributeException(e);
					continue;
				}
				List<IParameter> params;
				List<IGuard> guards;
				List<IAction> actions;
				IRefinesEvent[] refinesEvents;
				IWitness[] witnesses;
				try {
					refinesEvents = evt
							.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
					params = UIUtils.getVisibleChildrenOfType(evt, IParameter.ELEMENT_TYPE);
					guards = UIUtils.getVisibleChildrenOfType(evt, IGuard.ELEMENT_TYPE);
					witnesses = evt.getChildrenOfType(IWitness.ELEMENT_TYPE);
					actions = UIUtils.getVisibleChildrenOfType(evt, IAction.ELEMENT_TYPE);
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the children for event "
									+ evt.getElementName());
					continue;
				}

				try {
					Convergence convergence = evt.getConvergence();
					keyword("STATUS", 1);
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

				if (params.size() != 0) {
					keyword("ANY", 1);
					for (IParameter param: params) {
						beginLevel3();
						try {
							appendParameterIdentifier(param, evt);
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the identifier string for parameter "
											+ param.getElementName());
						}
						addComment(param);
						endLevel3();
					}
					keyword("WHERE", 1);
				} else {
					if (guards.size() != 0) {
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
						final boolean isTheorem = isTheorem(guard);
						appendGuard(guard, evt, isTheorem);
					} catch (RodinDBException e) {
						EventBEditorUtils.debugAndLogError(e,
								"Cannot get details for guard "
										+ guard.getElementName());
					}
					addComment(guard);
					endLevel3();
				}

				if (witnesses.length != 0) {
					if (params.size() != 0 || guards.size() != 0) {
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

				if (params.size() != 0 || guards.size() != 0
						|| witnesses.length != 0) {
					keyword("THEN", 1);
				}

				if (actions.size() == 0) {
					beginLevel3();
					appendActionAssignment("skip");
					endLevel3();
				} else {
					for (IAction action: actions) {
						beginLevel3();
						try {
							appendAction(action, evt);
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
	 * @param root
	 *            the root of rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addVariant(IMachineRoot root, IProgressMonitor monitor) {
		IVariant[] variants;
		try {
			variants = root.getChildrenOfType(IVariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variants for "
					+ root.getRodinFile().getElementName());
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


	
	private String getBeginParameterIdentifier(IParameter prm, IEvent evt) {
		return getDirectOrImplicitChild(evt, prm, BEGIN_PARAMETER_IDENTIFIER,
				BEGIN_IMPLICIT_PARAMETER_IDENTIFIER);
	}

	private String getEndParameterIdentifier(IParameter prm, IEvent evt) {
		return getDirectOrImplicitChild(evt, prm, END_PARAMETER_IDENTIFIER,
				END_IMPLICIT_PARAMETER_IDENTIFIER);
	}

	private void appendParameterIdentifier(IParameter prm, IEvent evt)
			throws RodinDBException {
		final String identifier = makeHyperlink(prm.getHandleIdentifier(),
				wrapString(prm.getIdentifierString()));
		final String bpi = getBeginParameterIdentifier(prm, evt);
		final String epi = getEndParameterIdentifier(prm, evt);

		append(identifier, bpi, epi, BEGIN_PARAMETER_IDENTIFIER_SEPARATOR,
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

	private void appendInvariantLabel(String label, boolean isTheorem) {
		final String begin, end;
		if (isTheorem) {
			begin = BEGIN_THEOREM_LABEL;
			end = END_THEOREM_LABEL;
		} else {
			begin = BEGIN_INVARIANT_LABEL;
			end = END_INVARIANT_LABEL;
		}
		append(label, begin, end, BEGIN_INVARIANT_LABEL_SEPARATOR,
				END_INVARIANT_LABEL_SEPARATOR);
	}

	private void appendEventLabel(String label) {
		append(label, BEGIN_EVENT_LABEL, END_EVENT_LABEL,
				BEGIN_EVENT_LABEL_SEPARATOR, END_EVENT_LABEL_SEPARATOR);
	}

	private String getBeginGuardLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChild(evt, grd, BEGIN_GUARD_LABEL,
				BEGIN_IMPLICIT_GUARD_LABEL);
	}

	private String getEndGuardLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChild(evt, grd, END_GUARD_LABEL,
				END_IMPLICIT_GUARD_LABEL);
	}
	
	private String getBeginGuardTheoremLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChild(evt, grd, BEGIN_GUARD_THEOREM_LABEL,
				BEGIN_IMPLICIT_GUARD_THEOREM_LABEL);
	}

	private String getEndGuardTheoremLabel(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChild(evt, grd, END_GUARD_THEOREM_LABEL,
				END_IMPLICIT_GUARD_THEOREM_LABEL);
	}

	private String getBeginGuardPredicate(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChild(evt, grd, BEGIN_GUARD_PREDICATE,
				BEGIN_IMPLICIT_GUARD_PREDICATE);
	}

	private String getEndGuardPredicate(IGuard grd, IEvent evt) {
		return getDirectOrImplicitChild(evt, grd, END_GUARD_PREDICATE,
				END_IMPLICIT_GUARD_PREDICATE);
	}
	
	private void appendGuard(IGuard grd, IEvent evt, boolean isTheorem)
			throws RodinDBException {
		appendGuardLabel(grd, evt, isTheorem);
		appendGuardPredicate(grd, evt, isTheorem);
	}

	private void appendGuardLabel(IGuard grd, IEvent evt, boolean isTheorem)
			throws RodinDBException {
		final String label = makeHyperlink(grd.getHandleIdentifier(),
				wrapString(grd.getLabel()));
		final String bgl;
		final String egl;
		if (isTheorem) {
			bgl = getBeginGuardTheoremLabel(grd, evt);
			egl = getEndGuardTheoremLabel(grd, evt);
		} else {
			bgl = getBeginGuardLabel(grd, evt);
			egl = getEndGuardLabel(grd, evt);
		}
		append(label, bgl, egl, BEGIN_GUARD_LABEL_SEPARATOR,
				END_GUARD_LABEL_SEPARATOR);
	}

	private void appendGuardPredicate(IGuard grd, IEvent evt, boolean isTheorem)
			throws RodinDBException {
		final String predicate = wrapString(grd.getPredicateString());
		final String bgp = getBeginGuardPredicate(grd, evt);
		final String egp = getEndGuardPredicate(grd, evt);
		append(predicate, bgp, egp, BEGIN_GUARD_PREDICATE_SEPARATOR,
				END_GUARD_PREDICATE_SEPARATOR);
	}

	private void appendWitnessLabel(String label) {
		append(label, BEGIN_WITNESS_LABEL, END_WITNESS_LABEL,
				BEGIN_WITNESS_LABEL_SEPARATOR, END_WITNESS_LABEL_SEPARATOR);
	}

	private void appendAxiomLabel(String label, boolean isTheorem) {
		final String begin, end;
		if (isTheorem) {
			begin = BEGIN_THEOREM_LABEL;
			end = END_THEOREM_LABEL;
		} else {
			begin = BEGIN_AXIOM_LABEL;
			end = END_AXIOM_LABEL;
		}
		append(label, begin, end, BEGIN_AXIOM_LABEL_SEPARATOR,
				END_AXIOM_LABEL_SEPARATOR);
	}

	private void appendAbstractEventLabel(String label) {
		append(label, BEGIN_ABSTRACT_EVENT_LABEL, END_ABSTRACT_EVENT_LABEL,
				BEGIN_ABSTRACT_EVENT_LABEL_SEPARATOR,
				END_ABSTRACT_EVENT_LABEL_SEPARATOR);
	}

	private String getBeginActionLabel(IAction act, IEvent evt) {
		return getDirectOrImplicitChild(evt, act, BEGIN_ACTION_LABEL,
				BEGIN_IMPLICIT_ACTION_LABEL);
	}

	private String getEndActionLabel(IAction act, IEvent evt) {
		return getDirectOrImplicitChild(evt, act, END_ACTION_LABEL,
				END_IMPLICIT_ACTION_LABEL);
	}

	private String getBeginActionAssignment(IAction act, IEvent evt) {
		return getDirectOrImplicitChild(evt, act, BEGIN_ACTION_ASSIGNMENT,
				BEGIN_IMPLICIT_ACTION_ASSIGNMENT);
	}

	private String getEndActionAssignment(IAction act, IEvent evt) {
		return getDirectOrImplicitChild(evt, act, END_ACTION_ASSIGNMENT,
				END_IMPLICIT_ACTION_ASSIGNMENT);
	}

	private void appendAction(IAction act, IEvent evt) throws RodinDBException {
		final String label = makeHyperlink(act.getHandleIdentifier(),
				wrapString(act.getLabel()));
		final String assignment = wrapString(act.getAssignmentString());

		final String bal = getBeginActionLabel(act, evt);
		final String eal = getEndActionLabel(act, evt);
		final String baa = getBeginActionAssignment(act, evt);
		final String eaa = getEndActionAssignment(act, evt);

		append(label, bal, eal, BEGIN_ACTION_LABEL_SEPARATOR,
				END_ACTION_LABEL_SEPARATOR);
		append(assignment, baa, eaa, BEGIN_ACTION_ASSIGNMENT_SEPARATOR,
				END_ACTION_ASSIGNMENT_SEPARATOR);
	}

	private void appendInvariantPredicate(String predicate) {
		append(predicate, BEGIN_INVARIANT_PREDICATE, END_INVARIANT_PREDICATE,
				BEGIN_INVARIANT_PREDICATE_SEPARATOR,
				END_INVARIANT_PREDICATE_SEPARATOR);
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

	private void appendExtended() {
		append("extended", BEGIN_EXTENDED, END_EXTENDED,
				BEGIN_EXTENDED_SEPARATOR, END_EXTENDED_SEPARATOR);
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

	private String getDirectOrImplicitChild(IEvent event, IInternalElement child,
			String directChildId, String implicitChildId) {
		return (child.getParent().equals(event)) ? directChildId
				: implicitChildId;
	}
	
	protected abstract String makeHyperlink(String link, String text);

	protected abstract String wrapString(String text);

}
