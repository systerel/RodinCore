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
package org.eventb.internal.core.seqprover.paramTactics;

import org.eventb.core.seqprover.IParameterDesc;

/**
 * @author Nicolas Beauger
 *
 */
public class ParameterValues {

	public static abstract class AbstractParameterValue<T> {
		private T value;
		
		public AbstractParameterValue(IParameterDesc desc) {
			setValue(desc.getDefaultValue());
		}
	
		protected abstract T asValue(Object o);
		
		public Object getValue() {
			return value;
		}
	
		public void setValue(Object value) {
			this.value = asValue(value);
		}
		
		@Override
		public String toString() {
			return value.toString();
		}
	}

	public static class BoolParameterValue extends AbstractParameterValue<Boolean> {
	
		public BoolParameterValue(IParameterDesc desc) {
			super(desc);
		}
	
		@Override
		public Boolean asValue(Object o) {
			return (Boolean) o;
		}
		
	}

	public static class IntParameterValue extends AbstractParameterValue<Integer> {
	
		public IntParameterValue(IParameterDesc desc) {
			super(desc);
		}
	
		@Override
		public Integer asValue(Object o) {
			return (Integer) o;
		}
	}

	public static class LongParameterValue extends AbstractParameterValue<Long> {
	
		public LongParameterValue(IParameterDesc desc) {
			super(desc);
		}
	
		@Override
		public Long asValue(Object o) {
			return (Long) o;
		}
	}

	public static class StringParameterValue extends AbstractParameterValue<String> {
	
		public StringParameterValue(IParameterDesc desc) {
			super(desc);
		}
	
		@Override
		public String asValue(Object o) {
			return (String) o;
		}
	}

}
