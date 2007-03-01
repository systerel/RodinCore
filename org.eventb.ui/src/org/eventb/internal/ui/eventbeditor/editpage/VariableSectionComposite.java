package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormText;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVarName;
import org.eventb.ui.EventBFormText;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class VariableSectionComposite extends DefaultSectionComposite {

	@Override
	public void createContents() throws RodinDBException {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		fComp.setLayout(gridLayout);
		map = new LinkedHashMap<IInternalElement, EditRow>();
		fComp.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent e) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Variable Section Composite Key event " + e);
			}
			
		});
		
		FormText widget = fToolkit.createFormText(fComp, true);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>VARIABLES</b></li></form>";
		widget.setText(text, true, true);

		IVariable[] variables;
		variables = ((IMachineFile) fInput).getVariables();

		for (IVariable variable : variables) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Create a row for "
						+ variable.getIdentifierString());
			}
			EditRow row = new EditRow(fPage, fInput, fToolkit, fComp,
					variable, IVariable.ELEMENT_TYPE, fForm, 0) {

				@Override
				public void Add() {
					VariableSectionComposite.this.Add(parent, element);
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

		new EditRow(fPage, fInput, fToolkit, fComp, null,
				IVariable.ELEMENT_TYPE, fForm, 0) {

			@Override
			public void Add() {
				VariableSectionComposite.this.Add(parent, null);
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
		processDelta(delta, IVariable.ELEMENT_TYPE);
		postRefresh();
	}

	void Add(final IInternalParent parent, final IInternalElement element) {
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

					var.create(element, new NullProgressMonitor());
					var.setIdentifierString(newIdentifier, monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}