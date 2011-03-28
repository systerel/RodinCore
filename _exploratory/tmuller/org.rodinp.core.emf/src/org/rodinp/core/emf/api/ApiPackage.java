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
package org.rodinp.core.emf.api;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.rodinp.core.emf.api.ApiFactory
 * @model kind="package"
 * @generated
 */
public interface ApiPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "api";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://emf.core.rodinp.org/models/lightcore/api";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.rodinp.core.emf.lightcore.api";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ApiPackage eINSTANCE = org.rodinp.core.emf.api.impl.ApiPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.api.itf.ILElement <em>IL Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.api.itf.ILElement
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getILElement()
	 * @generated
	 */
	int IL_ELEMENT = 0;

	/**
	 * The number of structural features of the '<em>IL Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IL_ELEMENT_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getList()
	 * @generated
	 */
	int LIST = 1;

	/**
	 * The meta object id for the '<em>IAttribute Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IAttributeType
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIAttributeType()
	 * @generated
	 */
	int IATTRIBUTE_TYPE = 2;

	/**
	 * The meta object id for the '<em>IAttribute Value</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IAttributeValue
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIAttributeValue()
	 * @generated
	 */
	int IATTRIBUTE_VALUE = 3;

	/**
	 * The meta object id for the '<em>IInternal Element</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IInternalElement
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIInternalElement()
	 * @generated
	 */
	int IINTERNAL_ELEMENT = 4;

	/**
	 * The meta object id for the '<em>IInternal Element Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IInternalElementType
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIInternalElementType()
	 * @generated
	 */
	int IINTERNAL_ELEMENT_TYPE = 5;

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.api.itf.ILElement <em>IL Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IL Element</em>'.
	 * @see org.rodinp.core.emf.api.itf.ILElement
	 * @generated
	 */
	EClass getILElement();

	/**
	 * Returns the meta object for data type '{@link java.util.List <em>List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>List</em>'.
	 * @see java.util.List
	 * @model instanceClass="java.util.List" serializeable="false" typeParameters="T"
	 * @generated
	 */
	EDataType getList();

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IAttributeType <em>IAttribute Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IAttribute Type</em>'.
	 * @see org.rodinp.core.IAttributeType
	 * @model instanceClass="org.rodinp.core.IAttributeType" serializeable="false"
	 * @generated
	 */
	EDataType getIAttributeType();

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IAttributeValue <em>IAttribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IAttribute Value</em>'.
	 * @see org.rodinp.core.IAttributeValue
	 * @model instanceClass="org.rodinp.core.IAttributeValue" serializeable="false"
	 * @generated
	 */
	EDataType getIAttributeValue();

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IInternalElement <em>IInternal Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IInternal Element</em>'.
	 * @see org.rodinp.core.IInternalElement
	 * @model instanceClass="org.rodinp.core.IInternalElement" serializeable="false"
	 * @generated
	 */
	EDataType getIInternalElement();

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IInternalElementType <em>IInternal Element Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IInternal Element Type</em>'.
	 * @see org.rodinp.core.IInternalElementType
	 * @model instanceClass="org.rodinp.core.IInternalElementType" serializeable="false" typeParameters="T"
	 * @generated
	 */
	EDataType getIInternalElementType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ApiFactory getApiFactory();

} //ApiPackage
