/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.sync;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.lightcore.IImplicitChildProvider;
import org.rodinp.core.emf.lightcore.ImplicitElement;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.childproviders.ImplicitChildProviderManager;

/**
 * Class able to load and save EMF/Rodin models.
 * 
 * @author Thomas Muller
 */
public class SynchroManager {

	private static SynchroManager INSTANCE;

	private SynchroManager() {
		// private constructor : SINGLETON
	}

	public static SynchroManager getDefault() {
		if (INSTANCE == null)
			INSTANCE = new SynchroManager();
		return INSTANCE;
	}

	/**
	 * Gets the model for the given Rodin root element.
	 * 
	 * @param root
	 *            the rodin internal root element
	 * @return the corresponding EMF light model
	 */
	public InternalElement getModelForRoot(IInternalElement root) {
		if (!root.isRoot()) {
			return null;
		}
		return loadRodinModel(root);
	}

	private static InternalElement loadRodinModel(IInternalElement iParent) {
		final InternalElement parent = loadInternalElementFor(iParent);
		implicitLoad(parent, iParent);
		if (iParent.isRoot()) {
			SynchroUtils.adaptRootForDBChanges(parent);
		}
		try {
			for (IRodinElement ichild : iParent.getChildren()) {
				if (ichild instanceof IInternalElement) {
					final IInternalElement iInChild = (IInternalElement) ichild;
					recursiveLoad(parent, iParent, iInChild);
				}
			}
		} catch (RodinDBException e) {
			System.out.println("Could not create children of the UI"
					+ " model for the element " + iParent.toString() + " "
					+ e.getMessage());
		}
		return parent;
	}

	private static void recursiveLoad(LightElement parent,
			IInternalElement iParent, IInternalElement child)
			throws RodinDBException {
		final InternalElement lChild = loadInternalElementFor(child);
		parent.getEChildren().add(lChild);
		implicitLoad(lChild, child);
		for (IRodinElement ichild : child.getChildren()) {
			if (ichild instanceof IInternalElement)
				recursiveLoad(lChild, child, (IInternalElement) ichild);
		}
	}

	/**
	 * Computes the list of providers for a parent element type and iterates on
	 * them to get and add the implicit children to the parent light element.
	 * 
	 * @param parent
	 *            the light element that we want to add implicit children to
	 * @param iParent
	 *            the IInternalElement counterpart of the parent
	 */
	public static void implicitLoad(LightElement parent,
			final IInternalElement iParent) {
		final List<IImplicitChildProvider> providers = ImplicitChildProviderManager
				.getProvidersFor(iParent.getElementType());
		for (final IImplicitChildProvider p : providers) {
			for (IInternalElement e : ImplicitChildrenComputer
					.safeGetImplicitChildren(iParent, p)) {
				final ImplicitElement implicit = loadImplicitElementFor(e);
				parent.getEChildren().add(implicit);
			}
		}
	}

	private static class ImplicitChildrenComputer {

		private List<? extends IInternalElement> implicitChildren;
		private final IInternalElement parent;
		private final IImplicitChildProvider provider;

		private ImplicitChildrenComputer(IInternalElement parent,
				IImplicitChildProvider provider) {
			this.parent = parent;
			this.provider = provider;
			computeImplicitChildren();
		}

		private void computeImplicitChildren() {
			SafeRunner.run(new ISafeRunnable() {

				@Override
				public void run() throws Exception {
					implicitChildren = provider.getImplicitChildren(parent);
				}

				@Override
				public void handleException(Throwable exception) {
					System.out
							.println("An exception occured while the implicit child provider"
									+ provider.toString()
									+ " attempted to calculate implicit children of "
									+ parent.getElementName());
				}
			});
		}

		private List<? extends IInternalElement> getImplicitChildren() {
			return implicitChildren;
		}

		public static List<? extends IInternalElement> safeGetImplicitChildren(
				IInternalElement parent, IImplicitChildProvider provider) {
			final ImplicitChildrenComputer c = new ImplicitChildrenComputer(
					parent, provider);
			final List<? extends IInternalElement> children = c
					.getImplicitChildren();
			return (children == null) ? Collections
					.<IInternalElement> emptyList() : children;
		}

	}
	
	private static InternalElement loadInternalElementFor(IRodinElement iElement) {
		final InternalElement eElement = LightcoreFactory.eINSTANCE
				.createInternalElement();
		eElement.setERodinElement(iElement);
		eElement.load();
		return eElement;
	}

	private static ImplicitElement loadImplicitElementFor(IRodinElement iElement) {
		final ImplicitElement eImplicit = LightcoreFactory.eINSTANCE
				.createImplicitElement();
		eImplicit.setERodinElement(iElement);
		eImplicit.load();
		return eImplicit;
	}

	public void saveModelFromRoot(IInternalElement content) {
		// FIXME NEED TO DO SOMETHING ?
	}

}
