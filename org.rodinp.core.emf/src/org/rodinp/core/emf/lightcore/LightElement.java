/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.rodinp.core.emf.api.itf.ILElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Light Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getEAttributes <em>EAttributes</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#isEIsRoot <em>EIs Root</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getReference <em>Reference</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getEChildren <em>EChildren</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getEParent <em>EParent</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getERodinElement <em>ERodin Element</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getERoot <em>ERoot</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement()
 * @model abstract="true"
 * @generated
 */
public interface LightElement extends LightObject, ILElement {
	/**
	 * Returns the value of the '<em><b>EAttributes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.rodinp.core.emf.lightcore.Attribute},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>EAttributes</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>EAttributes</em>' map.
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_EAttributes()
	 * @model mapType="org.rodinp.core.emf.lightcore.StringToAttributeMapEntry<org.eclipse.emf.ecore.EString, org.rodinp.core.emf.lightcore.Attribute>"
	 * @generated
	 */
	EMap<String, Attribute> getEAttributes();

	/**
	 * Returns the value of the '<em><b>EIs Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>EIs Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>EIs Root</em>' attribute.
	 * @see #setEIsRoot(boolean)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_EIsRoot()
	 * @model
	 * @generated
	 */
	boolean isEIsRoot();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#isEIsRoot <em>EIs Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>EIs Root</em>' attribute.
	 * @see #isEIsRoot()
	 * @generated
	 */
	void setEIsRoot(boolean value);

	/**
	 * Returns the value of the '<em><b>Reference</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reference</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference</em>' attribute.
	 * @see #setReference(String)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_Reference()
	 * @model default="" id="true" required="true"
	 * @generated
	 */
	String getReference();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#getReference <em>Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference</em>' attribute.
	 * @see #getReference()
	 * @generated
	 */
	void setReference(String value);

	/**
	 * Returns the value of the '<em><b>EChildren</b></em>' containment reference list.
	 * The list contents are of type {@link org.rodinp.core.emf.lightcore.LightElement}.
	 * It is bidirectional and its opposite is '{@link org.rodinp.core.emf.lightcore.LightElement#getEParent <em>EParent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>EChildren</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>EChildren</em>' containment reference list.
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_EChildren()
	 * @see org.rodinp.core.emf.lightcore.LightElement#getEParent
	 * @model opposite="eParent" containment="true"
	 * @generated
	 */
	EList<LightElement> getEChildren();

	/**
	 * Returns the value of the '<em><b>EParent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.rodinp.core.emf.lightcore.LightElement#getEChildren <em>EChildren</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>EParent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>EParent</em>' container reference.
	 * @see #setEParent(LightElement)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_EParent()
	 * @see org.rodinp.core.emf.lightcore.LightElement#getEChildren
	 * @model opposite="eChildren" transient="false"
	 * @generated
	 */
	LightElement getEParent();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#getEParent <em>EParent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>EParent</em>' container reference.
	 * @see #getEParent()
	 * @generated
	 */
	void setEParent(LightElement value);

	/**
	 * Returns the value of the '<em><b>ERodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>ERodin Element</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>ERodin Element</em>' attribute.
	 * @see #setERodinElement(Object)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_ERodinElement()
	 * @model
	 * @generated
	 */
	Object getERodinElement();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#getERodinElement <em>ERodin Element</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>ERodin Element</em>' attribute.
	 * @see #getERodinElement()
	 * @generated
	 */
	void setERodinElement(Object value);

	/**
	 * Returns the value of the '<em><b>ERoot</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>ERoot</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>ERoot</em>' reference.
	 * @see #setERoot(LightElement)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_ERoot()
	 * @model
	 * @generated
	 */
	LightElement getERoot();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#getERoot <em>ERoot</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>ERoot</em>' reference.
	 * @see #getERoot()
	 * @generated
	 */
	void setERoot(LightElement value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns the 'reference' attribute of this element.
	 * If this element is a proxy, the reference is obtained from the proxy URI fragment. Otherwise the value of the reference attribute is returned
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='if (this.eIsProxy()){\n\treturn ((InternalEObject)this).eProxyURI().fragment();\n}else{\n\treturn reference;\n}'"
	 * @generated
	 */
	String getReferenceWithoutResolving();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Sets the 'reference' attribute of this element.
	 * If this element is a proxy, the reference is seet in the proxy URI fragment. Otherwise the value of the reference attribute is set.
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='if (this.eIsProxy()){\n\t((InternalEObject)this).eProxyURI().appendFragment(newReference);\n}else{\n\treference = newReference;\n}'"
	 * @generated
	 */
	void doSetReference(String newReference);

	/**
	 * @generated NOT
	 * @param toAdd
	 * @param pos
	 */
	void addElement(ILElement toAdd, int pos);
	
} // LightElement
