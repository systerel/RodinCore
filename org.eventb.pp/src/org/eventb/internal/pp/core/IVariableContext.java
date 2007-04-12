package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Variable;

public interface IVariableContext {

	public abstract int getNextLocalVariableID();

	public abstract Variable getNextVariable(Sort sort);

}