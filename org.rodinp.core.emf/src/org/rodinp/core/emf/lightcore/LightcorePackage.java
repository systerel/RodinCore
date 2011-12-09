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
	 * The feature id for the '<em><b>EAttributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__EATTRIBUTES = LIGHT_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>EIs Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__EIS_ROOT = LIGHT_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__REFERENCE = LIGHT_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>EChildren</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__ECHILDREN = LIGHT_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>EParent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__EPARENT = LIGHT_OBJECT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>ERodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__ERODIN_ELEMENT = LIGHT_OBJECT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>ERoot</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT__EROOT = LIGHT_OBJECT_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Light Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LIGHT_ELEMENT_FEATURE_COUNT = LIGHT_OBJECT_FEATURE_COUNT + 7;

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
	 * The feature id for the '<em><b>EOwner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__EOWNER = LIGHT_OBJECT_FEATURE_COUNT + 2;

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
	 * The feature id for the '<em><b>EAttributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__EATTRIBUTES = LIGHT_ELEMENT__EATTRIBUTES;

	/**
	 * The feature id for the '<em><b>EIs Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__EIS_ROOT = LIGHT_ELEMENT__EIS_ROOT;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__REFERENCE = LIGHT_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>EChildren</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__ECHILDREN = LIGHT_ELEMENT__ECHILDREN;

	/**
	 * The feature id for the '<em><b>EParent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__EPARENT = LIGHT_ELEMENT__EPARENT;

	/**
	 * The feature id for the '<em><b>ERodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__ERODIN_ELEMENT = LIGHT_ELEMENT__ERODIN_ELEMENT;

	/**
	 * The feature id for the '<em><b>ERoot</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERNAL_ELEMENT__EROOT = LIGHT_ELEMENT__EROOT;

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
	 * The feature id for the '<em><b>EAttributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__EATTRIBUTES = LIGHT_ELEMENT__EATTRIBUTES;

	/**
	 * The feature id for the '<em><b>EIs Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__EIS_ROOT = LIGHT_ELEMENT__EIS_ROOT;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__REFERENCE = LIGHT_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>EChildren</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__ECHILDREN = LIGHT_ELEMENT__ECHILDREN;

	/**
	 * The feature id for the '<em><b>EParent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__EPARENT = LIGHT_ELEMENT__EPARENT;

	/**
	 * The feature id for the '<em><b>ERodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__ERODIN_ELEMENT = LIGHT_ELEMENT__ERODIN_ELEMENT;

	/**
	 * The feature id for the '<em><b>ERoot</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLICIT_ELEMENT__EROOT = LIGHT_ELEMENT__EROOT;

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
	 * Returns the meta object for the map '{@link org.rodinp.core.emf.lightcore.LightElement#getEAttributes <em>EAttributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>EAttributes</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getEAttributes()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_EAttributes();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.LightElement#isEIsRoot <em>EIs Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>EIs Root</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#isEIsRoot()
	 * @see #getLightElement()
	 * @generated
	 */
	EAttribute getLightElement_EIsRoot();

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
	 * Returns the meta object for the containment reference list '{@link org.rodinp.core.emf.lightcore.LightElement#getEChildren <em>EChildren</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>EChildren</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getEChildren()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_EChildren();

	/**
	 * Returns the meta object for the container reference '{@link org.rodinp.core.emf.lightcore.LightElement#getEParent <em>EParent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>EParent</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getEParent()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_EParent();

	/**
	 * Returns the meta object for the reference '{@link org.rodinp.core.emf.lightcore.LightElement#getERoot <em>ERoot</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>ERoot</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getERoot()
	 * @see #getLightElement()
	 * @generated
	 */
	EReference getLightElement_ERoot();

	/**
	 * Returns the meta object for the attribute '{@link org.rodinp.core.emf.lightcore.LightElement#getERodinElement <em>ERodin Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>ERodin Element</em>'.
	 * @see org.rodinp.core.emf.lightcore.LightElement#getERodinElement()
	 * @see #getLightElement()
	 * @generated
	 */
	EAttribute getLightElement_ERodinElement();

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
	 * Returns the meta object for the reference '{@link org.rodinp.core.emf.lightcore.Attribute#getEOwner <em>EOwner</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>EOwner</em>'.
	 * @see org.rodinp.core.emf.lightcore.Attribute#getEOwner()
	 * @see #getAttribute()
	 * @generated
	 */
	EReference getAttribute_EOwner();

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
		 * The meta object literal for the '<em><b>EAttributes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__EATTRIBUTES = eINSTANCE.getLightElement_EAttributes();

		/**
		 * The meta object literal for the '<em><b>EIs Root</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LIGHT_ELEMENT__EIS_ROOT = eINSTANCE.getLightElement_EIsRoot();

		/**
		 * The meta object literal for the '<em><b>Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LIGHT_ELEMENT__REFERENCE = eINSTANCE.getLightElement_Reference();

		/**
		 * The meta object literal for the '<em><b>EChildren</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__ECHILDREN = eINSTANCE.getLightElement_EChildren();

		/**
		 * The meta object literal for the '<em><b>EParent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__EPARENT = eINSTANCE.getLightElement_EParent();

		/**
		 * The meta object literal for the '<em><b>ERoot</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LIGHT_ELEMENT__EROOT = eINSTANCE.getLightElement_ERoot();

		/**
		 * The meta object literal for the '<em><b>ERodin Element</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LIGHT_ELEMENT__ERODIN_ELEMENT = eINSTANCE.getLightElement_ERodinElement();

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
		 * The meta object literal for the '<em><b>EOwner</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTRIBUTE__EOWNER = eINSTANCE.getAttribute_EOwner();

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
