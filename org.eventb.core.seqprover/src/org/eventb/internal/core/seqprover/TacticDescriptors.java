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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.seqprover.IAutoTacticRegistry.IParamTacticDescriptor;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.ITactic;
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

	}

	static class TacticDescriptor extends AbstractTacticDescriptor {

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

	static class ParamTacticDescriptor extends AbstractTacticDescriptor
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
					return logAndMakeFailure(new NullPointerException(
							"null instance returned"), valuation);
				}
				return tactic;
			} catch (Throwable t) {
				return logAndMakeFailure(t, valuation);
			}
		}

		private ITactic logAndMakeFailure(Throwable t,
				IParameterValuation valuation) {
			Util.log(t, "while making parameterized tactic " + getTacticID()
					+ " with parameter valuation " + valuation);
			return BasicTactics
					.failTac("failed to create parameterized tactic");
		}

		@Override
		protected boolean checkInstance(Object instance) {
			return instance instanceof ITacticParameterizer;
		}

	}

}
