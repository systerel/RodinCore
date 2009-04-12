/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.editpage.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IAction;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.eventbeditor.manipulation.AssignmentAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.CommentAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ConvergenceAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExpressionAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendedAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendsContextAbstractContextNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IdentifierAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.LabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.PredicateAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesEventAbstractEventLabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesMachineAbstractMachineNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.SeesContextNameAttributeManipulation;
import org.eventb.ui.tests.utils.EventBUITest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class TestAttributeManipulation extends EventBUITest {

	public void testExtendsContextGetPossibleValueWithoutExtendsClause()
			throws RodinDBException {
		final IAttributeManipulation manipulation = new ExtendsContextAbstractContextNameAttributeManipulation();
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		final IContextRoot ctx3 = createContext("ctx3");

		final IExtendsContext seeCtx = ctx3.getExtendsClause("extends_ctx0");

		final String[] possibleValues = manipulation.getPossibleValues(seeCtx, null);

		// context doesn't have extends context clause so expected all context
		assertPossibleValues("Error in getPossibleValue for Extends Context",
				possibleValues, "ctx0", "ctx1", "ctx2");
	}

	public void testExtendsContextGetPossibleValueWithExtendsClause()
			throws RodinDBException {
		final IAttributeManipulation manipulation = new ExtendsContextAbstractContextNameAttributeManipulation();
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		final IContextRoot ctx3 = createContext("ctx3");

		createExtendsContextClause(ctx3, "ctx1");
		createExtendsContextClause(ctx3, "ctx2");

		final IExtendsContext seeCtx = ctx3.getExtendsClause("extends_ctx0");

		// ctx3 extends ctx1 and ctx2 context clause so expected ctx0
		final String[] possibleValues = manipulation.getPossibleValues(seeCtx, null);
		assertPossibleValues("Error in getPossibleValue for Extends Context",
				possibleValues, "ctx0");
	}

	public void testExtendsContextGetPossibleValueWithExtendsClauseCalledWithExistingClause()
			throws RodinDBException {
		final IAttributeManipulation manipulation = new ExtendsContextAbstractContextNameAttributeManipulation();
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		final IContextRoot ctx3 = createContext("ctx3");

		final IExtendsContext seeCtx = createExtendsContextClause(ctx3, "ctx2");
		createExtendsContextClause(ctx3, "ctx1");

		// ctx3 extends ctx1 and ctx2 context clause and call with existing
		// clause so expected ctx0, ctx2
		final String[] possibleValues = manipulation.getPossibleValues(seeCtx, null);
		assertPossibleValues("Error in getPossibleValue for Extends Context",
				possibleValues, "ctx0", "ctx2");

	}

	public void testSeeContextGetPossibleValueWithoutSeesClause()
			throws RodinDBException {
		final IAttributeManipulation manipulation = new SeesContextNameAttributeManipulation();
		final IMachineRoot mch = createMachine("mch");
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		createContext("ctx3");

		final ISeesContext seeCtx = mch.getSeesClause("see_ctx0");

		final String[] possibleValues = manipulation.getPossibleValues(seeCtx, null);

		// machine doesn't have see context clause so expected all context
		assertPossibleValues("Error in getPossibleValue for SeesContext",
				possibleValues, "ctx0", "ctx1", "ctx2", "ctx3");
	}

	public void testSeeContextGetPossibleValueWithSeesClause()
			throws RodinDBException {
		final IAttributeManipulation manipulation = new SeesContextNameAttributeManipulation();
		final IMachineRoot mch = createMachine("mch");
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		createContext("ctx3");

		final ISeesContext seeCtx = mch.getSeesClause("extends_ctx0");
		createSeesContextClause(mch, "ctx1");

		final String[] possibleValues = manipulation.getPossibleValues(seeCtx, null);
		// machine have 2 sees context clause so expected 2 context
		assertPossibleValues("Error in getPossibleValue for SeesContext",
				possibleValues, "ctx0", "ctx2", "ctx3");
	}

	public void testSeeContextGetPossibleValueWithSeesClauseCalledWithExistingClause()
			throws RodinDBException {
		final IAttributeManipulation manipulation = new SeesContextNameAttributeManipulation();
		final IMachineRoot mch = createMachine("mch");
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		createContext("ctx3");

		final ISeesContext seeCtx = createSeesContextClause(mch, "ctx2");
		createSeesContextClause(mch, "ctx1");

		final String[] possibleValues = manipulation.getPossibleValues(seeCtx, null);
		// machine have 2 sees context clause so expected 2 context
		assertPossibleValues("Error in getPossibleValue for SeesContext",
				possibleValues, "ctx0", "ctx2", "ctx3");
	}

	public void testRefineMachineGetPossibleValue() throws RodinDBException {
		final IAttributeManipulation manipulation = new RefinesMachineAbstractMachineNameAttributeManipulation();
		createMachine("mch0");
		createMachine("mch1");
		final IMachineRoot mch2 = createMachine("mch2");

		final IRefinesMachine refined = createRefinesMachineClause(mch2, "mch0");

		final String[] possibleValues = manipulation
				.getPossibleValues(refined, null);

		assertPossibleValues("Error in getPossibleValue for RefineMachine",
				possibleValues, "mch0", "mch1");
	}

	public void testRefineEventGetPossibleValue() throws RodinDBException {
		final IAttributeManipulation manipulation = new RefinesEventAbstractEventLabelAttributeManipulation();
		final IMachineRoot mch0 = createMachine("mch0");
		final IMachineRoot mch1 = createMachine("mch1");
		final IMachineRoot mch2 = createMachine("mch2");

		createEvent(mch0, "event01");
		createEvent(mch0, "event02");
		createEvent(mch1, "event11");
		createEvent(mch1, "event12");
		IEvent event21 = createEvent(mch2, "event21");

		createRefinesMachineClause(mch2, "mch0");
		IRefinesEvent refined = createRefinesEventClause(event21, "event01");

		String[] possibleValues = manipulation.getPossibleValues(refined, null);

		assertPossibleValues("Error in getPossibleValue for RefineEvent",
				possibleValues, "event01", "event02");

	}

	public void testExtendsContextHasValueTrue() throws Exception {
		final IExtendsContext extendsContext = createExtendsContext();
		final ExtendsContextAbstractContextNameAttributeManipulation manipulation = new ExtendsContextAbstractContextNameAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, extendsContext, false);

		extendsContext.setAbstractContextName("ctx1", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, extendsContext, true);
	}

	public void testSeesContextHasValue() throws Exception {
		final ISeesContext seesContext = createSeesContext();
		final SeesContextNameAttributeManipulation manipulation = new SeesContextNameAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, seesContext, false);

		seesContext.setSeenContextName("ctx1", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, seesContext, true);
	}

	public void testAssignmentHasValue() throws Exception {
		final IAssignmentElement assignment = createAssignment();
		final AssignmentAttributeManipulation manipulation = new AssignmentAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, assignment, false);

		assignment.setAssignmentString("var1 := 4", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, assignment, true);
	}

	public void testCommentAttributeHasValue() throws Exception {
		final ICommentedElement commented = createCommented();
		final CommentAttributeManipulation manipulation = new CommentAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, commented, false);

		commented.setComment("my comment", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, commented, true);
	}

	public void testConvergenceHasValue() throws Exception {
		final IConvergenceElement convergence = createConvergence();
		final ConvergenceAttributeManipulation manipulation = new ConvergenceAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, convergence, false);

		convergence.setConvergence(IConvergenceElement.Convergence.CONVERGENT,
				null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, convergence, true);
	}

	public void testExpressionHasValue() throws Exception {
		final IExpressionElement expression = createExpression();
		final ExpressionAttributeManipulation manipulation = new ExpressionAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, expression, false);

		expression.setExpressionString("var1", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, expression, true);
	}

	public void testExtendedHasValue() throws Exception {
		final IEvent event = createEvent();
		final ExtendedAttributeManipulation manipulation = new ExtendedAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, event, false);

		event.setExtended(true, null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, event, true);
	}

	public void testIdentifierHasValue() throws Exception {
		final IIdentifierElement identifier = createIdentifier();
		final IdentifierAttributeManipulation manipulation = new IdentifierAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, identifier, false);

		identifier.setIdentifierString("identifier", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, identifier, true);
	}

	public void testLabelHasValue() throws Exception {
		final ILabeledElement labeled = createLabeled();
		final LabelAttributeManipulation manipulation = new LabelAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, labeled, false);

		labeled.setLabel("label", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, labeled, true);
	}

	public void testPredicateHasValue() throws Exception {
		final IPredicateElement predicate = createPredicate();
		final PredicateAttributeManipulation manipulation = new PredicateAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, predicate, false);

		predicate.setPredicateString("a < b", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, predicate, true);
	}

	public void testRefinesEventHasValue() throws Exception {
		final IRefinesEvent refines = createRefinesEvent();
		final RefinesEventAbstractEventLabelAttributeManipulation manipulation = new RefinesEventAbstractEventLabelAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, refines, false);

		refines.setAbstractEventLabel("event1", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, refines, true);
	}

	public void testRefinesMachineHasValue() throws Exception {
		final IRefinesMachine refines = createRefinesMachine();
		final RefinesMachineAbstractMachineNameAttributeManipulation manipulation = new RefinesMachineAbstractMachineNameAttributeManipulation();

		// the attribute value is not set so must return false
		assertHasValue(manipulation, refines, false);

		refines.setAbstractMachineName("mch1", null);
		// the attribute value is set so must return true
		assertHasValue(manipulation, refines, true);
	}

	private void assertPossibleValues(String msg, String[] actual,
			String... expected) {
		final Set<String> setExpected = new HashSet<String>(Arrays
				.asList(expected));
		final Set<String> setActual = new HashSet<String>(Arrays.asList(actual));
		final String messageFail = msg + ": expected: " + setExpected
				+ ", but was: " + setActual;

		if (!setExpected.equals(setActual))
			fail(messageFail);
	}

	private void assertHasValue(IAttributeManipulation manipulation,
			IInternalElement element, boolean expected) throws RodinDBException {
		String msg = (expected) ? "Element should have value"
				: "Element should not have value";
		assertEquals(msg, expected, manipulation.hasValue(element, null));
	}

	/**
	 * Creates a context with an extends context clause, without set the
	 * abstract context name.
	 * 
	 * @return the newly created extendsContext
	 */
	private IExtendsContext createExtendsContext() throws RodinDBException {
		final IContextRoot ctx = createContext("ctx");
		return ctx.createChild(IExtendsContext.ELEMENT_TYPE, null, null);
	}

	/**
	 * Creates a machine with a sees context clause, without set the sees
	 * context name.
	 * 
	 * @return the newly created seesContext
	 */
	private ISeesContext createSeesContext() throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return mch.createChild(ISeesContext.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return an assignment element without assignment attribute. The element
	 * required for the assignment element are also created.
	 * 
	 * @return the newly created assignment element.
	 */
	private IAssignmentElement createAssignment() throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		final IEvent event = createEvent(mch, "event");
		return event.createChild(IAction.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return a commented element without comment attribute. The element
	 * required for the commented element are also created.
	 * 
	 * @return the newly created commented element.
	 */
	private ICommentedElement createCommented() throws RodinDBException {
		return createMachine("mch");
	}

	/**
	 * Return an event without attribute. The element required for the event are
	 * also created.
	 * 
	 * @return the newly created event.
	 */
	private IEvent createEvent() throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return mch.createChild(IEvent.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return a convergence element without convergence attribute. The element
	 * required for the convergence element are also created.
	 * 
	 * @return the newly created convergence element.
	 */
	private IConvergenceElement createConvergence() throws RodinDBException {
		return createEvent();
	}

	/**
	 * Return an expression element without expression attribute. The element
	 * required for the expression element are also created.
	 * 
	 * @return the newly created expression element.
	 */
	private IExpressionElement createExpression() throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return mch.createChild(IVariant.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return an identifier element without identifier attribute. The element
	 * required for the identifier element are also created.
	 * 
	 * @return the newly created identifier element.
	 */
	private IIdentifierElement createIdentifier() throws RodinDBException {
		final IContextRoot ctx = createContext("ctx");
		return ctx.createChild(ICarrierSet.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return a labeled element without labeled attribute. The element required
	 * for the labeled element are also created.
	 * 
	 * @return the newly created labeled element.
	 */
	private ILabeledElement createLabeled() throws RodinDBException {
		return createEvent();
	}

	/**
	 * Return a predicate element without predicate attribute. The element
	 * required for the predicate element are also created.
	 * 
	 * @return the newly created predicate element.
	 */
	private IPredicateElement createPredicate() throws RodinDBException {
		final IContextRoot ctx = createContext("ctx");
		return ctx.createChild(IAxiom.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return a refines event clause without attribute. The element required for
	 * the refines event clause are also created.
	 * 
	 * @return the newly created refines event.
	 */
	private IRefinesEvent createRefinesEvent() throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		final IEvent event = createEvent(mch, "event");
		return event.createChild(IRefinesEvent.ELEMENT_TYPE, null, null);
	}

	/**
	 * Return a refines machine clause without attribute. The element required
	 * for the refines machine clause are also created.
	 * 
	 * @return the newly created refines machine.
	 */
	private IRefinesMachine createRefinesMachine() throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return mch.createChild(IRefinesMachine.ELEMENT_TYPE, null, null);
	}
}
