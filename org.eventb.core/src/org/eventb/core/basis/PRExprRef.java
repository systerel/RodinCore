package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRExprRef;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Expression;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

public class PRExprRef extends InternalElement implements IPRExprRef{

	public PRExprRef(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public Expression getExpression(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException {
		String ref = getAttributeValue(EventBAttributes.STORE_REF_ATTRIBUTE, monitor);
		return store.getExpression(ref, monitor);
	}

	public void setExpression(Expression expr, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException {
		String ref = store.putExpression(expr);
		setAttributeValue(EventBAttributes.STORE_REF_ATTRIBUTE, ref , monitor);
	}

	




}
