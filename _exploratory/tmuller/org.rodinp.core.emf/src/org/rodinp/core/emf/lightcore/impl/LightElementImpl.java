/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Light Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getEAttributes <em>EAttributes</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getEChildren <em>EChildren</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getEParent <em>EParent</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#isERoot <em>ERoot</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getERodinElement <em>ERodin Element</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class LightElementImpl extends LightObjectImpl implements LightElement {
	/**
	 * The cached value of the '{@link #getEAttributes() <em>EAttributes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEAttributes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Attribute> eAttributes;

	/**
	 * The default value of the '{@link #getReference() <em>Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReference()
	 * @generated
	 * @ordered
	 */
	protected static final String REFERENCE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getReference() <em>Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReference()
	 * @generated
	 * @ordered
	 */
	protected String reference = REFERENCE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEChildren() <em>EChildren</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<LightElement> eChildren;

	/**
	 * The default value of the '{@link #isERoot() <em>ERoot</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isERoot()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EROOT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isERoot() <em>ERoot</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isERoot()
	 * @generated
	 * @ordered
	 */
	protected boolean eRoot = EROOT_EDEFAULT;

	/**
	 * The default value of the '{@link #getERodinElement() <em>ERodin Element</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getERodinElement()
	 * @generated
	 * @ordered
	 */
	protected static final Object ERODIN_ELEMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getERodinElement() <em>ERodin Element</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getERodinElement()
	 * @generated
	 * @ordered
	 */
	protected Object eRodinElement = ERODIN_ELEMENT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LightElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LightcorePackage.Literals.LIGHT_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, Attribute> getEAttributes() {
		if (eAttributes == null) {
			eAttributes = new EcoreEMap<String,Attribute>(LightcorePackage.Literals.STRING_TO_ATTRIBUTE_MAP_ENTRY, StringToAttributeMapEntryImpl.class, this, LightcorePackage.LIGHT_ELEMENT__EATTRIBUTES);
		}
		return eAttributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReference(String newReference) {
		String oldReference = reference;
		reference = newReference;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__REFERENCE, oldReference, reference));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<LightElement> getEChildren() {
		if (eChildren == null) {
			eChildren = new EObjectContainmentWithInverseEList<LightElement>(LightElement.class, this, LightcorePackage.LIGHT_ELEMENT__ECHILDREN, LightcorePackage.LIGHT_ELEMENT__EPARENT);
		}
		return eChildren;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightElement getEParent() {
		if (eContainerFeatureID() != LightcorePackage.LIGHT_ELEMENT__EPARENT) return null;
		return (LightElement)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetEParent(LightElement newEParent, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newEParent, LightcorePackage.LIGHT_ELEMENT__EPARENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEParent(LightElement newEParent) {
		if (newEParent != eInternalContainer() || (eContainerFeatureID() != LightcorePackage.LIGHT_ELEMENT__EPARENT && newEParent != null)) {
			if (EcoreUtil.isAncestor(this, newEParent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newEParent != null)
				msgs = ((InternalEObject)newEParent).eInverseAdd(this, LightcorePackage.LIGHT_ELEMENT__ECHILDREN, LightElement.class, msgs);
			msgs = basicSetEParent(newEParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__EPARENT, newEParent, newEParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isERoot() {
		return eRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setERoot(boolean newERoot) {
		boolean oldERoot = eRoot;
		eRoot = newERoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__EROOT, oldERoot, eRoot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getERodinElement() {
		return eRodinElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setERodinElement(Object newERodinElement) {
		Object oldERodinElement = eRodinElement;
		eRodinElement = newERodinElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT, oldERodinElement, eRodinElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<? extends ILElement> getChildren() {
		return getEChildren();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<IAttributeValue> getAttributes() {
		final EMap<String, Attribute> attributes = getEAttributes();
		final List<IAttributeValue> values = new ArrayList<IAttributeValue>(
				attributes.size());
		for (Attribute att : attributes.values()) {
			values.add((IAttributeValue) att.getValue());
		}
		return values;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IAttributeValue getAttribute(IAttributeType type) {
		final Attribute attribute = getEAttributes().get(type.getId());
		if (attribute == null)
			return null;
		return (IAttributeValue) attribute.getValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttribute(IAttributeValue value) {
		final Attribute lAttribute = LightcoreFactory.eINSTANCE.createAttribute();
		lAttribute.setOwner(this);
		lAttribute.setType(value.getType());
		lAttribute.setValue(value);
		getEAttributes().put(value.getType().getId(), lAttribute);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IInternalElement getElement() {
		return (IInternalElement) getERodinElement();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void delete() {
		EcoreUtil.delete(this, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void moveChild(int newPos, int oldPos) {
		final EList<LightElement> children = getEChildren();
		children.move(newPos, oldPos);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isImplicit() {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<ILElement> getChildrenOfType(IInternalElementType<?> type) {
		final List<ILElement> list = new ArrayList<ILElement>();
		for (ILElement child : getChildren()) {
			if (child.getElement().getElementType() == type) {
				list.add(child);
			}
		}
		return list;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return getElement().getElementType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReferenceWithoutResolving() {
		if (this.eIsProxy()){
			return ((InternalEObject)this).eProxyURI().fragment();
		}else{
			return reference;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void doSetReference(String newReference) {
		if (this.eIsProxy()){
			((InternalEObject)this).eProxyURI().appendFragment(newReference);
		}else{
			reference = newReference;
		}
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
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getEChildren()).basicAdd(otherEnd, msgs);
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetEParent((LightElement)otherEnd, msgs);
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
			case LightcorePackage.LIGHT_ELEMENT__EATTRIBUTES:
				return ((InternalEList<?>)getEAttributes()).basicRemove(otherEnd, msgs);
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				return ((InternalEList<?>)getEChildren()).basicRemove(otherEnd, msgs);
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				return basicSetEParent(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				return eInternalContainer().eInverseRemove(this, LightcorePackage.LIGHT_ELEMENT__ECHILDREN, LightElement.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LightcorePackage.LIGHT_ELEMENT__EATTRIBUTES:
				if (coreType) return getEAttributes();
				else return getEAttributes().map();
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				return getReference();
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				return getEChildren();
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				return getEParent();
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				return isERoot();
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				return getERodinElement();
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
			case LightcorePackage.LIGHT_ELEMENT__EATTRIBUTES:
				((EStructuralFeature.Setting)getEAttributes()).set(newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				setReference((String)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				getEChildren().clear();
				getEChildren().addAll((Collection<? extends LightElement>)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				setEParent((LightElement)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				setERoot((Boolean)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				setERodinElement(newValue);
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
			case LightcorePackage.LIGHT_ELEMENT__EATTRIBUTES:
				getEAttributes().clear();
				return;
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				setReference(REFERENCE_EDEFAULT);
				return;
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				getEChildren().clear();
				return;
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				setEParent((LightElement)null);
				return;
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				setERoot(EROOT_EDEFAULT);
				return;
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				setERodinElement(ERODIN_ELEMENT_EDEFAULT);
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
			case LightcorePackage.LIGHT_ELEMENT__EATTRIBUTES:
				return eAttributes != null && !eAttributes.isEmpty();
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				return REFERENCE_EDEFAULT == null ? reference != null : !REFERENCE_EDEFAULT.equals(reference);
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				return eChildren != null && !eChildren.isEmpty();
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				return getEParent() != null;
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				return eRoot != EROOT_EDEFAULT;
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				return ERODIN_ELEMENT_EDEFAULT == null ? eRodinElement != null : !ERODIN_ELEMENT_EDEFAULT.equals(eRodinElement);
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
		result.append(" (reference: ");
		result.append(reference);
		result.append(", eRoot: ");
		result.append(eRoot);
		result.append(", eRodinElement: ");
		result.append(eRodinElement);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void load() {
		if (eRodinElement instanceof IInternalElement) {
			final IInternalElement iElement = (IInternalElement) eRodinElement;
			this.setERoot(iElement.isRoot());
			SynchroUtils.loadAttributes(iElement, this);
			SynchroUtils.adaptForElementMoveAndRemove(this);
			SynchroUtils.adaptRootForImplicitChildren(this);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void save() {
		if (eRodinElement instanceof IInternalElement) {
			//
		}
	}

} //LightElementImpl
