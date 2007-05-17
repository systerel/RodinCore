package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;

public class PrefixAxmName extends PrefixElementName {

	public static final String DEFAULT_PREFIX = EditSectionRegistry
			.getDefault().getDefaultPrefix(IAxiom.ELEMENT_TYPE,
					"org.eventb.ui.label");

	public void run(IAction action) {
		QualifiedName qualifiedName = UIUtils
				.getQualifiedName(IAxiom.ELEMENT_TYPE);
		setPrefix(qualifiedName, DEFAULT_PREFIX, "Axiom Name Prefix",
				"Please specify the prefix for axiom names");
	}

}
