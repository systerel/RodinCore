/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - added getParsed()
 *     Systerel - fixed Hyperlink.setImage() calls
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static java.util.Collections.singleton;
import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.registry.PositionApplicationProxy;
import org.eventb.internal.ui.prover.tactics.ExistsInstantiationGoal.ExistsInstantiationGoalApplication;
import org.eventb.internal.ui.prover.tactics.ForallInstantiationHyp.ForallInstantiationHypApplication;
import org.eventb.ui.prover.IProofCommand;
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
	
	
	/**
	 * The character used to build a margin on the left of the proving UI
	 */
	public static final String TAB = "\t";
	
	public static final String OBJ = "\uFFFC";

	// The debug prefix.
	private final static String DEBUG_PREFIX = "*** ProverUI *** ";
	
	
	public final static Color SOFT_BG_COLOR = EventBSharedColor.getColor(new RGB(247, 247, 247));

	/**
	 * Prints the message if the {@link #DEBUG} flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the message to print out
	 */
	public static void debug(String message) {
		if (DEBUG) {
			System.out.println(DEBUG_PREFIX + message);
		}
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
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				userSupport.applyTactic(tactic, applyPostTactic, monitor);
			}
		});
	}
	
	/**
	 * Applies the given command using the given user support and arguments.
	 * 
	 * @param command
	 *            a command to apply
	 * @param userSupport
	 *            the user support on which the command is to be applied
	 * @param hyp
	 *            the current hypothesis or <code>null</code> to apply to the
	 *            goal
	 * @param inputs
	 *            the inputs of the tactic
	 * @param pm
	 *            a progress monitor
	 */
	public static void applyCommand(IProofCommand command, IUserSupport userSupport, Predicate hyp, String[] inputs, IProgressMonitor pm) {
		if(pm != null)
			pm.beginTask("Proving", IProgressMonitor.UNKNOWN);
		try {
			command.apply(userSupport, hyp, inputs, pm);
		} catch (RodinDBException e) {
			if (UIUtils.DEBUG)
				e.printStackTrace();
			UIUtils.log(e, "Error applying proof command");
		} finally {
			if(pm != null)
				pm.done();
		}
	}

	/**
	 * Applies the given tactic using the given user support and arguments.
	 * 
	 * @param tactic
	 *            a tactic to apply
	 * @param userSupport
	 *            the user support on which the tactic is to be applied
	 * @param hyps
	 *            a set of hypotheses or <code>null</code>
	 * @param skipPostTactic
	 *            if <code>true</code>, post tactic will NOT be applied;
	 *            otherwise post tactic will be applied provided that the user
	 *            did not deactivate them
	 * @param pm
	 *            a progress monitor
	 */
	public static void applyTactic(ITactic tactic, IUserSupport userSupport,
			Set<Predicate> hyps, boolean skipPostTactic, IProgressMonitor pm) {
		if (pm != null)
			pm.beginTask("Proving", IProgressMonitor.UNKNOWN);
		try {
			if (hyps == null) {
				userSupport.applyTactic(tactic, !skipPostTactic, pm);
			} else {
				userSupport.applyTacticToHypotheses(tactic, hyps,
						!skipPostTactic, pm);
			}
		} finally {
			if (pm != null)
				pm.done();
		}
	}

	/**
	 * Applies the instantiation tactic on the hypothesis or the goal if
	 * hypothesis is <code>null</code>.
	 * 
	 * @param hypothesis
	 *            the hypothesis to instantiate or <code>null</code> if the goal
	 *            is to be taken into consideration
	 * @param us
	 *            the current user support
	 * @param inputs
	 *            the user inputs
	 * @param globalInput
	 *            the global input
	 */
	public static void applyInstantiation(Predicate hypothesis,
			IUserSupport us, String[] inputs, String globalInput) {
		final TacticUIRegistry registry = TacticUIRegistry.getDefault();
		final List<PositionApplicationProxy> applis = registry
				.getPositionApplications(us, hypothesis);
		final Set<Predicate> hypset;
		if (hypothesis == null) {
			hypset = null;
		} else {
			hypset = singleton(hypothesis);
		}
		// FIXME hard coded list ?
		final Set<String> iTacticIDs = getInstantiationTacticIDs();
		for (PositionApplicationProxy app : applis) {
			if (iTacticIDs.contains(app.getTacticID())) {
				applyTactic(app.getTactic(inputs, globalInput), us, hypset,
						false, new NullProgressMonitor());
				return;
			}
		}
	}

	/**
	 * Returns the IDs of the instantiation tactics.
	 * 
	 * @return the IDs of the instantiation tactics.
	 */
	private static Set<String> getInstantiationTacticIDs() {
		final Set<String> tacticIDs = new HashSet<String>();
		tacticIDs.add(new ForallInstantiationHypApplication(null).getTacticID());
		tacticIDs.add(new ExistsInstantiationGoalApplication().getTacticID());
		return tacticIDs;
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

	public static void addHyperlink(Composite parent, FormToolkit toolkit, int alignment, Image icon, String tooltip, IHyperlinkListener listener, boolean enable) {
		ImageHyperlink hyperlink = new ImageHyperlink(parent,
				SWT.CENTER);
		hyperlink.setLayoutData(new GridData(alignment, alignment, false,
				false));
		toolkit.adapt(hyperlink, true, true);
		setHyperlinkImage(hyperlink, icon);
	
		hyperlink.addHyperlinkListener(listener);
		hyperlink.setToolTipText(tooltip);
		hyperlink.setEnabled(enable);
	}

	/**
	 * Returns a parsed and type checked version of the given predicate string,
	 * using the given type environment.
	 * <p>
	 * Used by methods that require source locations and/or type checked
	 * predicates.
	 * </p>
	 * <p>
	 * Assumes that the given string is indeed parseable and type checkable.
	 * </p>
	 * 
	 * @param predString
	 *            a predicate string
	 * @param typeEnv
	 *            a type environment that allows type checking the predicate
	 * @return a parsed and type checked predicate
	 */
	public static Predicate getParsedTypeChecked(String predString, ITypeEnvironment typeEnv) {
		final Predicate parsedPred = getParsed(predString, typeEnv.getFormulaFactory());
		final ITypeCheckResult typeCheckResult = parsedPred.typeCheck(typeEnv);
		assert !typeCheckResult.hasProblem();
		return parsedPred;
	}
	
	/**
	 * Returns a parsed and not type checked version of the given predicate
	 * string, using the given type environment.
	 * <p>
	 * Used by methods that require source locations.
	 * </p>
	 * <p>
	 * Assumes that the given string is indeed parseable.
	 * </p>
	 * 
	 * @param predString
	 *            a predicate string
	 * @param formulaFactory
	 *            the formula factory to use
	 * @return a parsed predicate
	 */
	public static Predicate getParsed(String predString,
			FormulaFactory formulaFactory) {
		final IParseResult parseResult = formulaFactory.parsePredicate(
				predString, null);
		assert !parseResult.hasProblem();
		return parseResult.getParsedPredicate();
	}

	/**
	 * Returns a map associating tactic applications with their corresponding
	 * application points (i.e. hyperlink bounds) for a predicate and its
	 * corresponding string representation.
	 * 
	 * @param us
	 *            the current user support
	 * @param isHypothesis
	 *            <code>true</code>if application points are searched for an
	 *            hypothesis predicate, <code>false</code> for goal tactic
	 *            applications
	 * @param str
	 *            the string representation of the given predicate
	 * @param pred
	 *            the predicate tactic application points are searched
	 *            for
	 * @return a map associating points and tactic applications for the given
	 *         predicate <code>pred</code> and its string representation
	 *         <code>str</code>
	 */
	public static Map<Point, List<PositionApplicationProxy>> getHyperlinks(
			TacticHyperlinkManager manager, IUserSupport us,
			boolean isHypothesis, String str, Predicate pred) {

		final Map<Point, List<PositionApplicationProxy>> links;
		links = new HashMap<Point, List<PositionApplicationProxy>>();

		final TacticUIRegistry registry = TacticUIRegistry.getDefault();
		final Predicate hyp = isHypothesis ? pred : null;
		final List<PositionApplicationProxy> applications = registry
				.getPositionApplications(us, hyp);

		// Non type-checked predicate containing source location used here to
		// get hyperlinks (in which oftype expression has been removed etc.)
		final Predicate parsedPred = getParsed(str, us.getFormulaFactory());

		for (PositionApplicationProxy application : applications) {
			final Point pt = application.getHyperlinkBounds(str, parsedPred);
			if (pt == null) {
				// client error has already been reported
				continue;
			}
			final Point positionInText = getGlobalLocationAtOffset(manager, pt);
			List<PositionApplicationProxy> applicationList = links
					.get(positionInText);
			if (applicationList == null) {
				applicationList = new ArrayList<PositionApplicationProxy>();
				links.put(positionInText, applicationList);
			}
			applicationList.add(application);
		}
		return links;
	}

	/**
	 * Returns the location of an hyperlink, accorded to the contents of the
	 * text.
	 * 
	 * @param hyperlinkPosition
	 *            the position of the hyperlink in the predicate
	 * @return the position of the hyperlink relatively to the managed styled
	 *         text contents
	 */
	public static Point getGlobalLocationAtOffset(
			TacticHyperlinkManager manager, Point hyperlinkPosition) {
		final int offset = manager.getCurrentOffset();
		hyperlinkPosition.x += offset;
		hyperlinkPosition.y += offset;
		return hyperlinkPosition;
	}
	
	public static String getControlSpacing(int nbControls, int nbTabs) {
		final StringBuilder sb = new StringBuilder();
		appendStrs(sb, nbControls, OBJ);
		appendTabs(sb, nbTabs);
		return sb.toString();
	}
	
	public static void appendTabs(StringBuilder sb, int nbTabs) {
			appendStrs(sb, nbTabs, TAB);
	}
	
	public static void appendStrs(StringBuilder sb, int nbTabs, String str) {
		for (int i = 0; i < nbTabs; i++) {
			sb.append(str);
		}
	}

	public static CaretListener getCaretListener(
			final ScrolledComposite scrolledComp, final int padding) {
		return new CaretListener() {

			@Override
			public void caretMoved(CaretEvent e) {
				final StyledText lText = (StyledText) e.widget;
				final int caretOffset = lText.getCaretOffset();
				final Point caretPos = lText.getLocationAtOffset(caretOffset);
				final Rectangle area = scrolledComp.getClientArea();
				final Point origin = scrolledComp.getOrigin();
				if (caretPos.x > origin.x + area.width - padding)
					origin.x = Math.max(origin.x, caretPos.x - area.width
							+ padding);
				if (caretPos.x < origin.x)
					origin.x = caretPos.x;
				if (caretPos.y > origin.y + area.height - padding)
					origin.y = Math.max(origin.y, caretPos.y + padding
							- area.height);
				if (caretPos.y < origin.y)
					origin.y = caretPos.y;
				scrolledComp.setOrigin(origin);
			}
		};

	}

}
