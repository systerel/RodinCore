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
 *     Systerel - mathematical language V2
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITraceableElement;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBTest extends BuilderTest {
	
	public ITypeEnvironment emptyEnv = factory.makeTypeEnvironment();
	public final Type intType = factory.makeIntegerType();
	public final Type boolType = factory.makeBooleanType();
	public final Type powIntType = factory.makePowerSetType(intType);
	public final Type relIntType = factory.makePowerSetType(factory.makeProductType(intType, intType));

	public EventBTest() {
		super();
	}

	public EventBTest(String name) {
		super(name);
	}

	public void addAxioms(IContextRoot root, String[] names, String[] axioms,
			boolean... derived) throws RodinDBException {
		for (int i = 0; i < names.length; i++) {
			addAxiom(root, names[i], axioms[i], derived[i]);
		}
	}

	public IAxiom addAxiom(IContextRoot root, String name, String predicate,
			boolean derived) throws RodinDBException {
		final IAxiom axiom = root.createChild(IAxiom.ELEMENT_TYPE, null, null);
		axiom.setPredicateString(predicate, null);
		axiom.setLabel(name, null);
		axiom.setTheorem(derived, null);
		return axiom;
	}

	public void addAxioms(IContextRoot root, String... strings)
			throws RodinDBException {
		final int length = strings.length;
		assert length % 2 == 0;
		for (int i = 0; i < length; i += 2) {
			final IAxiom axiom = root.createChild(IAxiom.ELEMENT_TYPE, null, null);
			axiom.setLabel(strings[i], null);
			axiom.setPredicateString(strings[i + 1], null);
			axiom.setTheorem(false, null);
		}
	}

	public void addAxioms(IRodinFile rodinFile, String[] names,
			String[] axioms, boolean...derived) throws RodinDBException {
		final IContextRoot root = (IContextRoot) rodinFile.getRoot();
		addAxioms(root, names, axioms, derived);
	}

	public void addAxioms(IRodinFile rodinFile,
			String... strings) throws RodinDBException {
		final IContextRoot root = (IContextRoot) rodinFile.getRoot();
		addAxioms(root, strings);
	}
		
	
	public void addCarrierSets(IRodinFile rodinFile, String... names) throws RodinDBException {
		final IContextRoot root = (IContextRoot) rodinFile.getRoot();
		addCarrierSets(root, names);
	}
	
	public void addCarrierSets(IContextRoot root, String... names) throws RodinDBException {
		for(String name : names) {
			ICarrierSet set = root.createChild(ICarrierSet.ELEMENT_TYPE, null, null);
			set.setIdentifierString(name, null);
		}
			
	}
	
	public void addConstants(IRodinFile rodinFile, String... names) throws RodinDBException {
		final IContextRoot root = (IContextRoot) rodinFile.getRoot();
		addConstants(root, names);
	}

	public void addConstants(IContextRoot root, String... names) throws RodinDBException {
		for(String name : names) {
			IConstant constant = root.createChild(IConstant.ELEMENT_TYPE, null, null);
			constant.setIdentifierString(name, null);
		}
	}

	
	public void addEventRefines(IEvent event, String... names) throws RodinDBException {
		for (String name : names) {
			IRefinesEvent refines = event.createChild(IRefinesEvent.ELEMENT_TYPE, null, null);
			refines.setAbstractEventLabel(name, null);
		}
	}

	public void addEventWitness(IEvent event, String label, String pred)
			throws RodinDBException {
		final IWitness witness = event.createChild(IWitness.ELEMENT_TYPE, null, null);
		witness.setLabel(label, null);
		witness.setPredicateString(pred, null);
	}

	public void addEventWitnesses(IEvent event, String... strings)
			throws RodinDBException {
		assert strings.length % 2 == 0;
		for (int i = 0; i < strings.length; i += 2) {
			addEventWitness(event, strings[i], strings[i + 1]);
		}
	}

	public void addEventWitnesses(IEvent event, String[] labels,
			String[] predicates) throws RodinDBException {
		assert labels.length == predicates.length;
		for (int i = 0; i < labels.length; i++) {
			addEventWitness(event, labels[i], predicates[i]);
		}
	}

	public IEvent addEvent(IMachineRoot root, String name, String[] params,
			String[] guardNames, String[] guards, String[] actionNames,
			String[] actions) throws RodinDBException {
		return addEvent(root, name, params, guardNames, guards,
				new boolean[guards.length], actionNames, actions);
	}
	
	public IEvent addEvent(IMachineRoot root, 
				String name,
				String[] params,
				String[] guardNames,
				String[] guards,
				boolean[] theorems,
				String[] actionNames,
				String[] actions
	) throws RodinDBException {
		IEvent event = root.createChild(IEvent.ELEMENT_TYPE, null, null);
		event.setLabel(name, null);
		event.setExtended(false, null);
		event.setConvergence(IConvergenceElement.Convergence.ORDINARY, null);
		for(int i=0; i<params.length; i++) {
			IParameter parameter = event.createChild(IParameter.ELEMENT_TYPE, null, null);
			parameter.setIdentifierString(params[i], null);
			
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = event.createChild(IGuard.ELEMENT_TYPE, null, null);
			guard.setPredicateString(guards[i], null);
			guard.setLabel(guardNames[i], null);
			guard.setTheorem(theorems[i], null);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = event.createChild(IAction.ELEMENT_TYPE, null, null);
			action.setAssignmentString(actions[j], null);
			action.setLabel(actionNames[j], null);
		}
		return event;
	}
	
	public IEvent addInitialisation(IMachineRoot root, String... variables) throws RodinDBException {
		String assn = variables.length == 0 ? null : variables[0];
		for (int k=1; k<variables.length; k++) {
			assn += "," + variables[k];
		}
		if (assn != null) 
			assn += ":∣⊤";
		String[] ll = (assn == null) ? makeSList() : makeSList("INIT");
		String[] aa = (assn == null) ? makeSList() : makeSList(assn);
		return addInitialisation(root, ll, aa);
	}
	
	public IEvent addEvent(IRodinFile rodinFile, 
			String name) throws RodinDBException {
		final IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		return addEvent(root, name);
	}

	
	public IEvent addEvent(IMachineRoot root, 
			String name) throws RodinDBException {
		return addEvent(root, name, makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
	}

	
	public IEvent addExtendedEvent(IMachineRoot root, 
			String name) throws RodinDBException {
		IEvent event = root.createChild(IEvent.ELEMENT_TYPE, null, null);
		event.setLabel(name, null);
		event.setExtended(true, null);
		event.setConvergence(IConvergenceElement.Convergence.ORDINARY, null);
		return event;
		
	}
	
	public void setExtended(IEvent event) throws RodinDBException {
		event.setExtended(true, null);
	}
	
	public IEvent addInitialisation(IMachineRoot root, String[] actionNames,
			String[] actions) throws RodinDBException {

		return addEvent(root, IEvent.INITIALISATION, makeSList(),
				makeSList(), makeSList(), actionNames, actions);
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
	
	
	public void addInvariant(IMachineRoot root, String label,
			String pred, boolean derived) throws RodinDBException {
		final IInvariant invariant = root.createChild(IInvariant.ELEMENT_TYPE, null, null);
		invariant.setLabel(label, null);
		invariant.setTheorem(derived, null);
		invariant.setPredicateString(pred, null);
	}
	
	
	public void addInvariant(IRodinFile rodinFile, String label, String pred, boolean derived)
			throws RodinDBException {
		final IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		addInvariant(root, label, pred, derived);
	}

	public void addInvariants(IRodinFile rodinFile,
			String[] labels, String[] preds, boolean...derived) throws RodinDBException {
		final IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		addInvariants(root, labels, preds, derived);
	}

	public void addInvariants(IMachineRoot root, String[] labels,
			String[] preds, boolean...derived) throws RodinDBException {
		for (int i = 0; i < labels.length; i++) {
			addInvariant(root, labels[i], preds[i], derived[i]);
		}
	}
	
	

	
	public void addVariant(IMachineRoot root, String expression) throws RodinDBException {
		IVariant variant = root.createChild(IVariant.ELEMENT_TYPE, null, null);
		variant.setExpressionString(expression, null);
	}
	
	public void addVariant(IRodinFile rodinFile, String expression) throws RodinDBException {
		IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		addVariant(root, expression);
	}

	public void addMachineSees(IMachineRoot root, String name) throws RodinDBException {
		ISeesContext sees = root.createChild(ISeesContext.ELEMENT_TYPE, null, null);
		sees.setSeenContextName(name, null);
	}

	public void addMachineRefines(IRodinFile rodinFile, String name) throws RodinDBException {
		IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		addMachineRefines(root, name);
	}
	

	public void addMachineRefines(IMachineRoot root, String name) throws RodinDBException {
		IRefinesMachine refines = root.createChild(IRefinesMachine.ELEMENT_TYPE, null, null);
		refines.setAbstractMachineName(name, null);
	}


	public void addContextExtends(IRodinFile rodinFile, String name) throws RodinDBException {
		IContextRoot root = (IContextRoot) rodinFile.getRoot();
		addContextExtends(root, name);
	}


	public void addContextExtends(IContextRoot root, String name) throws RodinDBException {
		IExtendsContext extendsContext = root.createChild(IExtendsContext.ELEMENT_TYPE, null, null);
		extendsContext.setAbstractContextName(name, null);
	}

	public void addVariables(IRodinFile rodinFile, String... names) throws RodinDBException {
		IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		addVariables(root, names);
	}

	public void addVariables(IMachineRoot root, String... names) throws RodinDBException {
		for(String name : names) {
			IVariable variable = root.createChild(IVariable.ELEMENT_TYPE, null, null);
			variable.setIdentifierString(name, null);
		}
	}

	
	public static String[] makeSList(String...strings) {
		return strings;
	}

	public static boolean[] makeBList(boolean...bools) {
		return bools;
	}

	public static String[] makePrime(String...strings) {
		String[] result = strings.clone();
		for (int i = 0; i < strings.length; i++) {
			result[i] = strings[i] + "'";
		}
		return result;
	}

	public static String makeMaplet(String...strings) {
		final StringBuilder b = new StringBuilder();
		String sep = "";
		for (String s: strings) {
			b.append(sep);
			b.append(s);
			sep = " ↦ ";
		}
		return b.toString();
	}

	// generic methods
	
	public void addPredicates(IRodinFile rodinFile, String[] names,
			String[] predicates, boolean...derived) throws RodinDBException {
		IInternalElement root = rodinFile.getRoot();
		assert (root instanceof IMachineRoot) || (root instanceof IContextRoot);
		if (root instanceof IMachineRoot) {
			addInvariants((IMachineRoot) root, names, predicates, derived);
		} else {
			addAxioms((IContextRoot) root, names, predicates, derived);
		}
	}

	public void addIdents(IRodinFile rodinFile, String... names)
			throws RodinDBException {
		IInternalElement root = rodinFile.getRoot();
		assert (root instanceof IMachineRoot) || (root instanceof IContextRoot);
		if (root instanceof IMachineRoot) {
			addVariables((IMachineRoot) root, names);
		} else {
			addConstants((IContextRoot) root, names);
		}
	}

	public void addSuper(IRodinFile rodinFile, String name)
			throws RodinDBException {
		IInternalElement root = rodinFile.getRoot();
		assert (root instanceof IMachineRoot) || (root instanceof IContextRoot);
		if (root instanceof IMachineRoot) {
			addMachineRefines((IMachineRoot)root, name);
		} else {
			addContextExtends((IContextRoot)root, name);
		}
	}

	public String getNormalizedExpression(String input, ITypeEnvironment environment) {
		Expression expr = factory.parseExpression(input, V2, null).getParsedExpression();
		expr.typeCheck(environment);
		assertTrue(expr.isTypeChecked());
		return expr.toStringWithTypes();
	}

	public String getNormalizedPredicate(String input, ITypeEnvironment environment) {
		Predicate pred = factory.parsePredicate(input, V2, null).getParsedPredicate();
		pred.typeCheck(environment);
		assertTrue(pred.isTypeChecked());
		return pred.toStringWithTypes();
	}

	public String getNormalizedAssignment(String input, ITypeEnvironment environment) {
		Assignment assn = factory.parseAssignment(input, V2, null).getParsedAssignment();
		assn.typeCheck(environment);
		assertTrue(assn.isTypeChecked());
		return assn.toStringWithTypes();
	}

	public Set<IEventBRoot> roots;
	
	@Override
	protected void runBuilder() throws CoreException {
		super.runBuilder();
		checkSources();
		roots.clear(); // forget
	}

	public static class DeltaListener implements IElementChangedListener {

		final ArrayList<IRodinElementDelta> deltas;

		public DeltaListener() {
			deltas = new ArrayList<IRodinElementDelta>();
		}

		public void elementChanged(ElementChangedEvent event) {
			deltas.add(event.getDelta());
		}

		public void assertNotChanged(IEventBRoot... roots) {
			for (IEventBRoot root : roots) {
				for (IRodinElementDelta delta : deltas) {
					assertNotChanged(delta, root.getRodinFile());
				}
			}
		}

		public void assertNotChanged(IRodinElementDelta delta, IRodinFile rf) {
			final IRodinElement elem = delta.getElement();
			if (elem.equals(rf)) {
				fail("File " + rf + " should not have changed.");
			}
			if (elem.isAncestorOf(rf)) {
				for (IRodinElementDelta child : delta.getAffectedChildren()) {
					assertNotChanged(child, rf);
				}
			}

		}
	}

	protected void runBuilderNotChanged(IEventBRoot... rfs) throws CoreException {
		final DeltaListener listener = new DeltaListener();
		RodinCore.addElementChangedListener(listener);
		super.runBuilder();
		RodinCore.removeElementChangedListener(listener);
		listener.assertNotChanged(rfs);
	}

	private void checkSources() throws RodinDBException {
		for (IEventBRoot root : roots) {
			if (root.exists())
				checkSources(root);
		}
	}

	
	private boolean isContextOrMachine(IOpenable element){
		if (element instanceof IRodinFile){
			IInternalElement root = ((IRodinFile)element).getRoot();
			return (root instanceof IContextRoot || root instanceof IMachineRoot);
		} else {
			return false;
		}
	}
	
	private void checkSources(IRodinElement element) throws RodinDBException {
		if (element instanceof ITraceableElement) {
			IRodinElement sourceElement = ((ITraceableElement) element).getSource();
			
			assertTrue("source reference must be in unchecked file",
					isContextOrMachine(sourceElement.getOpenable()));
		}
		if (element instanceof IInternalElement) {
		
			IInternalElement parent = (IInternalElement) element;
			
			IRodinElement[] elements = parent.getChildren();
			
			for(IRodinElement child : elements)
				checkSources(child);
			
		}
	}

	protected void addRoot(IEventBRoot root) {
		if (!roots.contains(root))
			roots.add(root);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		roots = new HashSet<IEventBRoot>();
	}

	@Override
	protected void tearDown() throws Exception {
		roots = null;
		super.tearDown();
	}

}
