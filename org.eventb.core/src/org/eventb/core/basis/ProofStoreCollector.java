/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored expression and predicate storage
 ******************************************************************************/
package org.eventb.core.basis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRExprRef;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.eventb.core.IPRStringInput;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pm.TypeEnvironmentSorter;
import org.eventb.internal.core.pm.TypeEnvironmentSorter.Entry;
import org.rodinp.core.RodinDBException;

/**
 * @since 1.0
 */
public class ProofStoreCollector implements IProofStoreCollector {

	private final ITypeEnvironment baseTypEnv;
	private Map<Predicate,String> predicates;
	private int predCount;
	private Map<Expression,String> expressions;
	private int exprCount;
	
	public ProofStoreCollector(ITypeEnvironment baseTypEnv){
		this.baseTypEnv = baseTypEnv;
		this.predicates = new HashMap<Predicate,String>();
		this.predCount = 0;
		this.expressions = new HashMap<Expression,String>();
		this.exprCount = 0;
	}
	
	@Override
	public String putPredicate(Predicate pred) throws RodinDBException {
		if (pred == null) 
			Util.log(new IllegalArgumentException(), "Trying to serialise a null Predicate");
		String ref = predicates.get(pred);
		if (ref == null)
		{
			ref = "p"+Integer.toString(predCount);
			predicates.put(pred,ref);
			predCount++;
		}
		return ref;
	}
	
	@Override
	public String putExpression(Expression expr) throws RodinDBException {
		String ref = expressions.get(expr);
		if (ref == null)
		{
			ref = "e"+Integer.toString(exprCount);
			expressions.put(expr,ref);
			exprCount++;
		}
		return ref;
	}

	@Override
	public void writeOut(IPRProof prProof, IProgressMonitor monitor)
			throws RodinDBException {

		writeTypeEnv(prProof);
		
		for (Map.Entry<Predicate, String> entry : predicates.entrySet()) {
			// TODO : writeout extra type info
			final IPRStoredPred prPred = prProof.getPredicate(entry.getValue());
			prPred.create(null, monitor);
			final Predicate pred = entry.getKey();
			prPred.setPredicate(pred, baseTypEnv, monitor);
		}

		for (Map.Entry<Expression, String> entry : expressions.entrySet()) {
			// TODO : writeout extra type info
			final IPRStoredExpr prExpr = prProof.getExpression(entry.getValue());
			prExpr.create(null, monitor);
			final Expression expr = entry.getKey();
			prExpr.setExpression(expr, baseTypEnv, monitor);
		}
	}
	
	// TODO fix monitors here ?
	private void writeTypeEnv(IPRProof prProof) throws RodinDBException {
		TypeEnvironmentSorter sorter = new TypeEnvironmentSorter(baseTypEnv);
		prProof.setSets(sorter.givenSets, null);
		for (Entry entry: sorter.variables) {
			IPRIdentifier ident = prProof.getIdentifier(entry.name);
			ident.create(null, null);
			ident.setType(entry.type, null);
		}
		
	}

	public static class Bridge implements IReasonerInputWriter {

		private final IPRProofRule prProofRule;
		private final IProofStoreCollector store;
		// This may not work..
		private final IProgressMonitor monitor;
		
		public Bridge(IPRProofRule prProofRule,IProofStoreCollector store,IProgressMonitor monitor){
			this.prProofRule = prProofRule;
			this.store = store;
			this.monitor= monitor;
		}
		
		@Override
		public void putExpressions(String key, Expression... exprs) throws SerializeException {
			try {
				IPRExprRef prRef = prProofRule.getPRExprRef(key);
				prRef.create(null, monitor);
				prRef.setExpressions(exprs, store, monitor);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		@Override
		public void putPredicates(String key, Predicate... preds) throws SerializeException {
			try {
				IPRPredRef prRef = prProofRule.getPRPredRef(key);
				prRef.create(null, monitor);
				prRef.setPredicates(preds, store, monitor);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		@Override
		public void putString(String key, String string) throws SerializeException {
			try {
				IPRStringInput prStrInp = prProofRule.getPRStringInput(key);
				prStrInp.create(null, monitor);
				prStrInp.setString(string, monitor);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}
		
	}

}
