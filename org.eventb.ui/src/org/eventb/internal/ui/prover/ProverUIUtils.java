package org.eventb.internal.ui.prover;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class ProverUIUtils {

	// Debug flag.
	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** ProverUI *** ";

	// public static final String HYPOTHESIS_PROOF_TACTIC_ID =
	// EventBUIPlugin.PLUGIN_ID
	// + ".hypothesisProofTactics";

	// public static final String GOAL_PROOF_TACTIC_ID =
	// EventBUIPlugin.PLUGIN_ID
	// + ".goalProofTactics";

	/**
	 * Print out the message if the <code>ProverUI.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debug(String message) {
		System.out.println(DEBUG_PREFIX + message);
	}

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

	public static Point getOperatorPosition(Formula subFormula) {
		if (subFormula instanceof QuantifiedPredicate) {
			// TODO Find the character representing the quantified operator
			return new Point(0, 1);
		}
		if (subFormula instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) subFormula;
			SourceLocation leftLocation = bPred.getLeft().getSourceLocation();
			SourceLocation rightLocation = bPred.getRight().getSourceLocation();
			return new Point(leftLocation.getEnd() + 1, rightLocation
					.getStart() - 1);
		}
		if (Tactics.isFunOvrApp((Expression) subFormula)) {
			Expression left = ((BinaryExpression) subFormula).getLeft();
			Expression[] children = ((AssociativeExpression) left)
					.getChildren();
			Expression last = children[children.length - 1];
			Expression secondLast = children[children.length - 2];
			return new Point(secondLast.getSourceLocation().getEnd() + 1, last
					.getSourceLocation().getStart());
		}
		return new Point(0, 1);// The first character
	}

	// private static Collection<HypothesisTacticUI> hypothesisTactics = null;

	/**
	 * Getting the list of tactics that are applicable to a hypothesis.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis
	 * @return a list of tactic symbols (strings)
	 */
	// public static Collection<HypothesisTacticUI> getApplicableToHypothesis(
	// IProofTreeNode node, Hypothesis hyp) {
	// if (hypothesisTactics == null)
	// internalGetApplicableToHypothesis();
	//
	// Collection<HypothesisTacticUI> result = new
	// ArrayList<HypothesisTacticUI>();
	//
	// for (HypothesisTacticUI tactic : hypothesisTactics) {
	// if (tactic.isApplicable(node, hyp))
	// result.add(tactic);
	// }
	// return result;
	// }
	// private static void internalGetApplicableToHypothesis() {
	// IExtensionRegistry registry = Platform.getExtensionRegistry();
	// IExtensionPoint extensionPoint = registry
	// .getExtensionPoint(HYPOTHESIS_PROOF_TACTIC_ID);
	// IExtension[] extensions = extensionPoint.getExtensions();
	//
	// hypothesisTactics = new ArrayList<HypothesisTacticUI>();
	//
	// for (IExtension extension : extensions) {
	// IConfigurationElement[] elements = extension
	// .getConfigurationElements();
	// for (IConfigurationElement element : elements) {
	// String name = element.getName();
	//
	// if (name.equals("tactic")) {
	// String namespace = element.getContributor().getName();
	// Bundle bundle = Platform.getBundle(namespace);
	// try {
	// String ID = element.getAttribute("id");
	// String icon = element.getAttribute("icon");
	//
	// String key = namespace + ":" + icon;
	// ImageRegistry imageRegistry = EventBUIPlugin
	// .getDefault().getImageRegistry();
	//
	// Image image = imageRegistry.get(key);
	//
	// if (image == null) {
	// EventBImage.registerImage(imageRegistry, key,
	// namespace, icon);
	// image = imageRegistry.get(key);
	// }
	//
	// Class clazz = bundle.loadClass(element
	// .getAttribute("class"));
	//
	// Class classObject = getSubclass(clazz,
	// IHypothesisTactic.class);
	// Constructor constructor = classObject
	// .getConstructor(new Class[0]);
	//
	// String hint = element.getAttribute("hint");
	// HypothesisTacticUI tactic = new HypothesisTacticUI(ID,
	// image, (IHypothesisTactic) constructor
	// .newInstance(new Object[0]), hint);
	// hypothesisTactics.add(tactic);
	//
	// } catch (Exception e) {
	// // TODO Exception handle
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// }
	// }
	// Code extracted to suppress spurious warning about unsafe type cast.
	// @SuppressWarnings("unchecked")
	// private static Class getSubclass(Class clazz, Class subClass) {
	// return clazz.asSubclass(subClass);
	// }
	// private static Collection<GoalTacticUI> goalTactics = null;
	// public static Collection<GoalTacticUI> getApplicableToGoal(
	// IProofTreeNode node) {
	// if (goalTactics == null) {
	// internalGetApplicableToGoal();
	// }
	//
	// Collection<GoalTacticUI> result = new ArrayList<GoalTacticUI>();
	//
	// for (GoalTacticUI goalTactic : goalTactics) {
	// if (goalTactic.isApplicable(node))
	// result.add(goalTactic);
	// }
	// return result;
	// }
	// private static void internalGetApplicableToGoal() {
	// IExtensionRegistry registry = Platform.getExtensionRegistry();
	// IExtensionPoint extensionPoint = registry
	// .getExtensionPoint(GOAL_PROOF_TACTIC_ID);
	// IExtension[] extensions = extensionPoint.getExtensions();
	//
	// goalTactics = new ArrayList<GoalTacticUI>();
	//
	// for (IExtension extension : extensions) {
	// IConfigurationElement[] elements = extension
	// .getConfigurationElements();
	// for (IConfigurationElement element : elements) {
	// String name = element.getName();
	//
	// if (name.equals("tactic")) {
	// String namespace = element.getContributor().getName();
	// Bundle bundle = Platform.getBundle(namespace);
	// try {
	// String ID = element.getAttribute("id");
	// String icon = element.getAttribute("icon");
	// String key = namespace + ":" + icon;
	//
	// ImageRegistry imageRegistry = EventBUIPlugin
	// .getDefault().getImageRegistry();
	//
	// Image image = imageRegistry.get(key);
	//
	// if (image == null) {
	// EventBImage.registerImage(imageRegistry, key,
	// namespace, icon);
	// image = imageRegistry.get(key);
	// }
	//
	// Class clazz = bundle.loadClass(element
	// .getAttribute("class"));
	//
	// Class classObject = getSubclass(clazz,
	// IGoalTactic.class);
	// Constructor constructor = classObject
	// .getConstructor(new Class[0]);
	//
	// String hint = element.getAttribute("hint");
	// GoalTacticUI tactic = new GoalTacticUI(ID, image,
	// (IGoalTactic) constructor
	// .newInstance(new Object[0]), hint);
	// goalTactics.add(tactic);
	//
	// } catch (Exception e) {
	// // TODO Exception handle
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// }
}
