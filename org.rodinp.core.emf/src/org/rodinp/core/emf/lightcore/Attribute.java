/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore;

import java.util.Map;
import org.rodinp.core.emf.api.itf.ILAttribute;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.Attribute#getType <em>Type</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.Attribute#getValue <em>Value</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.Attribute#getEOwner <em>EOwner</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.Attribute#getEntry <em>Entry</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.Attribute#getKey <em>Key</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getAttribute()
 * @model
 * @generated
 */
public interface Attribute extends LightObject, ILAttribute {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(Object)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getAttribute_Type()
	 * @model default="" required="true"
	 * @generated
	 */
	Object getType();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.Attribute#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(Object value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(Object)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getAttribute_Value()
	 * @model required="true"
	 * @generated
	 */
	Object getValue();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.Attribute#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(Object value);

	/**
	 * Returns the value of the '<em><b>EOwner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>EOwner</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>EOwner</em>' reference.
	 * @see #setEOwner(LightElement)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getAttribute_EOwner()
	 * @model required="true"
	 * @generated
	 */
	LightElement getEOwner();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.Attribute#getEOwner <em>EOwner</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>EOwner</em>' reference.
	 * @see #getEOwner()
	 * @generated
	 */
	void setEOwner(LightElement value);

	/**
	 * Returns the value of the '<em><b>Entry</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entry</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entry</em>' reference.
	 * @see #setEntry(Map.Entry)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getAttribute_Entry()
	 * @model mapType="org.rodinp.core.emf.lightcore.StringToAttributeMapEntry<org.eclipse.emf.ecore.EString, org.rodinp.core.emf.lightcore.Attribute>" ordered="false"
	 * @generated
	 */
	Map.Entry<String, Attribute> getEntry();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.Attribute#getEntry <em>Entry</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Entry</em>' reference.
	 * @see #getEntry()
	 * @generated
	 */
	void setEntry(Map.Entry<String, Attribute> value);

	/**
	 * Returns the value of the '<em><b>Key</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Key</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key</em>' reference.
	 * @see #setKey(Map.Entry)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getAttribute_Key()
	 * @model mapType="org.rodinp.core.emf.lightcore.StringToAttributeMapEntry<org.eclipse.emf.ecore.EString, org.rodinp.core.emf.lightcore.Attribute>"
	 * @generated
	 */
	Map.Entry<String, Attribute> getKey();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.Attribute#getKey <em>Key</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' reference.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(Map.Entry<String, Attribute> value);

} // Attribute
