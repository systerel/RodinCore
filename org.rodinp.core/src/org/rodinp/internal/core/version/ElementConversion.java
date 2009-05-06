/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel   - added ChangeName
 *******************************************************************************/
package org.rodinp.internal.core.version;

import static org.rodinp.internal.core.Buffer.VERSION_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ElementConversion extends Conversion {
	
	private final String path;
	private final boolean root;
	
	private final RenameAttribute[] renameAttr;
	private final AddAttribute[] addAttr;
	private final RenameElement renameElem;
	private final ChangeName changeName;
	
	public ElementConversion(IConfigurationElement configElement, SimpleConversionSheet sheet) {
		super(configElement, sheet);
		path = configElement.getAttribute("elementPath");
		root = verifyPath(path);
		String elemId = getIdFromPath(path);
		
		IConfigurationElement[] confElements = configElement.getChildren();
		final List<String> ids = new ArrayList<String>(confElements.length);
		final List<String> newIds = new ArrayList<String>(confElements.length);
		final List<RenameAttribute> rAttr = new ArrayList<RenameAttribute>(confElements.length);
		final List<AddAttribute> aAttr = new ArrayList<AddAttribute>(confElements.length);
		RenameElement rElem = null;
		ChangeName cName = null;
		
		if (root) {
		
			String version = String.valueOf(sheet.getVersion());
			aAttr.add(new AddAttribute(VERSION_ATTRIBUTE, version, sheet));
			
		}
		
		for (IConfigurationElement confElement : confElements) {
			String op = confElement.getName();
			if (op.equals("renameElement")) {
				if (rElem == null) {
					rElem = new RenameElement(confElement, elemId, sheet);
				} else {
					throw new IllegalStateException("Multiple rename element on path: " + path);
				}
			} else if (op.equals("renameAttribute")) {
				RenameAttribute ra = new RenameAttribute(confElement, sheet);
				rAttr.add(ra);
				String id = ra.getId();
				String newId = ra.getNewId();
				checkId(ids, id);
				checkId(newIds, newId);
			} else if (op.equals("addAttribute")) {
				AddAttribute aa = new AddAttribute(confElement, sheet);
				aAttr.add(aa);
				String newId = aa.getNewId();
				checkId(newIds, newId);
			} else if (op.equals("changeName")) {
				if (cName == null) {
					cName  = new ChangeName(confElement, sheet);
				} else {
					throw new IllegalStateException("Multiple change name on path: " + path);
				}
			} else {
				throw new IllegalStateException("Unknown operation: " + op);
			}
		}
		
		addAttr = aAttr.toArray(new AddAttribute[aAttr.size()]);
		renameAttr = rAttr.toArray(new RenameAttribute[rAttr.size()]);
		if (addAttr.length > 0 && rElem == null) {
			// use fake renaming to simply template generation
			renameElem = new RenameElement(configElement, elemId, elemId, sheet);
		} else {
			renameElem = rElem;
		}
		changeName = cName;
	}

	private boolean verifyPath(String p) {
		char[] ca = p.toCharArray();
		int ll = ca.length - 1;
		boolean x = true;
		int f = -1;
		for (int i=0; i<=ll; i++) {
			char c = ca[i];
			if (c == '/') {
				if (x && i<ll) {
					if (i>0 && f == -1) {
						f = i;
					}
					x = false;
					continue;
				}
			} else if (c != '@' && i>0) {
				x = true;
				continue;
			}
			throw new IllegalStateException("invalid element path");
		}
		String rootName = p.substring(1, f == -1 ? p.length() : f);
		if (!rootName.equals(getSheet().getType().getId())) {
			throw new IllegalStateException(
					"element path must begin with root element type " + 
					getSheet().getType());
		}
		return f == -1;
	}

	private String getIdFromPath(String somePath) {
		String id;
		int pos = somePath.lastIndexOf('/');
		if (pos == -1) {
			id = somePath;
		} else {
			id = somePath.substring(pos+1, somePath.length());
		}
		return id;
	}

	private void checkId(final List<String> ids, String id) {
		if (ids.contains(id)) {
			throw new IllegalStateException(
					"Multiple operations on same attribute: " + id);
		} else {
			ids.add(id);
		}
	}
	
	@Override
	public String getPath() {
		return path;
	}
	
	@Override
	public boolean isRoot() {
		return root;
	}

	@Override
	protected AddAttribute[] getAddAttributes() {
		return addAttr;
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
	protected ChangeName getChangeName() {
		return changeName;
	}
	
}
