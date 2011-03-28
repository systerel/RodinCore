/**
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *  
 * Contributors:
 *     Systerel - Initial API and implementation
 */
package org.rodinp.core.emf.api.itf;

import java.util.List;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IL Element</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.rodinp.core.emf.api.ApiPackage#getILElement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ILElement {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.List<? extends org.rodinp.core.emf.api.itf.ILElement>" many="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getEChildren();'"
	 * @generated
	 */
	List<? extends ILElement> getChildren();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.List<org.rodinp.core.emf.api.itf.IAttributeValue>" many="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final EMap<String, Attribute> attributes = getEAttributes();\nfinal List<IAttributeValue> values = new <%java.util.ArrayList%><IAttributeValue>(\n\t\tattributes.size());\nfor (Attribute att : attributes.values()) {\n\tvalues.add((IAttributeValue) att.getValue());\n}\nreturn values;'"
	 * @generated
	 */
	List<IAttributeValue> getAttributes();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="org.rodinp.core.emf.api.itf.IAttributeValue" typeDataType="org.rodinp.core.emf.api.itf.IAttributeType"
	 * @generated
	 */
	IAttributeValue getAttribute(IAttributeType type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model valueDataType="org.rodinp.core.emf.api.itf.IAttributeValue"
	 * @generated
	 */
	void setAttribute(IAttributeValue value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.IInternalElement"
	 * @generated
	 */
	IInternalElement getElement();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='int i = 2;\ni++;'"
	 * @generated
	 */
	void delete();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void moveChild(int newPos, int oldPos);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isImplicit();

} // ILElement
