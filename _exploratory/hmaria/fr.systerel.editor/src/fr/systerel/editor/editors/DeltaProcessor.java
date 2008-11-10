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
	
	public DeltaProcessor(IRodinElementDelta delta, IEventBRoot inputRoot) {
		this.inputRoot = inputRoot;
		mustRefresh = false;
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
		
		//we're only interested in changes to the inputRoot.
		if (kind == IRodinElementDelta.CHANGED) {
			if (element.equals(inputRoot)) {
				mustRefresh = true;
				return;
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
	


}
