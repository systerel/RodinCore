package org.eventb.core.seqprover;

/**
 * Exception thrown upon a serialization error.
 * 
 * @author Farhad Mehta
 */
public class SerializeException extends Exception {
	
	private static final long serialVersionUID = 1764122645237679016L;
	
	public SerializeException(Throwable cause){
		super(cause);
	}

}