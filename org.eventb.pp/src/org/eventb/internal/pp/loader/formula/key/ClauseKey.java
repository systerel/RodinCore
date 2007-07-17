package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;

public abstract class ClauseKey<T extends IndexedDescriptor> extends SymbolKey<T> {

	private List<SignedFormula<?>> signatures;
	
	public ClauseKey(List<SignedFormula<?>> signatures) {
		this.signatures = signatures;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ClauseKey) {
			ClauseKey<?> temp = (ClauseKey<?>) obj;
			return signatures.equals(temp.signatures);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return signatures.hashCode();
	}

	@Override
	public String toString() {
		return signatures.toString();
	}

}
