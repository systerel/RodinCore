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
 * A test suite for the '<em><b>LightCore</b></em>' model.
 * <!-- end-user-doc -->
 * @generated
 */
public class LightCoreAllTests extends TestSuite {

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
		TestSuite suite = new LightCoreAllTests("LightCore Tests");
		suite.addTest(LightcoreTests.suite());
		return suite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LightCoreAllTests(String name) {
		super(name);
	}

} //LightCoreAllTests
