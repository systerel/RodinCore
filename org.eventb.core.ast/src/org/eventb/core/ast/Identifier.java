/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.List;
import java.util.ListIterator;



/**
 * This is the base class for all identifiers in an event-B formula.
 * 
 * @author François Terrier
 *
 */
public abstract class Identifier extends Expression {
	
	protected Identifier(int tag,
			SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
	}
	
	/* (non-Javadoc)
	 * helper methods that return the current identifier transformed as a new bound identifier
	 * with a new index. It is used by method getFreeToBoundIdentsFormula
	 */
	protected Expression getBoundIdent(List<FreeIdentifier> freeIdent, int localOffset, String name, FormulaFactory factory) {
		ListIterator<FreeIdentifier> i = freeIdent.listIterator(0);
		while (i.hasNext()) {
			if ((i.next()).getName().equals(name)) {
				// it is bound to a variable in the same scope as ours.
				return factory.makeBoundIdentifier(i.previousIndex(), getSourceLocation());
			}
		}
		// it stays bound to a variable outside our scope, but it adjusts the index accordingly
		freeIdent.add(factory.makeFreeIdentifier(name, getSourceLocation()));
		return factory.makeBoundIdentifier(freeIdent.size()-1+localOffset, getSourceLocation());
	}
}
