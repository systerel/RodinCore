package org.eventb.core.seqprover;

/**
 * Exception thrown upon a serialization error when reading 
 * or writing reasoner inputs.
 * 
 * <p>
 * The purpose of this class is to encapsulate an exception that may 
 * occur when reading or writing reasoner inputs to an underlying
 * resource.
 * </p>
 * 
 * @see IReasoner
 * @see IReasonerInput
 * @see IReasonerInputReader
 * @see IReasonerInputWriter
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public class SerializeException extends Exception {
	
	private static final long serialVersionUID = 1764122645237679016L;
	
	
	/**
	 * Constructor for a serialize exception
	 * 
	 * @param cause
	 * 			The exception that this SerializeException
	 * 			encapsulates.
	 */
	public SerializeException(Throwable cause){
		super(cause);
	}

}