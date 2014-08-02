/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * The Model is used to present the structure of the machines and contexts and
 * the proof obligations. The ModelController controls the model (e.g. updates
 * it, when the database changes) and grants access to its element such as
 * Projects, Machines, Invariants etc.
 * 
 */
public class ModelController implements IElementChangedListener {
	
	
	private static HashMap<IRodinProject, ModelProject> projects = new HashMap<IRodinProject, ModelProject>();
	private static ModelController INSTANCE = new ModelController();

	private final ListenerList listeners = new ListenerList();

	private ModelController() {
		RodinCore.addElementChangedListener(this);
	}

	/**
	 * Returns the unique instance of the model controller.
	 */
	public static ModelController getInstance() {
		return INSTANCE;
	}

	/**
	 * Processes a RodinProject. Creates a model for this project (Machines,
	 * Contexts, Invariants etc.). Proof Obligations are not included in
	 * processing.
	 * 
	 * @param project
	 *            The Project to process.
	 */
	public static void processProject(IRodinProject project){
		getProcessedProject(project);
	}

	/**
	 * Returns a processed RodinProject. Creates a new model for this project
	 * (Machines, Contexts, Invariants, etc.) if none is already registered.
	 * Proof Obligations are not included in processing.
	 * 
	 * @param project
	 *            The Project to process
	 * @return the processed project
	 */
	public static ModelProject getProcessedProject(IRodinProject project) {
		final ModelProject prj = getRegisteredModelProject(project);
		return processProject(project, prj);
	}

	private static ModelProject processProject(IRodinProject project,
			ModelProject prj) {
		// only process if really needed
		if (prj.needsProcessing) {
			try {
				final IContextRoot[] contexts = ExplorerUtils
						.getContextRootChildren(project);
				for (IContextRoot context : contexts) {
					prj.processContext(context);
				}
				prj.calculateContextBranches();
				final IMachineRoot[] machines = ExplorerUtils
						.getMachineRootChildren(project);
				for (IMachineRoot machine : machines) {
					prj.processMachine(machine);
				}
				prj.calculateMachineBranches();
				prj.needsProcessing = false;
			} catch (RodinDBException e) {
				UIUtils.log(e, "when processing project " + project);
			}
		}
		return prj;
	}

	private static ModelProject getRegisteredModelProject(IRodinProject project) {
		if (!projects.containsKey(project)) {
			final ModelProject prj =  new ModelProject(project);
			projects.put(project, prj);
			return prj;
		}	
		return projects.get(project);
	}
	
	
	/**
	 * Gets the ModelInvariant for a given Invariant
	 * 
	 * @param invariant
	 *            The Invariant to look for
	 * @return The corresponding ModelInvariant, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelInvariant getInvariant(IInvariant invariant){
		ModelProject project = projects.get(invariant.getRodinProject());
		if (project != null) {
			return project.getInvariant(invariant);
		}
		return null;
	}
	
	/**
	 * Gets the ModelEvent for a given Event
	 * 
	 * @param event
	 *            The Event to look for
	 * @return The corresponding ModelEvent, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelEvent getEvent(IEvent event){
		ModelProject project = projects.get(event.getRodinProject());
		if (project != null) {
			return project.getEvent(event);
		}
		return null;
	}
	
	/**
	 * Gets the ModelAxiom for a given Axiom
	 * 
	 * @param axiom
	 *            The Axiom to look for
	 * @return The corresponding ModelAxiom, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelAxiom getAxiom(IAxiom axiom){
		ModelProject project = projects.get(axiom.getRodinProject());
		if (project != null) {
			return project.getAxiom(axiom);
		}
		return null;
	}

	/**
	 * Gets the ModelMachine for a given MachineRoot
	 * 
	 * @param machineRoot
	 *            The MachineRoot to look for
	 * @return The corresponding ModelMachine, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelMachine getMachine(IMachineRoot machineRoot){
		ModelProject project = projects.get(machineRoot.getRodinProject());
		if (project != null) {
				return project.getMachine(machineRoot);
		}
		return null;
	}
	
	/**
	 * Removes a ModelMachine from the Model for a given MachineRoot. Also
	 * removes dependencies
	 * 
	 * @param machineRoot
	 *            The MachineRoot to look for
	 */
	public static void removeMachine(IMachineRoot machineRoot){
		ModelProject project = projects.get(machineRoot.getRodinProject());
		if (project != null ) {
				project.removeMachine(machineRoot);
		}
	}
	

	/**
	 * Gets the ModelProofObligation for a given IPSStatus
	 * 
	 * @param status
	 *            The IPSStatus to look for
	 * @return The corresponding ModelProofObligation, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelProofObligation getModelPO(IPSStatus status){
		ModelProject project = projects.get(status.getRodinProject());
		if (project != null) {
				return project.getProofObligation(status);
		}
		return null;
	}
	
	
	/**
	 * Gets the ModelContext for a given ContextRoot
	 * 
	 * @param contextRoot
	 *            The ContextRoot to look for
	 * @return The corresponding ModelContext, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelContext getContext(IContextRoot contextRoot){
		ModelProject project = projects.get(contextRoot.getRodinProject());
		if (project != null) {
				return project.getContext(contextRoot);
		}
		return null;
	}
	
	public static IModelElement getModelElement(Object element){
		if (element instanceof IModelElement) {
			return (IModelElement) element;
		}
		if (element instanceof IRodinProject) {
			return projects.get(element);
		}
		if (element instanceof IRodinElement) {
			ModelProject project = projects.get(((IRodinElement)element).getRodinProject());
			if (project != null) {
				return project.getModelElement((IRodinElement)element);
			}
		}
		if (ExplorerUtils.DEBUG) {
			System.out.println("Element not found by ModelController: " +element);
		}
		return null;
	}
	
	/**
	 * Removes a ModelContext from the Model for a given ContextRoot
	 * 
	 * @param contextRoot
	 *            The ContextRoot to remove
	 */
	public static void removeContext(IContextRoot contextRoot){
		ModelProject project = projects.get(contextRoot.getRodinProject());
		if (project != null) {
				project.removeContext(contextRoot);
		}
	}
	/**
	 * Gets the ModelProject for a given RodinProject
	 * 
	 * @param project
	 *            The RodinProjecct to look for
	 * @return The corresponding ModelProject, if there exists one,
	 *         <code>null</code> otherwise
	 */
	public static ModelProject getProject(IRodinProject project) {
		return projects.get(project);
	}
	
	/**
	 * Removes the corresponding ModelProject from the Model if it was present.
	 * 
	 * @param project
	 *            The Project to remove.
	 */
	public static void removeProject(IRodinProject project){
		projects.remove(project);
	}
	
	/**
	 * Gets the corresponding IMachineRoots for a set of ModelMachines
	 * 
	 * @param machs
	 *            The ModelMachines to convert
	 * @return The corresponding IMachineRoots
	 */
	public static IMachineRoot[] convertToIMachine(ModelMachine[] machs) {
		IMachineRoot[] results = new IMachineRoot[machs.length];
		for (int i = 0; i < machs.length; i++) {
			results[i] = machs[i].getInternalMachine();
			
		}
		return results;
	}
	
	/**
	 * Gets the corresponding IMachineRoots for a set of ModelMachines
	 * 
	 * @param machs
	 *            The ModelMachines to convert
	 * @return The corresponding IMachineRoots
	 */
	public static List<IMachineRoot> convertToIMachine(List<ModelMachine> machs) {
		List<IMachineRoot> results = new LinkedList<IMachineRoot>();
		for (ModelMachine mach : machs) {
			 results.add(mach.getInternalMachine());
		}
		return results;
	}

	/**
	 * Gets the corresponding IContextRoots for a set of ModelContexts
	 * 
	 * @param conts
	 *            The ModelContexts to convert
	 * @return The corresponding IContextRoots
	 */
	public static IContextRoot[] convertToIContext(ModelContext[] conts) {
		IContextRoot[] results = new IContextRoot[conts.length];
		for (int i = 0; i < conts.length; i++) {
			results[i] = conts[i].getInternalContext();
			
		}
		return results;
	}

	/**
	 * Gets the corresponding IContextRoots for a set of ModelContexts
	 * 
	 * @param conts
	 *            The ModelContexts to convert
	 * @return The corresponding IContextRoots
	 */
	public static List<IContextRoot> convertToIContext(List<ModelContext> conts) {
		List<IContextRoot> results = new LinkedList<IContextRoot>();
		for (ModelContext ctx : conts) {
			 results.add(ctx.getInternalContext());
		}
		return results;
	}
	
	/**
	 * React to changes in the database.
	 *
	 * The actual changes to the model must be executed within the UI thread to
	 * avoid any concurrency issue.
	 */
	@Override
	public void elementChanged(ElementChangedEvent event) {
		final DeltaProcessor processor = new DeltaProcessor(event.getDelta());
		final ArrayList<IRodinElement> toRefresh = processor.getToRefresh();
		final ArrayList<IRodinElement> toRemove = processor.getToRemove();
		final Runnable operation = new Runnable() {
			@Override
			@SuppressWarnings("synthetic-access")
			public void run() {
				cleanUpModel(toRemove);
				// refresh the model
				for (final IRodinElement elem : toRefresh) {
					refreshModel(elem);
				}
				notifyListeners(toRefresh);
			}
		};
		final Display display = PlatformUI.getWorkbench().getDisplay();
		if (Thread.currentThread() == display.getThread()) {
			operation.run();
		} else {
			display.asyncExec(operation);
		}
	}
	
	/**
	 * Refreshes explorer model for all Rodin projects.
	 */
	public void refreshModel() {
		try {
			final IRodinProject[] rProjects = RodinCore.getRodinDB().getRodinProjects();
			for (IRodinProject project : rProjects) {
				refreshModel(project);
			}
			notifyListeners(Arrays.<IRodinElement>asList(rProjects));
		} catch (RodinDBException e) {
			ExplorerUtils.log(e, "while refreshing explorer model");
		}
	}
	
	/**
	 * Refreshes the model
	 * 
	 * @param element
	 *            The element to refresh
	 */
	private void refreshModel(IRodinElement element) {
		if (!(element instanceof IRodinDB)) {
			ModelProject project = projects.get(element.getRodinProject());
			if (project != null) {
				if (element instanceof IRodinProject) {
					project.needsProcessing = true;
					processProject((IRodinProject)element);
				}
				
				if (element instanceof IMachineRoot) {
					project.processMachine((IMachineRoot)element);
				}
				if (element instanceof IContextRoot) {
					project.processContext((IContextRoot)element);
				}
				if (element instanceof IPORoot) {
					IPORoot root = (IPORoot) element;
					//get corresponding machine or context
					if (root.getMachineRoot().exists()) {
						ModelMachine machine = getMachine(root.getMachineRoot());
						machine.poNeedsProcessing = true;
						machine.processPORoot();
						//process the statuses as well
						machine.psNeedsProcessing = true;
						machine.processPSRoot();
					}
					if (root.getContextRoot().exists()) {
						ModelContext context = getContext(root.getContextRoot());
						context.poNeedsProcessing = true;
						context.processPORoot();
						//process the statuses as well
						context.psNeedsProcessing = true;
						context.processPSRoot();
					}
				}
				if (element instanceof IPSRoot) {
					IPSRoot root = (IPSRoot) element;
					//get corresponding machine or context
					if (root.getMachineRoot().exists()) {
						ModelMachine machine = getMachine(root.getMachineRoot());
						machine.psNeedsProcessing = true;
						machine.processPSRoot();
					}
					if (root.getContextRoot().exists()) {
						ModelContext context = getContext(root.getContextRoot());
						context.psNeedsProcessing = true;
						context.processPSRoot();
					}
				}
				
			}
		}
	}
	
	/**
	 * Removes the corresponding elements from the model
	 * @param toRemove
	 */
	private void cleanUpModel(ArrayList<IRodinElement> toRemove){
		for (IRodinElement element : toRemove) {
			if (element instanceof IContextRoot) {
				removeContext((IContextRoot) element);
			}
			if (element instanceof IMachineRoot) {
				removeMachine((IMachineRoot) element);
			}
			if (element instanceof IRodinProject) {
				removeProject((IRodinProject) element);
			}
		}
	}
	
	public void addListener(IModelListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IModelListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners(List<IRodinElement> toRefresh) {
		for (Object listener : listeners.getListeners()) {
			safeNotify((IModelListener) listener, toRefresh);
		}
	}

	private void safeNotify(IModelListener listener,
			List<IRodinElement> toRefresh) {
		try {
			listener.refresh(toRefresh);
		} catch (Throwable e) {
			// TODO log properly exception in listener
			e.printStackTrace();
		}
	}
}
