/*******************************************************************************
 * Copyright (c) 2006, 2020 ETH Zurich and others.
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

import static org.eventb.core.IVariant.DEFAULT_LABEL;

import java.util.StringJoiner;

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

	private final String[] varLabels;

	private final Expression[] varExpressions;
	
	private final ISCVariant[] variants;

	@Override
	public int count() {
		return varExpressions.length;
	}

	@Override
	public String getLabel(int index) {
		return varLabels[index];
	}
	
	@Override
	@Deprecated
	public Expression getExpression() {
		if (hasSingleDefaultVariant()) {
			return varExpressions[0];
		}
		throw new IndexOutOfBoundsException("not backward compatible");
	}

	@Override
	public Expression getExpression(int index) {
		return varExpressions[index];
	}
	
	@Override
	@Deprecated
	public ISCVariant getVariant() {
		if (hasSingleDefaultVariant()) {
			return variants[0];
		}
		throw new IndexOutOfBoundsException("not backward compatible");
	}

	@Override
	public ISCVariant getVariant(int index) {
		return variants[index];
	}

	@Override
	public String getPOName(int index, String prefix, String suffix) {
		final StringJoiner joiner = new StringJoiner("/");
		if (prefix.length() != 0) {
			joiner.add(prefix);
		}

		if (hasSingleDefaultVariant()) {
			// Backward compatibility with Rodin 3.4
			// Do not add the variant label
		} else {
			joiner.add(getLabel(index));
		}

		if (suffix.length() != 0) {
			joiner.add(suffix);
		}
		return joiner.toString();
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public MachineVariantInfo(ISCVariant[] variants, ITypeEnvironment typeEnvironment) throws CoreException {
		this.variants = variants;
		varLabels = new String[variants.length];
		varExpressions = new Expression[variants.length];
		for (int i = 0; i < variants.length; ++i) {
			varLabels[i] = variants[i].getLabel();
			varExpressions[i] = variants[i].getExpression(typeEnvironment);
		}
	}

	@Override
	public boolean machineHasVariant() {
		return varExpressions.length != 0;
	}

	// Are we backward compatible with Rodin 3.4 : a single variant carrying the
	// default label?
	private boolean hasSingleDefaultVariant() {
		return count() == 1 && DEFAULT_LABEL.equals(getLabel(0));
	}
	
}
