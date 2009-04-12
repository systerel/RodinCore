/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - used IAttributeFactory
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractContextManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.SeesContextNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editing Sees
 *         clause.
 */
public class SeesSection extends
		AbstractContextsSection<IMachineRoot, ISeesContext> {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Seen Contexts";

	private static final String SECTION_DESCRIPTION = "Select the seen contexts of this machine";

	private final static AbstractContextManipulation<ISeesContext> factory = new SeesContextNameAttributeManipulation();

	public SeesSection(IEventBEditor<IMachineRoot> editor, FormToolkit toolkit,
			Composite parent) {
		super(editor, toolkit, parent);
	}

	@Override
	protected void addClause(String contextName) throws RodinDBException {
		History.getInstance().addOperation(
				OperationFactory.createElement(editor.getRodinInput(),
						ISeesContext.ELEMENT_TYPE,
						EventBAttributes.TARGET_ATTRIBUTE, contextName));
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
	protected ISeesContext getFreeElementContext() throws RodinDBException {
		final String childName = UIUtils.getFreeChildName(rodinRoot,
				ISeesContext.ELEMENT_TYPE);
		return rodinRoot.getSeesClause(childName);
	}

	@Override
	protected AbstractContextManipulation<ISeesContext> getManipulation() {
		return factory;
	}

}