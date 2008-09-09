package org.eventb.internal.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public abstract class AbstractModifier implements IElementModifier {

	protected void doModify(IAttributeFactory factory,
			IInternalElement element, String newText) {
		String oldText = null;
		try {
			oldText = factory.getValue(element, new NullProgressMonitor());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		// Try to set the new value if the current value
		// is <code>null</code> or is different from the text
		if (oldText == null || !oldText.equals(newText)) {
			History.getInstance().addOperation(
					OperationFactory.changeAttribute(element.getRodinFile(),
							factory, element, newText));
		}

	}
}
