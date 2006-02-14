package org.eventb.internal.ui.obligationexplorer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;

public class ObligationContentExample {
	
	public static Collection<IProverSequent> discharged = new HashSet<IProverSequent>();
	
	private static IProverSequent genSeq(String s){
		String[] hypsStr = (s.split("[|]-")[0]).split(";;");
		String goalStr = s.split("[|]-")[1];
		
		FormulaFactory ff = Lib.ff;
		
		// Parsing
		Predicate[] hyps = new Predicate[hypsStr.length];
		Predicate goal = ff.parsePredicate(goalStr).getParsedPredicate();
		for (int i=0;i<hypsStr.length;i++){
			hyps[i] = ff.parsePredicate(hypsStr[i]).getParsedPredicate();
		}
		
		// Type check
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		for (int i=0;i<hyps.length;i++){
			ITypeCheckResult tcResult =  hyps[i].typeCheck(typeEnvironment);
			assert tcResult.isSuccess();
			typeEnvironment.addAll(tcResult.getInferredEnvironment());
		}
		ITypeCheckResult tcResult =  goal.typeCheck(typeEnvironment);
		assert tcResult.isSuccess();
		typeEnvironment.addAll(tcResult.getInferredEnvironment());
		
		// constructing sequent
		Set<Hypothesis> Hyps = Hypothesis.Hypotheses(hyps);
		
		IProverSequent Seq = new SimpleProverSequent(typeEnvironment,Hyps,goal);
		return Seq.selectHypotheses(Hyps);
	}

	private static String [] machineObligations = {
		"1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2",
		"1=1 ;; 2=2 |- 1=1 ∧(3=3 ⇒ 2=2 ∧3=3 ∧(∀x·x=1 ⇒ x = 1))",
		"x=1 ∨x=2 |- x < 3 ",
		"1=1 |-  ∃x·x=1"
	};

	private static String [] contextObligations = {
		"1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2",
		"1=1 ;; 2=2 |- 1=1 ∧(3=3 ⇒ 2=2 ∧3=3 ∧(∀x·x=1 ⇒ x = 1))",
		"x=1 ∨x=2 |- x < 3 ",
		"1=1 |-  ∃x·x=1"
	};

	public static IProverSequent [] getObligations(Object obj) {
		if (obj instanceof IMachine) {
			IProverSequent [] result = new IProverSequent[machineObligations.length];
			for (int i = 0; i < machineObligations.length; i++) {
				result[i] = genSeq(machineObligations[i]);
			}
			return result;
		}
		else if (obj instanceof IContext) {
			IProverSequent [] result = new IProverSequent[contextObligations.length];
			for (int i = 0; i < contextObligations.length; i++) {
				result[i] = genSeq(contextObligations[i]);
			}
			return result;
		}
		return new IProverSequent[0];
	}

	public static Image getImage(Object obj) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		System.out.println("Here " + obj);
		if (discharged.contains(obj))
			return registry.get(EventBImage.IMG_AXIOM);

		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public static void addToDischarged(IProverSequent ps) {
		System.out.println("Here " + ps.toString());
		discharged.add(ps);
	}
}
