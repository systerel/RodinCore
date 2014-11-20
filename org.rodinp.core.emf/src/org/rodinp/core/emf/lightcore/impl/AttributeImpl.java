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
package org.rodinp.core.emf.lightcore.impl;

import java.util.Map;


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl#getEOwner <em>EOwner</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl#getEntry <em>Entry</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.AttributeImpl#getKey <em>Key</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AttributeImpl extends LightObjectImpl implements Attribute {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated NOT
	 * @ordered
	 */
	protected static final Object TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected Object type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final Object VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected Object value = VALUE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEOwner() <em>EOwner</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEOwner()
	 * @generated
	 * @ordered
	 */
	protected LightElement eOwner;

	/**
	 * The cached value of the '{@link #getEntry() <em>Entry</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntry()
	 * @generated
	 * @ordered
	 */
	protected Map.Entry<String, Attribute> entry;

	/**
	 * The cached value of the '{@link #getKey() <em>Key</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected Map.Entry<String, Attribute> key;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttributeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LightcorePackage.Literals.ATTRIBUTE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(Object newType) {
		Object oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.ATTRIBUTE__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(Object newValue) {
		Object oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.ATTRIBUTE__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightElement getEOwner() {
		if (eOwner != null && eOwner.eIsProxy()) {
			InternalEObject oldEOwner = (InternalEObject)eOwner;
			eOwner = (LightElement)eResolveProxy(oldEOwner);
			if (eOwner != oldEOwner) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, LightcorePackage.ATTRIBUTE__EOWNER, oldEOwner, eOwner));
			}
		}
		return eOwner;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightElement basicGetEOwner() {
		return eOwner;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEOwner(LightElement newEOwner) {
		LightElement oldEOwner = eOwner;
		eOwner = newEOwner;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.ATTRIBUTE__EOWNER, oldEOwner, eOwner));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Map.Entry<String, Attribute> getEntry() {
		if (entry != null && ((EObject)entry).eIsProxy()) {
			InternalEObject oldEntry = (InternalEObject)entry;
			entry = (Map.Entry<String, Attribute>)eResolveProxy(oldEntry);
			if (entry != oldEntry) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, LightcorePackage.ATTRIBUTE__ENTRY, oldEntry, entry));
			}
		}
		return entry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Attribute> basicGetEntry() {
		return entry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEntry(Map.Entry<String, Attribute> newEntry) {
		Map.Entry<String, Attribute> oldEntry = entry;
		entry = newEntry;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.ATTRIBUTE__ENTRY, oldEntry, entry));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public Map.Entry<String, Attribute> getKey() {
		if (key != null && ((EObject)key).eIsProxy()) {
			InternalEObject oldKey = (InternalEObject)key;
			key = (Map.Entry<String, Attribute>)eResolveProxy(oldKey);
			if (key != oldKey) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, LightcorePackage.ATTRIBUTE__KEY, oldKey, key));
			}
		}
		return key;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Attribute> basicGetKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetKey(Map.Entry<String, Attribute> newKey, NotificationChain msgs) {
		Map.Entry<String, Attribute> oldKey = key;
		key = newKey;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LightcorePackage.ATTRIBUTE__KEY, oldKey, newKey);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setKey(Map.Entry<String, Attribute> newKey) {
		if (newKey != key) {
			NotificationChain msgs = null;
			if (key != null)
				msgs = ((InternalEObject)key).eInverseRemove(this, LightcorePackage.STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE, Map.Entry.class, msgs);
			if (newKey != null)
				msgs = ((InternalEObject)newKey).eInverseAdd(this, LightcorePackage.STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE, Map.Entry.class, msgs);
			msgs = basicSetKey(newKey, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.ATTRIBUTE__KEY, newKey, newKey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ILElement getOwner() {
		return getEOwner();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LightcorePackage.ATTRIBUTE__KEY:
				if (key != null)
					msgs = ((InternalEObject)key).eInverseRemove(this, LightcorePackage.STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE, Map.Entry.class, msgs);
				return basicSetKey((Map.Entry<String, Attribute>)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LightcorePackage.ATTRIBUTE__KEY:
				return basicSetKey(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LightcorePackage.ATTRIBUTE__TYPE:
				return getType();
			case LightcorePackage.ATTRIBUTE__VALUE:
				return getValue();
			case LightcorePackage.ATTRIBUTE__EOWNER:
				if (resolve) return getEOwner();
				return basicGetEOwner();
			case LightcorePackage.ATTRIBUTE__ENTRY:
				if (resolve) return getEntry();
				return basicGetEntry();
			case LightcorePackage.ATTRIBUTE__KEY:
				if (resolve) return getKey();
				return basicGetKey();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case LightcorePackage.ATTRIBUTE__TYPE:
				setType(newValue);
				return;
			case LightcorePackage.ATTRIBUTE__VALUE:
				setValue(newValue);
				return;
			case LightcorePackage.ATTRIBUTE__EOWNER:
				setEOwner((LightElement)newValue);
				return;
			case LightcorePackage.ATTRIBUTE__ENTRY:
				setEntry((Map.Entry<String, Attribute>)newValue);
				return;
			case LightcorePackage.ATTRIBUTE__KEY:
				setKey((Map.Entry<String, Attribute>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case LightcorePackage.ATTRIBUTE__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case LightcorePackage.ATTRIBUTE__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case LightcorePackage.ATTRIBUTE__EOWNER:
				setEOwner((LightElement)null);
				return;
			case LightcorePackage.ATTRIBUTE__ENTRY:
				setEntry((Map.Entry<String, Attribute>)null);
				return;
			case LightcorePackage.ATTRIBUTE__KEY:
				setKey((Map.Entry<String, Attribute>)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case LightcorePackage.ATTRIBUTE__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case LightcorePackage.ATTRIBUTE__VALUE:
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
			case LightcorePackage.ATTRIBUTE__EOWNER:
				return eOwner != null;
			case LightcorePackage.ATTRIBUTE__ENTRY:
				return entry != null;
			case LightcorePackage.ATTRIBUTE__KEY:
				return key != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (type: ");
		result.append(type);
		result.append(", value: ");
		result.append(value);
		result.append(')');
		return result.toString();
	}

} //AttributeImpl
