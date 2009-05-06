/*******************************************************************************
 * Copyright (c) 2008, 2009 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton    - initial API and implementation
 *     Systerel - added attribute modification
 *     Systerel - used XSLWriter
 *     Systerel - added ChangeName
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class Conversion extends SimpleOperation {

	public Conversion(IConfigurationElement configElement,
			SimpleConversionSheet sheet) {
		super(configElement, sheet);
	}
	
	public abstract String getPath();
	
	public abstract boolean isRoot();
	
	protected abstract RenameAttribute[] getRenameAttributes();

	protected abstract AddAttribute[] getAddAttributes();
	
	protected abstract RenameElement getRenameElement();
	
	protected abstract ChangeName getChangeName();

	public void addTemplates(XSLWriter writer) {
		for (RenameAttribute ra : getRenameAttributes())
			ra.addTemplate(writer, getPath());
		
		if (getChangeName() != null) {
			getChangeName().addTemplate(writer, getPath());
		}
		
		if (getRenameElement() != null) {
			
			getRenameElement().beginTemplate(writer, getPath());
			
			for (AddAttribute aa : getAddAttributes()) {
				aa.addAttribute(writer);
			}
	
			writer.appendApplyTemplates(getSheet());
					
			getRenameElement().endTemplate(writer);
		}
	}

}
