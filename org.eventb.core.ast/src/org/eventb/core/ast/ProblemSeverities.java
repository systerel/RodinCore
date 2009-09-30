/*
 * Created on 31-jul-2005
 *
 */
package org.eventb.core.ast;

/**
 * This contains all possible problem severities.
 * <p>
 * If a problem added to a result is an error, it should cause the corresponding
 * result to indicate a failure.
 * 
 * @author François Terrier
 *
 * @since 1.0
 */
public interface ProblemSeverities {
	
	/**
	 * Problem is ignored
	 */
	final int Ignore = -1;
	/**
	 * Problem is a warning
	 */
	final int Warning = 0;
	/**
	 * Problem is an error
	 */
	final int Error = 1;
}
