package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.List;

import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.rodinp.core.IElementType;

public interface IElementRelUISpecRegistry {

	public abstract List<IElementRelationship> getElementRelationships(
			IElementType<?> parentType);

	public abstract String getPrefix(IElementRelationship rel);

	public abstract String getPostfix(IElementRelationship rel);

}