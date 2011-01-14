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
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.internal.core.ast.extension.OperatorCoverage;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 * 
 */
public class PropertyParserDB {

	private static final class Properties {
		
		private final Notation notation;
		private final FormulaType formulaType;
		private final boolean isAssociative;

		public Properties(IOperatorProperties operProps) {
			this(operProps.getNotation(), operProps.getFormulaType(), operProps
					.isAssociative());
		}

		public Properties(OperatorCoverage opCover) {
			this(opCover.getNotation(), opCover.getFormulaType(), opCover
					.isAssociative());
		}

		public Properties(Notation notation, FormulaType formulaType,
				boolean isAssociative) {
			this.notation = notation;
			this.formulaType = formulaType;
			this.isAssociative = isAssociative;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + formulaType.hashCode();
			result = prime * result + (isAssociative ? 1231 : 1237);
			result = prime * result + notation.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Properties)) {
				return false;
			}
			Properties other = (Properties) obj;
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

	private final Map<Properties, List<IPropertyParserInfo<? extends Formula<?>>>> map = new HashMap<Properties, List<IPropertyParserInfo<? extends Formula<?>>>>();

	public void add(IPropertyParserInfo<? extends Formula<?>> newParser)
			throws OverrideException {
		final OperatorCoverage newOpCover = newParser.getOperatorCoverage();

		final Properties newProp = new Properties(newOpCover);

		List<IPropertyParserInfo<? extends Formula<?>>> list = map.get(newProp);
		if (list == null) {
			list = new ArrayList<IPropertyParserInfo<? extends Formula<?>>>();
			map.put(newProp, list);
		}
		for (IPropertyParserInfo<? extends Formula<?>> parser : list) {
			final OperatorCoverage opCover = parser.getOperatorCoverage();
			if (newOpCover.conflictsWith(opCover)) {
				throw new GenParser.OverrideException("overriding a parser");
			}
		}
		list.add(newParser);
	}

	public IOperatorInfo<? extends Formula<?>> getParser(
			IOperatorProperties operProps, String image, int tag, String opId,
			String groupId) {
		final Properties prop = new Properties(operProps);
		final List<IPropertyParserInfo<? extends Formula<?>>> list = map
				.get(prop);
		if (list == null) {
			return null;
		}
		for (IPropertyParserInfo<? extends Formula<?>> parserInfo : list) {
			final OperatorCoverage opCover = parserInfo.getOperatorCoverage();
			if (opCover.covers(operProps)) {
				return parserInfo.makeOpInfo(image, tag, opId, groupId);
			}
		}
		return null;
	}

}
