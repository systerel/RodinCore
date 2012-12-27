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

import static org.eclipse.ui.menus.CommandContributionItem.STYLE_PUSH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * Abstract class to populate the context menu with entries to create elements
 * from the current place in the editor.
 * 
 * @author "Thomas Muller"
 */
public abstract class DynamicAddElementContributionItem extends
		CompoundContributionItem implements IWorkbenchContribution {

	private static final IContributionItem[] NO_ITEM = new IContributionItem[0];

	private static final String typeID = "typeID";

	private IServiceLocator serviceLoc;

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
		final ChildCreationInfo possibility = getChildCreationInfo(editor,
				editor.getCurrentOffset());
		if (possibility == null) {
			return NO_ITEM;
		}
		final List<CommandContributionItem> menu = new ArrayList<CommandContributionItem>(
				possibility.getPossibleChildTypes().size());
		for (IInternalElementType<?> t : possibility.getPossibleChildTypes()) {
			final CommandContributionItemParameter param = new CommandContributionItemParameter(
					serviceLoc, null, getTargetCommand(), STYLE_PUSH);
			param.label = getCommandLabel(t, getKindLabel());
			param.parameters = Collections.singletonMap(typeID, t.getId());
			menu.add(new CommandContributionItem(param));
		}
		return menu.toArray(new CommandContributionItem[menu.size()]);
	}

	protected abstract ChildCreationInfo getChildCreationInfo(
			RodinEditor editor, int offset);

	protected abstract String getTargetCommand();

	protected abstract String getKindLabel();

	private String getCommandLabel(IInternalElementType<?> t, String kindLabel) {
		final StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(kindLabel);
		b.append("]-> Add ");
		b.append(t.getName().replaceFirst("Event-B", ""));
		return b.toString();
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
		this.serviceLoc = serviceLocator;
	}

}
