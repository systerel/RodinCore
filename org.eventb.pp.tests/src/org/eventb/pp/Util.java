package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.TrueClause;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral.AType;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Divide;
import org.eventb.internal.pp.core.elements.terms.Expn;
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
import org.eventb.internal.pp.loader.clause.LoaderResult;
import org.eventb.internal.pp.loader.formula.ISubFormula;
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.ConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.DivideSignature;
import org.eventb.internal.pp.loader.formula.terms.ExpnSignature;
import org.eventb.internal.pp.loader.formula.terms.MinusSignature;
import org.eventb.internal.pp.loader.formula.terms.ModSignature;
import org.eventb.internal.pp.loader.formula.terms.PlusSignature;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TimesSignature;
import org.eventb.internal.pp.loader.formula.terms.UnaryMinusSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableHolder;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;
import org.eventb.internal.pp.loader.predicate.IContext;
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
	public static LoaderResult doPhaseOneAndTwo(String predicate, ITypeEnvironment environment) {
		Predicate pred = parsePredicate(predicate, environment);
		PredicateBuilder pBuilder = new PredicateBuilder();
		pBuilder.build(pred,false);
		ClauseBuilder cBuilder = new ClauseBuilder();
		cBuilder.buildClauses(pBuilder.getContext());
		return cBuilder.getResult();
	}
	
	public static LoaderResult doPhaseOneAndTwo(String predicate) {
		Predicate pred = parsePredicate(predicate);
		PredicateBuilder pBuilder = new PredicateBuilder();
		pBuilder.build(pred,false);
		ClauseBuilder cBuilder = new ClauseBuilder();
		cBuilder.buildClauses(pBuilder.getContext());
		return cBuilder.getResult();
	}
	
	
	/////////////
	// PHASE 1 //
	/////////////
	public static Constant mConstant(String name, Sort sort) {
		return new Constant(name, sort);
	}
	
	public static Sort mSort(Type type) {
		return new Sort(type);
	}
	
	public static Sort INTEGER() {
		return Sort.ARITHMETIC;
	}
	
	public static Literal[] mArr(Literal... literals) {
		return literals;
	}
	
	public static Literal[][] mArr(Literal[]... literals) {
		return literals;
	}
	
	public static <T> List<T> mList(T... elements) {
		List<T> list = new ArrayList<T>();
		list.addAll(Arrays.asList(elements));
		return list;
	}
	
	public static List<Clause> mList(Clause... clause) {
		List<Clause> list = new ArrayList<Clause>();
		list.addAll(Arrays.asList(clause));
		return list;
	}
	
	public static List<TermSignature> mList(TermSignature... terms) {
		List<TermSignature> list = new ArrayList<TermSignature>();
		list.addAll(Arrays.asList(terms));
		return list;
	}
	
	public static List<ISubFormula> mList(ISubFormula... sigs) {
		List<ISubFormula> list = new ArrayList<ISubFormula>();
		list.addAll(Arrays.asList(sigs));
		return list;
	}
	
	public static <T extends Literal> List<T> mList(T... literals) {
		List<T> list = new ArrayList<T>();
		list.addAll(Arrays.asList(literals));
		return list;
	}
	
//	public static PredicateFormula mPredicate() {
//		return new PredicateFormula(0);
//	}
//
//	public static QuantifiedFormula mQPredicate(QuantifiedLiteralKey sig) {
//		return new QuantifiedFormula(0, sig.isForall(),sig.getSignature(),sig.getTerms());
//	}
//	
//	public static PredicateFormula mPredicate(int index) {
//		return new PredicateFormula(index);
//	}
//	
//	public static EqualityFormula mEqualitySig(Sort sort) {
//		return new EqualityFormula(sort);
//	}
//	
//	public static DisjunctiveClause mSet(List<ISignedFormula> signatures) {
//		return new DisjunctiveClause(0, signatures);
//	}
	
	public static VariableSignature mVariable(int uniqueIndex, int index) {
		return new VariableSignature(uniqueIndex, index, new Sort(ff.makeGivenType("A")));
	}
	
	public static ConstantSignature mConstant(String name) {
		return new ConstantSignature(name, new Sort(ff.makeGivenType("A")));
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
	
	
	
	/////////////
	// PHASE 2 //
	/////////////
	private static class DummyOrigin implements IOrigin {
		private Level level;
		
		public DummyOrigin(Level level) {
			this.level = level;
		}
		
		public boolean dependsOnGoal() {
			return false;
		}

		public void getDependencies(Stack<Level> dependencies) {
		}

		public boolean isDefinition() {
			return false;
		}

		public void trace(Tracer tracer) {
		}

		public Level getLevel() {
			return level;
		}

		public void getDependencies(Set<Level> dependencies) {
			
		}
		
	}
	
	
	public static Clause newDisjClause(IOrigin origin, List<Literal> literals) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new DisjunctiveClause(origin,predicates,equalities,arithmetic);
	}
	
	public static Clause newEqClause(IOrigin origin, List<Literal> literals) {
		assert literals.size() > 1;
		
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new EquivalenceClause(origin,predicates,equalities,arithmetic);
	}
	
	
	public static Clause cClause(Literal... literals) {
		Clause clause = newDisjClause(new DummyOrigin(Level.base), mList(literals));
		return clause;
	}
	
	
	public static Clause TRUE(Level level) {
		return new TrueClause(new DummyOrigin(level));
	}
	
	public static Clause FALSE(Level level) {
		return new FalseClause(new DummyOrigin(level));
	}
	
	
	public static Clause cClause(Level level, Literal... literals) {
		Clause clause = newDisjClause(new DummyOrigin(level), mList(literals));
		return clause;
	}
	
	public static Clause cClause(List<? extends Literal> literals, EqualityLiteral... conditions) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new DisjunctiveClause(new DummyOrigin(Level.base),predicates,equalities,arithmetic,(List)mList(conditions));
	}
	
	public static Clause cEqClause(Literal... literals) {
		Clause clause = newEqClause(new DummyOrigin(Level.base),mList(literals));
		return clause;
	}
	
	public static Clause cEqClause(Level level, Literal... literals) {
		Clause clause = newEqClause(new DummyOrigin(level),mList(literals));
		return clause;
	}
	
	public static Clause cEqClause(List<? extends Literal> literals, EqualityLiteral... conditions) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		for (Literal literal : literals) {
			if (literal instanceof PredicateLiteral) predicates.add((PredicateLiteral)literal);
			else if (literal instanceof EqualityLiteral) equalities.add((EqualityLiteral)literal);
			else if (literal instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)literal);
		}
		return new EquivalenceClause(new DummyOrigin(Level.base),predicates,equalities,arithmetic,(List)mList(conditions));
	}
	
	public static ComplexPredicateLiteral cPred(int index, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(new org.eventb.internal.pp.core.elements.PredicateDescriptor(index, true), Arrays.asList(terms));
	}
	
	public static ComplexPredicateLiteral cNotPred(int index, SimpleTerm... terms) {
		return new ComplexPredicateLiteral(new org.eventb.internal.pp.core.elements.PredicateDescriptor(index, false), Arrays.asList(terms));
	}
	
	public static PredicateLiteral cProp(int index) {
		return new AtomicPredicateLiteral(new org.eventb.internal.pp.core.elements.PredicateDescriptor(index, true));
	}
	
	public static PredicateLiteral cNotProp(int index) {
		return new AtomicPredicateLiteral(new org.eventb.internal.pp.core.elements.PredicateDescriptor(index, false));
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
	
	
	public static Constant cCons(String name) {
		return new Constant(name, null);
	}
	
	public static Variable cVar(Sort sort) {
		return new Variable(sort);
	}
	
	public static LocalVariable cFLocVar(int index, Sort sort) {
		return new LocalVariable(index, true, sort);
	}
	
	public static LocalVariable cELocVar(int index, Sort sort) {
		return new LocalVariable(index, false, sort);
	}
	
	
	private static Sort A = new Sort(ff.makeGivenType("A"));
	public static Variable cVar() {
		return new Variable(A);
	}
	
	public static LocalVariable cFLocVar(int index) {
		return new LocalVariable(index, true, A);
	}
	
	public static LocalVariable cELocVar(int index) {
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
	
	public static IIntermediateResult mIR(TermSignature... elements) {
		IntermediateResult result = new IntermediateResult(Arrays.asList(elements));
		return result;
	}
	
	public static IIntermediateResult mIR(IIntermediateResult... elements) {
		IIntermediateResult result = new IntermediateResultList(Arrays.asList(elements));
		return result;
	}
	
	public static QuantifiedDescriptor Q(IContext context, int index, String literal, List<TermSignature> terms, IIntermediateResult... ir) {
		QuantifiedDescriptor desc = new QuantifiedDescriptor(context,index);
		for (IIntermediateResult list : ir) {
			desc.addResult(list);
		}
		return desc;
	}
//	
	public static PredicateDescriptor P(IContext context, int index, IIntermediateResult... ir) {
		PredicateDescriptor desc = new PredicateDescriptor(context, index);
		for (IIntermediateResult list : ir) {
			desc.addResult(list);
		}
		return desc;
	}
	
	public static DisjunctiveClauseDescriptor L(IContext context, int index, List<String> literals, IIntermediateResult... ir) {
		DisjunctiveClauseDescriptor desc = new DisjunctiveClauseDescriptor(context, index);
		for (IIntermediateResult list : ir) {
			desc.addResult(list);
		}
		return desc;
	}
	
	public static EqualityDescriptor E(IContext context, Sort sort, IIntermediateResult... ir) {
		EqualityDescriptor desc = new EqualityDescriptor(context, sort);
		for (IIntermediateResult list : ir) {
			desc.addResult(list);
		}
		return desc;
	}
	
//	///////////////////
//	public static VariableSignature V(int index) {
//		return new VariableSignature(index,null);
//	}
//	
//	public static ConstantIndex C(TermSignature term) {
//		return new ConstantIndex(term);
//	}
//	
//	public static List<Index> L(Index... indexes) {
//		return Arrays.asList(indexes);
//	}
	
	///////////// copy pasted from org.eventb.core.ast.tests
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
	////////////////////////////////////////////////////////
	

	
}
