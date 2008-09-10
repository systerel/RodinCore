package fr.systerel.explorer.poModel;

import org.eventb.core.ITheorem;

public class Theorem extends POContainer {
	public Theorem(ITheorem theorem){
		internalTheorem = theorem;
	}

	private ITheorem internalTheorem;
	
	
	public ITheorem getInternalTheorem() {
		return internalTheorem;
	}
	
}
