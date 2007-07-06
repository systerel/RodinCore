package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateDescriptor;

public interface IMatchIterator {

	public Iterator<Clause> iterator(PredicateDescriptor predicate);
	
}
