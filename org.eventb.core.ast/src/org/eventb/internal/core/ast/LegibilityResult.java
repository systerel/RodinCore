/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;

/**
 * This class represents a result of a well-formedness check on a formula.
 * <p>
 * It contains a list of already encountered free identifiers and a list of already
 * encountered bound identifiers in the current formula. 
 * 
 * @author François Terrier
 *
 */
public class LegibilityResult extends AbstractResult {
	
	// list of the encountered free and bound variables
	private final Map<String, FreeIdentifier> freeIdents;
	private final Map<String, BoundIdentDecl> boundIdentDecls;
	
	/**
	 * 
	 */
	public LegibilityResult(Collection<FreeIdentifier> context) {
		this.boundIdentDecls = new HashMap<String, BoundIdentDecl>();
		if (context == null) {
			this.freeIdents = new HashMap<String, FreeIdentifier>();
		} else {
			this.freeIdents = new HashMap<String, FreeIdentifier>(context.size());
			for (FreeIdentifier ident: context) {
				this.freeIdents.put(ident.getName(), ident);
			}
		}
	}

	/**
	 * Constructor used to make a copy of the given result.
	 */
	public LegibilityResult(LegibilityResult other) {
		this.freeIdents = new HashMap<String, FreeIdentifier>(other.freeIdents);
		this.boundIdentDecls = new HashMap<String, BoundIdentDecl>(
				other.boundIdentDecls);

		for (ASTProblem problem : other.getProblems()) {
			addProblem(problem);
		}
	}
	
	/**
	 * Returns <code>true</code> if this result contains the given name in the
	 * free identifiers collected so far.
	 * 
	 * @param name
	 *            identifier name to search through free identifiers
	 * @return <code>true</code> if this result contains the given name
	 */
	public boolean hasFreeIdent(String name) {
		return freeIdents.containsKey(name);
	}
	
	
	/**
	 * Returns <code>true</code> if this result contains the given name in the
	 * bound identifier declarations collected so far.
	 * 
	 * @param name
	 *            identifier name to search through bound identifier declarations.
	 * @return <code>true</code> if this result contains the given name
	 */
	public boolean hasBoundIdentDecl(String name) {
		return boundIdentDecls.containsKey(name);
	}
	
	
	/**
	 * Returns the free identifier with the given name.
	 * <p>
	 * Method {@link LegibilityResult#hasFreeIdent(String)} can be used
	 * to check the existence of that free identifier.
	 * </p>
	 * 
	 * @param name
	 *            identifier name to search
	 * @return the free identifier with the given name or <code>null</code> if
	 *         not found.
	 */
	public FreeIdentifier getExistingFreeIdentifier(String name) {
		return freeIdents.get(name);
	}
	
	/**
	 * Returns the bound identifier declaration with the given name.
	 * <p>
	 * Method {@link LegibilityResult#hasBoundIdentDecl(String)} can be used to
	 * check the existence of that bound identifier declaration.
	 * </p>
	 * 
	 * @param name
	 *            identifier name to search
	 * @return the bound identifier declaration with the given name or
	 *         <code>null</code> if not found.
	 */
	public BoundIdentDecl getExistingBoundIdentDecl(String name) {
		return boundIdentDecls.get(name);
	}

	/**
	 * Adds the given free identifier to the list of known free identifiers
	 * 
	 * @param ident
	 *            a free identifier to add
	 */
	public void addFreeIdent(FreeIdentifier ident) {
		freeIdents.put(ident.getName(), ident);
	}
	
	/**
	 * Adds the given bound identifier declaration to the list of known bound
	 * identifier declarations
	 * 
	 * @param ident
	 *            a bound identifier declaration to add
	 */
	public void addBoundIdentDecl(BoundIdentDecl ident) {
		boundIdentDecls.put(ident.getName(), ident);
	}
	
}
