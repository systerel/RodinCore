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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
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
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the
 *         <code>org.eventbui.eventbeditor.EventBEditorPage</code> class and
 *         provides a pretty print page for Event-B Editor
 */
public class PrettyPrintPage extends EventBEditorPage implements
		IElementChangedListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".prettyprintpage"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_prettyPrint_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_prettyPrint_tabTitle;

	// The scrolled form
	private ScrolledForm form;

	// The form text
	private IEventBFormText formText;

	// The content string of the form text
	private String formString;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public PrettyPrintPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
		((IEventBEditor) editor).addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayout(new FillLayout());

		FormText widget = managedForm.getToolkit().createFormText(body, true);

		widget.addHyperlinkListener(new HyperlinkAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
			 */
			@Override
			public void linkActivated(HyperlinkEvent e) {
				String id = (String) e.getHref();
				IRodinElement element = RodinCore.valueOf(id);
				if (element != null && element.exists())
					UIUtils.linkToEventBEditor(element);
			}
		});

		formText = new EventBFormText(widget);

		setFormText(new NullProgressMonitor());

	}

	/**
	 * This private helper method is use to set the content string of the form
	 * text. The content string is set according to the type of the rodin input
	 * file and the content of that file
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 */
	void setFormText(IProgressMonitor monitor) {
		formString = "<form>";
		IRodinFile rodinFile = ((IEventBEditor) this.getEditor())
				.getRodinInput();
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
		formString += "<li style=\"text\" value=\"\"></li>";
		formString += "<li style=\"text\" value=\"\"><b>END</b></li>";
		formString += "</form>";

		formText.getFormText().setText(formString, true, true);
		form.reflow(true);
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
		String componentName = EventBPlugin.getComponentName(rodinFile
				.getElementName());
		if (rodinFile instanceof IMachineFile)
			formString += "<li style=\"text\" value=\"\"><b>MACHINE</b> ";
		else if (rodinFile instanceof IContextFile)
			formString += "<li style=\"text\" value=\"\"><b>CONTEXT</b> ";
		formString += UIUtils.makeHyperlink(rodinFile.getHandleIdentifier(),
				componentName);
		formString += "</li>";
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
					formString += "<li style=\"text\" value=\"\"></li>";
					formString += "<li style=\"text\" value=\"\"><b>REFINES</b> ";
					formString += UIUtils.makeHyperlink(EventBPlugin
							.getMachineFileName(name), name);
					formString += "</li>";
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
					formString += "<li style=\"text\" value=\"\"></li>";
					formString += "<li style=\"text\" value=\"\"><b>REFINES</b> ";
					formString += UIUtils.makeHyperlink(EventBPlugin
							.getContextFileName(name), name);
					formString += "</li>";
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
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>SEES</b> ";
			for (int i = 0; i < length; i++) {
				try {
					if (i != 0)
						formString += ", ";
					formString += UIUtils
							.makeHyperlink(rodinFile.getHandleIdentifier(),
									((SeesContext) seeContexts[i])
											.getSeenContextName());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get seen context name of "
									+ seeContexts[i].getElementName());
				}
			}
			formString += "</li>";
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
		IRodinElement[] vars;
		try {
			vars = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variables for "
					+ rodinFile.getElementName());
			return;
		}
		if (vars.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>VARIABLES</b>";
			formString += "</li>";
			for (IRodinElement child : vars) {
				IVariable var = (IVariable) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(var
							.getHandleIdentifier(), var.getIdentifierString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for variable "
									+ var.getElementName());
				}

				try {
					String comment = var.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this variable
				}
				formString += "</li>";
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
		IRodinElement[] invs;
		try {
			invs = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get invariants for "
					+ rodinFile.getElementName());
			return;
		}
		if (invs.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>INVARIANTS</b>";
			formString += "</li>";
			for (IRodinElement child : invs) {
				IInvariant inv = (IInvariant) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(inv
							.getHandleIdentifier(), inv.getLabel(monitor));
					formString += ": "
							+ UIUtils.XMLWrapUp(inv.getPredicateString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for invariant "
									+ inv.getElementName());
				}

				try {
					String comment = inv.getComment(monitor);
					if (!comment.equals(""))
						formString += "      /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this invariant
				}
				formString += "</li>";
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
		IRodinElement[] sets;
		try {
			sets = rodinFile.getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils
					.debugAndLogError(e, "Cannot get carrier sets for "
							+ rodinFile.getElementName());
			return;
		}
		if (sets.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>SETS</b>";
			formString += "</li>";
			for (IRodinElement child : sets) {
				ICarrierSet set = (ICarrierSet) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(set
							.getHandleIdentifier(), set.getIdentifierString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for carrier set "
									+ set.getElementName());
					e.printStackTrace();
				}

				try {
					String comment = set.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this carrier set
				}
				formString += "</li>";
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
		IRodinElement[] csts;
		try {
			csts = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get constants for "
					+ rodinFile.getElementName());
			return;
		}
		if (csts.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>CONSTANTS</b>";
			formString += "</li>";
			for (IRodinElement child : csts) {
				IConstant cst = (IConstant) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(cst
							.getHandleIdentifier(), cst.getIdentifierString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for constant "
									+ cst.getElementName());
					e.printStackTrace();
				}

				try {
					String comment = cst.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this carrier set
				}
				formString += "</li>";
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
		IRodinElement[] axms;
		try {
			axms = rodinFile.getChildrenOfType(IAxiom.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get axioms for "
					+ rodinFile.getElementName());
			return;
		}
		if (axms.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>AXIOMS</b>";
			formString += "</li>";
			for (IRodinElement child : axms) {
				IAxiom axm = (IAxiom) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(axm
							.getHandleIdentifier(), axm.getLabel(monitor));
					formString += ": "
							+ UIUtils.XMLWrapUp(axm.getPredicateString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for axiom "
									+ axm.getElementName());
				}

				try {
					String comment = axm.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this theorem
				}
				formString += "</li>";
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
		IRodinElement[] thms;
		try {
			thms = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get theorems for "
					+ rodinFile.getElementName());
			return;
		}
		if (thms.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>THEOREMS</b>";
			formString += "</li>";
			for (IRodinElement child : thms) {
				ITheorem thm = (ITheorem) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(thm
							.getHandleIdentifier(), thm.getLabel(monitor));
					formString += ": "
							+ UIUtils.XMLWrapUp(thm.getPredicateString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for theorem "
									+ thm.getElementName());
				}

				try {
					String comment = thm.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this theorem
				}
				formString += "</li>";
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
		IRodinElement[] evts;
		try {
			evts = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get events for "
					+ rodinFile.getElementName());
			return;
		}

		if (evts.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>EVENTS</b>";
			formString += "</li>";
			for (IRodinElement element : evts) {
				IEvent evt = (IEvent) element;
				try {
					formString += "<li style=\"text\" value=\"\"></li>";
					formString += "<li style=\"text\" value=\"\" bindent = \"20\">"
							+ UIUtils.makeHyperlink(evt.getHandleIdentifier(),
									evt.getLabel(monitor)) + "</li>";
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the label for event "
									+ evt.getElementName());
					continue;
				}

				try {
					String comment = evt.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this event
				}

				IRodinElement[] lvars;
				IRodinElement[] guards;
				IRodinElement[] actions;
				IRodinElement[] refinesEvents;
				IRodinElement[] witnesses;
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
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent = \"40\">";
					formString = formString + "<b>REFINES</b></li>";
					for (IRodinElement child : refinesEvents) {
						IRefinesEvent refinesEvent = (IRefinesEvent) child;
						formString += "<li style=\"text\" value=\"\" bindent = \"60\">";
						try {
							formString += UIUtils.makeHyperlink(refinesEvent
									.getHandleIdentifier(), refinesEvent
									.getAbstractEventLabel());
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the abstract event label for refines event "
											+ refinesEvent.getElementName());
						}

						formString += "</li>";
					}
				}

				if (lvars.length != 0) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent = \"40\">";
					formString = formString + "<b>ANY</b></li>";
					for (IRodinElement child : lvars) {
						IVariable var = (IVariable) child;
						formString += "<li style=\"text\" value=\"\" bindent = \"60\">";
						try {
							formString += UIUtils.makeHyperlink(var
									.getHandleIdentifier(), var
									.getIdentifierString());
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get the identifier string for local variable "
											+ var.getElementName());
						}

						try {
							String comment = var.getComment(monitor);
							if (!comment.equals(""))
								formString += "   /* "
										+ UIUtils.XMLWrapUp(comment) + " */";
						} catch (RodinDBException e) {
							// There is no comment attached to this local
							// variable
						}
						formString += "</li>";
					}
					formString += "<li style=\"text\" value=\"\" bindent = \"40\"><b>WHERE</b></li>";
				} else {
					if (guards.length != 0) {
						formString += "<li style=\"text\" value=\"\" bindent = \"40\">";
						formString += "<b>WHEN</b></li>";
					} else {
						formString += "<li style=\"text\" value=\"\" bindent = \"40\">";
						formString += "<b>BEGIN</b></li>";
					}

				}

				for (IRodinElement child : guards) {
					IGuard guard = (IGuard) child;
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"60\">";
					try {
						formString = formString
								+ UIUtils.makeHyperlink(guard
										.getHandleIdentifier(), guard
										.getLabel(monitor)) + ": "
								+ UIUtils.XMLWrapUp(guard.getPredicateString());
					} catch (RodinDBException e) {
						EventBEditorUtils.debugAndLogError(e,
								"Cannot get details for guard "
										+ guard.getElementName());
					}
					try {
						String comment = guard.getComment(monitor);
						if (!comment.equals(""))
							formString += "   /* " + UIUtils.XMLWrapUp(comment)
									+ " */";
					} catch (RodinDBException e) {
						// There is no comment attached to this event
					}
					formString = formString + "</li>";
				}

				if (witnesses.length != 0) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent = \"40\">";
					formString = formString + "<b>WITNESSES</b></li>";
					for (IRodinElement child : witnesses) {
						IWitness witness = (IWitness) child;
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"60\">";
						try {
							formString = formString
									+ UIUtils.makeHyperlink(witness
											.getHandleIdentifier(), witness
											.getLabel(monitor))
									+ ": "
									+ UIUtils.XMLWrapUp(witness
											.getPredicateString());
						} catch (RodinDBException e) {
							EventBEditorUtils.debugAndLogError(e,
									"Cannot get details for guard "
											+ witness.getElementName());
						}
						try {
							String comment = witness.getComment(monitor);
							if (!comment.equals(""))
								formString += "   /* "
										+ UIUtils.XMLWrapUp(comment) + " */";
						} catch (RodinDBException e) {
							// There is no comment attached to this event
						}
						formString = formString + "</li>";
					}
				}

				if (guards.length != 0) {
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"40\">";
					formString = formString + "<b>THEN</b></li>";
				}

				for (IRodinElement child : actions) {
					IAction action = (IAction) child;
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"60\">";
					try {
						formString = formString
								+ UIUtils.makeHyperlink(action
										.getHandleIdentifier(), action
										.getLabel(monitor))
								+ ": "
								+ UIUtils.XMLWrapUp(action
										.getAssignmentString());
					} catch (RodinDBException e) {
						EventBEditorUtils.debugAndLogError(e,
								"Cannot get details for action "
										+ action.getElementName());
					}
					try {
						String comment = action.getComment(monitor);
						if (!comment.equals(""))
							formString += "   /* " + UIUtils.XMLWrapUp(comment)
									+ " */";
					} catch (RodinDBException e) {
						// There is no comment attached to this event
					}
					formString = formString + "</li>";
				}
				formString = formString
						+ "<li style=\"text\" value=\"\" bindent=\"40\">";
				formString = formString + "<b>END</b></li>";
			}
		}
	}

	/**
	 * This private helper method adds component's information about variants to
	 * the content string
	 * <p>
	 * 
	 * @param rodinFile
	 *            the rodin input file
	 * @param monitor
	 *            a progress monitor
	 */
	private void addVariant(IRodinFile rodinFile, IProgressMonitor monitor) {
		IRodinElement[] variants;
		try {
			variants = rodinFile.getChildrenOfType(IVariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variants for "
					+ rodinFile.getElementName());
			return;
		}
		if (variants.length != 0) {
			formString += "<li style=\"text\" value=\"\"></li>";
			formString += "<li style=\"text\" value=\"\"><b>VARIANT</b>";
			formString += "</li>";
			for (IRodinElement child : variants) {
				IVariant variant = (IVariant) child;
				formString += "<li style=\"text\" value=\"\" bindent = \"20\">";
				try {
					formString += UIUtils.makeHyperlink(variant
							.getHandleIdentifier(), variant
							.getExpressionString());
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the expression string for variant "
									+ variant.getElementName());
				}

				try {
					String comment = variant.getComment(monitor);
					if (!comment.equals(""))
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
				} catch (RodinDBException e) {
					// There is no comment attached to this variant
				}
				formString += "</li>";
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (formText != null)
			formText.dispose();
		((IEventBEditor) this.getEditor()).removeElementChangedListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		if (form == null)
			return;
		if (form.getContent().isDisposed())
			return;

		Display display = this.getEditorSite().getShell().getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				// Reset the content string of the form text
				setFormText(new NullProgressMonitor());
			}

		});
	}

}
