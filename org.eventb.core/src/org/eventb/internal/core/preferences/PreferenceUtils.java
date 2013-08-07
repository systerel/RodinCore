/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import static java.util.Arrays.asList;
import static org.eventb.core.EventBPlugin.PLUGIN_ID;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_SIMPLIFY_PROOFS;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;
import static org.eventb.internal.core.preferences.PreferenceInitializer.DEFAULT_AUTO_ENABLE;
import static org.eventb.internal.core.preferences.PreferenceInitializer.DEFAULT_POST_ENABLE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.EventBPlugin;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators;
import org.eventb.internal.core.Util;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for preferences using.
 */
public class PreferenceUtils {

	/**
	 * The debug flag. This is set by the option when the platform is launched.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;

	private static final class BoolPrefUpdater implements
			IPreferenceChangeListener {

		private static final IScopeContext[] CONTEXTS = new IScopeContext[] {
				InstanceScope.INSTANCE, DefaultScope.INSTANCE };

		public BoolPrefUpdater() {
			// avoid synthetic access
		}

		private void update(IAutoTacticPreference pref, String qualifier,
				String key) {
			final boolean enabled = Platform.getPreferencesService()
					.getBoolean(qualifier, key, false, CONTEXTS);
			pref.setEnabled(enabled);
		}

		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			final String key = event.getKey();
			final Preferences node = event.getNode();
			final String qualifier = node.name();
			
			if (key.equals(P_CONSIDER_HIDDEN_HYPOTHESES)) {
				EventBPlugin.getUserSupportManager()
						.setConsiderHiddenHypotheses(
								(Boolean) event.getNewValue());
				return;
			}
			
			final IAutoTacticPreference pref;

			if (key.equals(P_POSTTACTIC_ENABLE)) {
				pref = EventBPlugin.getAutoPostTacticManager()
						.getPostTacticPreference();
			} else if (key.equals(P_AUTOTACTIC_ENABLE)) {
				pref = EventBPlugin.getAutoPostTacticManager()
						.getAutoTacticPreference();
			} else {
				return;
			}

			update(pref, qualifier, key);
		}
	}

	public static class PreferenceException extends RuntimeException {

		private static final long serialVersionUID = -4388540765121161963L;

		private static final PreferenceException INSTANCE = new PreferenceException();

		private PreferenceException() {
			// singleton
		}

		public static PreferenceException getInstance() {
			return INSTANCE;
		}
	}

	public static class ReadPrefMapEntry<T> implements IPrefMapEntry<T> {

		private final String key;
		private final T value;

		public ReadPrefMapEntry(String key, T value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public T getValue() {
			return value;
		}

		@Override
		public void setKey(String key) {
			// do nothing
		}

		@Override
		public void setValue(T value) {
			// do nothing
		}

		@Override
		public T getReference() {
			return null;
		}

	}

	public static class UnresolvedPrefMapEntry<T> extends ReadPrefMapEntry<T> {

		public UnresolvedPrefMapEntry(String key) {
			super(key, null);
		}

	}

	/**
	 * Returns a string representation of a list of input objects. The objects
	 * are separated by a given character.
	 * 
	 * @param objects
	 *            a list of objects
	 * @param separator
	 *            the character to use to separate the objects
	 * @return the string representation of input objects
	 */
	public static <T> String flatten(List<T> objects, String separator) {
		final StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (T item : objects) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(item);
		}
		return buffer.toString();
	}

	/**
	 * Parse a character separated string to a list of string.
	 * 
	 * @param stringList
	 *            the comma separated string.
	 * @param c
	 *            the character separates the string
	 * @return an array of strings that make up the character separated input
	 *         string.
	 */
	public static String[] parseString(String stringList, String c) {
		StringTokenizer st = new StringTokenizer(stringList, c);//$NON-NLS-1$
		ArrayList<String> result = new ArrayList<String>();
		while (st.hasMoreElements()) {
			result.add((String) st.nextElement());
		}
		return result.toArray(new String[result.size()]);
	}

	// for compatibility
	public static ITacticDescriptor loopOnAllPending(
			List<ITacticDescriptor> descs, String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor comb = reg
				.getCombinatorDescriptor(TacticCombinators.LoopOnAllPending.COMBINATOR_ID);
		return comb.combine(descs, id);
	}

	/**
	 * Returns a 'loop on all pending' tactic descriptor on auto tactics with
	 * given ids; the resulting tactic ears the given id.
	 * 
	 * @param tacticIDs
	 *            an array of auto tactic ids
	 * @param id
	 *            the id of the resulting tactic
	 * @return a tactic descriptor
	 */
	public static ITacticDescriptor loopOnAllPending(String[] tacticIDs,
			String id) {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ArrayList<ITacticDescriptor> descs = new ArrayList<ITacticDescriptor>();
		for (String descId : tacticIDs) {
			if (!reg.isRegistered(descId)) {
				continue;
			}
			final ITacticDescriptor desc = reg.getTacticDescriptor(descId);
			descs.add(desc);
		}
		return loopOnAllPending(descs, id);
	}

	public static enum XMLElementTypes {
		TACTIC_PREF, PREF_UNIT, SIMPLE, PARAMETERIZED, PARAMETER, COMBINED, PREF_REF;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public static Element createElement(Document doc, XMLElementTypes name) {
			return doc.createElement(name.toString());
		}

		public static boolean hasName(Node node, XMLElementTypes name) {
			return node.getNodeName().equals(name.toString());
		}

		public static NodeList getElementsByTagName(Element node,
				XMLElementTypes nodeType) {
			return node.getElementsByTagName(nodeType.toString());
		}

		public static void assertName(Node node, XMLElementTypes name)
				throws PreferenceException {
			if (!hasName(node, name)) {
				throw PreferenceException.getInstance();
			}
		}
	}

	public static enum XMLAttributeTypes {
		PREF_KEY, TACTIC_ID, PARAMETERIZER_ID, LABEL, TYPE, COMBINATOR_ID;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		public static String getAttribute(Node node,
				XMLAttributeTypes attributeType) throws PreferenceException {
			final NamedNodeMap attributes = node.getAttributes();
			final Node att = attributes.getNamedItem(attributeType.toString());
			if (att == null) {
				throw PreferenceException.getInstance();
			}
			return att.getNodeValue();
		}

		public static void setAttribute(Element node,
				XMLAttributeTypes attributeType, String value) {
			node.setAttribute(attributeType.toString(), value);
		}
	}

	/**
	 * Returns a Document that can be used to build a DOM tree
	 * 
	 * @return the Document
	 * @throws ParserConfigurationException
	 *             if an exception occurs creating the document builder
	 */
	public static Document getDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	/**
	 * Makes a DOM document from the given string.
	 * 
	 * @param str
	 *            xml content
	 * @return a document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document makeDocument(String str)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		return docBuilder.parse(new InputSource(new StringReader(str)));
	}

	/**
	 * Serializes a XML document into a string - encoded in UTF8 format, with
	 * platform line separators.
	 * 
	 * @param doc
	 *            document to serialize
	 * @return the document as a string
	 * @throws TransformerException
	 *             if an unrecoverable error occurs during the serialization
	 * @throws IOException
	 *             if the encoding attempted to be used is not supported
	 */
	public static String serializeDocument(Document doc)
			throws TransformerException, IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();

		TransformerFactory factory = TransformerFactory.newInstance();

		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		DOMSource source = new DOMSource(doc);
		StreamResult outputTarget = new StreamResult(s);
		transformer.transform(source, outputTarget);

		return s.toString("UTF8"); //$NON-NLS-1$			
	}

	public static Node getUniqueChild(Node node) {
		final NodeList unitChildren = node.getChildNodes();
		for (int j = 0; j < unitChildren.getLength(); j++) {
			final Node child = unitChildren.item(j);
			if (child instanceof Element) {
				return child;
			}
		}
		throw PreferenceException.getInstance();
	}

	public static boolean getSimplifyProofPref() {
		return Platform.getPreferencesService().getBoolean(PLUGIN_ID,
				P_SIMPLIFY_PROOFS, false, null);
	}

	public static void initTacticPreferenceUpdater() {
		final IEclipsePreferences prefNode = InstanceScope.INSTANCE
				.getNode(PLUGIN_ID);
		prefNode.addPreferenceChangeListener(new BoolPrefUpdater());
	}

	/**
	 * Initialize from stored preferences.
	 */
	public static void init() {
		final IAutoPostTacticManager manager = EventBPlugin.getAutoPostTacticManager();
		
		final IEclipsePreferences defaultNode = DefaultScope.INSTANCE.getNode(PLUGIN_ID);
		final IEclipsePreferences node = InstanceScope.INSTANCE.getNode(PLUGIN_ID);

		final boolean defAutoEnable = defaultNode.getBoolean(P_AUTOTACTIC_ENABLE, DEFAULT_AUTO_ENABLE);
		final boolean autoTacticEnable = node.getBoolean(P_AUTOTACTIC_ENABLE, defAutoEnable);
		manager.getAutoTacticPreference().setEnabled(autoTacticEnable);
		
		final boolean defPostEnable = defaultNode.getBoolean(P_POSTTACTIC_ENABLE, DEFAULT_POST_ENABLE);
		final boolean postTacticEnable = node.getBoolean(P_POSTTACTIC_ENABLE, defPostEnable);
		manager.getPostTacticPreference().setEnabled(postTacticEnable);
	}

	private static final List<String> MOVED_PREFERENCES = asList(
			P_AUTOTACTIC_ENABLE, P_AUTOTACTIC_CHOICE, P_POSTTACTIC_ENABLE,
			P_POSTTACTIC_CHOICE, P_TACTICSPROFILES);

	private static void movePref(String key, IEclipsePreferences from,
			IEclipsePreferences to) {
		if (to.get(key, null) != null) {
			// do not override user setting
			return;
		}
		final String fromValue = from.get(key, null);
		if (fromValue != null) {
			to.put(key, fromValue);
			from.remove(key);
		}
	}

	private static void moveTacticPrefs(IEclipsePreferences from,
	IEclipsePreferences to) {
		for (String movedPref : MOVED_PREFERENCES) {
			movePref(movedPref, from, to);
		}
	}

	private static boolean mayRequireRestoration(IEclipsePreferences node) throws BackingStoreException {
			final String[] keys = node.keys();
			final List<String> nodekeys = new ArrayList<String>(asList(keys));
			return nodekeys.isEmpty() || !nodekeys.removeAll(MOVED_PREFERENCES);
	}

	/**
	 * Restores UI preferences into the given (core) preference node.
	 * 
	 * @param prefNode
	 *            a preference node
	 * @param force
	 *            <code>true</code> to force restoration and preference
	 *            synchronization, <code>false</code> otherwise
	 */
	public static void restoreFromUIIfNeeded(final IEclipsePreferences prefNode, boolean force) {
		try {
			if (!force && !mayRequireRestoration(prefNode)) {
				return;
			}
			final IProject project = getProject(prefNode);
			final IEclipsePreferences uiPrefNode = getUIPreference(project);

			moveTacticPrefs(uiPrefNode, prefNode);

			if (force || canSave(project)) {
				prefNode.sync();
				uiPrefNode.flush();
				uiPrefNode.sync();
			}
		} catch (BackingStoreException e) {
			Util.log(e, "while restoring UI preferences");
		}
	}

	private static boolean canSave(IProject project) {
		if (project == null) {
			return true;
		}
		final ISchedulingRule currentRule = Job.getJobManager().currentRule();
		if (currentRule == null) {
			return false;
		}
		return currentRule.contains(project);
	}

	private static IProject getProject(IEclipsePreferences prefNode) {
		final String absolutePath = prefNode.absolutePath();
		final String[] split = absolutePath.split("/");
		if (split[1].equals("project")) {
			final IWorkspace ws = ResourcesPlugin.getWorkspace();
			final IProject project = ws.getRoot().getProject(split[2]);
			return project;
		}
		return null;
	}

	private static IEclipsePreferences getUIPreference(IProject project) throws BackingStoreException {
		final IScopeContext scope;
		if (project == null) {
			scope = InstanceScope.INSTANCE;
		} else {
			scope = new ProjectScope(project);
		}
		return scope.getNode("org.eventb.ui");
	}
}
