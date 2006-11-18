package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

public class PRPredRef extends InternalElement implements IPRPredRef{

	public PRPredRef(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public Predicate getPredicate(IProofStoreReader store) throws RodinDBException {
		String ref = getAttributeValue(EventBAttributes.STORE_REF_ATTRIBUTE);
		return store.getPredicate(ref);
	}

	public void setPredicate(Predicate pred, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String ref = store.putPredicate(pred);
		setAttributeValue(EventBAttributes.STORE_REF_ATTRIBUTE, ref , monitor);
	}
	




}
