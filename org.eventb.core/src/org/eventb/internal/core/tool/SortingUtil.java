/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author Stefan Hallerstede
 *
 */
public class SortingUtil {
	
	protected static <V> String[] getSortedIds(Map<String, V> map) {
		Set<String> idSet = map.keySet();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		Arrays.sort(ids);
		return ids;
	}

}
