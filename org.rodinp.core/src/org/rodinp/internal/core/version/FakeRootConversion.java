/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class FakeRootConversion extends Conversion {

	private final String path;
	
	private final static RenameAttribute[] renameAttr = new RenameAttribute[0];
	private final AddAttribute[] addAttr;
	private final RenameElement renameElem;


	public FakeRootConversion(IFileElementType<IRodinFile> type,
			SimpleConversionSheet sheet) {
		super(null, sheet);
		
		path = "/" + type.getId();
		
		addAttr = new AddAttribute[1];
		String version = String.valueOf(sheet.getVersion());
		addAttr[0] = new AddAttribute("version", version, sheet);
		
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
		// TODO Auto-generated method stub
		return false;
	}

}
