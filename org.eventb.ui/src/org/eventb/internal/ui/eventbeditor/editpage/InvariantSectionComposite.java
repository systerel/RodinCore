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
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixInvName;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class InvariantSectionComposite extends DefaultSectionComposite {

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
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>INVARIANTS</b></li></form>";
		widget.setText(text, true, true);

		IInvariant[] invariants;
		invariants = ((IMachineFile) fInput).getInvariants();

		for (IInvariant invariant : invariants) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Create a row for "
						+ invariant.getLabel());
			}
			EditRow row = new EditRow(fPage, fInput, fToolkit, fComp,
					invariant, IInvariant.ELEMENT_TYPE, fForm, 0) {

				@Override
				public void Add() {
					InvariantSectionComposite.this.Add(parent, element);
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
			map.put(invariant, row);
		}

		new EditRow(fPage, fInput, fToolkit, fComp, null,
				IInvariant.ELEMENT_TYPE, fForm, 0) {

			@Override
			public void Add() {
				InvariantSectionComposite.this.Add(parent, null);
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
		processDelta(delta, IInvariant.ELEMENT_TYPE);
		postRefresh();
	}

	void Add(final IInternalParent parent, final IInternalElement element) {
		QualifiedName qualifiedName = PrefixInvName.QUALIFIED_NAME;
		String defaultPrefix = PrefixInvName.DEFAULT_PREFIX;
		try {
			final String newName = UIUtils.getFreeElementName(fEditor, parent,
					IInvariant.ELEMENT_TYPE, qualifiedName, defaultPrefix);
			final String newLabel = UIUtils.getFreeElementLabel(fEditor,
					parent, IInvariant.ELEMENT_TYPE, qualifiedName,
					defaultPrefix);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IInvariant inv = parent.getInternalElement(
							IInvariant.ELEMENT_TYPE, newName);

					inv.create(element, new NullProgressMonitor());
					inv.setLabel(newLabel, monitor);
					inv.setPredicateString(EventBUIPlugin.INV_DEFAULT, monitor);
				}

			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
