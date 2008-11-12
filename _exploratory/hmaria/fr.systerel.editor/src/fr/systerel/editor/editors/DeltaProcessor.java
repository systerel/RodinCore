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

import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCMachineRoot;
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
	
	private boolean mustRefresh;
	private boolean mustRefreshMarkers;
	
	public DeltaProcessor(IRodinElementDelta delta, IEventBRoot inputRoot) {
		this.inputRoot = inputRoot;
		mustRefresh = false;
		mustRefreshMarkers = false;
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
		IEventBRoot scRoot;
		if (inputRoot instanceof IMachineRoot) {
			scRoot = inputRoot.getSCMachineRoot();
		} else {
			scRoot = inputRoot.getSCContextRoot();
		}
		
		//we're only interested in changes to the inputRoot and its statically checked version.
		if (kind == IRodinElementDelta.CHANGED) {
			if (element.equals(inputRoot)) {
				mustRefresh = true;
				return;
			}else if (element.equals(scRoot)){
				mustRefreshMarkers = true;
				
			// this part can be removed, when delta contains the root and no longer the file
			}else if (element instanceof ISCMachineFile){
				if (((ISCMachineFile) element).getRoot().equals(scRoot)) {
					mustRefreshMarkers = true;
				}
					
			} else{
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
