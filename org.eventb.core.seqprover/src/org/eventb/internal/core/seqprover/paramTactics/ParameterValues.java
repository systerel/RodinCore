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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			if (!(obj instanceof AbstractParameterValue)) {
				return false;
			}
			AbstractParameterValue<?> other = (AbstractParameterValue<?>) obj;
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
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
