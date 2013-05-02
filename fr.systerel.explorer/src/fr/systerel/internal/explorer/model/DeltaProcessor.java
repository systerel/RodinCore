/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.internal.explorer.model;

import java.util.ArrayList;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * This class processes an <code>IRodinElementDelta</code> for the
 * <code>ModelController</code>. It decides what needs to be refreshed in the
 * model and the viewer and what needs to be removed from the model.
 */
public class DeltaProcessor {
	
	public DeltaProcessor(IRodinElementDelta delta) {
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
		if (kind == IRodinElementDelta.ADDED) {
			addToRefresh(element.getParent());
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			if (element instanceof IRodinProject) {
				addToRemove(element);
				// This will update everything.
				addToRefresh(element.getRodinDB());
			} else if (element instanceof IRodinFile) {
				final IRodinFile file = (IRodinFile) element;
				final IInternalElement root = file.getRoot();
				if (root instanceof IContextRoot) {
					// remove the context from the model
					addToRemove(root);
				} else if (root instanceof IMachineRoot) {
					// remove the machine from the model
					addToRemove(root);
				}
				if (root instanceof IEventBRoot) {
					// add the containing project to refresh.
					// if it is a root
					addToRefresh(element.getRodinProject());
				}
			} else if (element instanceof IContextRoot) {
				// remove the context from the model
				addToRemove(element);
			} else if (element instanceof IMachineRoot) {
				// remove the machine from the model
				addToRemove(element);
			} else if (element instanceof IEventBRoot) {
				// add the containing project to refresh.
				// if it is a root
				addToRefresh(element.getRodinProject());
			} else {
				// otherwise add the parent to refresh
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
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}
			if ((flags & IRodinElementDelta.F_OPENED) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}
			if ((flags & IRodinElementDelta.F_CLOSED) != 0) {
				//refresh parent for safety (e.g. dependencies between machines)
				if (element.getParent() != null) {
					addToRefresh(element.getParent());
				} else {
					addToRefresh(element);
				}
				return;
			}
			
		}

	}
	
	private void addToRefresh(IRodinElement o) {
		if (!toRefresh.contains(o)) {
			//add the root and not the file
			if (o instanceof IRodinFile) {
				o = ((IRodinFile) o).getRoot();
			}
			toRefresh.add(o);
		}
	}

	private void addToRemove(IRodinElement o) {
		if (!toRemove.contains(o)) {
			//add the root and not the file
			if (o instanceof IRodinFile) {
				o = ((IRodinFile) o).getRoot();
			}
			toRemove.add(o);
		}
	}
	
	// List of elements that need to be refreshed in the viewer and the model.
	private ArrayList<IRodinElement> toRefresh =new ArrayList<IRodinElement>();

	// List of elements that need to be removed from the model
	private ArrayList<IRodinElement> toRemove =new ArrayList<IRodinElement>();
	
	public ArrayList<IRodinElement> getToRefresh() {
		return toRefresh;
	}

	public ArrayList<IRodinElement> getToRemove() {
		return toRemove;
	}

}
