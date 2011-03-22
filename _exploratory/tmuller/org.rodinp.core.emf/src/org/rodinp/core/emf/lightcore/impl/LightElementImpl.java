/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
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
import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Light Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#isRoot <em>Root</em>}</li>
 *   <li>{@link org.rodinp.core.emf.lightcore.impl.LightElementImpl#getRodinElement <em>Rodin Element</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class LightElementImpl extends LightObjectImpl implements LightElement {
	/**
	 * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Attribute> attributes;

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
	 * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<LightElement> children;

	/**
	 * The default value of the '{@link #isRoot() <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRoot()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ROOT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRoot() <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRoot()
	 * @generated
	 * @ordered
	 */
	protected boolean root = ROOT_EDEFAULT;

	/**
	 * The default value of the '{@link #getRodinElement() <em>Rodin Element</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRodinElement()
	 * @generated
	 * @ordered
	 */
	protected static final Object RODIN_ELEMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRodinElement() <em>Rodin Element</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRodinElement()
	 * @generated
	 * @ordered
	 */
	protected Object rodinElement = RODIN_ELEMENT_EDEFAULT;

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
	public EMap<String, Attribute> getAttributes() {
		if (attributes == null) {
			attributes = new EcoreEMap<String,Attribute>(LightcorePackage.Literals.STRING_TO_ATTRIBUTE_MAP_ENTRY, StringToAttributeMapEntryImpl.class, this, LightcorePackage.LIGHT_ELEMENT__ATTRIBUTES);
		}
		return attributes;
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
	public EList<LightElement> getChildren() {
		if (children == null) {
			children = new EObjectContainmentWithInverseEList<LightElement>(LightElement.class, this, LightcorePackage.LIGHT_ELEMENT__CHILDREN, LightcorePackage.LIGHT_ELEMENT__PARENT);
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightElement getParent() {
		if (eContainerFeatureID() != LightcorePackage.LIGHT_ELEMENT__PARENT) return null;
		return (LightElement)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParent(LightElement newParent, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParent, LightcorePackage.LIGHT_ELEMENT__PARENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParent(LightElement newParent) {
		if (newParent != eInternalContainer() || (eContainerFeatureID() != LightcorePackage.LIGHT_ELEMENT__PARENT && newParent != null)) {
			if (EcoreUtil.isAncestor(this, newParent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParent != null)
				msgs = ((InternalEObject)newParent).eInverseAdd(this, LightcorePackage.LIGHT_ELEMENT__CHILDREN, LightElement.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__PARENT, newParent, newParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRoot() {
		return root;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRoot(boolean newRoot) {
		boolean oldRoot = root;
		root = newRoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__ROOT, oldRoot, root));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getRodinElement() {
		return rodinElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRodinElement(Object newRodinElement) {
		Object oldRodinElement = rodinElement;
		rodinElement = newRodinElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LightcorePackage.LIGHT_ELEMENT__RODIN_ELEMENT, oldRodinElement, rodinElement));
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<LightElement> getElementsOfType(Object type) {
		final EList<LightElement> list = new BasicEList<LightElement>();
			if (!(type instanceof IInternalElement)) {
				return list;	
			}
			final EList<LightElement> children = getChildren();
			for (LightElement child : children) {
				if (((IInternalElement) child.getRodinElement()).getElementType() == type) {
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
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LightcorePackage.LIGHT_ELEMENT__CHILDREN:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParent((LightElement)otherEnd, msgs);
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
			case LightcorePackage.LIGHT_ELEMENT__ATTRIBUTES:
				return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
			case LightcorePackage.LIGHT_ELEMENT__CHILDREN:
				return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				return basicSetParent(null, msgs);
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
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				return eInternalContainer().eInverseRemove(this, LightcorePackage.LIGHT_ELEMENT__CHILDREN, LightElement.class, msgs);
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
			case LightcorePackage.LIGHT_ELEMENT__ATTRIBUTES:
				if (coreType) return getAttributes();
				else return getAttributes().map();
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				return getReference();
			case LightcorePackage.LIGHT_ELEMENT__CHILDREN:
				return getChildren();
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				return getParent();
			case LightcorePackage.LIGHT_ELEMENT__ROOT:
				return isRoot();
			case LightcorePackage.LIGHT_ELEMENT__RODIN_ELEMENT:
				return getRodinElement();
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
			case LightcorePackage.LIGHT_ELEMENT__ATTRIBUTES:
				((EStructuralFeature.Setting)getAttributes()).set(newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				setReference((String)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__CHILDREN:
				getChildren().clear();
				getChildren().addAll((Collection<? extends LightElement>)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				setParent((LightElement)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__ROOT:
				setRoot((Boolean)newValue);
				return;
			case LightcorePackage.LIGHT_ELEMENT__RODIN_ELEMENT:
				setRodinElement(newValue);
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
			case LightcorePackage.LIGHT_ELEMENT__ATTRIBUTES:
				getAttributes().clear();
				return;
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				setReference(REFERENCE_EDEFAULT);
				return;
			case LightcorePackage.LIGHT_ELEMENT__CHILDREN:
				getChildren().clear();
				return;
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				setParent((LightElement)null);
				return;
			case LightcorePackage.LIGHT_ELEMENT__ROOT:
				setRoot(ROOT_EDEFAULT);
				return;
			case LightcorePackage.LIGHT_ELEMENT__RODIN_ELEMENT:
				setRodinElement(RODIN_ELEMENT_EDEFAULT);
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
			case LightcorePackage.LIGHT_ELEMENT__ATTRIBUTES:
				return attributes != null && !attributes.isEmpty();
			case LightcorePackage.LIGHT_ELEMENT__REFERENCE:
				return REFERENCE_EDEFAULT == null ? reference != null : !REFERENCE_EDEFAULT.equals(reference);
			case LightcorePackage.LIGHT_ELEMENT__CHILDREN:
				return children != null && !children.isEmpty();
			case LightcorePackage.LIGHT_ELEMENT__PARENT:
				return getParent() != null;
			case LightcorePackage.LIGHT_ELEMENT__ROOT:
				return root != ROOT_EDEFAULT;
			case LightcorePackage.LIGHT_ELEMENT__RODIN_ELEMENT:
				return RODIN_ELEMENT_EDEFAULT == null ? rodinElement != null : !RODIN_ELEMENT_EDEFAULT.equals(rodinElement);
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
		result.append(", root: ");
		result.append(root);
		result.append(", rodinElement: ");
		result.append(rodinElement);
		result.append(')');
		return result.toString();
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void load() {
		if (rodinElement instanceof IInternalElement) {
			final IInternalElement iElement = (IInternalElement) rodinElement;
			this.setRoot(iElement.isRoot());
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
		if (rodinElement instanceof IInternalElement) {
			//
		}
	}

} //LightElementImpl
