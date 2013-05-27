/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.internal.ui.eventbeditor.imageprovider.DefaultImageProvider;
import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.eventb.ui.prettyprint.DefaultPrettyPrinter;
import org.eventb.ui.prettyprint.IElementPrettyPrinter;

/**
 * Dummy implementation for describing unknown elements.
 */
public class NullElementDesc extends ElementDesc {

	private static final String PREFIX = "";
	private static final String CHILDREN_SUFFIX = "";
	private static final IImageProvider IMG_PROVIDER = new DefaultImageProvider(
			null);
	private static final AttributeDesc[] NO_ATTRIBUTES = {};
	private static final IElementRelationship[] NO_CHILD_RELATIONSHIPS = {};
	private static final String AUTO_NAME_PREFIX = "";
	private static final AttributeDesc AUTO_NAME_ATTRIBUTE = new NullAttributeDesc();
	private static final int NO_DEFAULT_COLUMN = -1;
	private static final IElementPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

	public NullElementDesc() {
		super(PREFIX, CHILDREN_SUFFIX, IMG_PROVIDER, NO_ATTRIBUTES,
				NO_ATTRIBUTES, NO_CHILD_RELATIONSHIPS, AUTO_NAME_PREFIX,
				AUTO_NAME_ATTRIBUTE, NO_DEFAULT_COLUMN, PRETTY_PRINTER);
	}

	@Override
	public boolean isValid() {
		return false;
	}

}
