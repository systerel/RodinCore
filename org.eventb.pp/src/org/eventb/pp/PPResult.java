/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp;

/**
 * Result of a PP run.
 *
 * @author François Terrier
 *
 * @since 0.2
 */
public class PPResult {

	/**
	 * The result of a run.
	 *
	 * @author François Terrier
	 *
	 */
	public enum Result {valid, invalid, error, timeout, cancel}
	
	private Result result;
	private ITracer tracer;
	
	public PPResult (Result result, ITracer tracer) {
		assert result == Result.valid || tracer == null; 
		
		this.result = result;
		this.tracer = tracer;
	}
	
	/**
	 * Returns the result of this proof run.
	 * 
	 * @return the result of this proof run
	 */
	public Result getResult() {
		return result;
	}
	
	/**
	 * Returns the tracer corresponding to this proof run if the 
	 * proof succeded (result is {@link Result#valid}) or <code>null</code> 
	 * if the proof did not succeed.
	 * 
	 * @return the tracer corresponding to this proof run if the 
	 * proof succeded or <code>null</code> if the proof did not succeed
	 */
	public ITracer getTracer() {
		return tracer;
	}
	
}
