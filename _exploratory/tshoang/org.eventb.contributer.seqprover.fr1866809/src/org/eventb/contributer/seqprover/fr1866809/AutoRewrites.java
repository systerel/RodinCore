package org.eventb.contributer.seqprover.fr1866809;



import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;


import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;

public class AutoRewrites extends AbstractAutoRewrites implements IReasoner {
		 

	public AutoRewrites() {
		super(null, true);
	}

	public static String REASONER_ID = "org.eventb.contributer.seqprover.fr1866809.autoRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "Simplify using dom of total function";
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		// an object for checking the hypothesises
		Checker checker = new Checker();
		// list of good/valid functions
		List<Expression_tuple> good_functions = new ArrayList<Expression_tuple>();
		// a tuple to store the checker result
		Expression_tuple store;
		
		// go through all hypothesises
		for(Predicate hyp: seq.hypIterable())
		{
			// only relational predicates can / have to be checked
			if(hyp instanceof RelationalPredicate)
			{
				store= checker.checker((RelationalPredicate) hyp);
				
				// is the store_tuple not null it will be added
				if(store!=null)
					good_functions.add(store);
			}
		}
		
		// set the rewriter which needs the good functions now and call the superclass for apply
		super.reset_rewriter(new AutoRewriterImpl(good_functions));
		return super.apply(seq, input, pm);
	}

		 
}