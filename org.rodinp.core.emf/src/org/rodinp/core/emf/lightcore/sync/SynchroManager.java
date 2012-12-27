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
package org.rodinp.core.emf.lightcore.sync;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.emf.common.util.EList;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ICoreImplicitChildProvider;
import org.rodinp.core.emf.api.itf.ImplicitChildProviderManager;
import org.rodinp.core.emf.lightcore.ImplicitElement;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;

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
		final InternalElement parent = loadInternalElementFor(iParent, null);
		parent.eSetDeliver(false);
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
		parent.eSetDeliver(true);
		return parent;
	}

	private static void recursiveLoad(LightElement parent,
			IInternalElement iParent, IInternalElement child)
			throws RodinDBException {
		final LightElement eRoot = parent.getERoot();
		final InternalElement lChild = loadInternalElementFor(child, eRoot);
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
		try {
			final List<ICoreImplicitChildProvider> providers = ImplicitChildProviderManager
					.getProvidersFor(iParent.getElementType());
			parent.eSetDeliver(false);
			for (final ICoreImplicitChildProvider p : providers) {
				final List<? extends IInternalElement> implicitChildren = ImplicitChildrenComputer
						.safeGetImplicitChildren(iParent, p);
				int insertionPosition = 0;
				for (IInternalElement e : implicitChildren) {
					final LightElement eRoot = parent.getERoot();
					final ImplicitElement implicit = loadImplicitElementFor(e,
							eRoot);
					final LightElement original = SynchroUtils
							.findChildElement(e, parent);
					final EList<LightElement> eChildren = parent.getEChildren();
					if (original != null) {
						reloadImplicitElement(original, implicit);
						final int childPosition = parent
								.getChildPosition(original);
						if (childPosition >= 0
								&& insertionPosition < eChildren.size()
								&& childPosition != insertionPosition) {
							parent.moveChild(insertionPosition, childPosition);
						}
					} else {
						if (insertionPosition > eChildren.size())
							eChildren.add(implicit);
						else
							eChildren.add(insertionPosition, implicit);
					}
					insertionPosition++;
				}
			}
		} finally {
			parent.eSetDeliver(true);
		}
	}

	private static class ImplicitChildrenComputer {

		private List<? extends IInternalElement> implicitChildren;
		private final IInternalElement parent;
		private final ICoreImplicitChildProvider provider;

		private ImplicitChildrenComputer(IInternalElement parent,
				ICoreImplicitChildProvider provider) {
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
				IInternalElement parent, ICoreImplicitChildProvider provider) {
			final ImplicitChildrenComputer c = new ImplicitChildrenComputer(
					parent, provider);
			final List<? extends IInternalElement> children = c
					.getImplicitChildren();
			return (children == null) ? Collections
					.<IInternalElement> emptyList() : children;
		}

	}
	
	public static InternalElement loadInternalElementFor(
			IRodinElement iElement, LightElement root) {
		final InternalElement eElement = LightcoreFactory.eINSTANCE
				.createInternalElement();
		eElement.eSetDeliver(false);
		eElement.setReference(iElement.getElementName()+"["+iElement.getElementType().getName()+"]");
		eElement.setERodinElement(iElement);
		final boolean isRoot = iElement.isRoot();
		eElement.setEIsRoot(isRoot);
		if (root == null && isRoot) {
			eElement.setERoot(eElement);
		} else {
			eElement.setERoot(root);
		}
		eElement.load();
		eElement.eSetDeliver(true);
		return eElement;
	}

	private static ImplicitElement loadImplicitElementFor(
			IRodinElement iElement, LightElement root) {
		final ImplicitElement eImplicit = LightcoreFactory.eINSTANCE
				.createImplicitElement();
		eImplicit.eSetDeliver(false);
		eImplicit.setERodinElement(iElement);
		final boolean isRoot = iElement.isRoot();
		eImplicit.setEIsRoot(isRoot);
		if (root == null && isRoot) {
			eImplicit.setERoot(eImplicit);
		} else {
			eImplicit.setERoot(root);
		}
		eImplicit.load();
		eImplicit.eSetDeliver(true);
		return eImplicit;
	}
	
	private static void reloadImplicitElement(
			LightElement original, LightElement newElement) {
		original.eSetDeliver(false);
		original.setERodinElement(newElement.getERodinElement());
		original.setEIsRoot(newElement.isEIsRoot());
		original.setERoot(newElement.getERoot());
		SynchroUtils.reloadAttributes(newElement.getElement(), original);
	}

	public void saveModelFromRoot(IInternalElement content) {
		// FIXME NEED TO DO SOMETHING ?
	}

}
