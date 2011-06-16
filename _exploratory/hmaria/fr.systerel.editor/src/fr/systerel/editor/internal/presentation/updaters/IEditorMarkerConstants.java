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
 * @author Nicolas Beauger
 * 
 */
public interface IEditorMarkerConstants {

	/**
	 * Formula based start marker attribute. {@link IMarker#CHAR_START} contains
	 * an editor based start index, this one is constant and is used to allow
	 * for marker updates upon model modifications.
	 */
	public static final String FORMULA_CHAR_START = "formulaCharStart";

	/**
	 * Formula based end marker attribute. {@link IMarker#CHAR_START} contains
	 * an editor based end index, this one is constant and is used to allow for
	 * marker updates upon model modifications.
	 */
	public static final String FORMULA_CHAR_END = "formulaCharEnd";

}
