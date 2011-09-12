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
package org.eventb.core.seqprover;

/**
 * Common protocol for parameter descriptors.
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IParameterDesc {

	enum ParameterType {
		BOOL {
			@Override
			public Boolean parse(String image) {
				return Boolean.valueOf(image);
			}

			@Override
			public boolean check(Object o) {
				return o instanceof Boolean;
			}
		},
		INT {
			@Override
			public Integer parse(String image) {
				return Integer.decode(image);
			}

			@Override
			public boolean check(Object o) {
				return o instanceof Integer;
			}
		},
		LONG {
			@Override
			public Long parse(String image) {
				return Long.decode(image);
			}

			@Override
			public boolean check(Object o) {
				return o instanceof Long;
			}
		},
		STRING {
			@Override
			public String parse(String image) {
				return image;
			}

			@Override
			public boolean check(Object o) {
				return o instanceof String;
			}
		};
		public abstract boolean check(Object o);

		public abstract Object parse(String image);
	}

	/**
	 * Returns the label of the described parameter.
	 * 
	 * @return a label
	 */
	String getLabel();

	/**
	 * Returns the type of the described parameter.
	 * 
	 * @return a parameter type
	 */
	ParameterType getType();

	/**
	 * Returns the default value of the described parameter.
	 * <p>
	 * The java type of the default value corresponds to the parameter type, as
	 * returned by {@link #getType()}.
	 * </p>
	 * 
	 * @return an object
	 */
	Object getDefaultValue();

	/**
	 * Returns the description of the described parameter.
	 * 
	 * @return a string
	 */
	String getDescription();

}
