/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

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
 * @see org.rodinp.core.emf.lightcore.LightcoreFactory
 * @model kind="package"
 * @generated
 */
public interface LightcorePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "lightcore";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://emf.core.rodinp.org/models/lightcore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.rodinp.core.emf.lightcore";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LightcorePackage eINSTANCE = org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.lightcore.impl.LightObjectImpl <em>Light Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.lightcore.impl.LightObjectImpl
	 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getLightObject()
	 * @generated
	 */
	int LIGHT_OBJECT = 0;

	/**
	 * The number of structural features of the '<em>Light Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_OBJECT_FEATURE_COUNT = EcorePackage.EOBJECT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl <em>Light Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.lightcore.impl.LightElementImpl
	 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getLightElement()
	 * @generated
	 */
	int LIGHT_ELEMENT = 1;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__ATTRIBUTES = LIGHT_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__REFERENCE = LIGHT_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__CHILDREN = LIGHT_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__PARENT = LIGHT_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__ROOT = LIGHT_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Rodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__RODIN_ELEMENT = LIGHT_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Light Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT_FEATURE_COUNT = LIGHT_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.lightcore.impl.StringToAttributeMapEntryImpl <em>String To Attribute Map Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.lightcore.impl.StringToAttributeMapEntryImpl
	 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getStringToAttributeMapEntry()
	 * @generated
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY = 2;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To Attribute Map Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl <em>Attribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.lightcore.impl.AttributeImpl
	 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getAttribute()
	 * @generated
	 */
	int ATTRIBUTE = 3;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__TYPE = LIGHT_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__VALUE = LIGHT_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__OWNER = LIGHT_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Entry</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__ENTRY = LIGHT_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__KEY = LIGHT_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_FEATURE_COUNT = LIGHT_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.lightcore.impl.InternalElementImpl <em>Internal Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.lightcore.impl.InternalElementImpl
	 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getInternalElement()
	 * @generated
	 */
	int INTERNAL_ELEMENT = 4;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__ATTRIBUTES = LIGHT_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__REFERENCE = LIGHT_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__CHILDREN = LIGHT_ELEMENT__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__PARENT = LIGHT_ELEMENT__PARENT;

	/**
	 * The feature id for the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__ROOT = LIGHT_ELEMENT__ROOT;

	/**
	 * The feature id for the '<em><b>Rodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__RODIN_ELEMENT = LIGHT_ELEMENT__RODIN_ELEMENT;

	/**
	 * The number of structural features of the '<em>Internal Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT_FEATURE_COUNT = LIGHT_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.rodinp.core.emf.lightcore.impl.ImplicitElementImpl <em>Implicit Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.rodinp.core.emf.lightcore.impl.ImplicitElementImpl
	 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getImplicitElement()
	 * @generated
	 */
	int IMPLICIT_ELEMENT = 5;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__ATTRIBUTES = LIGHT_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__REFERENCE = LIGHT_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__CHILDREN = LIGHT_ELEMENT__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__PARENT = LIGHT_ELEMENT__PARENT;

	/**
	 * The feature id for the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__ROOT = LIGHT_ELEMENT__ROOT;

	/**
	 * The feature id for the '<em><b>Rodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__RODIN_ELEMENT = LIGHT_ELEMENT__RODIN_ELEMENT;

	/**
	 * The number of structural features of the '<em>Implicit Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT_FEATURE_COUNT = LIGHT_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.lightcore.LightObject <em>Light Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Light Object</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightObject
	 * @generated
	 */
	EClass getLightObject();

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.lightcore.LightElement <em>Light Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Light Element</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement
	 * @generated
	 */
	EClass getLightElement();

	/**
	 * Returns the meta object for the map '{@link org.rodinp.core.emf.lightcore.LightElement#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Attributes</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getAttributes()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_Attributes();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.LightElement#getReference <em>Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Reference</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getReference()
	 * @see #getLightElement()
	 * @generated
	 */
	EAttribute getLightElement_Reference();

	/**
	 * Returns the meta object for the containment reference list '{@link org.rodinp.core.emf.lightcore.LightElement#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getChildren()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_Children();

	/**
	 * Returns the meta object for the container reference '{@link org.rodinp.core.emf.lightcore.LightElement#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getParent()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_Parent();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.LightElement#isRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Root</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#isRoot()
	 * @see #getLightElement()
	 * @generated
	 */
	EAttribute getLightElement_Root();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.LightElement#getRodinElement <em>Rodin Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rodin Element</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getRodinElement()
	 * @see #getLightElement()
	 * @generated
	 */
	EAttribute getLightElement_RodinElement();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To Attribute Map Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To Attribute Map Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString" keyRequired="true"
	 *        valueType="org.rodinp.core.emf.lightcore.Attribute" valueOpposite="key" valueRequired="true"
	 * @generated
	 */
	EClass getStringToAttributeMapEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToAttributeMapEntry()
	 * @generated
	 */
	EAttribute getStringToAttributeMapEntry_Key();

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToAttributeMapEntry()
	 * @generated
	 */
	EReference getStringToAttributeMapEntry_Value();

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.lightcore.Attribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute
	 * @generated
	 */
	EClass getAttribute();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.Attribute#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute#getType()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.Attribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute#getValue()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Value();

	/**
	 * Returns the meta object for the reference '{@link org.rodinp.core.emf.lightcore.Attribute#getOwner <em>Owner</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Owner</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute#getOwner()
	 * @see #getAttribute()
	 * @generated
	 */
	EReference getAttribute_Owner();

	/**
	 * Returns the meta object for the reference '{@link org.rodinp.core.emf.lightcore.Attribute#getEntry <em>Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Entry</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute#getEntry()
	 * @see #getAttribute()
	 * @generated
	 */
	EReference getAttribute_Entry();

	/**
	 * Returns the meta object for the reference '{@link org.rodinp.core.emf.lightcore.Attribute#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Key</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute#getKey()
	 * @see #getAttribute()
	 * @generated
	 */
	EReference getAttribute_Key();

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.lightcore.InternalElement <em>Internal Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Internal Element</em>'.
	 * @see org.rodinp.core.emf.lightcore.InternalElement
	 * @generated
	 */
	EClass getInternalElement();

	/**
	 * Returns the meta object for class '{@link org.rodinp.core.emf.lightcore.ImplicitElement <em>Implicit Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Implicit Element</em>'.
	 * @see org.rodinp.core.emf.lightcore.ImplicitElement
	 * @generated
	 */
	EClass getImplicitElement();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	LightcoreFactory getLightcoreFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.rodinp.core.emf.lightcore.impl.LightObjectImpl <em>Light Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.rodinp.core.emf.lightcore.impl.LightObjectImpl
		 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getLightObject()
		 * @generated
		 */
		EClass LIGHT_OBJECT = eINSTANCE.getLightObject();

		/**
		 * The meta object literal for the '{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl <em>Light Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.rodinp.core.emf.lightcore.impl.LightElementImpl
		 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getLightElement()
		 * @generated
		 */
		EClass LIGHT_ELEMENT = eINSTANCE.getLightElement();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__ATTRIBUTES = eINSTANCE.getLightElement_Attributes();

		/**
		 * The meta object literal for the '<em><b>Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LIGHT_ELEMENT__REFERENCE = eINSTANCE.getLightElement_Reference();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__CHILDREN = eINSTANCE.getLightElement_Children();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__PARENT = eINSTANCE.getLightElement_Parent();

		/**
		 * The meta object literal for the '<em><b>Root</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LIGHT_ELEMENT__ROOT = eINSTANCE.getLightElement_Root();

		/**
		 * The meta object literal for the '<em><b>Rodin Element</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LIGHT_ELEMENT__RODIN_ELEMENT = eINSTANCE.getLightElement_RodinElement();

		/**
		 * The meta object literal for the '{@link org.rodinp.core.emf.lightcore.impl.StringToAttributeMapEntryImpl <em>String To Attribute Map Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.rodinp.core.emf.lightcore.impl.StringToAttributeMapEntryImpl
		 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getStringToAttributeMapEntry()
		 * @generated
		 */
		EClass STRING_TO_ATTRIBUTE_MAP_ENTRY = eINSTANCE.getStringToAttributeMapEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_ATTRIBUTE_MAP_ENTRY__KEY = eINSTANCE.getStringToAttributeMapEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE = eINSTANCE.getStringToAttributeMapEntry_Value();

		/**
		 * The meta object literal for the '{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl <em>Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.rodinp.core.emf.lightcore.impl.AttributeImpl
		 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getAttribute()
		 * @generated
		 */
		EClass ATTRIBUTE = eINSTANCE.getAttribute();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__TYPE = eINSTANCE.getAttribute_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

		/**
		 * The meta object literal for the '<em><b>Owner</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE__OWNER = eINSTANCE.getAttribute_Owner();

		/**
		 * The meta object literal for the '<em><b>Entry</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE__ENTRY = eINSTANCE.getAttribute_Entry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE__KEY = eINSTANCE.getAttribute_Key();

		/**
		 * The meta object literal for the '{@link org.rodinp.core.emf.lightcore.impl.InternalElementImpl <em>Internal Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.rodinp.core.emf.lightcore.impl.InternalElementImpl
		 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getInternalElement()
		 * @generated
		 */
		EClass INTERNAL_ELEMENT = eINSTANCE.getInternalElement();

		/**
		 * The meta object literal for the '{@link org.rodinp.core.emf.lightcore.impl.ImplicitElementImpl <em>Implicit Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.rodinp.core.emf.lightcore.impl.ImplicitElementImpl
		 * @see org.rodinp.core.emf.lightcore.impl.LightcorePackageImpl#getImplicitElement()
		 * @generated
		 */
		EClass IMPLICIT_ELEMENT = eINSTANCE.getImplicitElement();

	}

} //LightcorePackage
