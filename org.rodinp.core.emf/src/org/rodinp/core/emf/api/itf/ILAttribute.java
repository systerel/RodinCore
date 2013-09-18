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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IL Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.rodinp.core.emf.api.ApiPackage#getILAttribute()
 * @model interface="true" abstract="true"
 * @generated
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILAttribute {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return (ILElement)getEOwner();'"
	 * @generated
	 */
	ILElement getOwner();
} // ILAttribute
