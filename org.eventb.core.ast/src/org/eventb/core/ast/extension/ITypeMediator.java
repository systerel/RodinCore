package org.eventb.core.ast.extension;

import java.util.List;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.GenericType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 */
/**
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITypeMediator {

	BooleanType makeBooleanType();

	GenericType makeGenericType(List<Type> typePrms,
			IExpressionExtension iExpressionExtension);

	GivenType makeGivenType(String name);

	IntegerType makeIntegerType();

	PowerSetType makePowerSetType(Type base);

	ProductType makeProductType(Type left, Type right);

	PowerSetType makeRelationalType(Type left, Type right);
}