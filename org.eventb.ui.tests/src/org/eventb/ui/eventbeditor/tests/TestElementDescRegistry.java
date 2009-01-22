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
package org.eventb.ui.eventbeditor.tests;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXTENDED_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.internal.ui.eventbeditor.elementdesc.AttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ComboDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.TextDesc;
import org.eventb.internal.ui.eventbeditor.manipulation.AssignmentAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.CommentAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ConvergenceAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExpressionAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendedAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendsContextAbstractContextNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IdentifierAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.LabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.NullAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.PredicateAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesEventAbstractEventLabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesMachineAbstractMachineNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.SeesContextNameAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

public class TestElementDescRegistry extends TestCase {

	class NullAttributeDesc implements IAttributeDesc {

		public IEditComposite createWidget() {
			return null;
		}

		public IAttributeType getAttributeType() {
			return new IAttributeType() {

				public java.lang.String getId() {
					return "";
				}

				public java.lang.String getName() {
					return "";
				}

				@Override
				public boolean equals(Object obj) {
					if (!(obj instanceof IAttributeType))
						return false;
					final IAttributeType at = (IAttributeType) obj;
					return this.getId().equals(at.getId())
							&& this.getName().equals(at.getName());
				}
			};
		}

		public IAttributeManipulation getManipulation() {
			return new NullAttributeManipulation();
		}

		public String getSuffix() {
			return "";
		}

		public boolean isHorizontalExpand() {
			return false;
		}

		public String getPrefix() {
			return "";
		}

	}

	private IElementDescRegistry registry;

	private final IInternalElementType<?>[] noChildren = new IInternalElementType<?>[0];
	private final AttributeDesc[] noAttribute = new AttributeDesc[0];
	private final String nullPrefix = "";
	private final IAttributeDesc nullAttribute = new NullAttributeDesc();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		registry = ElementDescRegistry.getInstance();
	}

	public void testGetMachineDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IMachineRoot.ELEMENT_TYPE);

		assertElementDesc(desc, "MACHINE", "END",
				"icons/full/obj16/mch_obj.gif", nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				noAttribute);

		assertChildrens("Test children", desc.getChildTypes(),
				IRefinesMachine.ELEMENT_TYPE, ISeesContext.ELEMENT_TYPE,
				IVariable.ELEMENT_TYPE, IInvariant.ELEMENT_TYPE,
				ITheorem.ELEMENT_TYPE, IVariant.ELEMENT_TYPE,
				IEvent.ELEMENT_TYPE);

	}

	public void testGetContextDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IContextRoot.ELEMENT_TYPE);

		assertElementDesc(desc, "CONTEXT", "END",
				"icons/full/obj16/ctx_obj.gif", nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				noAttribute);

		assertChildrens("Test children", desc.getChildTypes(),
				IExtendsContext.ELEMENT_TYPE, ICarrierSet.ELEMENT_TYPE,
				IConstant.ELEMENT_TYPE, IAxiom.ELEMENT_TYPE,
				ITheorem.ELEMENT_TYPE);

	}

	public void testGetRefinesMachineDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IRefinesMachine.ELEMENT_TYPE);

		final AttributeDesc expectedAttribute = new ComboDesc(
				new RefinesMachineAbstractMachineNameAttributeManipulation(), "",
				"", false, TARGET_ATTRIBUTE);

		assertElementDesc(desc, "REFINES", "", "icons/full/obj16/mch_obj.gif",
				nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedAttribute);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetSeesContextDesc() {
		final IElementDesc desc = registry
				.getElementDesc(ISeesContext.ELEMENT_TYPE);

		final AttributeDesc expectedAttribute = new ComboDesc(
				new SeesContextNameAttributeManipulation(), "", "", false,
				TARGET_ATTRIBUTE);

		assertElementDesc(desc, "SEES", "", "icons/full/obj16/ctx_obj.gif",
				nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedAttribute);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetVariablesDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IVariable.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "VARIABLES", "",
				"icons/full/obj16/var_obj.gif", "var", expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetInvariantsDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IInvariant.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "INVARIANTS", "",
				"icons/full/obj16/inv_obj.gif", "inv", expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetTheoremDesc() {
		final IElementDesc desc = registry
				.getElementDesc(ITheorem.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "THEOREMS", "", "icons/full/obj16/thm_obj.gif",
				"thm", expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetVariantDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IVariant.ELEMENT_TYPE);

		final AttributeDesc expectedExpression = getExpressionDesc();

		assertElementDesc(desc, "VARIANT", "", "icons/sample.gif", nullPrefix,
				nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedExpression);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetEventDesc() {
		final IElementDesc desc = registry.getElementDesc(IEvent.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedExtended = new ComboDesc(
				new ExtendedAttributeManipulation(), "", "", false,
				EXTENDED_ATTRIBUTE);
		final AttributeDesc expectedConvergence = new ComboDesc(
				new ConvergenceAttributeManipulation(), "", "", false,
				CONVERGENCE_ATTRIBUTE);
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "EVENTS", "END",
				"icons/full/obj16/evt_obj.gif", "evt", expectedLabel, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedExtended, expectedConvergence,
				expectedComment);

		assertChildrens("Test children", desc.getChildTypes(),
				IRefinesEvent.ELEMENT_TYPE, IParameter.ELEMENT_TYPE,
				IGuard.ELEMENT_TYPE, IWitness.ELEMENT_TYPE,
				IAction.ELEMENT_TYPE);
	}

	public void testGetRefinesEventDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IRefinesEvent.ELEMENT_TYPE);

		final AttributeDesc expectedAttribute = new ComboDesc(
				new RefinesEventAbstractEventLabelAttributeManipulation(), "", "",
				false, TARGET_ATTRIBUTE);

		assertElementDesc(desc, "REFINES", "", "icons/full/obj16/evt_obj.gif",
				nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedAttribute);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetParameterDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IParameter.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "ANY", "", "icons/full/obj16/var_obj.gif",
				"prm", expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetGuardDesc() {
		final IElementDesc desc = registry.getElementDesc(IGuard.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "WHERE", "", "icons/full/obj16/grd_obj.gif",
				"grd", expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetWitnessDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IWitness.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "WITH", "", "icons/sample.gif", "wit",
				expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetActionDesc() {
		final IElementDesc desc = registry.getElementDesc(IAction.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedAssignment = getAssignmentDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "THEN", "", "icons/full/obj16/act_obj.gif",
				"act", expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedAssignment, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetExtendsContextDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IExtendsContext.ELEMENT_TYPE);

		final AttributeDesc expectedExtends = new ComboDesc(
				new ExtendsContextAbstractContextNameAttributeManipulation(), "",
				"", false, TARGET_ATTRIBUTE);

		assertElementDesc(desc, "EXTENDS", "", "icons/full/obj16/ctx_obj.gif",
				nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedExtends);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);
	}

	public void testGetCarrierSetDesc() {
		final IElementDesc desc = registry
				.getElementDesc(ICarrierSet.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "SETS", "", "icons/full/obj16/set_obj.gif",
				"set", expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetConstantDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IConstant.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "CONSTANTS", "",
				"icons/full/obj16/cst_obj.gif", "cst", expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	public void testGetAxiomDesc() {
		final IElementDesc desc = registry.getElementDesc(IAxiom.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "AXIOMS", "", "icons/full/obj16/axm_obj.gif",
				"axm", expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	private void assertElementDesc(IElementDesc actualDesc, String prefix,
			String childrenSuffix, String imageName, String autoNamingPrefix,
			IAttributeDesc autoNamingAttribute, int defaultColumn) {
		assertAttributeDesc("Attribute for auto naming should be equals",
				autoNamingAttribute, actualDesc.getAutoNameAttribute());
		if (actualDesc == null)
			fail("ElementDesc should not be null");
		if (!prefix.equals(actualDesc.getPrefix()))
			fail("Prefix should be equals - expected: " + prefix
					+ ", but was : " + actualDesc.getPrefix());
		if (!childrenSuffix.equals(actualDesc.getChildrenSuffix()))
			fail("Children suffix should be equals - expected: "
					+ childrenSuffix + ", but was : "
					+ actualDesc.getChildrenSuffix());

		if (!autoNamingPrefix.equals(actualDesc.getAutoNamePrefix()))
			fail("Prefix for auto naming should be equals - expected: "
					+ autoNamingPrefix + ", but was : "
					+ actualDesc.getAutoNamePrefix());

		if (defaultColumn != actualDesc.getDefaultColumn())
			fail("Default column should be equals - expected: " + defaultColumn
					+ ", but was : " + actualDesc.getDefaultColumn());

		final ImageDescriptor imageDescriptor = EventBImage
				.getImageDescriptor(imageName);
		if (!imageDescriptor.equals(actualDesc.createImageDescriptor()))
			fail("Image descriptor should be equals - expected: "
					+ imageDescriptor + ", but was : "
					+ actualDesc.createImageDescriptor());
		if (!imageName.equals(actualDesc.getImageName()))
			fail("Image descriptor should be equals - expected: " + imageName
					+ ", but was : " + actualDesc.getImageName());
	}

	private void assertChildrens(String msg, IElementType<?>[] actual,
			IElementType<?>... expected) {
		final List<IElementType<?>> actualList = Arrays.asList(actual);
		final List<IElementType<?>> expectedList = Arrays.asList(expected);
		assertEquals(msg, expectedList, actualList);

	}

	private void assertAttributeDesc(String msg, IAttributeDesc[] actual,
			IAttributeDesc... expected) {
		if (expected == null || actual == null)
			fail(msg + " - should not be null");
		if (expected.length != actual.length)
			fail(msg + " - expected " + Arrays.asList(expected) + " but was " + Arrays.asList(actual));
		for (int i = 0; i < expected.length; i++) {
			assertAttributeDesc(msg, expected[i], actual[i]);
		}
	}

	private void assertAttributeDesc(String msg, IAttributeDesc expected,
			IAttributeDesc actual) {
		if (expected == null || actual == null)
			fail(msg);
		if (!(expected.getManipulation().getClass().equals(
				actual.getManipulation().getClass())
				&& expected.getPrefix().equals(actual.getPrefix())
				&& expected.getSuffix().equals(actual.getSuffix())
				&& expected.isHorizontalExpand() == actual.isHorizontalExpand() && expected
				.getAttributeType().equals(actual.getAttributeType())))
			fail(msg + " - expected: " + expected + ", but was : " + actual);
		if (expected instanceof TextDesc && actual instanceof TextDesc) {
			assertTextDesc(msg, (TextDesc) expected, (TextDesc) actual);
		}
	}

	private void assertTextDesc(String msg, TextDesc expected, TextDesc actual) {
		if (!(expected.isMath() == actual.isMath() && expected.getStyle() == actual
				.getStyle()))
			fail(msg + " - expected: " + expected + ", but was : " + actual);
	}

	private TextDesc getCommentedDesc() {
		return new TextDesc(new CommentAttributeManipulation(), "//", "", true,
				false, TextDesc.Style.MULTI, COMMENT_ATTRIBUTE);
	}

	private TextDesc getIdentifierDesc() {
		return new TextDesc(new IdentifierAttributeManipulation(), "", "",
				false, false, TextDesc.Style.SINGLE, IDENTIFIER_ATTRIBUTE);
	}

	private TextDesc getLabelDesc() {
		return new TextDesc(new LabelAttributeManipulation(), "", ":", false, false,
				TextDesc.Style.SINGLE, LABEL_ATTRIBUTE);
	}

	// multi line, can be expand horizontally and is math
	private TextDesc getAssignmentDesc() {
		return new TextDesc(new AssignmentAttributeManipulation(), "", "", false,
				true, TextDesc.Style.MULTI, ASSIGNMENT_ATTRIBUTE);
	}

	// multi line, can be expand horizontally and is math
	private TextDesc getPredicateDesc() {
		return new TextDesc(new PredicateAttributeManipulation(), "", "", false,
				true, TextDesc.Style.MULTI, PREDICATE_ATTRIBUTE);
	}

	// multi line, can be expand horizontally and is math
	private TextDesc getExpressionDesc() {
		return new TextDesc(new ExpressionAttributeManipulation(), "", "", true,
				true, TextDesc.Style.MULTI, EXPRESSION_ATTRIBUTE);
	}

}
