package org.eventb.internal.ui.eventbeditor.prettyprint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.basis.SeesContext;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class PrettyPrintPage extends FormPage implements
		IElementChangedListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Pretty Print"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Pretty Print";

	public static final String PAGE_TAB_TITLE = "Pretty Print";

	private ScrolledForm form;

	private IEventBFormText formText;

	public PrettyPrintPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE);
		((EventBEditor) editor).addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		form.setText(PAGE_TITLE);
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
				IRodinElement element = RodinCore.create(id);
				if (element.exists())
					UIUtils.linkToEventBEditor(element);
				// UIUtils.activateView(IPageLayout.ID_PROBLEM_VIEW);
				// UIUtils.activateView(ProjectExplorer.VIEW_ID);
			}

		});

		formText = new EventBFormText(widget);

		setFormText(new NullProgressMonitor());

	}

	String formString;

	private void setFormText(IProgressMonitor monitor) {
		formString = "<form>";
		IRodinFile rodinFile = ((EventBEditor) this.getEditor())
				.getRodinInput();
		if (rodinFile instanceof IMachineFile) {
			addMachineName(rodinFile);
			addMachineDependencies(rodinFile, monitor);
			addVariables(rodinFile, monitor);
			addInvariants(rodinFile, monitor);
			addTheorems(rodinFile, monitor);
			addEvents(rodinFile, monitor);
		} else if (rodinFile instanceof IContextFile) {

		}
		formString += "<li style=\"text\" value=\"\"></li>";
		formString += "<li style=\"text\" value=\"\"><b>END</b></li>";
		formString += "</form>";

		formText.getFormText().setText(formString, true, true);
		form.reflow(true);
	}

	private void addMachineName(IRodinFile rodinFile) {
		// Print the Machine/Context name
		String componentName = EventBPlugin.getComponentName(rodinFile
				.getElementName());
		formString += "<li style=\"text\" value=\"\"><b>MACHINE</b> ";
		formString += UIUtils.makeHyperlink(rodinFile.getHandleIdentifier(),
				componentName);
		formString += "</li>";
		return;
	}

	private void addMachineDependencies(IRodinFile rodinFile,
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
					formString += UIUtils.makeHyperlink(name, name);
					formString += "</li>";
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// SEES clause
			IRodinElement[] seeContexts;
			try {
				seeContexts = rodinFile
						.getChildrenOfType(ISeesContext.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
						formString += UIUtils.makeHyperlink(rodinFile
								.getHandleIdentifier(),
								((SeesContext) seeContexts[i])
										.getSeenContextName());
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				formString += "</li>";
			}
		} else if (rodinFile instanceof IContextFile) {

		}
	}

	private void addVariables(IRodinFile rodinFile, IProgressMonitor monitor) {
		IRodinElement[] vars;
		try {
			vars = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					String comment = var.getComment(monitor);
					formString += "   /* " + UIUtils.XMLWrapUp(comment) + " */";
				} catch (RodinDBException e) {
					// Do nothing
				}
				formString += "</li>";
			}
		}
	}

	private void addInvariants(IRodinFile rodinFile, IProgressMonitor monitor) {
		IRodinElement[] invs;
		try {
			invs = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					String comment = inv.getComment(monitor);
					formString += "      /* " + UIUtils.XMLWrapUp(comment)
							+ " */";
				} catch (RodinDBException e) {
					// Do nothing
				}
				formString += "</li>";
			}
		}
	}

	private void addTheorems(IRodinFile rodinFile, IProgressMonitor monitor) {
		IRodinElement[] thms;
		try {
			thms = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					String comment = thm.getComment(monitor);
					formString += "   /* " + UIUtils.XMLWrapUp(comment) + " */";
				} catch (RodinDBException e) {
					// Do nothing
				}
				formString += "</li>";
			}
		}
	}

	private void addEvents(IRodinFile rodinFile, IProgressMonitor monitor) {
		IRodinElement[] evts;
		try {
			evts = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					try {
						String comment = evt.getComment(monitor);
						formString += "   /* " + UIUtils.XMLWrapUp(comment)
								+ " */";
					} catch (RodinDBException e) {
						// Do nothing
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				try {
					IRodinElement[] lvars;
					lvars = evt.getChildrenOfType(IVariable.ELEMENT_TYPE);
					IRodinElement[] guards = evt
							.getChildrenOfType(IGuard.ELEMENT_TYPE);
					IRodinElement[] actions = evt
							.getChildrenOfType(IAction.ELEMENT_TYPE);

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
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {
								String comment = var.getComment(monitor);
								formString += "   /* "
										+ UIUtils.XMLWrapUp(comment) + " */";
							} catch (RodinDBException e) {
								// Do nothing
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
						formString = formString
								+ UIUtils.makeHyperlink(guard
										.getHandleIdentifier(), guard
										.getLabel(new NullProgressMonitor()))
								+ ": "
								+ UIUtils.XMLWrapUp(guard.getPredicateString());
						try {
							String comment = guard.getComment(monitor);
							formString += "   /* " + UIUtils.XMLWrapUp(comment)
									+ " */";
						} catch (RodinDBException e) {
							// Do nothing
						}
						formString = formString + "</li>";
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
						formString = formString
								+ UIUtils.makeHyperlink(action
										.getHandleIdentifier(), action
										.getLabel(new NullProgressMonitor()))
								+ ": "
								+ UIUtils.XMLWrapUp(action
										.getAssignmentString());
						try {
							String comment = action.getComment(monitor);
							formString += "   /* " + UIUtils.XMLWrapUp(comment)
									+ " */";
						} catch (RodinDBException e) {
							// Do nothing
						}
						formString = formString + "</li>";
					}
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"40\">";
					formString = formString + "<b>END</b></li>";
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (formText != null)
			formText.dispose();
		((EventBEditor) this.getEditor()).removeElementChangedListener(this);
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
				setFormText(new NullProgressMonitor());
			}

		});
	}

}
