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
import org.eventb.core.seqprover.IAutoTacticRegistry.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.IParamTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticInstantiator;
import org.eventb.core.seqprover.IParamTacticInstantiator;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticCombinator;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.paramTactics.ParameterSetting;

/**
 * @author Nicolas Beauger
 * 
 */
public class TacticDescriptors {

	@SuppressWarnings("unchecked")
	private static <T> T loadInstance(
			IConfigurationElement configurationElement, Class<T> expectedClass,
			String tacticId) {
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
				System.out.println("Successfully loaded tactic " + tacticId);

			return (T) loadedInstance;
		} catch (Exception e) {
			final String className = configurationElement.getAttribute("class");
			final String errorMsg = "Error instantiating class " + className
					+ " for tactic " + tacticId;
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

	private static ITactic logAndMakeFailure(Throwable t, String logMessage,
			String failTacMessage) {
		Util.log(t, logMessage);
		return BasicTactics.failTac(failTacMessage);
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

	public static class ParamTacticInstantiator implements
			IParamTacticInstantiator {

		private final UninstantiableTacticDescriptor descriptor;
		private final Collection<IParameterDesc> parameterDescs;
		private final IConfigurationElement element;

		/**
		 * Tactic parameterizer lazily loaded
		 */
		private ITacticParameterizer parameterizer;

		public ParamTacticInstantiator(
				UninstantiableTacticDescriptor descriptor,
				Collection<IParameterDesc> parameterDescs,
				IConfigurationElement element) {
			this.element = element;
			this.descriptor = descriptor;
			this.parameterDescs = parameterDescs;
		}

		@Override
		public Collection<IParameterDesc> getParameterDescs() {
			return Collections.unmodifiableCollection(parameterDescs);
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
		public IParamTacticDescriptor instantiate(IParameterValuation valuation)
				throws IllegalArgumentException {
			if (parameterizer == null) {
				parameterizer = loadInstance(element,
						ITacticParameterizer.class, descriptor.getTacticID());
			}
			return new ParamTacticDescriptor(descriptor.getTacticID(),
					descriptor.getTacticName(),
					descriptor.getTacticDescription(), parameterizer, valuation);
		}

	}

	public static class ParamTacticDescriptor extends AbstractTacticDescriptor
			implements IParamTacticDescriptor {

		private final IParameterValuation valuation;
		private final ITacticParameterizer parameterizer;
		private ITactic tactic;

		public ParamTacticDescriptor(String id, String name,
				String description, ITacticParameterizer parameterizer,
				IParameterValuation valuation) {
			super(id, name, description);
			this.parameterizer = parameterizer;
			this.valuation = valuation;
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			if (tactic != null) {
				return tactic;
			}
			try {
				tactic = parameterizer.getTactic(valuation);
				if (tactic == null) {
					throw new NullPointerException(
							"null tactic returned by parameterizer");
				}
				return tactic;
			} catch (Throwable t) {
				return logAndMakeFailure(t,
						"while making parameterized tactic " + getTacticID()
								+ " with parameter valuation " + valuation,
						"failed to create parameterized tactic "
								+ getTacticName());
			}
		}

		@Override
		public IParameterValuation getValuation() {
			return valuation;
		}

	}

	public static class CombinedTacticInstantiator implements
			ICombinedTacticInstantiator {

		private final UninstantiableTacticDescriptor descriptor;
		private final int minArity;
		private final boolean isArityBound;
		private final IConfigurationElement element;
		private ITacticCombinator combinator;

		public CombinedTacticInstantiator(
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
		public ICombinedTacticDescriptor instantiate(
				List<ITacticDescriptor> tactics)
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
			return new CombinedTacticDescriptor(descriptor.getTacticID(),
					descriptor.getTacticName(),
					descriptor.getTacticDescription(), combinator, tactics);
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
		private final List<ITacticDescriptor> combinedDescs;
		private final List<ITactic> combined;
		private ITactic tactic;

		public CombinedTacticDescriptor(String id, String name,
				String description, ITacticCombinator combinator,
				List<ITacticDescriptor> combinedDescs) {
			super(id, name, description);
			this.combinator = combinator;
			this.combinedDescs = combinedDescs;
			this.combined = new ArrayList<ITactic>(combinedDescs.size());
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			if (tactic != null) {
				return tactic;
			}
			try {
				for (ITacticDescriptor desc : combinedDescs) {
					final ITactic combinedInst = desc.getTacticInstance();
					combined.add(combinedInst);
				}
				tactic = combinator.getTactic(combined);
				if (tactic == null) {
					throw new NullPointerException(
							"null tactic returned by combinator");
				}
				return tactic;
			} catch (Throwable t) {
				return logAndMakeFailure(t, "while making combined tactic "
						+ getTacticID() + " with tactics " + combinedDescs,
						"failed to create combined tactic " + getTacticName());
			}
		}

		@Override
		public List<ITacticDescriptor> getCombinedTactics() {
			return Collections.unmodifiableList(combinedDescs);
		}

	}
}
