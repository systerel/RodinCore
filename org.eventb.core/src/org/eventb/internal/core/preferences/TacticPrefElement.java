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
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.SequentProver;

/**
 * The preference element translator for tactic descriptors.
 * 
 * @since 2.1
 */
public class TacticPrefElement implements
		IPrefElementTranslator<ITacticDescriptor> {

	private static final char OPEN = '[';
	private static final char CLOSE = ']';
	private static final String SEPARATOR_ID = "#";
	private static final String SEPARATOR_PARAM = "@";
	private static final String SEPARATOR_TYPE = "$";
	private static final String SEPARATOR_PARAM_VALUE = "=";

	// translates to/from an id
	// does not record parameter valuation, supposed to be recorded independently in auto/post
	// propagates simple translation through combined tactics
	private static class TacticRefTranslator implements
			IPrefElementTranslator<ITacticDescriptor> {

		private TacticRefTranslator() {
			// singleton
		}

		private static final TacticRefTranslator DEFAULT = new TacticRefTranslator();
		
		public static TacticRefTranslator getDefault() {
			return DEFAULT;
		}
		
		@Override
		public String extract(ITacticDescriptor desc) {
			if (desc instanceof ICombinedTacticDescriptor) {
				final String combStr = CombinedTacticTranslator.getDefault()
						.extract((ICombinedTacticDescriptor) desc);
				return combStr;
			} else { // param or simple
				return desc.getTacticID();
			}
		}

		private static boolean isDeclared(ITacticDescriptor tacticDesc) {
			final IAutoPostTacticManager manager = getAutoPostTacticManager();
			if (manager.getAutoTacticPreference().isDeclared(tacticDesc))
				return true;
			return manager.getPostTacticPreference().isDeclared(tacticDesc);
		}
		
		@Override
		public ITacticDescriptor inject(String str) {
			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			if (reg.isRegistered(str)) {
				// simple
				final ITacticDescriptor tacticDescriptor = reg
						.getTacticDescriptor(str);
				if (!isDeclared(tacticDescriptor)) {
					printDebug("Tactic is not declared in this scope " + str);
					return null;
					
				}
				return tacticDescriptor;
			}
			final ICombinedTacticDescriptor comb = CombinedTacticTranslator
					.getDefault().inject(str);
			if (comb != null) {
				// combined
				return comb;
			}
			// parameterized
			// FIXME fetch param tactic from reference 
			// => access store or delay (return a special descriptor)
			printDebug("maybe a parameterized tactic " + str);
			return null;
		}

	}

	private static class ParamTacticTranslator implements
			IPrefElementTranslator<IParamTacticDescriptor> {

		private ParamTacticTranslator() {
			// singleton
		}

		private static final ParamTacticTranslator DEFAULT = new ParamTacticTranslator();
		
		public static ParamTacticTranslator getDefault() {
			return DEFAULT;
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
				final String label = paramDesc.getLabel();
				sb.append(label);
				sb.append(SEPARATOR_TYPE);
				sb.append(paramDesc.getType());
				sb.append(SEPARATOR_PARAM_VALUE);
				final String value = valuation.get(label).toString();
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
			final String[] sId = s.split(SEPARATOR_ID, 2);
			if (sId.length != 2) return null;
			final String parameterizerId = sId[0];

			final IParameterizerDescriptor parameterizer = reg
					.getParameterizerDescriptor(parameterizerId);
			final IParameterSetting paramSetting = parameterizer.makeParameterSetting();
			
			final String[] sParam = sId[1].split(SEPARATOR_PARAM);
			if (sParam.length == 0) return null;
			final String tacticID = sParam[0];
			for (int i = 1; i < sParam.length; i++) {
				final String[] sType = sParam[i].split(SEPARATOR_TYPE, 2);
				if (sType.length != 2) return null;
				final String label = sType[0];
				final String[] sValue = sType[1].split(SEPARATOR_PARAM_VALUE, 2);
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

		private static final CombinedTacticTranslator DEFAULT = new CombinedTacticTranslator();
		
		public static CombinedTacticTranslator getDefault() {
			return DEFAULT;
		}
		
		// here we just store references to combined tactics
		// even if they are parameterized
		// they are stored in extension independently
		// hence the use of the reference translator
		private final ListPreference<ITacticDescriptor> listTrans = new ListPreference<ITacticDescriptor>(
				TacticRefTranslator.getDefault());

		private CombinedTacticTranslator() {
			// singleton
		}

		
		
		@Override
		public String extract(ICombinedTacticDescriptor combinator) {
			final StringBuilder sb = new StringBuilder();
			sb.append(combinator.getCombinatorId());
			sb.append(SEPARATOR_ID);
			sb.append(combinator.getTacticID());
			sb.append(OPEN);
			sb.append(listTrans.extract(combinator.getCombinedTactics()));
			sb.append(CLOSE);
			return sb.toString();
		}

		@Override
		public ICombinedTacticDescriptor inject(String s) {
			final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
			final String[] sId = s.split(SEPARATOR_ID, 2);
			if (sId.length != 2) return null;
			final String combinatorId = sId[0];
			final ICombinatorDescriptor combinator = reg
					.getCombinatorDescriptor(combinatorId);
			if (combinator == null) return null;
			final int openIndex = sId[1].indexOf(OPEN);
			if (openIndex < 0) return null;
			
			final String tacticId = sId[1].substring(0, openIndex);
			final String afterOpen = sId[1].substring(openIndex + 1);
			final int indexToClose = indexToClose(afterOpen);
			if (indexToClose < 0) return null;
			final String combs = afterOpen.substring(0, indexToClose);
			// FIXME list trans splits on ',' even inside combined sub tactics
			final List<ITacticDescriptor> combined = listTrans.inject(combs);
			if (combined == null) return null;
			return combinator.instantiate(combined, tacticId);
		}



		private static int indexToClose(String string) {
			int open = 0;
			for (int i = 0; i < string.length(); i++) {
				final char c = string.charAt(i);
				switch (c) {
				case OPEN:
					open++;
					break;
				case CLOSE:
					if (open == 0) {
						return i;
					}
					open--;
					break;
				}
			}
			return -1;
		}
	}

	@Override
	public String extract(ITacticDescriptor desc) {
		if (desc instanceof ICombinedTacticDescriptor) {
			return CombinedTacticTranslator.getDefault().extract(
					(ICombinedTacticDescriptor) desc);
		}
		if (desc instanceof IParamTacticDescriptor) {
			return ParamTacticTranslator.getDefault().extract(
					(IParamTacticDescriptor) desc);
		}
		return TacticRefTranslator.getDefault().extract(desc);
	}

	@Override
	public ITacticDescriptor inject(String str) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		if (reg.isRegistered(str)) {
			return TacticRefTranslator.getDefault().inject(str);
		}
		final String[] split = str.split(SEPARATOR_ID, 2);
		if (split.length != 2) {
			printDebug("unrecognized tactic preference format: " + str);
			return null;
		}
		final String id = split[0];
		if (reg.getCombinatorDescriptor(id) != null) {
			return CombinedTacticTranslator.getDefault().inject(str);
		}
		if (reg.getParameterizerDescriptor(id) != null) {
			return ParamTacticTranslator.getDefault().inject(str);
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