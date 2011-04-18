/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author Thomas Muller
 */
public class AddChildContributionComputer extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		final IContributionItem[] contribs = new IContributionItem[1];

		Map<String, String> parms = new HashMap<String, String>();
		parms.put("childType", "org.eventb.core.event");
		
		final CommandContributionItemParameter param = new CommandContributionItemParameter(
				EventBUIPlugin.getActiveWorkbenchWindow(),
				null,
				"fr.systerel.editor.addChild", Collections.emptyMap(), null, null,
				null, "Add Event", null, null, CommandContributionItem.STYLE_PUSH,
				null, false);
		
		contribs[0] = new CommandContributionItem(param);
		return contribs;
	}

}
