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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.internal.core.AttributeType;
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
		InternalElementType<T> implements IInternalElementType2<T>,
		Comparable<InternalElementType2<?>> {

	private List<InternalElementType2<?>> parentTypes = null;
	private List<InternalElementType2<?>> childTypes = null;
	private List<AttributeType<?>> attributeTypes = null;

	public InternalElementType2(IConfigurationElement configurationElement) {
		super(configurationElement);
	}

	@Override
	public boolean canParent(IInternalElementType<?> childType) {
		return childTypes.contains(childType);
	}

	@Override
	public InternalElementType<?>[] getChildTypes() {
		return childTypes
				.toArray(new InternalElementType<?>[childTypes.size()]);
	}

	@Override
	public InternalElementType<?>[] getParentTypes() {
		return parentTypes.toArray(new InternalElementType<?>[parentTypes
				.size()]);
	}

	@Override
	public AttributeType<?>[] getAttributeTypes() {
		return attributeTypes
				.toArray(new AttributeType[attributeTypes.size()]);
	}

	@Override
	public boolean canCarry(IAttributeType attributeType) {
		return attributeTypes.contains(attributeType);
	}

	public void setRelation(List<InternalElementType2<?>> pTypes,
			List<InternalElementType2<?>> cTypes, List<AttributeType<?>> aTypes) {
		if (parentTypes != null || childTypes != null || attributeTypes != null) {
			throw new IllegalStateException(
					"Illegal attempt to set relations for internal element type "
							+ getName());
		}
		this.parentTypes = pTypes;
		this.childTypes = cTypes;
		this.attributeTypes = aTypes;
	}

	@Override
	public int compareTo(InternalElementType2<?> other) {
		return this.id.compareTo(other.id);
	}

}