/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class Conversion extends Operation {
	
	private final String path;
	
	private final RenameAttribute[] renameAttr;
	private final AddAttribute[] addAttr;
	private final RenameElement renameElem;
	
	public Conversion(IConfigurationElement configElement, SimpleConversionSheet sheet) {
		super(configElement, sheet);
		path = configElement.getAttribute("elementPath");
		verifyPath(path);
		String elemId = getIdFromPath(path);
		
		IConfigurationElement[] confElements = configElement.getChildren();
		final List<String> ids = new ArrayList<String>(confElements.length);
		final List<String> newIds = new ArrayList<String>(confElements.length);
		final List<RenameAttribute> rAttr = new ArrayList<RenameAttribute>(confElements.length);
		final List<AddAttribute> aAttr = new ArrayList<AddAttribute>(confElements.length);
		RenameElement rElem = null;
		
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
	}

	private void verifyPath(String p) {
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
		if (f == -1)
			f = p.length();
		String root = p.substring(1, f);
		if (!root.equals(getSheet().getType().getId())) {
			throw new IllegalStateException(
					"element path must begin with file element type " + 
					getSheet().getType());
		}
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
	
	private static String T1 = 
		"\t\t<" + XSLConstants.XSL_APPLY_TEMPLATES + " " + XSLConstants.XSL_SELECT +
		"=\"" + XSLConstants.XSL_ALL + "\"";
	private static String T2 = ">\n";
	private static String T3 = "\t\t</" + XSLConstants.XSL_APPLY_TEMPLATES + ">\n";
	private static String T4 = "/>\n";
	
	public void addTemplates(StringBuffer document) {
		for (RenameAttribute ra : renameAttr)
			ra.addTemplate(document, path);
		
		if (renameElem != null) {
			
			renameElem.beginTemplate(document, path);
			
			for (AddAttribute aa : addAttr) {
				aa.addAttribute(document);
			}
			
			document.append(T1);
			
			if (getSheet().hasSorter()) {
				document.append(T2);
				
				getSheet().addSorter(document);
				
				document.append(T3);
			} else {
				document.append(T4);
			}
			
			renameElem.endTemplate(document);
		}
	}

	public String getPath() {
		return path;
	}
	
}
