package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RelImgUnionRightRewrites;

/**
 * Unit tests for the Relational Image with Union Right Rewrites reasoner
 * {@link RelImgUnionRightRewrites}
 * 
 * @author htson
 */
public class RelImgUnionRightTests extends AbstractManualRewriterTests {

	// r[S \/ ... \/ T] == r[S] \/ ... \/ r[T]
	String P1 = "(0 = 1) ⇒ 1 ∈ r[{0, 1} ∪ {1, 2} ∪ {2, 3}]";

	String resultP1 = "0=1⇒1∈r[{0,1}]∪r[{1,2}]∪r[{2,3}]";

	String P2 = "∀x·x = TRUE ⇒ x ∈ r[{0, 1} ∪ {1, 2} ∪ {2, 3}]";

	String resultP2 = "∀x·x=TRUE⇒x∈r[{0,1}]∪r[{1,2}]∪r[{2,3}]";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.relImgUnionRightRewrites";
	}
		
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.relImgUnionRightGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "1.1", resultP1),
				new SuccessfulTest(P2, "1.1.1", resultP2)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0.1",
				P2, "1.0.1",
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "1.1",
				P2, "1.1.1"	
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
