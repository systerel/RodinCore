/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.internal.core.ast.extension.ExtensionSignature.ExpressionExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionSignature.PredicateExtSignature;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.ExpressionExtTranslator;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.PredicateExtTranslator;
import org.eventb.internal.core.ast.extension.ExtensionTranslator.TypeExtTranslator;

/**
 * Maintains a map between extension signatures and extension translations. Two
 * subclasses are provided, one for predicate extensions, the other for
 * expression extensions.
 * 
 * @author Laurent Voisin
 */
public abstract class TranslatorRegistry<S extends ExtensionSignature, T extends ExtensionTranslator> {

	private final ExtensionTranslation translation;

	private final Map<S, T> translators = new HashMap<S, T>();

	public TranslatorRegistry(ExtensionTranslation translation) {
		this.translation = translation;
	}

	public T get(S signature) {
		T result = translators.get(signature);
		if (result == null) {
			final FreeIdentifier function = translation.makeFunction(signature);
			result = newTranslator(function);
			translators.put(signature, result);
		}
		return result;
	}

	protected abstract T newTranslator(FreeIdentifier function);

	public static class PredTranslatorRegistry extends
			TranslatorRegistry<PredicateExtSignature, PredicateExtTranslator> {

		public PredTranslatorRegistry(ExtensionTranslation translation) {
			super(translation);
		}

		@Override
		protected PredicateExtTranslator newTranslator(FreeIdentifier function) {
			return new PredicateExtTranslator(function);
		}

	}

	public static class ExprTranslatorRegistry extends
			TranslatorRegistry<ExpressionExtSignature, ExpressionExtTranslator> {

		public ExprTranslatorRegistry(ExtensionTranslation translation) {
			super(translation);
		}

		@Override
		protected ExpressionExtTranslator newTranslator(FreeIdentifier function) {
			return new ExpressionExtTranslator(function);
		}

	}


	public static class TypeTranslatorRegistry extends
			TranslatorRegistry<ExpressionExtSignature, TypeExtTranslator> {

		public TypeTranslatorRegistry(ExtensionTranslation translation) {
			super(translation);
		}

		@Override
		protected TypeExtTranslator newTranslator(FreeIdentifier typeName) {
			return new TypeExtTranslator(typeName);
		}

	}

}
