/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import static fr.systerel.internal.explorer.navigator.ExplorerUtils.runWithProgress;
import static org.eventb.core.EventBPlugin.rebuildProof;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelPOContainer;

/**
 * Implementation of the "Proof Replay on Undischarged Proofs" action.
 * 
 * @author Nicolas Beauger
 */
public class ReplayUndischargedAction extends Action {

	private final StructuredViewer viewer;
	final FormulaFactory factory;

	public ReplayUndischargedAction(StructuredViewer viewer,
			FormulaFactory factory) {
		this.viewer = viewer;
		this.factory = factory;
		setText(Messages.actions_replayUndischarged_text);
		setToolTipText(Messages.actions_replayUndischarged_tooltip);
		setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_DISCHARGED_BROKEN_PATH));
	}

	@Override
	public void run() {
		final ISelection sel = viewer.getSelection();
		if (!(sel instanceof IStructuredSelection)) {
			return;
		}
		final IStructuredSelection ssel = (IStructuredSelection) sel;
		
		final Object[] objects = ssel.toArray();

		final IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				final SubMonitor subMonitor = SubMonitor.convert(monitor,
						Messages.dialogs_replayingProofs, 10 * objects.length);
				final Set<IPSStatus> statuses = new LinkedHashSet<IPSStatus>();

				try {
					// gather POs
					for (Object obj : objects) {
						checkCancel(subMonitor);
						addObjectStatuses(obj, statuses, subMonitor.newChild(1));
					}
					// rebuild proofs of gathered POs
					rebuildProofs(statuses, subMonitor.newChild(9 * objects.length));
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}
		};
		runWithProgress(op);
	}

	static void checkCancel(IProgressMonitor monitor) throws InterruptedException {
		if(monitor != null && monitor.isCanceled()) {
			throw new InterruptedException();
		}
	}

	static void addObjectStatuses(Object obj, Set<IPSStatus> statuses,
			SubMonitor subMonitor) throws InterruptedException {
		if (obj instanceof IProject) {
			final IRodinProject prj = RodinCore.valueOf((IProject) obj);
			if (prj != null) {
				addStatuses(prj, statuses, subMonitor.newChild(1));
			}
		} else if (obj instanceof IRodinProject) {
			final IRodinProject prj = (IRodinProject) obj;
			addStatuses(prj, statuses, subMonitor.newChild(1));
		} else if (obj instanceof IEventBRoot) {
			addStatuses((IEventBRoot) obj, statuses, subMonitor.newChild(1));
		} else if (obj instanceof IPSStatus) {
			addStatus((IPSStatus) obj, statuses, subMonitor.newChild(1));
		} else if (obj instanceof IElementNode) {
			addStatuses((IElementNode) obj, statuses, subMonitor.newChild(1));
		} else if (obj instanceof IRodinElement) {
			// invariants, events, theorems, axioms
			final IModelElement element = ModelController.getModelElement(obj);
			addStatuses(element, statuses, subMonitor.newChild(1));
		}
	}

	static void addStatuses(IRodinProject project, Set<IPSStatus> statuses,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		if (!project.exists()) {
			return;
		}
		try {
			final IPSRoot[] psRoots = project
					.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
			final SubMonitor subMonitor = SubMonitor.convert(monitor, psRoots.length);
			for (IPSRoot root : psRoots) {
				checkCancel(subMonitor);
				addStatuses(root, statuses, subMonitor.newChild(1));
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e,
					UserAwareness.INFORM);
		}
	}

	static void addStatuses(IEventBRoot root, Set<IPSStatus> statuses,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		final IPSRoot psRoot = root.getPSRoot();
		if (!psRoot.exists()) {
			return;
		}
		try {
			statuses.addAll(Arrays.asList(psRoot.getStatuses()));
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e,
					UserAwareness.INFORM);
		}
	}

	// 'Proof Obligations', 'Axioms', ...
	static void addStatuses(IElementNode node, Set<IPSStatus> statuses,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		final IModelElement modelElem = ModelController.getModelElement(node);
		if (modelElem == null) {
			return;
		}
		
		final IInternalElementType<?> childrenType = node.getChildrenType();
		final Object[] children = modelElem.getChildren(childrenType, false);
		final SubMonitor subMonitor = SubMonitor.convert(monitor, children.length);

		if (childrenType == IPSStatus.ELEMENT_TYPE) { // Proof Obligations Node
			for (IPSStatus status : (IPSStatus[]) children) {
				checkCancel(subMonitor);
				addStatus(status, statuses, subMonitor.newChild(1));
			}
			return;
		}
		for (Object child : children) {
			checkCancel(subMonitor);
			final IModelElement modelElement = ModelController
			.getModelElement(child);
			if (modelElement != null) {
				addStatuses(modelElement, statuses, subMonitor.newChild(1));
			}
		}
	}

	static void addStatuses(IModelElement element, Set<IPSStatus> statuses,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		if (element instanceof ModelPOContainer) {
			final ModelPOContainer modelPOContainer = (ModelPOContainer) element;
			final IPSStatus[] modelStatuses = modelPOContainer.getIPSStatuses();
			final SubMonitor subMonitor = SubMonitor.convert(monitor, modelStatuses.length);
			for (IPSStatus status : modelStatuses) {
				checkCancel(subMonitor);
				addStatus(status, statuses, subMonitor.newChild(1));
			}
		}
	}

	static void addStatus(IPSStatus status, Set<IPSStatus> statuses,
			IProgressMonitor monitor) throws InterruptedException {
		checkCancel(monitor);
		try {
			if (!status.exists()) {
				return;
			}
			if (!isBrokenOrPending(status)) {
				return;
			}
			final IPRProof proof = status.getProof();
			if (!proof.exists()) {
				return;
			}
			statuses.add(status);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(e,
					UserAwareness.INFORM);
		}
	}

	private static boolean isBrokenOrPending(IPSStatus status)
			throws RodinDBException {
		return status.isBroken()
				|| status.getConfidence() == IConfidence.PENDING;
	}

	void rebuildProofs(Set<IPSStatus> statuses, IProgressMonitor monitor)
			throws InterruptedException {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, statuses.size());
		for (IPSStatus status : statuses) {
			checkCancel(subMonitor);
			final IPRProof proof = status.getProof();
			try {
				rebuildProof(proof, factory, subMonitor.newChild(1));
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleRodinException(e,
						UserAwareness.INFORM);
			}
		}
	}

}
