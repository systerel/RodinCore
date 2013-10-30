/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractContextManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendsContextAbstractContextNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editting Sees
 *         clause.
 */
public class ExtendsSection extends
		AbstractContextsSection<IContextRoot, IExtendsContext> {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Abstract Contexts";

	private static final String SECTION_DESCRIPTION = "Select abstract contexts of this context";

	private final static AbstractContextManipulation<IExtendsContext> manipulation = new ExtendsContextAbstractContextNameAttributeManipulation();

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The context editor that contains this section
	 * @param parent
	 *            The composite parent
	 */
	public ExtendsSection(IEventBEditor<IContextRoot> editor,
			FormToolkit toolkit, Composite parent) {

		super(editor, toolkit, parent);
	}

	@Override
	protected void addClause(String contextName) throws RodinDBException {
		History.getInstance().addOperation(
				OperationFactory.createElement(editor.getRodinInput(),
						IExtendsContext.ELEMENT_TYPE,
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
	protected IExtendsContext getFreeElementContext() throws RodinDBException {
		final String childName = EventBUtils.getFreeChildName(rodinRoot,
				IExtendsContext.ELEMENT_TYPE, "i");
		return rodinRoot.getExtendsClause(childName);
	}

	@Override
	protected AbstractContextManipulation<IExtendsContext> getManipulation() {
		return manipulation;
	}
}