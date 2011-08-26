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
package org.eventb.internal.core.preferences;

import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;
import static org.eventb.internal.core.preferences.PreferenceUtils.getUniqueChild;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.COMBINATOR_ID;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.LABEL;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.PARAMETERIZER_ID;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.PREF_KEY;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.TACTIC_ID;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.TYPE;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.getAttribute;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLAttributeTypes.setAttribute;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.COMBINED;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PARAMETER;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PARAMETERIZED;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PREF_UNIT;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PREF_REF;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.SIMPLE;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.assertName;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.createElement;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.hasName;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IXMLPrefSerializer;
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
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;
import org.eventb.internal.core.preferences.PreferenceUtils.ReadPrefMapEntry;
import org.eventb.internal.core.preferences.PreferenceUtils.UnresolvedPrefMapEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Nicolas Beauger
 * 
 */
public class PrefUnitTranslator implements
		IXMLPrefSerializer<ITacticDescriptor> {

	private static interface IInternalXMLSerializer<T> {
		
		/**
		 * Serializes the given preference into the given parent.
		 * 
		 * @param pref
		 *            a preference
		 * @param doc
		 *            the document where serialization takes place
		 * @param parent
		 *            the parent of the serialized preference
		 */
		void put(T pref, Document doc, Node parent);

		/**
		 * Deserializes the given node.
		 * 
		 * @param n
		 *            a node
		 * @return the deserialization result, or <code>null</code> if
		 *         deserialization failed
		 */
		T get(Node n);

		/**
		 * Replaces the reference placeholders, contained in the given
		 * preference, by actual references obtained from the given map.
		 * 
		 * @param pref
		 *            a preference
		 * @param map
		 *            a preference map
		 */
		T resolveReferences(T pref, CachedPreferenceMap<ITacticDescriptor> map);

	}
	
	private static class Selector implements IInternalXMLSerializer<ITacticDescriptor> {

		private Selector() {
		}

		private static final Selector INSTANCE = new Selector();

		public static Selector getInstance() {
			return INSTANCE;
		}

		@Override
		public void put(ITacticDescriptor desc, Document doc, Node parent) {
			if (desc instanceof ITacticDescriptorRef) {
				TacticRef.getDefault().put((ITacticDescriptorRef) desc, doc, parent);
			} else if (desc instanceof ICombinedTacticDescriptor) {
				CombinedTacticTranslator.getDefault().put(
						(ICombinedTacticDescriptor) desc, doc, parent);
			} else if (desc instanceof IParamTacticDescriptor) {
				ParamTacticTranslator.getDefault().put(
						(IParamTacticDescriptor) desc, doc, parent);
			} else {
				SimpleTactic.getDefault().put(desc, doc, parent);
			}

		}

		@Override
		public ITacticDescriptor resolveReferences(ITacticDescriptor desc,
				CachedPreferenceMap<ITacticDescriptor> map) {
			if (desc instanceof ITacticDescriptorRef) {
				return TacticRef.getDefault().resolveReferences(
						(ITacticDescriptorRef) desc, map);
			} else if (desc instanceof ICombinedTacticDescriptor) {
				return CombinedTacticTranslator.getDefault().resolveReferences(
						(ICombinedTacticDescriptor) desc, map);
			}
			// else nothing to do
			return desc;
		}

		@Override
		public ITacticDescriptor get(Node e) {
			if (hasName(e, SIMPLE)) {
				return SimpleTactic.getDefault().get(e);
			}
			if (hasName(e, PARAMETERIZED)) {
				return ParamTacticTranslator.getDefault().get(e);
			}
			if (hasName(e, PREF_REF)) {
				return TacticRef.getDefault().get(e);
			}
			if (hasName(e, COMBINED)) {
				return CombinedTacticTranslator.getDefault().get(e);
			}
			printDebug("unreadable node: " + e);
			throw PreferenceException.getInstance();
		}

	}

	// translates to/from an id
	// does not record parameter valuation, supposed to be recorded
	// independently in auto/post
	// propagates simple translation through combined tactics
	private static class SimpleTactic implements
			IInternalXMLSerializer<ITacticDescriptor> {

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

		@Override
		public ITacticDescriptor resolveReferences(ITacticDescriptor pref,
				CachedPreferenceMap<ITacticDescriptor> map) {
			return pref;
		}

	}

	private static class ParamTacticTranslator implements
			IInternalXMLSerializer<IParamTacticDescriptor> {

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
			setAttribute(parameterized, PARAMETERIZER_ID,
					desc.getParameterizerId());

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
			switch (type) {
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

			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			final IParameterizerDescriptor parameterizer = reg
					.getParameterizerDescriptor(parameterizerId);
			if (parameterizer == null)
				return null;
			final IParameterSetting paramSetting = parameterizer
					.makeParameterSetting();

			final NodeList childNodes = e.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				final Node param = childNodes.item(i);
				if (!(param instanceof Element))
					continue;
				assertName(param, PARAMETER);
				final String label = getAttribute(param, LABEL);
				final String sType = getAttribute(param, TYPE);
				final ParameterType type = ParameterType.valueOf(sType);

				final Object value = type.parse(param.getTextContent());
				setValue(paramSetting, label, type, value);
			}
			return parameterizer.instantiate(paramSetting, tacticId);
		}

		@Override
		public IParamTacticDescriptor resolveReferences(
				IParamTacticDescriptor pref,
				CachedPreferenceMap<ITacticDescriptor> map) {
			return pref;
		}

	}

	private static class TacticRef implements IInternalXMLSerializer<ITacticDescriptorRef>{

		private TacticRef() {
			// singleton
		}

		private static final TacticRef DEFAULT = new TacticRef();

		public static TacticRef getDefault() {
			return DEFAULT;
		}

		@Override
		public void put(ITacticDescriptorRef desc, Document doc, Node parent) {
			final Element ref = createElement(doc, PREF_REF);
			final String key = desc.getPrefEntry().getKey();
			setAttribute(ref, PREF_KEY, key);
			parent.appendChild(ref);
		}

		@Override
		public ITacticDescriptorRef get(Node e) {
			assertName(e, PREF_REF);
			final String key = getAttribute(e, PREF_KEY);
			final IPrefMapEntry<ITacticDescriptor> entry = new UnresolvedPrefMapEntry<ITacticDescriptor>(
					key);
			return new TacticDescriptorRef(entry);
		}

		@Override
		public ITacticDescriptorRef resolveReferences(ITacticDescriptorRef pref,
				CachedPreferenceMap<ITacticDescriptor> map) {
			// prefEntry is an UnresolvedPrefMapEntry: contains only a key
			final IPrefMapEntry<ITacticDescriptor> unresEntry = pref.getPrefEntry();
			final String key = unresEntry.getKey();
			final IPrefMapEntry<ITacticDescriptor> entry = map.getEntry(key);
			if (entry == null) {
				// remains unresolved
				return pref;
			}
			return new TacticDescriptorRef(entry);
		}
	}

	private static class CombinedTacticTranslator implements
			IInternalXMLSerializer<ICombinedTacticDescriptor> {

		private static final CombinedTacticTranslator DEFAULT = new CombinedTacticTranslator();

		public static CombinedTacticTranslator getDefault() {
			return DEFAULT;
		}

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
				Selector.getInstance().put(comb, doc, combined);
			}
			parent.appendChild(combined);
		}

		@Override
		public ICombinedTacticDescriptor get(Node combined) {
			assertName(combined, COMBINED);
			final String tacticId = getAttribute(combined, TACTIC_ID);
			final String combinatorId = getAttribute(combined, COMBINATOR_ID);

			final IAutoTacticRegistry reg = SequentProver
					.getAutoTacticRegistry();
			final ICombinatorDescriptor combinator = reg
					.getCombinatorDescriptor(combinatorId);
			if (combinator == null)
				return null;
			final NodeList childNodes = combined.getChildNodes();
			final int length = childNodes.getLength();
			final List<ITacticDescriptor> combs = new ArrayList<ITacticDescriptor>(
					length);
			for (int i = 0; i < length; i++) {
				final Node comb = childNodes.item(i);
				if (!(comb instanceof Element))
					continue;
				final ITacticDescriptor combDesc = Selector.getInstance().get(comb);
				if (combDesc == null)
					return null;
				combs.add(combDesc);
			}
			return combinator.instantiate(combs, tacticId);
		}

		@Override
		public ICombinedTacticDescriptor resolveReferences(
				ICombinedTacticDescriptor desc,
				CachedPreferenceMap<ITacticDescriptor> map) {
			final List<ITacticDescriptor> combined = desc.getCombinedTactics();
			final List<ITacticDescriptor> newCombs = new ArrayList<ITacticDescriptor>(combined.size());
			boolean changed = false;
			for (ITacticDescriptor comb : combined) {
				final ITacticDescriptor newComb = Selector.getInstance()
						.resolveReferences(comb, map);
				newCombs.add(newComb);
				changed |= newComb != comb;
			}
			if (!changed) {
				return desc;
			}
			final String combinatorId = desc.getCombinatorId();
			final ICombinatorDescriptor combinator = SequentProver
					.getAutoTacticRegistry().getCombinatorDescriptor(
							combinatorId);
			if (combinator == null) {
				// should not happen, as deserialization would have failed before
				return desc;
			}
			return combinator.instantiate(newCombs, desc.getTacticID());
		}

	}

	static void printDebug(String msg) {
		if (PreferenceUtils.DEBUG) {
			System.out.println(msg);
		}
	}

	@Override
	public void put(IPrefMapEntry<ITacticDescriptor> unit, Document doc, Node parent) {
		final Element unitElem = createElement(doc, PREF_UNIT);
		final String unitName = unit.getKey();
		setAttribute(unitElem, PREF_KEY, unitName);
		unitElem.setIdAttribute(PREF_KEY.toString(), true);
		final ITacticDescriptor element = unit.getValue();
		Selector.getInstance().put(element, doc, unitElem);
		parent.appendChild(unitElem);
	}

	@Override
	public IPrefMapEntry<ITacticDescriptor> get(Node unitElem) {
		assertName(unitElem, PREF_UNIT);
		final String unitName = getAttribute(unitElem, PREF_KEY);
		final Node child = getUniqueChild(unitElem);
			
		final ITacticDescriptor desc = Selector.getInstance().get(child);
		return new ReadPrefMapEntry<ITacticDescriptor>(unitName, desc);
	}

	@Override
	public void resolveReferences(
			IPrefMapEntry<ITacticDescriptor> entry,
			CachedPreferenceMap<ITacticDescriptor> map) {
		final ITacticDescriptor desc = entry.getValue();
		final ITacticDescriptor newDesc = Selector.getInstance()
				.resolveReferences(desc, map);
		if (newDesc == desc) {
			return;
		}
		entry.setValue(newDesc);
	}

}
