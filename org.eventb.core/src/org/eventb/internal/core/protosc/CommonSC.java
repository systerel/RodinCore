/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBMarker;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;

/**
 * @author halstefa
 *
 */
public class CommonSC implements ISCProblemList {

	public static final boolean DEBUG = false;
	
	protected LinkedList<SCProblem> problems;
	
	public CommonSC() {
		problems = new LinkedList<SCProblem>();
	}

	public void addProblem(SCProblem problem) {
		problems.add(problem);
		
	}
	
	protected void issueProblems(IRodinFile file) {
		for(SCProblem problem : problems) {
			addMarker((IRodinFile) problem.getElement().getOpenable(), problem.getElement(), problem.getMessage(), problem.getSeverity());
			if(DEBUG)
				System.out.println(getClass().getName() + ": " + problem.getMessage());
		}
	}
	
	public static void makeError(String message) throws CoreException {
		throw new CoreException (
				new Status(IStatus.ERROR, EventBPlugin.getPlugin().getBundle().getSymbolicName(), Platform.PLUGIN_ERROR, message, null)
				);
	}
	
	private String printElement(IRodinElement element) {
		String elementType = element.getElementType();
		String result = elementType.substring(elementType.lastIndexOf('.')+1);
		if(element instanceof IUnnamedInternalElement)
			result = result + " in " + printElement(element.getParent());
		else if(element instanceof IInternalElement)
			result = result + " " + ((IInternalElement) element).getElementName(); 
		return result;
	}
	
	protected void addMarker(IRodinFile rodinFile, IRodinElement element, String message, int severity) {
		try {
			IMarker marker = rodinFile.getResource().createMarker(IRodinDBMarker.RODIN_PROBLEM_MARKER);
			
			// TODO: correctly implement marker location
			marker.setAttribute(IMarker.LOCATION, element.getPath().toString());
			marker.setAttribute(IMarker.MESSAGE, "(" + printElement(element) + ") " + message);
			marker.setAttribute(IMarker.SEVERITY, severity);
		} catch(CoreException e) {
			// can safely ignore
		}
	}
	
	protected void logMessage(Exception e, String message) {
		Plugin plugin = EventBPlugin.getDefault();
		IStatus status = new Status(IMarker.SEVERITY_ERROR, EventBPlugin.PLUGIN_ID, Platform.PLUGIN_ERROR, message, e);
		plugin.getLog().log(status);
	}
	
	protected Collection<FreeIdentifier> makeIdentifiers(Collection<String> strings, FormulaFactory factory) {
		ArrayList<FreeIdentifier> identifiers = new ArrayList<FreeIdentifier>(strings.size());
		for(String string : strings) {
			identifiers.add(factory.makeFreeIdentifier(string, null));
		}
		return identifiers;
	}

	protected void addMarker(IRodinElement element, String message, int severity) {
		addMarker((IRodinFile) element.getOpenable(), element, message, severity);
	}

	protected void addProblemMarkers(IRodinElement element, List<ASTProblem> problemList) {
		
		// TODO: this is only a rudimentary implementation...
		
		for(ASTProblem problem : problemList) {
			int severity = problem.isError() ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING;
			String message = problem.toString();
			
			addMarker(element, message, severity);
		}
	}

	public void addProblem(IInternalElement element, String message, int severity) {
		addProblem(new SCProblem(element, message, severity));
	}

}
