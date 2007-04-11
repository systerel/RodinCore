/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import java.util.EnumMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author Stefan Hallerstede
 *
 */
public class GraphProblemTest extends TestCase {
	
	private static class Spec implements Comparable<Spec> {
		
		public final GraphProblem problem;
		public final int arity;
		
		public Spec(final GraphProblem problem, final int arity) {
			this.problem = problem;
			this.arity = arity;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Spec && problem.equals(((Spec) obj).problem);
		}
		@Override
		public int hashCode() {
			return problem.hashCode();
		}
		@Override
		public String toString() {
			return problem.toString() + "/" + arity;
		}
		public int compareTo(Spec o) {
			return problem.compareTo(o.problem);
		}
	}
	
	private static Spec spec(final GraphProblem problem, final int arity) {
		return new Spec(problem, arity);
	}
	
	private static Spec[] specs = new Spec[] {
			spec(GraphProblem.InvalidIdentifierError, 1),
			spec(GraphProblem.InvalidIdentifierSpacesError, 1),
			spec(GraphProblem.AbstractContextNotFoundError, 1),
			spec(GraphProblem.SeenContextNotFoundError, 1),
			spec(GraphProblem.TooManyAbstractMachinesError, 0),
			spec(GraphProblem.AbstractMachineNotFoundError, 1),
			spec(GraphProblem.AbstractEventNotFoundError, 1),
			spec(GraphProblem.AbstractEventNotRefinedError, 1),
			spec(GraphProblem.AbstractEventLabelConflictWarning, 1),
			spec(GraphProblem.EventMergeSplitError, 1),
			spec(GraphProblem.EventMergeMergeError, 1),
			spec(GraphProblem.EventInheritedMergeSplitError, 1),
			spec(GraphProblem.EventMergeVariableTypeError, 1),
			spec(GraphProblem.EventMergeActionError, 0),
			spec(GraphProblem.EventRefinementError, 0),
			spec(GraphProblem.MachineWithoutInitialisationError, 0),
			spec(GraphProblem.InitialisationRefinedError, 0),
			spec(GraphProblem.InitialisationVariableError, 0),
			spec(GraphProblem.InitialisationGuardError, 0),
			spec(GraphProblem.InitialisationActionRHSError, 1),
			spec(GraphProblem.InitialisationIncompleteWarning, 1),
			spec(GraphProblem.CarrierSetNameImportConflictError, 2),
			spec(GraphProblem.CarrierSetNameImportConflictWarning, 2),
			spec(GraphProblem.CarrierSetNameConflictError, 1),
			spec(GraphProblem.CarrierSetNameConflictWarning, 1),
			spec(GraphProblem.ConstantNameImportConflictError, 2),
			spec(GraphProblem.ConstantNameImportConflictWarning, 2),
			spec(GraphProblem.ConstantNameConflictError, 1),
			spec(GraphProblem.ConstantNameConflictWarning, 1),
			spec(GraphProblem.VariableNameImportConflictError, 2),
			spec(GraphProblem.VariableNameImportConflictWarning, 2),
			spec(GraphProblem.VariableNameConflictError, 1),
			spec(GraphProblem.VariableNameConflictWarning, 1),
			spec(GraphProblem.EventVariableNameConflictError, 1),
			spec(GraphProblem.EventVariableNameConflictWarning, 1),
			spec(GraphProblem.UntypedCarrierSetError, 1),
			spec(GraphProblem.UntypedConstantError, 1),
			spec(GraphProblem.UntypedVariableError, 1),
			spec(GraphProblem.UntypedIdentifierError, 1),
			spec(GraphProblem.UndeclaredFreeIdentifierError, 1),
			spec(GraphProblem.FreeIdentifierFaultyDeclError, 1),
			spec(GraphProblem.VariantFreeIdentifierError, 1),
			spec(GraphProblem.AxiomFreeIdentifierError, 1),
			spec(GraphProblem.TheoremFreeIdentifierError, 1),
			spec(GraphProblem.InvariantFreeIdentifierError, 1),
			spec(GraphProblem.GuardFreeIdentifierError, 1),
			spec(GraphProblem.ActionFreeIdentifierError, 1),
			spec(GraphProblem.ActionDisjointLHSError, 0),
			spec(GraphProblem.WitnessFreeIdentifierError, 1),
			spec(GraphProblem.InvalidVariantTypeError, 1),
			spec(GraphProblem.TooManyVariantsError, 0),
			spec(GraphProblem.ConvergentFaultyConvergenceWarning, 1),
			spec(GraphProblem.OrdinaryFaultyConvergenceWarning, 1),
			spec(GraphProblem.AnticipatedFaultyConvergenceWarning, 1),
			spec(GraphProblem.NoConvergentEventButVariantWarning, 0),
			spec(GraphProblem.ConvergentEventNoVariantWarning, 1),
			spec(GraphProblem.InitialisationNotOrdinaryWarning, 0),
			spec(GraphProblem.AxiomLabelConflictError, 1),			
			spec(GraphProblem.AxiomLabelConflictWarning, 1),
			spec(GraphProblem.TheoremLabelConflictError, 1),
			spec(GraphProblem.TheoremLabelConflictWarning, 1),
			spec(GraphProblem.InvariantLabelConflictError, 1),
			spec(GraphProblem.InvariantLabelConflictWarning, 1),
			spec(GraphProblem.EventLabelConflictError, 1),
			spec(GraphProblem.EventLabelConflictWarning, 1),
			spec(GraphProblem.GuardLabelConflictError, 1),
			spec(GraphProblem.GuardLabelConflictWarning, 1),
			spec(GraphProblem.ActionLabelConflictError, 1),
			spec(GraphProblem.ActionLabelConflictWarning, 1),
			spec(GraphProblem.WitnessLabelConflictError, 1),
			spec(GraphProblem.WitnessLabelConflictWarning, 1),
			spec(GraphProblem.WitnessLabelMissingWarning, 1),			
			spec(GraphProblem.WitnessLabelNeedLessError, 1),
			spec(GraphProblem.ContextOnlyInAbstractMachineWarning, 1),
			spec(GraphProblem.WasAbstractEventLabelWarning, 1),
			spec(GraphProblem.InconsistentEventLabelWarning, 1),
			spec(GraphProblem.VariableHasDisappearedError, 1),
			spec(GraphProblem.DisappearedVariableRedeclaredError, 1),
			spec(GraphProblem.VariableIsLocalInAbstractMachineError, 2),
			spec(GraphProblem.AssignedIdentifierNotVariableError, 1),
			spec(GraphProblem.LocalVariableChangedTypeError, 3),
			spec(GraphProblem.AssignmentToLocalVariableError, 1)
	};
	
	private static Map<GraphProblem, Spec> specMap = 
		new EnumMap<GraphProblem, Spec>(GraphProblem.class);
	static {
		for (Spec spec : specs) {
			specMap.put(spec.problem, spec);
		}
	}
	
	/**
	 * check whether the messages loaded from the properties file are complete
	 * and take the correct number of parameters.
	 */
	public void testMessages() throws Exception {
		assertEquals("wrong number of messages", specs.length, GraphProblem.values().length);
		for (Spec spec : specs) {
			assertEquals("wrong number of arguments", spec.arity, spec.problem.getArity());
		}
	}
	
	public static boolean check(IRodinFile file) throws CoreException {
		boolean ok = true;
		IMarker[] markers = 
			file.getResource().findMarkers(
					RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
					true, 
					IResource.DEPTH_INFINITE);
		for (IMarker marker : markers) {
			String errorCode = RodinMarkerUtil.getErrorCode(marker);
			GraphProblem problem;
			try {
				problem = GraphProblem.valueOfErrorCode(errorCode);
			} catch (IllegalArgumentException e) {
				// not a graph problem
				continue;
			}
			Spec spec = specMap.get(problem);
			int k = RodinMarkerUtil.getArguments(marker).length;
			if (spec.arity != k) {
				ok = false;
				System.out.println("Wrong number of arguments " + 
						problem.toString() + "/" + k + " expected: " + spec.arity);
			}
		}
		return ok;
	}

}
