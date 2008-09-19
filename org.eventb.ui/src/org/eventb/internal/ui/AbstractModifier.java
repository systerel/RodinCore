package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IInternalElement;

public abstract class AbstractModifier implements IElementModifier {

	protected void doModify(IAttributeFactory factory,
			IInternalElement element, String value) {
		UIUtils.setStringAttribute(element, factory, value,
				new NullProgressMonitor());
	}
}
