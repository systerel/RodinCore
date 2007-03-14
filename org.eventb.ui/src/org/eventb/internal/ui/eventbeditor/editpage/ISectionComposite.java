package org.eventb.internal.ui.eventbeditor.editpage;

import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;

public interface ISectionComposite {

	void dispose();

	void refresh(IRodinElement element);

	IElementType getElementType();

	void elementRemoved(IRodinElement element);

	void elementAdded(IRodinElement element);

	void childrenChanged(IRodinElement element,
			IElementType type);

	void select(IRodinElement element, boolean select);

}
