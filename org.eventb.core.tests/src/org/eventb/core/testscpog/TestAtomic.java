/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Various tests for checking that modifications done by tools on the Rodin
 * database are atomic.
 * 
 * @author Laurent Voisin
 */
public class TestAtomic extends BuilderTest {

	public static class DeltaListener implements IElementChangedListener {
		ArrayList<IRodinElementDelta> deltas;
		int eventType;
		
		public DeltaListener() {
			DeltaListener.this.deltas = new ArrayList<IRodinElementDelta>();
			DeltaListener.this.eventType = -1;
		}
		public DeltaListener(int eventType) {
			DeltaListener.this.deltas = new ArrayList<IRodinElementDelta>();
			DeltaListener.this.eventType = eventType;
		}
		public void elementChanged(ElementChangedEvent event) {
			if (DeltaListener.this.eventType == -1 ||
					event.getType() == DeltaListener.this.eventType) {
				DeltaListener.this.deltas.add(event.getDelta());
			}
		}
		public void flush() {
			DeltaListener.this.deltas = new ArrayList<IRodinElementDelta>();
		}
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			final int length = DeltaListener.this.deltas.size();
			for (int i = 0; i < length; i++) {
				IRodinElementDelta delta = this.deltas.get(i);
				IRodinElementDelta[] children = delta.getAffectedChildren();
				final int childrenLength = children.length;
				if (childrenLength > 0) {
					for (int j = 0; j < childrenLength; j++) {
						buffer.append(children[j]);
						if (j != childrenLength-1) {
							buffer.append("\n");
						}
					}
				} else {
					buffer.append(delta);
				}
				if (i != length-1) {
					buffer.append("\n\n");
				}
			}
			return buffer.toString();
		}
	}
	
	private IContextFile createContext() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("ctx.buc", true, null);
		addCarrierSets(rodinFile, makeList("S"));
		addConstants(rodinFile, makeList("c"));
		addAxioms(rodinFile, makeList("A1"), makeList("c ∈ S"), null);
		rodinFile.save(null, true);
		return (IContextFile) rodinFile;
	}

	private IMachineFile createMachine(boolean sees) throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("mch.bum", true, null);
		if (sees)
			addSees(rodinFile, "ctx");
		addVariables(rodinFile, makeList("v"));
		addInvariants(rodinFile, makeList("I1"), makeList("v ∈ ℤ"));
		addEvent(rodinFile, "INITIALISATION", 
				makeList(), 
				makeList(), 
				makeList(), 
				makeList("v :∈ ℤ"));
		rodinFile.save(null, true);
		return (IMachineFile) rodinFile;
	}

	private void runBuilderForDeltas(String expected) throws CoreException {
		DeltaListener listener = new DeltaListener(ElementChangedEvent.POST_CHANGE);
		RodinCore.addElementChangedListener(listener);
		runBuilder();
		assertEquals("Wrong deltas", expected, listener.toString());
	}
	
	public final void testContextSC() throws Exception {
		createContext();
		runBuilderForDeltas(
				"P[*]: {CHILDREN}\n" +
				"	ctx.bcc[+]: {}\n" +
				"	ctx.bpo[+]: {}\n" +
				"	ctx.bpr[+]: {}"
		);
	}
	
	public void testMachineSC() throws Exception {
		createMachine(false);
		runBuilderForDeltas(
				"P[*]: {CHILDREN}\n" +
				"	mch.bcm[+]: {}\n" +
				"	mch.bpo[+]: {}\n" +
				"	mch.bpr[+]: {}"
		);
	}

}
