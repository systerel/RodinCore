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
import static org.eventb.internal.core.preferences.PreferenceUtils.getDocument;
import static org.eventb.internal.core.preferences.PreferenceUtils.getUniqueChild;
import static org.eventb.internal.core.preferences.PreferenceUtils.makeDocument;
import static org.eventb.internal.core.preferences.PreferenceUtils.serializeDocument;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.COMBINATOR_ID;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.LABEL;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.PARAMETERIZER_ID;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.PREF_UNIT_NAME;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.TACTIC_ID;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.TYPE;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.getAttribute;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.setAttribute;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.COMBINED;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PARAMETER;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PARAMETERIZED;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PREF_UNIT_REF;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.SIMPLE;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.assertName;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.createElement;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.hasName;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.preferences.IPrefElementTranslator;
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
import org.eventb.internal.core.Util;
import org.eventb.internal.core.preferences.PreferenceUtils.IXMLPref;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The preference element translator for tactic descriptors.
 * 
 * @since 2.1
 */
public class TacticPrefElement implements
		IPrefElementTranslator<ITacticDescriptor>, IXMLPref<ITacticDescriptor> {

	
	private static class Selector implements IXMLPref<ITacticDescriptor> {

		private final boolean refOnly;

		private Selector(boolean refOnly) {
			this.refOnly = refOnly;
		}
		
		private static final Selector REF_INSTANCE = new Selector(true);
		private static final Selector FULL_INSTANCE = new Selector(false);
		
		public static Selector getFull() {
			return FULL_INSTANCE;
		}
		
		public static Selector getRef() {
			return REF_INSTANCE;
		}
		
		@Override
		public void put(ITacticDescriptor desc, Document doc, Node parent) {
			if (desc instanceof ICombinedTacticDescriptor) {
				CombinedTacticTranslator.getDefault().put(
						(ICombinedTacticDescriptor) desc, doc, parent);
			} else if (desc instanceof IParamTacticDescriptor) {
				if (refOnly) {
					UnitRef.getDefault().put(desc, doc, parent);
				} else {
					ParamTacticTranslator.getDefault().put(
							(IParamTacticDescriptor) desc, doc, parent);
				}
			} else {

				SimpleTactic.getDefault().put(desc, doc, parent);
			}

		}

		@Override
		public ITacticDescriptor get(Node e) {
			if (hasName(e, SIMPLE)) {
				return SimpleTactic.getDefault().get(e);
			}
			if (hasName(e, PARAMETERIZED)) {
				return ParamTacticTranslator.getDefault().get(e);
			}
			if (hasName(e, PREF_UNIT_REF)) {
				return UnitRef.getDefault().get(e);
			}
			if (hasName(e, COMBINED)) {
				return CombinedTacticTranslator.getDefault().get(e);
			}
			printDebug("unreadable node: " + e);
			throw PreferenceException.getInstance();
		}

	}
	
	// translates to/from an id
	// does not record parameter valuation, supposed to be recorded independently in auto/post
	// propagates simple translation through combined tactics
	private static class SimpleTactic implements
			IXMLPref<ITacticDescriptor> {

		private SimpleTactic() {
			// singleton
		}

		private static final SimpleTactic DEFAULT = new SimpleTactic();
		
		public static SimpleTactic getDefault() {
			return DEFAULT;
		}

		@Override
		public void put(ITacticDescriptor desc, Document doc, Node parent) {
			final Element simple = createElement(doc, SIMPLE);
			setAttribute(simple, TACTIC_ID, desc.getTacticID());

			parent.appendChild(simple);
		}

		private static boolean isDeclared(ITacticDescriptor tacticDesc) {
			final IAutoPostTacticManager manager = getAutoPostTacticManager();
			if (manager.getAutoTacticPreference().isDeclared(tacticDesc))
				return true;
			return manager.getPostTacticPreference().isDeclared(tacticDesc);
		}

		@Override
		public ITacticDescriptor get(Node e) {
			assertName(e, SIMPLE);
			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			final String tacticId = getAttribute(e, TACTIC_ID);
			if (!reg.isRegistered(tacticId)) {
				printDebug("Tactic is not registered " + tacticId);
				return null;
			}
			final ITacticDescriptor tacticDescriptor = reg
					.getTacticDescriptor(tacticId);
			if (!isDeclared(tacticDescriptor)) {
				printDebug("Tactic is not declared in this scope " + tacticId);
				return null;
			}
			return tacticDescriptor;
		}

	}

	private static class ParamTacticTranslator implements
			IXMLPref<IParamTacticDescriptor> {

		private ParamTacticTranslator() {
			// singleton
		}

		private static final ParamTacticTranslator DEFAULT = new ParamTacticTranslator();
		
		public static ParamTacticTranslator getDefault() {
			return DEFAULT;
		}
		

		@Override
		public void put(IParamTacticDescriptor desc, Document doc, Node parent) {
			final Element parameterized = createElement(doc, PARAMETERIZED);
			setAttribute(parameterized, TACTIC_ID, desc.getTacticID());
			setAttribute(parameterized, PARAMETERIZER_ID, desc.getParameterizerId());
			
			final IParameterValuation valuation = desc.getValuation();
			for (IParameterDesc param : valuation.getParameterDescs()) {
				final Element parameter = createElement(doc, PARAMETER);
				final String label = param.getLabel();
				setAttribute(parameter, LABEL, label);
				setAttribute(parameter, TYPE, param.getType().toString());
				parameter.setTextContent(valuation.get(label).toString());
				parameterized.appendChild(parameter);
			}
			parent.appendChild(parameterized);
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
		public IParamTacticDescriptor get(Node e) {
			assertName(e, PARAMETERIZED);
			final String tacticId = getAttribute(e, TACTIC_ID);
			final String parameterizerId = getAttribute(e, PARAMETERIZER_ID);

			final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
			final IParameterizerDescriptor parameterizer = reg
					.getParameterizerDescriptor(parameterizerId);
			if (parameterizer == null) return null;
			final IParameterSetting paramSetting = parameterizer.makeParameterSetting();

			final NodeList childNodes = e.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				final Node param = childNodes.item(i);
				if (!(param instanceof Element)) continue;
				assertName(param, PARAMETER);
				final String label = getAttribute(param, LABEL);
				final String sType = getAttribute(param, TYPE);
				final ParameterType type = ParameterType.valueOf(sType);
				
				final Object value = type.parse(param.getTextContent());
				setValue(paramSetting, label, type, value);
			}
			return parameterizer.instantiate(paramSetting, tacticId);
		}

	}
	
	private static class UnitRef implements IXMLPref<ITacticDescriptor> {

		private UnitRef() {
			// singleton
		}

		private static final UnitRef DEFAULT = new UnitRef();

		public static UnitRef getDefault() {
			return DEFAULT;
		}
		
		@Override
		public void put(ITacticDescriptor desc, Document doc, Node parent) {
			final Element ref = createElement(doc, PREF_UNIT_REF);
			setAttribute(ref, PREF_UNIT_NAME, desc.getTacticID());
			parent.appendChild(ref);
		}

		@Override
		public ITacticDescriptor get(Node e) {
			assertName(e, PREF_UNIT_REF);
			final String unitName = getAttribute(e, PREF_UNIT_NAME);
			final Element unit = e.getOwnerDocument().getElementById(unitName);
			if (unit == null) {
				// reference to an unknown element
				throw PreferenceException.getInstance();
			}
			// FIXME a mere copy of unit is not linked to it (is not updated)  
			// return a reference instead !
			final Node tactic = getUniqueChild(unit);
			return Selector.getFull().get(tactic);
		}
	}

	private static class CombinedTacticTranslator implements
			IXMLPref<ICombinedTacticDescriptor> {

		private static final CombinedTacticTranslator DEFAULT = new CombinedTacticTranslator();
		
		public static CombinedTacticTranslator getDefault() {
			return DEFAULT;
		}
		
		// here we just store references to combined tactics
		// even if they are parameterized
		// they are stored in extension independently
		// hence the use of the reference selector

		private CombinedTacticTranslator() {
			// singleton
		}

		@Override
		public void put(ICombinedTacticDescriptor combinator, Document doc,
				Node parent) {
			final Element combined = createElement(doc, COMBINED);
			setAttribute(combined, TACTIC_ID, combinator.getTacticID());
			setAttribute(combined, COMBINATOR_ID, combinator.getCombinatorId());
			for (ITacticDescriptor comb : combinator.getCombinedTactics()) {
				Selector.getFull().put(comb, doc, combined); //FIXME Selector.getRef() 
			}
			parent.appendChild(combined);
		}

		@Override
		public ICombinedTacticDescriptor get(Node combined) {
			assertName(combined, COMBINED);
			final String tacticId = getAttribute(combined, TACTIC_ID);
			final String combinatorId = getAttribute(combined, COMBINATOR_ID);
			
			final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
			final ICombinatorDescriptor combinator = reg
					.getCombinatorDescriptor(combinatorId);
			if (combinator == null) return null;
			final NodeList childNodes = combined.getChildNodes();
			final int length = childNodes.getLength();
			final List<ITacticDescriptor> combs = new ArrayList<ITacticDescriptor>(
					length);
			for (int i = 0; i < length; i++) {
				final Node comb = childNodes.item(i);
				if (!(comb instanceof Element)) continue;
				final ITacticDescriptor combDesc = Selector.getRef().get(comb);
				if (combDesc == null) return null;
				combs.add(combDesc);
			}
			return combinator.instantiate(combs, tacticId);
		}

	}

	@Override
	public String extract(ITacticDescriptor desc) {
		try {
			final Document doc = getDocument();
			put(desc, doc, doc);
			return serializeDocument(doc);
		} catch (Exception e) {
			Util.log(e,
					"while storing tactic preference for " + desc.getTacticID());
		}
		return "";
	}

	@Override
	public ITacticDescriptor inject(String str) {
		Document doc;
		try {
			doc = makeDocument(str);
			return get(doc.getDocumentElement());
		} catch (Exception e) {
			Util.log(e, "while retrieving tactic preference from:\n" + str);
			return null;
		}
	}

	static void printDebug(String msg) {
		if (PreferenceUtils.DEBUG) {
			System.out.println(msg);
		}
	}
	
	@Override
	public void put(ITacticDescriptor desc, Document doc, Node parent) {
		Selector.getFull().put(desc, doc, parent);
	}

	@Override
	public ITacticDescriptor get(Node node) {
		return Selector.getFull().get(node);
	}

}