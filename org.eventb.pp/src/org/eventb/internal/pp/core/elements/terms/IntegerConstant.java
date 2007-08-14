package org.eventb.internal.pp.core.elements.terms;

import java.math.BigInteger;

import org.eventb.internal.pp.core.elements.Sort;

public final class IntegerConstant extends Constant {

	private BigInteger value;
	
	public IntegerConstant(BigInteger value) {
		super(value.toString(), Sort.NATURAL);
		
		this.value = value;
	}
	
	public BigInteger getValue() {
		return value;
	}

}
