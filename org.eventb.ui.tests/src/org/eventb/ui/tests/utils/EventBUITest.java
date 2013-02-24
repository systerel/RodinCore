/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added methods for creating elements
 *     Systerel - replaced inherited by extended, added tool configuration
 *     Systerel - separation of file and root element
 *     Systerel - disabled database indexer in setup
 *     Systerel - port to JUnit 4
 *******************************************************************************/
package org.eventb.ui.tests.utils;

import static org.eventb.core.IConfigurationElement.DEFAULT_CONFIGURATION;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.debug.DebugHelpers;

/**
 * @author htson
 *         <p>
 *         Abstract class for Event-B UI tests. This is the simplification of
 *         the builder tests by Laurent Voisin.
 */
public abstract class EventBUITest {
	
	public static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * The pointer to the test Rodin project.
	 */
	protected IRodinProject rodinProject;

	/**
	 * The testing workspace. 
	 */
	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

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
	protected IContextRoot createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IRodinFile file = rodinProject.getRodinFile(fileName);
		file.create(true, null);
		IContextRoot result = (IContextRoot) file.getRoot();
		result.setConfiguration(DEFAULT_CONFIGURATION, null);
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
	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		final IRodinFile file = rodinProject.getRodinFile(fileName);
		file.create(true, null);
		final IMachineRoot result = (IMachineRoot) file.getRoot();
		result.setConfiguration(DEFAULT_CONFIGURATION, null);
		return result;
	}
	
	/**
	 * Utility method to create a new refines machine clause for a machine.
	 * 
	 * @param machine
	 *            the input machine {@link IMachineRoot}.
	 * @param abstractMachineName
	 *            the name of the abstract machine.
	 * @return the newly created refines machine clause
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IRefinesMachine createRefinesMachineClause(IMachineRoot machine,
			String abstractMachineName) throws RodinDBException {
		IRefinesMachine refinesClause = machine.createChild(
				IRefinesMachine.ELEMENT_TYPE, null, null);
		refinesClause.setAbstractMachineName(abstractMachineName,
				null);
		return refinesClause;
	}
	
	
	
	/**
	 * Utility method to create a new sees context clause for a machine.
	 * 
	 * @param machine
	 *            the input machine {@link IMachineRoot}.
	 * @param contextName
	 *            the name of the context.
	 * @return the newly created sees context clause
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected ISeesContext createSeesContextClause(IMachineRoot machine,
			String contextName) throws RodinDBException {
		ISeesContext seesClause = machine.createChild(
				ISeesContext.ELEMENT_TYPE, null, null);
		seesClause.setSeenContextName(contextName, null);
		return seesClause;
	}
	
	
	
	/**
	 * Utility method to create a new extends context clause for a context.
	 * 
	 * @param context
	 *            the input context {@link IMachineRoot}.
	 * @param contextName
	 *            the name of the extended context.
	 * @return the newly created extends context clause
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IExtendsContext createExtendsContextClause(IContextRoot context,
			String contextName) throws RodinDBException {
		IExtendsContext extendsClause = context.createChild(
				IExtendsContext.ELEMENT_TYPE, null, null);
		extendsClause.setAbstractContextName(contextName, null);
		return extendsClause;
	}
	
	/**
	 * Utility method to create a new event which belong to a machine with the
	 * given label. The new event is non-extended.
	 * 
	 * @param machine
	 *            a machine root.
	 * @param eventLabel
	 *            the label of the new event.
	 * @return the newly created event.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IEvent createEvent(IMachineRoot machine, String eventLabel)
			throws RodinDBException {
		IEvent event = machine.createChild(IEvent.ELEMENT_TYPE, null, null);
		event.setLabel(eventLabel, null);
		event.setExtended(false, null);
		event.setConvergence(ORDINARY, null);
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
		IRefinesEvent refinesClause = event.createChild(IRefinesEvent.ELEMENT_TYPE, null, null);
		refinesClause.setAbstractEventLabel(abstractEventLabel, null);
		return refinesClause;
	}
	
	/**
	 * Creates a parameter of an event.
	 * 
	 * @param event
	 *            an event
	 * @param ident
	 *            the parameter identifier
	 * @return the newly created parameter
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	protected IParameter createParameter(IEvent event, String ident)
			throws RodinDBException {
		IParameter parameter = event.createChild(IParameter.ELEMENT_TYPE, null, null);
		parameter.setIdentifierString(ident, null);
		return parameter;
	}

	/**
	 * Creates a guard of an event.
	 * 
	 * @param event
	 *            an event.
	 * @param label
	 *            the label of the guard
	 * @param pred
	 *            the predicate of the guard
	 * @return the newly created guard
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	protected IGuard createGuard(IEvent event, String label, String pred)
			throws RodinDBException {
		IGuard guard = event.createChild(IGuard.ELEMENT_TYPE, null, null);
		guard.setLabel(label, null);
		guard.setPredicateString(pred, null);
		guard.setTheorem(false, null);
		return guard;
	}

	/**
	 * Creates a witness of an event.
	 * 
	 * @param event
	 *            an event.
	 * @param label
	 *            the label of the witness
	 * @param pred
	 *            the predicate of the witness
	 * @return the newly created witness
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	protected IWitness createWitness(IEvent event, String label, String pred)
			throws RodinDBException {
		IWitness witness = event.createChild(IWitness.ELEMENT_TYPE, null, null);
		witness.setLabel(label, null);
		witness.setPredicateString(pred, null);
		return witness;
	}

	/**
	 * Utility method for creating an action of an event.
	 * 
	 * @param event
	 *            an event
	 * @param label
	 *            the label of the action
	 * @param assign
	 *            the assignment of the action
	 * @return the newly created action
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	protected IAction createAction(IEvent event, String label, String assign)
			throws RodinDBException {
		IAction action = event.createChild(IAction.ELEMENT_TYPE, null, null);
		action.setLabel(label, null);
		action.setAssignmentString(assign, null);
		return action;
	}

	/**
	 * Utility method for creating a variant of a machine.
	 * 
	 * @param mch
	 *            a machine root
	 * @param expression
	 *            the expression of the variant
	 * @return the newly created variant
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	protected IVariant createVariant(IMachineRoot mch, String expression)
			throws RodinDBException {
		IVariant variant = mch.createChild(IVariant.ELEMENT_TYPE, null, null);
		variant.setExpressionString(expression, null);
		return variant;
	}

	/**
	 * Utility method for creating an invariant of a machine.
	 * 
	 * @param mch
	 *            a machine root
	 * @param label
	 *            the label of the invariant
	 * @param predicate
	 *            the predicate of the invariant
	 * @param theorem
	 *            true if the invariant is a theorem
	 * @return the newly created invariant
	 * @throws RodinDBException
	 *             if some problems occurs
	 */
	protected IInvariant createInvariant(IMachineRoot mch, String label,
			String predicate, boolean theorem) throws RodinDBException {
		IInvariant invariant = mch.createChild(IInvariant.ELEMENT_TYPE, null, null);
		invariant.setLabel(label, null);
		invariant.setPredicateString(predicate, null);
		invariant.setTheorem(theorem, null);
		return invariant;
	}
	
	/**
	 * Utility method to create an axiom of a context.
	 * 
	 * @param ctx
	 *            a context root
	 * @param label
	 *            the label of the axiom
	 * @param predicate
	 *            the predicate of the axiom
	 * @param theorem
	 *            true iff the axiom is a theorem
	 * @return the newly created axiom
	 * @throws RodinDBException
	 *             if some problems occur
	 */
	protected IAxiom createAxiom(IContextRoot ctx, String label,
			String predicate, boolean theorem) throws RodinDBException {
		IAxiom axiom = ctx.createChild(IAxiom.ELEMENT_TYPE, null, null);
		axiom.setLabel(label, null);
		axiom.setPredicateString(predicate, null);
		axiom.setTheorem(theorem, null);

		return axiom;
	}

	@Before
	public void setUp() throws Exception {
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		// disable indexing
		DebugHelpers.disableIndexing();
		
		// Create a new project
		rodinProject = createRodinProject("P"); //$NON-NLS-1$
	}

	protected IRodinProject createRodinProject(String name) throws CoreException {
		IProject project = workspace.getRoot().getProject(name);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		final IRodinProject rodinPrj = RodinCore.valueOf(project);
		assertNotNull(rodinPrj);
		return rodinPrj;
	}
	
	@After
	public void tearDown() throws Exception {
		EventBUIPlugin.getActivePage().closeAllEditors(false);
		workspace.getRoot().delete(true, null);
	}

	/**
	 * Open the an Event-B Editor ({@link IEventBEditor}) for a given
	 * component. Assuming that the component is either a machine ({@link IMachineRoot})
	 * or a context ({@link IContextRoot}).
	 * 
	 * @param component
	 *            the input component
	 * @return the Event-B Editor for the input component.
	 * @throws PartInitException
	 *             if some problems occur when open the editor.
	 */
	protected IEventBEditor<?> openEditor(IEventBRoot component)
			throws PartInitException {
		
		IEditorInput fileInput = new FileEditorInput(component.getResource());
		String editorId = ""; //$NON-NLS-1$
		if (component instanceof IMachineRoot) {
			editorId = EventBMachineEditor.EDITOR_ID;
		} else if (component instanceof IContextRoot) {
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
	 * @return			the created element
	 * @throws RodinDBException
	 */
	protected <T extends IInternalElement> T createInternalElement(
			IInternalElement parent, IInternalElementType<T> childType) throws RodinDBException {

		return parent.createChild(childType, null, null);
	}

	protected void createNAxioms(IInternalElement parent,
			String elementAttributePrefix, long n, long beginIndex) throws RodinDBException {

		for (long i = beginIndex; i < beginIndex + n; i++) {
			final IAxiom newAxiom = createInternalElement(parent,
					IAxiom.ELEMENT_TYPE);
			newAxiom.setLabel(elementAttributePrefix + i, null);
		}
	}

	protected void createNEvents(IMachineRoot machine,
			String eventLabelPrefix, long n, long beginIndex) throws RodinDBException {

		for (long i = beginIndex; i < beginIndex + n; i++) {
			createEvent(machine, eventLabelPrefix + i);
		}
	}

	/**
	 * Utility method to get a handle to a machine root with the given name. The
	 * machine is located in the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param name
	 *            the bare name (without any file extension) of the machine
	 * @return a handle to a machine root with the given name
	 */
	protected IMachineRoot getMachineRoot(String name) {
		final String fileName = EventBPlugin.getMachineFileName(name);
		final IRodinFile file = rodinProject.getRodinFile(fileName);
		return (IMachineRoot) file.getRoot();
	}
	
	/**
	 * Utility method to get a handle to a context root with the given name. The
	 * context is located in the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param name
	 *            the bare name (without any file extension) of the context
	 * @return a handle to a context root with the given name
	 */
	protected IContextRoot getContextRoot(String name) {
		final String fileName = EventBPlugin.getContextFileName(name);
		final IRodinFile file = rodinProject.getRodinFile(fileName);
		return (IContextRoot) file.getRoot();
	}

}
