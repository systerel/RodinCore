package org.eventb.internal.ui;

import org.eventb.core.IExtendsContext;
import org.eventb.ui.IElementLabelProvider;
import org.rodinp.core.RodinDBException;

public class ExtendsContextLabelProvider implements IElementLabelProvider {

	public String getLabel(Object obj) {
		if (obj instanceof IExtendsContext) {
			try {
				return ((IExtendsContext) obj).getAbstractContextName();
			} catch (RodinDBException e) {
				if (UIUtils.DEBUG)
					e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
