package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.STORE_REF_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRPredRef;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @since 1.0
 */
public class PRPredRef extends InternalElement implements IPRPredRef{

	public PRPredRef(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRPredRef> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public Predicate[] getPredicates(IProofStoreReader store)
			throws RodinDBException {

		final String value = getAttributeValue(STORE_REF_ATTRIBUTE);
		final String[] refs = value.split(",", -1);
		final int length = refs.length;
		final Predicate[] preds = new Predicate[length];
		for (int i = 0; i < preds.length; i++) {
			final String ref = refs[i];
			if (ref.length() == 0) {
				preds[i] = null;
			} else {
				preds[i] = store.getPredicate(ref);
			}
		}
		return preds;
	}

	@Override
	public void setPredicates(Predicate[] preds, IProofStoreCollector store,
			IProgressMonitor monitor) throws RodinDBException {

		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (Predicate pred: preds) {
			builder.append(sep);
			sep = ",";
			if (pred != null) {
				builder.append(store.putPredicate(pred));
			}
		}
		setAttributeValue(STORE_REF_ATTRIBUTE, builder.toString(), monitor);
	}

}
