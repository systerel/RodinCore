/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticCombinator;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.internal.core.seqprover.paramTactics.ParameterSetting;

/**
 * @author Nicolas Beauger
 * 
 */
public class TacticDescriptors {

	@SuppressWarnings("unchecked")
	public static <T> T loadInstance(
			IConfigurationElement configurationElement, Class<T> expectedClass,
			String id) {
	if (configurationElement == null) {
			throw new IllegalArgumentException("Null configuration element");
		}

		// Try creating an instance of the specified class
		try {
			final Object loadedInstance = configurationElement
					.createExecutableExtension("class");
			if (!expectedClass.isInstance(loadedInstance)) {
				throw new IllegalArgumentException("unexpected instance");
			}
			if (AutoTacticRegistry.DEBUG)
				System.out.println("Successfully loaded " + id);

			return (T) loadedInstance;
		} catch (Exception e) {
			final String className = configurationElement.getAttribute("class");
			final String errorMsg = "Error instantiating class " + className
					+ " for " + id;
			Util.log(e, errorMsg);
			if (AutoTacticRegistry.DEBUG)
				System.out.println(errorMsg);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	/**
	 * Private helper class implementing lazy loading of tactic instances
	 */
	public static abstract class AbstractTacticDescriptor implements
			ITacticDescriptor {

		private final String id;
		private final String name;
		private final String description;

		public AbstractTacticDescriptor(String id, String name,
				String description) {
			this.id = id;
			this.name = name;
			this.description = description;
		}

		public synchronized String getTacticDescription()
				throws IllegalArgumentException {
			return description;
		}

		public String getTacticID() {
			return id;
		}

		public String getTacticName() {
			return name;
		}

	}

	public static class UninstantiableTacticDescriptor extends
			AbstractTacticDescriptor {

		public UninstantiableTacticDescriptor(String id, String name,
				String description) {
			super(id, name, description);
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			throw new UnsupportedOperationException(
					"this descriptor cannot be instantiated");
		}

	}

	public static class TacticDescriptor extends AbstractTacticDescriptor {

		/**
		 * Tactic instance lazily loaded using <code>configurationElement</code>
		 */
		private ITactic instance;
		private final IConfigurationElement element;

		public TacticDescriptor(IConfigurationElement element, String id,
				String name, String description) {
			super(id, name, description);
			this.element = element;
		}

		public synchronized ITactic getTacticInstance() {
			if (instance != null) {
				return instance;
			}
			instance = loadInstance(element, ITactic.class, getTacticID());
			return instance;
		}

	}

	public static class ParameterizerDescriptor implements
			IParameterizerDescriptor {

		private final UninstantiableTacticDescriptor descriptor;
		private final Collection<IParameterDesc> parameterDescs;
		private final IConfigurationElement element;

		/**
		 * Tactic parameterizer lazily loaded
		 */
		private ITacticParameterizer parameterizer;

		public ParameterizerDescriptor(
				UninstantiableTacticDescriptor descriptor,
				Collection<IParameterDesc> parameterDescs,
				IConfigurationElement element) {
			this.element = element;
			this.descriptor = descriptor;
			this.parameterDescs = parameterDescs;
		}

		@Override
		public IParameterSetting makeParameterSetting() {
			return new ParameterSetting(parameterDescs);
		}

		@Override
		public ITacticDescriptor getTacticDescriptor() {
			return descriptor;
		}

		@Override
		public IParamTacticDescriptor instantiate(
				IParameterValuation valuation, String id)
				throws IllegalArgumentException {
			if (parameterizer == null) {
				parameterizer = loadInstance(element,
						ITacticParameterizer.class, descriptor.getTacticID());
			}
			return new ParamTacticDescriptor(id, descriptor.getTacticName(),
					descriptor.getTacticDescription(), parameterizer,
					descriptor.getTacticID(), valuation);
		}

	}

	public static class ParamTacticDescriptor extends AbstractTacticDescriptor
			implements IParamTacticDescriptor {

		private final ITacticParameterizer parameterizer;
		private final String parameterizerId;
		private final IParameterValuation valuation;
		private ITactic tactic;

		public ParamTacticDescriptor(String id, String name,
				String description, ITacticParameterizer parameterizer,
				String parameterizerId, IParameterValuation valuation) {
			super(id, name, description);
			this.parameterizer = parameterizer;
			this.parameterizerId = parameterizerId;
			this.valuation = valuation;
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			if (tactic != null) {
				return tactic;
			}
			try {
				tactic = parameterizer.getTactic(valuation);
			} catch (Throwable t) {
				throw new IllegalArgumentException(t);
			}
			if (tactic == null) {
				throw new IllegalArgumentException(
						"null tactic returned by parameterizer");
			}
			return tactic;
		}

		@Override
		public String getParameterizerId() {
			return parameterizerId;
		}

		@Override
		public IParameterValuation getValuation() {
			return valuation;
		}

	}

	public static class CombinatorDescriptor implements
			ICombinatorDescriptor {

		private final UninstantiableTacticDescriptor descriptor;
		private final int minArity;
		private final boolean isArityBound;
		private final IConfigurationElement element;
		private ITacticCombinator combinator;

		public CombinatorDescriptor(
				UninstantiableTacticDescriptor descriptor, int minArity,
				boolean isArityBound, IConfigurationElement element) {
			this.descriptor = descriptor;
			this.minArity = minArity;
			this.isArityBound = isArityBound;
			this.element = element;
		}

		@Override
		public ITacticDescriptor getTacticDescriptor() {
			return descriptor;
		}

		@Override
		public ICombinedTacticDescriptor combine(
				List<ITacticDescriptor> tactics, String id)
				throws IllegalArgumentException {
			if (combinator == null) {
				combinator = loadInstance(element, ITacticCombinator.class,
						descriptor.getTacticID());
			}
			final int size = tactics.size();
			if (!checkCombinedArity(size)) {
				throw new IllegalArgumentException(
						"Invalid number of combined tactics, expected "
								+ minArity
								+ (isArityBound ? " exactly, " : " or more, ")
								+ "but was " + size);
			}
			return new CombinedTacticDescriptor(id, descriptor.getTacticName(),
					descriptor.getTacticDescription(), combinator,
					descriptor.getTacticID(), tactics);
		}

		private boolean checkCombinedArity(int size) {
			if (isArityBound)
				return size == minArity;
			else
				return size >= minArity;
		}

		@Override
		public int getMinArity() {
			return minArity;
		}

		@Override
		public boolean isArityBound() {
			return isArityBound;
		}

	}

	public static class CombinedTacticDescriptor extends
			AbstractTacticDescriptor implements ICombinedTacticDescriptor {

		private final ITacticCombinator combinator;
		private final String combinatorId;
		private final List<ITacticDescriptor> combinedDescs;
		private final List<ITactic> combined;
		private ITactic tactic;

		public CombinedTacticDescriptor(String id, String name,
				String description, ITacticCombinator combinator,
				String combinatorId, List<ITacticDescriptor> combinedDescs) {
			super(id, name, description);
			this.combinator = combinator;
			this.combinatorId = combinatorId;
			this.combinedDescs = combinedDescs;
			this.combined = new ArrayList<ITactic>(combinedDescs.size());
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			if (tactic != null) {
				return tactic;
			}
			for (ITacticDescriptor desc : combinedDescs) {
				final ITactic combinedInst = desc.getTacticInstance();
				combined.add(combinedInst);
			}
			try {
				tactic = combinator.getTactic(combined);
			} catch (Throwable t) {
				throw new IllegalArgumentException(t);
			}
			if (tactic == null) {
				throw new IllegalArgumentException(
						"null tactic returned by combinator");
			}
			return tactic;
		}

		@Override
		public String getCombinatorId() {
			return combinatorId;
		}

		@Override
		public List<ITacticDescriptor> getCombinedTactics() {
			return Collections.unmodifiableList(combinedDescs);
		}

	}
}
