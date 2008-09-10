package fr.systerel.explorer.poModel;

import org.eventb.core.IContextFile;

public class Context extends POContainer {
	public Context(IContextFile context){
		internalContext = context;
	}

	private IContextFile internalContext;
	
	
	public IContextFile getInternalContext() {
		return internalContext;
	}

}
