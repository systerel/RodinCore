package fr.systerel.editor.documentModel;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.EventBAttributes;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

public class DocumentElementUtils {
	
	private static final IAttributeType[] BASIC_ATTRIBUTE_TYPES = {
		ASSIGNMENT_ATTRIBUTE, COMMENT_ATTRIBUTE, IDENTIFIER_ATTRIBUTE,
		LABEL_ATTRIBUTE, PREDICATE_ATTRIBUTE, EXPRESSION_ATTRIBUTE};
	
	// Retrieves the element desc from the registry for the given element e
	@SuppressWarnings("restriction")
	public static IElementDesc getElementDesc(ILElement e) {
		final IRodinElement rodinElement = (IRodinElement) e.getElement();
		return ElementDescRegistry.getInstance().getElementDesc(rodinElement);
	}

	/**
	 * Retrieves the element desc from the registry for the given element type
	 * <code>type<code>.
	 * 
	 * @param type
	 *            the element type to retrieve the descriptor for
	 */
	@SuppressWarnings("restriction")
	public static IElementDesc getElementDesc(IInternalElementType<?> type) {
		return ElementDescRegistry.getInstance().getElementDesc(type);
	}

	@SuppressWarnings("restriction")
	public static List<IAttributeDesc> getAttributeDescs(
			IInternalElementType<?> elementType) {
		final List<IAttributeDesc> descs = new ArrayList<IAttributeDesc>();
		int i = 0;
		IAttributeDesc desc;
		final List<IAttributeType> refList = Arrays
				.asList(BASIC_ATTRIBUTE_TYPES);
		while ((desc = ElementDescRegistry.getInstance().getAttribute(
				elementType, i)) != null) {
			if (!refList.contains(desc.getAttributeType())) {
				descs.add(desc);
			}
			i++;
		}
		return descs;
	}
	
	public static Set<IInternalElementType<?>> getChildPossibleTypes(
			ILElement element) {
		final IElementDesc eDesc = getElementDesc(element);
		final Set<IInternalElementType<?>> types = new HashSet<IInternalElementType<?>>();
		for (IElementType<?> t : eDesc.getChildTypes()) {
			if (t instanceof IInternalElementType<?>) {
				types.add((IInternalElementType<?>) t);
			}
		}
		return types;
	}
	
	public static ILElement getSibling(ILElement element) {
		final ILElement parent = element.getParent();
		if (parent == null) {
			return null;
		}
		final List<ILElement> ofType = parent.getChildrenOfType(element
				.getElementType());
		int sibling = 0;
		for (ILElement el : ofType) {
			if (el == element) {
				sibling++;
				break;
			}
			sibling++;
		}
		if (sibling < ofType.size()) {
			return ofType.get(sibling);
		}
		return null;
	}
	
}
