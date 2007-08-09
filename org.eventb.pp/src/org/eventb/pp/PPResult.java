/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.pp;

public class PPResult {

	public enum Result {valid, invalid, error, timeout, cancel}
	
	private Result result;
	private ITracer tracer;
	
	public PPResult (Result result, ITracer tracer) {
		this.result = result;
		this.tracer = tracer;
	}
	
	public Result getResult() {
		return result;
	}
	
	// is null when result different from valid
	public ITracer getTracer() {
		return tracer;
	}
	
}
