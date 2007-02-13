package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class VariableSectionComposite extends DefaultSectionComposite {

	@Override
	public void createContents() throws RodinDBException {
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = sectionRegistry.getNumColumns(IVariable.ELEMENT_TYPE) + 1;

		map = new HashMap<IRodinElement, Collection<IEditComposite>>();

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		fComp.setLayout(gridLayout);
		Label label = fToolkit.createLabel(fComp, "VARIABLES");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		label.setLayoutData(gd);

		Composite tmpComp = fToolkit.createComposite(fComp);
		gd = new GridData();
		gd.heightHint = 0;
		gd.widthHint = 0;
		tmpComp.setLayoutData(gd);
		String[] names = sectionRegistry.getColumnNames(IVariable.ELEMENT_TYPE);
		for (String name : names) {
			label = fToolkit.createLabel(fComp, name);
			gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			label.setLayoutData(gd);
		}

		IVariable[] variables;
		variables = ((IMachineFile) fInput).getVariables();

		for (IVariable variable : variables) {
			createButtons(fEditor, fToolkit, fComp, fInput, variable);

			map = sectionRegistry.createColumns(fForm, fToolkit, fComp,
					variable, map);
		}
		fToolkit.paintBordersFor(fComp);
		fComp.getParent().pack();
		fForm.reflow(true);
	}

	private void createButtons(IEventBEditor editor, FormToolkit toolkit,
			Composite comp, IInternalParent parent, IInternalElement element) {
		CoolBar coolBar = new CoolBar(comp, SWT.FLAT);
		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		coolBar.setBackground(white);
		coolBar.setLayoutData(new GridData());
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);

		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		String[] actions = sectionRegistry.getActions(IVariable.ELEMENT_TYPE);

		DropdownToolItem dropdownItem = new DropdownToolItem(editor, item,
				parent, element, IVariable.ELEMENT_TYPE);
		for (String action : actions) {
			try {
				if (sectionRegistry.isApplicable(action, parent, element,
						IVariable.ELEMENT_TYPE))
					dropdownItem.addTactic(action);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CoolItem coolItem = new CoolItem(coolBar, SWT.NONE);
		coolItem.setControl(toolBar);
		Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point coolSize = coolItem.computeSize(size.x, size.y);
		coolItem.setMinimumSize(30, coolSize.y);
		coolItem.setPreferredSize(coolSize);
		coolItem.setSize(coolSize);

		// Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		// combo.add("+");
		// combo.add("-");
		// combo.add("^");
		// combo.add("d");
		// Button button = toolkit.createButton(parent, "+", SWT.PUSH);
		// GridData gd = new GridData();
		// button.setLayoutData(gd);
		// combo.setLayoutData(gd);
		// combo.addSelectionListener(new SelectionListener())
	}

	Set<IRodinElement> changedElements = null;

	boolean refresh = false;

	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		changedElements = new HashSet<IRodinElement>();
		refresh = false;
		processDelta(delta);
		// notifyElementMovedListener(moved);
		postRefresh();
	}

	private void postRefresh() {
		if (fForm.isDisposed())
			return;

		Display display = fForm.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				if (refresh)
					refresh();
				else {
					for (IRodinElement element : changedElements) {
						if (EventBEditorUtils.DEBUG)
							EventBEditorUtils.debug("Refresh "
									+ element.getElementName());
						Collection<IEditComposite> editComposites = map
								.get(element);
						assert (editComposites != null);
						for (IEditComposite editComposite : editComposites) {
							editComposite.refresh();
						}
					}
				}
			}

		});
	}

	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();
		int kind = delta.getKind();
		if (element instanceof IRodinFile && kind == IRodinElementDelta.CHANGED) {
			for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
				processDelta(subDelta);
			}
			return;
		}

		if (element instanceof IVariable) {
			if (kind == IRodinElementDelta.ADDED
					|| kind == IRodinElementDelta.REMOVED) {
				refresh = true;
				return;
			} else { // kind == CHANGED
				int flags = delta.getFlags();
				if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
					if (EventBEditorUtils.DEBUG)
						EventBEditorUtils.debug("REORDERED");
					refresh = true;
					return;
				} else {
					changedElements.add(element);
					return;
				}
			}
		}

	}

}