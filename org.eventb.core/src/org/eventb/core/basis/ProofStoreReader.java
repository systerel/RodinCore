package org.eventb.core.basis;

import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.eventb.core.IPRExprRef;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRReasonerInput;
import org.eventb.core.IPRStoredExpr;
import org.eventb.core.IPRStoredPred;
import org.eventb.core.IPRStringInput;
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

	private final IPRProof prProof;
	private final FormulaFactory factory;
	
	private ITypeEnvironment baseTypEnv;
	private Map<String, Predicate> predicates;
	private Map<String, Expression> expressions;
	
	public FormulaFactory getFormulaFactory() {
		return factory;
	}
	
	public ProofStoreReader(IPRProof prProof, FormulaFactory factory){
		this.prProof = prProof;
		this.factory = factory;
		this.baseTypEnv = null;
		this.predicates = new HashMap<String, Predicate>();
		this.expressions = new HashMap<String, Expression>();
	}
	
	public ITypeEnvironment getBaseTypeEnv() throws RodinDBException {
		if (baseTypEnv == null) {
			baseTypEnv = factory.makeTypeEnvironment();
			for (String set: prProof.getSets()) {
				baseTypEnv.addGivenSet(set);
			}
			for (IPRIdentifier ident: prProof.getIdentifiers()) {
				baseTypEnv.addName(ident.getElementName(), ident.getType(factory));
			}
		}
		return baseTypEnv;
	}

	public Predicate getPredicate(String ref)
			throws RodinDBException {
		Predicate pred = predicates.get(ref);
		if (pred == null){
			final ITypeEnvironment baseTypeEnv = getBaseTypeEnv();
			final IPRStoredPred prPred = prProof.getPredicate(ref);
			FreeIdentifier[] newFreeIdents = prPred.getFreeIdents(factory);
			if (newFreeIdents.length == 0)
			{
				pred = prPred.getPredicate(factory, baseTypeEnv);
			}
			else
			{
				ITypeEnvironment newTypEnv = baseTypeEnv.clone();
				newTypEnv.addAll(newFreeIdents);
				pred = prPred.getPredicate(factory, newTypEnv);
			}
		}
		return pred;
	}

	public Expression getExpression(String ref) throws RodinDBException {
		Expression expr = expressions.get(ref);
		if (expr == null){
			final ITypeEnvironment baseTypeEnv = getBaseTypeEnv();
			final IPRStoredExpr prExpr = prProof.getExpression(ref);
			FreeIdentifier[] newFreeIdents = prExpr.getFreeIdents(factory);
			if (newFreeIdents.length == 0)
			{
				expr = prExpr.getExpression(factory, baseTypeEnv);
			}
			else
			{
				ITypeEnvironment newTypEnv = baseTypeEnv.clone();
				newTypEnv.addAll(newFreeIdents);
				expr = prExpr.getExpression(factory, newTypEnv);
			}
		}
		return expr;
	}

	public static class Bridge implements IReasonerInputSerializer{

		private final IPRReasonerInput prReasonerInput;
		private final IProofStoreReader store;
		
		public Bridge(IPRReasonerInput prReasonerInput,IProofStoreReader store){
			this.prReasonerInput = prReasonerInput;
			this.store = store;
		}
		
		public Expression getExpression(String name) throws SerializeException {
			try{
				return ((IPRExprRef)prReasonerInput.getInternalElement(IPRExprRef.ELEMENT_TYPE, name)).getExpression(store);
			}
			catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public Predicate getPredicate(String name) throws SerializeException {
			try{
				return ((IPRPredRef)prReasonerInput.getInternalElement(IPRPredRef.ELEMENT_TYPE, name)).getPredicate(store);
			}
			catch (RodinDBException e) {
				throw new SerializeException(e);
			}
		}

		public String getString(String name) throws SerializeException {
			try{
				return ((IPRStringInput)prReasonerInput.getInternalElement(IPRStringInput.ELEMENT_TYPE, name)).getStrInp();
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
					subInputSerializers[i] =  new Bridge((IPRReasonerInput)rodinElements[i],store);
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
