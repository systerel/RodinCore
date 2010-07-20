/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import static org.eventb.core.ast.Formula.MAPSTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;

/**
 * Support for parsing a pattern of a lambda expression. This class is used by
 * the mathematical parser to build a lambda pattern, while checking its
 * well-formedness. Most notably, it ensures that names occurring in the pattern
 * are pairwise distinct.
 * <p>
 * To use this class, the parser calls methods
 * {@link #declParsed(BoundIdentDecl)} and {@link #mapletParsed(SourceLocation)}
 * during the parse. Once the pattern has been fully parsed, declarations are
 * available through {@link #getDecls()} and the final pattern (made of bound
 * identifiers) is built and returned by {@link #getPattern()}.
 * </p>
 * <p>
 * If a name is duplicated, an error is added to the parser result passed to the
 * constructor, and the erroneous declaration is replaced by a dummy expression
 * (placeholder), to support error recovery in the parser.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class Pattern {

	private final ParseResult result;
	private final FormulaFactory ff;

	// Declaration names since so far
	private final Set<String> names;

	// Declarations
	private final List<BoundIdentDecl> decls;

	// Stack of parsed expressions
	private final Stack<Expression> stack;

	// Declarations as free identifiers (will be bound in getPattern())
	private final List<FreeIdentifier> idents;

	public Pattern(ParseResult result) {
		this.result = result;
		this.ff = result.factory;
		this.names = new HashSet<String>();
		this.decls = new ArrayList<BoundIdentDecl>();
		this.stack = new Stack<Expression>();
		this.idents = new ArrayList<FreeIdentifier>();
	}

	public void declParsed(BoundIdentDecl decl) {
		if (!names.add(decl.getName())) {
			result.addProblem(duplicateNameInPattern(decl));
			stack.push(ff.makeIntegerLiteral(BigInteger.ZERO, null));
			return;
		}
		decls.add(decl);
		final FreeIdentifier ident = asFreeIdentifier(decl);
		idents.add(ident);
		stack.push(ident);
	}

	private FreeIdentifier asFreeIdentifier(BoundIdentDecl decl) {
		final String name = decl.getName();
		final Type type = decl.getType();
		final SourceLocation loc = decl.getSourceLocation();
		return ff.makeFreeIdentifier(name, loc, type);
	}

	public void mapletParsed(SourceLocation loc) {
		final Expression right = stack.pop();
		final Expression left = stack.pop();
		stack.push(ff.makeBinaryExpression(MAPSTO, left, right, loc));
	}

	public List<BoundIdentDecl> getDecls() {
		return decls;
	}

	public Expression getPattern() {
		assert stack.size() == 1;
		final Expression pattern = stack.firstElement();
		return pattern.bindTheseIdents(idents, ff);
	}

	private ASTProblem duplicateNameInPattern(BoundIdentDecl decl) {
		return new ASTProblem(decl.getSourceLocation(),
				ProblemKind.DuplicateIdentifierInPattern, ProblemSeverities.Error,
				decl.getName());
	}

}
