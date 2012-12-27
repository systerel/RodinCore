/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

import static org.eventb.internal.core.preferences.PreferenceUtils.flatten;
import static org.eventb.internal.core.preferences.PreferenceUtils.parseString;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent and manipulate a string (e.g. a preference) which is
 * a list of elements.
 * 
 * @see IPrefElementTranslator
 * @since 2.1
 */
public class ListPreference<T> implements IPrefElementTranslator<List<T>> {

	// Separator for elements of a list
	private static final String LIST_ITEM_SEPARATOR = ",";

	private final IPrefElementTranslator<T> translator;

	public ListPreference(IPrefElementTranslator<T> translator) {
		this.translator = translator;
	}

	@Override
	public List<T> inject(String s) {
		final String[] e = parseString(s, LIST_ITEM_SEPARATOR);
		return asList(e);
	}

	@Override
	public String extract(List<T> list) {
		final List<String> strings = new ArrayList<String>();
		for (T t : list) {
			strings.add(translator.extract(t));
		}
		return flatten(strings, LIST_ITEM_SEPARATOR);
	}

	private List<T> asList(String[] toConvert) {
		final List<T> result = new ArrayList<T>();
		for (String elementStr : toConvert) {
			final T element = translator.inject(elementStr);
			if (element != null) {
				result.add(element);
			}
		}
		return result;
	}

}