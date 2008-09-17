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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Maria Husmann
 *
 */
public class ModelController {
	
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

}
