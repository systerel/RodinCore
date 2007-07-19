package org.eventb.internal.core.pm;

import org.eventb.core.pm.IPostTacticContainer;
import org.eventb.core.pm.IPostTacticContainerRegistry;

public class PostTacticContainer extends TacticContainer implements
		IPostTacticContainer {

	public PostTacticContainer(IPostTacticContainerRegistry registry) {
		super(registry);
	}

}
