/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.operation.tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.LabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.PredicateAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.operation.tests.utils.Element;
import org.eventb.ui.eventbeditor.operation.tests.utils.OperationTest;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class TestOperation extends OperationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testChangeAttribute() throws Exception {
		final IAttributeManipulation factory = new PredicateAttributeManipulation();

		final IInvariant invariant = createInvariant(mch, "myInvariant",
				"predicate", false);
		// after execute and redo, only event are renamed
		addInvariant(mchElement, "myInvariant", "predicateIsRenamed");

		final AtomicOperation op = OperationFactory.changeAttribute(mch
				.getRodinFile(), factory, invariant, "predicateIsRenamed");

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * add all elements to parent.
	 */
	private void addElements(Element parent, IInternalElement[] elements)
			throws RodinDBException {
		for (IInternalElement element : elements) {
			parent.addChild(Element.valueOf(element), null);
		}
	}

	@Test
	public void testCopyElements() throws Exception {

		final IMachineRoot mchSource = createMachine("source");

		final IInternalElement[] elements = new IInternalElement[] {
				createEvent(mchSource, "event"),
				createInvariant(mchSource, "inv2", "predicate", false),
				createRefinesMachineClause(mchSource, "mch2") };

		// at beginning and after undo
		createInvariant(mch, "myInvariant", "predicate", false);
		createVariant(mch, "expression");

		// after execute and redo
		mchElement = Element.valueOf(mch);
		addElements(mchElement, elements);

		final AtomicOperation op = OperationFactory.copyElements(mch, elements);

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * ensures that an action is created with
	 * <code>OperationFactory.createAction(machineEditor, event, label, assignement,
	 * null))</code>
	 * 
	 */
	@Test
	public void testCreateAction() throws Exception {
		final IEvent event = createEvent(mch, "event");

		final Element eventElement = addEventElement(mchElement, "event");
		addAction(eventElement, "myAction", "myAssignment");

		final AtomicOperation op = OperationFactory.createAction(event,
				"myAction", "myAssignment", null);

		verifyOperation(op, mch, mchElement);
	}

	@Test
	public void testCreateActionMultiple() throws Exception {
		final IEvent event = createEvent(mch, "event");

		final Element eventElement = addEventElement(mchElement, "event");

		final String[] label = new String[] { "act1", "act2", "act3" };
		final String[] assignment = new String[] { "var1:=4", "var2:=4",
				"var3:=4" };
		addAction(eventElement, label, assignment);

		final AtomicOperation op = OperationFactory.createAction(event, label,
				assignment, null);

		verifyOperation(op, mch, mchElement);
	}

	@Test
	public void testCreateAxiomWizard() throws Exception {
		addElementWithLabelPredicate(ctxElement, IAxiom.ELEMENT_TYPE, "axiom",
				"predicate", false);
		final AtomicOperation op = OperationFactory.createAxiomWizard(ctx,
				"axiom", "predicate", false);

		verifyOperation(op, ctx, ctxElement);
	}

	@Test
	public void testCreateAxiomWizardMultiple() throws Exception {
		final String[] labels = new String[] { "label1", "label2", "label3" };
		final String[] predicates = new String[] { "pred1", "pred2", "pred3" };
		final boolean[] isTheorem = new boolean[] { true, false, true };
		
		addElementWithLabelPredicate(ctxElement, IAxiom.ELEMENT_TYPE, labels,
				predicates, isTheorem);

		final AtomicOperation op = OperationFactory.createAxiomWizard(ctx,
				labels, predicates, isTheorem);

		verifyOperation(op, ctx, ctxElement);
	}

	@Test
	public void testCreateCarrierSetWizard() throws Exception {
		addElementWithIdentifier(ctxElement, ICarrierSet.ELEMENT_TYPE, "mySet");
		final AtomicOperation op = OperationFactory.createCarrierSetWizard(ctx,
				"mySet");

		verifyOperation(op, ctx, ctxElement);
	}

	@Test
	public void testCreateCarrierSetWizardMultiple() throws Exception {
		final String[] identifiers = new String[] { "mySet1", "mySet2",
				"mySet3" };
		addElementWithIdentifier(ctxElement, ICarrierSet.ELEMENT_TYPE,
				identifiers);
		final AtomicOperation op = OperationFactory.createCarrierSetWizard(ctx,
				identifiers);

		verifyOperation(op, ctx, ctxElement);
	}

	@Test
	public void testCreateConstantWizard() throws Exception {
		final String[] labels = new String[] { "axm1", "axm2", "axm3" };
		final String[] predicates = new String[] { "prd1", "prd2", "prd3" };
		final boolean[] isTheorem = new boolean[] { false, false, false};
		addElementWithIdentifier(ctxElement, IConstant.ELEMENT_TYPE,
				"myConstant");
		addElementWithLabelPredicate(ctxElement, IAxiom.ELEMENT_TYPE, labels,
				predicates, isTheorem);

		final AtomicOperation op = OperationFactory.createConstantWizard(ctx,
				"myConstant", labels, predicates);

		verifyOperation(op, ctx, ctxElement);
	}

	@Test
	public void testCreateElement() throws Exception {
		addElementWithStringAttribute(ctxElement, IExtendsContext.ELEMENT_TYPE,
				EventBAttributes.TARGET_ATTRIBUTE, "ctx");
		final AtomicOperation op = OperationFactory.createElement(ctx,
				IExtendsContext.ELEMENT_TYPE,
				EventBAttributes.TARGET_ATTRIBUTE, "ctx");

		verifyOperation(op, ctx, ctxElement);
	}

	@Test
	public void testCreateElementGeneric() throws Exception, RodinDBException {
		addInvariant(mchElement, "inv1", "\u22a4");

		final AtomicOperation op = OperationFactory.createElementGeneric(mch,
				IInvariant.ELEMENT_TYPE, null);

		verifyOperation(op, mch, mchElement);
	}

	@Test
	public void testCreateEnumeratedSetWizard() throws Exception {
		final String[] elements = new String[] { "e1", "e2", "e3" };
		addElementWithIdentifier(ctxElement, ICarrierSet.ELEMENT_TYPE, "mySet");
		addElementWithIdentifier(ctxElement, IConstant.ELEMENT_TYPE, elements);
		addElementWithLabelPredicate(ctxElement, IAxiom.ELEMENT_TYPE, "axm1",
				"partition(mySet, {e1}, {e2}, {e3})", false);

		final AtomicOperation op = OperationFactory.createEnumeratedSetWizard(
				ctx, "mySet", elements);

		verifyOperation(op, ctx, ctxElement);
	}

	// test Bug 2217041
	@Test
	public void testCreateEvent() throws Exception {
		final String[] varNames = new String[] { "var1", "var2" };
		final String[] grdNames = new String[] { "grd1", "grd2" };
		final String[] grdPredicates = new String[] { "var1 : NAT",
				"var2 : NAT" };
		final boolean[] isTheorem = new boolean[] { false, false, false};
		final String[] actNames = new String[] { "act1", "act2" };
		final String[] actSubstitutions = new String[] { "a := var1",
				"b := var2" };

		final Element eventElement = addEventElement(mchElement, "evt");
		addElementWithIdentifier(eventElement, IParameter.ELEMENT_TYPE,
				varNames);
		addElementWithLabelPredicate(eventElement, IGuard.ELEMENT_TYPE,
				grdNames, grdPredicates, isTheorem);
		addAction(eventElement, actNames, actSubstitutions);

		final AtomicOperation op = OperationFactory.createEvent(mch, "evt",
				varNames, grdNames, grdPredicates, actNames, actSubstitutions);

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * ensures that a guard is created with
	 * <code>OperationFactory.createGuard( event, label, assignement,
	 * null))</code>
	 * 
	 */
	@Test
	public void testCreateGuard() throws Exception {
		final IEvent event = createEvent(mch, "event");

		final Element eventElement = addEventElement(mchElement, "event");
		addElementWithLabelPredicate(eventElement, IGuard.ELEMENT_TYPE,
				"myGuard", "a : NAT", false);

		final AtomicOperation op = OperationFactory.createGuard(event,
				"myGuard", "a : NAT", null);

		verifyOperation(op, mch, mchElement);
	}

	@Test
	public void testCreateInvariantWizard() throws Exception {
		addElementWithLabelPredicate(mchElement, IInvariant.ELEMENT_TYPE,
				"myInvariant", "myPredicate", false);
		final AtomicOperation op = OperationFactory.createInvariantWizard(mch,
				"myInvariant", "myPredicate", false);

		verifyOperation(op, mch, mchElement);
	}

	@Test
	public void testCreateInvariantWizardMultiple() throws Exception {
		final String[] labels = new String[] { "inv1", "inv2", "inv3" };
		final String[] predicates = new String[] { "pred1", "pred2", "pred3" };
		final boolean[] isTheorem = new boolean[] { true, false, true };
		addElementWithLabelPredicate(mchElement, IInvariant.ELEMENT_TYPE,
				labels, predicates, isTheorem);

		final AtomicOperation op = OperationFactory.createInvariantWizard(mch,
				labels, predicates, isTheorem);

		verifyOperation(op, mch, mchElement);
	}

	private class InvariantsPair extends Pair<String, String> {
		public InvariantsPair(String obj1, String obj2) {
			super(obj1, obj2);
		}
	}

	@Test
	public void testCreateVariableWizard() throws Exception {
		addElementWithIdentifier(mchElement, IVariable.ELEMENT_TYPE,
				"myVariable");
		addInvariant(mchElement, "inv1", "myVariable > 0");
		addInvariant(mchElement, "inv2", "myVariable < 3");
		final Element event = addEventElement(mchElement, "INITIALISATION");
		addAction(event, "act1", "myVariable := 1");

		InvariantsPair[] invariants = new InvariantsPair[] {
				new InvariantsPair("inv1", "myVariable > 0"),
				new InvariantsPair("inv2", "myVariable < 3") };

		final ArrayList<Pair<String, String>> invariantCollection = new ArrayList<Pair<String, String>>(
				Arrays.asList(invariants));

		final AtomicOperation op = OperationFactory.createVariableWizard(mch,
				"myVariable", invariantCollection, "act1", "myVariable := 1");

		verifyOperation(op, mch, mchElement);
	}

	@Test
	public void testCreateVariantWizard() throws Exception {
		addVariant(mchElement, "expression");

		final AtomicOperation op = OperationFactory.createVariantWizard(mch,
				"expression");

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * ensures that an element is deleted when execute and redo operation.<br>
	 * ensures that the deleted element is created and that the orders is kept
	 * when undo
	 */
	@Test
	public void testDeleteElement() throws Exception {
		// after execute and redo, there is one invariant
		addInvariant(mchElement, "inv2", "predicate2");

		IInvariant inv = createInvariant(mch, "inv1", "predicate1", false);
		createInvariant(mch, "inv2", "predicate2", false);

		final AtomicOperation op = OperationFactory.deleteElement(inv);

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * ensures that elements are deleted when execute and redo operation.<br>
	 * ensures that all deleted elements are created and that the orders is kept
	 * when undo
	 */
	@Test
	public void testDeleteElementMultiple() throws Exception {
		// after execute and redo, there is one invariant
		addInvariant(mchElement, "inv2", "predicate2");

		final IInvariant inv1 = createInvariant(mch, "inv1", "predicate1",
				false);
		createInvariant(mch, "inv2", "predicate2", false);
		final IInvariant inv3 = createInvariant(mch, "inv3", "predicate3",
				false);

		final AtomicOperation op = OperationFactory.deleteElement(
				new IInternalElement[] { inv1, inv3 }, true);

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * ensures that the first element is move down when execute and redo<br>
	 * ensures that the first element is still the first after undo
	 */
	@Test
	public void testMove1() throws Exception {
		// orders after execute and redo
		addInvariant(mchElement, "inv2", "predicate");
		addInvariant(mchElement, "inv1", "predicate");
		addInvariant(mchElement, "inv3", "predicate");
		addInvariant(mchElement, "inv4", "predicate");

		final IInvariant moved = createInvariant(mch, "inv1", "predicate",
				false);
		createInvariant(mch, "inv2", "predicate", false);
		final IInvariant nextSibling = createInvariant(mch, "inv3",
				"predicate", false);
		createInvariant(mch, "inv4", "predicate", false);

		final AtomicOperation op = OperationFactory.move(mch, moved, mch,
				nextSibling);

		verifyOperation(op, mch, mchElement);
	}

	/**
	 * ensures that the last element is move before the first when execute and
	 * redo<br>
	 * ensures that the last element is still the last after undo
	 */
	@Test
	public void testMove2() throws Exception {
		// orders after execute and redo
		addInvariant(mchElement, "inv4", "predicate");
		addInvariant(mchElement, "inv1", "predicate");
		addInvariant(mchElement, "inv2", "predicate");
		addInvariant(mchElement, "inv3", "predicate");

		final IInvariant nextSibling = createInvariant(mch, "inv1", "predicate", false);
		createInvariant(mch, "inv2", "predicate", false);
		createInvariant(mch, "inv3", "predicate", false);
		final IInvariant moved = createInvariant(mch, "inv4", "predicate", false);

		final AtomicOperation op = OperationFactory.move(mch, moved, mch,
				nextSibling);

		verifyOperation(op, mch, mchElement);

	}

	/**
	 * ensures that prefix of elements with the same type is renamed<br>
	 * ensures others elements are not renamed
	 */
	@Test
	public void testRenameElements() throws Exception {
		final IAttributeManipulation factory = new LabelAttributeManipulation();

		// at beginning and after undo
		final IEvent event = createEvent(mch, "myEvent1");
		createEvent(mch, "myEvent2");
		createEvent(mch, "myEvent3");
		createEvent(mch, "myEvent4");
		createGuard(event, "myGuard", "predicate"); // ILabeledElement
		createInvariant(mch, "myInvariant", "predicate", false); // ILabeledElement

		// after execute and redo, only event are renamed
		final Element eventElement = addEventElement(mchElement, "evt1");
		addEventElement(mchElement, "evt2");
		addEventElement(mchElement, "evt3");
		addEventElement(mchElement, "evt4");
		addElementWithLabelPredicate(eventElement, IGuard.ELEMENT_TYPE,
				"myGuard", "predicate", false);
		addInvariant(mchElement, "myInvariant", "predicate");

		final AtomicOperation op = OperationFactory.renameElements(mch,
				IEvent.ELEMENT_TYPE, factory, "evt");

		verifyOperation(op, mch, mchElement);

	}
}
