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
package fr.systerel.editor.internal.handlers;

import static org.eclipse.ui.menus.CommandContributionItem.STYLE_PUSH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.context.ChildCreationInfo;

/**
 * Creates dynamically the submenu for the "Add child" contextual menu. It fills
 * the children types that one can create some elements for, at the current
 * place.
 * 
 * @author "Thomas Muller"
 */
public class DynamicAddChildContributionItem extends CompoundContributionItem
		implements IWorkbenchContribution {

	private static final IContributionItem[] NO_ITEM = new IContributionItem[0];

	private static final String ADDCHILD_COMMAND_ID = "fr.systerel.editor.addChild";
	private static final String typeID = "typeID";

	private IServiceLocator serviceLoc;

	public DynamicAddChildContributionItem() {
		super();
	}

	public DynamicAddChildContributionItem(String id) {
		super(id);
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final IEditorPart activeEditor = EditorPlugin.getActivePage()
				.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return NO_ITEM;
		}
		final RodinEditor editor = (RodinEditor) activeEditor;
		final int offset = editor.getCurrentOffset();
		final ChildCreationInfo possibility = editor.getDocumentMapper()
				.getChildCreationPossibility(offset);
		if (possibility == null) {
			return NO_ITEM;
		}
		final Set<IInternalElementType<?>> pChildTypes = possibility
				.getPossibleChildTypes();
		final List<CommandContributionItem> childCreationSubMenu = new ArrayList<CommandContributionItem>(
				pChildTypes.size());
		for (IInternalElementType<?> t : pChildTypes) {
			final CommandContributionItemParameter param = new CommandContributionItemParameter(
					serviceLoc, null, ADDCHILD_COMMAND_ID, STYLE_PUSH);
			param.label = t.getName();
			param.parameters = Collections.singletonMap(typeID, t.getId());
			childCreationSubMenu.add(new CommandContributionItem(param));
		}
		return childCreationSubMenu
				.toArray(new CommandContributionItem[childCreationSubMenu
						.size()]);
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
		this.serviceLoc = serviceLocator;
	}

}
