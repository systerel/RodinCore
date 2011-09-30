/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IPredicateExtension;

/**
 * A basic implementation for an extended predicate matcher.
 * 
 * <p> This class is parameterised on the type of the extension of the operator. Contributors
 * to the extension point '<code>org.eventb.core.pm.extendedPredicateMatcher</code>' should extend
 * this class.
 * <p> This class is intended to be extended by clients.
 * @author maamria
 * @since 1.0
 *
 */
public abstract class ExtendedPredicateMatcher<P extends IPredicateExtension> extends PredicateMatcher<ExtendedPredicate> {

	private Class<P> extensionClass;
	
	public ExtendedPredicateMatcher(Class<P> extensionClass) {
		super(ExtendedPredicate.class);
		this.extensionClass = extensionClass;
	}

	@Override
	protected ExtendedPredicate getPredicate(Predicate p) {
		return (ExtendedPredicate) p;
	}
	
	public Class<P> getExtensionClass(){
		return extensionClass;
	}
}
