/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixExtendsContextName;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editting Sees
 *         clause.
 */
public class ExtendsSection extends AbstractContextsSection<IContextFile> {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Abstract Contexts";

	private static final String SECTION_DESCRIPTION =
		"Select abstract contexts of this context";

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The context editor that contains this section
	 * @param parent
	 *            The composite parent
	 */
	public ExtendsSection(IEventBEditor<IContextFile> editor,
			FormToolkit toolkit, Composite parent) {

		super(editor, toolkit, parent);
	}
	
	@Override
	protected void addClause(String contextName) throws RodinDBException {
		final String name = UIUtils.getFreeElementName(editor,
				rodinFile, IExtendsContext.ELEMENT_TYPE,
				PrefixExtendsContextName.QUALIFIED_NAME,
				PrefixExtendsContextName.DEFAULT_PREFIX);
		final IExtendsContext clause = rodinFile.getExtendsClause(name);
		clause.create(null, null);
		clause.setAbstractContextName(contextName, null);
	}

	@Override
	protected IExtendsContext[] getClauses() {
		try {
			return rodinFile.getExtendsClauses();
		} catch (RodinDBException e) {
			UIUtils.log(e, null);
			return new IExtendsContext[0];
		}
	}

	@Override
	protected String getDescription() {
		return SECTION_DESCRIPTION;
	}

	@Override
	protected String getTitle() {
		return SECTION_TITLE;
	}

	@Override
	protected Set<String> getUsedContextNames() {
		Set<String> usedNames = new HashSet<String>();

		// First add myself
		usedNames.add(rodinFile.getBareName());

		// Then, all contexts already extended
		for (IExtendsContext clause : getClauses()) {
			try {
				usedNames.add(clause.getAbstractContextName());
			} catch (RodinDBException e) {
				UIUtils.log(e, "when reading the extends clause " + clause);
			}
		}
		return usedNames;
	}

}