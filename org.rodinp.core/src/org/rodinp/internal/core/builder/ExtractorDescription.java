/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.internal.core.builder;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IExtractor;

/**
 * Description of an extractor registered with the tool manager.
 * <p>
 * Implements lazy class loading for the extractor.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ExtractorDescription extends ExecutableExtensionDesc {

	// Description of associated tool
	private final ToolDescription toolDescription;
	
	// List of file types that this extractor can parse.
	private final IFileElementType[] inputTypes;

	/**
	 * Creates a new extractor decription.
	 * 
	 * @param configElement
	 *            description of this extractor in the Eclipse registry
	 * @param toolDescription
	 *            description of the tool associated to this extractor
	 */
	public ExtractorDescription(IConfigurationElement configElement,
			ToolDescription toolDescription) {
		
		super(configElement);
		this.toolDescription = toolDescription;
		
		IConfigurationElement[] children = configElement.getChildren("inputType");
		this.inputTypes = new IFileElementType[children.length];
		for (int i = 0; i < children.length; i++) {
			final String id = children[i].getAttribute("id");
			// TODO check for existence of the file element type
			this.inputTypes[i] = RodinCore.getFileElementType(id);
		}
	}

	/**
	 * Returns an instance of this extractor.
	 * 
	 * @return Returns an instance of this extractor.
	 */
	public IExtractor getExtractor() {
		return (IExtractor) super.getExecutableExtension();
	}

	/**
	 * Returns the description of the tool associated to this extractor.
	 * 
	 * @return the tool associated to this extractor.
	 */
	public ToolDescription getAssociatedTool() {
		return toolDescription;
	}

	/**
	 * Returns the file types this extractor can parse.
	 * 
	 * @return Returns the input types known to this extractor.
	 */
	public IFileElementType[] getInputTypes() {
		return inputTypes;
	}

}
