/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.api.itf;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

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
public interface ILElement extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.List<org.rodinp.core.emf.api.itf.ILElement>" many="false"
	 * @generated
	 */
	List<ILElement> getChildren();

} // ILElement
