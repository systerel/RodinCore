package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IGuard;
import org.eventb.ui.EventBUIPlugin;

public class PrefixGrdName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "grd";
	
	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
						EventBUIPlugin.PLUGIN_ID, IGuard.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Guard Name Prefix",
				"Please specify the prefix for guard names");
	}

}
