/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import org.eclipse.core.resources.IMarker;

/**
 * Attributes used to store information into problem markers after Rodin Editor
 * has been used.
 * 
 * @author Nicolas Beauger
 */
public interface IEditorMarkerConstants {

	/**
	 * Tells if the marker is formula based, which means that the marker keeps
	 * informations about the position of the error within a formula. This
	 * attribute is false in case of a marker that concerns the whole interval
	 * of an attribute.
	 */
	public static final String FORMULA_BASED = "formulaBased";

	/**
	 * Formula based start marker attribute. Actually,
	 * {@link IMarker#CHAR_START} is updated to contain an editor based start
	 * index. Thus, this current attribute is a backup constant which describes
	 * the char start of the marker in the formula and is used to allow marker
	 * updates upon model modifications.
	 */
	public static final String FORMULA_CHAR_START = "formulaCharStart";

	/**
	 * Formula based start marker attribute. Actually, {@link IMarker#CHAR_END}
	 * is updated to contain an editor based start index. Thus, this current
	 * attribute is a backup constant which describes the char start of the
	 * marker in the formula and is used to allow marker updates upon model
	 * modifications.
	 */
	public static final String FORMULA_CHAR_END = "formulaCharEnd";

}
