package org.eventb.ui.eventbeditor.editpage.tests;

import junit.framework.TestCase;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.editpage.EditSectionRegistry;
import org.eventb.ui.tests.utils.Util;
import org.rodinp.core.IElementType;

public class TestEditPageRegistry extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetChildrenTypes() {
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();

		IElementType<?>[] types = editSectionRegistry
				.getChildrenTypes(IMachineFile.ELEMENT_TYPE);

		assertChildrenTypes(
				"Children types of Machine File ",
				"org.eventb.core.variable\n" + "org.eventb.core.invariant\n"
						+ "org.eventb.core.theorem\n" + "org.eventb.core.event",
				types);

		types = editSectionRegistry.getChildrenTypes(IVariable.ELEMENT_TYPE);

		assertChildrenTypes("Children types of Variable ", "", types);

		types = editSectionRegistry.getChildrenTypes(IInvariant.ELEMENT_TYPE);

		assertChildrenTypes("Children types of Invariant ", "", types);

		types = editSectionRegistry.getChildrenTypes(ITheorem.ELEMENT_TYPE);

		assertChildrenTypes("Children types of Theorem ", "", types);

		types = editSectionRegistry.getChildrenTypes(IEvent.ELEMENT_TYPE);

		assertChildrenTypes("Children types of Event ",
				"org.eventb.core.refinesEvent\n" + "org.eventb.core.variable\n"
						+ "org.eventb.core.guard\n" + "org.eventb.core.action",
				types);

		types = editSectionRegistry.getChildrenTypes(IGuard.ELEMENT_TYPE);

		assertChildrenTypes("Children types of Guard ", "", types);

		types = editSectionRegistry.getChildrenTypes(IAction.ELEMENT_TYPE);

		assertChildrenTypes("Children types of Action ", "", types);
	}

	public void testGetPrefix() {
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();

		String prefix = editSectionRegistry.getPrefix(
				IMachineFile.ELEMENT_TYPE, IVariable.ELEMENT_TYPE);

		assertEquals("Prefix for Variables ", "VARIABLES", prefix);

		prefix = editSectionRegistry.getPrefix(IMachineFile.ELEMENT_TYPE,
				IInvariant.ELEMENT_TYPE);

		assertEquals("Prefix for Invariants ", "INVARIANTS", prefix);

		prefix = editSectionRegistry.getPrefix(IMachineFile.ELEMENT_TYPE,
				ITheorem.ELEMENT_TYPE);

		assertEquals("Prefix for Theorems ", "THEOREMS", prefix);

		prefix = editSectionRegistry.getPrefix(IMachineFile.ELEMENT_TYPE,
				IEvent.ELEMENT_TYPE);

		assertEquals("Prefix for Events ", "EVENTS", prefix);

		prefix = editSectionRegistry.getPrefix(IEvent.ELEMENT_TYPE,
				IVariable.ELEMENT_TYPE);

		assertEquals("Prefix for Local Variables ", "ANY", prefix);

		prefix = editSectionRegistry.getPrefix(IEvent.ELEMENT_TYPE,
				IGuard.ELEMENT_TYPE);

		assertEquals("Prefix for Guards ", "WHERE", prefix);

		prefix = editSectionRegistry.getPrefix(IEvent.ELEMENT_TYPE,
				IAction.ELEMENT_TYPE);

		assertEquals("Prefix for Actions ", "THEN", prefix);
	}

	public void testGetPostfix() {
		EditSectionRegistry editSectionRegistry = EditSectionRegistry
				.getDefault();

		String prefix = editSectionRegistry.getPostfix(
				IMachineFile.ELEMENT_TYPE, IVariable.ELEMENT_TYPE);

		assertEquals("Postfix for Variables ", null, prefix);

		prefix = editSectionRegistry.getPostfix(IMachineFile.ELEMENT_TYPE,
				IInvariant.ELEMENT_TYPE);

		assertEquals("Postfix for Invariants ", null, prefix);

		prefix = editSectionRegistry.getPostfix(IMachineFile.ELEMENT_TYPE,
				ITheorem.ELEMENT_TYPE);

		assertEquals("Postfix for Theorems ", null, prefix);

		prefix = editSectionRegistry.getPostfix(IMachineFile.ELEMENT_TYPE,
				IEvent.ELEMENT_TYPE);

		assertEquals("Postfix for Events ", "END", prefix);

		prefix = editSectionRegistry.getPostfix(IEvent.ELEMENT_TYPE,
				IVariable.ELEMENT_TYPE);

		assertEquals("Postfix for Local Variables ", null, prefix);

		prefix = editSectionRegistry.getPostfix(IEvent.ELEMENT_TYPE,
				IGuard.ELEMENT_TYPE);

		assertEquals("Postfix for Guards ", null, prefix);

		prefix = editSectionRegistry.getPostfix(IEvent.ELEMENT_TYPE,
				IAction.ELEMENT_TYPE);

		assertEquals("Postfix for Actions ", "END", prefix);
	}

	void assertChildrenTypes(String message, String expected,
			IElementType<?>[] types) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IElementType<?> type : types) {
			if (sep)
				builder.append('\n');
			builder.append(type);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
