/*
 * Created on 24-jun-2005
 *
 */
package org.eventb.core.ast;


/**
 * Describes a problem encountered when dealing with a formula.
 * <p>
 * This class is not intended to be implemented by clients.
 * </p>
 * 
 * @author François Terrier
 */
public class ASTProblem {
	
	private Object[] args;
	// necessary information to locate and indicate the problem
	private SourceLocation location;
	private ProblemKind msg;
	private int severity;
	
	/**
	 * @param location the location of this error or warning
	 * @param msg the problem of this problem
	 * @param severity the severity of the problem
	 * @param args the additional arguments needed to describe the problem. These arguments are best described here {@link ProblemKind}
	 */
	public ASTProblem(SourceLocation location, ProblemKind msg, int severity, Object... args) {
		this.location = location;
		this.msg = msg;
		this.args = args;
		this.severity = severity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ASTProblem) {
			ASTProblem other = (ASTProblem) obj;
			if (args.length != other.args.length) {
				return false;
			}
			for (int i = 0; i < args.length; i++) {
				if (!args[i].equals(other.args[i])) {
					return false;
				}
			}
			return other.location==null?true:other.location.equals(location) && other.msg.equals(msg) && other.severity == severity;
		}
		return false;
	}
	
	
	/**
	 * Returns the arguments of this problem.
	 * 
	 * @return the arguments of this problem
	 */
	public Object[] getArgs() {
		return args;
	}
	
	/**
	 * Return the {@link ProblemKind} of this error of warning
	 * 
	 * @return the problem of this error of warning
	 */
	public ProblemKind getMessage() {
		return msg;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	/**
	 * Returns the source location of this error or warning.
	 * 
	 * @return the source location of the error or warning
	 */
	public SourceLocation getSourceLocation(){
		return location;
	}
	
	/**
	 * Returns <code>true</code> if this is an error.
	 * 
	 * @return <code>true</code> if this is an error
	 */
	public boolean isError() {
		return (this.severity & ProblemSeverities.Error) != 0;
	}
	
	/**
	 * Returns <code>true</code> if this is an warning.
	 * 
	 * @return <code>true</code> if this is an warning
	 */
	public boolean isWarning() {
		return (this.severity & ProblemSeverities.Error) == 0;
	}

	@Override
	public String toString() {
		final SourceLocation loc = getSourceLocation();
		final Object[] formatArgs;
		if (loc != null) {
			formatArgs = new Object[args.length + 2];
			System.arraycopy(args, 0, formatArgs, 0, args.length);
			formatArgs[args.length] = loc.getStart();
			formatArgs[args.length + 1] = loc.getEnd();
		} else {
			formatArgs = args;
		}
		return String.format(getMessage().toString(), formatArgs);
	}
	
}
