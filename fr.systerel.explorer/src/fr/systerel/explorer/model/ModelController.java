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
import org.eventb.core.ITheorem;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.navigator.RodinNavigator;

/**
 * @author Maria Husmann
 *
 */
public class ModelController implements IElementChangedListener {
	
	public ModelController(RodinNavigator navigator){
		RodinCore.addElementChangedListener(this);
		this.navigator = navigator;
	}
	
	public static void processProject(IRodinProject project){
		try {
			ModelProject prj;
			prj =  new ModelProject(project);
			//overwrite existing project
			projects.put(project.getHandleIdentifier(), prj);
			IMachineFile[] machines = project.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
			for (int i = 0; i < machines.length; i++) {
				prj.processMachine(machines[i]);
			}
			
			IContextFile[] contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
			for (int i = 0; i < contexts.length; i++) {
				prj.processContext(contexts[i]);
			}
			
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ModelInvariant getInvariant(IInvariant invariant){
		ModelProject project = projects.get(invariant.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getInvariant(invariant);
		}
		return null;
	}

	public static ModelEvent getEvent(IEvent event){
		ModelProject project = projects.get(event.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getEvent(event);
		}
		return null;
	}
	
	public static ModelAxiom getAxiom(IAxiom axiom){
		ModelProject project = projects.get(axiom.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getAxiom(axiom);
		}
		return null;
	}

	public static ModelTheorem getTheorem(ITheorem theorem){
		ModelProject project = projects.get(theorem.getRodinProject().getHandleIdentifier());
		if (project != null) {
			return project.getTheorem(theorem);
		}
		return null;
	}
	
	public static ModelMachine getMachine(IMachineFile machineFile){
		ModelProject project = projects.get(machineFile.getRodinProject().getHandleIdentifier());
		if (project != null) {
				return project.getMachine(machineFile.getBareName());
		}
		return null;
	}

	public static ModelContext getContext(IContextFile contextFile){
		ModelProject project = projects.get(contextFile.getRodinProject().getHandleIdentifier());
		if (project != null) {
				return project.getContext(contextFile.getBareName());
		}
		return null;
	}
	
	public static ModelProject getProject(IRodinProject project) {
		return projects.get(project.getHandleIdentifier());
	}
	
	public static void removeProject(IRodinProject project){
		projects.remove(project.getHandleIdentifier());
	}
	
	public static IMachineFile[] convertToIMachine(ModelMachine[] machs) {
		IMachineFile[] results = new IMachineFile[machs.length];
		for (int i = 0; i < machs.length; i++) {
			results[i] = machs[i].getInternalMachine();
			
		}
		return results;
	}
	
	public static List<IMachineFile> convertToIMachine(List<ModelMachine> machs) {
		List<IMachineFile> results = new LinkedList<IMachineFile>();
		for (Iterator<ModelMachine> iterator = machs.iterator(); iterator.hasNext();) {
			 results.add(iterator.next().getInternalMachine());
		}
		return results;
	}

	public static IContextFile[] convertToIContext(ModelContext[] conts) {
		IContextFile[] results = new IContextFile[conts.length];
		for (int i = 0; i < conts.length; i++) {
			results[i] = conts[i].getInternalContext();
			
		}
		return results;
	}

	public static List<IContextFile> convertToIContext(List<ModelContext> conts) {
		List<IContextFile> results = new LinkedList<IContextFile>();
		for (Iterator<ModelContext> iterator = conts.iterator(); iterator.hasNext();) {
			 results.add(iterator.next().getInternalContext());
		}
		return results;
	}
	
	
	private static HashMap<String, ModelProject> projects = new HashMap<String, ModelProject>();
	RodinNavigator navigator;

	/**
	 * React to changes in the database.
	 *
	 */
	public void elementChanged(ElementChangedEvent event) {	
//		System.out.println(event.getDelta());
		toRefresh = new ArrayList<Object>();
		processDelta(event.getDelta());
		navigator.getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
			public void run() {
				TreeViewer viewer = navigator.getCommonViewer();
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
//					Object[] objects = viewer.getExpandedElements();
					for (Object elem : toRefresh) {
						viewer.refresh(elem);
					}
//					viewer.setExpandedElements(objects);
				}
			}});
	}

	
	/**
	 * Refreshes the model
	 * @param element	The element to refresh
	 */
	private void refreshModel(IRodinElement element) {
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
			ModelMachine mach = (ModelMachine) getInvariant((IInvariant) element).getParent();
			mach.addInvariant((IInvariant) element);
		}
		if (element instanceof IEvent) {
			ModelMachine mach = (ModelMachine) getEvent((IEvent) element).getParent();
			mach.addEvent((IEvent) element);
		}
		if (element instanceof ITheorem) {
			ModelTheorem thm = getTheorem((ITheorem) element);
			if (thm.getParent() instanceof ModelMachine) {
				ModelMachine mach = (ModelMachine) thm.getParent();
				mach.addTheorem((ITheorem) element);
			}
			if (thm.getParent() instanceof ModelContext) {
				ModelContext ctx = (ModelContext) thm.getParent();
				ctx.addTheorem((ITheorem) element);
			}
		}
		if (element instanceof IAxiom) {
			ModelContext ctx = (ModelContext) getAxiom((IAxiom) element).getParent();
			ctx.addAxiom((IAxiom) element);
		}
	}
	
	// List of elements need that to be refreshed in the viewer (when processing Delta of changes).
	ArrayList<Object> toRefresh;
	
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
				toRefresh.add(element.getRodinDB());
			} else {
				refreshModel(element.getParent());
				toRefresh.add(element.getParent());
			}
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinProject) {
//				the content provider refreshes the model
				toRefresh.add(element.getRodinDB());
			} else {
				refreshModel(element.getParent());
				toRefresh.add(element.getParent());
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
				refreshModel(element.getParent());
				toRefresh.add(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				refreshModel(element.getParent());
				toRefresh.add(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				refreshModel(element.getParent());
				toRefresh.add(element.getParent());
				return;
			}
		}

	}
	
}
