package org.eventb.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.eventb.internal.pp.core.elements.AbstractPPClause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPArithmetic;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPEquality;
import org.eventb.internal.pp.core.elements.PPPredicate;
import org.eventb.internal.pp.core.elements.PPProposition;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.PPArithmetic.AType;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Divide;
import org.eventb.internal.pp.core.elements.terms.Expn;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Minus;
import org.eventb.internal.pp.core.elements.terms.Mod;
import org.eventb.internal.pp.core.elements.terms.Plus;
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
import org.eventb.internal.pp.loader.ordering.TermOrderer;
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
		return cBuilder.buildClauses(pBuilder.getContext());
	}
	
	public static LoaderResult doPhaseOneAndTwo(String predicate) {
		Predicate pred = parsePredicate(predicate);
		PredicateBuilder pBuilder = new PredicateBuilder();
		pBuilder.build(pred,false);
		ClauseBuilder cBuilder = new ClauseBuilder();
		return cBuilder.buildClauses(pBuilder.getContext());
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
	
	public static ILiteral[] mArr(ILiteral... literals) {
		return literals;
	}
	
	public static ILiteral[][] mArr(ILiteral[]... literals) {
		return literals;
	}
	
	public static <T> List<T> mList(T... elements) {
		List<T> list = new ArrayList<T>();
		list.addAll(Arrays.asList(elements));
		return list;
	}
	
	public static List<IClause> mList(IClause... clause) {
		List<IClause> list = new ArrayList<IClause>();
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
	
	public static List<ILiteral> mList(ILiteral... literals) {
		List<ILiteral> list = new ArrayList<ILiteral>();
		list.addAll(Arrays.asList(literals));
		return list;
	}
	
//	public static PredicateLiteral mPredicate() {
//		return new PredicateLiteral(0);
//	}
//
//	public static QuantifiedLiteral mQPredicate(QuantifiedLiteralKey sig) {
//		return new QuantifiedLiteral(0, sig.isForall(),sig.getSignature(),sig.getTerms());
//	}
//	
//	public static PredicateLiteral mPredicate(int index) {
//		return new PredicateLiteral(index);
//	}
//	
//	public static EqualityLiteral mEqualitySig(Sort sort) {
//		return new EqualityLiteral(sort);
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
		
	}
	
	public static IClause cClause(ILiteral... literals) {
		IClause clause = ClauseFactory.getDefault().newDisjClause(mList(literals));
		clause.setOrigin(new DummyOrigin());
		return clause;
	}
	
	
	public static IClause cClause(Level level, ILiteral... literals) {
		IClause clause = ClauseFactory.getDefault().newDisjClause(level, mList(literals));
		clause.setOrigin(new DummyOrigin());
		return clause;
	}
	
	public static IClause cEqClause(ILiteral... literals) {
		IClause clause = ClauseFactory.getDefault().newEqClause(mList(literals));
		clause.setOrigin(new DummyOrigin());
		return clause;
	}
	
	public static IClause cEqClause(Level level, ILiteral... literals) {
		IClause clause = ClauseFactory.getDefault().newEqClause(level,mList(literals));
		clause.setOrigin(new DummyOrigin());
		return clause;
	}
	
	public static AbstractPPClause cEqClause(List<ILiteral> literals, IEquality... conditions) {
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		for (ILiteral literal : literals) {
			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
		}
		return new PPEqClause(Level.base,predicates,equalities,arithmetic,(List)mList(conditions));
	}
	
	public static PPPredicate cPred(int index, Term... terms) {
		return new PPPredicate(index, true, Arrays.asList(terms));
	}
	
	public static PPPredicate cNotPred(int index, Term... terms) {
		return new PPPredicate(index, false, Arrays.asList(terms));
	}
	
	public static PPProposition cProp(int index) {
		return new PPProposition(index, true);
	}
	
	public static PPProposition cNotProp(int index) {
		return new PPProposition(index, false);
	}
	
	public static PPEquality cEqual(Term term1, Term term2) {
		return new PPEquality(term1, term2, true);
	}
	
	public static PPEquality cNEqual(Term term1, Term term2) {
		return new PPEquality(term1, term2, false);
	}
	
	
	public static PPArithmetic cAEqual(Term term1, Term term2) {
		return new PPArithmetic(term1,term2,AType.EQUAL);
	}
	
	public static PPArithmetic cANEqual(Term term1, Term term2) {
		return new PPArithmetic(term1,term2,AType.UNEQUAL);
	}
	
	public static PPArithmetic cLess(Term term1, Term term2) {
		return new PPArithmetic(term1,term2,AType.LESS);
	}
	
	public static PPArithmetic cLE(Term term1, Term term2) {
		return new PPArithmetic(term1,term2,AType.LESS_EQUAL);
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
		IntermediateResult result = new IntermediateResult(Arrays.asList(elements), new TermOrderer());
		return result;
	}
	
	public static IIntermediateResult mIR(IIntermediateResult... elements) {
		IIntermediateResult result = new IntermediateResultList(Arrays.asList(elements));
		return result;
	}
	
	public static QuantifiedDescriptor Q(IContext context, int index, String literal, List<TermSignature> terms, IIntermediateResult... ir) {
		QuantifiedDescriptor desc = new QuantifiedDescriptor(context, index,terms);
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
