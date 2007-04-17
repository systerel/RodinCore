/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests.pm;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         Utility methods for writing a PS & PR file.
 */
public class PSWrapperUtil {

	public static void removePO(final IPOFile poFile, final IPSFile psFile,
			final IPRFile prFile, final String name) {
		final IProgressMonitor monitor = new NullProgressMonitor();

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPOSequent poSequent = poFile.getSequent(name);
					poSequent.delete(true, monitor);
					poFile.save(monitor, true);
				}

			}, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPSStatus psStatus = psFile.getStatus(name);
					psStatus.delete(true, monitor);
					psFile.save(monitor, true);
					// Do not remove corresponding the prProof
				}

			}, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyPO(final IPOFile poFile, final IPSFile psFile,
			final IPRFile prFile, final String from, final String to) {
		final IProgressMonitor monitor = new NullProgressMonitor();

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPOSequent poSequentFrom = poFile.getSequent(from);
					// Find the correct sibling
					IPOSequent poSequentTo = poFile.getSequent(to);
					IPOSequent poSibling = null;
					if (poSequentTo != null) {
						IPOSequent[] poSequents = poFile.getSequents();
						for (int i = poSequents.length - 1; i >= 0; --i) {
							if (poSequents[i].equals(poSequentTo))
								break;
							else
								poSibling = poSequents[i];
						}
					}
					poSequentFrom.copy(poFile, poSibling, to, true, monitor);
					poFile.save(monitor, true);
				}

			}, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					IPSStatus psStatusFrom = psFile.getStatus(from);

					// Find the correct sibling
					IPSStatus psStatusTo = psFile.getStatus(to);
					IPSStatus psSibling = null;
					if (psStatusTo != null) {
						IPSStatus[] psStatuses = psFile.getStatuses();
						for (int i = psStatuses.length - 1; i >= 0; --i) {
							if (psStatuses[i].equals(psStatusTo))
								break;
							else
								psSibling = psStatuses[i];
						}
					}
					psStatusFrom.copy(psFile, psSibling, to, true, monitor);
					psFile.save(monitor, true);

					IPRProof prProofFrom = prFile.getProof(from);
					// Find the correct sibling
					IPRProof prProofTo = prFile.getProof(to);
					IPRProof prSibling = null;
					if (prProofTo != null) {
						IPRProof[] prProofs = prFile.getProofs();
						for (int i = prProofs.length - 1; i >= 0; --i) {
							if (prProofs[i].equals(prProofTo))
								break;
							else
								prSibling = prProofs[i];
						}
					}
					prProofFrom.copy(prFile, prSibling, to, true, monitor);
					prFile.save(monitor, true);
				}

			}, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
