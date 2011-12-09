/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Southampton - Initial API and implementation
 *     Systerel - copied from eventb persistence plug-in
 *******************************************************************************/
package org.rodinp.core.emf.lightcore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.api.itf.ILFileFactory;

public class RodinResourceFactory implements Resource.Factory, ILFileFactory {
	/**
	 * A map of {@link Resource}s which are handled, i.e., created by this
	 * factory.
	 */
	private static Map<IResource, Collection<RodinResource>> handledResources;
	private static Object handledResourcesMutex = new Object();

	public Resource createResource(final URI uri) {
		checkInit();

		final RodinResource resource = new RodinResource();
		resource.setURI(uri);

		synchronized (handledResourcesMutex) {
			addNewResource(resource);
		}

		return resource;
	}

	public void removeResource(final RodinResource resource) {
		IResource key = resource.getUnderlyingResource();
		Collection<RodinResource> collection = handledResources.get(key);

		if (collection != null) {
			collection.remove(resource);

			if (collection.isEmpty()) {
				handledResources.remove(collection);
			}
		}
	}

	private void addNewResource(final RodinResource resource) {
		IResource key = resource.getUnderlyingResource();
		Collection<RodinResource> collection = handledResources.get(key);

		if (collection == null) {
			collection = new HashSet<RodinResource>();
			handledResources.put(key, collection);
		}

		collection.add(resource);
	}

	private void checkInit() {
		synchronized (handledResourcesMutex) {
			if (handledResources == null) {
				handledResources = new HashMap<IResource, Collection<RodinResource>>();
			}
		}
	}

	/**
	 * Returns the {@link Collection} of all {@link RodinResource} which are
	 * handled for the given {@link IResource} if it has already been loaded
	 * with this factory or <code>null</code> if not. <b>Attention</b>: The
	 * {@link Collection} is unmodifiable!
	 * 
	 * @param resource
	 * @return
	 */
	public static Collection<RodinResource> getHandledResources(IResource resource) {
		synchronized (handledResourcesMutex) {
			if (handledResources != null && handledResources.containsKey(resource)) {
				return Collections.unmodifiableCollection(handledResources.get(resource));
			}
		}

		return null;
	}

	@Override
	public ILFile createILFile(IFile file) {
		final String projectName = file.getProject().getName();
		final URI resourceURI = URI.createPlatformResourceURI(projectName + "/"
				+ file.getName(), true);
		return (ILFile) createResource(resourceURI);
	}

	@Override
	public void removeILFile(ILFile file) {
		removeResource((RodinResource) file);
	}
	
}
