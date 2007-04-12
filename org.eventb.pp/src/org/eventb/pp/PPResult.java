package org.eventb.pp;


public class PPResult {

	public enum Result {valid, invalid, error, timeout, cancel};
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
