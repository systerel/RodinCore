/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers.refactoring;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;

/**
 * {@link AbstractRenameElementHandler} subclasses
 * 
 * @author Thomas Muller
 */
public class AutoLabelRenamings {
	
	public static class AutoActionLabelRenaming extends AbstractRenameElementHandler {
		
		public AutoActionLabelRenaming() {
			type = IAction.ELEMENT_TYPE;
		}
	
	}
	
	public static class AutoAxiomLabelRenaming extends AbstractRenameElementHandler {

		public AutoAxiomLabelRenaming() {
			type = IAxiom.ELEMENT_TYPE;
		}

	}
	
	public static class AutoGuardLabelRenaming extends AbstractRenameElementHandler {

		public AutoGuardLabelRenaming() {
			type = IGuard.ELEMENT_TYPE;
		}

	}
	
	public static class AutoInvariantLabelRenaming extends AbstractRenameElementHandler {

		public AutoInvariantLabelRenaming() {
			type = IInvariant.ELEMENT_TYPE;
		}
		
	}

}
