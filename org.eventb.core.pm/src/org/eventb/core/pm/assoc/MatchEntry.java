/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.List;

import org.eventb.core.ast.Formula;

/**
 * Match entry implementation.
 * 
 * <p> A match entry stores the list of matches for a particular indexed pattern.
 * 
 * @author maamria
 *
 */
public class MatchEntry<F extends Formula<F>> {

	private IndexedFormula<F> indexedPattern;
	private List<Match<F>> matches;
	
	public MatchEntry(IndexedFormula<F> indexedPattern, List<Match<F>> matches){
		this.indexedPattern = indexedPattern;
		this.matches = matches;
	}
	
	/**
	 * Returns the rank (i.e., number of matches) of the entry.
	 * @return the rank
	 */
	public int getRank(){
		return matches.size();
	}
	
	/**
	 * Returns the indexed pattern whose matches this entry is holding.
	 * @return the indexed pattern
	 */
	public IndexedFormula<F> getIndexedPattern() {
		return indexedPattern;
	}
	
	/**
	 * Returns the list of matches stored in this entry.
	 * @return the list of matches
	 */
	public List<Match<F>> getMatches() {
		return matches;
	}
	
	@Override
	public String toString() {
		return "MatchEntry{" + indexedPattern.toString() + "=>" + matches+"}";
	}
}
