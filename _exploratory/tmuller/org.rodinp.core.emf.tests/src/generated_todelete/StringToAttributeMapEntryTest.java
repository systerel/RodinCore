/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package generated_todelete;

import java.util.Map;

import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightcoreFactory;
import org.rodinp.core.emf.lightcore.LightcorePackage;

import junit.framework.TestCase;

import junit.textui.TestRunner;


/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>String To Attribute Map Entry</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class StringToAttributeMapEntryTest extends TestCase {

	/**
	 * The fixture for this String To Attribute Map Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Map.Entry<String, Attribute> fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(StringToAttributeMapEntryTest.class);
	}

	/**
	 * Constructs a new String To Attribute Map Entry test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StringToAttributeMapEntryTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this String To Attribute Map Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Map.Entry<String, Attribute> fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this String To Attribute Map Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Map.Entry<String, Attribute> getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		setFixture((Map.Entry<String, Attribute>)LightcoreFactory.eINSTANCE.create(LightcorePackage.Literals.STRING_TO_ATTRIBUTE_MAP_ENTRY));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //StringToAttributeMapEntryTest
