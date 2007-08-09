package org.eventb.ui.eventbeditor.editpage.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.editpage.CComboEditComposite;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.internal.ui.eventbeditor.editpage.TextEditComposite;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class TestAttributeRelUISpecRegistry extends EventBUITest {

	IAttributeRelUISpecRegistry registry = AttributeRelUISpecTestRegistry
			.getDefault();

	@Test
	public void testCreateVariables() {
		IMachineFile m0;
		try {
			m0 = createMachine("m0");
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false);
			return;
		}

		IEventBEditor<?> editor;
		try {
			editor = openEditor(m0);
		} catch (PartInitException e) {
			assertTrue(
					"Problem occured when opening machine m0 with Event-B Editor",
					false);
			return;
		}

		// Test creating variable
		IInternalElement element;
		try {
			element = registry.createElement(editor, m0,
					IVariable.ELEMENT_TYPE, null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new variable within m0",
					false);
			return;
		}

		assertTrue("Creating a variable unsuccessfully", element != null);
		assertTrue("Element created is not a variable",
				element instanceof IVariable);
		IVariable var = (IVariable) element;
		try {
			assertTrue("New variable should has identifier string", var
					.hasIdentifierString());
			assertEquals("Incorrect identifier string for new variable",
					"var1", var.getIdentifierString());
		} catch (RodinDBException e) {
			assertTrue("Exception: New variable should has identifier string",
					false);
			return;
		}

		try {
			assertFalse("New variable should has no comment", var.hasComment());
		} catch (RodinDBException e) {
			assertTrue("Exception: New variable should has no comment", false);
			return;
		}

	}

	@Test
	public void testCreateInvariants() {
		IMachineFile m0;
		try {
			m0 = createMachine("m0");
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false);
			return;
		}

		IEventBEditor<?> editor;
		try {
			editor = openEditor(m0);
		} catch (PartInitException e) {
			assertTrue(
					"Problem occured when opening machine m0 with Event-B Editor",
					false);
			return;
		}

		// Test creating invariant
		IInternalElement element;
		try {
			element = registry.createElement(editor, m0,
					IInvariant.ELEMENT_TYPE, null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new invariant within m0",
					false);
			return;
		}

		assertTrue("Creating an invariant unsuccessfully", element != null);
		assertTrue("Element created is not an invariant",
				element instanceof IInvariant);
		IInvariant inv = (IInvariant) element;
		try {
			assertTrue("New invariant should has label", inv.hasLabel());
			assertEquals("Incorrect label for new invariant", "inv1", inv
					.getLabel());
		} catch (RodinDBException e) {
			assertTrue("Exception: New invariant should has label", false);
			return;
		}

		try {
			assertFalse("New invariant should has no predicate", inv
					.hasPredicateString());
		} catch (RodinDBException e) {
			assertTrue("Exception: New invariant should has no predicate",
					false);
			return;
		}

		try {
			assertFalse("New invariant should has no comment", inv.hasComment());
		} catch (RodinDBException e) {
			assertTrue("Exception: New invariant should has no comment", false);
			return;
		}
	}

	@Test
	public void testCreateEvents() {
		IMachineFile m0;
		try {
			m0 = createMachine("m0");
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false);
			return;
		}

		IEventBEditor<?> editor;
		try {
			editor = openEditor(m0);
		} catch (PartInitException e) {
			assertTrue(
					"Problem occured when opening machine m0 with Event-B Editor",
					false);
			return;
		}

		// Test creating event
		IInternalElement element;
		try {
			element = registry.createElement(editor, m0, IEvent.ELEMENT_TYPE,
					null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new event within m0",
					false);
			return;
		}

		assertTrue("Creating an event unsuccessfully", element != null);
		assertTrue("Element created is not an event", element instanceof IEvent);
		IEvent evt = (IEvent) element;
		try {
			assertFalse("New event should has no label", evt.hasLabel());
		} catch (RodinDBException e) {
			assertTrue("Exception: New event should has no label", false);
			return;
		}

		try {
			assertFalse("New event should has no comment", evt.hasComment());
		} catch (RodinDBException e) {
			assertTrue("Exception: New event should has no comment", false);
			return;
		}

		try {
			assertTrue("New event should has inherited attribute", evt.hasInherited());
			assertFalse("New event should not be inherited", evt.isInherited());
		} catch (RodinDBException e) {
			assertTrue("Exception: New event should has no inherited", false);
			return;
		}
	
		try {
			assertFalse("New event should has no convergence", evt.hasConvergence());
		} catch (RodinDBException e) {
			assertTrue("Exception: New event should has no convergence", false);
			return;
		}
	
	}

	@Test
	public void testCreateParameters() {
		IMachineFile m0;
		try {
			m0 = createMachine("m0");
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false);
			return;
		}

		IEventBEditor<?> editor;
		try {
			editor = openEditor(m0);
		} catch (PartInitException e) {
			assertTrue(
					"Problem occured when opening machine m0 with Event-B Editor",
					false);
			return;
		}

		// Creating event
		IInternalElement element;
		try {
			element = registry.createElement(editor, m0, IEvent.ELEMENT_TYPE,
					null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new event within m0",
					false);
			return;
		}

		IEvent evt = (IEvent) element;

		// Test creating parameter
		try {
			element = registry.createElement(editor, evt,
					IVariable.ELEMENT_TYPE, null);
		} catch (CoreException e) {
			assertTrue(
					"Problem occured when creating new parameter within evt1",
					false);
			return;
		}

		assertTrue("Creating a parameter unsuccessfully", element != null);
		assertTrue("Element created is not a parameter",
				element instanceof IVariable);
		IVariable param = (IVariable) element;
		try {
			assertTrue("New parameter should has identifier string", param
					.hasIdentifierString());
			assertEquals("Incorrect identifier string for new parameter",
					"var1", param.getIdentifierString());
		} catch (RodinDBException e) {
			assertTrue("Exception: New parameter should has identifier string",
					false);
			return;
		}

		try {
			assertFalse("New parameter should has no comment", param
					.hasComment());
		} catch (RodinDBException e) {
			assertTrue("Exception: New variable should has no comment", false);
			return;
		}

	}

	@Test
	public void testCreateGuards() {
		IMachineFile m0;
		try {
			m0 = createMachine("m0");
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false);
			return;
		}

		IEventBEditor<?> editor;
		try {
			editor = openEditor(m0);
		} catch (PartInitException e) {
			assertTrue(
					"Problem occured when opening machine m0 with Event-B Editor",
					false);
			return;
		}

		// Creating event
		IInternalElement element;
		try {
			element = registry.createElement(editor, m0, IEvent.ELEMENT_TYPE,
					null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new event within m0",
					false);
			return;
		}

		IEvent evt = (IEvent) element;

		// Test creating guard
		try {
			element = registry.createElement(editor, evt, IGuard.ELEMENT_TYPE,
					null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new guard within evt1",
					false);
			return;
		}

		assertTrue("Creating a guard unsuccessfully", element != null);
		assertTrue("Element created is not a guard", element instanceof IGuard);
		IGuard guard = (IGuard) element;

		try {
			assertFalse("New guard should has no label", guard.hasLabel());
		} catch (RodinDBException e) {
			assertTrue("Exception: New guard should has no label", false);
			return;
		}

		try {
			assertFalse("New guard should has no predicate string", guard
					.hasPredicateString());
		} catch (RodinDBException e) {
			assertTrue("Exception: New guard should has no predicate string",
					false);
			return;
		}

		try {
			assertFalse("New guard should has no comment", guard.hasComment());
		} catch (RodinDBException e) {
			assertTrue("Exception: New guard should has no comment", false);
			return;
		}

	}

	@Test
	public void testCreateActions() {
		IMachineFile m0;
		try {
			m0 = createMachine("m0");
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false);
			return;
		}

		IEventBEditor<?> editor;
		try {
			editor = openEditor(m0);
		} catch (PartInitException e) {
			assertTrue(
					"Problem occured when opening machine m0 with Event-B Editor",
					false);
			return;
		}

		// Creating event
		IInternalElement element;
		try {
			element = registry.createElement(editor, m0, IEvent.ELEMENT_TYPE,
					null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new event within m0",
					false);
			return;
		}

		IEvent evt = (IEvent) element;

		// Test creating action
		try {
			element = registry.createElement(editor, evt, IAction.ELEMENT_TYPE,
					null);
		} catch (CoreException e) {
			assertTrue("Problem occured when creating new action within evt1",
					false);
			return;
		}

		assertTrue("Creating an action unsuccessfully", element != null);
		assertTrue("Element created is not an action",
				element instanceof IAction);
		IAction act = (IAction) element;
		try {
			assertTrue("New action should has a label", act.hasLabel());
			assertEquals("Incorrect label for new action", "act1", act
					.getLabel());
		} catch (RodinDBException e) {
			assertTrue("Exception: New action should has a label", false);
			return;
		}

		try {
			assertFalse("New action should has no assignment string", act
					.hasAssignmentString());
		} catch (RodinDBException e) {
			assertTrue("Exception: New action should has no assignment string",
					false);
			return;
		}

		try {
			assertFalse("New action should has no comment", act.hasComment());
		} catch (RodinDBException e) {
			assertTrue("Exception: New action should has no comment", false);
			return;
		}

	}

	@Test
	public void testGetNumberOfAttributes() {
		// IVariable
		int numVarAttributes = registry
				.getNumberOfAttributes(IVariable.ELEMENT_TYPE);
		assertEquals("Incorrect number of attributes for IVariable", 1,
				numVarAttributes);

		// IInvariant
		int numInvAttributes = registry
				.getNumberOfAttributes(IInvariant.ELEMENT_TYPE);
		assertEquals("Incorrect number of attributes for IInvariant", 2,
				numInvAttributes);

		// IEvent
		int numEvtAttributes = registry
				.getNumberOfAttributes(IEvent.ELEMENT_TYPE);
		assertEquals("Incorrect number of attributes for IEvent", 2,
				numEvtAttributes);

		// IGuard
		int numGrdAttributes = registry
				.getNumberOfAttributes(IGuard.ELEMENT_TYPE);
		assertEquals("Incorrect number of attributes for IGuard", 0,
				numGrdAttributes);

		// IEvent
		int numActAttributes = registry
				.getNumberOfAttributes(IAction.ELEMENT_TYPE);
		assertEquals("Incorrect number of attributes for IAction", 1,
				numActAttributes);
	}

	@Test
	public void testGetVariableEditComposites() {
		IEditComposite[] editComposites = registry
				.getEditComposites(IVariable.ELEMENT_TYPE);
		assertEquals("Incorrect number of edit composite", 1,
				editComposites.length);

		assertTrue("First edit composite should be a TextEditComposite",
				editComposites[0] instanceof TextEditComposite);
	}

	@Test
	public void testGetInvariantEditComposites() {
		IEditComposite[] editComposites = registry
				.getEditComposites(IInvariant.ELEMENT_TYPE);
		assertEquals("Incorrect number of edit composite", 2,
				editComposites.length);

		assertTrue("First edit composite should be a TextEditComposite",
				editComposites[0] instanceof TextEditComposite);

		assertTrue("Secibd edit composite should be a TextEditComposite",
				editComposites[1] instanceof TextEditComposite);
	}

	@Test
	public void testGetEventEditComposites() {
		IEditComposite[] editComposites = registry
				.getEditComposites(IEvent.ELEMENT_TYPE);
		assertEquals("Incorrect number of edit composite", 2,
				editComposites.length);

		assertTrue("First edit composite should be a TextEditComposite",
				editComposites[0] instanceof CComboEditComposite);

		assertTrue("Second edit composite should be a TextEditComposite",
				editComposites[1] instanceof CComboEditComposite);
	}

}
