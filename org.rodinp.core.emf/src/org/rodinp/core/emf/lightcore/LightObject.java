/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Light Object</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.rodinp.core.emf.lightcore.LightcorePackage#getLightObject()
 * @model abstract="true"
 * @generated
 */
public interface LightObject extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * returns the nearest container of this element that is a sub-type eClass
	 * or null if no container of that type
	 * @param 	the EClass that is the super-type of the returned elements
	 * @return 	containing EventBObject that is a sub-type of eClass
	 * 
	 * <!-- end-model-doc -->
	 * @model required="true" eClassRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='LightObject lObject = this;\nwhile (!eClass.isSuperTypeOf(lObject.eClass()))\n\tif (lObject.eContainer() instanceof LightObject) lObject=(LightObject)lObject.eContainer();\n\telse return null;\nreturn lObject;'"
	 * @generated
	 */
	LightObject getContaining(EClass eClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * returns a list of elements that sub-type eClass and
	 * that are contained (directly or indirectly) by this element
	 * @param  eClass - 	the EClass that is the super-type of the returned elements
	 * @param resolve - 	whether to resolve proxies
	 * 
	 * <!-- end-model-doc -->
	 * @model many="false" eClassRequired="true" resolveRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='EList<EObject> typeObjects = new BasicEList<EObject>();\ntypeObjects.add(null);\t//include the null object\nfor (TreeIterator<EObject>trit = EcoreUtil.getAllContents(this, resolve); trit.hasNext();){\n\tEObject o = trit.next();\n\tif (eClass.isSuperTypeOf(o.eClass())) typeObjects.add(o);\n}\nreturn typeObjects;'"
	 * @generated
	 */
	EList<EObject> getAllContained(EClass eClass, boolean resolve);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void load();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void save();

} // LightObject
