package org.eventb.internal.core.ast.extension;

import java.util.List;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GenericType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.ITypeMediator;

public class TypeMediator implements ITypeMediator {

	private final FormulaFactory factory;

	public TypeMediator(FormulaFactory factory) {
		this.factory = factory;
	}

	@Override
	public BooleanType makeBooleanType() {
		return factory.makeBooleanType();
	}
	
	@Override
	public GenericType makeGenericType(List<Type> typePrms,
			IExpressionExtension exprExt) {
		return factory.makeGenericType(typePrms, exprExt);
	}

	@Override
	public GivenType makeGivenType(String name) {
		return factory.makeGivenType(name);
	}

	@Override
	public IntegerType makeIntegerType() {
		return factory.makeIntegerType();
	}

	@Override
	public PowerSetType makePowerSetType(Type base) {
		return factory.makePowerSetType(base);
	}

	@Override
	public ProductType makeProductType(Type left, Type right) {
		return factory.makeProductType(left, right);
	}

	@Override
	public PowerSetType makeRelationalType(Type left, Type right) {
		return factory.makeRelationalType(left, right);
	}
}