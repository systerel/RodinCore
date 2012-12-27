/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.spec;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * An element spec is used to define grammatical rules for database elements.
 * Based on an element spec the following operations are possible on associated
 * database elements:
 * <li>
 * <ul><code>verify()</code>: check whether an element satisfies its element spec</ul>
 * <ul><code>repair()</code>: repair an element so that is satifies its element spec</ul>
 * <ul><code>vacuum()</code>: remove attributes that do not appear in its element spec</ul>
 * </li>
 * All operations can be carried out recursively or for a single element. The last two
 * operations, <code>repair()</code> and <code>vacuum()</code>, are destructive and should 
 * be used with caution.
 * <p>
 * Element specs define constraints on the structure of database elements. These are defined
 * by means of the <code>elementSpecs</code> extension point. It is permitted
 * that a database element does not have an associated element spec. As a consequence none
 * of the three operations will have an effect: <code>verify()</code> succeeds immediately, and 
 * <code>repair()</code> and <code>vacuum()</code> do not change the element (nor its children).
 * </p>
 * <p>
 * Element specs also define a syntactical skeleton for database elements that can
 * be used for displaying it. Whether a database element is actually displayed is not
 * specified in the element spec.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IElementSpec {

	/**
	 * Returns the element type to which this spec applies.
	 * 
	 * @return the element type to which this spec applies
	 */
	IElementType<? extends IRodinElement> getElementType();
	
	/**
	 * Verifies the element with respect to this spec.
	 * This method does not change the element passed as parameter.
	 * 
	 * @param element the element to verify
	 * @param recursive <code>true</code> iff all children are to be verified too
	 * 		(with repect to their spec)
	 * @return a list of problems found where the element does not
	 * 		satisfy this spec
	 * @throws RodinDBException if there was a problem accessing the database
	 * @throws IllegalArgumentException if the element type of the element does
	 * 		not correspond to this spec
	 */
	List<SpecProblem> verify(IRodinElement element, boolean recursive) 
	throws RodinDBException;
	
	/**
	 * Repairs the element so that it satisfies this spec. To repair means:
	 * <li>
	 * <ul>add missing (sub-) elements (that satisfy their spec)</ul>
	 * <ul>add missing attributes (with default values)</ul>
	 * <ul>remove attributes with inavlid values and replace them by valid ones (with default values)</ul>
	 * <ul><b>not</b> to remove unknown attributes (this achieved by <code>vacuum()</code>)</ul>
	 * </li>
	 * 
	 * @param element the element to repair
	 * @param recursive <code>true</code> iff all children are to be repaired too
	 * 		(with repect to their spec)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 * @throws IllegalArgumentException if the element type of the element does
	 * 		not correspond to this spec
	 * @throws IllegalStateException if this element spec does not describe a repairable element
	 */
	void repair(IRodinElement element, boolean recursive, IProgressMonitor monitor) 
	throws RodinDBException;
	
	/**
	 * Removes all elements and attributes that do not appear in this spec.
	 * Elements and attributes that are present but do not satisfy their spec are
	 * <b>not</b> removed.
	 * 
	 * @param element the element to vacuum
	 * @param recursive <code>true</code> iff all children are to be vacuumed too
	 * 		(with repect to their spec)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 * @throws IllegalArgumentException if the element type of the element does
	 * 		not correspond to this spec
	 * @throws IllegalStateException if this element spec does not describe a vacuumable element
	 */
	void vacuum(IRodinElement element, boolean recursive, IProgressMonitor monitor) 
	throws RodinDBException;
}
