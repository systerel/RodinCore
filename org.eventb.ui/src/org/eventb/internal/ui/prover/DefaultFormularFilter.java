package org.eventb.internal.ui.prover;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

public class DefaultFormularFilter implements IFormulaFilter {

	public boolean select(AssociativeExpression expression) {
		return false;
	}

	public boolean select(AssociativePredicate predicate) {
		return false;
	}

	public boolean select(AtomicExpression expression) {
		return false;
	}

	public boolean select(BinaryExpression expression) {
		return false;
	}

	public boolean select(BinaryPredicate predicate) {
		return false;
	}

	public boolean select(BoolExpression expression) {
		return false;
	}

	public boolean select(BoundIdentDecl decl) {
		return false;
	}

	public boolean select(BoundIdentifier identifier) {
		return false;
	}

	public boolean select(FreeIdentifier identifier) {
		return false;
	}

	public boolean select(IntegerLiteral literal) {
		return false;
	}

	public boolean select(LiteralPredicate predicate) {
		return false;
	}

	public boolean select(QuantifiedExpression expression) {
		return false;
	}

	public boolean select(QuantifiedPredicate predicate) {
		return false;
	}

	public boolean select(RelationalPredicate predicate) {
		return false;
	}

	public boolean select(SetExtension expression) {
		return false;
	}

	public boolean select(SimplePredicate predicate) {
		return false;
	}

	public boolean select(UnaryExpression expression) {
		return false;
	}

	public boolean select(UnaryPredicate predicate) {
		return false;
	}

}
