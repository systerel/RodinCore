/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.emf.tests.basics.AbstractRodinEMFCoreTest.createAndSaveRodinFile;
import static org.rodinp.core.emf.tests.basics.AbstractRodinEMFCoreTest.getNamedElement;
import static org.rodinp.core.emf.tests.basics.AbstractRodinEMFCoreTest.getRodinResource;
import static org.rodinp.core.emf.tests.basics.AbstractRodinEMFCoreTest.pNAME;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ICoreImplicitChildProvider;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.api.itf.ImplicitChildProviderManager;
import org.rodinp.core.emf.lightcore.LightElement;
import org.rodinp.core.emf.lightcore.LightcorePackage;
import org.rodinp.core.emf.lightcore.adapters.dboperations.OperationProcessor;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;
import org.rodinp.core.emf.tests.basis.ImplicitHolder;
import org.rodinp.core.emf.tests.basis.RodinTestDependency;
import org.rodinp.core.emf.tests.basis.RodinTestRoot;
import org.rodinp.core.emf.tests.basis.TestBuggyImplicitChildProvider;
import org.rodinp.core.emf.tests.basis.TestImplicitChildProvider;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Tests for the implicit children handling within light models.
 */
public class ImplicitElementHandlingTests {

	private static final String f1Name = "resource1.ert";
	private static final String f2Name = "resource2.ert";
	private static final String f3Name = "resource3.ert";

	private IRodinProject project;
	private IRodinFile rf1;
	private IRodinFile rf2;
	private IRodinFile rf3;

	@Before
	public void setUp() throws CoreException {
		project = AbstractRodinEMFCoreTest.createRodinProject(pNAME);
		project.save(null, true);
		rf1 = createAndSaveRodinFile(project, f1Name);
		rf2 = createAndSaveRodinFile(project, f2Name);
		rf3 = createAndSaveRodinFile(project, f3Name);
	}

	@After
	public void tearDown() throws Exception {
		project.getProject().delete(true, true, null);
		project.getRodinDB().close();
	}

	/**
	 * Checks that the implicit child providing mechanism works.
	 * 
	 * @throws RodinDBException
	 */
	@Test
	public void hasImplicitChildrenTest() throws RodinDBException {
		final ImplicitHolder holder1 = getImplicitHolder(rf1.getRoot(),
				"Holder1");
		final NamedElement s1 = getNamedElement(holder1, "s1");
		final NamedElement s2 = getNamedElement(holder1, "s2");

		final IInternalElement rf2Root = rf2.getRoot();
		final ImplicitHolder holder2 = getImplicitHolder(rf2.getRoot(),
				"Holder2");
		final RodinTestDependency d = getDependencyElement(rf2Root,
				"dependencyToRf1");
		d.setDependency(rf1.getRoot());
		final NamedElement ss1 = getNamedElement(holder2, "ss1");
		final NamedElement ss2 = getNamedElement(holder2, "ss2");

		final IInternalElement rf3Root = rf3.getRoot();
		final RodinTestDependency d2 = getDependencyElement(rf3Root,
				"dependencyToRf2");
		d2.setDependency(rf2.getRoot());

		// The holder which is supposed to be the root for all implicit elements
		// from rf2 and rf1
		final ImplicitHolder holder3 = getImplicitHolder(rf3Root, "Holder3");

		rf1.save(null, true);
		rf2.save(null, true);
		rf3.save(null, true);
		OperationProcessor.waitUpToDate();

		createProvider(ImplicitHolder.ELEMENT_TYPE, NamedElement.ELEMENT_TYPE);
		// now checking the loaded resource
		final ILFile rodinResource = getRodinResource(project,
				rf3.getElementName());
		// We get the light model
		final ILElement rootElement = rodinResource.getRoot();
		final List<ILElement> hold = rootElement
				.getChildrenOfType(ImplicitHolder.ELEMENT_TYPE);

		final ILElement eHolder3 = hold.get(0);
		assertTrue(eHolder3.getElement().equals(holder3));

		final NamedElement[] expecteds = { s1, s2, ss1, ss2 };
		final EList<EObject> eImplicitChildren = ((LightElement)eHolder3).getAllContained(
				LightcorePackage.Literals.IMPLICIT_ELEMENT, false);

		final NamedElement[] actuals = new NamedElement[4];
		int i = 0;
		for (EObject e : eImplicitChildren) {
			if (e != null) {
				assertTrue(
						"The type of the element " + e.toString()
								+ " is not NamedElement.",
						((LightElement) e).getERodinElement() instanceof NamedElement);
				actuals[i] = (NamedElement) ((LightElement) e)
						.getERodinElement();
				i++;
			}
		}
		assertTrue("We found more than 4 implicit children", i == 4);
		assertArrayEquals("Implicit element array should be equal", expecteds,
				actuals);
	}

	/**
	 * Creates a simple dependency from one root element to a parent element,
	 * and checks that the implicit children have been recomputed in the child
	 * root element.
	 */
	@Test
	public void createDependencyAndCheckImplicitChildren()
			throws RodinDBException, InterruptedException {
		final ImplicitHolder holder1 = getImplicitHolder(rf1.getRoot(),
				"Holder1");
		final NamedElement s1 = getNamedElement(holder1, "s1");
		final NamedElement s2 = getNamedElement(holder1, "s2");
		final IInternalElement rf2Root = rf2.getRoot();
		final ImplicitHolder holder2 = getImplicitHolder(rf2.getRoot(),
				"Holder2");
		rf1.save(null, true);
		rf2.save(null, true);

		final ICoreImplicitChildProvider p = createProvider(ImplicitHolder.ELEMENT_TYPE, NamedElement.ELEMENT_TYPE);

		// now checking the loaded resource for rf2
		final ILFile rodinResource = getRodinResource(project,
				rf2.getElementName());
		
		final ILElement rootElement = rodinResource.getRoot();
		
		final ILElement eHolder2 = SynchroUtils.findElement(holder2,
				rootElement);
		// we check that there is no implicit element under eHolder2
		assertTrue(eHolder2.getChildren().isEmpty());
		// now we add the dependency to rf1, so we expect the implicit children
		// to be recalculated from the delta of database
		final RodinTestDependency d = getDependencyElement(rf2Root,
				"dependencyToRf1");
		d.setDependency(rf1.getRoot());
		rf2.save(null, true);
		OperationProcessor.waitUpToDate();
		// we check that implicit elements have been recomputed and that holder2
		// carries s1 and s2
		final List<? extends ILElement> children2 = eHolder2.getChildren();
		assertTrue(children2.size() == 2);
		assertTrue(children2.get(0).getElement().equals(s1));
		assertTrue(children2.get(1).getElement().equals(s2));
		// now we delete the dependency, so it might not be any implicit element
		// left.
		d.delete(true, null);
		rf2.save(null, true);
		OperationProcessor.waitUpToDate();
		// we check that implicit elements have been recomputed and that holder2
		// does not contain implicit children
		assertTrue(eHolder2.getChildren().isEmpty());
		ImplicitChildProviderManager.removeProvider(p);
	}

	/**
	 * Creates a complex dependency from one root parent element to another
	 * parent element, and checks that the implicit children have been
	 * recomputed in the lowest element in the hierarchy.
	 * 
	 * @throws RodinDBException
	 */
	@Test
	public void createComplexDependencyAndCheckImplicitChildren()
			throws RodinDBException {
		final ImplicitHolder holder1 = getImplicitHolder(rf1.getRoot(),
				"Holder1");
		final NamedElement s1 = getNamedElement(holder1, "s1");
		final NamedElement s2 = getNamedElement(holder1, "s2");

		final IInternalElement rf2Root = rf2.getRoot();
		final ImplicitHolder holder2 = getImplicitHolder(rf2.getRoot(),
				"Holder2");
		final NamedElement ss1 = getNamedElement(holder2, "ss1");
		final NamedElement ss2 = getNamedElement(holder2, "ss2");

		final IInternalElement rf3Root = rf3.getRoot();
		final RodinTestDependency d2 = getDependencyElement(rf3Root,
				"dependencyToRf2");
		d2.setDependency(rf2.getRoot());

		// The holder which is supposed to be the root for all implicit elements
		// from rf2 as there is not yet a dependency between rf2 and rf1
		final ImplicitHolder holder3 = getImplicitHolder(rf3Root, "Holder3");

		rf1.save(null, true);
		rf2.save(null, true);
		rf3.save(null, true);
		OperationProcessor.waitUpToDate();

		final ICoreImplicitChildProvider p = createProvider(ImplicitHolder.ELEMENT_TYPE, NamedElement.ELEMENT_TYPE);

		// now checking the loaded resource
		final ILFile rodinResource = getRodinResource(project,
				rf3.getElementName());
		// We get the light model
		final ILElement rootElement = rodinResource.getRoot();

		final ILElement eHolder3 = SynchroUtils.findElement(holder3,
				rootElement);
		final NamedElement[] expecteds1 = { ss1, ss2 };
		int i = 0;
		final List<? extends ILElement> children = eHolder3.getChildren();
		for (ILElement e : children){
			assertTrue(e.getElement().equals(expecteds1[i]));
			i++;
		}

		// now adding a dependency between parents rf2 and rf1
		final RodinTestDependency d = getDependencyElement(rf2Root,
				"dependencyToRf1");
		d.setDependency(rf1.getRoot());
		OperationProcessor.waitUpToDate();

		// checks that rf3 contains implicit children from rf1 and rf2 in the
		// right order
		final NamedElement[] expecteds2 = { s1, s2, ss1, ss2 };
		int j = 0;
		for (ILElement e : children){
			assertTrue(e.getElement().equals(expecteds2[j]));
			j++;
		}
		
		// now removing the dependency between rf3 and rf2
		final RodinTestDependency[] dArray = rf3.getRoot().getChildrenOfType(
				RodinTestDependency.ELEMENT_TYPE);
		assertTrue(dArray.length == 1);
		final RodinTestDependency rDependency = dArray[0];
		assertNotNull(rDependency);
		rDependency.delete(true, null);
		OperationProcessor.waitUpToDate();

		// check that there is no more implicit children
		assertTrue(eHolder3.getChildren().isEmpty());
		
		ImplicitChildProviderManager.removeProvider(p);
	}
	
	/**
	 * Checks that the implicit child providing mechanism works.
	 */
	@Test
	public void resistToBuggyProvider() throws RodinDBException {		
		final IInternalElement rf1root = rf1.getRoot();
		final NamedElement s1 = getNamedElement(rf1root, "s1");
		final NamedElement s2 = getNamedElement(rf1root, "s2");
		rf1.save(null, true);

		final ICoreImplicitChildProvider p = new TestBuggyImplicitChildProvider();
		ImplicitChildProviderManager.addProviderFor(p,
				RodinTestRoot.ELEMENT_TYPE, NamedElement.ELEMENT_TYPE);
		
		// now checking the loaded resource
		final ILFile rodinResource = getRodinResource(project,
				rf1.getElementName());
		// We get the light model
		final ILElement rootElement = rodinResource.getRoot();
		final ILElement eS1 = SynchroUtils.findElement(s1, rootElement);
		// removing eS1 to create a delta and throw the NPE
		EcoreUtil.remove((LightElement) eS1);
		// If we reach this point, the exception didn't break nothing
		assertTrue(rootElement.getChildren().size() == 1);
		assertTrue(rootElement.getChildren().get(0).getElement().equals(s2));
		//ImplicitChildProviderManager.removeProvider(p);
	}
	
	public ICoreImplicitChildProvider createProvider(
			IInternalElementType<? extends IInternalElement> parentType,
			IInternalElementType<? extends IInternalElement> childType) {
		final ICoreImplicitChildProvider p = new TestImplicitChildProvider();
		ImplicitChildProviderManager.addProviderFor(p, parentType, childType);
		return p;
	}

	private static ImplicitHolder getImplicitHolder(IInternalElement parent,
			String name) throws RodinDBException {
		final ImplicitHolder ie = parent.getInternalElement(
				ImplicitHolder.ELEMENT_TYPE, name);
		ie.create(null, null);
		return ie;
	}

	private static RodinTestDependency getDependencyElement(
			IInternalElement parent, String name) throws RodinDBException {
		final RodinTestDependency d = parent.getInternalElement(
				RodinTestDependency.ELEMENT_TYPE, name);
		d.create(null, null);
		return d;
	}

}
