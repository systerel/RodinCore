/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * The Model is used to present the structure of the machines and contexts and
 * the proof obligations. For a machine, context, invariant, theorem, axiom and
 * event it shows what proof obligations are tied to it. The Model also shows
 * the dependencies between machines and contexts. I.e. how the machines refine
 * each other, what contexts they see and how the contexts extend each other.
 * The Model is built by the ModelController. The modeling of the machines and
 * contexts is triggered by the context providers that provide the machines and
 * context. The modeling of the proof obligations is triggered by the content
 * provider for the POs (POContentProvider)
 */
public interface IModelElement {
	
	/**
	 * 
	 * @return The Parent of this element from the model perspective. E.g. the
	 *         <code>ModelProject</code> for <code>ModelMachines</code> and
	 *         <code>ModelContexts</code> or the <code>ModelMachine</code>
	 *         for <code>ModelInvariants</code>. For
	 *         <code>ModelProjects</code> the result is <code>null</code>.
	 */
	public IModelElement getModelParent();
	
	/**
	 * 
	 * @return The RodinElement that this ModelElement is based on (e.g. an
	 *         IMachineRoot for ModelMachine) or <code>null</code> if there is
	 *         none.
	 */	
	public IRodinElement getInternalElement();
	
	/**
	 * This method returns the element that is displayed in the navigator tree
	 * as the parent of the element that is represented by this model element.
	 * E.g. if this is an instance of ModelMachine, the returned object is
	 * either the <code>IRodinProject</code> that contains this machine or the
	 * <code>IMachineRoot</code> that is the abstract machine.
	 * 
	 * @param complex
	 *            Determines whether the navigator tree show the complex
	 *            structure of machines or contexts.
	 * @return The object that is the parent of this element in the navigator
	 *         tree.
	 */
	public Object getParent(boolean complex);
	
	/**
	 * This method returns the elements that are displayed in the navigator tree
	 * as the children of the element that is represented by this model element.
	 * E.g. if this is an instance of ModelMachine, the returned objects contain
	 * the <code>IElementNode</code> of the given type or the
	 * <code>IMachineRoot</code>s that refine this machine.
	 * 
	 * @param type
	 *            The type the children should be associated with
	 * @param complex
	 *            Determines whether the navigator tree show the complex
	 *            structure of machines or contexts
	 * @return The objects that are children of this element in the navigator
	 *         tree.
	 */
	public Object[] getChildren(IInternalElementType<?> type, boolean complex);
	
}
