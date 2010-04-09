/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prettyprint;


/**
 * Enumerations defining horizontal and vertical alignments for pretty printing
 * <br>
 * Those enums are intended to be used in :
 * <br>
 * {@link PrettyPrintUtils#getHTMLBeginForCSSClass(String, HorizontalAlignment, VerticalAlignement)} and 
 * {@link PrettyPrintUtils#getHTMLEndForCSSClass(String, HorizontalAlignment, VerticalAlignement)}.
 * 
 * @author Thomas Muller
 * @since 1.2
 * 
 */
public class PrettyPrintAlignments {

	/**
	 * Enumeration for horizontal alignment of text in the pretty printer. It
	 * exists 4 different types of horizontal alignments : <code>LEFT</code>,
	 * <code>RIGHT</code>, <code>CENTER</code>, <code>MIDDLE</code>.
	 */
	public static enum HorizontalAlignment {
		LEFT("left"), // 
		RIGHT("right"), //
		CENTER("center"), //
		JUSTIFY("justify");

		private final String align;

		// Constructor
		HorizontalAlignment(String align) {
			this.align = align;
		}

		public String getAlign() {
			return align;
		}
	}

	/**
	 * Enumeration for vertical alignment of text in the pretty printer It
	 * exists 4 different types of vertical alignments : <code>TOP</code>,
	 * <code>MIDDLE</code>, <code>BOTTOM</code>, <code>BASELINE</code>.
	 */
	public static enum VerticalAlignement {
		TOP("top"), //
		MIDDLE("middle"), //
		BOTTOM("bottom"), //
		BASELINE("baseline");

		public final String align;

		// Constructor
		VerticalAlignement(String align) {
			this.align = align;
		}

		public String getAlign() {
			return align;
		}
	}

}
