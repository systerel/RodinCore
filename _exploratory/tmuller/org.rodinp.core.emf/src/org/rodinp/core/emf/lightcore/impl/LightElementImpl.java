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
import org.rodinp.core.IAttributeType.Handle;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.sync.SynchroManager;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Light Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getEAttributes <em>EAttributes</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#isEIsRoot <em>EIs Root</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getEChildren <em>EChildren</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getEParent <em>EParent</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getERodinElement <em>ERodin Element</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getERoot <em>ERoot</em>}</li>
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
	 * The default value of the '{@link #isEIsRoot() <em>EIs Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEIsRoot()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EIS_ROOT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isEIsRoot() <em>EIs Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEIsRoot()
	 * @generated
	 * @ordered
	 */
	protected boolean eIsRoot = EIS_ROOT_EDEFAULT;

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
	 * The cached value of the '{@link #getERoot() <em>ERoot</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getERoot()
	 * @generated
	 * @ordered
	 */
	protected LightElement eRoot;

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
	public boolean isEIsRoot() {
		return eIsRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEIsRoot(boolean newEIsRoot) {
		boolean oldEIsRoot = eIsRoot;
		eIsRoot = newEIsRoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__EIS_ROOT, oldEIsRoot, eIsRoot));
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
	public LightElement getERoot() {
		if (eRoot != null && eRoot.eIsProxy()) {
			InternalEObject oldERoot = (InternalEObject)eRoot;
			eRoot = (LightElement)eResolveProxy(oldERoot);
			if (eRoot != oldERoot) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, LightcorePackage.LIGHT_ELEMENT__EROOT, oldERoot, eRoot));
			}
		}
		return eRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightElement basicGetERoot() {
		return eRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setERoot(LightElement newERoot) {
		LightElement oldERoot = eRoot;
		eRoot = newERoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__EROOT, oldERoot, eRoot));
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
			final IAttributeValue value = valueOf(att);
			values.add(value);
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
		return valueOf(attribute);
	}

	private static IAttributeValue valueOf(Attribute att) {
		final IAttributeType type = (IAttributeType) att.getType();
		final Object valueObj = att.getValue();
		if (type instanceof IAttributeType.Boolean) {
			return ((IAttributeType.Boolean) type)
					.makeValue((Boolean) valueObj);
		}
		if (type instanceof IAttributeType.Handle) {
			return ((IAttributeType.Handle) type)
					.makeValue((IRodinElement) valueObj);
		}
		if (type instanceof IAttributeType.String) {
			return ((IAttributeType.String) type).makeValue((String) valueObj);
		}
		if (type instanceof IAttributeType.Integer) {
			return ((IAttributeType.Integer) type)
					.makeValue((Integer) valueObj);
		}
		if (type instanceof IAttributeType.Long) {
			return ((IAttributeType.Long) type).makeValue((Long) valueObj);
		}
		throw new IllegalStateException("illegal attribute type: "
				+ type.getId());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getAttribute(org.rodinp.core.IAttributeType.Boolean type) {
		final Attribute attribute = getEAttributes().get(type.getId());
		if (attribute == null)
			return null;
		return (Boolean) attribute.getValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IRodinElement getAttribute(Handle type) {
		final Attribute attribute = getEAttributes().get(type.getId());
		if (attribute == null)
			return null;
		return (IRodinElement) attribute.getValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Integer getAttribute(org.rodinp.core.IAttributeType.Integer type) {
		final Attribute attribute = getEAttributes().get(type.getId());
		if (attribute == null)
			return null;
		return (Integer) attribute.getValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Long getAttribute(org.rodinp.core.IAttributeType.Long type) {
		final Attribute attribute = getEAttributes().get(type.getId());
		if (attribute == null)
			return null;
		return (Long) attribute.getValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAttribute(org.rodinp.core.IAttributeType.String type) {
		final Attribute attribute = getEAttributes().get(type.getId());
		if (attribute == null)
			return null;
		return (String) attribute.getValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttribute(IAttributeValue value) {
		final IAttributeType type = value.getType();
		Attribute attribute = getEAttributes().get(type.getId());
		final Object new_value = value.getValue();
		final Object old_value = (attribute != null) ? attribute.getValue()
			: null;
		if (new_value == null || new_value.equals(old_value)) {
			return;
		}
		if (attribute == null) {
			attribute = LightcoreFactory.eINSTANCE.createAttribute();
		        attribute.setEOwner(this);
			attribute.setType(type);
		}
		attribute.setValue(value.getValue());
		getEAttributes().put(type.getId(), attribute);
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
	public ILElement createChild(IInternalElementType<?> type, ILElement nextSibling) {
		final IInternalElement internalNextSibling = (nextSibling == null) ? null
				: nextSibling.getElement();
		try {
			final IInternalElement child = getElement().createChild(type,
					internalNextSibling, null);
			final InternalElement loaded = SynchroManager
					.loadInternalElementFor(child, eRoot);
			addElement(loaded, SynchroUtils.getPositionOf(eRoot, internalNextSibling));
			return loaded;
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ILElement getParent() {
		return getEParent();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ILElement getRoot() {
		return getERoot();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getChildPosition(ILElement element) {
		return getEChildren().indexOf(element);
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addChild(ILElement child, int position) {
		final LightElement lChild = (LightElement) child;
		getEChildren().add(position, lChild);
		lChild.setEParent(this);
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addChild(ILElement toAdd, ILElement nextSibling) {
		int nextSiblingPosition = -1;
		if (nextSibling != null) {
			nextSiblingPosition = getChildPosition(nextSibling);
		}
		final LightElement child = (LightElement) toAdd;
		if (nextSiblingPosition == -1) {
			getEChildren().add(child);
		} else {
			getEChildren().add(nextSiblingPosition, child);
		}
		child.setEParent(this);
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
			case LightcorePackage.LIGHT_ELEMENT__EIS_ROOT:
				return isEIsRoot();
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				return getReference();
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				return getEChildren();
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				return getEParent();
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				return getERodinElement();
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				if (resolve) return getERoot();
				return basicGetERoot();
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
			case LightcorePackage.LIGHT_ELEMENT__EIS_ROOT:
				setEIsRoot((Boolean)newValue);
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
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				setERodinElement(newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				setERoot((LightElement)newValue);
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
			case LightcorePackage.LIGHT_ELEMENT__EIS_ROOT:
				setEIsRoot(EIS_ROOT_EDEFAULT);
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
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				setERodinElement(ERODIN_ELEMENT_EDEFAULT);
				return;
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				setERoot((LightElement)null);
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
			case LightcorePackage.LIGHT_ELEMENT__EIS_ROOT:
				return eIsRoot != EIS_ROOT_EDEFAULT;
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				return REFERENCE_EDEFAULT == null ? reference != null : !REFERENCE_EDEFAULT.equals(reference);
			case LightcorePackage.LIGHT_ELEMENT__ECHILDREN:
				return eChildren != null && !eChildren.isEmpty();
			case LightcorePackage.LIGHT_ELEMENT__EPARENT:
				return getEParent() != null;
			case LightcorePackage.LIGHT_ELEMENT__ERODIN_ELEMENT:
				return ERODIN_ELEMENT_EDEFAULT == null ? eRodinElement != null : !ERODIN_ELEMENT_EDEFAULT.equals(eRodinElement);
			case LightcorePackage.LIGHT_ELEMENT__EROOT:
				return eRoot != null;
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
		result.append(" (eIsRoot: ");
		result.append(eIsRoot);
		result.append(", reference: ");
		result.append(reference);
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
			SynchroUtils.loadAttributes(iElement, this, true);
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
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void addElement(ILElement toAdd, int pos) {
		final ILElement found = SynchroUtils.findElement(toAdd.getElement(),
				eRoot);
		final int foundIndex = getEChildren().indexOf(found);
		if (found != null) {
			if (pos != -1 && foundIndex != -1 && foundIndex != pos) {
				moveChild(pos, foundIndex);
			}
			return;
		}
		if (pos != -1) {
			getEChildren().add(pos, (LightElement) toAdd);
			return;
		}
		getEChildren().add((LightElement) toAdd);
	}

	/**
	 * @Generated NOT
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((reference == null) ? 0 : reference.hashCode());
		return result;
	}

	/**
	 * @Generated NOT
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LightElementImpl other = (LightElementImpl) obj;
		if (eAttributes == null) {
			if (other.eAttributes != null)
				return false;
		} else if (!getAttributes().equals(other.getAttributes()))
			return false;
		if (eChildren == null) {
			if (other.eChildren != null)
				return false;
		} else if (!getChildren().equals(other.getChildren()))
			return false;
		if (eIsRoot != other.eIsRoot)
			return false;
		if (eRodinElement == null) {
			if (other.eRodinElement != null)
				return false;
		} else if (!getElement().equals(other.getElement()))
			return false;
		if (eRoot == null) {
			if (other.eRoot != null)
				return false;
		} else if (!getRoot().equals(other.getRoot()))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!getReference().equals(other.getReference()))
			return false;
		return true;
	}

} //LightElementImpl
