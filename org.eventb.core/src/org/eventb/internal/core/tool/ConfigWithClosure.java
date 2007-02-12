/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public ConfigWithClosure(IConfigurationElement configElement) {
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
				assert c != null;
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
