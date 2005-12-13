/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

/**
 * Implementation of the visitor design pattern for event-B formulas.
 * <p>
 * Instances of this interface are passed as argument to the
 * {@link Formula#accept(IVisitor)} in order to traverse an AST in depth-first
 * order. Then, for each node encountered during that traversal, methods of that
 * interface will be called back.
 * </p><p>
 * Foreach leaf node tag of event-B (a tag that corresponds to a node that never
 * has children), a visitor provides a <code>visit</code> method. This method
 * takes the node to visit as argument.
 * </p><p>
 * Foreach parent node tag of event-B (a tag that corresponds to a node that can
 * have children), a visitor provides two methods called <code>enter</code>
 * and <code>exit</code>. The <code>enter</code> method is called before
 * visiting the children (prefix order), while the <code>exit</code> method is
 * called after visiting the children (postfix order). Both methods take the
 * node to visit as argument.
 * </p><p>
 * All methods return a boolean value which is used to possibly accelerate the
 * traversal of an AST. A <code>true</code> result means to resume the
 * traversal normally, while a <code>false</code> result means to take some
 * shortcut:
 * <ul>
 *   <li>When a <code>visit</code> or <code>exit</code> method returns
 *       <code>false</code>, then the siblings of the visited node will not be
 *       traversed anymore.  The traversal is resumed at the call of the
 *        <code>exit</code> method on the parent of the last visited node.</li>
 *   <li>When an <code>enter</code> method returns <code>false</code>, then the
 *       children of the visited node will not be traversed.
 *       The traversal is resumed at the call of the <code>exit</code>
 *       method on the same node.</li>
 * </ul>
 * </p><p>
 * <em>Note</em> that, whatever the result of the visit calls, for each parent
 * node traversed, there will be exactly one call of both <code>enter</code>
 * and <code>exit</code> methods.
 * </p><p>
 * For instance, suppose we have the following AST:
 * <pre>
 *     PLUS
 *      +--- FREE_IDENT("x")
 *      +--- MINUS
 *      |      +--- FREE_IDENT("y") 
 *      |      +--- FREE_IDENT("z") 
 *      +--- FREE_IDENT("t")
 * </pre>
 * </p><p>
 * Normal in-depth traversal (the one obtained when all calls return
 * <code>true</code>) produces the following sequence of calls:
 * <pre>
 *      true <-- enterPLUS
 *      true <-- visitFREE_IDENT("x")
 *      true <-- enterMINUS
 *      true <-- visitFREE_IDENT("y")
 *      true <-- visitFREE_IDENT("z")
 *      true <-- exitMINUS
 *      true <-- visitFREE_IDENT("t")
 *      true <-- exitPLUS
 * </pre>
 * </p><p>
 * Then, suppose that the visit of identifier <code>x</code> returns
 * <code>false</code>, the sequence of calls becomes:
 * <pre>
 *      true <-- enterPLUS
 *     false <-- visitFREE_IDENT("x")
 *      true <-- exitPLUS
 * </pre>
 * </p><p>
 * If, instead, it's the call to <code>enterMINUS</code> which returns
 * <code>false</code>, the sequence of calls becomes:
 * <pre>
 *      true <-- enterPLUS
 *      true <-- visitFREE_IDENT("x")
 *     false <-- enterMINUS
 *      true <-- exitMINUS
 *      true <-- visitFREE_IDENT("t")
 *      true <-- exitPLUS
 * </pre>
 * </p>
 * 
 * @see Formula#accept(IVisitor)
 * 
 * @author Laurent Voisin
 */
public interface IVisitor {

	/**
	 * Visits a <code>FREE_IDENT</code> node.
	 *
	 * @param ident
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitFREE_IDENT(FreeIdentifier ident);

	/**
	 * Visits a <code>BOUND_IDENT_DECL</code> node.
	 *
	 * @param ident
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident);

	/**
	 * Visits a <code>BOUND_IDENT</code> node.
	 *
	 * @param ident
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitBOUND_IDENT(BoundIdentifier ident);

	/**
	 * Visits a <code>INTLIT</code> node.
	 *
	 * @param lit
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitINTLIT(IntegerLiteral lit);

	/**
	 * Enters a <code>SETEXT</code> node.
	 *
	 * @param set
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterSETEXT(SetExtension set);

	/**
	 * Exits a <code>SETEXT</code> node.
	 *
	 * @param set
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitSETEXT(SetExtension set);

	/**
	 * Enters a <code>EQUAL</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterEQUAL(RelationalPredicate pred);

	/**
	 * Exits a <code>EQUAL</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitEQUAL(RelationalPredicate pred);

	/**
	 * Enters a <code>NOTEQUAL</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterNOTEQUAL(RelationalPredicate pred);

	/**
	 * Exits a <code>NOTEQUAL</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitNOTEQUAL(RelationalPredicate pred);

	/**
	 * Enters a <code>LT</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterLT(RelationalPredicate pred);

	/**
	 * Exits a <code>LT</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitLT(RelationalPredicate pred);

	/**
	 * Enters a <code>LE</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterLE(RelationalPredicate pred);

	/**
	 * Exits a <code>LE</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitLE(RelationalPredicate pred);

	/**
	 * Enters a <code>GT</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterGT(RelationalPredicate pred);

	/**
	 * Exits a <code>GT</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitGT(RelationalPredicate pred);

	/**
	 * Enters a <code>GE</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterGE(RelationalPredicate pred);

	/**
	 * Exits a <code>GE</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitGE(RelationalPredicate pred);

	/**
	 * Enters a <code>IN</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterIN(RelationalPredicate pred);

	/**
	 * Exits a <code>IN</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitIN(RelationalPredicate pred);

	/**
	 * Enters a <code>NOTIN</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterNOTIN(RelationalPredicate pred);

	/**
	 * Exits a <code>NOTIN</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitNOTIN(RelationalPredicate pred);

	/**
	 * Enters a <code>SUBSET</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterSUBSET(RelationalPredicate pred);

	/**
	 * Exits a <code>SUBSET</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitSUBSET(RelationalPredicate pred);

	/**
	 * Enters a <code>NOTSUBSET</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterNOTSUBSET(RelationalPredicate pred);

	/**
	 * Exits a <code>NOTSUBSET</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitNOTSUBSET(RelationalPredicate pred);

	/**
	 * Enters a <code>SUBSETEQ</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterSUBSETEQ(RelationalPredicate pred);

	/**
	 * Exits a <code>SUBSETEQ</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitSUBSETEQ(RelationalPredicate pred);

	/**
	 * Enters a <code>NOTSUBSETEQ</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterNOTSUBSETEQ(RelationalPredicate pred);

	/**
	 * Exits a <code>NOTSUBSETEQ</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitNOTSUBSETEQ(RelationalPredicate pred);

	/**
	 * Enters a <code>MAPSTO</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterMAPSTO(BinaryExpression expr);

	/**
	 * Exits a <code>MAPSTO</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitMAPSTO(BinaryExpression expr);

	/**
	 * Enters a <code>REL</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterREL(BinaryExpression expr);

	/**
	 * Exits a <code>REL</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitREL(BinaryExpression expr);

	/**
	 * Enters a <code>TREL</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterTREL(BinaryExpression expr);

	/**
	 * Exits a <code>TREL</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitTREL(BinaryExpression expr);

	/**
	 * Enters a <code>SREL</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterSREL(BinaryExpression expr);

	/**
	 * Exits a <code>SREL</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitSREL(BinaryExpression expr);

	/**
	 * Enters a <code>STREL</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterSTREL(BinaryExpression expr);

	/**
	 * Exits a <code>STREL</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitSTREL(BinaryExpression expr);

	/**
	 * Enters a <code>PFUN</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPFUN(BinaryExpression expr);

	/**
	 * Exits a <code>PFUN</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPFUN(BinaryExpression expr);

	/**
	 * Enters a <code>TFUN</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterTFUN(BinaryExpression expr);

	/**
	 * Exits a <code>TFUN</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitTFUN(BinaryExpression expr);

	/**
	 * Enters a <code>PINJ</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPINJ(BinaryExpression expr);

	/**
	 * Exits a <code>PINJ</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPINJ(BinaryExpression expr);

	/**
	 * Enters a <code>TINJ</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterTINJ(BinaryExpression expr);

	/**
	 * Exits a <code>TINJ</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitTINJ(BinaryExpression expr);

	/**
	 * Enters a <code>PSUR</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPSUR(BinaryExpression expr);

	/**
	 * Exits a <code>PSUR</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPSUR(BinaryExpression expr);

	/**
	 * Enters a <code>TSUR</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterTSUR(BinaryExpression expr);

	/**
	 * Exits a <code>TSUR</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitTSUR(BinaryExpression expr);

	/**
	 * Enters a <code>TBIJ</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterTBIJ(BinaryExpression expr);

	/**
	 * Exits a <code>TBIJ</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitTBIJ(BinaryExpression expr);

	/**
	 * Enters a <code>SETMINUS</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterSETMINUS(BinaryExpression expr);

	/**
	 * Exits a <code>SETMINUS</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitSETMINUS(BinaryExpression expr);

	/**
	 * Enters a <code>CPROD</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterCPROD(BinaryExpression expr);

	/**
	 * Exits a <code>CPROD</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitCPROD(BinaryExpression expr);

	/**
	 * Enters a <code>DPROD</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterDPROD(BinaryExpression expr);

	/**
	 * Exits a <code>DPROD</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitDPROD(BinaryExpression expr);

	/**
	 * Enters a <code>PPROD</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPPROD(BinaryExpression expr);

	/**
	 * Exits a <code>PPROD</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPPROD(BinaryExpression expr);

	/**
	 * Enters a <code>DOMRES</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterDOMRES(BinaryExpression expr);

	/**
	 * Exits a <code>DOMRES</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitDOMRES(BinaryExpression expr);

	/**
	 * Enters a <code>DOMSUB</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterDOMSUB(BinaryExpression expr);

	/**
	 * Exits a <code>DOMSUB</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitDOMSUB(BinaryExpression expr);

	/**
	 * Enters a <code>RANRES</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterRANRES(BinaryExpression expr);

	/**
	 * Exits a <code>RANRES</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitRANRES(BinaryExpression expr);

	/**
	 * Enters a <code>RANSUB</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterRANSUB(BinaryExpression expr);

	/**
	 * Exits a <code>RANSUB</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitRANSUB(BinaryExpression expr);

	/**
	 * Enters a <code>UPTO</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterUPTO(BinaryExpression expr);

	/**
	 * Exits a <code>UPTO</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitUPTO(BinaryExpression expr);

	/**
	 * Enters a <code>MINUS</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterMINUS(BinaryExpression expr);

	/**
	 * Exits a <code>MINUS</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitMINUS(BinaryExpression expr);

	/**
	 * Enters a <code>DIV</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterDIV(BinaryExpression expr);

	/**
	 * Exits a <code>DIV</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitDIV(BinaryExpression expr);

	/**
	 * Enters a <code>MOD</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterMOD(BinaryExpression expr);

	/**
	 * Exits a <code>MOD</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitMOD(BinaryExpression expr);

	/**
	 * Enters a <code>EXPN</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterEXPN(BinaryExpression expr);

	/**
	 * Exits a <code>EXPN</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitEXPN(BinaryExpression expr);

	/**
	 * Enters a <code>FUNIMAGE</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterFUNIMAGE(BinaryExpression expr);

	/**
	 * Exits a <code>FUNIMAGE</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitFUNIMAGE(BinaryExpression expr);

	/**
	 * Enters a <code>RELIMAGE</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterRELIMAGE(BinaryExpression expr);

	/**
	 * Exits a <code>RELIMAGE</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitRELIMAGE(BinaryExpression expr);

	/**
	 * Enters a <code>LIMP</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterLIMP(BinaryPredicate pred);

	/**
	 * Exits a <code>LIMP</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitLIMP(BinaryPredicate pred);

	/**
	 * Enters a <code>LEQV</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterLEQV(BinaryPredicate pred);

	/**
	 * Exits a <code>LEQV</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitLEQV(BinaryPredicate pred);

	/**
	 * Enters a <code>BUNION</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterBUNION(AssociativeExpression expr);

	/**
	 * Exits a <code>BUNION</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitBUNION(AssociativeExpression expr);

	/**
	 * Enters a <code>BINTER</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterBINTER(AssociativeExpression expr);

	/**
	 * Exits a <code>BINTER</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitBINTER(AssociativeExpression expr);

	/**
	 * Enters a <code>BCOMP</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterBCOMP(AssociativeExpression expr);

	/**
	 * Exits a <code>BCOMP</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitBCOMP(AssociativeExpression expr);

	/**
	 * Enters a <code>FCOMP</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterFCOMP(AssociativeExpression expr);

	/**
	 * Exits a <code>FCOMP</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitFCOMP(AssociativeExpression expr);

	/**
	 * Enters a <code>OVR</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterOVR(AssociativeExpression expr);

	/**
	 * Exits a <code>OVR</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitOVR(AssociativeExpression expr);

	/**
	 * Enters a <code>PLUS</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPLUS(AssociativeExpression expr);

	/**
	 * Exits a <code>PLUS</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPLUS(AssociativeExpression expr);

	/**
	 * Enters a <code>MUL</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterMUL(AssociativeExpression expr);

	/**
	 * Exits a <code>MUL</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitMUL(AssociativeExpression expr);

	/**
	 * Enters a <code>LAND</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterLAND(AssociativePredicate pred);

	/**
	 * Exits a <code>LAND</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitLAND(AssociativePredicate pred);

	/**
	 * Enters a <code>LOR</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterLOR(AssociativePredicate pred);

	/**
	 * Exits a <code>LOR</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitLOR(AssociativePredicate pred);

	/**
	 * Visits a <code>INTEGER</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitINTEGER(AtomicExpression expr);

	/**
	 * Visits a <code>NATURAL</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitNATURAL(AtomicExpression expr);

	/**
	 * Visits a <code>NATURAL1</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitNATURAL1(AtomicExpression expr);

	/**
	 * Visits a <code>BOOL</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitBOOL(AtomicExpression expr);

	/**
	 * Visits a <code>TRUE</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitTRUE(AtomicExpression expr);

	/**
	 * Visits a <code>FALSE</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitFALSE(AtomicExpression expr);

	/**
	 * Visits a <code>EMPTYSET</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitEMPTYSET(AtomicExpression expr);

	/**
	 * Visits a <code>KPRED</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitKPRED(AtomicExpression expr);

	/**
	 * Visits a <code>KSUCC</code> node.
	 *
	 * @param expr
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitKSUCC(AtomicExpression expr);

	/**
	 * Enters a <code>KBOOL</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKBOOL(BoolExpression expr);

	/**
	 * Exits a <code>KBOOL</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKBOOL(BoolExpression expr);

	/**
	 * Visits a <code>BTRUE</code> node.
	 *
	 * @param pred
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitBTRUE(LiteralPredicate pred);

	/**
	 * Visits a <code>BFALSE</code> node.
	 *
	 * @param pred
	 *             the node to visit
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean visitBFALSE(LiteralPredicate pred);

	/**
	 * Enters a <code>KFINITE</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKFINITE(SimplePredicate pred);

	/**
	 * Exits a <code>KFINITE</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKFINITE(SimplePredicate pred);

	/**
	 * Enters a <code>NOT</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterNOT(UnaryPredicate pred);

	/**
	 * Exits a <code>NOT</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitNOT(UnaryPredicate pred);

	/**
	 * Enters a <code>KCARD</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKCARD(UnaryExpression expr);

	/**
	 * Exits a <code>KCARD</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKCARD(UnaryExpression expr);

	/**
	 * Enters a <code>POW</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPOW(UnaryExpression expr);

	/**
	 * Exits a <code>POW</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPOW(UnaryExpression expr);

	/**
	 * Enters a <code>POW1</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterPOW1(UnaryExpression expr);

	/**
	 * Exits a <code>POW1</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitPOW1(UnaryExpression expr);

	/**
	 * Enters a <code>KUNION</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKUNION(UnaryExpression expr);

	/**
	 * Exits a <code>KUNION</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKUNION(UnaryExpression expr);

	/**
	 * Enters a <code>KINTER</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKINTER(UnaryExpression expr);

	/**
	 * Exits a <code>KINTER</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKINTER(UnaryExpression expr);

	/**
	 * Enters a <code>KDOM</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKDOM(UnaryExpression expr);

	/**
	 * Exits a <code>KDOM</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKDOM(UnaryExpression expr);

	/**
	 * Enters a <code>KRAN</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKRAN(UnaryExpression expr);

	/**
	 * Exits a <code>KRAN</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKRAN(UnaryExpression expr);

	/**
	 * Enters a <code>KPRJ1</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKPRJ1(UnaryExpression expr);

	/**
	 * Exits a <code>KPRJ1</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKPRJ1(UnaryExpression expr);

	/**
	 * Enters a <code>KPRJ2</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKPRJ2(UnaryExpression expr);

	/**
	 * Exits a <code>KPRJ2</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKPRJ2(UnaryExpression expr);

	/**
	 * Enters a <code>KID</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKID(UnaryExpression expr);

	/**
	 * Exits a <code>KID</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKID(UnaryExpression expr);

	/**
	 * Enters a <code>KMIN</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKMIN(UnaryExpression expr);

	/**
	 * Exits a <code>KMIN</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKMIN(UnaryExpression expr);

	/**
	 * Enters a <code>KMAX</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterKMAX(UnaryExpression expr);

	/**
	 * Exits a <code>KMAX</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitKMAX(UnaryExpression expr);

	/**
	 * Enters a <code>CONVERSE</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterCONVERSE(UnaryExpression expr);

	/**
	 * Exits a <code>CONVERSE</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitCONVERSE(UnaryExpression expr);

	/**
	 * Enters a <code>UNMINUS</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterUNMINUS(UnaryExpression expr);

	/**
	 * Exits a <code>UNMINUS</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitUNMINUS(UnaryExpression expr);

	/**
	 * Enters a <code>QUNION</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterQUNION(QuantifiedExpression expr);

	/**
	 * Exits a <code>QUNION</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitQUNION(QuantifiedExpression expr);

	/**
	 * Enters a <code>QINTER</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterQINTER(QuantifiedExpression expr);

	/**
	 * Exits a <code>QINTER</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitQINTER(QuantifiedExpression expr);

	/**
	 * Enters a <code>CSET</code> node.
	 *
	 * @param expr
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterCSET(QuantifiedExpression expr);

	/**
	 * Exits a <code>CSET</code> node.
	 *
	 * @param expr
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitCSET(QuantifiedExpression expr);

	/**
	 * Enters a <code>FORALL</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterFORALL(QuantifiedPredicate pred);

	/**
	 * Exits a <code>FORALL</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitFORALL(QuantifiedPredicate pred);

	/**
	 * Enters a <code>EXISTS</code> node.
	 *
	 * @param pred
	 *             the node which is entered
	 * @return <code>false</code> to prevent visiting the children
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean enterEXISTS(QuantifiedPredicate pred);

	/**
	 * Exits a <code>EXISTS</code> node.
	 *
	 * @param pred
	 *             the node which is exited
	 * @return <code>false</code> to prevent visiting the siblings
	 *         of the given node, <code>true</code> to continue visiting.
	 *
	 * @see Formula#accept(IVisitor)
	 */
	boolean exitEXISTS(QuantifiedPredicate pred);

}