package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormText;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.IVariable;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixActName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixEvtName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixGrdName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixRefinesEventName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVarName;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class EventSectionComposite extends DefaultSectionComposite {

	private final static String EMPTY_LINE = "<li style=\"text\" value=\"\"></li>";

	@Override
	public void createContents() throws RodinDBException {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		fComp.setLayout(gridLayout);
		map = new LinkedHashMap<IInternalElement, EditRow>();

		FormText widget = fToolkit.createFormText(fComp, true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>EVENTS</b></li></form>";
		widget.setText(text, true, true);

		IEvent[] events;
		events = ((IMachineFile) fInput).getEvents();

		for (final IEvent event : events) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Create a row for " + event.getLabel());
			}
			EditRow row = new EditRow(fPage, fInput, fToolkit, fComp, event,
					IEvent.ELEMENT_TYPE, fForm, 0) {

				@Override
				public void Add() {
					EventSectionComposite.this.AddEvent(parent, element);
				}

				@Override
				public void Remove() {
					try {
						element.delete(true, new NullProgressMonitor());
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
			map.put(event, row);

			widget = fToolkit.createFormText(fComp, true);

			gd = new GridData(GridData.FILL_HORIZONTAL);
			widget.setLayoutData(gd);
			new EventBFormText(widget);
			text = "<form><li style=\"text\" bindent = \"40\"><b>REFINES</b></li></form>";
			widget.setText(text, true, true);

			IRefinesEvent[] refinesEvents = event.getRefinesClauses();
			for (final IRefinesEvent refinesEvent : refinesEvents) {
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils.debug("Create a row for "
							+ refinesEvent.getAbstractEventLabel());
				}
				row = new EditRow(fPage, event, fToolkit, fComp, refinesEvent,
						IRefinesEvent.ELEMENT_TYPE, fForm, 1) {

					@Override
					public void Add() {
						EventSectionComposite.this.AddRefinesEvent(event,
								element);
					}

					@Override
					public void Remove() {
						try {
							element.delete(true, new NullProgressMonitor());
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				};
				map.put(refinesEvent, row);
			}

			row = new EditRow(fPage, event, fToolkit, fComp, null,
					IRefinesEvent.ELEMENT_TYPE, fForm, 1) {

				@Override
				public void Add() {
					EventSectionComposite.this.AddRefinesEvent(event, element);
				}

				@Override
				public void Remove() {
					// Do nothing
				}

			};

			widget = fToolkit.createFormText(fComp, true);

			gd = new GridData(GridData.FILL_HORIZONTAL);
			widget.setLayoutData(gd);
			new EventBFormText(widget);
			text = "<form><li style=\"text\" bindent = \"40\"><b>ANY</b></li></form>";
			widget.setText(text, true, true);

			IVariable[] variables = event.getVariables();
			for (final IVariable variable : variables) {
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils.debug("Create a row for "
							+ variable.getIdentifierString());
				}
				row = new EditRow(fPage, event, fToolkit, fComp, variable,
						IVariable.ELEMENT_TYPE, fForm, 1) {

					@Override
					public void Add() {
						EventSectionComposite.this.AddVariable(event, element);
					}

					@Override
					public void Remove() {
						try {
							element.delete(true, new NullProgressMonitor());
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				};
				map.put(variable, row);

			}

			row = new EditRow(fPage, event, fToolkit, fComp, null,
					IVariable.ELEMENT_TYPE, fForm, 1) {

				@Override
				public void Add() {
					EventSectionComposite.this.AddVariable(event, null);
				}

				@Override
				public void Remove() {
					// Do nothing
				}

			};

			widget = fToolkit.createFormText(fComp, true);

			gd = new GridData(GridData.FILL_HORIZONTAL);
			widget.setLayoutData(gd);
			new EventBFormText(widget);
			text = "<form><li style=\"text\" bindent = \"40\"><b>WHERE</b></li></form>";
			widget.setText(text, true, true);

			IGuard[] guards = event.getGuards();
			for (IGuard guard : guards) {
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils.debug("Create a row for "
							+ guard.getLabel());
				}
				row = new EditRow(fPage, event, fToolkit, fComp, guard,
						IGuard.ELEMENT_TYPE, fForm, 1) {

					@Override
					public void Add() {
						EventSectionComposite.this.AddGuard(event, element);
					}

					@Override
					public void Remove() {
						try {
							element.delete(true, new NullProgressMonitor());
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				};
				map.put(guard, row);

			}
			
			row = new EditRow(fPage, event, fToolkit, fComp, null,
					IGuard.ELEMENT_TYPE, fForm, 1) {

				@Override
				public void Add() {
					EventSectionComposite.this.AddGuard(event, null);
				}

				@Override
				public void Remove() {
					// Do nothing
				}

			};
			widget = fToolkit.createFormText(fComp, true);

			gd = new GridData(GridData.FILL_HORIZONTAL);
			widget.setLayoutData(gd);
			new EventBFormText(widget);
			text = "<form><li style=\"text\" bindent = \"40\"><b>THEN</b></li></form>";
			widget.setText(text, true, true);

			IAction[] actions = event.getActions();
			for (final IAction action : actions) {
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils.debug("Create a row for "
							+ action.getLabel());
				}
				row = new EditRow(fPage, event, fToolkit, fComp, action,
						IAction.ELEMENT_TYPE, fForm, 1) {

					@Override
					public void Add() {
						EventSectionComposite.this.AddAction(event, element);
					}

					@Override
					public void Remove() {
						try {
							element.delete(true, new NullProgressMonitor());
						} catch (RodinDBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				};
				map.put(action, row);

			}

			row = new EditRow(fPage, event, fToolkit, fComp, null,
					IAction.ELEMENT_TYPE, fForm, 1) {

				@Override
				public void Add() {
					EventSectionComposite.this.AddAction(event, null);
				}

				@Override
				public void Remove() {
					// Do nothing
				}

			};

			widget = fToolkit.createFormText(fComp, true);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			widget.setLayoutData(gd);
			new EventBFormText(widget);
			text = "<form><li style=\"text\" bindent = \"40\"><b>END</b></li>"
					+ EMPTY_LINE + "</form>";
			widget.setText(text, true, true);
		}

		new EditRow(fPage, fInput, fToolkit, fComp, null, IEvent.ELEMENT_TYPE,
				fForm, 0) {

			@Override
			public void Add() {
				EventSectionComposite.this.AddEvent(parent, null);
			}

			@Override
			public void Remove() {
				// Do nothing
			}

		};

		fToolkit.paintBordersFor(fComp);
		fToolkit.paintBordersFor(fForm.getBody());
		fForm.getBody().pack();
		fForm.reflow(true);
	}

	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		changedElements = new HashSet<IRodinElement>();
		refresh = false;
		processDelta(delta, IEvent.ELEMENT_TYPE, IVariable.ELEMENT_TYPE,
				IGuard.ELEMENT_TYPE, IAction.ELEMENT_TYPE,
				IRefinesEvent.ELEMENT_TYPE, IRefinesMachine.ELEMENT_TYPE);
		postRefresh();
	}

	void AddEvent(final IInternalParent parent, final IInternalElement element) {
		QualifiedName qualifiedName = PrefixEvtName.QUALIFIED_NAME;
		String defaultPrefix = PrefixEvtName.DEFAULT_PREFIX;
		try {
			final String newName = UIUtils.getFreeElementName(fEditor, parent,
					IEvent.ELEMENT_TYPE, qualifiedName, defaultPrefix);
			final String newLabel = UIUtils.getFreeElementLabel(fEditor,
					parent, IEvent.ELEMENT_TYPE, qualifiedName, defaultPrefix);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IEvent evt = parent.getInternalElement(IEvent.ELEMENT_TYPE,
							newName);

					evt.create(element, monitor);
					evt.setLabel(newLabel, monitor);
					evt.setInherited(false, monitor);
					evt.setConvergence(Convergence.ORDINARY, monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void AddVariable(final IInternalParent parent,
			final IInternalElement element) {
		QualifiedName qualifiedName = PrefixVarName.QUALIFIED_NAME;
		String defaultPrefix = PrefixVarName.DEFAULT_PREFIX;
		try {
			final String newName = UIUtils.getFreeElementName(fEditor, parent,
					IVariable.ELEMENT_TYPE, qualifiedName, defaultPrefix);
			final String newIdentifier = UIUtils.getFreeElementIdentifier(
					fEditor, parent, IVariable.ELEMENT_TYPE, qualifiedName,
					defaultPrefix);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IVariable var = parent.getInternalElement(
							IVariable.ELEMENT_TYPE, newName);

					var.create(element, monitor);
					var.setIdentifierString(newIdentifier, monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void AddRefinesEvent(final IInternalParent parent,
			final IInternalElement element) {
		QualifiedName qualifiedName = PrefixRefinesEventName.QUALIFIED_NAME;
		String defaultPrefix = PrefixRefinesEventName.DEFAULT_PREFIX;
		try {
			final String newName = UIUtils.getFreeElementName(fEditor, parent,
					IRefinesEvent.ELEMENT_TYPE, qualifiedName, defaultPrefix);
			// final String newIdentifier = UIUtils.getFreeElementIdentifier(
			// fEditor, parent, IVariable.ELEMENT_TYPE, qualifiedName,
			// defaultPrefix);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IRefinesEvent refinesEvent = parent.getInternalElement(
							IRefinesEvent.ELEMENT_TYPE, newName);

					refinesEvent.create(element, monitor);
					refinesEvent.setAbstractEventLabel(((IEvent) parent)
							.getLabel(), monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void AddGuard(final IInternalParent parent,
			final IInternalElement element) {
		QualifiedName qualifiedName = PrefixGrdName.QUALIFIED_NAME;
		String defaultPrefix = PrefixGrdName.DEFAULT_PREFIX;
		try {
			final String newName = UIUtils.getFreeElementName(fEditor, parent,
					IGuard.ELEMENT_TYPE, qualifiedName, defaultPrefix);
			final String newLabel = UIUtils.getFreeElementLabel(
					fEditor, parent, IGuard.ELEMENT_TYPE, qualifiedName,
					defaultPrefix);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IGuard grd = parent.getInternalElement(
							IGuard.ELEMENT_TYPE, newName);

					grd.create(element, monitor);
					grd.setLabel(newLabel, monitor);
					grd.setPredicateString(EventBUIPlugin.GRD_DEFAULT, monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void AddAction(final IInternalParent parent,
			final IInternalElement element) {
		QualifiedName qualifiedName = PrefixActName.QUALIFIED_NAME;
		String defaultPrefix = PrefixActName.DEFAULT_PREFIX;
		try {
			final String newName = UIUtils.getFreeElementName(fEditor, parent,
					IAction.ELEMENT_TYPE, qualifiedName, defaultPrefix);
			final String newLabel = UIUtils.getFreeElementLabel(
					fEditor, parent, IAction.ELEMENT_TYPE, qualifiedName,
					defaultPrefix);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IAction grd = parent.getInternalElement(
							IAction.ELEMENT_TYPE, newName);

					grd.create(element, monitor);
					grd.setLabel(newLabel, monitor);
					grd.setAssignmentString(EventBUIPlugin.SUB_DEFAULT, monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
