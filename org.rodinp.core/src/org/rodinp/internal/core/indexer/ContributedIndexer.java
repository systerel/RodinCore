/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.internal.core.util.Util;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContributedIndexer extends IndexerElement {

	private final IConfigurationElement element;
	private IIndexer indexer;

	public ContributedIndexer(
			IConfigurationElement element, String indexerId) {
		super(indexerId);
		this.element = element;
		this.indexer = null;
	}

	@Override
	public IIndexer getIndexer() {
		if (indexer == null) {
			try {
				if (!element.isValid()) {
					processError(null);
					return null;
				}
				final Object instance = element
						.createExecutableExtension("class");
				if (!(instance instanceof IIndexer)) {
					processError(null);
					return null;
				}
				indexer = (IIndexer) instance;
			} catch (Exception e) {
				processError(e);
				return null;
			}
		}
		return indexer;
	}

	private void processError(Exception e) {
		Util.log(e, ("Unable to load indexer class " + indexerId));
	}


}
