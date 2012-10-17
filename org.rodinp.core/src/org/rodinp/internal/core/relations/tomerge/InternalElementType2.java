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

import java.util.List;

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

	private List<IInternalElementType<?>> parentTypes = null;
	private List<IInternalElementType<?>> childTypes = null;
	private List<IAttributeType> attributeTypes = null;

	public InternalElementType2(IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	@Override
	public boolean canParent(IInternalElementType<?> childType) {
		return childTypes.contains(childType);
	}

	@Override
	public IInternalElementType<?>[] getChildTypes() {
		return childTypes
				.toArray(new IInternalElementType<?>[childTypes.size()]);
	}

	@Override
	public IInternalElementType<?>[] getParentTypes() {
		return parentTypes.toArray(new IInternalElementType<?>[parentTypes
				.size()]);
	}

	@Override
	public IAttributeType[] getAttributeTypes() {
		return attributeTypes
				.toArray(new IAttributeType[attributeTypes.size()]);
	}

	@Override
	public boolean canCarry(IAttributeType attributeType) {
		return attributeTypes.contains(attributeType);
	}

	public void setRelation(IInternalElementType<?>[] pTypes,
			IInternalElementType<?>[] cTypes, IAttributeType[] aTypes) {
		if (parentTypes != null || childTypes != null || attributeTypes != null) {
			throw new IllegalStateException(
					"Illegal attempt to set relations for internal element type "
							+ getName());
		}
		this.parentTypes = asList(pTypes);
		this.childTypes = asList(cTypes);
		this.attributeTypes = asList(aTypes);
	}

}