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
package org.rodinp.core.emf.api.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.api.ApiFactory;

import org.rodinp.core.emf.api.itf.ILElement;

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
 * @generated
 */
public class ApiPackageImpl extends EPackageImpl {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String eNAME = "api";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String eNS_URI = "http://emf.core.rodinp.org/models/lightcore/api";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String eNS_PREFIX = "org.rodinp.core.emf.lightcore.api";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final ApiPackageImpl eINSTANCE = org.rodinp.core.emf.api.impl.ApiPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.api.itf.ILElement <em>IL Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.api.itf.ILElement
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getILElement()
	 * @generated
	 */
	public static final int IL_ELEMENT = 0;

	/**
	 * The number of structural features of the '<em>IL Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	public static final int IL_ELEMENT_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getList()
	 * @generated
	 */
	public static final int LIST = 1;

	/**
	 * The meta object id for the '<em>IAttribute Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IAttributeType
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIAttributeType()
	 * @generated
	 */
	public static final int IATTRIBUTE_TYPE = 2;

	/**
	 * The meta object id for the '<em>IAttribute Value</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IAttributeValue
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIAttributeValue()
	 * @generated
	 */
	public static final int IATTRIBUTE_VALUE = 3;

	/**
	 * The meta object id for the '<em>IInternal Element</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.IInternalElement
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#getIInternalElement()
	 * @generated
	 */
	public static final int IINTERNAL_ELEMENT = 4;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ilElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType listEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeValueEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iInternalElementEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.rodinp.core.emf.api.impl.ApiPackageImpl#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ApiPackageImpl() {
		super(eNS_URI, ((EFactory) ApiFactory.INSTANCE));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link ApiPackageImpl#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ApiPackageImpl init() {
		if (isInited)
			return (ApiPackageImpl) EPackage.Registry.INSTANCE
					.getEPackage(ApiPackageImpl.eNS_URI);

		// Obtain or create and register package
		ApiPackageImpl theApiPackage = (ApiPackageImpl) (EPackage.Registry.INSTANCE
				.get(eNS_URI) instanceof ApiPackageImpl ? EPackage.Registry.INSTANCE
				.get(eNS_URI) : new ApiPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theApiPackage.createPackageContents();

		// Initialize created meta-data
		theApiPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theApiPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ApiPackageImpl.eNS_URI, theApiPackage);
		return theApiPackage;
	}

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.api.itf.ILElement <em>IL Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IL Element</em>'.
	 * @see org.rodinp.core.emf.api.itf.ILElement
	 * @generated
	 */
	public EClass getILElement() {
		return ilElementEClass;
	}

	/**
	 * Returns the meta object for data type '{@link java.util.List <em>List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>List</em>'.
	 * @see java.util.List
	 * @generated
	 */
	public EDataType getList() {
		return listEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IAttributeType <em>IAttribute Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IAttribute Type</em>'.
	 * @see org.rodinp.core.IAttributeType
	 * @generated
	 */
	public EDataType getIAttributeType() {
		return iAttributeTypeEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IAttributeValue <em>IAttribute Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IAttribute Value</em>'.
	 * @see org.rodinp.core.IAttributeValue
	 * @generated
	 */
	public EDataType getIAttributeValue() {
		return iAttributeValueEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.rodinp.core.IInternalElement <em>IInternal Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IInternal Element</em>'.
	 * @see org.rodinp.core.IInternalElement
	 * @generated
	 */
	public EDataType getIInternalElement() {
		return iInternalElementEDataType;
	}

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	public ApiFactory getApiFactory() {
		return (ApiFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		ilElementEClass = createEClass(IL_ELEMENT);

		// Create data types
		listEDataType = createEDataType(LIST);
		iAttributeTypeEDataType = createEDataType(IATTRIBUTE_TYPE);
		iAttributeValueEDataType = createEDataType(IATTRIBUTE_VALUE);
		iInternalElementEDataType = createEDataType(IINTERNAL_ELEMENT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters
		addETypeParameter(listEDataType, "T");

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(ilElementEClass, ILElement.class, "ILElement", IS_ABSTRACT,
				IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = addEOperation(ilElementEClass, null, "getChildren", 0,
				1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(this.getList());
		EGenericType g2 = createEGenericType(this.getILElement());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		op = addEOperation(ilElementEClass, null, "getAttributes", 0, 1,
				IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getList());
		g2 = createEGenericType(this.getIAttributeValue());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		op = addEOperation(ilElementEClass, this.getIAttributeValue(),
				"getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeType(), "type", 0, 1, IS_UNIQUE,
				IS_ORDERED);

		op = addEOperation(ilElementEClass, null, "setAttribute", 0, 1,
				IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeValue(), "value", 0, 1, IS_UNIQUE,
				IS_ORDERED);

		addEOperation(ilElementEClass, this.getIInternalElement(),
				"getElement", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(ilElementEClass, null, "delete", 0, 1, IS_UNIQUE,
				IS_ORDERED);

		op = addEOperation(ilElementEClass, null, "moveChild", 0, 1, IS_UNIQUE,
				IS_ORDERED);
		addEParameter(op, ecorePackage.getEInt(), "newPos", 0, 1, IS_UNIQUE,
				IS_ORDERED);
		addEParameter(op, ecorePackage.getEInt(), "oldPos", 0, 1, IS_UNIQUE,
				IS_ORDERED);

		// Initialize data types
		initEDataType(listEDataType, List.class, "List", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeTypeEDataType, IAttributeType.class,
				"IAttributeType", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeValueEDataType, IAttributeValue.class,
				"IAttributeValue", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iInternalElementEDataType, IInternalElement.class,
				"IInternalElement", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //ApiPackageImpl
