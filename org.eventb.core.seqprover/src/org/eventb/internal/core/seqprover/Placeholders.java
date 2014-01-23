/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static org.eventb.core.seqprover.IParameterDesc.ParameterType.BOOL;
import static org.eventb.core.seqprover.IParameterDesc.ParameterType.INT;
import static org.eventb.core.seqprover.IParameterDesc.ParameterType.LONG;
import static org.eventb.core.seqprover.IParameterDesc.ParameterType.STRING;

import java.util.Collections;
import java.util.List;

import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.TacticDescriptors.AbstractTacticDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.CombinedTacticDescriptor;
import org.eventb.internal.core.seqprover.paramTactics.ParameterDesc;
import org.eventb.internal.core.seqprover.paramTactics.ParameterSetting;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.BoolParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.IntParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.LongParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.StringParameterValue;

/**
 * Placeholder implementations of auto tactic descriptor interfaces. Provides
 * placeholders for tactics, parameterizers and combinators.
 * 
 * @author Nicolas Beauger
 */
public class Placeholders {

	private static final String TACTIC_PLACEHOLDER = "Tactic Placeholder";

	private static String makePlaceholderId(String id) {
		return "! " + id + " !";
	}

	/**
	 * Tactic placeholder with a custom ID. This tactic always fails.
	 */
	public static class TacticPlaceholder extends AbstractTacticDescriptor {

		public TacticPlaceholder(String id) {
			super(id, makePlaceholderId(id), TACTIC_PLACEHOLDER);
		}

		@Override
		public ITactic getTacticInstance() {
			return BasicTactics.failTac(TACTIC_PLACEHOLDER);
		}

	}

	/**
	 * Parameter setting placeholder with custom parameterizer ID.
	 * <p>
	 * It accepts all settings without check, in order to be filled by
	 * deserialization and editing. As parameter descriptors are initially
	 * unknown, they are also filled at that time.
	 * </p>
	 */
	private static class ParameterSettingPlaceholder extends ParameterSetting {

		private final String description;

		public ParameterSettingPlaceholder(String parameterizerId) {
			super(Collections.<IParameterDesc> emptyList());
			this.description = makePlaceholderId(parameterizerId)
					+ "\nis unknown";
		}

		@Override
		public void setBoolean(String label, Boolean value) {
			if (!valuation.containsKey(label)) {
				final ParameterDesc desc = new ParameterDesc(label, BOOL,
						false, description);
				paramDescs.add(desc);
				final BoolParameterValue paramValue = new BoolParameterValue(
						desc);
				valuation.put(label, paramValue);
			}
			super.setBoolean(label, value);
		}

		@Override
		public void setInt(String label, Integer value) {
			if (!valuation.containsKey(label)) {
				final ParameterDesc desc = new ParameterDesc(label, INT, -1,
						description);
				paramDescs.add(desc);
				final IntParameterValue paramValue = new IntParameterValue(desc);
				valuation.put(label, paramValue);
			}
			super.setInt(label, value);
		}

		@Override
		public void setLong(String label, Long value) {
			if (!valuation.containsKey(label)) {
				final ParameterDesc desc = new ParameterDesc(label, LONG, -1L,
						description);
				paramDescs.add(desc);
				final LongParameterValue paramValue = new LongParameterValue(
						desc);
				valuation.put(label, paramValue);
			}
			super.setLong(label, value);
		}

		@Override
		public void setString(String label, String value) {
			if (!valuation.containsKey(label)) {
				final ParameterDesc desc = new ParameterDesc(label, STRING, "",
						description);
				paramDescs.add(desc);
				final StringParameterValue paramValue = new StringParameterValue(
						desc);
				valuation.put(label, paramValue);
			}
			super.setString(label, value);
		}

		@Override
		public void set(String label, Object value) {
			if (value instanceof Boolean) {
				setBoolean(label, (Boolean) value);
			} else if (value instanceof Integer) {
				setInt(label, (Integer) value);
			} else if (value instanceof Long) {
				setLong(label, (Long) value);
			} else if (value instanceof String) {
				setString(label, (String) value);
			} else {
				throw new IllegalArgumentException(
						"Unsupported value setting: " + value + " of type "
								+ value.getClass().getName());
			}
		}
	}

	/**
	 * Parameterized tactic descriptor placeholder.
	 * <p>
	 * The tactic instance returned by these placeholders always fail.
	 * </p>
	 */
	private static class ParamTacticDescriptorPlaceholder extends
			TacticPlaceholder implements IParamTacticDescriptor {

		private final String parameterizerId;
		private final IParameterValuation valuation;

		public ParamTacticDescriptorPlaceholder(String id,
				String parameterizerId, IParameterValuation valuation) {
			super(id);
			this.parameterizerId = parameterizerId;
			this.valuation = valuation;
		}

		@Override
		public String getParameterizerId() {
			return parameterizerId;
		}

		@Override
		public IParameterValuation getValuation() {
			return valuation;
		}

	}

	/**
	 * Tactic parameterizer descriptor placeholder.
	 * <p>
	 * Instantiation returns a placeholder that always fails.
	 * </p>
	 */
	public static class ParameterizerPlaceholder implements
			IParameterizerDescriptor {

		private final String parameterizerId;
		private final TacticPlaceholder tacticPlaceholder;

		public ParameterizerPlaceholder(String parameterizerId) {
			this.parameterizerId = parameterizerId;
			this.tacticPlaceholder = new TacticPlaceholder(parameterizerId);
		}

		@Override
		public ITacticDescriptor getTacticDescriptor() {
			return tacticPlaceholder;
		}

		@Override
		public IParameterSetting makeParameterSetting() {
			return new ParameterSettingPlaceholder(parameterizerId);
		}

		@Override
		public IParamTacticDescriptor instantiate(
				IParameterValuation valuation, String id)
				throws IllegalArgumentException {
			return instantiate(id, null, null, valuation);
		}

		@Override
		public IParamTacticDescriptor instantiate(String id, String name,
				String description, IParameterValuation valuation) {
			return new ParamTacticDescriptorPlaceholder(id, parameterizerId,
					valuation);
		}

	}

	/**
	 * Combined tactic descriptor placeholder.
	 * <p>
	 * The tactic instance always fails.
	 * </p>
	 */
	private static class CombinedTacticDescriptorPlaceholder extends
			CombinedTacticDescriptor {

		public CombinedTacticDescriptorPlaceholder(String id, String name,
				String description, String combinatorId,
				List<ITacticDescriptor> combined) {
			super(id, name, description, null, combinatorId, combined);
		}

		@Override
		public ITactic getTacticInstance() {
			return BasicTactics.failTac(TACTIC_PLACEHOLDER);
		}

	}

	/**
	 * Tactic combinator descriptor placeholder.
	 * <p>
	 * Describes a combinator that accepts any number of combined tactics.
	 * Combination returns a placeholder that always fails.
	 * </p>
	 */
	public static class CombinatorDescriptorPlaceholder implements
			ICombinatorDescriptor {

		private final TacticPlaceholder tacticPlaceholder;

		public CombinatorDescriptorPlaceholder(String id) {
			this.tacticPlaceholder = new TacticPlaceholder(id);
		}

		@Override
		public ITacticDescriptor getTacticDescriptor() {
			return tacticPlaceholder;
		}

		@Override
		public ICombinedTacticDescriptor combine(
				List<ITacticDescriptor> tactics, String id)
				throws IllegalArgumentException {
			return combine(tactics, id, makePlaceholderId(id),
					TACTIC_PLACEHOLDER);
		}

		@Override
		public ICombinedTacticDescriptor combine(
				List<ITacticDescriptor> tactics, String id, String name,
				String description) {
			return new CombinedTacticDescriptorPlaceholder(id, name,
					description, tacticPlaceholder.getTacticID(), tactics);
		}

		@Override
		public int getMinArity() {
			return 0;
		}

		@Override
		public boolean isArityBound() {
			return false;
		}

	}
}
