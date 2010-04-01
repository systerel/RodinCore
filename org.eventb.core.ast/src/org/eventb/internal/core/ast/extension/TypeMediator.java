package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.ITypeMediator;

public class TypeMediator implements ITypeMediator {

	private final FormulaFactory ff;
	
	public TypeMediator(FormulaFactory ff) {
		this.ff = ff;
	}

	public FormulaFactory getFormulaFactory() {
		return ff;
	}

}