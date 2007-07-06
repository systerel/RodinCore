package org.eventb.pp;

import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cFLocVar;
import static org.eventb.pp.Util.cVar;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;

public abstract class AbstractPPTest extends TestCase {

	protected List<EqualityLiteral> EMPTY = new ArrayList<EqualityLiteral>(); 

	private static FormulaFactory ff = FormulaFactory.getDefault();
	// Types used in these tests
	protected static IntegerType INT = ff.makeIntegerType();
	protected static BooleanType BOOL = ff.makeBooleanType();

	protected static Clause TRUE = Util.TRUE(Level.base);
	protected static Clause FALSE = Util.FALSE(Level.base);
	
	
	protected static GivenType ty_S = ff.makeGivenType("S");
	protected static GivenType ty_T = ff.makeGivenType("T");
	protected static GivenType ty_U = ff.makeGivenType("U");
	protected static GivenType ty_V = ff.makeGivenType("V");

	protected static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	protected static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	protected static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	protected static Level BASE = Level.base;
	protected static Level ONE = new Level(BigInteger.ONE);
	protected static Level TWO = new Level(BigInteger.valueOf(2));
	protected static Level THREE = new Level(BigInteger.valueOf(3));
	protected static Level FOUR = new Level(BigInteger.valueOf(4));
	protected static Level FIVE = new Level(BigInteger.valueOf(5));
	protected static Level SIX = new Level(BigInteger.valueOf(6));
	protected static Level SEVEN = new Level(BigInteger.valueOf(7));
	protected static Level EIGHT = new Level(BigInteger.valueOf(8));
	protected static Level NINE = new Level(BigInteger.valueOf(9));
	protected static Level TEN = new Level(BigInteger.valueOf(10));
	protected static Level ELEVEN = new Level(BigInteger.valueOf(11));
	protected static Level NINETEEN = new Level(BigInteger.valueOf(19));
	protected static Level TWENTY = new Level(BigInteger.valueOf(20));
	
	
	
	protected static Variable x = cVar();
	protected static Variable y = cVar();
	protected static Variable z = cVar();
	protected static Constant a = cCons("a");
	protected static Constant b = cCons("b");
	protected static Constant c = cCons("c");
	protected static Constant d = cCons("d");
	protected static Constant e = cCons("e");
	protected static Constant f = cCons("f");
	
	protected static Variable var0 = Util.cVar();
	protected static Variable var1 = Util.cVar();
	protected static Variable var2 = Util.cVar();
	protected static Variable var3 = Util.cVar();
	protected static Variable var4 = Util.cVar();
	
	protected static LocalVariable evar0 = cELocVar(0);
	protected static LocalVariable evar1 = cELocVar(1);
	protected static LocalVariable fvar0 = cFLocVar(0);
	protected static LocalVariable fvar1 = cFLocVar(1);
	
	protected static EqualityLiteral ab = Util.cEqual(a, b);
	protected static EqualityLiteral ac = Util.cEqual(a, c);
	protected static EqualityLiteral nab = Util.cNEqual(a, b);
	protected static EqualityLiteral bc = Util.cEqual(b, c);
	protected static EqualityLiteral nbc = Util.cNEqual(b, c);
	protected static EqualityLiteral cd = Util.cEqual(c, d);
	protected static EqualityLiteral ncd = Util.cNEqual(c, d);
	protected static EqualityLiteral nbd = Util.cNEqual(b, d);
	protected static EqualityLiteral nac = Util.cNEqual(a, c);
	
	
	protected static EqualityLiteral xa = Util.cEqual(x, a);
	protected static EqualityLiteral xb = Util.cEqual(x, b);
	protected static EqualityLiteral yb = Util.cEqual(y, b);
	protected static EqualityLiteral nxa = Util.cNEqual(x, a);
	protected static EqualityLiteral nxb = Util.cNEqual(x, b);
	protected static EqualityLiteral xc = Util.cEqual(x, c);
	protected static EqualityLiteral xd = Util.cEqual(x, d);
	
	
	protected static <T> Set<T> mSet(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}
	
	public void initDebug() {
//		PredicateBuilder.DEBUG = true;
//		ClauseBuilder.DEBUG = true;
		
		PPProof.DEBUG = true;
		ClauseDispatcher.DEBUG = true;
		PredicateProver.DEBUG = true;
		ClauseSimplifier.DEBUG = true;
		CaseSplitter.DEBUG = true;
//		Dumper.DEBUG = true;
	}
}
