package org.eventb.core.basis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRExprRef;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IPRProofStore;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.eventb.core.IPRStringInput;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.rodinp.core.RodinDBException;

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
	
	public String putPredicate(Predicate pred) throws RodinDBException {
		String ref = predicates.get(pred);
		if (ref == null)
		{
			ref = "p"+Integer.toString(predCount);
			predicates.put(pred,ref);
			predCount++;
		}
		return ref;
	}
	
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

	// TODO : Make this efficient; At the moment this is not possible since once
	// cannot overwrite a type environment.
	private FreeIdentifier[] getIdentsNotInBase(Formula formula){
		FreeIdentifier[] freeIdents = formula.getFreeIdentifiers();
		ArrayList<FreeIdentifier> absent = new ArrayList<FreeIdentifier>();
		for (FreeIdentifier ident : freeIdents) {
			if (!(baseTypEnv.contains(ident.getName()) &&
					baseTypEnv.getType(ident.getName()).equals(ident.getType())))
			{
				absent.add(ident);
			}
		}
		FreeIdentifier[] result = new FreeIdentifier[absent.size()];
		return absent.toArray(result);
	}
	
	public void writeOut(IPRProofStore prProofStore, IProgressMonitor monitor)
			throws RodinDBException {
		
		IPRTypeEnvironment prBaseTypeEnv = (IPRTypeEnvironment) prProofStore.getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE, "baseTypEnv");
		prBaseTypeEnv.create(null, null);
		prBaseTypeEnv.setTypeEnvironment(baseTypEnv, null);
		
		for(Map.Entry<Predicate, String> entry : predicates.entrySet()){
			// TODO : writeout extra type info
			IPRStoredPred prPred = (IPRStoredPred)prProofStore.getInternalElement(IPRStoredPred.ELEMENT_TYPE, entry.getValue());
			prPred.create(null, monitor);
			Predicate pred = entry.getKey();
			prPred.setPredicate(pred, monitor);
			prPred.setFreeIdents(getIdentsNotInBase(pred), monitor);			
		}
		
		for(Map.Entry<Expression, String> entry : expressions.entrySet()){
			// TODO : writeout extra type info
			IPRStoredExpr prExpr = (IPRStoredExpr)prProofStore.getInternalElement(IPRStoredExpr.ELEMENT_TYPE, entry.getValue());
			prExpr.create(null, monitor);
			prExpr.setExpression(entry.getKey(), monitor);
			Expression expr = entry.getKey();
			prExpr.setExpression(expr, monitor);
			prExpr.setFreeIdents(getIdentsNotInBase(expr), monitor);
		}
	}
	
	public static class Bridge implements IReasonerInputSerializer{

		private final IPRReasonerInput prReasonerInput;
		private final IProofStoreCollector store;
		// This may not work..
		private final IProgressMonitor monitor;
		
		public Bridge(IPRReasonerInput prReasonerInput,IProofStoreCollector store,IProgressMonitor monitor){
			this.prReasonerInput = prReasonerInput;
			this.store = store;
			this.monitor= monitor;
		}
		
		public Expression getExpression(String name) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public Predicate getPredicate(String name) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public String getString(String name) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public IReasonerInputSerializer[] getSubInputSerializers() throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public IReasonerInputSerializer[] makeSubInputSerializers(int length) throws SerializeException {
			Bridge[] subInputSerializers = 
				new Bridge[length];
			try {
				for (int i = 0; i < length; i++) {
					IPRReasonerInput prChild = (IPRReasonerInput) 
					prReasonerInput.getInternalElement(IPRReasonerInput.ELEMENT_TYPE,Integer.toString(i));
					prChild.create(null, monitor);
					subInputSerializers[i]=new Bridge(prChild,store,monitor);
				}
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
			return subInputSerializers;
		}

		public void putExpression(String name, Expression expr) throws SerializeException {
			try {
				IPRExprRef prRef = (IPRExprRef)prReasonerInput.getInternalElement(IPRExprRef.ELEMENT_TYPE, name);
				prRef.create(null, monitor);
				prRef.setExpression(expr, store, monitor);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public void putPredicate(String name, Predicate pred) throws SerializeException {
			try {
				IPRPredRef prRef = (IPRPredRef)prReasonerInput.getInternalElement(IPRPredRef.ELEMENT_TYPE, name);
				prRef.create(null, monitor);
				prRef.setPredicate(pred, store, monitor);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public void putString(String name, String string) throws SerializeException {
			try {
				IPRStringInput prStrInp = (IPRStringInput)prReasonerInput.getInternalElement(IPRStringInput.ELEMENT_TYPE, name);
				prStrInp.create(null, monitor);
				prStrInp.setStrInp(string, monitor);
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}
		
	}

}
