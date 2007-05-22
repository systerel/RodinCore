package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
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
	
	public IClause getLeftCase() {
		return left;
	}

	public IClause getRightCase() {
		return right;
	}

	private IClause left, right;
	private List<IPredicate> leftPredicates = new ArrayList<IPredicate>();
	private List<IEquality> leftEqualities = new ArrayList<IEquality>();
	private List<IArithmetic> leftArithmetic = new ArrayList<IArithmetic>();
	
	private List<IPredicate> rightPredicates = new ArrayList<IPredicate>();
	private List<IEquality> rightEqualities = new ArrayList<IEquality>();
	private List<IArithmetic> rightArithmetic = new ArrayList<IArithmetic>();
	
	private void splitLeftCase() {
		// warning, both cases must have distinct variables
		// for now if we do not split on variables it is no problem
		if (predicates.size() >= 1) {
			IPredicate literal = predicates.get(0);
			leftPredicates.add(literal);
			rightPredicates.add(inverseLiteral(literal));
		}
		else if (equalities.size() >= 1) {
			IEquality literal = equalities.get(0);
			leftEqualities.add(literal);
			rightEqualities.add(inverseLiteral(literal));
		}
		else if (arithmetic.size() >= 1) {
			IArithmetic literal = arithmetic.get(0);
			leftArithmetic.add(literal);
			rightArithmetic.add(inverseLiteral(literal));
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	private <T extends ILiteral<T>> T inverseLiteral(T literal) {
		ILiteral<T> result = literal.getInverse();
		List<LocalVariable> variables = new ArrayList<LocalVariable>();
		for (Term term : result.getTerms()) {
			term.collectLocalVariables(variables);
		}
		Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
		for (LocalVariable variable : variables) {
			if (variable.isForall()) map.put(variable, variable.getVariable(context));
		}
		return result.substitute(map);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(IClause clause) {
		splitLeftCase();
		left = new PPDisjClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic);
		// right case
		right = new PPDisjClause(getOrigin(clause, parent.getRightBranch()),rightPredicates,rightEqualities,rightArithmetic);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(IClause clause) {
		splitLeftCase();
		left = PPEqClause.newClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic,new ArrayList<IEquality>(),context);
		// right case
//		PPEqClause.inverseOneliteral(predicates, equalities, arithmetic);
		right = PPEqClause.newClause(getOrigin(clause, parent.getRightBranch()), rightPredicates, rightEqualities, rightArithmetic,new ArrayList<IEquality>(),context);
	}

	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		// we should do this, for performance reason we let it down
		// if (!canInfer(clause)) throw new IllegalStateException(); 
		if (parent == null) throw new IllegalStateException();

		left = null;
		right = null;
		leftArithmetic = new ArrayList<IArithmetic>();
		leftEqualities = new ArrayList<IEquality>();
		leftPredicates = new ArrayList<IPredicate>();
		rightArithmetic = new ArrayList<IArithmetic>();
		rightEqualities = new ArrayList<IEquality>();
		rightPredicates = new ArrayList<IPredicate>();
	}

	@Override
	protected void reset() {
		parent = null;
		
	}

	protected IOrigin getOrigin(IClause clause, Level level) {
		List<IClause> parents = new ArrayList<IClause>();
		parents.add(clause);
		return new SplitOrigin(parents, level);
	}

	public boolean canInfer(IClause clause) {
		if (clause.isEmpty()) return false;
		if (clause.isUnit()) return false;
		if (clause.getOrigin().isDefinition()) return false;
		if (clause.getConditions().size() > 0) return false;
		
//		if (!clause.getOrigin().dependsOnGoal()) return false;
		
		if (!isConstant(clause.getPredicateLiterals()) || !isConstant(clause.getEqualityLiterals()) || !isConstant(clause.getArithmeticLiterals())) return false;
		return true;
	}
	
	private boolean isConstant(List<? extends ILiteral<?>> literals) {
		for (ILiteral<?> lit : literals) {
			if (!lit.isConstant()) return false;
		}
		return true;
	}


}
