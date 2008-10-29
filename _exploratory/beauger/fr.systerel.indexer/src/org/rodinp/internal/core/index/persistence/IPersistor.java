/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.persistence;

import java.io.File;

import org.rodinp.internal.core.index.PerProjectPIM;


/**
 * @author Nicolas Beauger
 *
 */
public interface IPersistor {

	void save(PerProjectPIM pppim, File file);
	
	void restore(File file, PerProjectPIM pppim);
	
}
