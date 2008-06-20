package org.eventb.contributer.seqprover.fr1866809;

import org.eventb.core.ast.Expression;

public class Expression_tuple {
	public Expression_tuple(Expression a_first,Expression a_second)
	{
		first=a_first;
		second=a_second;
	}
	private Expression first;
	private Expression second;
	
	public Expression get_first()
	{
		return first;
	}
	
	public Expression get_second()
	{
		return second;
	}

}
