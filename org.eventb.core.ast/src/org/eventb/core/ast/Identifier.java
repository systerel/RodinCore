/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;




/**
 * This is the base class for all identifiers in an event-B formula.
 * 
 * @author François Terrier
 *
 */
public abstract class Identifier extends Expression {
	
	protected Identifier(int tag, SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
	}

}
