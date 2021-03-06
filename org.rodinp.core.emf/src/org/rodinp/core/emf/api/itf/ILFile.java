/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.api.itf;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * @author Nicolas Beauger
 * @author Thomas Muller
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILFile {

	void load(final Map<?, ?> options) throws IOException;

	void reload();

	ILElement getRoot();

	boolean isEmpty();

	void save();

	void unloadResource();
	
	void addEContentAdapter(EContentAdapter adapter);

	void addAdapter(Adapter adapter);

	/**
	 * @since 1.1
	 */
	void removeEContentAdapter(EContentAdapter adapter);

	/**
	 * @since 1.1
	 */
	void removeAdapter(Adapter adapter);

}
