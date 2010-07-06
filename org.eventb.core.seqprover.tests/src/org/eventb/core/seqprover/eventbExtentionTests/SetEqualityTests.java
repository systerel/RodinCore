package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.SetEqlRewrites;

/**
 * Unit tests for the Set Equality Rewrites reasoner
 * {@link SetEqlRewrites}
 * 
 * @author htson
 */
public class SetEqualityTests extends AbstractManualRewriterTests {

	// S = T  ==  S <: T & T <: S (where S, T are sets)
	String P1 = "(0 = 1) ⇒ {0, 1} = {1, 2}";

	String resultP1 = "0=1⇒{0,1}⊆{1,2}∧{1,2}⊆{0,1}";

	String P2 = "∀x·x = 0 ⇒ {x, 1} = {1, 2}";

	String resultP2 = "∀x·x=0⇒{x,1}⊆{1,2}∧{1,2}⊆{x,1}";

	String P3 = "(0 = 1) ⇒ 1 = 2";

	String P4 = "∀x·x = 0 ⇒ x = 1";

	String P5 = "(0 = 1) ⇒ 1 ↦ 2 = 2 ↦ 1";

	String P6 = "∀x·x = 0 ⇒ x ↦ 1 = 1 ↦ 0";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.setEqlRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.setEqlGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "1", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2)
		};
	}


	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0",
				P3, "1",
				P4, "1.1",
				P5, "1",
				P6, "1.1",
		};
	}


	@Override
	protected String[] getTestGetPositions() {
		return new String [] {
				P1, "1",
				P2, "1.1"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
