package org.eventb.core.seqprover;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

public interface IReasonerInputSerializer {
	
	void putPredicate(String name, Predicate predicate) throws SerializeException;
	
	void putExpression(String name, Expression expression) throws SerializeException;
	
	void putString(String name, String string) throws SerializeException;
	
	Predicate getPredicate(String name) throws SerializeException;
	
	Expression getExpression(String name) throws SerializeException;
	
	String getString(String name) throws SerializeException;
	
	class SerializeException extends Exception{
		
		private static final long serialVersionUID = 1764122645237679016L;
		private final Exception nextedException;
		
		public SerializeException(Exception nestedException){
			this.nextedException = nestedException;
		}

		/**
		 * @return Returns the nextedException.
		 */
		public final Exception getNextedException() {
			return nextedException;
		}
		
		
		
	}

	IReasonerInputSerializer[] getSubInputSerializers() throws SerializeException;

	IReasonerInputSerializer[] makeSubInputSerializers(int length) throws SerializeException;

}
