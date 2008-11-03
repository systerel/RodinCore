/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - add test for IAttributeFactory
 *******************************************************************************/
package org.eventb.ui.tests;

import org.eventb.core.IAction;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.IContextRoot;
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
import org.eventb.internal.ui.AbstractInternalElementLabelManipulation;
import org.eventb.internal.ui.AssignmentLabelManipulation;
import org.eventb.internal.ui.ExpressionLabelManipulation;
import org.eventb.internal.ui.ExtendsContextLabelManipulation;
import org.eventb.internal.ui.IdentifierLabelManipulation;
import org.eventb.internal.ui.LabelLabelManipulation;
import org.eventb.internal.ui.PredicateLabelManipulation;
import org.eventb.internal.ui.RefinesEventLabelManipulation;
import org.eventb.internal.ui.RefinesMachineLabelManipulation;
import org.eventb.internal.ui.SeesContextLabelManipulation;
import org.eventb.ui.tests.utils.EventBUITest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class TestInternalElementLabelManipulation extends EventBUITest {

	public void testGetAssignmentLabel() throws Exception {
		final String label = "var1 := 3";
		final IAssignmentElement assignment = createAssignment(label);
		final AssignmentLabelManipulation manipulation = new AssignmentLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(assignment));
	}

	public void testGetExpressionLabel() throws Exception {
		final String label = "var1";
		final IExpressionElement expression = createExpression(label);
		final ExpressionLabelManipulation manipulation = new ExpressionLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(expression));
	}

	public void testGetExtendsContextLabel() throws Exception {
		final String label = "ctx1";
		final IExtendsContext extendsContext = createExtendsContext(label);
		final ExtendsContextLabelManipulation manipulation = new ExtendsContextLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(extendsContext));
	}

	public void testGetIdentifierLabel() throws Exception {
		final String label = "ctx1";
		final IIdentifierElement identifier = createIdentifier(label);
		final IdentifierLabelManipulation manipulation = new IdentifierLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(identifier));
	}

	public void testGetLabelLabel() throws Exception {
		final String label = "label1";
		final ILabeledElement labeled = createLabeled(label);
		final LabelLabelManipulation manipulation = new LabelLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(labeled));
	}

	public void testGetPredicateLabel() throws Exception {
		final String label = "a < b";
		final IPredicateElement predicate = createPredicate(label);
		final PredicateLabelManipulation manipulation = new PredicateLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(predicate));
	}

	public void testGetRefinesEventLabel() throws Exception {
		final String label = "event1";
		final IRefinesEvent refines = createRefinesEvent(label);
		final RefinesEventLabelManipulation manipulation = new RefinesEventLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(refines));
	}

	public void testGetRefinesMachineLabel() throws Exception {
		final String label = "machine";
		final IRefinesMachine refines = createRefinesMachine(label);
		final RefinesMachineLabelManipulation manipulation = new RefinesMachineLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(refines));
	}

	public void testGetSeesContextLabel() throws Exception {
		final String label = "context";
		final ISeesContext sees = createSeesContext(label);
		final SeesContextLabelManipulation manipulation = new SeesContextLabelManipulation();

		assertEquals("The return value is not equals", label, manipulation
				.getLabel(sees));
	}

	public void testSetAssignmentLabel() throws Exception {
		final IAssignmentElement assignment = createAssignment("assignement");
		final AssignmentLabelManipulation manipulation = new AssignmentLabelManipulation();

		assertGetAndSetAIELabel(manipulation, assignment, "var1 := 5");
	}

	public void testSetExpressionLabel() throws Exception {
		final IExpressionElement expression = createExpression("expression");
		final ExpressionLabelManipulation manipulation = new ExpressionLabelManipulation();

		assertGetAndSetAIELabel(manipulation, expression, "var1");
	}

	public void testSetExtendsContextLabel() throws Exception {
		final IExtendsContext extendsContext = createExtendsContext("extendsContext");
		final ExtendsContextLabelManipulation manipulation = new ExtendsContextLabelManipulation();

		assertGetAndSetAIELabel(manipulation, extendsContext, "context1");
	}

	public void testSetIdentifierLabel() throws Exception {
		final IIdentifierElement identifier = createIdentifier("identifier");
		final IdentifierLabelManipulation manipulation = new IdentifierLabelManipulation();

		assertGetAndSetAIELabel(manipulation, identifier, "var1");
	}

	public void testSetLabelLabel() throws Exception {
		final ILabeledElement labeled = createLabeled("label");
		final LabelLabelManipulation manipulation = new LabelLabelManipulation();

		assertGetAndSetAIELabel(manipulation, labeled, "action");
	}

	public void testSetPredicateLabel() throws Exception {
		final IPredicateElement predicate = createPredicate("predicate");
		final PredicateLabelManipulation manipulation = new PredicateLabelManipulation();

		assertGetAndSetAIELabel(manipulation, predicate, "a < b");
	}

	public void testSetRefinesEventLabel() throws Exception {
		final IRefinesEvent refines = createRefinesEvent("refines");
		final RefinesEventLabelManipulation manipulation = new RefinesEventLabelManipulation();

		assertGetAndSetAIELabel(manipulation, refines, "event1");
	}

	public void testSetRefinesMachineLabel() throws Exception {
		final IRefinesMachine refines = createRefinesMachine("refines");
		final RefinesMachineLabelManipulation manipulation = new RefinesMachineLabelManipulation();

		assertGetAndSetAIELabel(manipulation, refines, "machine");
	}

	public void testSetSeesContextLabel() throws Exception {
		final ISeesContext seesContext = createSeesContext("seesContext");
		final SeesContextLabelManipulation manipulation = new SeesContextLabelManipulation();

		assertGetAndSetAIELabel(manipulation, seesContext, "context1");
	}

	private void assertGetAndSetAIELabel(
			AbstractInternalElementLabelManipulation<?> manipulation,
			IRodinElement obj, String text) throws Exception {
		manipulation.modify(obj, text);
		assertEquals("The label is not set correctly", text, manipulation
				.getLabel(obj));
	}

	private IAction createAction(String label, String assignment)
			throws RodinDBException {
		IMachineRoot mch = createMachine("mch");
		IEvent event = createEvent(mch, "event");
		return createAction(event, label, assignment);
	}

	private IAssignmentElement createAssignment(String label)
			throws RodinDBException {
		return createAction("action", label);
	}

	private ILabeledElement createLabeled(String label) throws RodinDBException {
		return createAction(label, "assignement");
	}

	private IExpressionElement createExpression(String label)
			throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return createVariant(mch, label);
	}

	private IIdentifierElement createIdentifier(String label)
			throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		final IEvent event = createEvent(mch, "event");
		return createParameter(event, label);
	}

	private IPredicateElement createPredicate(String label)
			throws RodinDBException {
		IMachineRoot mch = createMachine("mch");
		return createInvariant(mch, "invariant", label);
	}

	private IRefinesEvent createRefinesEvent(String label)
			throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		final IEvent event = createEvent(mch, "event2");
		return createRefinesEventClause(event, label);

	}

	private IExtendsContext createExtendsContext(String label)
			throws RodinDBException {
		final IContextRoot ctx = createContext("ctx");
		return createExtendsContextClause(ctx, label);

	}

	private IRefinesMachine createRefinesMachine(String label)
			throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return createRefinesMachineClause(mch, label);
	}

	private ISeesContext createSeesContext(String label)
			throws RodinDBException {
		final IMachineRoot mch = createMachine("mch");
		return createSeesContextClause(mch, label);

	}
}
