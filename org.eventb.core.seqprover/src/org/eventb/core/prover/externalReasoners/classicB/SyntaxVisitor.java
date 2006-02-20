/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.externalReasoners.classicB;
import java.util.Set;
import java.util.Stack;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.QuantifiedUtil;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * @author halstefa
 * 
 * This class implements the translation of EventB syntax 
 * into classical B syntax of Atelier B (TM). Some operators
 * are not translated. These are TREL, SREL, and STREL.
 * In case any of these is encountered a SyntaxNotSupportedError
 * is thrown.
 * 
 * The translated predicate or expression must be type-checked.
 * 
 * Translation is only possible for expressions and predicates.
 *
 */
public class SyntaxVisitor extends DefaultVisitor {
	
//	Exception error = null;
	
	private Stack<String> syntaxStack;
	private Stack<String[]> boundStack;
	private Stack<String[]> quantStack;
	private Stack<Set<String>> usedStack;
	
	public SyntaxVisitor() {
		clear();
	}
	
	public void clear() {
		syntaxStack = new Stack<String>();
		boundStack = new Stack<String[]>();
		quantStack = new Stack<String[]>();
		usedStack = new Stack<Set<String>>();
		
		boundStack.push(new String[0]);
	}
	
	public String getString() {
		if(syntaxStack.size() > 1)
			throw new RuntimeException("invalid syntax stack");
		return syntaxStack.peek();
	}
	
	private void pushName(String name) {
		int last = name.length()-1;
		boolean primed = name.charAt(last) == '\'';
		String nn = (primed) ? name.substring(0,last-1) : name;
		if(nn.matches("^[A-Za-z0-9_]+$"))
			syntaxStack.push(primed ? nn + "$1" : nn);
		else {
			throw new SyntaxError("Identifier is not ASCII: " + name);
		}
	}
	
	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		pushName(ident.getName());
		return true;
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
//		pushName(ident.getName());
		return true;
	}

	@Override
	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		String[] boundIdents = boundStack.peek();
		int index = ident.getBoundIndex();
		String result = (index < boundIdents.length) ? boundIdents[boundIdents.length - index - 1] : null;
		if (result == null)
			throw new SyntaxError("Unbound deBruijn index.");
		pushName(result);
		return true;
	}

	@Override
	public boolean visitINTLIT(IntegerLiteral lit) {
		syntaxStack.push(lit.getValue().toString());
		return true;
	}

	@Override
	public boolean exitSETEXT(SetExtension set) {
		int setSize = set.getMembers().length;
		if(setSize == 0) {
			syntaxStack.push("{}");
		} else {
			String setExt = syntaxStack.pop() + "}"; 
			for(int i=1; i<setSize; i++)
				setExt = syntaxStack.pop() + "," + setExt; 
			syntaxStack.push("{" + setExt);
		}
		return true;
	}
	
	private void pushBinary(String op) {
		String rr = syntaxStack.pop();
		String ll = syntaxStack.pop();
		syntaxStack.push("(" + ll + " " + op + " " + rr + ")");
	}
	
	private void pushFunApp() {
		String rr = syntaxStack.pop();
		String ll = syntaxStack.pop();
//		if (rr.charAt(0) == '(')
//			syntaxStack.push(ll + rr);
//		else
			syntaxStack.push(ll + "(" + rr + ")");
	}

	private void pushAssociative(String op, int num) {
		String result = syntaxStack.pop() + ")";
		num--;
		for(int i=0; i<num; i++)
			result = syntaxStack.pop() + " " + op + " " + result;
		syntaxStack.push("(" + result);
	}
	
	private void invert(int num) {
		String[] acc = new String[num];
		for(int i=0; i<num; i++)
			acc[i] = syntaxStack.pop();
		for(int i=0; i<num; i++)
			syntaxStack.push(acc[i]);
	}

	@Override
	public boolean exitEQUAL(RelationalPredicate pred) {
		pushBinary("=");
		return true;
	}

	@Override
	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		pushBinary("/=");
		return true;
	}

	@Override
	public boolean exitLT(RelationalPredicate pred) {
		pushBinary("<");
		return true;
	}

	@Override
	public boolean exitLE(RelationalPredicate pred) {
		pushBinary("<=");
		return true;
	}

	@Override
	public boolean exitGT(RelationalPredicate pred) {
		pushBinary(">");
		return true;
	}

	@Override
	public boolean exitGE(RelationalPredicate pred) {
		pushBinary(">=");
		return true;
	}

	@Override
	public boolean exitIN(RelationalPredicate pred) {
		pushBinary(":");
		return true;
	}

	@Override
	public boolean exitNOTIN(RelationalPredicate pred) {
		pushBinary("/:");
		return true;
	}

	@Override
	public boolean exitSUBSET(RelationalPredicate pred) {
		pushBinary("<<:");
		return true;
	}

	@Override
	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		pushBinary("/<<:");
		return true;
	}

	@Override
	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		pushBinary("<:");
		return true;
	}

	@Override
	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		pushBinary("/<:");
		return true;
	}

	@Override
	public boolean exitMAPSTO(BinaryExpression expr) {
		pushBinary("|->");
		return true;
	}

	@Override
	public boolean exitREL(BinaryExpression expr) {
		pushBinary("<->");
		return true;
	}

	@Override
	public boolean exitTREL(BinaryExpression expr) {
		throw new SyntaxNotSupportedError("total relation");
		// return true;
	}

	@Override
	public boolean exitSREL(BinaryExpression expr) {
		throw new SyntaxNotSupportedError("surjective relation");
		// return true;
	}

	@Override
	public boolean exitSTREL(BinaryExpression expr) {
		throw new SyntaxNotSupportedError("total and surjective relation");
		// return true;
	}

	@Override
	public boolean exitPFUN(BinaryExpression expr) {
		pushBinary("+->");
		return true;
	}

	@Override
	public boolean exitTFUN(BinaryExpression expr) {
		pushBinary("-->");
		return true;
	}

	@Override
	public boolean exitPINJ(BinaryExpression expr) {
		pushBinary(">+>");
		return true;
	}

	@Override
	public boolean exitTINJ(BinaryExpression expr) {
		pushBinary(">->");
		return true;
	}

	@Override
	public boolean exitPSUR(BinaryExpression expr) {
		pushBinary("+->>");
		return true;
	}

	@Override
	public boolean exitTSUR(BinaryExpression expr) {
		pushBinary("-->>");
		return true;
	}

	@Override
	public boolean exitTBIJ(BinaryExpression expr) {
		pushBinary(">->>");
		return true;
	}

	@Override
	public boolean exitSETMINUS(BinaryExpression expr) {
		pushBinary("_moinsE");
		return true;
	}

	@Override
	public boolean exitCPROD(BinaryExpression expr) {
		pushBinary("_multE");
		return true;
	}

	@Override
	public boolean exitDPROD(BinaryExpression expr) {
		pushBinary("><");
		return true;
	}

	@Override
	public boolean exitPPROD(BinaryExpression expr) {
		pushBinary("||");
		return true;
	}

	@Override
	public boolean exitDOMRES(BinaryExpression expr) {
		pushBinary("<|");
		return true;
	}

	@Override
	public boolean exitDOMSUB(BinaryExpression expr) {
		pushBinary("<<|");
		return true;
	}

	@Override
	public boolean exitRANRES(BinaryExpression expr) {
		pushBinary("|>");
		return true;
	}

	@Override
	public boolean exitRANSUB(BinaryExpression expr) {
		pushBinary("|>>");
		return true;
	}

	@Override
	public boolean exitUPTO(BinaryExpression expr) {
		pushBinary("..");
		return true;
	}

	@Override
	public boolean exitMINUS(BinaryExpression expr) {
		pushBinary("-");
		return true;
	}

	@Override
	public boolean exitDIV(BinaryExpression expr) {
		pushBinary("/");
		return true;
	}

	@Override
	public boolean exitMOD(BinaryExpression expr) {
		pushBinary("mod");
		return true;
	}

	@Override
	public boolean exitEXPN(BinaryExpression expr) {
		pushBinary("**");
		return true;
	}

	@Override
	public boolean exitFUNIMAGE(BinaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean exitRELIMAGE(BinaryExpression expr) {
		String rr = syntaxStack.pop();
		String ll = syntaxStack.pop();
		syntaxStack.push(rr + "[" + ll + "]");
		return true;
	}

	@Override
	public boolean exitLIMP(BinaryPredicate pred) {
		pushBinary("=>");
		return true;
	}

	@Override
	public boolean exitLEQV(BinaryPredicate pred) {
		pushBinary("<=>");
		return true;
	}

	@Override
	public boolean exitBUNION(AssociativeExpression expr) {
		pushAssociative("\\/", expr.getChildren().length);
		return true;
	}


	@Override
	public boolean exitBINTER(AssociativeExpression expr) {
		pushAssociative("/\\", expr.getChildren().length);
		return true;
	}

	@Override
	public boolean exitBCOMP(AssociativeExpression expr) {
		int n = expr.getChildren().length;
		invert(n);
		pushAssociative(";", n);
		return true;
	}

	@Override
	public boolean exitFCOMP(AssociativeExpression expr) {
		pushAssociative(";", expr.getChildren().length);
		return true;
	}

	@Override
	public boolean exitOVR(AssociativeExpression expr) {
		pushAssociative("<+", expr.getChildren().length);
		return true;
	}

	@Override
	public boolean exitPLUS(AssociativeExpression expr) {
		pushAssociative("+", expr.getChildren().length);
		return true;
	}

	@Override
	public boolean exitMUL(AssociativeExpression expr) {
		pushAssociative("*", expr.getChildren().length);
		return true;
	}

	@Override
	public boolean exitLAND(AssociativePredicate pred) {
		pushAssociative("&", pred.getChildren().length);
		return true;
	}

	@Override
	public boolean exitLOR(AssociativePredicate pred) {
		pushAssociative("or", pred.getChildren().length);
		return true;
	}

	@Override
	public boolean visitINTEGER(AtomicExpression expr) {
		syntaxStack.push("INTEGER");
		return true;
	}

	@Override
	public boolean visitNATURAL(AtomicExpression expr) {
		syntaxStack.push("NATURAL");
		return true;
	}

	@Override
	public boolean visitNATURAL1(AtomicExpression expr) {
		syntaxStack.push("NATURAL1");
		return true;
	}

	@Override
	public boolean visitBOOL(AtomicExpression expr) {
		syntaxStack.push("BOOL");
		return true;
	}

	@Override
	public boolean visitTRUE(AtomicExpression expr) {
		syntaxStack.push("TRUE");
		return true;
	}

	@Override
	public boolean visitFALSE(AtomicExpression expr) {
		syntaxStack.push("FALSE");
		return true;
	}

	@Override
	public boolean visitEMPTYSET(AtomicExpression expr) {
		syntaxStack.push("{}");
		return true;
	}

	@Override
	public boolean visitKPRED(AtomicExpression expr) {
		syntaxStack.push("pred");
		return true;
	}

	@Override
	public boolean visitKSUCC(AtomicExpression expr) {
		syntaxStack.push("succ");
		return true;
	}
	
	@Override
	public boolean enterKBOOL(BoolExpression expr) {
		syntaxStack.push("bool");
		return true;
	}

	@Override
	public boolean exitKBOOL(BoolExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean visitBTRUE(LiteralPredicate pred) {
		syntaxStack.push("btrue");
		return true;
	}

	@Override
	public boolean visitBFALSE(LiteralPredicate pred) {
		syntaxStack.push("bfalse");
		return true;
	}

	@Override
	public boolean exitKFINITE(SimplePredicate pred) {
		SyntaxVisitor typeVisitor = new SyntaxVisitor();
		pred.getExpression().getType().toExpression(FormulaFactory.getDefault()).accept(typeVisitor);
		syntaxStack.push("FIN");
		syntaxStack.push(typeVisitor.getString());
		pushFunApp();
		pushBinary(":");
		return true;
	}

	@Override
	public boolean enterNOT(UnaryPredicate pred) {
		syntaxStack.push("not");
		return true;
	}

	@Override
	public boolean exitNOT(UnaryPredicate pred) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKCARD(UnaryExpression expr) {
		syntaxStack.push("card");
		return true;
	}

	@Override
	public boolean exitKCARD(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterPOW(UnaryExpression expr) {
		syntaxStack.push("POW");
		return true;
	}

	@Override
	public boolean exitPOW(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterPOW1(UnaryExpression expr) {
		syntaxStack.push("POW1");
		return true;
	}

	@Override
	public boolean exitPOW1(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKUNION(UnaryExpression expr) {
		syntaxStack.push("union");
		return true;
	}

	@Override
	public boolean exitKUNION(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKINTER(UnaryExpression expr) {
		syntaxStack.push("inter");
		return true;
	}

	@Override
	public boolean exitKINTER(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKDOM(UnaryExpression expr) {
		syntaxStack.push("dom");
		return true;
	}

	@Override
	public boolean exitKDOM(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKRAN(UnaryExpression expr) {
		syntaxStack.push("ran");
		return true;
	}

	@Override
	public boolean exitKRAN(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKPRJ1(UnaryExpression expr) {
		syntaxStack.push("prj1");
		return true;
	}

	@Override
	public boolean exitKPRJ1(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKPRJ2(UnaryExpression expr) {
		syntaxStack.push("prj2");
		return true;
	}

	@Override
	public boolean exitKPRJ2(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKID(UnaryExpression expr) {
		syntaxStack.push("id");
		return true;
	}

	@Override
	public boolean exitKID(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKMIN(UnaryExpression expr) {
		syntaxStack.push("min");
		return true;
	}

	@Override
	public boolean exitKMIN(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean enterKMAX(UnaryExpression expr) {
		syntaxStack.push("max");
		return true;
	}

	@Override
	public boolean exitKMAX(UnaryExpression expr) {
		pushFunApp();
		return true;
	}

	@Override
	public boolean exitCONVERSE(UnaryExpression expr) {
		syntaxStack.push("(" + syntaxStack.pop() + ")~");
		return true;
	}

	@Override
	public boolean exitUNMINUS(UnaryExpression expr) {
		String ll = syntaxStack.pop();
		if(ll.charAt(0) == '(')
			syntaxStack.push("(-" + ll + ")");
		else
			syntaxStack.push("(- (" + ll + "))");
		return true;
	}
	
	private void openQexpr(QuantifiedExpression expr) {
		String[] boundNames = boundStack.peek();
		Set<String> usedNames = expr.collectNamesAbove(boundNames); 
		
		String[] localNames = QuantifiedUtil.resolveIdents(expr.getBoundIdentifiers(), usedNames);
		String[] newBoundNames = QuantifiedUtil.catenateBoundIdentLists(boundNames, localNames);
		
		quantStack.push(localNames);
		boundStack.push(newBoundNames);
		usedStack.push(usedNames);
	}
	
	private String declList(String[] names) {
		if(names.length == 1)
			return names[0];
		else {
			String result = "(" + names[0];
			for(int i=1; i<names.length; i++)
				result += "," + names[i];
			return result + ")";
		}
	}
	
	private String getType(String name, Type type) {
		FormulaFactory factory = FormulaFactory.getDefault();
		SyntaxVisitor visitor = new SyntaxVisitor();
		type.toExpression(factory).accept(visitor);
		return "(" + name + " : " + visitor.getString() + ")";
	}

	private String getTypes(String[] names, BoundIdentDecl[] decl) {
		FormulaFactory factory = FormulaFactory.getDefault();
		SyntaxVisitor visitor = new SyntaxVisitor();
		boolean first = true;
		String result = "(";
		for(int i=0; i<names.length; i++) {
			decl[i].getType().toExpression(factory).accept(visitor);
			if(first)
				first = false;
			else
				result += " & ";
			result += names[i] + " : " + visitor.getString();
			visitor.clear();
		}
		return result + ")";
	}

	private void pushQexpr(String op, BoundIdentDecl[] quantifiedIdentifiers) {
		String[] localNames = quantStack.pop();
		@SuppressWarnings("unused") String[] boundNames = boundStack.pop();
		@SuppressWarnings("unused") Set<String> usedNames = usedStack.pop();
		String rr = syntaxStack.pop();
		String ll = syntaxStack.pop();
		String types = getTypes(localNames, quantifiedIdentifiers);
		String result = op + " " + declList(localNames) + ".(" + types + " & " + ll + " | " + rr + ")";
		syntaxStack.push(result);
	}

	@Override
	public boolean enterQUNION(QuantifiedExpression expr) {
		openQexpr(expr);
		return true;
	}
	
	@Override
	public boolean exitQUNION(QuantifiedExpression expr) {
		pushQexpr("UNION", expr.getBoundIdentifiers());
		return true;
	}

	@Override
	public boolean enterQINTER(QuantifiedExpression expr) {
		openQexpr(expr);
		return true;
	}

	@Override
	public boolean exitQINTER(QuantifiedExpression expr) {
		pushQexpr("INTER", expr.getBoundIdentifiers());
		return true;
	}

	@Override
	public boolean enterCSET(QuantifiedExpression expr) {
		openQexpr(expr);
		return true;
	}

	@Override
	public boolean exitCSET(QuantifiedExpression expr) {
		String[] localNames = quantStack.pop();
		@SuppressWarnings("unused") String[] boundNames = boundStack.pop();
		Set<String> usedNames = usedStack.pop();
		BoundIdentDecl[] newIdent = new BoundIdentDecl[] {
				FormulaFactory.getDefault().makeBoundIdentDecl("yy", null)
		};
		String[] newName = QuantifiedUtil.resolveIdents(newIdent, usedNames);
		String exp = syntaxStack.pop();
		String prd = syntaxStack.pop();
		String types = getTypes(localNames, expr.getBoundIdentifiers());
		String newType = getType(newName[0], expr.getExpression().getType());
//		String result = 
//			"{" + newName[0] + " | " + newType + " &  # " + declList(localNames) + 
//			".(" + types + " & " + prd + " & (" + newName[0] + " = " + exp + "))}";
		String result = 
		"SET(" + newName[0] + ").(" + newType + " &  # " + declList(localNames) + 
		".(" + types + " & " + prd + " & (" + newName[0] + " = " + exp + ")))";
		syntaxStack.push(result);
		return true;
	}

	private void openQpred(QuantifiedPredicate pred) {
		String[] boundNames = boundStack.peek();
		Set<String> usedNames = pred.collectNamesAbove(boundNames);
		
		String[] localNames = QuantifiedUtil.resolveIdents(pred.getBoundIdentifiers(), usedNames);
		String[] newBoundNames = QuantifiedUtil.catenateBoundIdentLists(boundNames, localNames);
		
		quantStack.push(localNames);
		boundStack.push(newBoundNames);
		usedStack.push(usedNames);
	}
	
	private void pushQpred(char op, BoundIdentDecl[] quantifiedIdentifiers) {
		String[] localNames = quantStack.pop();
		@SuppressWarnings("unused") String[] boundNames = boundStack.pop();
		@SuppressWarnings("unused") Set<String> usedNames = usedStack.pop();
		String pred = syntaxStack.pop();
		String types = getTypes(localNames, quantifiedIdentifiers);
		if(pred.charAt(0) != '(')
			pred = "(" + pred + ")";
		if(op == '#')
			types = types + " & ";
		else
			types = types + " => ";
		if(pred.charAt(0) != '(')
			pred = "(" + pred + ")";
		String result = op + " " + declList(localNames) + ".(" + types + pred + ")";
		syntaxStack.push(result);
	}

	@Override
	public boolean enterFORALL(QuantifiedPredicate pred) {
		openQpred(pred);
		return true;
	}

	@Override
	public boolean exitFORALL(QuantifiedPredicate pred) {
		pushQpred('!', pred.getBoundIdentifiers());
		return true;
	}

	@Override
	public boolean enterEXISTS(QuantifiedPredicate pred) {
		openQpred(pred);
		return true;
	}

	@Override
	public boolean exitEXISTS(QuantifiedPredicate pred) {
		pushQpred('#', pred.getBoundIdentifiers());
		return true;
	}



}
