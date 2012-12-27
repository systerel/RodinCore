/*******************************************************************************
 * Copyright (c) 2008, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added ChangeName
 *******************************************************************************/
package org.rodinp.internal.core.version;

import static org.rodinp.internal.core.Buffer.VERSION_ATTRIBUTE;

import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 *
 */
public class FakeRootConversion extends Conversion {

	private final String path;
	
	private final static RenameAttribute[] renameAttr = new RenameAttribute[0];
	private final AddAttribute[] addAttr;
	private final RenameElement renameElem;


	public FakeRootConversion(IInternalElementType<?> type,
			SimpleConversionSheet sheet) {
		super(null, sheet);
		
		path = "/" + type.getId();
		
		addAttr = new AddAttribute[1];
		String version = String.valueOf(sheet.getVersion());
		addAttr[0] = new AddAttribute(VERSION_ATTRIBUTE, version, sheet);
		
		renameElem = new RenameElement(type.getId(), type.getId(), sheet);
		
	}

	@Override
	protected AddAttribute[] getAddAttributes() {
		return addAttr;
	}


	@Override
	public String getPath() {
		return path;
	}


	@Override
	protected RenameAttribute[] getRenameAttributes() {
		return renameAttr;
	}


	@Override
	protected RenameElement getRenameElement() {
		return renameElem;
	}


	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	protected ChangeName getChangeName() {
		return null;
	}

}
