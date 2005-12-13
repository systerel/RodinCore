package org.eventb.core.ast.tests;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Provides insight into the implementation of formulae.
 * 
 * Most notably the number of tags for each kind of formula.
 * 
 * @author Laurent Voisin
 */
public interface ITestHelper {
	
	int ASSOCIATIVE_EXPRESSION_LENGTH = AssociativeExpression.TAGS_LENGTH;

	int ASSOCIATIVE_PREDICATE_LENGTH = AssociativePredicate.TAGS_LENGTH;
	
	int ATOMIC_EXPRESSION_LENGTH = AtomicExpression.TAGS_LENGTH;
	
	int BINARY_EXPRESSION_LENGTH = BinaryExpression.TAGS_LENGTH;

	int BINARY_PREDICATE_LENGTH = BinaryPredicate.TAGS_LENGTH;

	int LITERAL_PREDICATE_LENGTH = LiteralPredicate.TAGS_LENGTH;
	
	int QUANTIFIED_EXPRESSION_LENGTH = QuantifiedExpression.TAGS_LENGTH;
	
	int QUANTIFIED_PREDICATE_LENGTH = QuantifiedPredicate.TAGS_LENGTH;
	
	int RELATIONAL_PREDICATE_LENGTH = RelationalPredicate.TAGS_LENGTH;
	
	int UNARY_EXPRESSION_LENGTH = UnaryExpression.TAGS_LENGTH;
	
	int UNARY_PREDICATE_LENGTH = UnaryPredicate.TAGS_LENGTH;
	
}
