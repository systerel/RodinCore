package org.eventb.internal.pp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.core.seqprover.xprover.XProverReasoner;

public class PPReasoner extends XProverReasoner {

	public static String REASONER_ID = "org.eventb.pp.pp";
	
	public static boolean DEBUG;
	private static void debug(String msg) {
		System.out.println(msg);
	}
	
	@Override
	public XProverCall newProverCall(IReasonerInput input, Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
		if (PPReasoner.DEBUG) PPReasoner.constructTest(hypotheses, goal);
		
		return new PPProverCall((XProverInput)input,hypotheses,goal,pm);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}


	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new PPInput(reader);
	}

	public static void constructTest(Iterable<Predicate> hypotheses, Predicate goal) {
		StringBuilder builder = new StringBuilder();
		builder.append("doTest(\n");
		builder.append("mList(\n");
		for (FreeIdentifier identifier : collectFreeIdentifiers(hypotheses, goal)) {
			builder.append("\""+identifier.getName()+"\",");
			builder.append("\""+identifier.getType().toString()+"\",");
			builder.append("\n");
		}
		builder.delete(builder.length()-2, builder.length());
		builder.append("\n");
		
		builder.append("),\n mSet(\n");
		for (Predicate predicate : hypotheses) {
			builder.append("\""+predicate.toString()+"\",");
			builder.append("\n");
		}
		builder.delete(builder.length()-2, builder.length());
		builder.append("\n");
		
		builder.append("),");
		builder.append("\""+goal.toString()+"\"");
		
		builder.append(",true);");
		debug(builder.toString());
	}
	
	private static Set<FreeIdentifier> collectFreeIdentifiers(Iterable<Predicate> hypotheses, Predicate goal) {
		Set<FreeIdentifier> result = new HashSet<FreeIdentifier>();
		for (Predicate predicate : hypotheses) {
			result.addAll(Arrays.asList(predicate.getFreeIdentifiers()));
		}
		result.addAll(Arrays.asList(goal.getFreeIdentifiers()));
		return result;
	}

}
