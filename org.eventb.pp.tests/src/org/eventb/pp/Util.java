package org.eventb.pp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.AtomicPredicateLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.FalseClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.TrueClause;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral.AType;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Divide;
import org.eventb.internal.pp.core.elements.terms.Expn;
import org.eventb.internal.pp.core.elements.terms.IntegerConstant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Minus;
import org.eventb.internal.pp.core.elements.terms.Mod;
import org.eventb.internal.pp.core.elements.terms.Plus;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Times;
import org.eventb.internal.pp.core.elements.terms.UnaryMinus;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.Tracer;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.terms.ConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.DivideSignature;
import org.eventb.internal.pp.loader.formula.terms.ExpnSignature;
import org.eventb.internal.pp.loader.formula.terms.IntegerSignature;
import org.eventb.internal.pp.loader.formula.terms.MinusSignature;
import org.eventb.internal.pp.loader.formula.terms.ModSignature;
import org.eventb.internal.pp.loader.formula.terms.PlusSignature;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TimesSignature;
import org.eventb.internal.pp.loader.formula.terms.UnaryMinusSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableHolder;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;
import org.eventb.internal.pp.loader.predicate.IntermediateResult;
import org.eventb.internal.pp.loader.predicate.IntermediateResultList;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;


public class Util {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	///////////////
	// AST Rodin //
	///////////////
	public static Expression parseExpression(String expression) {
		return ff.parseExpression(expression).getParsedExpression();
	}
	
	public static Predicate parsePredicate(String predicate) {
		return ff.parsePredicate(predicate).getParsedPredicate();
	}
	
	public static Predicate parsePredicate(String predicate, ITypeEnvironment environment) {
		 Predicate pred = ff.parsePredicate(predicate).getParsedPredicate();
		 pred.typeCheck(environment);
		 return pred;
	}

	
	///////////////////////////
	public static ClauseBuilder doPhaseOneAndTwo(String predicate, ITypeEnvironment environment,
			VariableTable table) {
		Predicate pred = parsePredicate(predicate, environment);
		PredicateBuilder pBuilder = new PredicateBuilder();
		pBuilder.build(pred,false);
		ClauseBuilder cBuilder = new ClauseBuilder();
		cBuilder.loadClausesFromContext(pBuilder.getContext(),table);
		return cBuilder;
	}
	
	public static ClauseBuilder doPhaseOneAndTwo(String predicate) {
		Predicate pred = parsePredicate(predicate);
		PredicateBuilder pBuilder = new PredicateBuilder();
		pBuilder.build(pred,false);
		ClauseBuilder cBuilder = new ClauseBuilder();
		cBuilder.loadClausesFromContext(pBuilder.getContext());
		return cBuilder;
	}
	
	public static VariableSignature mVariable(int uniqueIndex, int index) {
		return new VariableSignature(uniqueIndex, index, new Sort(ff.makeGivenType("A")));
	}
	
	public static ConstantSignature mConstant(String name) {
		return new ConstantSignature(name, new Sort(ff.makeGivenType("A")));
	}
	
	public static IntegerSignature mInteger(int value) {
		return new IntegerSignature(BigInteger.valueOf(value));
	}
	
	public static PlusSignature mPlus(TermSignature... children) {
		return new PlusSignature(Arrays.asList(children)); 
	}
	
	public static TimesSignature mTimes(TermSignature... children) {
		return new TimesSignature(Arrays.asList(children)); 
	}
	
	public static MinusSignature mMinus(TermSignature left, TermSignature right) {
		return new MinusSignature(left, right); 
	}
	
	public static DivideSignature mDivide(TermSignature left, TermSignature right) {
		return new DivideSignature(left, right); 
	}

	public static ModSignature mMod(TermSignature left, TermSignature right) {
		return new ModSignature(left, right); 
	}
	
	public static ExpnSignature mExpn(TermSignature left, TermSignature right) {
		return new ExpnSignature(left, right); 
	}
	
	public static UnaryMinusSignature mUnaryMinus(TermSignature child) {
		return new UnaryMinusSignature(child);
	}
	
	public static VariableHolder mVHolder() {
		return new VariableHolder(null);
	}

	public static IIntermediateResult mIR(TermSignature... elements) {
		IntermediateResult result = new IntermediateResult(Arrays.asList(elements));
		return result;
	}
	
	public static IIntermediateResult mIR(IIntermediateResult... elements) {
		IIntermediateResult result = new IntermediateResultList(Arrays.asList(elements));
		return result;
	}

	public static BoundIdentifier mBoundIdentifier(int index) {
		return ff.makeBoundIdentifier(index, null);
	}

	public static BoundIdentifier mBoundIdentifier(int index, Type type) {
		return ff.makeBoundIdentifier(index, null, type);
	}

	public static FreeIdentifier mFreeIdentifier(String name) {
		return ff.makeFreeIdentifier(name, null);
	}

	public static FreeIdentifier mFreeIdentifier(String name, Type type) {
		return ff.makeFreeIdentifier(name, null, type);
	}
	
	public static RelationalPredicate mIn(Expression left, Expression right) {
		return ff.makeRelationalPredicate(Formula.IN, left, right, null);
	}
	
	
	private static class DummyOrigin implements IOrigin {
		private Level level;
		
		public DummyOrigin(Level level) {
			this.level = level;
		}
		
		public boolean dependsOnGoal() {
			return false;
		}

		public void getDependencies(Stack<Level> dependencies) {
			// skip
		}

		public boolean isDefinition() {
			return false;
		}

		public void trace(Tracer tracer) {
			// skip
		}

		public Level getLevel() {
			return level;
		}

		public void getDependencies(Set<Level> dependencies) {
			// skip
		}

		public int getDepth() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public static Sort mSort(Type type) {
		return new Sort(type);
	}
	
	static Sort A = Util.mSort(ff.makeGivenType("A"));
	
	public static Constant cCons(String name, Sort sort) {
		return new Constant(name, sort);
	}
	
	public static IntegerConstant cIntCons(int value) {
		return new IntegerConstant(BigInteger.valueOf(value));
	}
	
	static Constant cCons(String name) {
		return new Constant(name, A);
	}
	
	public static Variable cVar(int index, Sort sort) {
		return new Variable(index, sort);
	}
	
	static Variable cVar(Sort sort) {
		return new Variable(1,sort);
	}
	
	static Variable cVar(int index) {
		return new Variable(index, A);
	}
	
	static Variable cVar() {
		return new Variable(1,A);
	}
	
	public static LocalVariable cFLocVar(int index, Sort sort) {
		return new LocalVariable(index, true, sort);
	}
	
	public static LocalVariable cELocVar(int index, Sort sort) {
		return new LocalVariable(index, false, sort);
	}
	
	static LocalVariable cFLocVar(int index) {
		return new LocalVariable(index, true, A);
	}
	
	static LocalVariable cELocVar(int index) {
		return new LocalVariable(index, false, A);
	}
	
	public static Plus cPlus(Term... terms) {
		return new Plus(Arrays.asList(terms));
	}
	
	public static Times cTimes(Term... terms) {
		return new Times(Arrays.asList(terms));
	}
	
	public static Minus cMinus(Term... terms) {
		return new Minus(Arrays.asList(terms));
	}
	
	public static Divide cDiv(Term... terms) {
		return new Divide(Arrays.asList(terms));
	}
	
	public static UnaryMinus cUnMin(Term... terms) {
		return new UnaryMinus(Arrays.asList(terms));
	}
	
	public static Mod cMod(Term... terms) {
		return new Mod(Arrays.asList(terms));
	}
	
	public static Expn cExpn(Term... terms) {
		return new Expn(Arrays.asList(terms));
	}
	
	private static PredicateLiteralDescriptor descriptor(int index) {
		return new PredicateLiteralDescriptor(index, 0, 0, false, new ArrayList<Sort>());
	}
	
	public static ComplexPredicateLiteral cPred(int index, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(descriptor(index), true, Arrays.asList(terms));
	}
	
	public static ComplexPredicateLiteral cNotPred(int index, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(descriptor(index), false, Arrays.asList(terms));
	}
	
	public static ComplexPredicateLiteral cPred(PredicateLiteralDescriptor descriptor, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(descriptor, true, Arrays.asList(terms));
	}
	
	public static ComplexPredicateLiteral cNotPred(PredicateLiteralDescriptor descriptor, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(descriptor, false, Arrays.asList(terms));
	}
	
	public static PredicateLiteral cProp(int index) {
		return new AtomicPredicateLiteral(descriptor(index), true);
	}
	
	public static PredicateLiteral cNotProp(int index) {
		return new AtomicPredicateLiteral(descriptor(index), false);
	}
	
	public static EqualityLiteral cEqual(SimpleTerm term1, SimpleTerm term2) {
		return new EqualityLiteral(term1, term2, true);
	}
	
	public static EqualityLiteral cNEqual(SimpleTerm term1, SimpleTerm term2) {
		return new EqualityLiteral(term1, term2, false);
	}
	
	public static ArithmeticLiteral cAEqual(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.EQUAL);
	}
	
	public static ArithmeticLiteral cANEqual(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.UNEQUAL);
	}
	
	public static ArithmeticLiteral cLess(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.LESS);
	}
	
	public static ArithmeticLiteral cLE(Term term1, Term term2) {
		return new ArithmeticLiteral(term1,term2,AType.LESS_EQUAL);
	}
	
	protected static Clause TRUE(Level level) {
		return new TrueClause(new DummyOrigin(level));
	}
	
	protected static Clause FALSE(Level level) {
		return new FalseClause(new DummyOrigin(level));
	}
	
	public static Clause newDisjClause(IOrigin origin, List<Literal<?,?>> literals) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new DisjunctiveClause(origin,predicates,equalities,arithmetic);
	}
	
	public static Clause newEqClause(IOrigin origin, List<Literal<?,?>> literals) {
		assert literals.size() > 1;
		
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new EquivalenceClause(origin,predicates,equalities,arithmetic);
	}
	
	public static Clause cClause(Literal<?,?>... literals) {
		Clause clause = newDisjClause(new DummyOrigin(Level.base), mList(literals));
		return clause;
	}
	
	public static Clause cClause(Level level, Literal<?,?>... literals) {
		Clause clause = newDisjClause(new DummyOrigin(level), mList(literals));
		return clause;
	}
	
	public static Clause cClause(List<? extends Literal<?,?>> literals, EqualityLiteral... conditions) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new DisjunctiveClause(new DummyOrigin(Level.base),predicates,equalities,arithmetic,mList(conditions));
	}
	
	public static Clause cEqClause(Literal<?,?>... literals) {
		Clause clause = newEqClause(new DummyOrigin(Level.base),mList(literals));
		return clause;
	}
	
	public static Clause cEqClause(Level level, Literal<?,?>... literals) {
		Clause clause = newEqClause(new DummyOrigin(level),mList(literals));
		return clause;
	}
	
	public static Clause cEqClause(List<? extends Literal<?,?>> literals, EqualityLiteral... conditions) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal<?,?> literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new EquivalenceClause(new DummyOrigin(Level.base),predicates,equalities,arithmetic,mList(conditions));
	}
	
	public static <T> Set<T> mSet(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}

	public static <T> List<T> mList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	
	
}
