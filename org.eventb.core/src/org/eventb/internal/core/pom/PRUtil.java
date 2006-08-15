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

import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.IPRReasoningStep;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.basis.PRProofRule;
import org.eventb.core.basis.PRProofTree;
import org.eventb.core.basis.PRProofTreeNode;
import org.eventb.core.basis.PRReasoningStep;
import org.eventb.core.prover.IProofDependencies;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.IReasoner;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.rules.ProofRule;
import org.eventb.core.prover.rules.ProofTreeNode;
import org.eventb.core.prover.rules.ReasoningStep;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.tactics.BasicTactics;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
		for (IPRSequent prSeq : (IPRSequent[]) prFile.getSequents()){
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
	
	public static IProverSequent makeSequent(IPRSequent prSeq) throws RodinDBException{
		ITypeEnvironment typeEnv = Lib.ff.makeTypeEnvironment();
		IPRFile prFile = (IPRFile) prSeq.getOpenable();
		addIdents(prFile.getIdentifiers(), typeEnv);
		addIdents(prSeq.getIdentifiers(),typeEnv);
		Set<Hypothesis> hypotheses = readHypotheses(prSeq.getHypothesis(),typeEnv);
		Set<Hypothesis> localHypotheses = readLocalHypotheses(prSeq.getHypothesis(),typeEnv);
		Predicate goal = readPredicate(prSeq.getGoal(),typeEnv);
		IProverSequent seq = Lib.makeSequent(typeEnv,hypotheses,goal);
		seq = seq.selectHypotheses(localHypotheses);
		return seq;
	}
	
	public static IProofTree makeInitialProofTree(IPRSequent prSeq) throws RodinDBException{		
		IProofTree proofTree = SequentProver.makeProofTree(makeSequent(prSeq));		
		return proofTree;
	}
	
	public static IProofTree rebiuldProofTree(IPRSequent prSeq) throws RodinDBException{		
		IProofTree proofTree = makeInitialProofTree(prSeq);		
		IProofTreeNode root = proofTree.getRoot();
		IPRProofTreeNode prRoot = prSeq.getProofTree().getRoot();
		ReplayHints replayHints = new ReplayHints();
		if (prRoot != null) rebuild(root,prRoot,replayHints);
		return proofTree;
	}
	
	// TODO:
	// Return value true if there may be a change in the proof tree node from the
	// stored DB version
	public static void rebuild(IProofTreeNode node,IPRProofTreeNode prNode, ReplayHints replayHints) throws RodinDBException{
		// System.out.println("trying rebuild"+node.getSequent());
		node.setComment(prNode.getComment());
		IPRProofRule prRule = prNode.getRule();
		// Check if this is an open node
		if (prRule == null) return;
		
		// Try to replay the rule
		if (prRule.getRuleID().equals("reasoningStep")){
			IRodinElement[] prReasoningSteps = prRule.getChildrenOfType(IPRReasoningStep.ELEMENT_TYPE);
			assert prReasoningSteps.length == 1;
			PRReasoningStep prReasoningStep = (PRReasoningStep) prReasoningSteps[0];
			
			ReasonerOutputSucc reuseReasonerOutput = prReasoningStep.getReasonerOutput();
			reuseReasonerOutput.display = reuseReasonerOutput.display + ".";
			IReasoner reasoner = reuseReasonerOutput.generatedBy;
			// uninstalled reasoner
			assert reasoner != null;
			IReasonerInput reasonerInput = reuseReasonerOutput.generatedUsing;
			
			// choose between reuse and replay
			boolean reuseSuccessfull = false;
			// if there are replay hints do not even try a reuse
			if (replayHints.isEmpty())
			{
				// see if reuse works
				Object error = BasicTactics.reasonerTac(reuseReasonerOutput).apply(node);
				reuseSuccessfull = (error == null);
			}
			
			ReasonerOutputSucc replayReasonerOutputSucc = null;
			
			if (! reuseSuccessfull)
			{	// reuse failed
				// try replay
				replayHints.applyHints(reasonerInput);
				ReasonerOutput replayReasonerOutput = reasoner.apply(node.getSequent(),reasonerInput, null);
				if ((replayReasonerOutput != null) && 
						((replayReasonerOutput instanceof ReasonerOutputSucc))){
					// reasoner successfully generated something
					// compare replayReasonerOutput and reuseReasonerOutput
					// and generate hints for continuing the proof
					replayReasonerOutputSucc =
						(ReasonerOutputSucc) replayReasonerOutput;
					BasicTactics.reasonerTac(replayReasonerOutputSucc).apply(node);
				}
				
				// BasicTactics.reasonerTac(reasoner,reasonerInput).apply(node);
			}	
		
		// Check if rebuild for this node was succesfull
		if (! node.hasChildren()) return;
		// System.out.println("rebuild successful! ");
		IPRProofTreeNode[] prChildren = prNode.getChildProofTreeNodes();
		assert prChildren != null;
		IProofTreeNode[] children = node.getChildren();
		assert children != null;
		
		// Maybe check if the node has the same number of children as the prNode
		// it may be smart to replay anyway, but generate a warning.
		if (children.length != prChildren.length) return;
		
		// run recursively for each child
		for (int i = 0; i < children.length; i++) {
			ReplayHints newReplayHints = replayHints;
			if (replayReasonerOutputSucc != null)
			{
				newReplayHints = replayHints.clone();
				newReplayHints.addHints(reuseReasonerOutput.anticidents[i],replayReasonerOutputSucc.anticidents[i]);
			}
			rebuild(children[i],prChildren[i],newReplayHints);
		}
		}
	}
	
	public static void updateProofTree(IPRSequent prSeq, IProofTree pt) throws RodinDBException{
		// IProofTree oldPt = makeProofTree(prSeq);
		IProverSequent oldProverSeq = makeSequent(prSeq);
		boolean broken;
		if (! Lib.identical(oldProverSeq, pt.getSequent())) {
			// The sequent changed in the file
			// TODO maybe throw a core exception here
			broken = true;
			System.out.println("Warning: Proof and Proof Obligation do not match");
		}
		else broken = false;
		
		// remove the previous proof
		if (prSeq.getProofTree().hasChildren())
		prSeq.getRodinDB().delete(prSeq.getProofTree().getChildren(),true,null);

		// Write out the proof tree dependencies
		IProofDependencies proofDependencies = pt.getProofDependencies();
		
		((IPRPredicate)(prSeq.getProofTree().createInternalElement(
				IPRPredicate.ELEMENT_TYPE,"goal",null,null))).
				setPredicate(proofDependencies.getGoal());
		((IPRPredicateSet)(prSeq.getProofTree().createInternalElement(
				IPRPredicateSet.ELEMENT_TYPE,"usedHypotheses",null,null))).
				setPredicateSet(Hypothesis.Predicates(proofDependencies.getUsedHypotheses()));
		((IPRTypeEnvironment)(prSeq.getProofTree().createInternalElement(
				IPRTypeEnvironment.ELEMENT_TYPE,"usedFreeIdentifiers",null,null))).
				setTypeEnvironment(proofDependencies.getUsedFreeIdents());
		((IPRTypeEnvironment)(prSeq.getProofTree().createInternalElement(
				IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers",null,null))).
				setTypeEnvironment(proofDependencies.getIntroducedFreeIdents());
		
		// Write out the proof tree
		writeOutProofTreeNode((ProofTreeNode) pt.getRoot(),0,(InternalElement) prSeq.getProofTree());
		
		// Update the status
		int confidence = pt.getConfidence();
		((PRProofTree)prSeq.getProofTree()).setConfidence(confidence);
			
		// set proof validity
		prSeq.setProofBroken(broken);
		
	}
	
	public static void writeOutRule (ProofRule rule,IPRProofTreeNode parent) throws RodinDBException{
		
		if (rule instanceof ReasoningStep) {
			IPRProofRule prRule = (IPRProofRule)
				parent.createInternalElement(
					PRProofRule.ELEMENT_TYPE,
					"reasoningStep",
					null,null);
			
			ReasoningStep reasoningStep = (ReasoningStep) rule;
			ReasonerOutputSucc reasonerOutput = reasoningStep.reasonerOutput;
			
			IPRReasoningStep prReasoningStep = 
				(IPRReasoningStep)
				prRule.createInternalElement(
					IPRReasoningStep.ELEMENT_TYPE,
					reasonerOutput.generatedBy.getReasonerID(),
					null,null);		
			prReasoningStep.setReasonerOutput(reasonerOutput);
		}
	}
	
	public static void writeOutProofTreeNode (ProofTreeNode proofTreeNode,int childNumber,InternalElement parent) throws RodinDBException{
		assert (proofTreeNode != null);
		IPRProofTreeNode prProofTreeNode = (IPRProofTreeNode)
			parent.createInternalElement(PRProofTreeNode.ELEMENT_TYPE,Integer.toString(childNumber),null,null);
		
		prProofTreeNode.setComment(proofTreeNode.getComment());
		
		if (proofTreeNode.isOpen()) return;
		
		writeOutRule(proofTreeNode.getRule(),prProofTreeNode);
		
		ProofTreeNode[] proofTreeNodeChildren = proofTreeNode.getChildren();
		for (int i = 0; i < proofTreeNodeChildren.length; i++) {
			writeOutProofTreeNode(proofTreeNodeChildren[i],i,(InternalElement) prProofTreeNode);
		}
		
	}
	
//	
//	public static Map<String, Status> readStatus(IPRFile prFile) throws RodinDBException {
//		Map<String, Status> result 
//		= new HashMap<String, Status>(prFile.getSequents().length);
//		
//		for (IPRSequent prSeq : (IPRSequent[]) prFile.getSequents()){
//			result.put(prSeq.getName(),prSeq.getProofTree().getStatus());
//		}
//		return result;
//	}
	
//	public static Map<String, Integer> readConfidence(IPRFile prFile) throws RodinDBException {
//		Map<String, Integer> result 
//		= new HashMap<String, Integer>(prFile.getSequents().length);
//		
//		for (IPRSequent prSeq : (IPRSequent[]) prFile.getSequents()){
//			result.put(prSeq.getName(),prSeq.getProofTree().getConfidence());
//		}
//		return result;
//	}

	
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
			boolean wellTyped = Lib.typeCheckClosed(pred,typeEnv);
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

	

}
