/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code extracted from class ElementTypeManager
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Item type management utils.
 * 
 * @author Laurent Voisin
 */
public class ItemTypeUtils {

	/* package */static <V> String[] getSortedIds(HashMap<String, V> map) {
		Set<String> idSet = map.keySet();
		String[] ids = idSet.toArray(new String[idSet.size()]);
		Arrays.sort(ids);
		return ids;
	}

	/* package */static void debug(String str) {
		System.out.println(str);
	}

}
