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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.seqprover.IAutoTacticRegistry.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.IParamTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
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

	/**
	 * Private helper class implementing lazy loading of tactic instances
	 */
	public static abstract class AbstractTacticDescriptor implements
			ITacticDescriptor {

		private final IConfigurationElement configurationElement;
		private final String id;
		private final String name;
		private final String description;

		public AbstractTacticDescriptor(IConfigurationElement element,
				String id, String name, String description) {
			this.configurationElement = element;
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

		protected Object loadInstance() {
			if (configurationElement == null) {
				throw new IllegalArgumentException("Null configuration element");
			}

			// Try creating an instance of the specified class
			try {
				final Object loadedInstance = configurationElement
						.createExecutableExtension("class");
				if (!checkInstance(loadedInstance)) {
					throw new IllegalArgumentException("unexpected instance");
				}
				if (AutoTacticRegistry.DEBUG)
					System.out.println("Successfully loaded tactic " + id);

				return loadedInstance;
			} catch (Exception e) {
				final String className = configurationElement
						.getAttribute("class");
				final String errorMsg = "Error instantiating class "
						+ className + " for tactic " + id;
				Util.log(e, errorMsg);
				if (AutoTacticRegistry.DEBUG)
					System.out.println(errorMsg);
				throw new IllegalArgumentException(errorMsg, e);
			}

		}

		protected abstract boolean checkInstance(Object instance);

		protected static ITactic logAndMakeFailure(Throwable t,
				String logMessage, String failTacMessage) {
			Util.log(t, logMessage);
			return BasicTactics.failTac(failTacMessage);
		}

	}

	public static class TacticDescriptor extends AbstractTacticDescriptor {

		/**
		 * Tactic instance lazily loaded using <code>configurationElement</code>
		 */
		private ITactic instance;

		public TacticDescriptor(IConfigurationElement element, String id,
				String name, String description) {
			super(element, id, name, description);
		}

		public synchronized ITactic getTacticInstance() {
			if (instance != null) {
				return instance;
			}
			instance = (ITactic) loadInstance();
			return instance;
		}

		@Override
		protected boolean checkInstance(Object instance) {
			return instance instanceof ITactic;
		}

	}

	public static class ParamTacticDescriptor extends AbstractTacticDescriptor
			implements IParamTacticDescriptor {

		/**
		 * Tactic instance lazily loaded using <code>configurationElement</code>
		 */
		private ITacticParameterizer parameterizer;
		private final Collection<IParameterDesc> parameterDescs;

		public ParamTacticDescriptor(IConfigurationElement element, String id,
				String name, String description,
				Collection<IParameterDesc> parameterDescs) {
			super(element, id, name, description);
			this.parameterDescs = parameterDescs;
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			return getTacticInstance(makeParameterSetting());
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
		public ITactic getTacticInstance(IParameterValuation valuation) {
			if (parameterizer == null) {
				parameterizer = (ITacticParameterizer) loadInstance();
			}
			return makeCheckedTactic(valuation);
		}

		private ITactic makeCheckedTactic(IParameterValuation valuation) {
			try {
				final ITactic tactic = parameterizer.getTactic(valuation);
				if (tactic == null) {
					throw new NullPointerException(
							"null tactic returned by parameterizer");
				}
				return tactic;
			} catch (Throwable t) {
				return logAndMakeFailure(t, "while making parameterized tactic " + getTacticID()
									+ " with parameter valuation " + valuation, "failed to create parameterized tactic "
						+ getTacticName());
			}
		}

		@Override
		protected boolean checkInstance(Object instance) {
			return instance instanceof ITacticParameterizer;
		}

	}

	public static class CombinedTacticDescriptor extends
			AbstractTacticDescriptor implements ICombinedTacticDescriptor {

		private final int minArity;
		private final boolean isArityBound;
		private ITacticCombinator combinator;

		public CombinedTacticDescriptor(IConfigurationElement element,
				String id, String name, String description, int minArity,
				boolean isArityBound) {
			super(element, id, name, description);
			this.minArity = minArity;
			this.isArityBound = isArityBound;
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			throw new IllegalArgumentException(
					"Combined tactic called without tactic arguments: "
							+ getTacticID());
		}

		@Override
		public ITactic getTacticInstance(List<ITactic> tactics)
				throws IllegalArgumentException {
			if (combinator == null) {
				combinator = (ITacticCombinator) loadInstance();
			}
			return makeCheckedTactic(tactics);
		}

		private ITactic makeCheckedTactic(List<ITactic> tactics) {
			final int size = tactics.size();
			if (!checkCombinedArity(size)) {
				throw new IllegalArgumentException(
						"Invalid number of combined tactics, expected "
								+ minArity + (isArityBound ? " exactly, " : " or more, ")
								+ "but was " + size);
			}
			try {
				final ITactic tactic = combinator.getTactic(tactics);
				if (tactic == null) {
					throw new NullPointerException(
							"null tactic returned by combinator");
				}
				return tactic;
			} catch (Throwable t) {
				return logAndMakeFailure(t, "while making combined tactic "
						+ getTacticID() + " with tactics " + tactics,
						"failed to create combined tactic " + getTacticName());
			}
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

		@Override
		protected boolean checkInstance(Object instance) {
			return instance instanceof ITacticCombinator;
		}

	}

}
