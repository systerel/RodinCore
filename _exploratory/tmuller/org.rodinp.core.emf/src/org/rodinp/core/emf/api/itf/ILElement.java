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
	 * @model valueDataType="org.rodinp.core.emf.api.itf.IAttributeValue"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final IAttributeType type = value.getType();\nAttribute attribute = getEAttributes().get(type.getId());\nfinal Object new_value = value.getValue();\nfinal Object old_value = (attribute != null) ? attribute.getValue()\n\t: null;\nif (new_value == null || new_value.equals(old_value)) {\n\treturn;\n}\nif (attribute == null) {\n\tattribute = LightcoreFactory.eINSTANCE.createAttribute();\n        attribute.setEOwner(this);\n\tattribute.setType(type);\n}\nattribute.setValue(value.getValue());\ngetEAttributes().put(type.getId(), attribute);'"
	 * @generated
	 */
	void setAttribute(IAttributeValue value);

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
	 * @model required="true" typeDataType="org.rodinp.core.emf.api.itf.IInternalElementType<?>" typeRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final IInternalElement internalNextSibling = (nextSibling == null) ? null\n\t\t: nextSibling.getElement();\ntry {\n\tfinal IInternalElement child = getElement().createChild(type,\n\t\t\tinternalNextSibling, null);\n\tfinal InternalElement loaded = SynchroManager\n\t\t\t.loadInternalElementFor(child, eRoot);\n\taddElement(loaded, SynchroUtils.getPositionOf(eRoot, internalNextSibling));\n\treturn loaded;\n} catch (RodinDBException e) {\n\te.printStackTrace();\n}\nreturn null;'"
	 * @generated
	 */
	ILElement createChild(IInternalElementType<?> type, ILElement nextSibling);

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

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model childRequired="true" positionRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='getEChildren().add(position, (LightElement) child);'"
	 * @generated
	 */
	void addChild(ILElement child, int position);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='if (nextSibling != null) {\n\tfinal int nextSiblingPosition = getChildPosition(nextSibling);\n\tif (nextSiblingPosition != -1) {\n\t\tgetEChildren().add(nextSiblingPosition, (LightElement) toAdd);\n\t\treturn;\n\t}\n}\ngetEChildren().add((LightElement) toAdd);\n'"
	 * @generated
	 */
	void addChild(ILElement toAdd, ILElement nextSibling);

} // ILElement
