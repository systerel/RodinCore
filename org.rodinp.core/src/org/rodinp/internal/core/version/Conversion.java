/*******************************************************************************
 * Copyright (c) 2008, 2009 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *     Soton    - initial API and implementation
 *     Systerel - added attribute modification
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Conversion extends SimpleOperation {

	private static final String T1 = "\t\t<" + XSLConstants.XSL_APPLY_TEMPLATES + " " + XSLConstants.XSL_SELECT +
			"=\"" + XSLConstants.XSL_ALL + "\"";
	private static final String T2 = ">\n";
	private static final String T3 = "\t\t</" + XSLConstants.XSL_APPLY_TEMPLATES + ">\n";
	private static final String T4 = "/>\n";

	public Conversion(IConfigurationElement configElement,
			SimpleConversionSheet sheet) {
		super(configElement, sheet);
	}
	
	public abstract String getPath();
	
	public abstract boolean isRoot();
	
	protected abstract RenameAttribute[] getRenameAttributes();

	protected abstract AddAttribute[] getAddAttributes();
	
	protected abstract RenameElement getRenameElement();

	public void addTemplates(StringBuffer document) {
		for (RenameAttribute ra : getRenameAttributes())
			ra.addTemplate(document, getPath());
		
		if (getRenameElement() != null) {
			
			getRenameElement().beginTemplate(document, getPath());
			
			for (AddAttribute aa : getAddAttributes()) {
				aa.addAttribute(document);
			}
			
			document.append(T1);
			
			if (getSheet().hasSorter()) {
				document.append(T2);
				
				getSheet().addSorter(document);
				
				document.append(T3);
			} else {
				document.append(T4);
			}
			
			getRenameElement().endTemplate(document);
		}
	}

}
