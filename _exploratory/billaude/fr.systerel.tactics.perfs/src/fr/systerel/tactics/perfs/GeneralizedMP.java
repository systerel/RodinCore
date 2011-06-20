/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.tactics.perfs;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.GeneralizedModusPonens;
import org.rodinp.core.RodinDBException;

/**
 * Testing performance of GeneralizedModusPonens.
 * <p>
 * As tests are running in JUnit3, to ingore them re-write them from
 * <i>test_XXX</i> to <i>ignore_XXX</i> or just comment them out.
 * </p>
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedMP extends PerfsTest {

	@Override
	public void setUp() throws RodinDBException, Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	public String getMRTName() {
		return "Generalized Modus Ponens";
	}

	@Override
	public void execute(IProverSequent sequent) {
		new GeneralizedModusPonens().apply(sequent, new EmptyInput(), null);
	}

	@Override
	public ITactic getTactic() {
		return BasicTactics.reasonerTac(new GeneralizedModusPonens(),
				new EmptyInput());
	}

	@Override
	protected int getFailureNumber() {
		return 50;
	}

	@Override
	protected int getSuccessNumber() {
		return -1;
	}
	
	@Override
	protected int getNbPerfTests() {
		return 15;
	}

	public void ignore_NnfGP_HeavyProject_Term() throws Exception {
		test_HeavyProject("CrCtl_Terminator_0.5.0");
	}

	public void ignore_NnfGP_HeavyProject_XCE() throws Exception {
		test_HeavyProject("XCoreEncoding");
	}

	public void ignore_NnfGP_HeavyProject_XC() throws Exception {
		test_HeavyProject("XCore");
	}

	public void ignore_NnfGP_HeavyProject_R2D2() throws Exception {
		test_HeavyProject("DC-C479_MOD-m8");
	}

	public void test_NnfGP_SoftProject_list() throws Exception {
		test_SoftProjects(10, "BirthdayBook", "Celebrity", "ch2_car",
				"Closure - Sans PostTactics", "Doors", "Galois");
	}
	
	public void ignore_NnfGP_RootProject_Term() throws Exception {
		test_rootProject("CrCtl_Terminator_0.5.0", 500);
	}
	
	public void ignore_NnfGP_RootProject_XCE() throws Exception {
		test_rootProject("XCoreEncoding", 500);
	}
	
	public void ignore_NnfGP_RootProject_XC() throws Exception {
		test_rootProject("XCore", 500);
	}
	
	public void ignore_NnfGP_RootProject_R2D2() throws Exception {
		test_rootProject("DC-C479_MOD-m8", 500);
	}

}
