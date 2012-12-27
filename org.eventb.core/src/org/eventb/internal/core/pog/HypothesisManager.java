/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class HypothesisManager extends State implements IHypothesisManager {

	@Override
	public String toString() {
		return getStateType().toString() + "[" + parentElement.getElementName() + "]";
	}

	@Override
	public Iterator<FreeIdentifier> iterator() {
		return identifiers.iterator();
	}

	/**
	 * Hypotheses are represented by predicate sets.
	 * Each predicate set may be associated with a type environment which is represented
	 * by a set of typed identifiers. When the predicate sets of this manager are created, the
	 * type environment associated with this manager is added to the first predicate set created
	 * (the one that is included in all other predicate sets of this manager). The identifiers
	 * of this manager can be accessed via the <code>Iterable</code> interface it implements.
	 * 
	 * @param identifier the free identifier to be added
	 * @throws CoreException if the hypthothesis manager is immutable
	 */
	public void addIdentifier(FreeIdentifier identifier) throws CoreException {
		assertMutable();
		identifiers.add(identifier);
	}

	public static String PRD_NAME_PREFIX = "PRD";
	private final IRodinElement parentElement;
	protected final IPORoot target;
	private final ISCPredicateElement[] predicateTable;
	private final String[] hypothesisNames;
	private final Hashtable<String, Integer> predicateMap;
	private final String rootHypName;
	private final String hypPrefix;
	private final String allHypName;
	private final String identHypName;
	private final HashSet<FreeIdentifier> identifiers;
	protected final boolean accurate;

	@Override
	public IRodinElement getParentElement() {
		return parentElement;
	}

	public HypothesisManager(
			IRodinElement parentElement, 
			IPORoot target,
			ISCPredicateElement[] predicateTable, 
			boolean accurate,
			String rootHypName, 
			String hypPrefix,
			String allHypName,
			String identHypName,
			int identifierHashSize) {
		this.parentElement = parentElement;
		this.target = target;
		this.rootHypName = rootHypName;
		this.hypPrefix = hypPrefix;
		this.allHypName = allHypName;
		this.identHypName = identHypName;
		this.predicateTable = predicateTable;
		this.accurate = accurate;
		hypothesisNames = new String[predicateTable.length];
		predicateMap = new Hashtable<String, Integer>(predicateTable.length * 4 / 3 + 1);
		identifiers = new HashSet<FreeIdentifier>(identifierHashSize * 4 / 3 + 1);
		
		for(int i=0; i<predicateTable.length; i++) {
			predicateMap.put(predicateTable[i].getElementName(), i);
		}
	}
	
	private String getFirstHypothesisName() {
		return (identifiers.size() > 0) ? identHypName : rootHypName;
	}
	
	private String getHypothesisName(int index) {
		if (hypothesisNames[index] == null)
			if (index == 0)
				hypothesisNames[index] = getFirstHypothesisName();
			else
				hypothesisNames[index] = hypPrefix + predicateTable[index-1].getElementName();
		return hypothesisNames[index];
	}

	@Override
	public IPOPredicateSet makeHypothesis(ISCPredicateElement element) throws CoreException {
		assertMutable();
		Integer index = predicateMap.get(element.getElementName());
		if (index == null)
			return null;
		return target.getPredicateSet(getHypothesisName(index));
	}

	// anticipate the predicate set in which the predicate will be located
	@Override
	public IPOPredicate getPredicate(ISCPredicateElement element) throws CoreException {
		if (!isImmutable())
			throw Util.newCoreException(Messages.pog_mutableHypothesisViolation);
		Integer index = predicateMap.get(element.getElementName());
		if (index == null)
			return null;
		for (int i=index+1; i< hypothesisNames.length; i++) {
			if (hypothesisNames[i] != null) {
				return target.getPredicateSet(hypothesisNames[i]).getPredicate(PRD_NAME_PREFIX + index);
			}
		}
		return target.getPredicateSet(allHypName).getPredicate(PRD_NAME_PREFIX + index);
	}

	public void createHypotheses(IProgressMonitor monitor) throws RodinDBException {
		
		if (identifiers.size() > 0)
			addIdentifiers(monitor);
		
		int previous = 0;
		String previousName = getFirstHypothesisName();
		
		// we start at index 1 because the root hypothesis set is created
		// by the layer above this hypothesis manager.
		// It is easier to keep the root hypothesis in the table because
		// it makes indices of hypotheses and predicates correspond.
		for (int i=1; i<predicateTable.length; i++) {
			
			if (hypothesisNames[i] == null)
				continue;
			else {
				addPredicateSet(i, hypothesisNames[i], previous, previousName, monitor);
				previous = i;
				previousName = hypothesisNames[i];
			}
		}
		
		addPredicateSet(predicateTable.length, allHypName, previous, previousName, monitor);
	
	}
	
	private void addIdentifiers(IProgressMonitor monitor) throws RodinDBException {
		IPOPredicateSet set = createPredicateSet(identHypName, rootHypName, monitor);
		for (FreeIdentifier identifier : identifiers) {
			String idName = identifier.getName();
			Type type = identifier.getType();
			IPOIdentifier poIdentifier = set.getIdentifier(idName);
			poIdentifier.create(null, monitor);
			poIdentifier.setType(type, monitor);
		}
	}

	private void addPredicateSet(
			int current, 
			String currentName, 
			int previous, 
			String previousName, 
			IProgressMonitor monitor) throws RodinDBException {
		IPOPredicateSet set = createPredicateSet(currentName, previousName, monitor);
		for (int k=previous; k<current; k++) {
			IPOPredicate predicate = set.getPredicate(PRD_NAME_PREFIX + previous++);
			predicate.create(null, monitor);
			predicate.setPredicateString(predicateTable[k].getPredicateString(), monitor);
			predicate.setSource(
					((ITraceableElement) predicateTable[k]).getSource(), monitor);
		}
	}

	private IPOPredicateSet createPredicateSet(
			String name, 
			String previousName, 
			IProgressMonitor monitor) throws RodinDBException {
		IPOPredicateSet set = target.getPredicateSet(name);
		set.create(null, monitor);
		set.setParentPredicateSet(target.getPredicateSet(previousName), monitor);
		return set;
	}
	
	@Override
	public IPOPredicateSet getFullHypothesis() {
		return target.getPredicateSet(allHypName);
	}

	@Override
	public IPOPredicateSet getRootHypothesis() {
		return target.getPredicateSet(rootHypName);
	}

}
