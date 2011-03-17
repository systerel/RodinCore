/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package generated_todelete;

import org.rodinp.core.emf.lightcore.Attribute;
import org.rodinp.core.emf.lightcore.LightcoreFactory;

import junit.textui.TestRunner;


/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class AttributeTest extends LightObjectTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(AttributeTest.class);
	}

	/**
	 * Constructs a new Attribute test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttributeTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Attribute test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected Attribute getFixture() {
		return (Attribute)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(LightcoreFactory.eINSTANCE.createAttribute());
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

} //AttributeTest
