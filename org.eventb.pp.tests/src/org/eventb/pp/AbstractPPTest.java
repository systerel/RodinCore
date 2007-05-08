package org.eventb.pp;

import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cFLocVar;
import static org.eventb.pp.Util.cVar;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.PPTrueClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;

public abstract class AbstractPPTest extends TestCase {

	protected List<IEquality> EMPTY = new ArrayList<IEquality>(); 

	private static FormulaFactory ff = FormulaFactory.getDefault();
	// Types used in these tests
	protected static IntegerType INT = ff.makeIntegerType();
	protected static BooleanType BOOL = ff.makeBooleanType();

	protected static IClause TRUE = Util.TRUE(Level.base);
	protected static IClause FALSE = Util.FALSE(Level.base);
	
	
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
	
	protected static IEquality ab = Util.cEqual(a, b);
	protected static IEquality nab = Util.cNEqual(a, b);
	protected static IEquality bc = Util.cEqual(b, c);
	protected static IEquality nbc = Util.cNEqual(b, c);
	protected static IEquality cd = Util.cEqual(c, d);
	protected static IEquality ncd = Util.cNEqual(c, d);
	
	
	protected static <T> Set<T> mSet(T... elements) {
		return new HashSet<T>(Arrays.asList(elements));
	}
	
}
