/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.internal.core.version;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Nicolas Beauger
 * 
 */
public class PatternConversion extends ExtensionDesc {

	private final String path;
	private final List<ModifyAttribute> modifAttrs = new ArrayList<ModifyAttribute>();

	public PatternConversion(IConfigurationElement configElement) {
		super(configElement);
		this.path = configElement.getAttribute("patternPath");

		for(IConfigurationElement confElem : configElement.getChildren()) {
			modifAttrs.add(new ModifyAttribute(confElem));
		}

	}

	public void addTemplates(XSLWriter writer) {
		for(ModifyAttribute modifAttr : modifAttrs) {
			modifAttr.addTemplate(writer, path);
		}
	}

}
