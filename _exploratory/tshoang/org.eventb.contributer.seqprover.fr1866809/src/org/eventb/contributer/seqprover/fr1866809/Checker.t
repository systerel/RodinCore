package org.eventb.contributer.seqprover.fr1866809;

import java.math.BigInteger;

import org.eventb.core.ast.*;


@SuppressWarnings("unused")
public class Checker extends DefaultRewriter {
	
	
	public Checker() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	// a function that returns a tuple if the matched funtion is replacable for dom(f)
	// null otherwise
	public Expression_tuple checker(RelationalPredicate predicate)
	{
			%match (Predicate predicate) {
			/**
	         * Total function
	         */
			In(left,Tfun(left1,_))->
			{
						return new Expression_tuple(`left,`left1);
					
			}
			
			/**
	         * Total injective function
	         */
			In(left,Tinj(left1,_))->
			{
						return new Expression_tuple(`left,`left1);
					
			}
			
			/**
	         * Total surjective function
	         */
			In(left,Tsur(left1,_))->
			{
						return new Expression_tuple(`left,`left1);
					
			}

			/**
	         * Bijective function
	         */
			In(left,Tbij(left1,_))->
			{
						return new Expression_tuple(`left,`left1);
					
			}
			
			/**
	         * Total relation
	         */
			In(left,Trel(left1,_))->
			{
						return new Expression_tuple(`left,`left1);
					
			}
			}
			return null;
	} 
}