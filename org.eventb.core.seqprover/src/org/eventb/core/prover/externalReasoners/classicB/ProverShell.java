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
		// Path for fmehta
		// return "/home/fmehta/Tools/pk/pk -s /home/fmehta/Tools/pk/PP_ST ";
		
		return "/home/fmehta/Tools/pk/pk -s /home/fmehta/Tools/pk/PP_ST ";
	}

	public String getCommandForML() {
		// Path for fmehta
		// return "/home/fmehta/Tools/B4free-V2.0/krt -b /home/fmehta/Tools/B4free-V2.0/ML.kin ";
		
		return "/home/fmehta/Tools/B4free-V2.0/krt -b /home/fmehta/Tools/B4free-V2.0/ML.kin ";
	}

	public String getCommandForPP() {
		// Path for fmehta
		// return "/home/fmehta/Tools/B4free-V2.0/krt -b /home/fmehta/Tools/B4free-V2.0/ML.kin ";
		
		return "/home/fmehta/Tools/B4free-V2.0/krt -b /home/fmehta/Tools/B4free-V2.0/PP.kin ";
	}

}
