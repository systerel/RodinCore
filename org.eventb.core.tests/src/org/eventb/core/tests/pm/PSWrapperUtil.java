/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.eventb.core.tests.BuilderTest.*;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         Utility methods for writing a PS & PR file.
 */
public class PSWrapperUtil {

	public static void removePO(final IPORoot poRoot, final IPSRoot psRoot,
			final IPRRoot prRoot, final String name) {
		final IProgressMonitor monitor = new NullProgressMonitor();

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPOSequent poSequent = poRoot.getSequent(name);
					poSequent.delete(true, monitor);
					saveRodinFileOf(poRoot);
				}

			}, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPSStatus psStatus = psRoot.getStatus(name);
					psStatus.delete(true, monitor);
					saveRodinFileOf(psRoot);
					// Do not remove the corresponding prProof
				}

			}, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void copyPO(final IPORoot poRoot, final IPSRoot psRoot,
			final IPRRoot prRoot, final String from, final String to) {
		final IProgressMonitor monitor = new NullProgressMonitor();

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPOSequent poSequentFrom = poRoot.getSequent(from);
					// Find the correct sibling
					IPOSequent poSequentTo = poRoot.getSequent(to);
					IPOSequent poSibling = null;
					if (poSequentTo != null) {
						IPOSequent[] poSequents = poRoot.getSequents();
						for (int i = poSequents.length - 1; i >= 0; --i) {
							if (poSequents[i].equals(poSequentTo))
								break;
							else
								poSibling = poSequents[i];
						}
					}
					poSequentFrom.copy(poRoot, poSibling, to, true, monitor);
					saveRodinFileOf(poRoot);
				}

			}, monitor);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPSStatus psStatusFrom = psRoot.getStatus(from);

					// Find the correct sibling
					IPSStatus psStatusTo = psRoot.getStatus(to);
					IPSStatus psSibling = null;
					if (psStatusTo != null) {
						IPSStatus[] psStatuses = psRoot.getStatuses();
						for (int i = psStatuses.length - 1; i >= 0; --i) {
							if (psStatuses[i].equals(psStatusTo))
								break;
							else
								psSibling = psStatuses[i];
						}
					}
					psStatusFrom.copy(psRoot, psSibling, to, true, monitor);
					saveRodinFileOf(psRoot);

					IPRProof prProofFrom = prRoot.getProof(from);
					// Find the correct sibling
					IPRProof prProofTo = prRoot.getProof(to);
					IPRProof prSibling = null;
					if (prProofTo != null) {
						IPRProof[] prProofs = prRoot.getProofs();
						for (int i = prProofs.length - 1; i >= 0; --i) {
							if (prProofs[i].equals(prProofTo))
								break;
							else
								prSibling = prProofs[i];
						}
					}
					prProofFrom.copy(prRoot, prSibling, to, true, monitor);
					saveRodinFileOf(prRoot);
				}

			}, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
