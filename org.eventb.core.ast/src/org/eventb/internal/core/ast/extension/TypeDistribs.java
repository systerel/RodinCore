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
package org.eventb.internal.core.ast.extension;

import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;
import static org.eventb.core.ast.extension.IOperatorProperties.NULLARY;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IArity;
import org.eventb.core.ast.extension.ITypeDistribution;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

/**
 * @author Nicolas Beauger
 * 
 */
public class TypeDistribs {
	
	public static class AllSameType implements ITypeDistribution {

		private final FormulaType type;
		private final IArity arity;
		
		public AllSameType(FormulaType type, IArity arity) {
			this.type = type;
			this.arity = arity;
		}

		@Override
		public IArity getExprArity() {
			switch (type) {
			case EXPRESSION:
				return arity;
			case PREDICATE:
				return NULLARY;
			default:
				assert false;
				return null;
			}
		}

		@Override
		public IArity getPredArity() {
			switch (type) {
			case EXPRESSION:
				return NULLARY;
			case PREDICATE:
				return arity;
			default:
				assert false;
				return null;
			}
		}

		@Override
		public boolean check(List<? extends Formula<?>> proposedChildren) {
			if (!arity.check(proposedChildren.size())) {
				return false;
			}
			for (Formula<?> proposedChild : proposedChildren) {
				if (!type.check(proposedChild)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public List<Formula<?>> makeList(Expression[] childExprs,
				Predicate[] childPreds) {
			final List<Formula<?>> list;
			if (type == EXPRESSION) {
				list = Arrays.<Formula<?>> asList(childExprs);
			} else {
				list = Arrays.<Formula<?>> asList(childPreds);
			}
			assert arity.check(list.size());
			return new ArrayList<Formula<?>>(list);
		}

	}

	public static class MixedTypes implements ITypeDistribution {

		private final FormulaType[] types;
		private final IArity exprArity;
		private final IArity predArity;

		public MixedTypes(FormulaType[] types) {
			this.types = types;
			int nbExprs = 0;
			int nbPreds = 0;
			for (FormulaType type : types) {
				switch (type) {
				case EXPRESSION:
					nbExprs++;
					break;
				case PREDICATE:
					nbPreds++;
					break;
				}
			}
			this.exprArity = makeFixedArity(nbExprs);
			this.predArity = makeFixedArity(nbPreds);
		}

		@Override
		public IArity getExprArity() {
			return exprArity;
		}

		@Override
		public IArity getPredArity() {
			return predArity;
		}

		@Override
		public boolean check(List<? extends Formula<?>> proposedChildren) {
			if (proposedChildren.size() != types.length) {
				return false;
			}
			for (int i = 0; i < types.length; i++) {
				final Formula<?> proposedChild = proposedChildren.get(i);
				if (!types[i].check(proposedChild)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public List<Formula<?>> makeList(Expression[] childExprs,
				Predicate[] childPreds) {
			assert exprArity.check(childExprs.length);
			assert predArity.check(childPreds.length);

			final List<Formula<?>> children = new ArrayList<Formula<?>>();
			int exprIndex = 0;
			int predIndex = 0;
			for (FormulaType type : types) {
				switch (type) {
				case EXPRESSION:
					children.add(childExprs[exprIndex]);
					exprIndex++;
					break;
				case PREDICATE:
					children.add(childPreds[predIndex]);
					predIndex++;
					break;
				}
			}
			return children;
		}

	}

}
