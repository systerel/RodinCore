/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package generated_todelete;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>org.rodinp.core.emf.lightcore</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class LightcoreTests extends TestSuite {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Test suite() {
		TestSuite suite = new LightcoreTests("org.rodinp.core.emf.lightcore Tests");
		suite.addTestSuite(AttributeTest.class);
		suite.addTestSuite(InternalElementTest.class);
		suite.addTestSuite(ImplicitElementTest.class);
		return suite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightcoreTests(String name) {
		super(name);
	}

} //LightcoreTests
