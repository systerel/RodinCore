package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.ISectionComposite;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public abstract class DefaultSectionComposite implements ISectionComposite {

	Map<IInternalElement, EditRow> map;

	Composite fComp;

	FormToolkit fToolkit;

	ScrolledForm fForm;

	public List<IInternalElement> getSelectedElements() {
		List<IInternalElement> result = new ArrayList<IInternalElement>();
		for (IInternalElement element : map.keySet()) {
			EditRow row = map.get(element);
			if (row.isSelected())
				result.add(element);
		}
		return result;
	}

	IRodinFile fInput;

	EditPage fPage;

	IEventBEditor fEditor;

	Composite fParent;

	public ISectionComposite create(EditPage page, FormToolkit toolkit,
			ScrolledForm form, Composite parent) {
		this.fPage = page;
		fEditor = (IEventBEditor) page.getEditor();
		this.fInput = fEditor.getRodinInput();
		this.fToolkit = toolkit;
		this.fForm = form;
		this.fParent = parent;

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
		Control[] children = fParent.getChildren();
		Control next = null;
		int i;
		for (i = 0; i < children.length; ++i) {
			if (children[i] == fComp)
				break;
		}
		if (i < children.length - 1) {
			next = children[i + 1];
		}
		fComp.dispose();
		fComp = fToolkit.createComposite(fParent);
		fComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (next != null)
			fComp.moveAbove(next);
		try {
			createContents();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fForm.getBody().pack();
		fForm.reflow(true);
	}

	Set<IRodinElement> changedElements = null;

	boolean refresh = false;

	void processDelta(IRodinElementDelta delta, IElementType... types) {
		IRodinElement element = delta.getElement();
		int kind = delta.getKind();
		if (element instanceof IRodinFile && kind == IRodinElementDelta.CHANGED) {
			for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
				processDelta(subDelta, types);
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
					} else if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
						for (IRodinElementDelta subDelta : delta
								.getAffectedChildren()) {
							processDelta(subDelta, types);
						}
					} else if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
						changedElements.add(element);
					}
					return;
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
						EditRow editRow = map.get(element);
						if (editRow != null)
							editRow.refresh();
					}
				}
			}

		});
	}

}
