/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.core.upgrade;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;

/**
 * @author Nicolas Beauger
 * 
 */
public class UpgraderFactory {

	/**
	 * Returns the version upgrader that manages the upgrade to the given target
	 * version from the previous one.
	 * 
	 * @param targetVersion
	 *            the version of the language that the upgrader will upgrade to
	 * @param ff
	 *            a FormulaFactory
	 * @return a version upgrader for the target version
	 */
	public static VersionUpgrader getUpgrader(LanguageVersion targetVersion,
			FormulaFactory ff) {
		switch (targetVersion) {
		case V2:
			return new VersionUpgraderV1V2(ff);
		default:
			throw new IllegalArgumentException(
					"Cannot get an upgrader for version " + targetVersion);
		}
	}

}
