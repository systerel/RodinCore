/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import static org.eventb.core.EventBPlugin.MODIFICATION;
import static org.eventb.core.EventBPlugin.REFERENCE;
import static org.rodinp.core.RodinCore.getInternalLocation;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class FormulaIndexer extends DefaultVisitor {
	
	private final IdentTable visibleIdents;
	private final IIndexingBridge bridge;

	private static final class BoundRewriter extends DefaultRewriter {

		private final BoundIdentDecl[] boundIdents;
		
		public BoundRewriter(BoundIdentDecl[] boundIdents) {
			super(false);
			this.boundIdents = boundIdents;
		}
		
		@Override
		public Expression rewrite(BoundIdentifier identifier) {
			final int offsetIndex = identifier.getBoundIndex() - getBindingDepth();
			if (offsetIndex < 0) {
				return identifier;
			}
			final BoundIdentDecl boundIdentDecl = getDeclaration(offsetIndex);
			final String name = boundIdentDecl.getName();
			return identifier.getFactory().makeFreeIdentifier(name,
					identifier.getSourceLocation());
		}

		private BoundIdentDecl getDeclaration(int boundIndex) {
			return boundIdents[boundIdents.length - boundIndex - 1];
		}
	}

	public FormulaIndexer(IdentTable visibleIdents, IIndexingBridge bridge) {
		this.visibleIdents = visibleIdents;
		this.bridge = bridge;
	}

	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		addOccIfVisible(ident, REFERENCE);
		return true;
	}

	// Assignments need special processing in order to distinguish
	// MODIFICATION occurrences on the left-hand side
	// REFERENCE    occurrences on the right-hand side 
	@Override
	public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return false;
	}

	@Override
	public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		indexAssignedIdents(assign);
		for (Expression expression : assign.getExpressions()) {
			expression.accept(this);
		}
		return true;
	}

	@Override
	public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return false;
	}

	@Override
	public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		indexAssignedIdents(assign);
		assign.getSet().accept(this);
		return true;
	}
	
	@Override
	public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return false;
	}
	
	@Override
	public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		indexAssignedIdents(assign);
		// change primed bound identifiers into free identifiers
		final BoundRewriter primedRewriter = new BoundRewriter(assign
				.getPrimedIdents());
		final Predicate rewrittenCondition = assign.getCondition().rewrite(
				primedRewriter);

		rewrittenCondition.accept(this);
		return true;
	}
	
	private void indexAssignedIdents(Assignment assign) {
		for (FreeIdentifier ident : assign.getAssignedIdentifiers()) {
			addOccIfVisible(ident, MODIFICATION);
		}
	}

	private void addOccIfVisible(FreeIdentifier ident, IOccurrenceKind kind) {
		final IDeclaration declaration = visibleIdents.get(ident);
		if (declaration == null) {
			// Ignore an undeclared identifier
			return;
		}
		final IInternalLocation loc = getRodinLocation(ident);
		bridge.addOccurrence(declaration, kind, loc);
	}

	/**
	 * Returns the Rodin location for the given formula, assuming that the
	 * source location of the given formula contains an attribute location of
	 * type <code>String</code>.
	 * <p>
	 * Note that when extracting a location from a <code>SourceLocation</code>,
	 * using this method is mandatory, as long as {@link SourceLocation} and
	 * {@link IAttributeSubstringLocation} do not share the same range
	 * convention.
	 * </p>
	 * 
	 * @param formula
	 *            a formula
	 * @return the corresponding IInternalLocation
	 */
	private static IInternalLocation getRodinLocation(Formula<?> formula) {
		final SourceLocation srcLoc = formula.getSourceLocation();
		final IAttributeLocation elemLoc = (IAttributeLocation) srcLoc
				.getOrigin();
		final IInternalElement element = elemLoc.getElement();
		final IAttributeType.String attrType = (IAttributeType.String) elemLoc
				.getAttributeType();
		final int start = srcLoc.getStart();
		final int end = srcLoc.getEnd() + 1;
		return getInternalLocation(element, attrType, start, end);
	}

}
