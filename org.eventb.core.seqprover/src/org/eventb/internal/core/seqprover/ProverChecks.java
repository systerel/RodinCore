/**
 * 
 */
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * This class contains a collection of static methods that clients can use to check that the input they provide to
 * the sequent prover fulfills the expectations the sequent prover has on them. 
 * 
 * <p>
 * The intention of this class is to make explicit (programatically) the assumptions that the sequent prover makes on
 * the inputs its gets from its environment. The methods included here may be used by clients in test cases and assertion
 * checks.
 * </p>
 * 
 * <p>
 * The methods return debug trace messages in case one of their checks fail.
 * </p>
 * 
 * <p>
 * For the moment clients are requsted not to depend on the code here since it is currently not entirely stable.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class ProverChecks {
	
	// *******************************************************************************************
	// Debug trace setup code :
	// *******************************************************************************************

	
	/**
	 * Debug flag for <code>PROVER_CHECKS_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private static void checkFailure(String message){
		if (DEBUG)
			System.out.println("Prover Check Failure: " + message);
	}

	// *******************************************************************************************
	// Public Methods :
	// *******************************************************************************************


	/**
	 * Checks that a formula is well formed, and type checked.
	 * 
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @param formula
	 * 			The formula to check
	 * @return
	 * 			<code>true</code> iff the given formula is well formed and type checked
	 */
	public static boolean checkFormula(Formula formula){
		if (! formula.isWellFormed()) {
			checkFailure(" Formula " + formula + " is not well formed.");
			return false;
		}
		if (! formula.isTypeChecked()) {
			checkFailure(" Formula " + formula + " is not type checked.");
			return false;
		}
		return true;
	}

	/**
	 * Checks that all free identifiers defined in a predicate are present and aggree in type with the
	 * identifiers present in the given type environment.
	 * 
	 * <p>
	 * It is assumed that the given predicate is well formed and type checked (i.e. that the {@link #checkFormula(Formula)}
	 * method returns <code>true</code>) before calling this method.
	 * </p>
	 * 
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @param formula
	 * 			The predicate to check
	 * @return
	 * 			<code>true</code> iff the given predicate is well formed and well typed
	 */
	public static boolean checkTyping(Formula formula, ITypeEnvironment typeEnv){

		for (FreeIdentifier freeIdent : formula.getFreeIdentifiers()) {
			if (! typeEnv.contains(freeIdent.getName())) {
				checkFailure(" Free Identifier " + freeIdent.getName() +
						" in formula " + formula +
						" is not defined in the type environment " + typeEnv);
				return false;
			}
			if (! typeEnv.getType(freeIdent.getName()).equals(freeIdent.getType())){
				checkFailure(" Free Identifier " + freeIdent.getName() +
						" in formula " + formula +
						" does not have the same type as in the type environment " + typeEnv);
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the assumptions made on a given sequent by the sequent prover.
	 * 
	 * <p>
	 * The following checks are performed:
	 * <ul> 
	 * <li> All predicates present in this sequent are well formed, type checked, and well
	 * typed with respect to the type environment of this sequent.
	 * <li> All selected and hidden hypotheses are contained in the hypotheses and that
	 * they are disjoint.
	 * </ul>
	 * </p>
	 * 
	 * @param seq
	 * 			the sequent to check
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */	
	public static boolean checkSequent(IProverSequent seq){
		if (! checkSequentPredicates(seq)) return false;
		if (! checkSequentSelection(seq)) return false;
		return true;
	}

	/**
	 * Checks that all predicates in the given rule are well formed, type checked, and well
	 * typed with respect to the type environment that can be generated from the rule.
	 * 
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */
	public static boolean checkRule(IProofRule rule){

		// This check occurs in two passes:
		//
		// * In the first pass the type environment that the rule anticipates from the sequent is built. While
		//		building this type environment it is also checked that this type environment it consistent (i.e. that
		// 		all free identifiers encountered have a unique type), and that all predicates are well formed and type
		//		checked. It is also checked that a goal dependent rule has no goal independent antecedents.
		// * In the second pass it is checked that all free variables contained in all predicates are present and agree
		//		in type with the type environment that is relevent in their respective contexts.
		//
		//	Two passes through the antecedents of the rule are needed since the type environment that the rule expects
		//  from its consequent sequent is not known a priori since this information is scattered across all antecedents.
		// 	This type environment is needed to make sure that the added free identifiers in each antecedent are fresh with
		//	respect to the type environment that the rule anticipates.

		// Pass 1 :
		// The type environment anticipated by the rule.
		final ITypeEnvironment ruleTypeEnv = Lib.makeTypeEnvironment();
		final  boolean goalIndependant = (rule.getGoal() == null);


		// Check the goal and all needed hyps and add to the rule type environment from them
		if (! goalIndependant){
			if (! checkFormula(rule.getGoal())) return false;
			if (! checkAndAddToTypeEnvironment(ruleTypeEnv, rule.getGoal().getFreeIdentifiers()))
				return false;
		}

		for (Predicate hyp : rule.getNeededHyps()) {
			if (! checkFormula(hyp)) return false;
			if (! checkAndAddToTypeEnvironment(ruleTypeEnv, hyp.getFreeIdentifiers()))
				return false;			
		}

		// Check the predicates contained in the antecedent and to the rule type environment from them
		for (IAntecedent antecedent : rule.getAntecedents()){

			// Check the antecedent goal and add to the rule type environment from it			
			if (antecedent.getGoal() != null){
				if (! checkFormula(antecedent.getGoal())) return false;
				// Get the free identifiers that are to be added to the rule type environment
				List<FreeIdentifier>freeIdents = new ArrayList<FreeIdentifier> (Arrays.asList(antecedent.getGoal().getFreeIdentifiers()));
				freeIdents.removeAll(Arrays.asList(antecedent.getAddedFreeIdents()));
				if (! checkAndAddToTypeEnvironment(ruleTypeEnv, freeIdents))
					return false;
			} else {
				if (! goalIndependant){
					checkFailure(" Antecedent " + antecedent +
							" is goal independant, but rule " + rule + " is not");
					return false;
				}
			}

			// Check all added hyps and add to the rule type environment from them			
			for (Predicate hyp : antecedent.getAddedHyps()) {
				if (! checkFormula(hyp)) return false;
				// Get the free identifiers that are to be added to the rule type environment
				List<FreeIdentifier>freeIdents = new ArrayList<FreeIdentifier> (Arrays.asList(hyp.getFreeIdentifiers()));
				freeIdents.removeAll(Arrays.asList(antecedent.getAddedFreeIdents()));
				if (! checkAndAddToTypeEnvironment(ruleTypeEnv, freeIdents))
					return false;
			}
			// Check all hyp actions and add to the rule type environment from them
			for (IHypAction hypAction : antecedent.getHypAction()){
				if (hypAction instanceof ISelectionHypAction) {
					ISelectionHypAction selHypAction = (ISelectionHypAction) hypAction;
					// Check action type
					if (! (selHypAction.getActionType().equals(IHypAction.ISelectionHypAction.SELECT_ACTION_TYPE) ||
							selHypAction.getActionType().equals(IHypAction.ISelectionHypAction.DESELECT_ACTION_TYPE) ||
							selHypAction.getActionType().equals(IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE) ||
							selHypAction.getActionType().equals(IHypAction.ISelectionHypAction.SHOW_ACTION_TYPE)
					)){
						checkFailure(" Hypothesis selection hyp action " + selHypAction + " in antecedent " + antecedent +
								" has incorrect action type " + selHypAction.getActionType());
						return false;
					}
					// Check hyps
					for (Predicate hyp : selHypAction.getHyps()){
						if (! checkFormula(hyp)) return false;
						// Get the free identifiers that are to be added to the rule type environment
						List<FreeIdentifier>freeIdents = new ArrayList<FreeIdentifier> (Arrays.asList(hyp.getFreeIdentifiers()));
						freeIdents.removeAll(Arrays.asList(antecedent.getAddedFreeIdents()));
						if (! checkAndAddToTypeEnvironment(ruleTypeEnv, freeIdents))
							return false;
					}
				} else if (hypAction instanceof IForwardInfHypAction) {
					IForwardInfHypAction fwdHypAction = (IForwardInfHypAction) hypAction;
					// Check action type
					if (! fwdHypAction.getActionType().equals(IHypAction.IForwardInfHypAction.ACTION_TYPE)){
						checkFailure(" Forward inference " + fwdHypAction + " in antecedent " + antecedent +
								" has incorrect action type " + fwdHypAction.getActionType());
						return false;
					}
					// Check required hyps
					for (Predicate hyp : fwdHypAction.getHyps()){
						if (! checkFormula(hyp)) return false;
						// Get the free identifiers that are to be added to the rule type environment
						List<FreeIdentifier>freeIdents = new ArrayList<FreeIdentifier> (Arrays.asList(hyp.getFreeIdentifiers()));
						freeIdents.removeAll(Arrays.asList(antecedent.getAddedFreeIdents()));
						if (! checkAndAddToTypeEnvironment(ruleTypeEnv, freeIdents))
							return false;
					}
					// Check inferred hyps
					for (Predicate hyp : fwdHypAction.getInferredHyps()){
						if (! checkFormula(hyp)) return false;
						// Get the free identifiers that are to be added to the rule type environment
						List<FreeIdentifier>freeIdents = new ArrayList<FreeIdentifier> (Arrays.asList(hyp.getFreeIdentifiers()));
						freeIdents.removeAll(Arrays.asList(antecedent.getAddedFreeIdents()));
						freeIdents.removeAll(Arrays.asList(fwdHypAction.getAddedFreeIdents()));
						if (! checkAndAddToTypeEnvironment(ruleTypeEnv, freeIdents))
							return false;
					}

				} else {
					// unknown hyp action type
					checkFailure(" Unknown hypothesis action " + hypAction + " in antecedent " + antecedent);
					return false;
				}

			}
		}

		// At this point the rule type environment contains all the free identifiers that are used by the rule

		// Check that all predicates contained in the antecedents are well typed with the relevant type environments
		for (IAntecedent antecedent : rule.getAntecedents()){
			// Compute the antecedent type environment
			final ITypeEnvironment antecedentTypeEnv = ruleTypeEnv.clone();
			if (! checkFreshAndAddToTypeEnvironment(antecedentTypeEnv, antecedent.getAddedFreeIdents()))
				return false;

			// Check the goal
			if (antecedent.getGoal() != null){
				if (! checkTyping(antecedent.getGoal(), antecedentTypeEnv))
					return false;
			}
			// Check all added hypotheses
			if (! checkTyping(antecedent.getAddedHyps(), antecedentTypeEnv))
				return false;

			// Check all hyp actions

			// The type environment that is progressively built using forward inferences
			// Note : since forward inferences are skippable, the typing check only assumes
			// the original antecedent type environment, but the non freeness check needs to
			// check that all added free identifiers are unique.
			final ITypeEnvironment fwdInfTypeEnv = antecedentTypeEnv.clone();

			for (IHypAction hypAction : antecedent.getHypAction()){
				if (hypAction instanceof ISelectionHypAction) {
					ISelectionHypAction selHypAction = (ISelectionHypAction) hypAction;
					if (! checkTyping(selHypAction.getHyps(), antecedentTypeEnv))
						return false;
				} else if (hypAction instanceof IForwardInfHypAction) {
					IForwardInfHypAction fwdHypAction = (IForwardInfHypAction) hypAction;
					if (! checkTyping(fwdHypAction.getHyps(), antecedentTypeEnv))
						return false;
					if (! checkFreshAndAddToTypeEnvironment(fwdInfTypeEnv, fwdHypAction.getAddedFreeIdents()))
						return false;
					// The type environment that should be used for the inferred hypotheses.
					final ITypeEnvironment infHypsTypeEnv = antecedentTypeEnv.clone();
					// no freeness check needed since a stronger check has previously been done.
					infHypsTypeEnv.addAll(fwdHypAction.getAddedFreeIdents());
					if (! checkTyping(fwdHypAction.getInferredHyps(), infHypsTypeEnv))
						return false;
				}
			}			
		}
		return true;
	}
	
	/**
	 * Generates the logical justificatons for the given proof rule.
	 * 
	 * <p>
	 * The justifications of a proof rule are expressed as a list of sequents. The first justifcation in this
	 * list is the <em>main</em> justification for the rule ans is always present. The rest of the justifications
	 * relate to {@link IForwardInfHypAction}s and occur in the same order of the antecedents, and within each antecedent,
	 * in the same order which they appear. 
	 * </p>
	 * 
	 * <p>
	 * It is assumed that the given rule passes all tests in {@link #checkRule(IProofRule)}.
	 * </p>
	 * 
	 * <p>
	 * The sequent prover, and clients providing Reasoners may use this method to generate logical justifications for the proof 
	 * rules they generate in order to re-check their logical content independently.
	 * </p>
	 * 
	 * <p>
	 * WARINING : The rturn tyoe of this method is subject to change in the near future. 
	 * </p>
	 * 
	 * @param rule
	 * 			the proof rule for which justifications to be generated.
	 * @return
	 * 			the generated justifications.
	 * TODO : Generate WD justifications too
	 * TODO : Maybe choose a better structure for returning justifications 
	 */
	public static List<IProverSequent> genRuleJustifications(IProofRule rule){

		List<IProverSequent> justifications = new ArrayList<IProverSequent>();

		final ITypeEnvironment typeEnv = Lib.makeTypeEnvironment();

		// Get G_r and H_r
		Predicate g_r = rule.getGoal();
		boolean goalIndependent = (rule.getGoal() == null);
		if (goalIndependent){
			g_r = Lib.False;
		}

		Set<Predicate> h_r = rule.getNeededHyps();

		// Enrich type environment using Gr and Hr
		for (Predicate hyp : h_r) {
			typeEnv.addAll(hyp.getFreeIdentifiers());
		}


		final IAntecedent[] antecedents = rule.getAntecedents();
		final Predicate[] antecedentParts = new Predicate[antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {

			final FreeIdentifier[] i_a = antecedents[i].getAddedFreeIdents();
			final Set<Predicate> h_a = antecedents[i].getAddedHyps();
			Predicate g_a = antecedents[i].getGoal();
			if (g_a == null){
				if (goalIndependent){
					g_a = Lib.False;
				}
				else
				{
					return null;
				}
			}

			antecedentParts[i] = Lib.makeUnivQuant(i_a, Lib.makeImpl(h_a, g_a));
			typeEnv.addAll(antecedentParts[i].getFreeIdentifiers());

			// Compute the forward inference justifications

			for (IHypAction hypAction : antecedents[i].getHypAction()) {
				if (hypAction instanceof IForwardInfHypAction) {
					IForwardInfHypAction fwdInf = (IForwardInfHypAction) hypAction;
					// Set<Predicate> hyps = union(h_r,h_a,fwdInf.getHyps());
					Set<Predicate> hyps = new LinkedHashSet<Predicate>(h_r);
					hyps.addAll(h_a);
					hyps.addAll(fwdInf.getHyps());
					Predicate goal = Lib.makeExQuant(fwdInf.getAddedFreeIdents(), Lib.makeConj(fwdInf.getInferredHyps()));
					ITypeEnvironment localTypeEnv = Lib.makeTypeEnvironment();
					for (Predicate hyp : h_r) {
						localTypeEnv.addAll(hyp.getFreeIdentifiers());
					}
					localTypeEnv.addAll(goal.getFreeIdentifiers());
					justifications.add(ProverFactory.makeSequent(localTypeEnv, hyps, hyps, goal));
				}
			}
		}

		Predicate goal = Lib.makeImpl(Arrays.asList(antecedentParts), g_r);

		typeEnv.addAll(goal.getFreeIdentifiers());

		final IProverSequent mainJustification = ProverFactory.makeSequent(typeEnv , h_r, h_r, goal);
		justifications.add(0, mainJustification);
		return justifications;
	}	

	// *******************************************************************************************
	// Provate Methods :
	// *******************************************************************************************

	/**
	 * Checks that all predicates in this sequent are well formed, type checked, and well
	 * typed with respect to the type environment of this sequent.
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @param seq
	 * 			the sequent to check
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */
	private static boolean checkSequentPredicates(IProverSequent seq){
		final ITypeEnvironment typeEnv = seq.typeEnvironment();
		if (! checkTyping(seq.goal(), typeEnv)) return false;
		for (Predicate hyp : seq.hypIterable()) {
			if (! checkTyping(hyp,typeEnv)) return false;
		}	
		return true;
	}

	/**
	 * Checks that all selected and hidden hypotheses are contained in the hypotheses and that
	 * they are disjoint.
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @param seq
	 * 			the sequent to check
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */
	private static boolean checkSequentSelection(IProverSequent seq){
		for (Predicate selHyp : seq.selectedHypIterable()){
			if (! seq.containsHypothesis(selHyp)){
				checkFailure(" Hypothesis " + selHyp +
				" is selected, but not pressent.");
				return false;
			}
			if (seq.isHidden(selHyp)){
				checkFailure(" Hypothesis " + selHyp +
				" is selected, and also hidden.");
				return false;
			}
		}
		for (Predicate hidHyp : seq.hiddenHypIterable()){
			if (! seq.containsHypothesis(hidHyp)){
				checkFailure(" Hypothesis " + hidHyp +
				" is hidden, but not pressent.");
				return false;
			}
		}
		return true;
	}


	/**
	 * Collection version of {@link #checkTyping(Formula, ITypeEnvironment)}
	 */
	private static boolean checkTyping(Collection<? extends Formula> formulae, ITypeEnvironment typeEnv){
		for (Formula formula : formulae) {
			if (!checkTyping(formula, typeEnv)) return false;
		}
		return true;
	}

	/**
	 * Checks that the given free identifiers are well formed, type checked, and can be successfully added to the
	 * given type environment (i.e. if the given free identifier already occurs in the type environment, it aggrees
	 * on its type).
	 * 
	 * <p>
	 * If all checks are successful, this method returns <code>true</code> and adds the given free identifiers
	 * to the given type environemnt. If a check fails, the method returns <code>false</code> it means one of the checks 
	 * failed, and only those free identifiers that could successfully be added are adeed to the type environment. The free
	 * identifiers are processed in the order in which they occur.
	 * <p>
	 *  
	 *  
	 * @param typeEnv
	 * 			the given type environment (may be modified).
	 * @param freeIdents
	 * 			the given free identifiers.
	 * @return
	 * 			<code>true</code> iff all checks succeed
	 */
	private static boolean checkAndAddToTypeEnvironment(ITypeEnvironment typeEnv, FreeIdentifier...freeIdents){
		for (FreeIdentifier freeIdent : freeIdents) {
			if (! checkFormula(freeIdent)) return false;

			if (typeEnv.contains(freeIdent.getName()) && ! typeEnv.getType(freeIdent.getName()).equals(freeIdent.getType())){
				checkFailure(" Free Identifier " + freeIdent.getName() +
						" with type " + freeIdent.getType() +
						" does not have the same type as in the type environment " + typeEnv);
				return false;
			}
			typeEnv.add(freeIdent);
		}
		return true;
	}

	/**
	 * Similar to {@link #checkAndAddToTypeEnvironment(ITypeEnvironment, FreeIdentifier[])} but also checks that the
	 * free identifiers to add are not already present in the type environment.
	 */
	private static boolean checkFreshAndAddToTypeEnvironment(ITypeEnvironment typeEnv, FreeIdentifier...freeIdents){
		for (FreeIdentifier freeIdent : freeIdents) {
			if (! checkFormula(freeIdent)) return false;

			if (typeEnv.contains(freeIdent.getName())){
				checkFailure(" Free Identifier " + freeIdent.getName() +
						" with type " + freeIdent.getType() +
						" already present in the type environment " + typeEnv);
				return false;
			}
			typeEnv.add(freeIdent);
		}
		return true;
	}



	/**
	 * Identical to {@link #checkAndAddToTypeEnvironment(ITypeEnvironment, FreeIdentifier[])}, except that this method
	 * accepts a list of free identifiers instead of an array.
	 */
	private static boolean checkAndAddToTypeEnvironment(ITypeEnvironment typeEnv, List<FreeIdentifier> freeIdents){
		FreeIdentifier[] freeIdentsArray = new FreeIdentifier[freeIdents.size()];
		freeIdentsArray = freeIdents.toArray(freeIdentsArray);
		return checkAndAddToTypeEnvironment(typeEnv, freeIdentsArray);
	}

}
