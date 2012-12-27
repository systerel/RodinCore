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
package fr.systerel.editor.internal.handlers;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;

/**
 * Creates dynamically the contextual menu entries to add children of the
 * current element. It fills the children types that one can create some
 * elements for, at the current place.
 * 
 * @author "Thomas Muller"
 */
public class DynamicAddChildContributionItem extends
		DynamicAddElementContributionItem {

	private static final String ADDCHILD_COMMAND_ID = "fr.systerel.editor.addChild";
	private static final String ADDCHILD_COMMAND_KINDLABEL = "Child";
	
	@Override
	protected ChildCreationInfo getChildCreationInfo(RodinEditor editor, int offset) {
		return editor.getDocumentMapper().getChildCreationPossibility(offset);
	}
	
	@Override
	protected String getTargetCommand() {
		return ADDCHILD_COMMAND_ID;
	}
	@Override
	protected String getKindLabel() {
		return ADDCHILD_COMMAND_KINDLABEL;
	}

}
