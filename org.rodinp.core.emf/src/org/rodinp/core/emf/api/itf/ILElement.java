/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import java.util.List;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeType.Handle;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IL Element</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.rodinp.core.emf.api.ApiPackage#getILElement()
 * @model interface="true" abstract="true"
 * @generated
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILElement {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.List<? extends org.rodinp.core.emf.api.itf.ILElement>" many="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getEChildren();'"
	 * @generated
	 */
	List<? extends ILElement> getChildren();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.List<org.rodinp.core.emf.api.itf.IAttributeValue>" many="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final EMap<String, Attribute> attributes = getEAttributes();\nfinal List<IAttributeValue> values = new ArrayList<IAttributeValue>(\n\t\tattributes.size());\nfor (Attribute att : attributes.values()) {\n\tfinal IAttributeValue value = valueOf(att);\n\tvalues.add(value);\n}\nreturn values;'"
	 * @generated
	 */
	List<IAttributeValue> getAttributes();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="org.rodinp.core.emf.api.itf.IAttributeValue" typeDataType="org.rodinp.core.emf.api.itf.IAttributeType"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn valueOf(attribute);'"
	 * @generated
	 */
	IAttributeValue getAttribute(IAttributeType type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model typeDataType="org.rodinp.core.emf.api.itf.IAttributeType_Boolean"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (Boolean) attribute.getValue();'"
	 * @generated
	 */
	Boolean getAttribute(org.rodinp.core.IAttributeType.Boolean type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="org.rodinp.core.emf.api.itf.IRodinElement" typeDataType="org.rodinp.core.emf.api.itf.IAttributeType_Handle"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (IRodinElement) attribute.getValue();'"
	 * @generated
	 */
	IRodinElement getAttribute(Handle type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model typeDataType="org.rodinp.core.emf.api.itf.IAttributeType_Integer"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (Integer) attribute.getValue();'"
	 * @generated
	 */
	Integer getAttribute(org.rodinp.core.IAttributeType.Integer type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model typeDataType="org.rodinp.core.emf.api.itf.IAttributeType_Long"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (Long) attribute.getValue();'"
	 * @generated
	 */
	Long getAttribute(org.rodinp.core.IAttributeType.Long type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model typeDataType="org.rodinp.core.emf.api.itf.IAttributeType_String"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final Attribute attribute = getEAttributes().get(type.getId());\nif (attribute == null)\n\treturn null;\nreturn (String) attribute.getValue();'"
	 * @generated
	 */
	String getAttribute(org.rodinp.core.IAttributeType.String type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.IInternalElement"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return (IInternalElement) getERodinElement();'"
	 * @generated
	 */
	IInternalElement getElement();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='EcoreUtil.delete(this, true);'"
	 * @generated
	 */
	void delete();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='final EList<LightElement> children = getEChildren();\nchildren.move(newPos, oldPos);'"
	 * @generated
	 */
	void moveChild(int newPos, int oldPos);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isImplicit();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="org.rodinp.core.emf.api.itf.List<org.rodinp.core.emf.api.itf.ILElement>" many="false" typeDataType="org.rodinp.core.emf.api.itf.IInternalElementType<?>"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final List<ILElement> list = new <%java.util.ArrayList%><ILElement>();\nfor (ILElement child : getChildren()) {\n\tif (child.getElement().getElementType() == type) {\n\t\tlist.add(child);\n\t}\n}\nreturn list;'"
	 * @generated
	 */
	List<ILElement> getChildrenOfType(IInternalElementType<?> type);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.rodinp.core.emf.api.itf.IInternalElementType<? extends org.rodinp.core.emf.api.itf.IInternalElement>"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getElement().getElementType();'"
	 * @generated
	 */
	IInternalElementType<? extends IInternalElement> getElementType();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getEParent();'"
	 * @generated
	 */
	ILElement getParent();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getERoot();'"
	 * @generated
	 */
	ILElement getRoot();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true" elementRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getEChildren().indexOf(element);\n'"
	 * @generated
	 */
	int getChildPosition(ILElement element);

} // ILElement
