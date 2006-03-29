package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mAssociativePredicate;
import static org.eventb.pp.tests.FastFactory.mLiteralPredicate;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.translator.IdentifierDecomposition;
import org.eventb.internal.pp.translator.Translator;

import com.sun.corba.se.spi.ior.MakeImmutable;

/**
 * Ensures that the translator from set-theory to predicate calculus works
 * correctly.
 * 
 * 
 * @author Matthias Konrad
 */

public class TranslationTests extends TestCase {
	
	public interface TestTranslation {
		Formula translate(Formula input, FormulaFactory ff);
	}
	
	private TestTranslation translator = new TestTranslation() {
		public Formula translate(Formula input, FormulaFactory formulaFactory) {
			return Translator.reduceToPredCalc((Predicate)input, formulaFactory);
		}
	};
	
	private TestTranslation identifierDecomposition = new TestTranslation() {
		public Formula translate(Formula input, FormulaFactory formulaFactory) {
			return IdentifierDecomposition.decomposeIdentifiers((Predicate)input, formulaFactory);
		}
	};
	

	private static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	private static IntegerType INT = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();
	private static PowerSetType INT_SET = ff.makePowerSetType(INT);

	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	private static BinaryExpression Maplet(Expression left, Expression right, SourceLocation loc) {
		return ff.makeBinaryExpression(Formula.MAPSTO, left, right, loc);
	}
	
	private static IntegerLiteral IntLiteral(int value, SourceLocation loc) {
		return ff.makeIntegerLiteral(new BigInteger("" + value), null);
	}
	
	/**
	 * Main test routine for predicates.
	 */
	public void testPredicateTranslation () {

		Predicate pred;
		
		pred = mAssociativePredicate(Formula.LAND, 
				mLiteralPredicate(Formula.BTRUE),
				mLiteralPredicate(Formula.BTRUE)
		);
		doTest(pred, pred);
	}
	
	public void testIdentifierDecomposition1() {
		Predicate input, expected;
		
		input = ff.makeQuantifiedPredicate(
					Formula.FORALL,
					FastFactory.mList(ff.makeBoundIdentDecl("x", null)),
					ff.makeRelationalPredicate(
						Formula.EQUAL,
						Maplet(IntLiteral(10, null), IntLiteral(20, null), null),
						ff.makeBoundIdentifier(0, null),
						null),
					null);
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x", null),
						ff.makeBoundIdentDecl("x0", null)),
				ff.makeRelationalPredicate(
						Formula.EQUAL,
						Maplet(IntLiteral(10, null), IntLiteral(20, null), null),
						Maplet(						
								ff.makeBoundIdentifier(0, null),
								ff.makeBoundIdentifier(1, null),
								null),
						null),
				null);	
		
		doTest(input, expected, identifierDecomposition);		
	}

	public void testIdentifierDecomposition2() {
		Predicate input, expected;
		
		input = ff.makeRelationalPredicate(
				Formula.EQUAL,
				Maplet(IntLiteral(10, null), IntLiteral(20, null), null),
				ff.makeFreeIdentifier("s", null),
				null);
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x", null),
						ff.makeBoundIdentDecl("x0", null)),
				ff.makeBinaryPredicate(
						Formula.LIMP,
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							ff.makeFreeIdentifier("s", null, CPROD(INT, INT)),
							Maplet(						
									ff.makeBoundIdentifier(0, null),
									ff.makeBoundIdentifier(1, null),
									null),
							null),
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							Maplet(IntLiteral(10, null), IntLiteral(20, null), null),
							Maplet(						
									ff.makeBoundIdentifier(0, null),
									ff.makeBoundIdentifier(1, null),
									null),
							null),
						null),
				null);	
		
		doTest(input, expected, identifierDecomposition);		
	}
	
	public void testIdentifierDecomposition3() {
		Predicate input, expected;
		
		input = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(ff.makeBoundIdentDecl("x", null)),
				ff.makeRelationalPredicate(
					Formula.EQUAL,
					ff.makeFreeIdentifier("s", null, CPROD(INT, INT)),
					ff.makeBoundIdentifier(0, null),
					null),
				null);
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x", null),
						ff.makeBoundIdentDecl("x0", null)),
				ff.makeBinaryPredicate(
						Formula.LIMP,
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							ff.makeFreeIdentifier("s", null, CPROD(INT, INT)),
							Maplet(						
									ff.makeBoundIdentifier(0, null),
									ff.makeBoundIdentifier(1, null),
									null),
							null),
						ff.makeQuantifiedPredicate(
							Formula.FORALL,
							FastFactory.mList(
									ff.makeBoundIdentDecl("x", null),
									ff.makeBoundIdentDecl("x0", null)),
							ff.makeRelationalPredicate(
									Formula.EQUAL,
									Maplet(
											ff.makeBoundIdentifier(2, null),
											ff.makeBoundIdentifier(3, null),
											null),
									Maplet(						
											ff.makeBoundIdentifier(0, null),
											ff.makeBoundIdentifier(1, null),
											null),
									null),
							null),
						null),
				null);

		
		doTest(input, expected, identifierDecomposition);		
	}
	
	public void testIdentifierDecomposition4() {
		Predicate input, expected;
		
		input = ff.makeRelationalPredicate(
				Formula.EQUAL,
				ff.makeFreeIdentifier("t", null, CPROD(INT, INT)),
				ff.makeFreeIdentifier("s", null),
				
				null);
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x", null),
						ff.makeBoundIdentDecl("x0", null),
						ff.makeBoundIdentDecl("x1", null),
						ff.makeBoundIdentDecl("x2", null)),
				ff.makeBinaryPredicate(
						Formula.LIMP,
						ff.makeAssociativePredicate(
								Formula.LAND,
								FastFactory.mList(
										ff.makeRelationalPredicate(
											Formula.EQUAL,
											ff.makeFreeIdentifier("t", null, CPROD(INT, INT)),
											Maplet(						
													ff.makeBoundIdentifier(0, null),
													ff.makeBoundIdentifier(1, null),
													null),
											null),
										ff.makeRelationalPredicate(
											Formula.EQUAL,
											ff.makeFreeIdentifier("s", null, CPROD(INT, INT)),
											Maplet(						
													ff.makeBoundIdentifier(2, null),
													ff.makeBoundIdentifier(3, null),
													null),
											null)),
								null),
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							Maplet(						
									ff.makeBoundIdentifier(0, null),
									ff.makeBoundIdentifier(1, null),
									null),
							Maplet(						
									ff.makeBoundIdentifier(2, null),
									ff.makeBoundIdentifier(3, null),
									null),
							null),
						null),
				null);	
		
		doTest(input, expected, identifierDecomposition);		
	}

	public void testIdentifierDecomposition5() {
		Predicate input, expected;
		
		input = ff.makeRelationalPredicate(
				Formula.EQUAL,
				ff.makeFreeIdentifier("t", null, CPROD(INT, INT)),
				ff.makeFreeIdentifier("t", null, CPROD(INT, INT)),
				
				null);
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x", null),
						ff.makeBoundIdentDecl("x0", null)),
				ff.makeBinaryPredicate(
						Formula.LIMP,
							ff.makeRelationalPredicate(
									Formula.EQUAL,
									ff.makeFreeIdentifier("t", null, CPROD(INT, INT)),
									Maplet(						
										ff.makeBoundIdentifier(0, null),
										ff.makeBoundIdentifier(1, null),
										null),
									null),
							ff.makeRelationalPredicate(
								Formula.EQUAL,
								Maplet(						
										ff.makeBoundIdentifier(0, null),
										ff.makeBoundIdentifier(1, null),
										null),
								Maplet(						
										ff.makeBoundIdentifier(0, null),
										ff.makeBoundIdentifier(1, null),
										null),
								null),
							null),
				null);	
		
		doTest(input, expected, identifierDecomposition);		
	}

/*
	public void testIdentifierDecomposition4() {
		Predicate input, expected;
		
		input = ff.makeRelationalPredicate(
				Formula.IN,
				ff.makeFreeIdentifier("i", null, CPROD(CPROD(INT, INT), INT)),
				ff.makeQuantifiedExpression(
						Formula.QUNION,
						FastFactory.mList(ff.makeBoundIdentDecl("x", null)),
						ff.makeLiteralPredicate(Formula.BTRUE, null),
						ff.makeSetExtension(
								Maplet(
									ff.makeBoundIdentifier(0, null),
									ff.makeUnaryExpression(
											Formula.KCARD,
											ff.makeQuantifiedExpression(
													Formula.QUNION,
													FastFactory.mList(ff.makeBoundIdentDecl("x", null, CPROD(CPROD(INT, INT), CPROD(INT, INT)))),
													ff.makeLiteralPredicate(Formula.BTRUE, null),
													ff.makeSetExtension(
															FastFactory.mList(
																Maplet(
																		ff.makeBoundIdentifier(1, null), 
																		ff.makeBoundIdentifier(0, null), 
																		null),
																ff.makeFreeIdentifier("s", null)),	
														    null),
											        null,
													QuantifiedExpression.Form.Explicit),
											null),
									null),
								null),
						null,
						QuantifiedExpression.Form.Explicit),
					null);
		
		Expression innerSet =
			ff.makeSetExtension(
					Maplet(
							Maplet( 
									ff.makeBoundIdentifier(4, null),
									ff.makeBoundIdentifier(5, null),
									null),
							Maplet(
									Maplet(
											ff.makeBoundIdentifier(0, null),
											ff.makeBoundIdentifier(1, null),
											null),
									Maplet(
											ff.makeBoundIdentifier(2, null),
											ff.makeBoundIdentifier(3, null),
											null),
									null),
							null),
				    null);
		
		Expression card = ff.makeUnaryExpression(
				Formula.KCARD,
				ff.makeQuantifiedExpression(
						Formula.QUNION,
						FastFactory.mList(
								ff.makeBoundIdentDecl("x_0", null, INT),
								ff.makeBoundIdentDecl("x_1", null, INT),
								ff.makeBoundIdentDecl("x_2", null, INT),
								ff.makeBoundIdentDecl("x_3", null, INT)),
						ff.makeLiteralPredicate(Formula.BTRUE, null),
						innerSet,
				        null,
						QuantifiedExpression.Form.Explicit),
				null);			
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x)
			ff.makeRelationalPredicate(
				Formula.IN,
				ff.makeFreeIdentifier("i", null, CPROD(CPROD(INT, INT), INT)),
				ff.makeQuantifiedExpression(
						Formula.QUNION,
						FastFactory.mList(
								ff.makeBoundIdentDecl("x_0", null),
								ff.makeBoundIdentDecl("x_1", null)),
						ff.makeLiteralPredicate(Formula.BTRUE, null),
						ff.makeSetExtension(
								FastFactory.mList(
									Maplet(
										Maplet(
												ff.makeBoundIdentifier(0, null),
												ff.makeBoundIdentifier(1, null),
												null),
										card,
										null),
									ff.makeFreeIdentifier("s", null)),								
								null),
						null,
						QuantifiedExpression.Form.Explicit),
					null);
		
		doTest(input, expected, identifierDecomposition);		
	}
	*/
	public void testPowerSetInRule() {
		Expression T, E, V;
		Predicate input, expected;
		
		T = ff.makeFreeIdentifier("T", null, INT_SET);
		E = ff.makeFreeIdentifier("E", null, CPROD(CPROD(ty_T, INT), CPROD(POW(INT), ty_V)));
		
		input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeUnaryExpression(Formula.POW, T, null),
				null);

		V = ff.makeBinaryExpression(
				Formula.MAPSTO,
				ff.makeBinaryExpression(
						Formula.MAPSTO, 
						ff.makeBoundIdentifier(0, null),
						ff.makeBoundIdentifier(1, null),
						null),
				ff.makeBinaryExpression(
						Formula.MAPSTO,
						ff.makeBoundIdentifier(2, null),
						ff.makeBoundIdentifier(3, null),
						null),
				null);
		
		expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(
						ff.makeBoundIdentDecl("x_0", null, ty_V),
						ff.makeBoundIdentDecl("x_1", null, POW(INT)),
						ff.makeBoundIdentDecl("x_2", null, INT),
						ff.makeBoundIdentDecl("x_3", null, ty_T)),
				ff.makeBinaryPredicate(
						Formula.LIMP,
						ff.makeRelationalPredicate(
								Formula.IN,
								V,
								E,
								null),
						ff.makeRelationalPredicate(
								Formula.IN,
								V,
								T,
								null), 
						null),
				null);
	
		
		doTest (input, expected);		
	}
	
	public void testNaturalInRule() {
		Expression E = ff.makeFreeIdentifier("E", null);
		Predicate input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeAtomicExpression(
						Formula.NATURAL, null),
				null);
		
		Predicate expected = ff.makeRelationalPredicate(
				Formula.GE,
				E,
				ff.makeIntegerLiteral(new BigInteger("0"), null),
				null);
		
		doTest (input, expected);		
	}
	
	public void testNatural1InRule() {
		Expression E = ff.makeFreeIdentifier("E", null);
		Predicate input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeAtomicExpression(Formula.NATURAL1, null),
				null);
		
		Predicate expected = ff.makeRelationalPredicate(
				Formula.GT,
				E,
				ff.makeIntegerLiteral(new BigInteger("0"), null),
				null);
		
		doTest (input, expected);		
	}
	
	public void testIntegerInRule() {
		Expression E = ff.makeFreeIdentifier("E", null);
		Predicate input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeAtomicExpression(Formula.INTEGER, null),
				null);
		
		Predicate expected = ff.makeLiteralPredicate(Formula.BTRUE, null);
		
		doTest (input, expected);
	}
	
	public void testCSetInRule() {
		Expression E = ff.makeFreeIdentifier("E", null);
		Predicate input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeQuantifiedExpression(
					Formula.CSET,
					FastFactory.mList(ff.makeBoundIdentDecl("x", null, INT)),
					ff.makeLiteralPredicate(Formula.BTRUE, null),
					ff.makeBoundIdentifier(0, null),
					null,
					null),
				null);
				
		Predicate expected = ff.makeQuantifiedPredicate(
				Formula.EXISTS,
				FastFactory.mList(ff.makeBoundIdentDecl("x", null, INT)),
				ff.makeAssociativePredicate(
					Formula.LAND,
					FastFactory.mList(
						ff.makeLiteralPredicate(Formula.BTRUE, null),
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							E,
							ff.makeBoundIdentifier(0, null),
							null)),
					null),
				null);
		
		doTest(input, expected);	
	}
	
	public void testQInterInRule() {
		Expression E = ff.makeFreeIdentifier("E", null);
		Predicate input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeQuantifiedExpression(
					Formula.QINTER,
					FastFactory.mList(ff.makeBoundIdentDecl("x", null, INT)),
					ff.makeLiteralPredicate(Formula.BTRUE, null),
					ff.makeSetExtension(
							FastFactory.mList(ff.makeBoundIdentifier(0, null)), null),
					null, null),
				null);

		Predicate expected = ff.makeQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(ff.makeBoundIdentDecl("x", null, INT)),
				ff.makeBinaryPredicate(
					Formula.LIMP,
					ff.makeLiteralPredicate(Formula.BTRUE, null),
					ff.makeRelationalPredicate(
						Formula.EQUAL,
						E,
						ff.makeBoundIdentifier(0, null), 
						null),
					null),
				null);
		
		doTest(input, expected);				
	}

	public void testQUnionInRule() {
		Expression E = ff.makeFreeIdentifier("E", null);
		Predicate input = ff.makeRelationalPredicate(
				Formula.IN,
				E,
				ff.makeQuantifiedExpression(
					Formula.QUNION,
					FastFactory.mList(ff.makeBoundIdentDecl("x", null, INT)),
					ff.makeLiteralPredicate(Formula.BTRUE, null),
					ff.makeSetExtension(
							FastFactory.mList(ff.makeBoundIdentifier(0, null)), null),
					null,
					null),
				null);

		Predicate expected = ff.makeQuantifiedPredicate(
				Formula.EXISTS,
				FastFactory.mList(ff.makeBoundIdentDecl("x", null, INT)),
				ff.makeAssociativePredicate(
					Formula.LAND,
					FastFactory.mList(
						ff.makeLiteralPredicate(Formula.BTRUE, null),
						ff.makeRelationalPredicate(
							Formula.EQUAL,
							E,
							ff.makeBoundIdentifier(0, null),
							null)),
					null),
				null);
		
		doTest(input, expected);				
	}

	private void doTest(Predicate input, Predicate expected) {
		doTest(input, expected, translator);
	}
	
	private void doTest(Formula input, Formula expected, TestTranslation translation) {
		input.typeCheck(ff.makeTypeEnvironment());
		expected.typeCheck(ff.makeTypeEnvironment());

		assertTrue("Input is not typed", input.isTypeChecked());
		assertTrue("Expected result is not typed", expected.isTypeChecked());
		Formula actual = translation.translate(input, ff);
		actual.typeCheck(ff.makeTypeEnvironment());
		assertEquals("Unexpected result of translation", expected, actual);
	}
}
