package org.eventb.internal.core.ast.extension.notation;

import org.eventb.core.ast.extension.notation.IFormulaChild;

public class FormulaChild implements IFormulaChild {

	private final int index;
	private final Kind kind;
	
	
	public FormulaChild(int index, Kind kind) {
		this.index = index;
		this.kind = kind;
	}

	public int getIndex() {
		return index;
	}

	public Kind getKind() {
		return kind;
	}

}
