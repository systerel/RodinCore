package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IVariant;
import org.eventb.ui.EventBUIPlugin;

public class PrefixVariantName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = "variant";

	public void run(IAction action) {
		QualifiedName qualifiedName = new QualifiedName(
				EventBUIPlugin.PLUGIN_ID, IVariant.ELEMENT_TYPE.getId());
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Variant Name Prefix",
				"Please specify the prefix for variant names");
	}

}
