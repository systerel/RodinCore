/*
 * Created on 07-jul-2005
 *
 */
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;

/**
 * Unit test of the well-formedness checker.
 * 
 * @author franz
 */
public class TestLegibility extends TestCase {
	
	private static final class TestItem {
		String input;
		Predicate formula;
		boolean expectedResult;
		
		TestItem(String input, Predicate formula, boolean result) {
			expectedResult = result;
			this.formula = formula;
			this.input = input;
		}
	}

	private List<TestItem> testItems;

	private FormulaFactory ff = FormulaFactory.getDefault();

	final FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	final FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
	final FreeIdentifier id_z = ff.makeFreeIdentifier("z", null);
	final FreeIdentifier id_s = ff.makeFreeIdentifier("s", null);
	final FreeIdentifier id_t = ff.makeFreeIdentifier("t", null);
	final FreeIdentifier id_u = ff.makeFreeIdentifier("u", null);
	final FreeIdentifier id_a = ff.makeFreeIdentifier("a", null);
	final FreeIdentifier id_b = ff.makeFreeIdentifier("b", null);
	
	final BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
	final BoundIdentDecl bd_y = ff.makeBoundIdentDecl("y", null);
	final BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z", null);
	final BoundIdentDecl bd_s = ff.makeBoundIdentDecl("s", null);
	final BoundIdentDecl bd_t = ff.makeBoundIdentDecl("t", null);
	final BoundIdentDecl bd_u = ff.makeBoundIdentDecl("u", null);

	final Expression b0 = ff.makeBoundIdentifier(0, null);
	final Expression b1 = ff.makeBoundIdentifier(1, null);

	final LiteralPredicate bfalse = ff.makeLiteralPredicate(Formula.BFALSE, null);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testItems = new ArrayList<TestItem>();
		final Expression emptySet = ff.makeAtomicExpression(Formula.EMPTYSET, null);
		final Expression sint = ff.makeAtomicExpression(Formula.INTEGER, null);
		final Expression set_x_in_int =
			ff.makeQuantifiedExpression(
				Formula.CSET,
				mList(bd_x),
				ff.makeRelationalPredicate(Formula.IN, b0, sint, null), b0, null, QuantifiedExpression.Form.Implicit);

		// Implicit comprehension set with enclosed comprehension set on the right
		TestItem item1 = new TestItem(
				"{ y \u2229 {x | x \u2208 \u2124} | y = \u2205 } = { \u2205 }",
				ff.makeRelationalPredicate(
						Formula.EQUAL,
						ff.makeQuantifiedExpression(
							Formula.CSET,
							mList(bd_y),
							ff.makeRelationalPredicate(Formula.EQUAL, b0, emptySet, null), ff.makeAssociativeExpression(Formula.BINTER, new Expression[]{b0, set_x_in_int}, null), null, QuantifiedExpression.Form.Implicit),
						ff.makeSetExtension(Arrays.asList(emptySet), null), null),
				true);
		testItems.add(item1);
		
		// Implicit comprehension set with enclosed comprehension set on the left
		TestItem item2 = new TestItem(
				"{ {x | x \u2208 \u2124} \u2229 y | y = \u2205 } = { \u2205 }",
				ff.makeRelationalPredicate(
						Formula.EQUAL,
						ff.makeQuantifiedExpression(
							Formula.CSET,
							mList(bd_y),
							ff.makeRelationalPredicate(Formula.EQUAL, b0, emptySet, null), ff.makeAssociativeExpression(Formula.BINTER, new Expression[]{set_x_in_int, b0}, null), null, QuantifiedExpression.Form.Implicit),
						ff.makeSetExtension(Arrays.asList(emptySet), null), null),
				true);
		testItems.add(item2);

		final Expression set_x_in_b0 =
			ff.makeQuantifiedExpression(
				Formula.CSET,
				mList(bd_x),
				ff.makeRelationalPredicate(Formula.IN, b0, b1, null), b0, null, QuantifiedExpression.Form.Implicit);

		TestItem item3 = new TestItem(
				"{ y \u2229 {x | x \u2208 y} | y = \u2205 } = { \u2205 }",
				ff.makeRelationalPredicate(
						Formula.EQUAL,
						ff.makeQuantifiedExpression(
							Formula.CSET,
							mList(bd_y),
							ff.makeRelationalPredicate(Formula.EQUAL, b0, emptySet, null), ff.makeAssociativeExpression(Formula.BINTER, new Expression[]{b0, set_x_in_b0}, null), null, QuantifiedExpression.Form.Implicit),
						ff.makeSetExtension(Arrays.asList(emptySet), null), null),
				true);
		testItems.add(item3);

		TestItem item4 = new TestItem(
				"{ {x | x \u2208 y} \u2229 y  | y = \u2205 } = { \u2205 }",
				ff.makeRelationalPredicate(
						Formula.EQUAL,
						ff.makeQuantifiedExpression(
							Formula.CSET,
							mList(bd_y),
							ff.makeRelationalPredicate(Formula.EQUAL, b0, emptySet, null), ff.makeAssociativeExpression(Formula.BINTER, new Expression[]{set_x_in_b0, b0}, null), null, QuantifiedExpression.Form.Implicit),
						ff.makeSetExtension(Arrays.asList(emptySet), null), null),
				true);
		testItems.add(item4);

	}


	
	
	private Object[] testPairs = new Object[]{
			// Pred
			"\u22a5", 
			bfalse,
			true,
			"finite(x)",
			ff.makeSimplePredicate(Formula.KFINITE,id_x,null),
			true,
			"x=x",
			ff.makeRelationalPredicate(Formula.EQUAL,id_x,id_x,null),
			true,
			"\u00ac\u22a5",
			ff.makeUnaryPredicate(Formula.NOT,bfalse,null),
			true,
			"\u22a5\u2227\u22a5",
			ff.makeAssociativePredicate(Formula.LAND,new Predicate[]{
							bfalse,
							bfalse},null),
			true,
			"\u22a5\u21d2\u22a5",
			ff.makeBinaryPredicate(Formula.LIMP,bfalse,bfalse,null),
			true,
			
			// QuantPred + Pred
			"\u2200x,y,z\u22c5\u22a5",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),bfalse,null),
			true,
			"\u2203x,y\u22c5\u2203s,t\u22c5\u22a5",
			ff.makeQuantifiedPredicate(Formula.EXISTS,mList(bd_x,bd_y),ff.makeQuantifiedPredicate(Formula.EXISTS,mList(bd_s,bd_t),bfalse,null),null),
			true,
			"\u2203x,y\u22c5\u2203s,y\u22c5\u22a5",
			ff.makeQuantifiedPredicate(Formula.EXISTS,mList(bd_x,bd_y),ff.makeQuantifiedPredicate(Formula.EXISTS,mList(bd_s,bd_y),bfalse,null),null),
			false, // bound in 2 places
			"bool(\u2200x\u22c5\u22a5)=x",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x),bfalse,null),null),id_x,null),
			false,
			"bool(\u2200x\u22c5\u22a5)=y",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x),bfalse,null),null),id_y,null),
			true,
			"x=bool(\u2200x\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL,id_x,ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x),bfalse,null),null),null),
			false,
			"\u2200x,y,z\u22c5x=x",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),ff.makeRelationalPredicate(Formula.EQUAL,id_x,id_x,null),null),
			false,
			"\u2200x,y,z\u22c5x=x",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),ff.makeRelationalPredicate(Formula.EQUAL,ff.makeBoundIdentifier(2,null),ff.makeBoundIdentifier(2,null),null),null),
			true,
			
			"\u2200x,y,z\u22c5finite({x,y\u22c5\u22a5\u2223z})",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x,bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),null),null),
			false,
			"\u2200x,y,z\u22c5finite({s,t\u22c5\u22a5\u2223z})",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),null),null),
			false,
			"\u2200x,y,z\u22c5finite({s,t\u22c5\u22a5\u2223z})",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x, bd_y, bd_z),ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,ff.makeBoundIdentifier(2,null),null, QuantifiedExpression.Form.Explicit),null),null),
			true,
			"\u2200x,y,z\u22c5finite({s,t\u22c5\u22a5\u2223t})",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,id_t,null, QuantifiedExpression.Form.Explicit),null),null),
			false,
			"\u2200x,y,z\u22c5finite({s,t\u22c5\u22a5\u2223t})",
			ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x,bd_y,bd_z),ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,ff.makeBoundIdentifier(0,null),null, QuantifiedExpression.Form.Explicit),null),null),
			true,
			
			
			// QuantExpr + Expr
			"{x,y\u22c5\u22a5\u2223z}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x,bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=x",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),id_x,null),
			false,
			"{x,y\u22c5\u22a5\u2223z}={s,t\u22c5\u22a5\u2223u}",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,id_u,null, QuantifiedExpression.Form.Explicit),null),
			true,
			"{x,y\u22c5\u22a5\u2223z}={x,y\u22c5\u22a5\u2223z}",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),null),
			false,
			"{x,y\u22c5\u22a5\u2223{x,y\u22c5\u22a5\u2223z}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u22a5\u2223{s,t\u22c5\u22a5\u2223u}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,id_u,null, QuantifiedExpression.Form.Explicit),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223{s,t\u22c5\u22a5\u2223x}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,id_x,null, QuantifiedExpression.Form.Explicit),null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u22a5\u2223{s,t\u22c5\u22a5\u2223x}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,ff.makeBoundIdentifier(3,null),null, QuantifiedExpression.Form.Explicit),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223{s,t\u22c5\u22a5\u2223t}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_s, bd_t),bfalse,ff.makeBoundIdentifier(0,null),null, QuantifiedExpression.Form.Explicit),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223z}={s,t}",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeSetExtension(new Expression[]{id_s,id_t},null),null),
			true,
			"{x,y\u22c5\u22a5\u2223z}={x,t}",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeSetExtension(new Expression[]{id_x,id_t},null),null),
			false,
			"{x,y\u22c5\u22a5\u2223{s,t}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeSetExtension(new Expression[]{id_s,id_t},null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223{x,t}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeSetExtension(new Expression[]{id_x,id_t},null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u22a5\u2223{x,t}}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeSetExtension(new Expression[]{ff.makeBoundIdentifier(1,null),id_t},null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223union(z)}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeUnaryExpression(Formula.KUNION,id_z,null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223union(x)}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeUnaryExpression(Formula.KUNION,id_x,null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u22a5\u2223union(x)}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeUnaryExpression(Formula.KUNION,ff.makeBoundIdentifier(1,null),null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=union(s)",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeUnaryExpression(Formula.KUNION,id_s,null),null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=union(x)",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeUnaryExpression(Formula.KUNION,id_x,null),null),
			false,
			"{x,y\u22c5\u22a5\u2223s^t}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeBinaryExpression(Formula.EXPN,id_s,id_t,null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223x^t}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeBinaryExpression(Formula.EXPN,id_x,id_t,null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u22a5\u2223x^t}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeBinaryExpression(Formula.EXPN,ff.makeBoundIdentifier(1,null),id_t,null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=s^t",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeBinaryExpression(Formula.EXPN,id_s,id_t,null),null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=x^t",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeBinaryExpression(Formula.EXPN,id_x,id_t,null),null),
			false,
			"{x,y\u22c5\u22a5\u2223z}=s*t",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeAssociativeExpression(Formula.MUL,new Expression[]{id_s,id_t},null),null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=x*t",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeAssociativeExpression(Formula.MUL,new Expression[]{id_x,id_t},null),null),
			false,
			"{x,y\u22c5\u22a5\u2223s*t}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeAssociativeExpression(Formula.MUL,new Expression[]{id_s,id_t},null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223x*t}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeAssociativeExpression(Formula.MUL,new Expression[]{id_x,id_t},null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u22a5\u2223x*t}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,ff.makeAssociativeExpression(Formula.MUL,new Expression[]{ff.makeBoundIdentifier(1,null),id_t},null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			
			"{x,y\u22c5\u22a5\u2223z}=bool(\u2200x,y\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x, bd_y),bfalse,null),null),null),
			false,
			"{x,y\u22c5\u22a5\u2223z}=bool(\u2200s,t\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t),bfalse,null),null),null),
			true,
			"{x,y\u22c5\u22a5\u2223s}=bool(\u2200s,t\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_s,null, QuantifiedExpression.Form.Explicit),ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t),bfalse,null),null),null),
			false,
			
			// QuantExpr + QuantPred
			"{x,y\u22c5\u2200x,y,z\u22c5\u22a5\u2223b}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),
							ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x, bd_y, bd_z),bfalse,null),id_b,null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u2200s,t,u\u22c5\u22a5\u2223b}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),
							ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t, bd_u),bfalse,null),id_b,null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u2200s,t,u\u22c5\u22a5\u2223x}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),
							ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t, bd_u),bfalse,null),id_x,null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u2200s,t,u\u22c5\u22a5\u2223u}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),
							ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t, bd_u),bfalse,null),id_u,null, QuantifiedExpression.Form.Explicit),id_a,null),
			false,
			"{x,y\u22c5\u2200s,t,u\u22c5\u22a5\u2223x}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),
							ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t, bd_u),bfalse,null),ff.makeBoundIdentifier(1,null),null, QuantifiedExpression.Form.Explicit),id_a,null),
			true,
			"{x,y\u22c5\u22a5\u2223z}=bool(\u2200x,y,z\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL, ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_x, bd_y, bd_z),bfalse,null),null),null),
			false,
			"{x,y\u22c5\u22a5\u2223z}=bool(\u2200s,t,u\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL, ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_z,null, QuantifiedExpression.Form.Explicit),ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t, bd_u),bfalse,null),null),null),
			true,
			"{x,y\u22c5\u22a5\u2223s}=bool(\u2200s,t,u\u22c5\u22a5)",
			ff.makeRelationalPredicate(Formula.EQUAL, ff.makeQuantifiedExpression(Formula.CSET,mList(bd_x, bd_y),bfalse,id_s,null, QuantifiedExpression.Form.Explicit),ff.makeBoolExpression(ff.makeQuantifiedPredicate(Formula.FORALL,mList(bd_s, bd_t, bd_u),bfalse,null),null),null),
			false,
			
			// Only Exprs
			"union(x)=y",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeUnaryExpression(Formula.KUNION,id_x,null),id_y,null),
			true,
			"bool(\u22a5)=y",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeBoolExpression(bfalse,null),id_y,null),
			true,
			"{x,y}=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeSetExtension(new Expression[]{id_x,id_y},null),id_a,null),
			true,
			"x=2",
			ff.makeRelationalPredicate(Formula.EQUAL,id_x,ff.makeIntegerLiteral(Common.TWO,null),null),
			true,
			"x^y=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeBinaryExpression(Formula.EXPN,id_x,id_y,null),id_a,null),
			true,
			"x*x=a",
			ff.makeRelationalPredicate(Formula.EQUAL,ff.makeAssociativeExpression(Formula.MUL,new Expression[]{id_x,id_x},null),id_a,null),
			true,
	};

	
	
	/**
	 * Main test routine.
	 */
	public void testWellFormedNess() {
		IParseResult parseResult = null;
		Predicate formula = null;
		boolean result;
		boolean expResult = false;
		int i = 0;
		String syntaxTree = null;
		try {
			for (i=0; i < testPairs.length; i+=3) {
				formula = (Predicate) testPairs[i+1];
				expResult = (Boolean) testPairs[i+2];
				result = formula.isLegible().isSuccess();
				try {
					syntaxTree = formula.getSyntaxTree();
				}
				catch (Exception e) {
					syntaxTree = e.toString();
				}
				assertEquals("\nTesting syntax tree:\n"+syntaxTree+"\nResult obtained: "+(result?"":"NOT")+" well-formed\n"+"Result expected: "+(expResult?"":"NOT")+" well-formed\n",result,expResult);
				if (expResult) {
					try {
						parseResult = ff.parsePredicate((String) testPairs[i]);
						assertEquals("\nTest failed on: "+formula+"\nParser result: "+parseResult,parseResult.getParsedPredicate(), formula);
					} catch (Exception e) {
						e.printStackTrace();
						fail("\nTest failed on parsing: "+formula+"\nParser result: "+parseResult);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("\nTesting:\n"+syntaxTree+"Result expected: "+(expResult?"":"NOT")+" well-formed\n");
		}
	}

	/**
	 * Second test routine (for difficult cases).
	 */
	public void testWellFormedNess2() {
		IParseResult parseResult = null;
		boolean result;
		String syntaxTree = null;
		for (TestItem item : testItems) {
			result = item.formula.isLegible().isSuccess();
			try {
				syntaxTree = item.formula.getSyntaxTree();
			} catch (Exception e) {
				syntaxTree = e.toString();
			}
			assertEquals("\nTesting syntax tree:\n" + syntaxTree
					+ "\nResult obtained: " + (result ? "" : "NOT")
					+ " well-formed\n" + "Result expected: "
					+ (item.expectedResult ? "" : "NOT") + " well-formed\n",
					item.expectedResult, result);

			parseResult = ff.parsePredicate(item.input);
			assertEquals("\nTest failed on: " + item.input
					+ "\nParser result: " + parseResult, item.formula,
					parseResult.getParsedPredicate());
		}
	}
}
