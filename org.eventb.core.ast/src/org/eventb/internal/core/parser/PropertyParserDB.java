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

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.internal.core.ast.extension.ExtensionPrinters.IExtensionPrinter;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 * 
 */
public class PropertyParserDB {

	private static final class Properties {
		
		private final IOperatorProperties.Notation notation;
		private final IOperatorProperties.FormulaType formulaType;
		private final IOperatorProperties.Arity arity;
		private final IOperatorProperties.FormulaType argumentType;
		private final boolean isExtension;
		
		public Properties(IOperatorProperties.Notation notation, IOperatorProperties.FormulaType formulaType, IOperatorProperties.Arity arity,
				IOperatorProperties.FormulaType argumentType, boolean isExtension) {
			this.notation = notation;
			this.formulaType = formulaType;
			this.arity = arity;
			this.argumentType = argumentType;
			this.isExtension = isExtension;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((argumentType == null) ? 0 : argumentType.hashCode());
			result = prime * result + ((arity == null) ? 0 : arity.hashCode());
			result = prime * result
					+ ((formulaType == null) ? 0 : formulaType.hashCode());
			result = prime * result + (isExtension ? 1231 : 1237);
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
			if (argumentType == null) {
				if (other.argumentType != null) {
					return false;
				}
			} else if (!argumentType.equals(other.argumentType)) {
				return false;
			}
			if (arity == null) {
				if (other.arity != null) {
					return false;
				}
			} else if (!arity.equals(other.arity)) {
				return false;
			}
			if (formulaType == null) {
				if (other.formulaType != null) {
					return false;
				}
			} else if (!formulaType.equals(other.formulaType)) {
				return false;
			}
			if (isExtension != other.isExtension) {
				return false;
			}
			if (notation == null) {
				if (other.notation != null) {
					return false;
				}
			} else if (!notation.equals(other.notation)) {
				return false;
			}
			return true;
		}
		
	}
	
	private static Properties makeProp(IOperatorProperties operProps,
			boolean isExtension) {
		return new Properties(operProps.getNotation(),
				operProps.getFormulaType(), operProps.getArity(), operProps
						.getArgumentType(), isExtension);
	}
	
	private final Map<Properties, IParserBuilder> map = new HashMap<Properties, IParserBuilder>();

	public void add(IParserBuilder parserBuilder)
			throws OverrideException {
		final Properties prop = makeProp(parserBuilder.getOperatorProperties(), parserBuilder.isExtension());

		final IParserBuilder old = map.put(prop, parserBuilder);
		if (old != null) {
			map.put(prop, old);
			throw new GenParser.OverrideException("overriding a parser");
		}

	}

	public IParserPrinter getParser(IOperatorProperties operProps,
			boolean isExtension, int tag) {
		final Properties prop = makeProp(operProps, isExtension);
		final IParserBuilder parserBuilder = map.get(prop);
		if (parserBuilder == null) {
			return null;
		}
		return parserBuilder.makeParser(tag);
	}

	public IExtensionPrinter getPrinter(IOperatorProperties operProps,
			boolean isExtension) {
		final Properties prop = makeProp(operProps, isExtension);
		final IParserBuilder parserBuilder = map.get(prop);
		if (parserBuilder == null) {
			return null;
		}
		return parserBuilder.getPrinter();
	}

}
