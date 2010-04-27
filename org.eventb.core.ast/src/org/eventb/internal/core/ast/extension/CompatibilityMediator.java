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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.internal.core.parser.OperatorRegistry;

/**
 * @author Nicolas Beauger
 *
 */
public class CompatibilityMediator implements ICompatibilityMediator {

	final OperatorRegistry registry;
	
	public CompatibilityMediator(OperatorRegistry registry) {
		this.registry = registry;
	}

	public void addCompatibility(String leftOpId, String rightOpId) {
		registry.addCompatibility(leftOpId, rightOpId);
	}

	public void addGroupCompatibility(String leftGroupId, String rightGroupId) {
		registry.addGroupCompatibility(leftGroupId, rightGroupId);
	}

}
