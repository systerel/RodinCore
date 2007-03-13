package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

public interface IElementComposite {

	List<IRodinElement> getSelectedElements();

	EditPage getPage();

	void folding();

	void refresh(IRodinElement element);

	void elementRemoved(IRodinElement element);

	void elementAdded(IRodinElement element);

	void dispose();

	IRodinElement getElement();

	boolean isExpanded();

	void childrenChanged(IRodinElement element, IElementType type);

	Composite getComposite();

	void select(IRodinElement element, boolean select);

}
