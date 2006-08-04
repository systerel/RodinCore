package org.eventb.internal.ui.prover;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.prover.hypothesisTactics.HypothesisTacticUI;
import org.eventb.ui.prover.IHypothesisTactic;
import org.osgi.framework.Bundle;

public class ProverUIUtils {

	public static final String CONJI_SYMBOL = "\u2227";

	public static final String IMPI_SYMBOL = "\u21d2";

	public static final String ALLI_SYMBOL = "\u2200";

	public static final String EXI_SYMBOL = "\u2203";

	public static final String NEG_SYMBOL = "\u00ac";

	public static final String ALLF_SYMBOL = "\u2200";

	public static final String CONJD_SYMBOL = "\u2227";

	public static final String IMPD1_SYMBOL = "\u21d2";

	public static final String IMPD2_SYMBOL = "ip1";

	public static final String DISJE_SYMBOL = "\u22c1";

	public static final String EXF_SYMBOL = "\u2203";

	public static final String EQE1_SYMBOL = "eh";

	public static final String EQE2_SYMBOL = "he";

	public static final String HYPOTHESIS_PROOF_TACTIC_ID = EventBUIPlugin.PLUGIN_ID
			+ ".hypothesisProofTactics";

	public static final String FALSIFY_SYMBOL = "ct";

	/**
	 * Print out the message if the <code>ProverUI.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugProverUI(String message) {
		if (ProverUI.DEBUG)
			System.out.println(message);
	}

	/**
	 * Getting the list of tactics that are applicable to the current goal.
	 * <p>
	 * 
	 * @param goal
	 *            the current goal
	 * @return a list of tactic symbols (strings)
	 */
	public static List<String> getApplicableToGoal(Predicate goal) {
		List<String> names = new ArrayList<String>();

		if (Tactics.impI_applicable(goal))
			names.add(IMPI_SYMBOL);
		if (Tactics.conjI_applicable(goal))
			names.add(CONJI_SYMBOL);
		if (Tactics.allI_applicable(goal))
			names.add(ALLI_SYMBOL);
		if (Tactics.exI_applicable(goal))
			names.add(EXI_SYMBOL);
		if (Tactics.removeNegGoal_applicable(goal))
			names.add(NEG_SYMBOL);
		if (Tactics.disjToImpGoal_applicable(goal))
			names.add(DISJE_SYMBOL);
		// Extra tactics applicable to goal should be added here.
		return names;
	}

	private static Collection<HypothesisTacticUI> hypothesisTactics = null;

	/**
	 * Getting the list of tactics that are applicable to a hypothesis.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis
	 * @return a list of tactic symbols (strings)
	 */
	public static Collection<HypothesisTacticUI> getApplicableToHypothesis(
			IProofTreeNode node, Hypothesis hyp) {
		if (hypothesisTactics == null)
			internalGetApplicableToHypothesis();

		Collection<HypothesisTacticUI> result = new ArrayList<HypothesisTacticUI>();

		for (HypothesisTacticUI tactic : hypothesisTactics) {
			if (tactic.isApplicable(node, hyp))
				result.add(tactic);
		}
		return result;
		//
		// if (Tactics.eqE_applicable(hyp)) {
		// names.add(EQE1_SYMBOL);
		// names.add(EQE2_SYMBOL);
		// }
	}

	private static void internalGetApplicableToHypothesis() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(HYPOTHESIS_PROOF_TACTIC_ID);
		IExtension[] extensions = extensionPoint.getExtensions();

		hypothesisTactics = new ArrayList<HypothesisTacticUI>();

		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				String name = element.getName();

				if (name.equals("tactic")) {
					Bundle bundle = Platform.getBundle(element.getNamespace());
					try {
						String ID = element.getAttribute("id");
						String icon = element.getAttribute("icon");
						ImageRegistry imageRegistry = EventBUIPlugin
								.getDefault().getImageRegistry();

						ImageDescriptor desc = EventBImage
								.getImageDescriptor(icon);
						imageRegistry.put(icon, desc);

						Image image = imageRegistry.get(icon);

						Class clazz = bundle.loadClass(element
								.getAttribute("class"));

						Class classObject = getSubclass(clazz,
								IHypothesisTactic.class);
						Constructor constructor = classObject
								.getConstructor(new Class[0]);
						
						String hint = element.getAttribute("hint");
						HypothesisTacticUI tactic = new HypothesisTacticUI(ID,
								image, (IHypothesisTactic) constructor
										.newInstance(new Object[0]), hint);
						hypothesisTactics.add(tactic);

					} catch (Exception e) {
						// TODO Exception handle
						e.printStackTrace();
					}
				}
			}

		}
	} // Code extracted to suppress spurious warning about unsafe type cast.

	@SuppressWarnings("unchecked")
	private static Class getSubclass(Class clazz, Class subClass) {
		return clazz.asSubclass(subClass);
	}
}
