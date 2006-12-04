/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBTest extends BuilderTest {
	
	public ITypeEnvironment emptyEnv = factory.makeTypeEnvironment();

	public EventBTest() {
		super();
	}

	public EventBTest(String name) {
		super(name);
	}

	private static int alloc;
	
	private static String getUniqueName() {
		if (alloc == Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		return "x" + alloc++;
	}
	
	public void addAxioms(
			IContextFile rodinFile, 
			String[] names, 
			String[] axioms) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IAxiom axiom = rodinFile.getAxiom(getUniqueName());
			axiom.create(null, null);
			axiom.setPredicateString(axioms[i], null);
			axiom.setLabel(names[i], null);
		}
	}
		
	public void addCarrierSets(IContextFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			ICarrierSet set = rodinFile.getCarrierSet(getUniqueName());
			set.create(null, null);
			set.setIdentifierString(name, null);
		}
			
	}
	
	public void addConstants(IContextFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			IConstant constant = rodinFile.getConstant(getUniqueName());
			constant.create(null, null);
			constant.setIdentifierString(name, null);
		}
	}

	public void addEventRefines(IEvent event, String name) throws RodinDBException {
		IRefinesEvent refines = event.getRefinesClause(getUniqueName());
		refines.create(null, null);
		refines.setAbstractEventLabel(name, null);
	}

	public void addEventWitnesses(IEvent event, String[] labels, String[] predicates) throws RodinDBException {
		assert labels.length == predicates.length;
		for (int i=0; i<labels.length; i++) {
			IWitness witness = event.getWitness(getUniqueName());
			witness.create(null, null);
			witness.setLabel(labels[i], null);
			witness.setPredicateString(predicates[i], null);
		}
	}

	public IEvent addEvent(IMachineFile rodinFile, 
				String name,
				String[] vars,
				String[] guardNames,
				String[] guards,
				String[] actionNames,
				String[] actions
	) throws RodinDBException {
		IEvent event = rodinFile.getEvent(getUniqueName());
		event.create(null, null);
		event.setLabel(name, null);
		event.setInherited(false, null);
		event.setConvergence(IConvergenceElement.Convergence.ORDINARY, null);
		for(int i=0; i<vars.length; i++) {
			IVariable variable = event.getVariable(getUniqueName());
			variable.create(null, null);
			variable.setIdentifierString(vars[i], null);
			
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = event.getGuard(getUniqueName());
			guard.create(null, null);
			guard.setPredicateString(guards[i], null);
			guard.setLabel(guardNames[i], null);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = event.getAction(getUniqueName());
			action.create(null, null);
			action.setAssignmentString(actions[j], null);
			action.setLabel(actionNames[j], null);
		}
		return event;
	}
	
	public IEvent addEvent(IMachineFile rodinFile, 
			String name) throws RodinDBException {
		return addEvent(rodinFile, name, makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
	}
	
	public IEvent addInheritedEvent(IMachineFile rodinFile, 
			String name) throws RodinDBException {
		IEvent event = rodinFile.getEvent(getUniqueName());
		event.create(null, null);
		event.setLabel(name, null);
		event.setInherited(true, null);
		event.setConvergence(IConvergenceElement.Convergence.ORDINARY, null);
		return event;
		
	}
	
	public void setConvergence(IEvent event, 
			IConvergenceElement.Convergence convergence) throws RodinDBException {
		event.setConvergence(convergence, null);
	}
	
	public void setOrdinary(IEvent event) throws RodinDBException {
		setConvergence(event, IConvergenceElement.Convergence.ORDINARY);
	}

	public void setAnticipated(IEvent event) throws RodinDBException {
		setConvergence(event, IConvergenceElement.Convergence.ANTICIPATED);
	}

	public void setConvergent(IEvent event) throws RodinDBException {
		setConvergence(event, IConvergenceElement.Convergence.CONVERGENT);
	}
	
	public void addInvariants(IMachineFile rodinFile, String[] names, String[] invariants) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInvariant invariant = rodinFile.getInvariant(getUniqueName());
			invariant.create(null, null);
			invariant.setPredicateString(invariants[i], null);
			invariant.setLabel(names[i], null);
		}
	}

	public void addVariant(IMachineFile rodinFile, String expression) throws RodinDBException {
		IVariant variant = rodinFile.getVariant(getUniqueName());
		variant.create(null, null);
		variant.setExpressionString(expression, null);
	}

	public void addMachineSees(IMachineFile rodinFile, String name) throws RodinDBException {
		ISeesContext sees = rodinFile.getSeesClause(getUniqueName());
		sees.create(null, null);
		sees.setSeenContextName(name, null);
	}

	public void addMachineRefines(IMachineFile rodinFile, String name) throws RodinDBException {
		IRefinesMachine refines = rodinFile.getRefinesClause(getUniqueName());
		refines.create(null, null);
		refines.setAbstractMachineName(name, null);
	}

	public void addContextExtends(IContextFile rodinFile, String name) throws RodinDBException {
		IExtendsContext extendsContext = rodinFile.getExtendsClause(getUniqueName());
		extendsContext.create(null, null);
		extendsContext.setAbstractContextName(name, null);
	}

	public void addTheorems(IMachineFile rodinFile, String[] names, String[] theorems) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = rodinFile.getTheorem(getUniqueName());
			theorem.create(null, null);
			theorem.setPredicateString(theorems[i], null);
			theorem.setLabel(names[i], null);
		}
	}

	public void addTheorems(IContextFile rodinFile, String[] names, String[] theorems) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = rodinFile.getTheorem(getUniqueName());
			theorem.create(null, null);
			theorem.setPredicateString(theorems[i], null);
			theorem.setLabel(names[i], null);
		}
	}

	public void addVariables(IMachineFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			IVariable variable = rodinFile.getVariable(getUniqueName());
			variable.create(null, null);
			variable.setIdentifierString(name, null);
		}
	}

	public static String[] makeSList(String...strings) {
		return strings;
	}

	// generic methods
	
	public void addNonTheorems(IContextFile rodinFile, String[] names, String[] axioms) throws RodinDBException {
		addAxioms(rodinFile, names, axioms);
	}
	
	public void addNonTheorems(IMachineFile rodinFile, String[] names, String[] invariants) throws RodinDBException {
		addInvariants(rodinFile, names, invariants);
	}

	public void addIdents(IContextFile rodinFile, String... names) throws RodinDBException {
		addConstants(rodinFile, names);
	}

	public void addIdents(IMachineFile rodinFile, String... names) throws RodinDBException {
		addVariables(rodinFile, names);
	}
	
	public IContextFile createComponent(String bareName, IContextFile dummy) throws RodinDBException {
		return createContext(bareName);
	}

	public IMachineFile createComponent(String bareName, IMachineFile dummy) throws RodinDBException {
		return createMachine(bareName);
	}
	
	public void addSuper(IContextFile rodinFile, String name) throws RodinDBException {
		addContextExtends(rodinFile, name);
	}

	public void addSuper(IMachineFile rodinFile, String name) throws RodinDBException {
		addMachineRefines(rodinFile, name);
	}

	public String getNormalizedExpression(String input, ITypeEnvironment environment) {
		Expression expr = factory.parseExpression(input).getParsedExpression();
		expr.typeCheck(environment);
		assertTrue(expr.isTypeChecked());
		return expr.toStringWithTypes();
	}

	public String getNormalizedPredicate(String input, ITypeEnvironment environment) {
		Predicate pred = factory.parsePredicate(input).getParsedPredicate();
		pred.typeCheck(environment);
		assertTrue(pred.isTypeChecked());
		return pred.toStringWithTypes();
	}

	public String getNormalizedAssignment(String input, ITypeEnvironment environment) {
		Assignment assn = factory.parseAssignment(input).getParsedAssignment();
		assn.typeCheck(environment);
		assertTrue(assn.isTypeChecked());
		return assn.toStringWithTypes();
	}

}
