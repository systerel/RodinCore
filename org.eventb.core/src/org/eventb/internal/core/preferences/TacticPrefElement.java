/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;

import java.util.List;

import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.ListPreference;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.SequentProver;

/**
 * The preference element translator for tactic descriptors.
 * 
 * @since 2.1
 */
public class TacticPrefElement implements
		IPrefElementTranslator<ITacticDescriptor> {

	private static final String SEPARATOR_ID = "#";
	private static final String SEPARATOR_PARAM = "@";
	private static final String SEPARATOR_TYPE = "$";
	private static final String SEPARATOR_PARAM_VALUE = "=";
	private static final String SEPARATOR_COMBINED = "!";

	private static class SimpleTacticTranslator implements
			IPrefElementTranslator<ITacticDescriptor> {

		public SimpleTacticTranslator() {
			// avoid synthetic access
		}

		@Override
		public String extract(ITacticDescriptor desc) {
			return desc.getTacticID();
		}

		private static boolean isDeclared(ITacticDescriptor tacticDesc) {
			final IAutoPostTacticManager manager = getAutoPostTacticManager();
			if (manager.getAutoTacticPreference().isDeclared(tacticDesc))
				return true;
			return manager.getPostTacticPreference().isDeclared(tacticDesc);
		}
		
		@Override
		public ITacticDescriptor inject(String str) {
			final IAutoTacticRegistry tacticRegistry = SequentProver
					.getAutoTacticRegistry();
			if (!tacticRegistry.isRegistered(str)) {
				printDebug("Trying to inject a tactic which is not registered "
						+ str);
				return null;
			}

			final ITacticDescriptor tacticDescriptor = tacticRegistry
					.getTacticDescriptor(str);
			if (!isDeclared(tacticDescriptor)) {
				printDebug("Tactic is not declared in this scope" + str);
				return null;

			}
			return tacticDescriptor;
		}

	}

	private static class ParamTacticTranslator implements
			IPrefElementTranslator<IParamTacticDescriptor> {

		public ParamTacticTranslator() {
			// avoid synthetic access
		}

		private static String getStringValue(IParameterDesc paramDesc,
				final IParameterValuation valuation) {
			final String label = paramDesc.getLabel();
			switch (paramDesc.getType()) {
			case BOOL:
				final boolean b = valuation.getBoolean(label);
				return Boolean.toString(b);
			case INT:
				final int i = valuation.getInt(label);
				return Integer.toString(i);
			case LONG:
				final long l = valuation.getLong(label);
				return Long.toString(l);
			case STRING:
				return valuation.getString(label);
			default:
				assert false;
				return null;
			}
		}

		@Override
		public String extract(IParamTacticDescriptor desc) {
			final StringBuilder sb = new StringBuilder();
			final String parameterizerId = desc.getParameterizerId();
			
			sb.append(parameterizerId);
			sb.append(SEPARATOR_ID);
			sb.append(desc.getTacticID());

			final IParameterValuation valuation = desc.getValuation();
			for (IParameterDesc paramDesc : valuation.getParameterDescs()) {
				sb.append(SEPARATOR_PARAM);
				sb.append(paramDesc.getLabel());
				sb.append(SEPARATOR_TYPE);
				sb.append(paramDesc.getType());
				sb.append(SEPARATOR_PARAM_VALUE);
				final String value = getStringValue(paramDesc, valuation);
				sb.append(value);
			}
			return sb.toString();
		}

		private static void setValue(IParameterSetting paramSetting,
				String label, ParameterType type, Object value) {
			switch(type) {
			case BOOL:
				paramSetting.setBoolean(label, (Boolean) value);
				break;
			case INT:
				paramSetting.setInt(label, (Integer) value);
				break;
			case LONG:
				paramSetting.setLong(label, (Long) value);
				break;
			case STRING:
				paramSetting.setString(label, (String) value);
				break;
			}
		}

		@Override
		public IParamTacticDescriptor inject(String s) {
			final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
			final String[] sId = s.split(SEPARATOR_ID);
			if (sId.length != 2) return null;
			final String parameterizerId = sId[0];

			final IParameterizerDescriptor parameterizer = reg
					.getParameterizerDescriptor(parameterizerId);
			final IParameterSetting paramSetting = parameterizer.makeParameterSetting();
			
			final String[] sParam = sId[1].split(SEPARATOR_PARAM);
			if (sParam.length == 0) return null;
			final String tacticID = sParam[0];
			for (int i = 1; i < sParam.length; i++) {
				final String[] sType = sParam[i].split(SEPARATOR_TYPE);
				if (sType.length != 2) return null;
				final String label = sType[0];
				final String[] sValue = sType[1].split(SEPARATOR_PARAM_VALUE);
				if (sValue.length != 2) return null;
				final ParameterType type = ParameterType.valueOf(sValue[0]);
				final Object value = type.parse(sValue[1]);
				setValue(paramSetting, label, type, value);
			}
			return parameterizer.instantiate(paramSetting, tacticID);
		}
	}

	private static class CombinedTacticTranslator implements
			IPrefElementTranslator<ICombinedTacticDescriptor> {

		// here we just store references to combined tactics
		// even if they are parameterized or combined
		// they are stored in extension independently
		// hence the use of the simple translator
		private final ListPreference<ITacticDescriptor> listTrans = new ListPreference<IAutoTacticRegistry.ITacticDescriptor>(
				new SimpleTacticTranslator());

		public CombinedTacticTranslator() {
			// avoid synthetic access
		}

		@Override
		public String extract(ICombinedTacticDescriptor combinator) {
			final StringBuilder sb = new StringBuilder();
			sb.append(combinator.getCombinatorId());
			sb.append(SEPARATOR_ID);
			sb.append(combinator.getTacticID());
			sb.append(SEPARATOR_COMBINED);
			sb.append(listTrans.extract(combinator.getCombinedTactics()));
			return sb.toString();
		}

		@Override
		public ICombinedTacticDescriptor inject(String s) {
			final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
			final String[] sId = s.split(SEPARATOR_ID);
			if (sId.length != 2) return null;
			final String combinatorId = sId[0];
			final ICombinatorDescriptor combinator = reg
					.getCombinatorDescriptor(combinatorId);
			final String[] sComb = sId[1].split(SEPARATOR_COMBINED, 2);
			if (sId.length != 2) return null;
			final String tacticId = sComb[0];
			final List<ITacticDescriptor> combined = listTrans.inject(sComb[1]);
			if (combined == null) return null;
			return combinator.instantiate(combined, tacticId);
		}
	}

	private final SimpleTacticTranslator simple = new SimpleTacticTranslator();
	private final ParamTacticTranslator param = new ParamTacticTranslator();
	private final CombinedTacticTranslator combined = new CombinedTacticTranslator();

	@Override
	public String extract(ITacticDescriptor desc) {
		if (desc instanceof ICombinedTacticDescriptor) {
			return combined.extract((ICombinedTacticDescriptor) desc);
		}
		if (desc instanceof IParamTacticDescriptor) {
			return param.extract((IParamTacticDescriptor) desc);
		}
		return simple.extract(desc);
	}

	@Override
	public ITacticDescriptor inject(String str) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		if (reg.isRegistered(str)) {
			return simple.inject(str);
		}
		final String[] split = str.split(SEPARATOR_ID);
		if (split.length != 2) {
			printDebug("unrecognized tactic preference format: " + str);
			return null;
		}
		final String id = split[0];
		if (reg.getCombinatorDescriptor(id) != null) {
			return combined.inject(str);
		}
		if (reg.getParameterizerDescriptor(id) != null) {
			return param.inject(str);
		}
		printDebug("unknown id: " + id);

		return null;
	}

	static void printDebug(String msg) {
		if (PreferenceUtils.DEBUG) {
			System.out.println(msg);
		}
	}

}