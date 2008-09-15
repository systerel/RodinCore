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


package fr.systerel.explorer.complexModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Administrator
 *
 */
public class ComplexModelFactory {

	public static void processProject(IRodinProject project){
		try {
			ComplexProject prj;
			prj =  new ComplexProject(project);
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
	
	public static ComplexMachine getMachine(String identifier){
		for (Iterator<ComplexProject> iterator = projects.values().iterator(); iterator.hasNext();) {
			ComplexProject project = iterator.next();
			if (project.hasMachine(identifier)) {
				return project.getMachine(identifier);
			}
		}
		return null;
	}
	
	public static ComplexContext getContext(String identifier){
		for (Iterator<ComplexProject> iterator = projects.values().iterator(); iterator.hasNext();) {
			ComplexProject project = iterator.next();
			if (project.hasContext(identifier)) {
				return project.getContext(identifier);
			}
		}
		return null;
	}

	public static IMachineFile[] convertToIMachine(ComplexMachine[] machs) {
		IMachineFile[] results = new IMachineFile[machs.length];
		for (int i = 0; i < machs.length; i++) {
			results[i] = machs[i].getInternalMachine();
			
		}
		return results;
	}

	public static List<IMachineFile> convertToIMachine(List<ComplexMachine> machs) {
		List<IMachineFile> results = new LinkedList<IMachineFile>();
		for (Iterator<ComplexMachine> iterator = machs.iterator(); iterator.hasNext();) {
			 results.add(iterator.next().getInternalMachine());
		}
		return results;
	}

	public static IContextFile[] convertToIContext(ComplexContext[] conts) {
		IContextFile[] results = new IContextFile[conts.length];
		for (int i = 0; i < conts.length; i++) {
			results[i] = conts[i].getInternalContext();
			
		}
		return results;
	}

	public static List<IContextFile> convertToIContext(List<ComplexContext> conts) {
		List<IContextFile> results = new LinkedList<IContextFile>();
		for (Iterator<ComplexContext> iterator = conts.iterator(); iterator.hasNext();) {
			 results.add(iterator.next().getInternalContext());
		}
		return results;
	}
	
	
	public static ComplexProject getProject(String identifier) {
		return projects.get(identifier);
	}
	
	
	private static HashMap<String, ComplexProject> projects = new HashMap<String, ComplexProject>();
	
}
