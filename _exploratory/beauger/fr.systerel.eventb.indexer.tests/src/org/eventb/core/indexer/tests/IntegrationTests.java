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
package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.tests.OccUtils.*;
import static org.eventb.core.indexer.tests.ResourceUtils.*;

import java.util.List;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.indexer.ContextIndexer;
import org.eventb.core.indexer.MachineIndexer;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexRequester;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;

/**
 * @author Nicolas Beauger
 * 
 */
public class IntegrationTests extends AbstractRodinDBTests {

    private static final String C1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
	    + "		<org.eventb.core.carrierSet"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.identifier=\"set1\"/>"
	    + "</org.eventb.core.contextFile>";

    private static final String C2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
	    + "	<org.eventb.core.extendsContext"
	    + "		name=\"internal_element1\""
	    + "		org.eventb.core.target=\"C1\"/>"
	    + "	<org.eventb.core.constant"
	    + "		name=\"internal_element1\""
	    + "		org.eventb.core.identifier=\"cst2\"/>"
	    + "	<org.eventb.core.axiom"
	    + "		name=\"internal_element1\""
	    + "		org.eventb.core.label=\"axm1\""
	    + "		org.eventb.core.predicate=\"cst2 ∈ set1\"/>"
	    + "</org.eventb.core.contextFile>";

    private static final String C3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
	    + "		<org.eventb.core.extendsContext"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.target=\"C1\"/>"
	    + "		<org.eventb.core.carrierSet"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.identifier=\"set3\"/>"
	    + "		<org.eventb.core.constant"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.identifier=\"cst2\"/>"
	    + "</org.eventb.core.contextFile>";

    private static final String M1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
	    + "		<org.eventb.core.seesContext"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.target=\"C2\"/>"
	    + "		<org.eventb.core.seesContext"
	    + "			name=\"internal_element2\""
	    + "			org.eventb.core.target=\"C3\"/>"
	    + "		<org.eventb.core.variable"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.identifier=\"var1\"/>"
	    + "		<org.eventb.core.invariant"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.label=\"inv1\""
	    + "			org.eventb.core.predicate=\"var1 ∈ set1\"/>"
	    + "		<org.eventb.core.theorem"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.label=\"thm1\""
	    + "			org.eventb.core.predicate=\"cst2 ∈ set3\"/>"
	    + "</org.eventb.core.machineFile>";

    private static final String M2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
	    + "		<org.eventb.core.refinesMachine"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.target=\"M1\"/>"
	    + "		<org.eventb.core.variable"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.identifier=\"var2\"/>"
	    + "		<org.eventb.core.invariant"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.comment=\"\""
	    + "			org.eventb.core.label=\"inv1\""
	    + "			org.eventb.core.predicate=\"var2 ∈ set3\"/>"
	    + "		<org.eventb.core.theorem"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.comment=\"\""
	    + "			org.eventb.core.label=\"thm1\""
	    + "			org.eventb.core.predicate=\"set1 ⊆ set1\"/>"
	    + "		<org.eventb.core.event"
	    + "			name=\"internal_element1\""
	    + "			org.eventb.core.convergence=\"0\""
	    + "			org.eventb.core.extended=\"false\""
	    + "			org.eventb.core.label=\"evt1\">"
	    + "			<org.eventb.core.witness"
	    + "				name=\"internal_element1\""
	    + "				org.eventb.core.label=\"var1\""
	    + "				org.eventb.core.predicate=\"var1 = 1\"/>"
	    + "		</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

    private static IRodinProject project;

    /**
     * @param name
     */
    public IntegrationTests(String name) {
	super(name);
    }

    protected void setUp() throws Exception {
	super.setUp();
	RodinIndexer.enableIndexing();
	project = createRodinProject("P");
	RodinIndexer.register(new ContextIndexer(), IContextRoot.ELEMENT_TYPE);
	RodinIndexer.register(new MachineIndexer(), IMachineRoot.ELEMENT_TYPE);
	// IndexManager.VERBOSE = true;
	// IndexManager.DEBUG = true;
    }

    protected void tearDown() throws Exception {
	deleteProject("P");
	RodinIndexer.disableIndexing();
	super.tearDown();
    }

    public void testIntegration() throws Exception {

	final IMachineRoot m2 = createMachine(project, "M2", M2);
	final IContextRoot c3 = createContext(project, "C3", C3);
	final IMachineRoot m1 = createMachine(project, "M1", M1);
	final IContextRoot c1 = createContext(project, "C1", C1);
	final IContextRoot c2 = createContext(project, "C2", C2);

	final ICarrierSet set1 = c1.getCarrierSet(INTERNAL_ELEMENT1);
	final IConstant cst2C2 = c2.getConstant(INTERNAL_ELEMENT1);
	final ICarrierSet set3 = c3.getCarrierSet(INTERNAL_ELEMENT1);
	final IConstant cst2C3 = c3.getConstant(INTERNAL_ELEMENT1);
	final IVariable var1 = m1.getVariable(INTERNAL_ELEMENT1);
	final IVariable var2 = m2.getVariable(INTERNAL_ELEMENT1);

	final IAxiom axmC2 = c2.getAxiom(INTERNAL_ELEMENT1);
	final IInvariant invM1 = m1.getInvariant(INTERNAL_ELEMENT1);
	final IInvariant invM2 = m2.getInvariant(INTERNAL_ELEMENT1);
	final ITheorem thmM1 = m1.getTheorem(INTERNAL_ELEMENT1);
	final ITheorem thmM2 = m2.getTheorem(INTERNAL_ELEMENT1);
	final IEvent evtM2 = m2.getEvent(INTERNAL_ELEMENT1);
	final IWitness witM2 = evtM2.getWitness(INTERNAL_ELEMENT1);

	final IIndexRequester requester = RodinIndexer.getIndexRequester();

	// Thread.sleep(800);
	requester.waitUpToDate();

	final IDeclaration declSet1 = requester.getDeclaration(set1);
	final IDeclaration declCst2C2 = requester.getDeclaration(cst2C2);
	final IDeclaration declSet3 = requester.getDeclaration(set3);
	final IDeclaration declCst2C3 = requester.getDeclaration(cst2C3);
	final IDeclaration declVar1 = requester.getDeclaration(var1);
	final IDeclaration declVar2 = requester.getDeclaration(var2);

	final IOccurrence declC1 = makeDecl(c1, declSet1);
	final IOccurrence refSet1AxmC2 = makeRefPred(axmC2, 7, 11, declSet1);
	final IOccurrence refSet1InvM1 = makeRefPred(invM1, 7, 11, declSet1);
	final IOccurrence refSet1ThmM2_1 = makeRefPred(thmM2, 0, 4, declSet1);
	final IOccurrence refSet1ThmM2_2 = makeRefPred(thmM2, 7, 11, declSet1);

	// set1 must be indexed in machines because imports from C2
	// and C3 are identical
	final List<IOccurrence> expSet1 = makeOccList(declC1, refSet1AxmC2,
		refSet1InvM1, refSet1ThmM2_1, refSet1ThmM2_2);

	final IOccurrence declC2 = makeDecl(c2, declCst2C2);
	final IOccurrence refCst2AxmC2 = makeRefPred(c2
		.getAxiom(INTERNAL_ELEMENT1), 0, 4, declCst2C2);

	// cst2 from C2 must not be referenced in M1 because imports in C2 and
	// C3 are
	// different
	final List<IOccurrence> expCst2C2 = makeOccList(declC2, refCst2AxmC2);

	final IOccurrence declC3Set3 = makeDecl(c3, declSet3);
	final IOccurrence refSet3ThmM1 = makeRefPred(thmM1, 7, 11, declSet3);
	final IOccurrence refSet3InvM2 = makeRefPred(invM2, 7, 11, declSet3);
	final List<IOccurrence> expSet3 = makeOccList(declC3Set3, refSet3ThmM1,
		refSet3InvM2);

	// cst2 from C3 must not be referenced in machines either
	final IOccurrence declC3Cst2 = makeDecl(c3, declCst2C3);
	final List<IOccurrence> expCst2C3 = makeOccList(declC3Cst2);

	final IOccurrence declM1 = makeDecl(m1, declVar1);
	final IOccurrence refVar1InvM1 = makeRefPred(invM1, 0, 4, declVar1);
	final IOccurrence refVar1LblWitM2 = makeRefLabel(witM2, declVar1);
	final IOccurrence refVar1PredWitM2 = makeRefPred(witM2, 0, 4, declVar1);

	final List<IOccurrence> expVar1 = makeOccList(declM1, refVar1InvM1,
		refVar1LblWitM2, refVar1PredWitM2);

	final IOccurrence declM2 = makeDecl(m2, declVar2);
	final IOccurrence refVar2InvM2 = makeRefPred(invM2, 0, 4, declVar2);

	final List<IOccurrence> expVar2 = makeOccList(declM2, refVar2InvM2);

	final IOccurrence[] occSet1 = requester.getOccurrences(declSet1);
	final IOccurrence[] occCst2C2 = requester.getOccurrences(declCst2C2);
	final IOccurrence[] occSet3 = requester.getOccurrences(declSet3);
	final IOccurrence[] occCst2C3 = requester.getOccurrences(declCst2C3);
	final IOccurrence[] occVar1 = requester.getOccurrences(declVar1);
	final IOccurrence[] occVar2 = requester.getOccurrences(declVar2);

	ListAssert.assertSameAsArray(expSet1, occSet1, "occ set1");
	ListAssert.assertSameAsArray(expCst2C2, occCst2C2, "occ cst2C2");
	ListAssert.assertSameAsArray(expSet3, occSet3, "occ set3");
	ListAssert.assertSameAsArray(expCst2C3, occCst2C3, "occ cst2C3");
	ListAssert.assertSameAsArray(expVar1, occVar1, "occ var1");
	ListAssert.assertSameAsArray(expVar2, occVar2, "occ var2");
    }

}
