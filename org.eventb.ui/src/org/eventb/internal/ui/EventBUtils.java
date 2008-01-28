/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class contains utility (static) methods for manipulating Event-B
 *         elements.
 *         </p>
 */
public class EventBUtils {

	/**
	 * Gets the abstract file of a Rodin File. This is done by checking the
	 * lists of refines machine clause of the input file. The input is assumed
	 * to be not <code>null</code>.
	 * 
	 * @param concrete
	 *            a Rodin File
	 * @return the abstract file corresponding to the input file. If there are
	 *         no refines machine or there are more than 1 refine machines then
	 *         <code>null</code> is returned. Otherwise, the handle to the
	 *         file corresponding the refine machine will be returned.
	 * @throws RodinDBException
	 *             if there are some problems in reading the refines machine
	 *             clause or in getting the abstract machine.
	 */
	public static IMachineFile getAbstractMachine(IMachineFile concrete)
			throws RodinDBException {
		assert concrete != null;
		IRodinElement[] refines = concrete
				.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
		if (refines.length == 1) {
			IRefinesMachine refine = (IRefinesMachine) refines[0];
			String name = refine.getAbstractMachineName();
			IEventBProject prj = (IEventBProject) concrete.getRodinProject()
					.getAdapter(IEventBProject.class);
			return prj.getMachineFile(name);
		}
		return null;
	}

	/**
	 * Get the first child of an input parent having the specified type and
	 * label.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param label
	 *            the label of the child that we are looking for.
	 * @return the child of the input parent with the input type and having the
	 *         label as the input label.
	 * @throws RodinDBException
	 *             if some problems occur in getting the list of child elements
	 *             or the label attributes of child elements.
	 */
	private static <T extends ILabeledElement> T getFirstChildOfTypeWithLabel(
			IParent parent, IInternalElementType<T> type, String label)
			throws RodinDBException {
		for (T child : parent.getChildrenOfType(type)) {
			if (child.hasAttribute(EventBAttributes.LABEL_ATTRIBUTE)
					&& label.equals(child.getLabel()))
				return child;
		}
		return null;
	}

	/**
	 * Gets the abstract event of an event. This is done by getting the abstract
	 * component and reading the inherited/refines event attribute.
	 * 
	 * @param event
	 *            an input event
	 * @return the abstract event corresponding to the input event or
	 *         <code>null</code>. Returns <code>null</code> in the
	 *         following cases:
	 *         <ul>
	 *         <li>If the abstract machine does not exist.
	 *         <li>If there is no abstract machine corresponding to the file
	 *         contains the machine containing the input event.
	 *         <li>If the event is not inherited and there are no refines event
	 *         attributes.
	 *         <li>If the event is not inherited and there are more than one
	 *         refines event attributes.
	 *         <li>if there is no abstract event corresponding to the refines
	 *         event clause.
	 *         </ul>
	 * @see #getAbstractMachine(IMachineFile)
	 * @throws RodinDBException
	 *             if some problems occur in getting the abstract file or
	 *             reading the inherited/refines event attribute.
	 */
	public static IEvent getAbstractEvent(IEvent event) throws RodinDBException {
		IRodinElement parent = event.getParent();
		assert parent instanceof IMachineFile;

		IMachineFile machine = getAbstractMachine((IMachineFile) parent);

		if (machine == null)
			return null;

		if (!machine.exists())
			return null;

		if (event.isInherited()) {
			return getFirstChildOfTypeWithLabel(machine, IEvent.ELEMENT_TYPE,
					event.getLabel());
		}

		IRefinesEvent[] refinesClauses = event.getRefinesClauses();
		if (refinesClauses.length == 1) {
			return getFirstChildOfTypeWithLabel(machine, IEvent.ELEMENT_TYPE,
					refinesClauses[0].getAbstractEventLabel());
		}
		return null;
	}

	/**
	 * Get the first non inherited abstract event of an input event.
	 * 
	 * @param event
	 *            an event
	 * @return the first non-inherited abstract event of the input event or
	 *         <code>null</code>. Return <code>null</code> in the following
	 *         cases:
	 *         <ul>
	 *         <li>if there is no non-inherited abstract event.
	 *         <li>if there is a loop of abstract events.
	 *         </ul>
	 * @throws RodinDBException
	 *             if some problems occur in getting the abstract events.
	 */
	public static IEvent getNonInheritedAbstractEvent(IEvent event)
			throws RodinDBException {
		Collection<IEvent> events = new ArrayList<IEvent>();
		events.add(event);
		IEvent abstractEvent = getAbstractEvent(event);
		while (abstractEvent != null) {
			if (events.contains(abstractEvent))
				return null;

			if (!abstractEvent.isInherited())
				return abstractEvent;
			events.add(abstractEvent);
			abstractEvent = getAbstractEvent(abstractEvent);
		}

		return null;
	}

	/**
	 * Get a free child name (internal name) for a new child element, given the
	 * parent element, the type of the child element and a proposed prefix for
	 * the name. A new unique name will be the prefix with a index appended to
	 * the end.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param prefix
	 *            the proposed prefix for the child internal name.
	 * @return the new unique name for the child of the input parent which has
	 *         the input type.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	public static <T extends IInternalElement> String getFreeChildName(
			IInternalParent parent, IInternalElementType<T> type, String prefix)
			throws RodinDBException {
		return prefix + getFreeChildNameIndex(parent, type, prefix);
	}

	/**
	 * Get a free index for a new child element, given the parent element, the
	 * type of the child element and a proposed prefix for the name. A new free
	 * index will be the index appended to so that the name by appending the
	 * index to the input prefix is also new.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param prefix
	 *            the proposed prefix for the child internal name.
	 * @return the new free index for the child of the input parent which has
	 *         the input type.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	public static <T extends IInternalElement> int getFreeChildNameIndex(
			IInternalParent parent, IInternalElementType<T> type, String prefix)
			throws RodinDBException {
		return getFreeChildNameIndex(parent, type, prefix, 1);
	}

	/**
	 * Get a free index for a new child element, given the parent element, the
	 * type of the child element and a proposed prefix for the name. A new free
	 * index will be the index appended to so that the name by appending the
	 * index to the input prefix is also new. The index will be the smallest
	 * available index starting from the input beginIndex.
	 * 
	 * @param <T>
	 *            an internal element class (i.e. extends
	 *            {@link IInternalElement}.
	 * @param parent
	 *            the internal parent ({@link IInternalParent}).
	 * @param type
	 *            the type of the child ({@link IInternalElementType}).
	 * @param prefix
	 *            the proposed prefix for the child internal name.
	 * @param beginIndex
	 *            the starting index for searching.
	 * @return the new free index for the child of the input parent which has
	 *         the input type.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	public static <T extends IInternalElement> int getFreeChildNameIndex(
			IInternalParent parent, IInternalElementType<T> type,
			String prefix, int beginIndex) throws RodinDBException {
		T[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			T element = parent.getInternalElement(type, prefix + i);
			if (!element.exists())
				break;
		}
		return i;
	}

}
