package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.HashMap;
import java.util.Map;

public class RewriterRegistry {
	
	private static Map<String,Rewriter> registry = new HashMap<String,Rewriter>();
	
	// Static initialization block for registry 
	static {
		Rewriter[] installedRewriters =	
		{
				// Add new rewriters here.
				new DisjToImpl(),
				new RemoveNegation(),
				new TrivialRewrites(),
				new TypeExpRewrites()
		};
		
		for (Rewriter rewriter : installedRewriters)
		{
			// no duplicate ids
			assert ! registry.containsKey(rewriter.getRewriterID());
			registry.put(rewriter.getRewriterID(),rewriter);
			// System.out.println("Registered "+reasoner.getReasonerID()+" as "+reasoner);
		}
		
	}
	
	public static Rewriter getRewriter(String rewriterID){
		return registry.get(rewriterID);
	}
}
