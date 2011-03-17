/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.rodinp.core.emf.lightcore.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.ImplicitElement;
import org.rodinp.core.emf.lightcore.InternalElement;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LightcoreFactoryImpl extends EFactoryImpl implements LightcoreFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static LightcoreFactory init() {
		try {
			LightcoreFactory theLightcoreFactory = (LightcoreFactory)EPackage.Registry.INSTANCE.getEFactory("http://emf.core.rodinp.org/models/lightcore"); 
			if (theLightcoreFactory != null) {
				return theLightcoreFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new LightcoreFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightcoreFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case LightcorePackage.STRING_TO_ATTRIBUTE_MAP_ENTRY: return (EObject)createStringToAttributeMapEntry();
			case LightcorePackage.ATTRIBUTE: return createAttribute();
			case LightcorePackage.INTERNAL_ELEMENT: return createInternalElement();
			case LightcorePackage.IMPLICIT_ELEMENT: return createImplicitElement();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Attribute> createStringToAttributeMapEntry() {
		StringToAttributeMapEntryImpl stringToAttributeMapEntry = new StringToAttributeMapEntryImpl();
		return stringToAttributeMapEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Attribute createAttribute() {
		AttributeImpl attribute = new AttributeImpl();
		SynchroUtils.adaptForAttributeUpdate(attribute);
		return attribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InternalElement createInternalElement() {
		InternalElementImpl internalElement = new InternalElementImpl();
		return internalElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplicitElement createImplicitElement() {
		ImplicitElementImpl implicitElement = new ImplicitElementImpl();
		return implicitElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightcorePackage getLightcorePackage() {
		return (LightcorePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static LightcorePackage getPackage() {
		return LightcorePackage.eINSTANCE;
	}

} //LightcoreFactoryImpl
