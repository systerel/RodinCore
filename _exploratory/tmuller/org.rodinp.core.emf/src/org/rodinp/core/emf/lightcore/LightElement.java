/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Light Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getReference <em>Reference</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getChildren <em>Children</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getParent <em>Parent</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#isRoot <em>Root</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.LightElement#getRodinElement <em>Rodin Element</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement()
 * @model abstract="true"
 * @generated
 */
public interface LightElement extends LightObject {
	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link org.rodinp.core.emf.lightcore.Attribute},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attributes</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' map.
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_Attributes()
	 * @model mapType="org.rodinp.core.emf.lightcore.StringToAttributeMapEntry<org.eclipse.emf.ecore.EString, org.rodinp.core.emf.lightcore.Attribute>"
	 * @generated
	 */
	EMap<String, Attribute> getAttributes();

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
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list.
	 * The list contents are of type {@link org.rodinp.core.emf.lightcore.LightElement}.
	 * It is bidirectional and its opposite is '{@link org.rodinp.core.emf.lightcore.LightElement#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_Children()
	 * @see org.rodinp.core.emf.lightcore.LightElement#getParent
	 * @model opposite="parent" containment="true"
	 * @generated
	 */
	EList<LightElement> getChildren();

	/**
	 * Returns the value of the '<em><b>Parent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.rodinp.core.emf.lightcore.LightElement#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' container reference.
	 * @see #setParent(LightElement)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_Parent()
	 * @see org.rodinp.core.emf.lightcore.LightElement#getChildren
	 * @model opposite="children" transient="false"
	 * @generated
	 */
	LightElement getParent();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(LightElement value);

	/**
	 * Returns the value of the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root</em>' attribute.
	 * @see #setRoot(boolean)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_Root()
	 * @model
	 * @generated
	 */
	boolean isRoot();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#isRoot <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' attribute.
	 * @see #isRoot()
	 * @generated
	 */
	void setRoot(boolean value);

	/**
	 * Returns the value of the '<em><b>Rodin Element</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rodin Element</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rodin Element</em>' attribute.
	 * @see #setRodinElement(Object)
	 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightElement_RodinElement()
	 * @model
	 * @generated
	 */
	Object getRodinElement();

	/**
	 * Sets the value of the '{@link org.rodinp.core.emf.lightcore.LightElement#getRodinElement <em>Rodin Element</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rodin Element</em>' attribute.
	 * @see #getRodinElement()
	 * @generated
	 */
	void setRodinElement(Object value);

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model many="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final EList<LightElement> list = new BasicEList<LightElement>();\n\tif (!(type instanceof IInternalElement)) {\n\t\treturn list;\t\n\t}\n\tfinal EList<LightElement> children = getChildren();\n\tfor (LightElement child : children) {\n\t\tif (((IInternalElement) child.getRodinElement()).getElementType() == type) {\n\t\t\tlist.add(child);\n\t\t}\n\t}\n\treturn list;'"
	 * @generated
	 */
	EList<?> getElementsOfType(Object type);

} // LightElement
