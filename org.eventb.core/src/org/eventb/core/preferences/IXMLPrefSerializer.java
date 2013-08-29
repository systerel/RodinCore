/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

import org.eventb.core.preferences.autotactics.IInjectLog;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Common protocol for xml preference translators.
 * 
 * @author Nicolas Beauger
 * 
 * @param <T>
 *            the type of preference to serialize / deserialize
 * @since 2.3
 */
public interface IXMLPrefSerializer<T> {

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
	void put(IPrefMapEntry<T> pref, Document doc, Node parent);

	/**
	 * Deserializes the given node.
	 * 
	 * @param n
	 *            a node
	 * @param log
	 *            a log to record warnings about partially loaded nodes
	 * @return the deserialized preference
	 * @throws PreferenceException
	 *             if deserialization failed
	 * @since 3.0
	 */
	IPrefMapEntry<T> get(Node n, IInjectLog log) throws PreferenceException;

	/**
	 * Replaces the reference placeholders, contained in the given preference,
	 * by actual references obtained from the given map.
	 * 
	 * @param pref
	 *            a preference
	 * @param map
	 *            a preference map
	 * @param log
	 *            a log to record warnings about unresolved references
	 * @since 3.0
	 */
	void resolveReferences(IPrefMapEntry<T> pref, CachedPreferenceMap<T> map, IInjectLog log);
}