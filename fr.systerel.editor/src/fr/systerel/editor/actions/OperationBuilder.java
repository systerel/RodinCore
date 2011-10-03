/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.actions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.editor.internal.actions.operations.ChangeAttribute;
import fr.systerel.editor.internal.actions.operations.ChangeAttributeWithManipulation;
import fr.systerel.editor.internal.actions.operations.CopyElement;
import fr.systerel.editor.internal.actions.operations.CreateElementGeneric;
import fr.systerel.editor.internal.actions.operations.CreateIdenticalElement;
import fr.systerel.editor.internal.actions.operations.DeleteElementLeaf;
import fr.systerel.editor.internal.actions.operations.Move;
import fr.systerel.editor.internal.actions.operations.OperationCreateElement;
import fr.systerel.editor.internal.actions.operations.OperationNode;

public class OperationBuilder {

	private final static OperationBuilder INSTANCE = new OperationBuilder();

	private OperationBuilder() {
		// singleton
	}

	public static OperationBuilder getDefault() {
		return INSTANCE;
	}

	public OperationTree deleteElement(IInternalElement element, boolean force) {
		final OperationTree cmdCreate = getCommandCreateElement(element);
		return new DeleteElementLeaf(element, cmdCreate, force);
	}

	public OperationTree deleteElement(IInternalElement[] elements,
			boolean force) {
		OperationNode op = new OperationNode();
		for (IInternalElement element : elements) {
			op.addCommand(deleteElement(element, force));
		}
		return op;
	}

	public <T extends IInternalElement> OperationCreateElement getCreateElement(
			IInternalElement parent, IInternalElementType<T> type,
			IInternalElement sibling, IAttributeValue[] values) {
		OperationCreateElement op = new OperationCreateElement(
				createDefaultElement(parent, type, sibling));
		op.addSubCommande(new ChangeAttribute(values));
		return op;
	}

	/**
	 * Return an operation to create an IInternalElement with a string attribute
	 */
	public <T extends IInternalElement> OperationTree createElementOneStringAttribute(
			IInternalElement root, IInternalElementType<T> elementType,
			IInternalElement sibling, IAttributeType.String type, String string) {
		final List<IAttributeValue> values = new LinkedList<IAttributeValue>();
		if (string != null) {
			values.add(type.makeValue(string));
		}
		final IAttributeValue[] array = values
				.toArray(new IAttributeValue[values.size()]);
		return getCreateElement(root, elementType, sibling, array);
	}

	public <T extends IInternalElement> OperationTree createElementOneStringAttribute(
			IInternalElement root, IInternalElementType<T> elementType,
			org.rodinp.core.IAttributeType.String type, String[] identifiers) {
		assert identifiers != null;
		final OperationNode op = new OperationNode();
		for (int i = 0; i < identifiers.length; i++) {
			op.addCommand(createElementOneStringAttribute(root, elementType,
					null, type, identifiers[i]));
		}
		return op;
	}

	/**
	 * return an Operation to create an Element with default name and label
	 * 
	 * @param parent
	 *            the element where the new element is inserted
	 * @param type
	 *            the type of the new element
	 * @param sibling
	 *            the new element is inserted before sibling. If sibling is
	 *            null, the new element is inserted after the last element in
	 *            parent.
	 * 
	 */
	public <T extends IInternalElement> CreateElementGeneric<T> createDefaultElement(
			IInternalElement parent, IInternalElementType<T> type,
			IInternalElement sibling) {
		return new CreateElementGeneric<T>(parent, type, sibling);
	}

	/**
	 * return a Command to create the element in parameter. if the element is
	 * delete, the Command create an identical element
	 * 
	 * @param element
	 *            an existing element
	 * @return a Command to create element
	 */
	private OperationTree getCommandCreateElement(IInternalElement element) {
		final OperationNode cmd = new OperationNode();
		cmd.addCommand(new CreateIdenticalElement(element));
		try {
			if (element.hasChildren()) {
				cmd.addCommand(getCommandCreateChildren(element.getChildren()));
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return cmd;

	}

	private OperationTree getCommandCreateChildren(IRodinElement[] children)
			throws RodinDBException {
		IInternalElement element;
		final OperationNode cmd = new OperationNode();
		for (IRodinElement rodinElement : children) {
			element = (IInternalElement) rodinElement;
			cmd.addCommand(getCommandCreateElement(element));
		}
		return cmd;
	}

	public <E extends IInternalElement> OperationTree changeAttribute(
			IAttributeManipulation factory, E element, String value) {
		final ChangeAttributeWithManipulation op = new ChangeAttributeWithManipulation(
				factory, element, value);
		return op;
	}

	public <E extends IInternalElement> OperationTree changeAttribute(
			E element, IAttributeValue value) {
		final ChangeAttribute op = new ChangeAttribute(element, value);
		return op;
	}

	private OperationTree copyElement(IInternalElement parent,
			IInternalElement element, IInternalElement sibling) {
		return new CopyElement(parent, element, sibling);
	}

	public OperationTree copyElements(IInternalElement parent,
			IRodinElement[] elements, IInternalElement sibling) {
		OperationNode op = new OperationNode();
		for (IRodinElement element : elements) {
			op.addCommand(copyElement(parent, (IInternalElement) element,
					sibling));
		}
		return op;
	}

	public OperationTree move(IInternalElement movedElement,
			IInternalElement newParent, IInternalElement newSibling) {
		return new Move(movedElement, newParent, newSibling);
	}

	public <T extends IInternalElement> OperationTree renameElement(
			IInternalElement parent, IInternalElementType<T> type,
			IAttributeManipulation factory, String prefix) {
		final OperationNode op = new OperationNode();
		try {
			final List<T> elements = new ArrayList<T>();
			UIUtils.addImplicitChildrenOfType(elements, parent, type);

			BigInteger counter = new BigInteger(
					UIUtils.getFreeElementLabelIndex(elements, prefix));
			for (IInternalElement element : parent.getChildrenOfType(type)) {
				op.addCommand(changeAttribute(factory, element, prefix
						+ counter));
				counter = counter.add(BigInteger.ONE);
			}
			for (IRodinElement element : parent.getChildren()) {
				final IInternalElement ie = (IInternalElement) element;
				op.addCommand(renameElement(ie, type, factory, prefix));
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return op;
	}

	public <T extends IInternalElement> OperationCreateElement createElementTwoStringAttribute(
			IInternalElement parent, IInternalElementType<T> typeElement,
			IAttributeType.String type1, IAttributeType.String type2,
			String string1, String string2) {
		final List<IAttributeValue>values = new LinkedList<IAttributeValue>();
		if (string1 != null) {
			values.add(type1.makeValue(string1));
		}
		if (string2 != null) {
			values.add(type2.makeValue(string2));
		}
		final IAttributeValue[] array = values.toArray(new IAttributeValue[values.size()]);
		return getCreateElement(parent, typeElement, null, array);
	}


	public <T extends IInternalElement> OperationNode createElementTwoStringAttribute(
			IInternalElement parent, IInternalElementType<T> typeElement,
			IAttributeType.String type1, IAttributeType.String type2,
			String[] string1, String[] string2) {
		OperationNode op = new OperationNode();
		for (int i = 0; i < string1.length; i++) {
			op.addCommand(createElementTwoStringAttribute(parent, typeElement,
					type1, type2, string1[i], string2[i]));
		}
		return op;
	}

}
