package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;

/**
 * Unit tests for the ri reasoner {@link RemoveInclusion}
 * 
 * @author htson
 */
public class RemoveInclusionUniversalTests extends AbstractManualRewriterTests {
	
	// S <: T == U \ T <: U \ S
	String P1 = "{1} ⊆ {1, 2}";

	String resultP1 = "ℤ ∖ {1,2}⊆ℤ ∖ {1}";

	String P2 = "(0 = 1) ⇒ {1} ⊆ {1, 2}";

	String resultP2 = "0=1⇒ℤ ∖ {1,2}⊆ℤ ∖ {1}";

	String P3 = "∀x·x = TRUE ⇒ {x} ⊆ {x, FALSE}";

	String resultP3 = "∀x·x=TRUE⇒BOOL ∖ {x,FALSE}⊆BOOL ∖ {x}";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.riUniversal";
	}

	public String [] getTestGetPositions() {
		return new String[] {
				P1, "ROOT",
				P2, "1",
				P3, "1.1",
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.riUniversalGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1),
				new SuccessfulTest(P2, "1", resultP2),
				new SuccessfulTest(P3, "1.1", resultP3),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "0",
				P3, "0.1"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
