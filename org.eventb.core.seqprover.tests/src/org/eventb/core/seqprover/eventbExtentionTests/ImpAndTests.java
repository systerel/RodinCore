package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpAndRewrites;

/**
 * Unit tests for the {@link ImpAndRewrites} reasoner
 * 
 * @author htson
 *
 */
public class ImpAndTests extends AbstractManualRewriterTests {

	String P1 = "x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3";

	String resultP1 = "(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)";

	String P2 = "x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)";

	String resultP2 = "x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)";

	String P3 = "∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)";
	
	String resultP3 = "∀x·x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)";

	String P4 = "∀x·x = 4 ⇒ ((x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) ∧ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3))";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.impAndRewrites";
	}

	@Override
	protected String[] getSuccessfulTests() {
		return new String[] {
				P1, "", resultP1,
				P2, "1", resultP2,
				P3, "1.1", resultP3
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

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.impAndGetPositions(predicate);
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "1",
				P3, "1.1",
				P4, "1\n" + "1.1.0\n" + "1.1.1" 
		};
	}

}