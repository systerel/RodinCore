package org.eventb.internal.core.ast;

import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;

/**
 * Abstract super class for all kinds of substitutions operated on formulas.
 * 
 * @author Stefan Hallerstede
 */
public abstract class Substitution extends DefaultRewriter {

	public Substitution(FormulaFactory ff) {
		super(false, ff);
	}

}
