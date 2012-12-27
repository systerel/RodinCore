/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.lightcore.childproviders.IImplicitProvidingAssociation;
import org.rodinp.core.emf.lightcore.childproviders.ImplicitProvidingAssociation;

/**
 * The manager class for {@link ICoreImplicitChildProvider}. An instance of implicit
 * child provider is linked to a child type. It can be many child providers for
 * a given parent type.
 * 
 * @author Thomas Muller
 */
public class ImplicitChildProviderManager {

	private final static Map<IInternalElementType<? extends IInternalElement>, List<IImplicitProvidingAssociation>> relationships = new HashMap<IInternalElementType<? extends IInternalElement>, List<IImplicitProvidingAssociation>>();

	/**
	 * Adds an implicit providing association made from the given childType and
	 * the given provider to the parentType sorted map.
	 * 
	 * @param provider
	 *            the implicit child provider to register
	 * @param parentType
	 *            the parent type of the providing association
	 * @param childType
	 *            the type of the provided children
	 */
	public static void addProviderFor(ICoreImplicitChildProvider provider,
			IInternalElementType<? extends IInternalElement> parentType,
			IInternalElementType<? extends IInternalElement> childType) {
		addProviderFor(parentType, new ImplicitProvidingAssociation(provider,
				childType));
	}

	/**
	 * Adds the given childType/provider association to the list of providing
	 * associations for the given parent type.
	 * 
	 * @param parentType
	 *            the type of the parent the association is added for
	 * @param association
	 *            the child/provider association to add
	 */
	public static void addProviderFor(
			IInternalElementType<? extends IInternalElement> parentType,
			IImplicitProvidingAssociation association) {
		List<IImplicitProvidingAssociation> list = relationships
				.get(parentType);
		if (list == null) {
			relationships.put(parentType,
					new ArrayList<IImplicitProvidingAssociation>());
			list = relationships.get(parentType);
		}
		for (IImplicitProvidingAssociation asso : list) {
			final IInternalElementType<? extends IInternalElement> typeToAdd = association
					.getType();
			if (asso.getType().equals(typeToAdd)) {
				final IInternalElementType<? extends IInternalElement> childType = typeToAdd;
				System.out
						.println("There is already a registered provider for the association: "
								+ parentType.getId()
								+ " <--> "
								+ childType.getId());
				return;
			}
		}
		list.add(association);
	}

	/**
	 * Returns the UNIQUE implicit child provider for a couple
	 * parentType/childType which actually describes a unique relationship
	 * within parent child traversal.
	 * 
	 * @param parentType
	 *            the type of the parent element
	 * @param childType
	 *            the type of the child element
	 * @return the unique implicit child provider for the given parent/child
	 *         relationship
	 */
	public static ICoreImplicitChildProvider getProviderFor(
			IInternalElementType<? extends IInternalElement> parentType,
			IInternalElementType<? extends IInternalElement> childType) {
		final List<IImplicitProvidingAssociation> list = relationships
				.get(parentType);
		if (list == null)
			return null;
		if (list.isEmpty()) {
			return null;
		}
		for (IImplicitProvidingAssociation assoc : list) {
			if (childType == assoc.getType()) {
				return assoc.getProvider();
			}
		}
		return null;
	}

	public static List<ICoreImplicitChildProvider> getProvidersFor(
			IInternalElementType<? extends IInternalElement> parentType) {
		final List<IImplicitProvidingAssociation> assocs = relationships
				.get(parentType);
		if (assocs == null) {
			return Collections.emptyList();
		}
		final List<ICoreImplicitChildProvider> providers = new ArrayList<ICoreImplicitChildProvider>(
				assocs.size());
		for (IImplicitProvidingAssociation a : assocs) {
			providers.add(a.getProvider());
		}
		return providers;
	}

	public static void removeProvider(ICoreImplicitChildProvider provider) {
		for (List<IImplicitProvidingAssociation> a : relationships.values()) {
			for (IImplicitProvidingAssociation asso : a) {
				if (asso.getProvider().equals(provider)) {
					a.remove(asso);
					return;
				}
			}
		}
	}

}
