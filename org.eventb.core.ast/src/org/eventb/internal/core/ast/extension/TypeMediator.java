package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeMediator;

public class TypeMediator implements ITypeMediator {

	private final FormulaFactory factory;

	public TypeMediator(FormulaFactory factory) {
		this.factory = factory;
	}

	public BooleanType makeBooleanType() {
		return factory.makeBooleanType();
	}

	public GivenType makeGivenType(String name) {
		return factory.makeGivenType(name);
	}

	public IntegerType makeIntegerType() {
		return factory.makeIntegerType();
	}

	public PowerSetType makePowerSetType(Type base) {
		return factory.makePowerSetType(base);
	}

	public ProductType makeProductType(Type left, Type right) {
		return factory.makeProductType(left, right);
	}

	public PowerSetType makeRelationalType(Type left, Type right) {
		return factory.makeRelationalType(left, right);
	}
}