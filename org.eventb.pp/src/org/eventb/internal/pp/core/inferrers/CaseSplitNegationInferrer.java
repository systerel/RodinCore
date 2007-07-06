package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.SplitOrigin;

/**
 * This class is responsible for splitting a clause in two.
 * <p>
 * For now, it also contains the logic that chooses where the clause is split.
 * TODO This logic should move in the {@link CaseSplitter}.
 *
 * @author François Terrier
 *
 */
public class CaseSplitNegationInferrer extends AbstractInferrer {

	private Level parent;
	
	public CaseSplitNegationInferrer(IVariableContext context) {
		super(context);
	}
	
	public void setLevel(Level parent) {
		this.parent = parent;
	}
	
	public Clause getLeftCase() {
		return left;
	}

	public Clause getRightCase() {
		return right;
	}

	private Clause left, right;
	private List<PredicateLiteral> leftPredicates = new ArrayList<PredicateLiteral>();
	private List<EqualityLiteral> leftEqualities = new ArrayList<EqualityLiteral>();
	private List<ArithmeticLiteral> leftArithmetic = new ArrayList<ArithmeticLiteral>();
	
	private List<PredicateLiteral> rightPredicates = new ArrayList<PredicateLiteral>();
	private List<EqualityLiteral> rightEqualities = new ArrayList<EqualityLiteral>();
	private List<ArithmeticLiteral> rightArithmetic = new ArrayList<ArithmeticLiteral>();
	
	private void splitLeftCase() {
		// warning, both cases must have distinct variables
		// for now if we do not split on variables it is no problem
		if (hasConstantPredicate(predicates)) {
			PredicateLiteral literal = getConstantLiteral(predicates);
			leftPredicates.add(literal);
			rightPredicates.add(inverseLiteral(literal));
		}
		else if (hasConstantPredicate(equalities)) {
			EqualityLiteral literal = getConstantLiteral(equalities);
			leftEqualities.add(literal);
			rightEqualities.add(inverseLiteral(literal));
		}
		else if (hasConstantPredicate(arithmetic)) {
			ArithmeticLiteral literal = getConstantLiteral(arithmetic);
			leftArithmetic.add(literal);
			rightArithmetic.add(inverseLiteral(literal));
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	private <T extends Literal<T,?>> T getConstantLiteral(List<T> literals) {
		for (T t : literals) {
			if (t.isConstant()) return t;
		}
		return null;
	}
	
	private <T extends Literal<T,?>> T inverseLiteral(T literal) {
		Literal<T,?> result = literal.getInverse();
		Set<LocalVariable> variables = new HashSet<LocalVariable>();
		for (Term term : result.getTerms()) {
			term.collectLocalVariables(variables);
		}
		Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (LocalVariable variable : variables) {
			if (variable.isForall()) map.put(variable, variable.getVariable(context));
		}
		return result.substitute(map);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		splitLeftCase();
		left = new DisjunctiveClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic);
		// right case
		right = new DisjunctiveClause(getOrigin(clause, parent.getRightBranch()),rightPredicates,rightEqualities,rightArithmetic);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		splitLeftCase();
		left = EquivalenceClause.newClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic,new ArrayList<EqualityLiteral>(),context);
		// right case
//		EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		right = EquivalenceClause.newClause(getOrigin(clause, parent.getRightBranch()), rightPredicates, rightEqualities, rightArithmetic,new ArrayList<EqualityLiteral>(),context);
	}

	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		// we should do this, for performance reason we let it down
		// if (!canInfer(clause)) throw new IllegalStateException(); 
		if (parent == null) throw new IllegalStateException();

		left = null;
		right = null;
		leftArithmetic = new ArrayList<ArithmeticLiteral>();
		leftEqualities = new ArrayList<EqualityLiteral>();
		leftPredicates = new ArrayList<PredicateLiteral>();
		rightArithmetic = new ArrayList<ArithmeticLiteral>();
		rightEqualities = new ArrayList<EqualityLiteral>();
		rightPredicates = new ArrayList<PredicateLiteral>();
	}

	@Override
	protected void reset() {
		parent = null;
		
	}

	protected IOrigin getOrigin(Clause clause, Level level) {
		List<Clause> parents = new ArrayList<Clause>();
		parents.add(clause);
		return new SplitOrigin(parents, level);
	}

	private int MAX_SPLIT_SIZE = 3;
	
	public boolean canInfer(Clause clause) {
		if (clause.isEmpty()) return false;
		if (clause.isUnit()) return false;
		if (clause.getOrigin().isDefinition()) return false;
		if (clause.getConditions().size() > 0) return false;
		
		Set<Level> dependencies = new HashSet<Level>();
		clause.getOrigin().getDependencies(dependencies);
		if (dependencies.size() > MAX_SPLIT_SIZE) {
			return false;
		}
		
//		if (clause.getOrigin().getLevel().getHeight()>2) return false;
		
//		if (clause.getArithmeticLiterals().size()+clause.getEqualityLiterals().size()+clause.getPredicateLiterals().size()>2) return false;
//		if (!clause.getOrigin().dependsOnGoal()) return false;
		
		if (!(	hasConstantPredicate(clause.getPredicateLiterals())
				|| hasConstantPredicate(clause.getArithmeticLiterals())
				|| hasConstantPredicate(clause.getEqualityLiterals()))
		) return false;
		return true;
	}
	
	private boolean hasConstantPredicate(List<? extends Literal<?,?>> literals) {
		for (Literal<?,?> literal : literals) {
			if (literal.isConstant()) return true;
		}
		return false;
	}
	


}
