/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is a class which store utility static methods that can be used in
 *         the Prover User interface.
 */
public class ProverUIUtils {

	
	/**
	 * The debug flag. 
	 */
	public static boolean DEBUG = false;

	// The debug prefix.
	private final static String DEBUG_PREFIX = "*** ProverUI *** ";

	/**
	 * Prints the message if the {@link #DEBUG} flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the message to print out
	 */
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

	/**
	 * Gets the user support delta {@link IUserSupportDelta} related to an user
	 * support {@link IUserSupport} within an user support manager delta
	 * {@link IUserSupportManagerDelta}.
	 * 
	 * @param delta
	 *            the input user support manager delta.
	 * @param userSupport
	 *            the input user support.
	 * @return the delta contains in the user support manager delta related to
	 *         the input user support.
	 */
	public static IUserSupportDelta getUserSupportDelta(
			IUserSupportManagerDelta delta, IUserSupport userSupport) {
		IUserSupportDelta[] affectedUserSupports = delta
				.getAffectedUserSupports();
		for (IUserSupportDelta affectedUserSupport : affectedUserSupports) {
			if (affectedUserSupport.getUserSupport() == userSupport) {
				return affectedUserSupport;
			}
		}
		return null;
	}

	/**
	 * Gets the proof state delta {@link IProofStateDelta} related to a proof
	 * state {@link IProofState} within an user support delta
	 * {@link IUserSupportDelta}.
	 * 
	 * @param delta
	 *            the input user support delta.
	 * @param proofState
	 *            the input proof state.
	 * @return the delta contains in the user support delta related to the input
	 *         proof state.
	 */
	public static IProofStateDelta getProofStateDelta(IUserSupportDelta delta,
			IProofState proofState) {
		IProofStateDelta[] affectedProofStates = delta.getAffectedProofStates();
		for (IProofStateDelta affectedProofState : affectedProofStates) {
			if (affectedProofState.getProofState() == proofState) {
				return affectedProofState;
			}
		}
		return null;
	}

	/**
	 * Applies a tactic with a progress monitor.
	 * 
	 * @param shell
	 *            the parent shell
	 * @param userSupport
	 *            the user support to run the tactic.
	 * @param tactic
	 *            the tactic to be run
	 * @param applyPostTactic
	 *            boolean flag to indicate if the post-tactics should be run
	 *            after applying the input tactic or not.
	 */
	public static void applyTacticWithProgress(Shell shell,
			final IUserSupport userSupport, final ITactic tactic,
			final boolean applyPostTactic) {
		UIUtils.runWithProgressDialog(shell, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					userSupport.applyTactic(tactic, applyPostTactic, monitor);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Converts an array of tactic IDs to an array of tactic descriptor
	 * {@link ITacticDescriptor} given the tactic preference
	 * {@link IAutoTacticPreference}.
	 * 
	 * @param tacticPreference
	 *            the tactic preference
	 * @param tacticIDs
	 *            an array of tactic IDs
	 * @return an array of registered tactic descriptors corresponding to the
	 *         input tactic IDs, i.e. ignores invalid tactic IDs and tactic
	 *         which are not registered to be used for this tactic preference.
	 */
	public static ArrayList<ITacticDescriptor> stringsToTacticDescriptors(
			IAutoTacticPreference tacticPreference, String[] tacticIDs) {
		ArrayList<ITacticDescriptor> result = new ArrayList<ITacticDescriptor>();
		for (String tacticID : tacticIDs) {
			IAutoTacticRegistry tacticRegistry = SequentProver.getAutoTacticRegistry();
			if (!tacticRegistry.isRegistered(tacticID)) {
				if (UIUtils.DEBUG) {
					System.out.println("Tactic " + tacticID
							+ " is not registered.");
				}
				continue;
			}
			
			ITacticDescriptor tacticDescriptor = tacticRegistry
					.getTacticDescriptor(tacticID);
			if (!tacticPreference.isDeclared(tacticDescriptor)) {
				if (UIUtils.DEBUG) {
					System.out
							.println("Tactic "
									+ tacticID
									+ " is not declared for using within this tactic container.");
				}
			}
			else {
				result.add(tacticDescriptor);
			}
		}
		return result;
	}

	/**
	 * Check if a proof status is discharged or not.
	 * 
	 * @param status
	 *            a proof status
	 * @return <code>true</code> if the proof status is discharge (at least
	 *         {@link IConfidence#DISCHARGED_MAX}). Return <code>false</code>
	 *         otherwise.
	 * @throws RodinDBException
	 *             if error occurs in getting the confidence of the input proof
	 *             status.
	 */
	public static boolean isDischarged(IPSStatus status) throws RodinDBException {
		return (status.getConfidence() >= IConfidence.DISCHARGED_MAX);
	}

	/**
	 * Check if a proof status is automatic or not
	 * 
	 * @param status
	 *            a proof status
	 * @return <code>true</code> if the proof is automatic, return
	 *         <code>false</code> otherwise.
	 * @throws RodinDBException
	 *             if any error occurs.
	 */
	public static boolean isAutomatic(IPSStatus status) throws RodinDBException {
		return !status.getHasManualProof();
	}

	/**
	 * Check if a proof status is reviewed (i.e. between
	 * {@link IConfidence#PENDING} and {@link IConfidence#REVIEWED_MAX}).
	 * 
	 * @param status
	 *            a proof status
	 * @return <code>true</code> if the proof status is reviewed, return
	 *         <code>false</code> otherwise.
	 * @throws RodinDBException
	 *             if error occurs in getting the confidence of the input proof
	 *             status.
	 */
	public static boolean isReviewed(IPSStatus status) throws RodinDBException {
		int confidence = status.getConfidence();
		return confidence > IConfidence.PENDING
				&& confidence <= IConfidence.REVIEWED_MAX;
	}

}
