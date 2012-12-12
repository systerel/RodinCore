package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.junit.Test;

public class TestCollectNamesAbove {

	static FormulaFactory ff = FormulaFactory.getDefault();
	
	static abstract class TestItem {
		String[] boundNames;
		String[] expected;
		TestItem(String[] boundNames, String[] expected) {
			this.boundNames = boundNames;
			this.expected = expected;
		}
		abstract Set<String> collectNamesAbove();
		Set<String> getExpected() {
			return new HashSet<String>(Arrays.asList(expected));
		}
	}

	static class ExprTestItem extends TestItem {
		QuantifiedExpression expr;
		ExprTestItem(QuantifiedExpression expr, String[] boundNames, String[] expected) {
			super(boundNames, expected);
			this.expr = expr;
		}
		@Override Set<String> collectNamesAbove() {
			return expr.collectNamesAbove(boundNames);
		}
	}
	
	static class PredTestItem extends TestItem {
		QuantifiedPredicate pred;
		PredTestItem(QuantifiedPredicate pred, String[] boundNames, String[] expected) {
			super(boundNames, expected);
			this.pred = pred;
		}
		@Override Set<String> collectNamesAbove() {
			return pred.collectNamesAbove(boundNames);
		}
	}
	
	private FreeIdentifier f_x = mFreeIdentifier("x");
	private FreeIdentifier f_y = mFreeIdentifier("y");
	private FreeIdentifier f_z = mFreeIdentifier("z");
	
	private BoundIdentDecl b_x = mBoundIdentDecl("x");
	private BoundIdentDecl b_y = mBoundIdentDecl("y");
	
	private BoundIdentifier b0 = mBoundIdentifier(0);
	private BoundIdentifier b1 = mBoundIdentifier(1);
	private BoundIdentifier b2 = mBoundIdentifier(2);

	private TestItem[] testItems = new TestItem[] {
		new ExprTestItem(
				mQuantifiedExpression(mList(b_x),
						mSimplePredicate(mAssociativeExpression(f_y, b0, b1)),
						mAssociativeExpression(f_z, b2)),
				mList("t", "u"),
				mList("y", "z", "t", "u")
		),
		new PredTestItem(
				mQuantifiedPredicate(mList(b_x),
						mSimplePredicate(mAssociativeExpression(f_y, b0, b1, f_z, b2))),
				mList("t", "u"),
				mList("y", "z", "t", "u")
		),
		new ExprTestItem(
				mQuantifiedExpression(mList(b_x, b_y),
						mSimplePredicate(mAssociativeExpression(f_x, b0, b1)),
						mAssociativeExpression(f_y, b2)),
				mList("t"),
				mList("x", "y", "t")
		),
		new PredTestItem(
				mQuantifiedPredicate(mList(b_x, b_y),
						mSimplePredicate(mAssociativeExpression(f_x, b0, b1, f_y, b2))),
				mList("t"),
				mList("x", "y", "t")
		),
		new ExprTestItem(
				mQuantifiedExpression(mList(b_x),
						mSimplePredicate(b0),
						mQuantifiedExpression(mList(b_y),
								mSimplePredicate(b0),
								mAssociativeExpression(f_z, b2))),
				mList("t"),
				mList("z", "t")
		),
		new PredTestItem(
				mQuantifiedPredicate(mList(b_x),
						mQuantifiedPredicate(mList(b_y),
								mSimplePredicate(mAssociativeExpression(b1, b0, f_z, b2)))),
				mList("t"),
				mList("z", "t")
		),
	};
	
	@Test 
	public void testCollectNamesAbove() {
		for (TestItem item: testItems) {
			assertEquals(item.toString(), item.getExpected(), item.collectNamesAbove());
		}
	}


}
