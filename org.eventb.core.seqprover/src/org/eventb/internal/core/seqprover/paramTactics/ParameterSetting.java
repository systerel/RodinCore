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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.AbstractParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.BoolParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.IntParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.LongParameterValue;
import org.eventb.internal.core.seqprover.paramTactics.ParameterValues.StringParameterValue;

// TODO toString()
public class ParameterSetting implements IParameterSetting {

	private final Collection<IParameterDesc> paramDescs;
	private final Map<String, AbstractParameterValue<?>> valuation = new LinkedHashMap<String, AbstractParameterValue<?>>();

	public ParameterSetting(Collection<IParameterDesc> paramDescs) {
		this.paramDescs = new ArrayList<IParameterDesc>(paramDescs);
		initDefaultValuation();
	}

	private void initDefaultValuation() {
		AbstractParameterValue<?> value = null;
		for (IParameterDesc desc : paramDescs) {
			switch (desc.getType()) {
			case BOOL:
				value = new BoolParameterValue(desc);
				break;
			case INT:
				value = new IntParameterValue(desc);
				break;
			case LONG:
				value = new LongParameterValue(desc);
				break;
			case STRING:
				value = new StringParameterValue(desc);
				break;
			default:
				assert false;
			}
			valuation.put(desc.getLabel(), value);
		}
	}

	@Override
	public Collection<IParameterDesc> getParameterDescs() {
		return Collections.unmodifiableCollection(paramDescs);
	}

	private AbstractParameterValue<?> checkAndGet(String label, ParameterType expectedType) {
		final AbstractParameterValue<?> paramValue = valuation.get(label);
		if (paramValue == null) {
			throw new IllegalArgumentException("unknown label "+label);
		}
		if (!expectedType.check(paramValue.getValue())) {
			throw new IllegalArgumentException("parameter " + label
					+ " does not have type " + expectedType);
		}
		return paramValue;
	}

	private void checkAndSet(String label, ParameterType expectedType, Object value) {
		final AbstractParameterValue<?> paramValue = checkAndGet(label, expectedType);
		paramValue.setValue(value);
	}

	@Override
	public void setBoolean(String label, Boolean value) {
		checkAndSet(label, ParameterType.BOOL, value);
	}

	@Override
	public void setInt(String label, Integer value) {
		checkAndSet(label, ParameterType.INT, value);
	}

	@Override
	public void setLong(String label, Long value) {
		checkAndSet(label, ParameterType.LONG, value);
	}

	@Override
	public void setString(String label, String value) {
		checkAndSet(label, ParameterType.STRING, value);
	}

	@Override
	public boolean getBoolean(String label) {
		final AbstractParameterValue<?> paramValue = checkAndGet(label,
				ParameterType.BOOL);
		return (Boolean) paramValue.getValue();
	}

	@Override
	public int getInt(String label) {
		final AbstractParameterValue<?> paramValue = checkAndGet(label,
				ParameterType.INT);
		return (Integer) paramValue.getValue();
	}

	@Override
	public long getLong(String label) {
		final AbstractParameterValue<?> paramValue = checkAndGet(label,
				ParameterType.LONG);
		return (Long) paramValue.getValue();
	}

	@Override
	public String getString(String label) {
		final AbstractParameterValue<?> paramValue = checkAndGet(label,
				ParameterType.STRING);
		return (String) paramValue.getValue();
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (Entry<String, AbstractParameterValue<?>> val : valuation
				.entrySet()) {
			sb.append(val.getKey());
			sb.append(" = ");
			sb.append(val.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}