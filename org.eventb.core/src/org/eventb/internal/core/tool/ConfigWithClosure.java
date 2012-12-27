/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - relaxed assertion into a test
 *******************************************************************************/
package org.eventb.internal.core.tool;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConfigWithClosure<T> extends Config {
	
	private List<T> closure;

	public ConfigWithClosure(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
	}
	
	protected List<T> newClosure() {
		return new LinkedList<T>();
	}

	public List<T> computeClosure(Map<String, ? extends ConfigWithClosure<T>> configs) {
		if (closure == null) {
			closure = newClosure();
			for (String string : getIncluded()) {
				ConfigWithClosure<T> c = configs.get(string);
				if (c == null)
					continue;
				List<T> included = c.computeClosure(configs);
				for (T t : included) {
					if (closure.contains(t))
						continue;
					closure.add(t);
				}
			}
		}
		return closure;
	}

}
