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

import org.eventb.core.IPOAnyPredicate;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOModifiedPredicate;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPRStatus.Status;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class PRUtil {

	// Functions to read & update status in PR files
	
	public static Map<String, IProverSequent> readPOs(IPRFile prFile) throws RodinDBException {
		// read in the global type environment
		ITypeEnvironment globalTypeEnv = Lib.ff.makeTypeEnvironment();
		addIdents(prFile.getIdentifiers(), globalTypeEnv);
		
		Map<String, IProverSequent> result 
		= new HashMap<String, IProverSequent>(prFile.getSequents().length);
		for (IPRSequent prSeq:prFile.getSequents()){
			String name = prSeq.getName();
			ITypeEnvironment typeEnv = globalTypeEnv.clone();
			addIdents(prSeq.getIdentifiers(),typeEnv);
			Set<Hypothesis> hypotheses = readHypotheses(prSeq.getHypothesis(),typeEnv);
			Set<Hypothesis> localHypotheses = readLocalHypotheses(prSeq.getHypothesis(),typeEnv);
			Predicate goal = readPredicate(prSeq.getGoal(),typeEnv);
			IProverSequent seq = Lib.makeSequent(typeEnv,hypotheses,goal);
			seq = seq.selectHypotheses(localHypotheses);
			// System.out.println(name+" : "+seq);
			result.put(name,seq);
		}
		return result;
	}
	
	public static IProofTree makeProofTree(IPRSequent prSeq) throws RodinDBException{
		ITypeEnvironment typeEnv = Lib.ff.makeTypeEnvironment();
		IPRFile prFile = (IPRFile) prSeq.getOpenable();
		addIdents(prFile.getIdentifiers(), typeEnv);
		addIdents(prSeq.getIdentifiers(),typeEnv);
		Set<Hypothesis> hypotheses = readHypotheses(prSeq.getHypothesis(),typeEnv);
		Set<Hypothesis> localHypotheses = readLocalHypotheses(prSeq.getHypothesis(),typeEnv);
		Predicate goal = readPredicate(prSeq.getGoal(),typeEnv);
		IProverSequent seq = Lib.makeSequent(typeEnv,hypotheses,goal);
		seq = seq.selectHypotheses(localHypotheses);
		return SequentProver.makeProofTree(seq);
	}
	
	public static void updateStatus(IPRSequent prSeq, IProofTree pt) throws RodinDBException{
		IProofTree oldPt = makeProofTree(prSeq);
		if (Lib.identical(oldPt.getSequent(),pt.getSequent())) {
			if (pt.isDischarged()){
				prSeq.getStatus().setContents("DISCHARGED");
			} else
			{
				prSeq.getStatus().setContents("PENDING");
			}
			return ;
		}
		// TODO maybe throw a core exception here
		else return;
	}
	
	
	
	
	public static void updateStatus(IPRFile prFile, String poName, Status status) throws RodinDBException{
		IPRSequent[] prSeqs = prFile.getSequents();
		for (IPRSequent prSeq : prSeqs){
			if (prSeq.getName().equals(poName)){
				prSeq.getStatus().setContents(status.toString());
			}
		}
		prFile.save(null, false);
	}
	
	public static Map<String, Status> readStatus(IPRFile prFile) throws RodinDBException {
		Map<String, Status> result 
		= new HashMap<String, Status>(prFile.getSequents().length);
		
		for (IPRSequent prSeq:prFile.getSequents()){
			result.put(prSeq.getName(),prSeq.getStatus().getStatus());
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
		for (IPOAnyPredicate poAnyPred:poHyp.getLocalHypothesis()){
			result.add(new Hypothesis(readPredicate(poAnyPred,typeEnv)));
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


	private static Predicate readPredicate(IPOAnyPredicate poAnyPred, ITypeEnvironment typeEnv) throws RodinDBException {
		if (poAnyPred instanceof IPOPredicate) {
			IPOPredicate poPred = (IPOPredicate) poAnyPred;
			Predicate pred =  Lib.parsePredicate(poPred.getContents());
			// System.out.println("Pred : " + poPred.getContents() +" Parsed : "+ pred);
			assert pred != null;
			boolean wellTyped = Lib.isWellTyped(pred,typeEnv);
			assert wellTyped;
			return pred;
		}
		if (poAnyPred instanceof IPOModifiedPredicate) {
			IPOModifiedPredicate poModPred = (IPOModifiedPredicate) poAnyPred;
			Predicate pred = readPredicate(poModPred.getPredicate(),typeEnv);
			assert pred != null;
			boolean wellTyped = Lib.isWellTyped(pred,typeEnv);
			assert wellTyped;
			Assignment assignment = Lib.parseAssignment(poModPred.getSubstitution());
			assert assignment != null;
			wellTyped = Lib.isWellTyped(assignment,typeEnv);
			assert wellTyped;
			if (! (assignment instanceof BecomesEqualTo)) throw new AssertionError("PO file ill formed");
			Predicate substPred = Lib.applyDeterministicAssignment((BecomesEqualTo)assignment,pred);
			wellTyped = Lib.isWellTyped(substPred,typeEnv);
			assert wellTyped;
			// System.out.println("modifiedPred : " + substPred);
			return substPred;
		}
		
		throw new AssertionError("Illegal type of predicate.");
	}


	private static void addIdents(IPOIdentifier[] poIdents, ITypeEnvironment typeEnv) throws RodinDBException {
		for (IPOIdentifier poIdent: poIdents){
			String name = poIdent.getName();
			Type type = Lib.parseType(poIdent.getType());
			assert (name!=null && type !=null);
			typeEnv.addName(name,type);
		}
	}
	

}
