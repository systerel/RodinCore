/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.lightcore.adapters.dboperations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.rodinp.core.emf.lightcore.adapters.dboperations.messages"; //$NON-NLS-1$
	public static String elementOperation_db_error;
	public static String elementOperation_implicit_creation_error;
	public static String elementOperationType_addition_type;
	public static String elementOperationType_recalculateImplicit_type;
	public static String elementOperationType_reload_type;
	public static String elementOperationType_remove_type;
	public static String elementOperationType_reorder_type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String implicitCreationOperationError(String root,
			String message) {
		return bind(elementOperation_implicit_creation_error, root, message);
	}
	
	public static String dbOperationError(String error) {
		return bind(elementOperation_db_error, error);
	}
	
}
