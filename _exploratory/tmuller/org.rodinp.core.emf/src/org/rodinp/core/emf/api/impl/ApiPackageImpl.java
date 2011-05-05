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
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeType.Handle;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.ApiFactory;
import org.rodinp.core.emf.api.ApiPackage;
import org.rodinp.core.emf.api.itf.ILAttribute;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl;

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
 * @generated
 */
public class ApiPackageImpl extends EPackageImpl implements ApiPackage {
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
	private EClass ilAttributeEClass = null;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iInternalElementTypeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeType_BooleanEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeType_HandleEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeType_IntegerEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeType_LongEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAttributeType_StringEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iRodinElementEDataType = null;

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
	 * @see org.rodinp.core.emf.api.ApiPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ApiPackageImpl() {
		super(eNS_URI, ApiFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ApiPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ApiPackage init() {
		if (isInited)
			return (ApiPackage) EPackage.Registry.INSTANCE
					.getEPackage(ApiPackage.eNS_URI);

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
		EPackage.Registry.INSTANCE.put(ApiPackage.eNS_URI, theApiPackage);
		return theApiPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getILElement() {
		return ilElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getILAttribute() {
		return ilAttributeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getList() {
		return listEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeType() {
		return iAttributeTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeValue() {
		return iAttributeValueEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIInternalElement() {
		return iInternalElementEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIInternalElementType() {
		return iInternalElementTypeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeType_Boolean() {
		return iAttributeType_BooleanEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeType_Handle() {
		return iAttributeType_HandleEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeType_Integer() {
		return iAttributeType_IntegerEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeType_Long() {
		return iAttributeType_LongEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAttributeType_String() {
		return iAttributeType_StringEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIRodinElement() {
		return iRodinElementEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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

		ilAttributeEClass = createEClass(IL_ATTRIBUTE);

		// Create data types
		listEDataType = createEDataType(LIST);
		iAttributeTypeEDataType = createEDataType(IATTRIBUTE_TYPE);
		iAttributeValueEDataType = createEDataType(IATTRIBUTE_VALUE);
		iInternalElementEDataType = createEDataType(IINTERNAL_ELEMENT);
		iInternalElementTypeEDataType = createEDataType(IINTERNAL_ELEMENT_TYPE);
		iAttributeType_BooleanEDataType = createEDataType(IATTRIBUTE_TYPE_BOOLEAN);
		iAttributeType_HandleEDataType = createEDataType(IATTRIBUTE_TYPE_HANDLE);
		iAttributeType_IntegerEDataType = createEDataType(IATTRIBUTE_TYPE_INTEGER);
		iAttributeType_LongEDataType = createEDataType(IATTRIBUTE_TYPE_LONG);
		iAttributeType_StringEDataType = createEDataType(IATTRIBUTE_TYPE_STRING);
		iRodinElementEDataType = createEDataType(IRODIN_ELEMENT);
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
		addETypeParameter(iInternalElementTypeEDataType, "T");

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(ilElementEClass, ILElement.class, "ILElement", IS_ABSTRACT,
				IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = addEOperation(ilElementEClass, null, "getChildren", 0,
				1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(this.getList());
		EGenericType g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		EGenericType g3 = createEGenericType(this.getILElement());
		g2.setEUpperBound(g3);
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

		op = addEOperation(ilElementEClass, ecorePackage.getEBooleanObject(),
				"getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeType_Boolean(), "type", 0, 1,
				IS_UNIQUE, IS_ORDERED);

		op = addEOperation(ilElementEClass, this.getIRodinElement(),
				"getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeType_Handle(), "type", 0, 1,
				IS_UNIQUE, IS_ORDERED);

		op = addEOperation(ilElementEClass, ecorePackage.getEIntegerObject(),
				"getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeType_Integer(), "type", 0, 1,
				IS_UNIQUE, IS_ORDERED);

		op = addEOperation(ilElementEClass, ecorePackage.getELongObject(),
				"getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeType_Long(), "type", 0, 1,
				IS_UNIQUE, IS_ORDERED);

		op = addEOperation(ilElementEClass, ecorePackage.getEString(),
				"getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAttributeType_String(), "type", 0, 1,
				IS_UNIQUE, IS_ORDERED);

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

		addEOperation(ilElementEClass, ecorePackage.getEBoolean(),
				"isImplicit", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(ilElementEClass, null, "getChildrenOfType", 0, 1,
				IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getIInternalElementType());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "type", 0, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getList());
		g2 = createEGenericType(this.getILElement());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		op = addEOperation(ilElementEClass, null, "getElementType", 0, 1,
				IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getIInternalElementType());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		g3 = createEGenericType(this.getIInternalElement());
		g2.setEUpperBound(g3);
		initEOperation(op, g1);

		op = addEOperation(ilElementEClass, this.getILElement(), "createChild",
				1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getIInternalElementType());
		g2 = createEGenericType();
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "type", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getILElement(), "nextSibling", 0, 1, IS_UNIQUE,
				IS_ORDERED);

		addEOperation(ilElementEClass, this.getILElement(), "getParent", 0, 1,
				IS_UNIQUE, IS_ORDERED);

		addEOperation(ilElementEClass, this.getILElement(), "getRoot", 0, 1,
				IS_UNIQUE, IS_ORDERED);

		op = addEOperation(ilElementEClass, ecorePackage.getEInt(),
				"getChildPosition", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getILElement(), "element", 1, 1, IS_UNIQUE,
				IS_ORDERED);

		op = addEOperation(ilElementEClass, null, "addChild", 0, 1, IS_UNIQUE,
				IS_ORDERED);
		addEParameter(op, this.getILElement(), "child", 1, 1, IS_UNIQUE,
				IS_ORDERED);
		addEParameter(op, ecorePackage.getEInt(), "position", 1, 1, IS_UNIQUE,
				IS_ORDERED);

		op = addEOperation(ilElementEClass, null, "addChild", 0, 1, IS_UNIQUE,
				IS_ORDERED);
		addEParameter(op, this.getILElement(), "toAdd", 0, 1, IS_UNIQUE,
				IS_ORDERED);
		addEParameter(op, this.getILElement(), "nextSibling", 0, 1, IS_UNIQUE,
				IS_ORDERED);

		initEClass(ilAttributeEClass, ILAttribute.class, "ILAttribute",
				IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		addEOperation(ilAttributeEClass, this.getILElement(), "getOwner", 0, 1,
				IS_UNIQUE, IS_ORDERED);

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
		initEDataType(iInternalElementTypeEDataType,
				IInternalElementType.class, "IInternalElementType",
				!IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeType_BooleanEDataType,
				org.rodinp.core.IAttributeType.Boolean.class,
				"IAttributeType_Boolean", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeType_HandleEDataType, Handle.class,
				"IAttributeType_Handle", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeType_IntegerEDataType,
				org.rodinp.core.IAttributeType.Integer.class,
				"IAttributeType_Integer", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeType_LongEDataType,
				org.rodinp.core.IAttributeType.Long.class,
				"IAttributeType_Long", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAttributeType_StringEDataType,
				org.rodinp.core.IAttributeType.String.class,
				"IAttributeType_String", !IS_SERIALIZABLE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iRodinElementEDataType, IRodinElement.class,
				"IRodinElement", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/emf/2002/GenModel
		createGenModelAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/GenModel</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createGenModelAnnotations() {
		String source = "http://www.eclipse.org/emf/2002/GenModel";
		addAnnotation(ilElementEClass.getEOperations().get(0), source,
				new String[] { "body", "return getEChildren();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(1),
				source,
				new String[] {
						"body",
						"final EMap<String, Attribute> attributes = getEAttributes();\nfinal List<IAttributeValue> values = new ArrayList<IAttributeValue>(\n\t\tattributes.size());\nfor (Attribute att : attributes.values()) {\n\tfinal IAttributeValue value = valueOf(att);\n\tvalues.add(value);\n}\nreturn values;" });
		addAnnotation(
				ilElementEClass.getEOperations().get(2),
				source,
				new String[] {
						"body",
						"final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn valueOf(attribute);" });
		addAnnotation(
				ilElementEClass.getEOperations().get(3),
				source,
				new String[] {
						"body",
						"final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (Boolean) attribute.getValue();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(4),
				source,
				new String[] {
						"body",
						"final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (IRodinElement) attribute.getValue();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(5),
				source,
				new String[] {
						"body",
						"final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (Integer) attribute.getValue();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(6),
				source,
				new String[] {
						"body",
						"final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (Long) attribute.getValue();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(7),
				source,
				new String[] {
						"body",
						"final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (String) attribute.getValue();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(8),
				source,
				new String[] {
						"body",
						"final IAttributeType type = value.getType();\nAttribute attribute = getEAttributes().get(type.getId());\nfinal Object new_value = value.getValue();\nfinal Object old_value = (attribute != null) ? attribute.getValue()\n\t: null;\nif (new_value == null || new_value.equals(old_value)) {\n\treturn;\n}\nif (attribute == null) {\n\tattribute = LightcoreFactory.eINSTANCE.createAttribute();\n        attribute.setEOwner(this);\n\tattribute.setType(type);\n}\nattribute.setValue(value.getValue());\ngetEAttributes().put(type.getId(), attribute);" });
		addAnnotation(ilElementEClass.getEOperations().get(9), source,
				new String[] { "body",
						"return (IInternalElement) getERodinElement();" });
		addAnnotation(ilElementEClass.getEOperations().get(10), source,
				new String[] { "body", "EcoreUtil.delete(this, true);" });
		addAnnotation(
				ilElementEClass.getEOperations().get(11),
				source,
				new String[] {
						"body",
						"final EList<LightElement> children = getEChildren();\nchildren.move(newPos, oldPos);" });
		addAnnotation(
				ilElementEClass.getEOperations().get(13),
				source,
				new String[] {
						"body",
						"final List<ILElement> list = new <%java.util.ArrayList%><ILElement>();\nfor (ILElement child : getChildren()) {\n\tif (child.getElement().getElementType() == type) {\n\t\tlist.add(child);\n\t}\n}\nreturn list;" });
		addAnnotation(
				ilElementEClass.getEOperations().get(14),
				source,
				new String[] { "body", "return getElement().getElementType();" });
		addAnnotation(
				ilElementEClass.getEOperations().get(15),
				source,
				new String[] {
						"body",
						"final IInternalElement internalNextSibling = (nextSibling == null) ? null\n\t\t: nextSibling.getElement();\ntry {\n\tfinal IInternalElement child = getElement().createChild(type,\n\t\t\tinternalNextSibling, null);\n\tfinal InternalElement loaded = SynchroManager\n\t\t\t.loadInternalElementFor(child, eRoot);\n\taddElement(loaded, SynchroUtils.getPositionOf(eRoot, internalNextSibling));\n\treturn loaded;\n} catch (RodinDBException e) {\n\te.printStackTrace();\n}\nreturn null;" });
		addAnnotation(ilElementEClass.getEOperations().get(16), source,
				new String[] { "body", "return getEParent();" });
		addAnnotation(ilElementEClass.getEOperations().get(17), source,
				new String[] { "body", "return getERoot();" });
		addAnnotation(ilElementEClass.getEOperations().get(18), source,
				new String[] { "body",
						"return getEChildren().indexOf(element);\n" });
		addAnnotation(ilElementEClass.getEOperations().get(19), source,
				new String[] { "body",
						"getEChildren().add(position, (LightElement) child);" });
		addAnnotation(
				ilElementEClass.getEOperations().get(20),
				source,
				new String[] {
						"body",
						"if (nextSibling != null) {\n\tfinal int nextSiblingPosition = getChildPosition(nextSibling);\n\tif (nextSiblingPosition != -1) {\n\t\tgetEChildren().add(nextSiblingPosition, (LightElement) toAdd);\n\t\treturn;\n\t}\n}\ngetEChildren().add((LightElement) toAdd);\n" });
		addAnnotation(ilAttributeEClass.getEOperations().get(0), source,
				new String[] { "body", "return (ILElement)getEOwner();" });
	}

} //ApiPackageImpl
