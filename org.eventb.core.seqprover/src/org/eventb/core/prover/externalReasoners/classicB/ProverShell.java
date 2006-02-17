/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.prover.externalReasoners.classicB;

public class ProverShell {
	
	private static ProverShell shell = new ProverShell();
	
	public static ProverShell getDefault() {
		return shell;
	}
	
	private ProverShell() {
		// nothing to do
	}
	
	public String getCommandForPK() {
		return "/home/halstefa/bin/pk -s /home/halstefa/bin/PP_ST ";
	}

	public String getCommandForML() {
		return "/home/halstefa/bin/B4free-V2.0/krt -b /home/halstefa/bin/B4free-V2.0/ML.kin ";
	}

}
