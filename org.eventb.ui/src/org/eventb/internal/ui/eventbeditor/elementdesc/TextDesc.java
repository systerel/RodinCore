/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;

public class TextDesc extends AttributeDesc {

	public enum Style {
		SINGLE, MULTI
	}

	public TextDesc(IAttributeManipulation factory, String prefix,
			String suffix, boolean isHorizontalExpand, boolean isMath,
			Style style, IAttributeType attrType, String preference) {
		super(factory, prefix, suffix, isHorizontalExpand, attrType);
		this.isMath = isMath;
		this.style = style;
		this.preference = preference;
	}

	private final boolean isMath;

	private final Style style;

	private final String preference;
	
	@Override
	public IEditComposite createWidget() {
		return new TextEditComposite(this);
	}

	/**
	 * Returns the preference key for the choice of font color. Must be defined
	 * in PreferenceConstants.
	 * 
	 * @return the preference key for the choice of font color.
	 */
	public String getForegroundColor() {
		return preference;
	}
	
	/**
	 * This indicates that the text widget will need math keyboard attached to
	 * it or not.
	 */

	public boolean isMath() {
		return isMath;
	}

	/**
	 * Either single or multi lines.
	 */
	public Style getStyle() {
		return style;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + (isMath ? "math" : "not math") + ", "
				+ style;
	}
}
