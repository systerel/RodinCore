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
import org.eventb.core.IMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editing Sees
 *         clause.
 */
public class SeesSection extends AbstractContextsSection<IMachineFile> {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Seen Contexts";

	private static final String SECTION_DESCRIPTION =
		"Select the seen contexts of this machine";

	public SeesSection(IEventBEditor<IMachineFile> editor, FormToolkit toolkit,
			Composite parent) {
		super(editor, toolkit, parent);
	}

	@Override
	protected void addClause(String contextName) throws RodinDBException {
		final String name = UIUtils.getFreeElementName(editor, rodinFile,
				ISeesContext.ELEMENT_TYPE,
				"seesContext");
		ISeesContext clause = rodinFile.getSeesClause(name);
		clause.create(null, null);
		clause.setSeenContextName(contextName, null);
	}

	@Override
	protected ISeesContext[] getClauses() {
		try {
			return rodinFile.getSeesClauses();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when reading the sees clauses");
			return new ISeesContext[0];
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

		// Add all contexts already seen
		for (ISeesContext clause : getClauses()) {
			try {
				usedNames.add(clause.getSeenContextName());
			} catch (RodinDBException e) {
				UIUtils.log(e, "when reading the sees clause " + clause);
			}
		}
		return usedNames;
	}

}