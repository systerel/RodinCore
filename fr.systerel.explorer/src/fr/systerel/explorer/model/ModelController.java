/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.navigator.RodinNavigator;

/**
 * The Model is used to present the structure of the machines and contexts 
 * and the proof obligations.
 * The ModelController controls the model (e.g. updates it, when the database changes)
 * and grants access to its element such as Projects, Machines, Invariants etc.
 *
 */
public class ModelController implements IElementChangedListener {
	
	
	private static HashMap<String, ModelProject> projects = new HashMap<String, ModelProject>();
	RodinNavigator navigator;

	/**
	 * Create this controller and register it in the DataBase for changes.
	 * @param navigator
	 */
	public ModelController(RodinNavigator navigator){
		RodinCore.addElementChangedListener(this);
		this.navigator = navigator;
	}
	
	/**
	 * Processes a RodinProject. Creates a model for this project (Machines, Contexts, Invariants etc.).
	 * Proof Obligations are not included in processing.
	 * 
	 * @param project The Project to process.
	 */
	public static void processProject(IRodinProject project){
		try {
//			System.out.println("Processing project " +project.getElementName());
			ModelProject prj;
			if (!projects.containsKey(project.getHandleIdentifier())) {
				prj =  new ModelProject(project);
				projects.put(project.getHandleIdentifier(), prj);
			}	
			prj =  projects.get(project.getHandleIdentifier());

			IContextFile[] contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
			for (int i = 0; i < contexts.length; i++) {
				prj.processContext(contexts[i]);
			}
			prj.calculateContextBranches();
			IMachineFile[] machines = project.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
			for (int i = 0; i < machines.length; i++) {
				prj.processMachine(machines[i]);
			}
			prj.calculateMachineBranches();
//			System.out.println("Processing project done " +System.currentTimeMillis());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Gets the ModelInvariant for a given Invariant
	 * @param invariant	The Invariant to look for
	 * @return	The corresponding ModelInvariant, if there exists one, <code>null</code> otherwise
	 */
	public static ModelInvariant getInvariant(IInvariant invariant){
		ModelProject project = projects.get(invariant.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getInvariant(invariant);
		}
		return null;
	}
	
	/**
	 * Gets the ModelEvent for a given Event
	 * @param event	The Event to look for
	 * @return	The corresponding ModelEvent, if there exists one, <code>null</code> otherwise
	 */
	public static ModelEvent getEvent(IEvent event){
		ModelProject project = projects.get(event.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getEvent(event);
		}
		return null;
	}
	
	/**
	 * Gets the ModelAxiom for a given Axiom
	 * @param axiom	The Axiom to look for
	 * @return	The corresponding ModelAxiom, if there exists one, <code>null</code> otherwise
	 */
	public static ModelAxiom getAxiom(IAxiom axiom){
		ModelProject project = projects.get(axiom.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getAxiom(axiom);
		}
		return null;
	}

	/**
	 * Gets the ModelTheorem for a given Theorem
	 * @param theorem	The Theorem to look for
	 * @return	The corresponding ModelTheorem, if there exists one, <code>null</code> otherwise
	 */
	public static ModelTheorem getTheorem(ITheorem theorem){
		ModelProject project = projects.get(theorem.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getTheorem(theorem);
		}
		return null;
	}
	
	/**
	 * Gets the ModelMachine for a given MachineFile
	 * @param machineFile	The  MachineFile to look for
	 * @return	The corresponding ModelMachine, if there exists one, <code>null</code> otherwise
	 */
	public static ModelMachine getMachine(IMachineFile machineFile){
		ModelProject project = projects.get(machineFile.getRodinProject().getHandleIdentifier());
		if (project != null) {
				return project.getMachine(machineFile.getBareName());
		}
		return null;
	}
	
	/**
	 * Removes a ModelMachine from the Model for a given MachineFile. Also removes dependencies
	 * @param machineFile	The  MachineFile to look for
	 */
	public static void removeMachine(IMachineFile machineFile){
		ModelProject project = projects.get(machineFile.getRodinProject().getHandleIdentifier());
		if (project != null ) {
				project.removeMachine(machineFile.getBareName());
		}
	}
	

	/**
	 * Gets the ModelProofObligation for a given IPSStatus
	 * @param status	The  IPSStatus to look for
	 * @return	The corresponding ModelProofObligation, if there exists one, <code>null</code> otherwise
	 */
	public static ModelProofObligation getModelPO(IPSStatus status){
		ModelProject project = projects.get(status.getRodinProject().getHandleIdentifier());
		if (project != null) {
				return project.getProofObligation(status);
		}
		return null;
	}
	
	
	/**
	 * Gets the ModelContext for a given ContextFile
	 * @param contextFile	The ContextFile to look for
	 * @return	The corresponding ModelContext, if there exists one, <code>null</code> otherwise
	 */
	public static ModelContext getContext(IContextFile contextFile){
		ModelProject project = projects.get(contextFile.getRodinProject().getHandleIdentifier());
		if (project != null) {
				return project.getContext(contextFile.getBareName());
		}
		return null;
	}
	
	/**
	 * Removes a ModelContext from the Model for a given ContextFile
	 * @param contextFile	The  ContextFile to remove
	 */
	public static void removeContext(IContextFile contextFile){
		ModelProject project = projects.get(contextFile.getRodinProject().getHandleIdentifier());
		if (project != null) {
				project.removeContext(contextFile.getBareName());
		}
	}
	/**
	 * Gets the ModelProject for a given RodinProject
	 * @param project	The RodinProjecct to look for
	 * @return	The corresponding ModelProject, if there exists one, <code>null</code> otherwise
	 */
	public static ModelProject getProject(IRodinProject project) {
		return projects.get(project.getHandleIdentifier());
	}
	
	/**
	 * Removes the corresponding ModelProject from the Model
	 * if it was present.
	 * @param project	The Project to remove.
	 */
	public static void removeProject(IRodinProject project){
		projects.remove(project.getHandleIdentifier());
	}
	
	/**
	 * Gets the corresponding IMachineFiles for a set of ModelMachines
	 * @param machs	The ModelMachines to convert	
	 * @return	The corresponding IMachineFiles
	 */
	public static IMachineFile[] convertToIMachine(ModelMachine[] machs) {
		IMachineFile[] results = new IMachineFile[machs.length];
		for (int i = 0; i < machs.length; i++) {
			results[i] = machs[i].getInternalMachine();
			
		}
		return results;
	}
	
	/**
	 * Gets the corresponding IMachineFiles for a set of ModelMachines
	 * @param machs	The ModelMachines to convert	
	 * @return	The corresponding IMachineFiles
	 */
	public static List<IMachineFile> convertToIMachine(List<ModelMachine> machs) {
		List<IMachineFile> results = new LinkedList<IMachineFile>();
		for (Iterator<ModelMachine> iterator = machs.iterator(); iterator.hasNext();) {
			 results.add(iterator.next().getInternalMachine());
		}
		return results;
	}

	/**
	 * Gets the corresponding IContextFiles for a set of ModelContexts
	 * @param conts	The ModelContexts to convert	
	 * @return	The corresponding IContextFiles
	 */
	public static IContextFile[] convertToIContext(ModelContext[] conts) {
		IContextFile[] results = new IContextFile[conts.length];
		for (int i = 0; i < conts.length; i++) {
			results[i] = conts[i].getInternalContext();
			
		}
		return results;
	}

	/**
	 * Gets the corresponding IContextFiles for a set of ModelContexts
	 * @param conts	The ModelContexts to convert	
	 * @return	The corresponding IContextFiles
	 */
	public static List<IContextFile> convertToIContext(List<ModelContext> conts) {
		List<IContextFile> results = new LinkedList<IContextFile>();
		for (Iterator<ModelContext> iterator = conts.iterator(); iterator.hasNext();) {
			 results.add(iterator.next().getInternalContext());
		}
		return results;
	}
	
	/**
	 * React to changes in the database.
	 *
	 */
	public void elementChanged(ElementChangedEvent event) {	
//		System.out.println("Event: " +event.getDelta());
		toRefresh = new ArrayList<IRodinElement>();
		processDelta(event.getDelta());
		for (IRodinElement elem : toRefresh) {
			refreshModel(elem);
			
		}
		navigator.getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
			public void run() {
				TreeViewer viewer = navigator.getCommonViewer();
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					//TODO: only temporary solution. change this.
					// refresh everything
					if (toRefresh.contains(RodinCore.getRodinDB())) {
//						System.out.println("refreshing view");
						viewer.refresh();
					} else {
						for (Object elem : toRefresh) {
							viewer.refresh();
//							System.out.println("refreshing view: " +elem);
						}
					}
				}
		}});
	}

	
	/**
	 * Refreshes the model
	 * @param element	The element to refresh
	 */
	private void refreshModel(IRodinElement element) {
//		System.out.println("refreshing model: "+element.toString() );
		if (!(element instanceof IRodinDB)) {
			ModelProject project = projects.get(element.getRodinProject().getHandleIdentifier());
			if (element instanceof IMachineFile) {
				project.processMachine((IMachineFile)element);
			}
			if (element instanceof IContextFile) {
				project.processContext((IContextFile)element);
			}
			if (element instanceof IPOFile) {
				IPOFile file = (IPOFile) element;
				//get corresponding machine or context
				if (file.getMachineFile() != null) {
					getMachine(file.getMachineFile()).processPOFile();
				}
				if (file.getContextFile() != null) {
					getContext(file.getContextFile()).processPOFile();
				}
			}
			if (element instanceof IPSFile) {
				IPSFile file = (IPSFile) element;
				//get corresponding machine or context
				if (file.getMachineFile() != null) {
					getMachine(file.getMachineFile()).processPSFile();
				}
				if (file.getContextFile() != null) {
					getContext(file.getContextFile()).processPSFile();
				}
			}
			if (element instanceof IInvariant) {
				ModelMachine mach = (ModelMachine) getInvariant((IInvariant) element).getModelParent();
				mach.addInvariant((IInvariant) element);
			}
			if (element instanceof IEvent) {
				ModelMachine mach = (ModelMachine) getEvent((IEvent) element).getModelParent();
				mach.addEvent((IEvent) element);
			}
			if (element instanceof ITheorem) {
				ModelTheorem thm = getTheorem((ITheorem) element);
				if (thm.getModelParent() instanceof ModelMachine) {
					ModelMachine mach = (ModelMachine) thm.getModelParent();
					mach.addTheorem((ITheorem) element);
				}
				if (thm.getModelParent() instanceof ModelContext) {
					ModelContext ctx = (ModelContext) thm.getModelParent();
					ctx.addTheorem((ITheorem) element);
				}
			}
			if (element instanceof IAxiom) {
				ModelContext ctx = (ModelContext) getAxiom((IAxiom) element).getModelParent();
				ctx.addAxiom((IAxiom) element);
			}
		}
	}
	
	// List of elements need that to be refreshed in the viewer (when processing Delta of changes).
	ArrayList<IRodinElement> toRefresh;
	
	
	private void addToRefresh(IRodinElement o) {
		if (!toRefresh.contains(o)) {
			toRefresh.add(o);
		}
	}
	
	/**
	 * Process the delta recursively and depend on the kind of the delta.
	 * <p>
	 * 
	 * @param delta
	 *            The Delta from the Rodin Database
	 */
	private void processDelta(final IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			if (element instanceof IRodinProject) {
//				the content provider refreshes the model
				addToRefresh(element.getRodinDB());
			} else {
				addToRefresh(element.getParent());
			}
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinProject) {
//				the content provider refreshes the model
				//TODO: adapt this for the workspace or a working set as input
				// This only works if the DB was used as input.
				addToRefresh(element.getRodinDB());
			} else {
				if (element instanceof IContextFile) {
					removeContext((IContextFile)element);
				}
				if (element instanceof IMachineFile) {
					removeMachine((IMachineFile)element);
				}
				addToRefresh(element.getParent());
			}
			return;
		}

		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (IRodinElementDelta element2 : deltas) {
					processDelta(element2);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				addToRefresh(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				addToRefresh(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				addToRefresh(element.getParent());
				return;
			}
			if ((flags & IRodinElementDelta.F_OPENED) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				addToRefresh(element.getParent());
				return;
			}
			if ((flags & IRodinElementDelta.F_CLOSED) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				addToRefresh(element.getParent());
				return;
			}
			
		}

	}
	
}
