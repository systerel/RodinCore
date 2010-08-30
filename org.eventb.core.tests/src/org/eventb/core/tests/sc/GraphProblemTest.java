/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IRodinProblem;
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
	
	@SuppressWarnings("deprecation")
	private static Spec[] specs = new Spec[] {
			spec(GraphProblem.LoadingRootModuleError, 0),
			spec(GraphProblem.ConfigurationMissingError, 1),
			spec(GraphProblem.IdentifierUndefError, 0),
			spec(GraphProblem.PredicateUndefError, 0),
			spec(GraphProblem.ExpressionUndefError, 0),
			spec(GraphProblem.AssignmentUndefError, 0),
			spec(GraphProblem.ConvergenceUndefError, 0),
			spec(GraphProblem.ExtendedUndefError, 0),
			spec(GraphProblem.DerivedPredUndefError, 0),
			spec(GraphProblem.DerivedPredIgnoredWarning, 0),
			spec(GraphProblem.InvalidIdentifierError, 1),
			spec(GraphProblem.InvalidIdentifierSpacesError, 1),
			spec(GraphProblem.LabelUndefError, 0),
			spec(GraphProblem.EmptyLabelError, 0),
			spec(GraphProblem.AbstractContextNameUndefError, 0),
			spec(GraphProblem.AbstractContextNotFoundError, 1),
			spec(GraphProblem.AbstractContextRedundantWarning, 1),
			spec(GraphProblem.SeenContextRedundantWarning, 1),
			spec(GraphProblem.SeenContextNameUndefError, 0),
			spec(GraphProblem.SeenContextNotFoundError, 1),
			spec(GraphProblem.SeenContextWithoutConfigurationError, 1),
			spec(GraphProblem.AbstractMachineNameUndefError, 0),
			spec(GraphProblem.TooManyAbstractMachinesError, 0),
			spec(GraphProblem.AbstractMachineWithoutConfigurationError, 1),
			spec(GraphProblem.AbstractContextWithoutConfigurationError, 1),
			spec(GraphProblem.AbstractMachineNotFoundError, 1),
			spec(GraphProblem.AbstractEventLabelUndefError, 0),
			spec(GraphProblem.AbstractEventNotFoundError, 1),
			spec(GraphProblem.AbstractEventNotRefinedError, 1),
			spec(GraphProblem.AbstractEventLabelConflictWarning, 1),
			spec(GraphProblem.EventMergeSplitError, 1),
			spec(GraphProblem.EventMergeMergeError, 1),
			spec(GraphProblem.EventInheritedMergeSplitError, 1),
			spec(GraphProblem.EventExtendedUnrefinedError, 1),
			spec(GraphProblem.EventExtendedMergeError, 1),
			spec(GraphProblem.EventMergeVariableTypeError, 1),
			spec(GraphProblem.EventMergeActionError, 0),
			spec(GraphProblem.EventMergeLabelError, 0),
			spec(GraphProblem.EventRefinementError, 0),
			spec(GraphProblem.MachineWithoutInitialisationWarning, 0),
			spec(GraphProblem.InitialisationRefinesEventWarning, 0),
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
			spec(GraphProblem.ParameterNameConflictError, 1),
			spec(GraphProblem.ParameterNameConflictWarning, 1),
			spec(GraphProblem.ParameterNameImportConflictError, 2),
			spec(GraphProblem.ParameterNameImportConflictWarning, 2),
			spec(GraphProblem.UntypedCarrierSetError, 1),
			spec(GraphProblem.UntypedConstantError, 1),
			spec(GraphProblem.UntypedVariableError, 1),
			spec(GraphProblem.UntypedParameterError, 1),
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
			spec(GraphProblem.ActionDisjointLHSWarining, 0),
			spec(GraphProblem.WitnessFreeIdentifierError, 1),
			spec(GraphProblem.InvalidVariantTypeError, 1),
			spec(GraphProblem.TooManyVariantsError, 0),
			spec(GraphProblem.FaultyAbstractConvergenceUnchangedWarning, 1),
			spec(GraphProblem.FaultyAbstractConvergenceOrdinaryWarning, 1),
			spec(GraphProblem.FaultyAbstractConvergenceAnticipatedWarning, 1),
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
			spec(GraphProblem.WitnessLabelNotPermissible, 1),
			spec(GraphProblem.ContextOnlyInAbstractMachineWarning, 1),
			spec(GraphProblem.WasAbstractEventLabelWarning, 1),
			spec(GraphProblem.InconsistentEventLabelWarning, 1),
			spec(GraphProblem.VariableHasDisappearedError, 1),
			spec(GraphProblem.DisappearedVariableRedeclaredError, 1),
			spec(GraphProblem.VariableIsParameterInAbstractMachineError, 2),
			spec(GraphProblem.AssignedIdentifierNotVariableError, 1),
			spec(GraphProblem.ParameterChangedTypeError, 3),
			spec(GraphProblem.AssignmentToParameterError, 1),
			spec(GraphProblem.AssignmentToCarrierSetError, 1),
			spec(GraphProblem.AssignmentToConstantError, 1),
			spec(GraphProblem.EventInitLabelMisspellingWarning,1),
	};
	
	private static Map<GraphProblem, Spec> specMap = 
		new EnumMap<GraphProblem, Spec>(GraphProblem.class);
	static {
		for (Spec spec : specs) {
			specMap.put(spec.problem, spec);
		}
	}
	
	/**
	 * check whether the messages loaded from the properties take the correct number of parameters.
	 */
	public void testArguments() throws Exception {
		for (Spec spec : specs) {
			assertEquals("wrong number of arguments", spec.arity, spec.problem.getArity());
		}
	}
	
	/**
	 * check whether the messages loaded from the properties file are complete
	 */
	public void testMessages() throws Exception {
		Set<IRodinProblem> problems = new HashSet<IRodinProblem>(specs.length * 4 / 3 + 1);
		for (Spec spec : specs) {
			problems.add(spec.problem);
		}
		for (IRodinProblem problem : GraphProblem.values()) {
			boolean found = problems.contains(problem);
			assertTrue("No spec for problem " + problem, found);
		}
		//assertEquals("wrong number of problems", specs.length, GraphProblem.values().length);
	}

	
	public static boolean check(IEventBRoot root) throws CoreException {
		boolean ok = true;
		IMarker[] markers = 
			root.getResource().findMarkers(
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
			assertNotNull("missing problem spec", spec);
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
