/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code extracted from class ElementTypeManager
 *******************************************************************************/
package org.rodinp.internal.core;

import static org.rodinp.internal.core.ElementTypeManager.debug;
import static org.rodinp.internal.core.ElementTypeManager.getSortedIds;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.rodinp.core.RodinCore;

/**
 * Stores a map between content type name and file associations, as defined by
 * the extension point <code>fileAssociations</code>.
 * <p>
 * Instances of this class are immutable and therefore thread-safe.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class FileAssociations {

	// Local id of the fileAssociations extension point of this plugin
	private static final String FILE_ASSOCIATIONS_ID = "fileAssociations";

	// Access to file association using their content type name
	private HashMap<String, FileAssociation> fileContentTypes;
	
	public FileAssociations() {
		computeFileAssociations();
	}

	private void computeFileAssociations() {
		fileContentTypes = new HashMap<String, FileAssociation>();
		// Read the extension point extensions.
		final IConfigurationElement[] elements = readExtensions();
		for (IConfigurationElement element : elements) {
			final FileAssociation type = new FileAssociation(element);
			fileContentTypes.put(type.getContentTypeId(), type);
		}

		if (ElementTypeManager.VERBOSE) {
			debug("-----------------------------------------------");
			debug("File association known to the Rodin database:");
			for (String id : getSortedIds(fileContentTypes)) {
				debug("    content-type: " + id);
				final FileAssociation assoc = fileContentTypes.get(id);
				debug("    root-element-type: " + assoc.getRootElementTypeId());
			}
			debug("-----------------------------------------------");
		}
	}

	private IConfigurationElement[] readExtensions() {
		return RegistryFactory.getRegistry().getConfigurationElementsFor(
				RodinCore.PLUGIN_ID, FILE_ASSOCIATIONS_ID);
	}

	public FileAssociation getFromContentId(String contentTypeId) {
		return fileContentTypes.get(contentTypeId);
	}

}
