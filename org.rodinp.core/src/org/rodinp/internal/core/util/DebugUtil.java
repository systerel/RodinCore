/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.util;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;

/**
 * Miscellaneous debugging utilities as static methods.
 * 
 * @author Laurent Voisin
 */
public abstract class DebugUtil {

	/**
	 * Prints a string representation of the given event on standard output.
	 * <p>
	 * Fragile, to be used for debugging purposes only.
	 * </p>
	 * @param event  an event to display
	 */
	public static void printEvent(IResourceChangeEvent event) {
		final int eventType = event.getType();
		final IResourceDelta delta = event.getDelta();
		
		System.out.print(eventTypeAsString(eventType));
		switch (eventType) {
		case IResourceChangeEvent.POST_CHANGE:
			System.out.println(resourceDeltaAsString(delta));
			break;
		case IResourceChangeEvent.PRE_CLOSE:
		case IResourceChangeEvent.PRE_DELETE:  
			System.out.println("(" + event.getResource() + ")");
			break;
		case IResourceChangeEvent.PRE_BUILD:
		case IResourceChangeEvent.POST_BUILD:  
			System.out.println("("
					+ buildKindAsString(event.getBuildKind())
					+ ", "
					+ event.getSource()
					+ ")");
			break;
		default: 
			break;
		}
	}

	public static String eventTypeAsString(int eventType) {
		switch (eventType) {
		case IResourceChangeEvent.POST_CHANGE: 
			return "POST_CHANGE";
		case IResourceChangeEvent.POST_BUILD:  
			return "POST_BUILD";
		case IResourceChangeEvent.PRE_BUILD:   
			return "PRE_BUILD";
		case IResourceChangeEvent.PRE_CLOSE:   
			return "PRE_CLOSE";
		case IResourceChangeEvent.PRE_DELETE:  
			return "PRE_DELETE";
		default: 
			return "UNKNOWN";
		}
	}
	
	public static String buildKindAsString(int buildKind) {
		switch (buildKind) {
		case IncrementalProjectBuilder.AUTO_BUILD: 
			return "auto";
		case IncrementalProjectBuilder.CLEAN_BUILD: 
			return "clean";
		case IncrementalProjectBuilder.FULL_BUILD: 
			return "full";
		case IncrementalProjectBuilder.INCREMENTAL_BUILD: 
			return "incremental";
		default: 
			return "unknown";
		}
	}
	
	// For the discouraged access to ResourceDelta.
	@SuppressWarnings("all")
	public static String resourceDeltaAsString(IResourceDelta delta) {
		return ((org.eclipse.core.internal.events.ResourceDelta) delta)
				.toDeepDebugString();
	}
		
}
