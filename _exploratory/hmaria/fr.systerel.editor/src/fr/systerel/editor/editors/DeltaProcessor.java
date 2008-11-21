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


package fr.systerel.editor.editors;

import java.util.ArrayList;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;

/**
 * This class processes an <code>IRodinElementDelta</code> for the
 * <code>RodinEditor</code>. 
 */
public class DeltaProcessor {
	/**
	 * The root (a machine or a context) that is used as input for the editor.
	 */
	private IEventBRoot inputRoot;
	// indicates that the whole document should be recreated
	private boolean mustRefresh;
	// indicates that the markers should be refreshed.
	private boolean mustRefreshMarkers;
	// indicates which elements should be refreshed.
	private ArrayList<IRodinElement> elementsToRefresh;
	private IEventBRoot scRoot;
	
	
	public IRodinElement[] getElementsToRefresh() {
		return elementsToRefresh.toArray(new IRodinElement[elementsToRefresh.size()]);
	}

	public DeltaProcessor(IRodinElementDelta delta, IEventBRoot inputRoot) {
		this.inputRoot = inputRoot;
		mustRefresh = false;
		mustRefreshMarkers = false;
		elementsToRefresh = new ArrayList<IRodinElement>();
		if (inputRoot instanceof IMachineRoot) {
			scRoot = inputRoot.getSCMachineRoot();
		} else {
			scRoot = inputRoot.getSCContextRoot();
		}
		processDelta(delta);

	}

	/**
	 * Process the delta recursively depending on the kind of the delta.
	 * <p>
	 * 
	 * @param delta
	 *            The Delta from the Rodin Database
	 */
	public void processDelta(final IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();


		//change happened to another machine or context. we're not interested.
		if (element instanceof IMachineRoot || element instanceof IContextRoot) {
			if (!element.equals(inputRoot)) {
				return;
				
			}
		}
		
		//TODO: handle additions and removing
		
		
		//we're only interested in changes to the inputRoot or its file and its statically checked version.
		if (kind == IRodinElementDelta.CHANGED) {
			
//			if (element.equals(inputRoot)) {
//				if ((delta.getFlags() & IRodinElementDelta.F_CHILDREN )!= 0) {
//					mustRefresh = true;
//					return;
//				}
//			}else if (element.equals(inputRoot.getRodinFile())) {
//				if ((delta.getFlags() & IRodinElementDelta.F_CHILDREN )!= 0) {
//					mustRefresh = true;
//					return;
//				}
			if (element.equals(scRoot)){
				mustRefreshMarkers = true;
				
			}else if (element.equals(scRoot.getRodinFile())) {
				mustRefreshMarkers = true;
					
			} else if ((delta.getFlags() & IRodinElementDelta.F_ATTRIBUTE ) != 0){
				if (element.getAncestor(IMachineRoot.ELEMENT_TYPE).equals(inputRoot )
						||  element.getAncestor(IContextRoot.ELEMENT_TYPE).equals(inputRoot )) {
					elementsToRefresh.add(element);
				}
			
			} else if ((delta.getFlags() & IRodinElementDelta.F_CHILDREN ) != 0){
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (IRodinElementDelta element2 : deltas) {
					processDelta(element2);
				}
				
			}
		}

	}
	
	/**
	 * 
	 * @return <code>true</code>, if the editor should be refreshed, <code>false</code> otherwise.
	 */
	public boolean isMustRefresh() {
		return mustRefresh;
	}

	public boolean isMustRefreshMarkers() {
		return mustRefreshMarkers;
	}
	


}
