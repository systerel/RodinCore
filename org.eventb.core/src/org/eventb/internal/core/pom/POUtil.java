/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.pom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class POUtil {

	// Functions to read PO files
	
	public static Map<String, IProverSequent> readPOs(IPOFile poFile) throws RodinDBException {
		// read in the global type environment
		ITypeEnvironment globalTypeEnv = Lib.ff.makeTypeEnvironment();
		addIdents(poFile.getIdentifiers(), globalTypeEnv);
		
		Map<String, IProverSequent> result 
		= new HashMap<String, IProverSequent>(poFile.getSequents().length);
		for (IPOSequent poSeq:poFile.getSequents()){
			String name = poSeq.getName();
			ITypeEnvironment typeEnv = globalTypeEnv.clone();
			addIdents(poSeq.getIdentifiers(),typeEnv);
			Set<Hypothesis> hypotheses = readHypotheses(poSeq.getHypothesis(),typeEnv);
			Set<Hypothesis> localHypotheses = readLocalHypotheses(poSeq.getHypothesis(),typeEnv);
			Predicate goal = readPredicate(poSeq.getGoal(),typeEnv);
			IProverSequent seq = Lib.makeSequent(typeEnv,hypotheses,goal);
			seq = seq.selectHypotheses(localHypotheses);
			// System.out.println(name+" : "+seq);
			result.put(name,seq);
		}
		return result;
	}

	
	private static Set<Hypothesis> readHypotheses(IPOHypothesis poHyp, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		result.addAll(readGlobalHypotheses(poHyp,typeEnv));
		result.addAll(readLocalHypotheses(poHyp,typeEnv));
		return result;
	}
	
	private static Set<Hypothesis> readGlobalHypotheses(IPOHypothesis poHyp, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		result.addAll(readPredicates(poHyp.getGlobalHypothesis(),typeEnv));
		return result;
	}
	
	private static Set<Hypothesis> readLocalHypotheses(IPOHypothesis poHyp, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (IPOPredicate poPred : poHyp.getLocalHypothesis()){
			result.add(new Hypothesis(readPredicate(poPred,typeEnv)));
		}
		return result;
	}


	private static Set<Hypothesis> readPredicates(IPOPredicateSet poPredSet, ITypeEnvironment typeEnv) throws RodinDBException {
		Set<Hypothesis> result = new HashSet<Hypothesis>();
		for (IPOPredicate poPred:poPredSet.getPredicates()){
			result.add(new Hypothesis(readPredicate(poPred,typeEnv)));
		}
		if (poPredSet.getPredicateSet() != null) 
			result.addAll(readPredicates(poPredSet.getPredicateSet(),typeEnv));
		return result;
	}


	private static Predicate readPredicate(IPOPredicate poPred, ITypeEnvironment typeEnv) throws RodinDBException {
			Predicate pred =  Lib.parsePredicate(poPred.getContents());
			// System.out.println("Pred : " + poPred.getContents() +" Parsed : "+ pred);
			assert pred != null;
			boolean wellTyped = Lib.isWellTyped(pred,typeEnv);
			// if (!wellTyped) System.out.println("Pred : " + poPred.getContents() +" NOT WELL TYPED");
			assert wellTyped;
			return pred;
	}


	private static void addIdents(IPOIdentifier[] poIdents, ITypeEnvironment typeEnv) throws RodinDBException {
		for (IPOIdentifier poIdent: poIdents){
			String name = poIdent.getName();
			Type type = Lib.parseType(poIdent.getType());
			assert (name!=null && type !=null);
			typeEnv.addName(name,type);
		}
	}
	
	
	// Functions to write PO files
	
	public static void addTypes(IInternalParent parent, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInternalElement element = parent.createInternalElement(IPOIdentifier.ELEMENT_TYPE, names[i], null, null);
			element.setContents(types[i]);
		}
	}
	
	public static void addPredicateSet(IRodinFile file, String name, String[] predicates, String parentSet) throws RodinDBException {
		IPOPredicateSet parent = (IPOPredicateSet) file.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, name, null, null);
		if(parentSet != null)
			parent.setContents(parentSet);
		for(int i=0; i<predicates.length; i++) {
			IInternalElement element = parent.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, null);
			element.setContents(predicates[i]);
		}
	}
	
	public static void addSequent(IRodinFile file, 
			String poName,
			String globalHypothesis, 
			String[] localNames,
			String[] localTypes,
			String[] localHypothesis,
			String goal) throws RodinDBException {
		IPOSequent sequent = (IPOSequent) file.createInternalElement(IPOSequent.ELEMENT_TYPE, poName, null, null);
		addTypes(sequent, localNames, localTypes);
		addHypothesis(sequent, globalHypothesis, localHypothesis);
		addPredicate(sequent,goal);
	}
	
	private static void addHypothesis(IPOSequent sequent, 
			String globalHypothesis, 
			String[] localHypothesis) throws RodinDBException {
		IPOHypothesis hypothesis = (IPOHypothesis) sequent.createInternalElement(IPOHypothesis.ELEMENT_TYPE, null, null, null);
		hypothesis.setContents(globalHypothesis);
		for(int i=0; i<localHypothesis.length; i++) {
			addPredicate(hypothesis, localHypothesis[i]);
		}
	}
	
	private static void addPredicate(IInternalParent internalParent, String predicate) throws RodinDBException {
		IInternalElement element = internalParent.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, null);
		element.setContents(predicate);
	}

}
