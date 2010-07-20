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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Arity;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 * 
 */
public class PropertyParserDB {

	private static final class Properties {
		
		private final Notation notation;
		private final FormulaType formulaType;
		private final FormulaType argumentType;
		private final boolean isAssociative;
		
		public Properties(Notation notation, FormulaType formulaType,
				FormulaType argumentType, boolean isAssociative) {
			this.notation = notation;
			this.formulaType = formulaType;
			this.argumentType = argumentType;
			this.isAssociative = isAssociative;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((argumentType == null) ? 0 : argumentType.hashCode());
			result = prime * result
					+ ((formulaType == null) ? 0 : formulaType.hashCode());
			result = prime * result + (isAssociative ? 1231 : 1237);
			result = prime * result
					+ ((notation == null) ? 0 : notation.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Properties)) {
				return false;
			}
			Properties other = (Properties) obj;
			if (argumentType != other.argumentType) {
				return false;
			}
			if (formulaType != other.formulaType) {
				return false;
			}
			if (isAssociative != other.isAssociative) {
				return false;
			}
			if (notation != other.notation) {
				return false;
			}
			return true;
		}


	}
	
	private static Properties makeProp(IOperatorProperties operProps) {
		return new Properties(operProps.getNotation(),
				operProps.getFormulaType(), operProps
						.getArgumentType(), operProps.isAssociative());
	}
	
	private final Map<Properties, List<IPropertyParserInfo<? extends Formula<?>>>> map = new HashMap<Properties, List<IPropertyParserInfo<? extends Formula<?>>>>();

	public void add(IPropertyParserInfo<? extends Formula<?>> newParser)
			throws OverrideException {
		final Properties newProp = makeProp(newParser.getProperties());

		List<IPropertyParserInfo<? extends Formula<?>>> list = map.get(newProp);
		if (list == null) {
			list = new ArrayList<IPropertyParserInfo<? extends Formula<?>>>();
			map.put(newProp, list);
		}
		final Arity newArity = newParser.getProperties().getArity();
		for (IPropertyParserInfo<? extends Formula<?>> parser : list) {
			final Arity arity = parser.getProperties().getArity();
			if(!newArity.isDistinct(arity)) {
				throw new GenParser.OverrideException("overriding a parser");
			}
		}
		list.add(newParser);
	}

	public IParserPrinter<? extends Formula<?>> getParser(IOperatorProperties operProps, int kind,
			int tag) {
		final Properties prop = makeProp(operProps);
		final List<IPropertyParserInfo<? extends Formula<?>>> list = map.get(prop);
		if (list == null) {
			return null;
		}
		final Arity desiredArity = operProps.getArity();
		for (IPropertyParserInfo<? extends Formula<?>> parserInfo : list) {
			final Arity arity = parserInfo.getProperties().getArity();
			if(arity.contains(desiredArity)) {
				return parserInfo.makeParser(kind, tag);
			}
		}
		return null;
	}

}
