package org.eventb.core.basis;

import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRExprRef;
import org.eventb.core.IPRExpression;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRProofStore;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPRStringInput;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ProofStoreReader implements IProofStoreReader {

	private final IPRProofStore prProofStore;
	private final FormulaFactory factory;
	
	private ITypeEnvironment baseTypEnv;
	private Map<String, Predicate> predicates;
	private Map<String, Expression> expressions;
	
	public FormulaFactory getFormulaFactory() {
		return factory;
	}
	
	public ProofStoreReader(IPRProofStore prProofStore,FormulaFactory factory){
		this.prProofStore = prProofStore;
		this.factory = factory;
		this.baseTypEnv = null;
		this.predicates = new HashMap<String, Predicate>();
		this.expressions = new HashMap<String, Expression>();
	}
	
	public ITypeEnvironment getBaseTypeEnv(IProgressMonitor monitor)
			throws RodinDBException {
		if (baseTypEnv == null)
		{
			baseTypEnv = ((IPRTypeEnvironment)
					prProofStore.getInternalElement(
							IPRTypeEnvironment.ELEMENT_TYPE,
							"baseTypEnv"))
							.getTypeEnvironment(factory, monitor);
		}
		return baseTypEnv;
	}

	public Predicate getPredicate(String ref, IProgressMonitor monitor)
			throws RodinDBException {
		Predicate pred = predicates.get(ref);
		if (pred == null){
			final ITypeEnvironment baseTypeEnv = getBaseTypeEnv(null);
			final IPRPredicate prPred = ((IPRPredicate)
								prProofStore.getInternalElement(
										IPRPredicate.ELEMENT_TYPE,
										ref));
			FreeIdentifier[] newFreeIdents = prPred.getFreeIdents(factory, monitor);
			if (newFreeIdents.length == 0)
			{
				pred = prPred.getPredicate(factory, baseTypeEnv, null);
			}
			else
			{
				ITypeEnvironment newTypEnv = baseTypeEnv.clone();
				newTypEnv.addAll(newFreeIdents);
				pred = prPred.getPredicate(factory, newTypEnv, null);
			}
		}
		return pred;
	}

	public Expression getExpression(String ref, IProgressMonitor monitor) throws RodinDBException {
		Expression expr = expressions.get(ref);
		if (expr == null){
			final ITypeEnvironment baseTypeEnv = getBaseTypeEnv(null);
			final IPRExpression prExpr = ((IPRExpression)
								prProofStore.getInternalElement(
										IPRExpression.ELEMENT_TYPE,
										ref));
			FreeIdentifier[] newFreeIdents = prExpr.getFreeIdents(factory, monitor);
			if (newFreeIdents.length == 0)
			{
				expr = prExpr.getExpression(factory, baseTypeEnv, null);
			}
			else
			{
				ITypeEnvironment newTypEnv = baseTypeEnv.clone();
				newTypEnv.addAll(newFreeIdents);
				expr = prExpr.getExpression(factory, newTypEnv, null);
			}
		}
		return expr;
	}

	public static class Bridge implements IReasonerInputSerializer{

		private final IPRReasonerInput prReasonerInput;
		private final IProofStoreReader store;
		// This may not work..
		private final IProgressMonitor monitor;
		
		public Bridge(IPRReasonerInput prReasonerInput,IProofStoreReader store,IProgressMonitor monitor){
			this.prReasonerInput = prReasonerInput;
			this.store = store;
			this.monitor= monitor;
		}
		
		public Expression getExpression(String name) throws SerializeException {
			try{
				return ((IPRExprRef)prReasonerInput.getInternalElement(IPRExprRef.ELEMENT_TYPE, name)).getExpression(store, monitor);
			}
			catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public Predicate getPredicate(String name) throws SerializeException {
			try{
				return ((IPRPredRef)prReasonerInput.getInternalElement(IPRPredRef.ELEMENT_TYPE, name)).getPredicate(store, monitor);
			}
			catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public String getString(String name) throws SerializeException {
			try{
				return ((IPRStringInput)prReasonerInput.getInternalElement(IPRStringInput.ELEMENT_TYPE, name)).getStrInp(monitor);
			}
			catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public IReasonerInputSerializer[] getSubInputSerializers() throws SerializeException {
			try {
				IRodinElement[] rodinElements = prReasonerInput.getChildrenOfType(IPRReasonerInput.ELEMENT_TYPE);
				Bridge[] subInputSerializers = new Bridge[rodinElements.length];
				for (int i = 0; i < subInputSerializers.length; i++) {
					subInputSerializers[i] =  new Bridge((IPRReasonerInput)rodinElements[i],store,monitor);
				}
				return subInputSerializers;
			} catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public IReasonerInputSerializer[] makeSubInputSerializers(int length) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public void putExpression(String name, Expression expression) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public void putPredicate(String name, Predicate predicate) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}

		public void putString(String name, String string) throws SerializeException {
			throw new SerializeException(new OperationNotSupportedException());
		}
		
	}

}
