package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public abstract class DefaultSectionComposite implements ISectionComposite {

	Map<IRodinElement, Collection<IEditComposite>> map;

	Composite fComp;

	FormToolkit fToolkit;

	ScrolledForm fForm;

	IRodinFile fInput;

	IEventBEditor fEditor;

	public ISectionComposite create(IEventBEditor editor, FormToolkit toolkit,
			ScrolledForm form, Composite parent, IRodinFile rInput) {
		this.fEditor = editor;
		this.fInput = rInput;
		this.fToolkit = toolkit;
		this.fForm = form;
		this.fInput = rInput;

		fComp = toolkit.createComposite(parent);
		fComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		try {
			createContents();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	abstract void createContents() throws RodinDBException;

	void refresh() {
		for (Control control : fComp.getChildren()) {
			control.dispose();
		}
		try {
			createContents();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fComp.getParent().pack();
		fForm.reflow(true);
	}

	void createButtons(IInternalParent parent, Composite comp, IInternalElement element,
			IInternalElementType<? extends IInternalElement> type) {
		CoolBar coolBar = new CoolBar(comp, SWT.FLAT);
		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		coolBar.setBackground(white);
		coolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);

		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		String[] actions = sectionRegistry.getActions(type);

		DropdownToolItem dropdownItem = new DropdownToolItem(fEditor, item,
				parent, element, type);
		for (String action : actions) {
			try {
				if (sectionRegistry.isApplicable(action, parent, element,
						type))
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
	}

	Set<IRodinElement> changedElements = null;

	boolean refresh = false;

	void processDelta(IRodinElementDelta delta, IElementType ... types) {
		IRodinElement element = delta.getElement();
		int kind = delta.getKind();
		if (element instanceof IRodinFile && kind == IRodinElementDelta.CHANGED) {
			for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
				processDelta(subDelta);
			}
			return;
		}
		
		IElementType elementType = element.getElementType();
		for (IElementType type : types) {
			if (type.equals(elementType)) {
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

	public void postRefresh() {
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

}
