/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantInfo extends State implements IMachineVariantInfo {

	@Override
	public String toString() {
		return varExpressions == null ? "null" : varExpressions.toString();
	}

	private final Expression[] varExpressions;
	
	private final ISCVariant[] variants;

	@Override
	public int count() {
		return varExpressions.length;
	}

	@Override
	public Expression getExpression(int index) {
		return varExpressions[index];
	}
	
	@Override
	public ISCVariant getVariant(int index) {
		return variants[index];
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public MachineVariantInfo(ISCVariant[] variants, ITypeEnvironment typeEnvironment) throws CoreException {
		this.variants = variants;
		varExpressions = new Expression[variants.length];
		for (int i = 0; i < variants.length; ++i) {
			varExpressions[i] = variants[i].getExpression(typeEnvironment);
		}
	}

	@Override
	public boolean machineHasVariant() {
		return varExpressions.length != 0;
	}

}
