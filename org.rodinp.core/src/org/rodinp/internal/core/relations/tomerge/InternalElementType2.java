/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.relations.tomerge;

import static java.util.Arrays.asList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.relations.api.IInternalElementType2;

/**
 * Adds support for item relations to {@link InternalElementType}.
 * <p>
 * <em> RODIN 3.0 - for testing purpose only </em> <br>
 * TODO retrofit into {@link InternalElementType}
 * </p>
 * 
 * @author Laurent Voisin
 */
public class InternalElementType2<T extends IInternalElement> extends
		InternalElementType<T> implements IInternalElementType2<T> {

	private IInternalElementType<?>[] parentTypes = null;
	private IInternalElementType<?>[] childTypes = null;
	private IAttributeType[] attributeTypes = null;

	public InternalElementType2(IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	@Override
	public boolean canParent(IInternalElementType<?> childType) {
		return asList(childTypes).contains(childType);
	}

	@Override
	public IInternalElementType<?>[] getChildTypes() {
		return childTypes;
	}

	@Override
	public IInternalElementType<?>[] getParentTypes() {
		return parentTypes;
	}

	@Override
	public IAttributeType[] getAttributeTypes() {
		return attributeTypes;
	}

	@Override
	public boolean canCarry(IAttributeType attributeType) {
		return asList(attributeTypes).contains(attributeType);
	}

	public void setRelation(IInternalElementType<?>[] pTypes,
			IInternalElementType<?>[] cTypes, IAttributeType[] aTypes) {
		if (parentTypes != null || childTypes != null || attributeTypes != null) {
			throw new IllegalStateException(
					"Illegal attempt to set relations for internal element type "
							+ getName());
		}
		this.parentTypes = pTypes;
		this.childTypes = cTypes;
		this.attributeTypes = aTypes;
	}

}