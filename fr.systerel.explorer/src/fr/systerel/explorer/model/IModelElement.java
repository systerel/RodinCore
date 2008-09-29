/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.model;

/**
 * The Model is used to present the structure of the machines and contexts 
 * and the proof obligations. For a machine, context, invariant, theorem, axiom and event
 * it shows what proof obligations are tied to it. 
 * The Model also shows the dependencies between machines and contexts. 
 * I.e. how the machines refine each other, what contexts they see and how the contexts
 * extend each other.
 * The Model is built by the ModelController.
 * The modelling of the machines and contexts is triggered by the context providers
 * that provide the machines and context (ComplexContextProvider, MachineContextContentProvider)
 * The modelling of the proof obligations is triggered by the content provider for the POs
 * (POContentProvider)
 */
public interface IModelElement {
	
	/**
	 * 
	 * @return The Parent of this element from the model perspective
	 */
	public IModelElement getParent();
}
