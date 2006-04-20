package org.eventb.internal.pp.translator.todel;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

public abstract class IdentityVisitorBase {
 	
	protected abstract Expression visit(Expression expr);
	protected abstract Predicate visit(Predicate expr);
	
	protected void visitAssociativeExpression(	AssociativeExpression expr) {
		
		for (Expression child: expr.getChildren()) {visit(child);}
	}
	
	
	protected void visitBinaryExpression(BinaryExpression expr) {

		visit(expr.getLeft());
		visit(expr.getRight());
	}
	
	protected void visitBoolExpression(BoolExpression expr) {

		visit(expr.getPredicate());
	}

	protected void visitQuantifiedExpression(
			QuantifiedExpression expr) {

		visit(expr.getPredicate());
		visit(expr.getExpression());
	}
	
	protected void visitSetExtension(SetExtension expr) {

		for (Expression child: expr.getMembers()) {
			Expression newChild = visit(child);
		}
	}
	protected void idTransUnaryExpression(UnaryExpression expr) {

		visit(expr.getChild());
	}
	
	protected void visitAssociativePredicate(AssociativePredicate pred) {

		for (Predicate child: pred.getChildren()) {
			visit(child);
		}
	}

	protected void visitBinaryPredicate(BinaryPredicate pred) {
		visit(pred.getLeft());
		visit(pred.getRight());
	}
	
	protected void visitUnaryPredicate(UnaryPredicate pred) {

		visit(pred.getChild());
	}
	
	protected void visitSimplePredicate(SimplePredicate pred) {
		
		visit(pred);
	}
	
	protected void visitRelationalPredicate(RelationalPredicate pred) {

		visit(pred.getLeft());
		visit(pred.getRight());
	}
	
	protected void visitTransQuantifiedPredicate(QuantifiedPredicate pred) {
		visit(pred.getPredicate());
	}
	
}
