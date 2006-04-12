package org.eventb.internal.pp.translator;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IVisitor;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

public class IdentityVisitor implements IVisitor {

	public boolean visitPredicate(Predicate pred) {
		return true;
	}

	public boolean visitExpression(Expression expr) {
		return true;
	}

	public boolean visitIdentifier(Identifier ident) {
		return visitExpression(ident);
	}

	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		return visitIdentifier(ident);
	}

	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
		return true;
	}

	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		return visitIdentifier(ident);
	}

	public boolean visitINTLIT(IntegerLiteral lit) {
		return visitExpression(lit);
	}

	public boolean enterSETEXT(SetExtension set) {
		return visitExpression(set);
	}

	public boolean exitSETEXT(SetExtension set) {
		return true;
	}

	public boolean visitRelationalPredicate(RelationalPredicate pred) {
		return visitPredicate(pred);
	}

	public boolean enterEQUAL(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean enterLT(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitLT(RelationalPredicate pred) {
		return true;
	}

	public boolean enterLE(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitLE(RelationalPredicate pred) {
		return true;
	}

	public boolean enterGT(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitGT(RelationalPredicate pred) {
		return true;
	}

	public boolean enterGE(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitGE(RelationalPredicate pred) {
		return true;
	}

	public boolean enterIN(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitIN(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTIN(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitNOTIN(RelationalPredicate pred) {
		return true;
	}

	public boolean enterSUBSET(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return visitRelationalPredicate(pred);
	}

	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean visitBinaryExpression(BinaryExpression expr) {
		return visitExpression(expr);
	}

	public boolean enterMAPSTO(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitMAPSTO(BinaryExpression expr) {
		return true;
	}

	public boolean enterREL(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterTREL(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitTREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterSREL(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitSREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterSTREL(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitSTREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterPFUN(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitPFUN(BinaryExpression expr) {
		return true;
	}

	public boolean enterTFUN(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitTFUN(BinaryExpression expr) {
		return true;
	}

	public boolean enterPINJ(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitPINJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterTINJ(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitTINJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterPSUR(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitPSUR(BinaryExpression expr) {
		return true;
	}

	public boolean enterTSUR(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitTSUR(BinaryExpression expr) {
		return true;
	}

	public boolean enterTBIJ(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitTBIJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterSETMINUS(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitSETMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean enterCPROD(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitCPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterDPROD(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitDPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterPPROD(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitPPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterDOMRES(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitDOMRES(BinaryExpression expr) {
		return true;
	}

	public boolean enterDOMSUB(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitDOMSUB(BinaryExpression expr) {
		return true;
	}

	public boolean enterRANRES(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitRANRES(BinaryExpression expr) {
		return true;
	}

	public boolean enterRANSUB(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitRANSUB(BinaryExpression expr) {
		return true;
	}

	public boolean enterUPTO(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitUPTO(BinaryExpression expr) {
		return true;
	}

	public boolean enterMINUS(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean enterDIV(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitDIV(BinaryExpression expr) {
		return true;
	}

	public boolean enterMOD(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitMOD(BinaryExpression expr) {
		return true;
	}

	public boolean enterEXPN(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitEXPN(BinaryExpression expr) {
		return true;
	}

	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean enterRELIMAGE(BinaryExpression expr) {
		return visitBinaryExpression(expr);
	}

	public boolean exitRELIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean visitBinaryPredicate(BinaryPredicate pred) {
		return visitBinaryPredicate(pred);
	}

	public boolean enterLIMP(BinaryPredicate pred) {
		return visitBinaryPredicate(pred);
	}

	public boolean exitLIMP(BinaryPredicate pred) {
		return true;
	}

	public boolean enterLEQV(BinaryPredicate pred) {
		return visitBinaryPredicate(pred);
	}

	public boolean exitLEQV(BinaryPredicate pred) {
		return true;
	}

	public boolean visitAssociativeExpression(AssociativeExpression expr) {
		return visitExpression(expr);
	}

	public boolean enterBUNION(AssociativeExpression expr) {
		return visitAssociativeExpression(expr);
	}

	public boolean exitBUNION(AssociativeExpression expr) {
		return true;
	}

	public boolean enterBINTER(AssociativeExpression expr) {
		return visitAssociativeExpression(expr);
	}

	public boolean exitBINTER(AssociativeExpression expr) {
		return true;
	}

	public boolean enterBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean enterFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean exitFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean enterOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean exitOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean enterPLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean exitPLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean enterMUL(AssociativeExpression expr) {
		return true;
	}

	public boolean exitMUL(AssociativeExpression expr) {

		return true;
	}

	public boolean enterLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean exitLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean enterLOR(AssociativePredicate pred) {
		return true;
	}

	public boolean exitLOR(AssociativePredicate pred) {
		return true;
	}
	
	public boolean visitAtomicExpression(AtomicExpression expr) {
		return visitExpression(expr);
	}

	public boolean visitINTEGER(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitNATURAL(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitNATURAL1(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitBOOL(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitTRUE(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitFALSE(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitEMPTYSET(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitKPRED(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean visitKSUCC(AtomicExpression expr) {
		return visitAtomicExpression(expr);
	}

	public boolean enterKBOOL(BoolExpression expr) {
		return visitExpression(expr);
	}

	public boolean exitKBOOL(BoolExpression expr) {
		return true;
	}
	
	public boolean visitLiteralPredicate(LiteralPredicate pred) {
		return visitPredicate(pred);
	}

	public boolean visitBTRUE(LiteralPredicate pred) {
		return visitLiteralPredicate(pred);
	}

	public boolean visitBFALSE(LiteralPredicate pred) {
		return visitLiteralPredicate(pred);
	}

	public boolean visitSimplePredicate(SimplePredicate pred) {
		return visitPredicate(pred);
	}

	public boolean enterKFINITE(SimplePredicate pred) {
		return visitSimplePredicate(pred);
	}

	public boolean exitKFINITE(SimplePredicate pred) {
		return true;
	}

	public boolean visitUnaryPredicate(UnaryPredicate pred) {
		return visitPredicate(pred);
	}

	public boolean enterNOT(UnaryPredicate pred) {
		return visitUnaryPredicate(pred);
	}

	public boolean exitNOT(UnaryPredicate pred) {
		return true;
	}

	public boolean visitUnaryExpression(UnaryExpression expr) {
		return visitExpression(expr);
	}

	public boolean enterKCARD(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKCARD(UnaryExpression expr) {
		return true;
	}

	public boolean enterPOW(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitPOW(UnaryExpression expr) {
		return true;
	}

	public boolean enterPOW1(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitPOW1(UnaryExpression expr) {
		return true;
	}

	public boolean enterKUNION(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKUNION(UnaryExpression expr) {
		return true;
	}

	public boolean enterKINTER(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKINTER(UnaryExpression expr) {
		return true;
	}

	public boolean enterKDOM(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKDOM(UnaryExpression expr) {
		return true;
	}

	public boolean enterKRAN(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKRAN(UnaryExpression expr) {
		return true;
	}

	public boolean enterKPRJ1(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKPRJ1(UnaryExpression expr) {
		return true;
	}

	public boolean enterKPRJ2(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKPRJ2(UnaryExpression expr) {
		return true;
	}

	public boolean enterKID(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKID(UnaryExpression expr) {
		return true;
	}

	public boolean enterKMIN(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKMIN(UnaryExpression expr) {
		return true;
	}

	public boolean enterKMAX(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitKMAX(UnaryExpression expr) {
		return true;
	}

	public boolean enterCONVERSE(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitCONVERSE(UnaryExpression expr) {
		return true;
	}

	public boolean enterUNMINUS(UnaryExpression expr) {
		return visitUnaryExpression(expr);
	}

	public boolean exitUNMINUS(UnaryExpression expr) {
		return true;
	}
	
	public boolean visitQuantifiedExpression(QuantifiedExpression expr) {
		return visitExpression(expr);
	}

	public boolean enterQUNION(QuantifiedExpression expr) {
		return visitQuantifiedExpression(expr);
	}

	public boolean exitQUNION(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterQINTER(QuantifiedExpression expr) {
		return visitQuantifiedExpression(expr);
	}

	public boolean exitQINTER(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterCSET(QuantifiedExpression expr) {
		return visitQuantifiedExpression(expr);
	}

	public boolean exitCSET(QuantifiedExpression expr) {
		return true;
	}
	
	public boolean visitQuantifiedPredicate(QuantifiedPredicate pred) {
		return visitPredicate(pred);
	}


	public boolean enterFORALL(QuantifiedPredicate pred) {
		return visitQuantifiedPredicate(pred);
	}

	public boolean exitFORALL(QuantifiedPredicate pred) {
		return true;
	}

	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return visitQuantifiedPredicate(pred);
	}

	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return true;
	}

}
