/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.state.IHypothesisManager;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class HypothesisManager implements IHypothesisManager {

	public Iterator<FreeIdentifier> iterator() {
		return identifiers.iterator();
	}

	public void addIdentifier(FreeIdentifier identifier) {
		identifiers.add(identifier);
	}

	public static String PRD_NAME_PREFIX = "PRD";
	private final IRodinElement parentElement;
	private final ISCPredicateElement[] predicateTable;
	private final String[] hypothesisNames;
	private final Hashtable<String, Integer> predicateMap;
	private final String rootHypName;
	private final String hypPrefix;
	private final String allHypName;
	private final HashSet<FreeIdentifier> identifiers;

	public IRodinElement getParentElement() {
		return parentElement;
	}

	public HypothesisManager(
			IRodinElement parentElement, 
			ISCPredicateElement[] predicateTable, 
			String rootHypName, 
			String hypPrefix,
			String allHypName,
			int identifierHashSize) {
		this.parentElement = parentElement;
		this.rootHypName = rootHypName;
		this.hypPrefix = hypPrefix;
		this.allHypName = allHypName;
		this.predicateTable = predicateTable;
		hypothesisNames = new String[predicateTable.length];
		predicateMap = new Hashtable<String, Integer>(predicateTable.length * 4 / 3 + 1);
		identifiers = new HashSet<FreeIdentifier>(identifierHashSize * 4 / 3 + 1);
		
		for(int i=0; i<predicateTable.length; i++) {
			predicateMap.put(predicateTable[i].getElementName(), i);
		}
	}

	public IPOPredicateSet getHypothesis(IPOFile file, ISCPredicateElement element, IProgressMonitor monitor) throws RodinDBException {
		Integer index = predicateMap.get(element.getElementName());
		if (index == null)
			return null;
		if (hypothesisNames[index] == null)
			if (index == 0)
				hypothesisNames[index] = rootHypName;
			else
				hypothesisNames[index] = hypPrefix + predicateTable[index-1].getElementName();
		return file.getPredicateSet(hypothesisNames[index]);
	}

	public void createHypotheses(IPOFile file, IProgressMonitor monitor) throws RodinDBException {
		
		int previous = 0;
		String previousName = rootHypName;
		
		int index = 0;
		
		// we start at index 1 because the root hypothesis set is created
		// by the layer above this hypothesis manager.
		// It is easier to keep the root hypothesis in the table because
		// it makes indices of hypotheses and predicates correspond.
		for (int i=1; i<predicateTable.length; i++) {
			
			if (hypothesisNames[i] == null)
				continue;
			else {
				index = 
					addPredicateSet(file, 
							hypothesisNames[i], previous, previousName, index, i, monitor);
			}
		}
		
		addPredicateSet(file, 
				allHypName, previous, previousName, index, predicateTable.length, monitor);
	
	}

	private int addPredicateSet(
			IPOFile file, 
			String name, 
			int previous, 
			String previousName, 
			int index, 
			int current, 
			IProgressMonitor monitor) throws RodinDBException {
		IPOPredicateSet set = file.getPredicateSet(name);
		set.create(null, monitor);
		set.setParentPredicateSet(file.getPredicateSet(previousName), monitor);
		if (index == 0)
		{
			for (FreeIdentifier identifier : identifiers) {
				String idName = identifier.getName();
				Type type = identifier.getType();
				IPOIdentifier poIdentifier = set.getIdentifier(idName);
				poIdentifier.create(null, monitor);
				poIdentifier.setType(type, monitor);
			}
		}
		for (int k=previous; k<current; k++) {
			IPOPredicate predicate = set.getPredicate(PRD_NAME_PREFIX + index++);
			predicate.create(null, monitor);
			predicate.setPredicateString(predicateTable[k].getPredicateString(), monitor);
			predicate.setSource(
					((ITraceableElement) predicateTable[k]).getSource(), monitor);
		}
		return index;
	}
	
	public IPOPredicateSet getFullHypothesis(IPOFile file) throws RodinDBException {
		return file.getPredicateSet(allHypName);
	}

	public IPOPredicateSet getRootHypothesis(IPOFile file) throws RodinDBException {
		return file.getPredicateSet(rootHypName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IHypothesisManager#getManagedPredicates()
	 */
	public List<ISCPredicateElement> getManagedPredicates() {
		return new ArrayList<ISCPredicateElement>(Arrays.asList(predicateTable));
	}


}
