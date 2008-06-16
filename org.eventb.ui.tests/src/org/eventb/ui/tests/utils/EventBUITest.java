/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added methods for creating elements
 *******************************************************************************/

package org.eventb.ui.tests.utils;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         Abstract class for Event-B UI tests. This is the simplification of
 *         the builder tests by Laurent Voisin.
 */
public abstract class EventBUITest extends TestCase {
	

	/**
	 * The pointer to the test Rodin project.
	 */
	protected IRodinProject rodinProject;

	/**
	 * The testing workspace. 
	 */
	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	/**
	 * Constructor: Create a test case.
	 */
	public EventBUITest() {
		super();
	}
	
	/**
	 * Constructor: Create a test case with the given name.
	 * 
	 * @param name
	 *            the name of test
	 */
	public EventBUITest(String name) {
		super(name);
	}
	
	/**
	 * Utility method to create a context with the given bare name. The context
	 * is created as a child of the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param bareName
	 *            the bare name (without the extension .ctx) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IContextFile createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IContextFile result = (IContextFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	/**
	 * Utility method to create a machine with the given bare name. The machine
	 * is created as a child of the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param bareName
	 *            the bare name (without the extension .mch) of the machine
	 * @return the newly created machine.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IMachineFile createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IMachineFile result = (IMachineFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}
	
	/**
	 * Utility method to create a new refines machine clause for a machine.
	 * 
	 * @param machine
	 *            the input machine {@link IMachineFile}.
	 * @param abstractMachineName
	 *            the name of the abstract machine.
	 * @return the newly created refines machine clause
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IRefinesMachine createRefinesMachineClause(IMachineFile machine,
			String abstractMachineName) throws RodinDBException {
		String childName = EventBUtils.getFreeChildName(machine,
				IRefinesMachine.ELEMENT_TYPE, "refines_machine"); //$NON-NLS-1$
		IRefinesMachine refinesClause = machine.getRefinesClause(childName);
		refinesClause.create(null, null);
		refinesClause.setAbstractMachineName(abstractMachineName,
				null);
		return refinesClause;
	}
	
	/**
	 * Utility method to create a new event which belong to a machine with the
	 * given label. The new event is non-inherited.
	 * 
	 * @param machine
	 *            a machine file.
	 * @param eventLabel
	 *            the label of the new event.
	 * @return the newly created event.
	 * @throws RodinDBException
	 *             if some problems occur.
	 * @see #createEvent(IMachineFile, String, String)
	 */
	protected IEvent createEvent(IMachineFile machine, String eventLabel)
			throws RodinDBException {
		String childName = EventBUtils.getFreeChildName(machine,
				IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		return createEvent(machine, childName, eventLabel);
	}
	
	/**
	 * Utility method to create a new event which belong to a machine with the
	 * given label. The new event is non-inherited. The internal name of the
	 * event is also specified.
	 * 
	 * @param machine
	 *            a machine file.
	 * @param eventLabel
	 *            the label of the new event.
	 * @return the newly created event.
	 * @throws RodinDBException
	 *             if some problems occur.
	 * @see #createEvent(IMachineFile, String)
	 */
	protected IEvent createEvent(IMachineFile machine, String internalName,
			String eventLabel) throws RodinDBException {
		IEvent event = machine.getEvent(internalName);
		event.create(null, null);
		event.setLabel(eventLabel, null);
		event.setInherited(false, null);
		return event;
	}

	/**
	 * Utility method for creating the refines event clause of an event having
	 * the abstract event label specified by the input.
	 * 
	 * @param event
	 *            an event.
	 * @param abstractEventLabel
	 *            the abstract event label.
	 * @return the newly created refines event clause.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IRefinesEvent createRefinesEventClause(IEvent event,
			String abstractEventLabel) throws RodinDBException {
		String childName = EventBUtils.getFreeChildName(event,
				IRefinesEvent.ELEMENT_TYPE, "refines_event"); //$NON-NLS-1$
		IRefinesEvent refinesClause = event.getRefinesClause(childName);
		refinesClause.create(null, null);
		refinesClause.setAbstractEventLabel(abstractEventLabel, null);
		return refinesClause;
	}
	
	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		// Create a new project
		IProject project = workspace.getRoot().getProject("P"); //$NON-NLS-1$
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		rodinProject = RodinCore.valueOf(project);
	}
	
	@After
	@Override
	protected void tearDown() throws Exception {
		EventBUIPlugin.getActivePage().closeAllEditors(false);
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

	/**
	 * Open the an Event-B Editor ({@link IEventBEditor}) for a given
	 * component. Assuming that the component is either a machine ({@link IMachineFile})
	 * or a context ({@link IContextFile}).
	 * 
	 * @param component
	 *            the input component
	 * @return the Event-B Editor for the input component.
	 * @throws PartInitException
	 *             if some problems occur when open the editor.
	 */
	protected IEventBEditor<?> openEditor(IRodinFile component)
			throws PartInitException {
		IEditorInput fileInput = new FileEditorInput(component.getResource());
		String editorId = ""; //$NON-NLS-1$
		if (component instanceof IMachineFile) {
			editorId = EventBMachineEditor.EDITOR_ID;
		} else if (component instanceof IContextFile) {
			editorId = EventBContextEditor.EDITOR_ID;
		}
		return (IEventBEditor<?>) EventBUIPlugin.getActivePage().openEditor(
				fileInput, editorId);
	}

	/**
	 * Method to create an internal element
	 * 
	 * @param <T>		the type of internal element to create
	 * @param parent	the parent of the element to create
	 * @param childType	the type of the child to create
	 * @param childName	the name of the element to create
	 * @return			the created element
	 * @throws RodinDBException
	 */
	protected <T extends IInternalElement> T createInternalElement(
			IInternalParent parent, IInternalElementType<T> childType,
			String childName) throws RodinDBException {

		T element = parent.getInternalElement(childType, childName);
		element.create(null, null);
		return element;
	}

	protected void createNAxioms(IInternalParent parent,
			String childNamePrefix, String elementAttributePrefix, long n,
			long beginIndex) throws RodinDBException {

		for (long i = beginIndex; i < beginIndex + n; i++) {
			final IAxiom newAxiom = createInternalElement(parent,
					IAxiom.ELEMENT_TYPE, childNamePrefix + i);
			newAxiom.setLabel(elementAttributePrefix + i, null);
		}
	}

	protected void createNEvents(IMachineFile machine,
			String internalNamePrefix, String eventLabelPrefix, long n,
			long beginIndex) throws RodinDBException {

		for (long i = beginIndex; i < beginIndex + n; i++) {
			createEvent(machine, internalNamePrefix + i, eventLabelPrefix + i);
		}
	}

}
