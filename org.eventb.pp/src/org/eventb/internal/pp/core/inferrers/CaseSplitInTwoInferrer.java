package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
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
public class CaseSplitInTwoInferrer extends AbstractInferrer {

	private Level parent;
	
	public CaseSplitInTwoInferrer(IVariableContext context) {
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
	private List<PredicateLiteral> leftPredicates;
	private List<EqualityLiteral> leftEqualities;
	private List<ArithmeticLiteral> leftArithmetic;
	
	private void splitLeftCase() {
		// warning, both cases must have distinct variables
		// for now if we do not split on variables it is no problem
		if (predicates.size() >= 1) {
			PredicateLiteral literal = predicates.remove(0);
			leftPredicates.add(literal);
		}
		else if (equalities.size() >= 1) {
			EqualityLiteral literal = equalities.remove(0);
			leftEqualities.add(literal);
		}
		else if (arithmetic.size() >= 1) {
			ArithmeticLiteral literal = arithmetic.remove(0);
			leftArithmetic.add(literal);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		splitLeftCase();
		left = new DisjunctiveClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic);
		// right case
		right = new DisjunctiveClause(getOrigin(clause, parent.getRightBranch()),predicates,equalities,arithmetic);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		splitLeftCase();
		left = EquivalenceClause.newClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic,new ArrayList<EqualityLiteral>(),context);
		// right case
		EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		right = EquivalenceClause.newClause(getOrigin(clause, parent.getRightBranch()), predicates, equalities, arithmetic,new ArrayList<EqualityLiteral>(),context);
	}

	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		// we should do this, for performance reason we let it down
		// if (!canInfer(clause)) throw new IllegalStateException(); 
		if (parent == null) throw new IllegalStateException();
		
		leftArithmetic = new ArrayList<ArithmeticLiteral>();
		leftEqualities = new ArrayList<EqualityLiteral>();
		leftPredicates = new ArrayList<PredicateLiteral>();
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

	public boolean canInfer(Clause clause) {
		if (clause.isUnit()) return false;
		if (clause.getOrigin().isDefinition()) return false;
		if (clause.isBlockedOnConditions()) return false;
		
		if (!(	hasConstantLiteral(clause.getPredicateLiterals())
				|| hasConstantLiteral(clause.getArithmeticLiterals())
				|| hasConstantLiteral(clause.getEqualityLiterals())
				|| hasConstantLiteral(clause.getConditions()))
		) return false;
		
//		if (!clause.getOrigin().dependsOnGoal()) return false;
		
		return true;
	}
	
	private boolean hasConstantLiteral(List<? extends Literal<?,?>> literals) {
		for (Literal<?,?> literal : literals) {
			if (literal.isConstant()) return true;
		}
		return false;
	}
	

}
