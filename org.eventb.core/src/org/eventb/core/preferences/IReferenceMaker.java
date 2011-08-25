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
package org.eventb.core.preferences;


/**
 * Common protocol for reference makers.
 * <p>
 * References makers make a reference of type T from an object of type T.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @see CachedPreferenceMap
 */
public interface IReferenceMaker<T> {

	T makeReference(IPreferenceUnit<T> prefUnit);
}
