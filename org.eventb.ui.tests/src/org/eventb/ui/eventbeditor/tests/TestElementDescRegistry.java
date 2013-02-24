/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
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
import static org.eventb.core.EventBAttributes.THEOREM_ATTRIBUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

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
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.eventbeditor.elementdesc.AttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ComboDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.NullAttributeDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.TextDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ToggleDesc;
import org.eventb.internal.ui.eventbeditor.imageprovider.AxiomImageProvider;
import org.eventb.internal.ui.eventbeditor.imageprovider.DefaultImageProvider;
import org.eventb.internal.ui.eventbeditor.imageprovider.GuardImageProvider;
import org.eventb.internal.ui.eventbeditor.imageprovider.IImageProvider;
import org.eventb.internal.ui.eventbeditor.imageprovider.InvariantImageProvider;
import org.eventb.internal.ui.eventbeditor.manipulation.AssignmentAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.CommentAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ConvergenceAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExpressionAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendedAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.ExtendsContextAbstractContextNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.IdentifierAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.LabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.PredicateAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesEventAbstractEventLabelAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesMachineAbstractMachineNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.SeesContextNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.manipulation.TheoremAttributeManipulation;
import org.junit.Test;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;

public class TestElementDescRegistry {

	private IElementDescRegistry registry = ElementDescRegistry.getInstance();

	private final IInternalElementType<?>[] noChildren = new IInternalElementType<?>[0];
	private final String nullPrefix = "";
	private final IAttributeDesc nullAttribute = new NullAttributeDesc();
	private final Class<? extends IImageProvider> defaultImgProvider = DefaultImageProvider.class;
	
	@Test
	public void testGetMachineDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IMachineRoot.ELEMENT_TYPE);

		assertElementDesc(desc, "MACHINE", "END", defaultImgProvider,
				nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				getCommentedDesc());

		assertChildrens("Test children", desc.getChildTypes(),
				IRefinesMachine.ELEMENT_TYPE, ISeesContext.ELEMENT_TYPE,
				IVariable.ELEMENT_TYPE, IInvariant.ELEMENT_TYPE,
				IVariant.ELEMENT_TYPE, IEvent.ELEMENT_TYPE);

	}

	@Test
	public void testGetContextDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IContextRoot.ELEMENT_TYPE);

		assertElementDesc(desc, "CONTEXT", "END", defaultImgProvider,
				nullPrefix, nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				getCommentedDesc());

		assertChildrens("Test children", desc.getChildTypes(),
				IExtendsContext.ELEMENT_TYPE, ICarrierSet.ELEMENT_TYPE,
				IConstant.ELEMENT_TYPE, IAxiom.ELEMENT_TYPE);

	}

	@Test
	public void testGetRefinesMachineDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IRefinesMachine.ELEMENT_TYPE);

		final AttributeDesc expectedAttribute = new ComboDesc(
				new RefinesMachineAbstractMachineNameAttributeManipulation(),
				"", "", false, TARGET_ATTRIBUTE, true);

		assertElementDesc(desc, "REFINES", "", defaultImgProvider, nullPrefix,
				nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedAttribute);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetSeesContextDesc() {
		final IElementDesc desc = registry
				.getElementDesc(ISeesContext.ELEMENT_TYPE);

		final AttributeDesc expectedAttribute = new ComboDesc(
				new SeesContextNameAttributeManipulation(), "", "", false,
				TARGET_ATTRIBUTE, true);

		assertElementDesc(desc, "SEES", "", defaultImgProvider, nullPrefix,
				nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedAttribute);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetVariablesDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IVariable.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "VARIABLES", "", defaultImgProvider, "var",
				expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetInvariantsDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IInvariant.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedTheorem = getTheoremDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "INVARIANTS", "", InvariantImageProvider.class,
				"inv", expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedTheorem,
				expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetVariantDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IVariant.ELEMENT_TYPE);

		final AttributeDesc expectedExpression = getExpressionDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "VARIANT", "", defaultImgProvider, nullPrefix,
				nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedExpression, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetEventDesc() {
		final IElementDesc desc = registry.getElementDesc(IEvent.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedExtended = new ComboDesc(
				new ExtendedAttributeManipulation(), "", "", false,
				EXTENDED_ATTRIBUTE, true);
		final AttributeDesc expectedConvergence = new ComboDesc(
				new ConvergenceAttributeManipulation(), "", "", false,
				CONVERGENCE_ATTRIBUTE, true);
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "EVENTS", "END", defaultImgProvider, "evt",
				expectedLabel, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedExtended, expectedConvergence,
				expectedComment);

		assertChildrens("Test children", desc.getChildTypes(),
				IRefinesEvent.ELEMENT_TYPE, IParameter.ELEMENT_TYPE,
				IGuard.ELEMENT_TYPE, IWitness.ELEMENT_TYPE,
				IAction.ELEMENT_TYPE);
	}

	@Test
	public void testGetRefinesEventDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IRefinesEvent.ELEMENT_TYPE);

		final AttributeDesc expectedAttribute = new ComboDesc(
				new RefinesEventAbstractEventLabelAttributeManipulation(), "",
				"", false, TARGET_ATTRIBUTE, true);

		assertElementDesc(desc, "REFINES", "", defaultImgProvider, nullPrefix,
				nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedAttribute);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetParameterDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IParameter.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "ANY", "", defaultImgProvider, "prm",
				expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetGuardDesc() {
		final IElementDesc desc = registry.getElementDesc(IGuard.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedTheorem = getTheoremDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "WHERE", "", GuardImageProvider.class, "grd",
				expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedTheorem,
				expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetWitnessDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IWitness.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "WITH", "", defaultImgProvider, "wit",
				expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetActionDesc() {
		final IElementDesc desc = registry.getElementDesc(IAction.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedAssignment = getAssignmentDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "THEN", "", defaultImgProvider, "act",
				expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedAssignment, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetExtendsContextDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IExtendsContext.ELEMENT_TYPE);

		final AttributeDesc expectedExtends = new ComboDesc(
				new ExtendsContextAbstractContextNameAttributeManipulation(),
				"", "", false, TARGET_ATTRIBUTE, true);

		assertElementDesc(desc, "EXTENDS", "", defaultImgProvider, nullPrefix,
				nullAttribute, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedExtends);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);
	}

	@Test
	public void testGetCarrierSetDesc() {
		final IElementDesc desc = registry
				.getElementDesc(ICarrierSet.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "SETS", "", defaultImgProvider, "set",
				expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetConstantDesc() {
		final IElementDesc desc = registry
				.getElementDesc(IConstant.ELEMENT_TYPE);

		final AttributeDesc expectedIdentifier = getIdentifierDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "CONSTANTS", "", defaultImgProvider, "cst",
				expectedIdentifier, 0);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedIdentifier, expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	@Test
	public void testGetAxiomDesc() {
		final IElementDesc desc = registry.getElementDesc(IAxiom.ELEMENT_TYPE);

		final AttributeDesc expectedLabel = getLabelDesc();
		final AttributeDesc expectedPredicate = getPredicateDesc();
		final AttributeDesc expectedTheorem = getTheoremDesc();
		final AttributeDesc expectedComment = getCommentedDesc();

		assertElementDesc(desc, "AXIOMS", "", AxiomImageProvider.class, "axm",
				expectedLabel, 1);

		assertAttributeDesc("Test attributes", desc.getAttributeDescription(),
				expectedLabel, expectedPredicate, expectedTheorem,
				expectedComment);

		assertChildrens("Test children", desc.getChildTypes(), noChildren);

	}

	private void assertElementDesc(IElementDesc actualDesc, String prefix,
			String childrenSuffix, Class<? extends IImageProvider> imageProvider,
			String autoNamingPrefix, IAttributeDesc autoNamingAttribute,
			int defaultColumn) {
		assertNotNull("ElementDesc should not be null", actualDesc);
		assertAttributeDesc(autoNamingAttribute, actualDesc
				.getAutoNameAttribute());
		assertEquals("Unexpected prefix", prefix, actualDesc.getPrefix());
		assertEquals("Unexpected children suffix", childrenSuffix, actualDesc
				.getChildrenSuffix());
		assertEquals("Unexpected prefix for auto naming", autoNamingPrefix,
				actualDesc.getAutoNamePrefix());
		assertEquals("Unexpected default column", defaultColumn, actualDesc
				.getDefaultColumn());
		assertEquals("Unexpected image provider class", imageProvider, actualDesc
				.getImageProvider().getClass());
	}

	private void assertChildrens(String msg, IElementType<?>[] actual,
			IElementType<?>... expected) {
		final List<IElementType<?>> actualList = Arrays.asList(actual);
		final List<IElementType<?>> expectedList = Arrays.asList(expected);
		assertEquals(msg, expectedList, actualList);

	}

	private void assertAttributeDesc(String msg, IAttributeDesc[] actual,
			IAttributeDesc... expected) {
		assertNotNull("Expected should not be null", expected);
		assertNotNull("Actual should not be null", actual);
		assertEquals("Differing lengths", expected.length, actual.length);

		for (int i = 0; i < expected.length; i++) {
			assertAttributeDesc(expected[i], actual[i]);
		}
	}

	private void assertAttributeDesc(IAttributeDesc expected,
			IAttributeDesc actual) {
		assertNotNull("Expected attribute description should not be null",
				expected);
		assertNotNull("Actual attribute description should not be null", actual);
		assertEquals("Unexpected attribute manipulation class", expected
				.getManipulation().getClass(), actual.getManipulation()
				.getClass());
		assertEquals("Unexpected prefix of attribute", expected.getPrefix(),
				actual.getPrefix());
		assertEquals("Unexpected suffix of attribute", expected.getSuffix(),
				actual.getSuffix());
		assertEquals("Unexpected horizontal expand", expected
				.isHorizontalExpand(), actual.isHorizontalExpand());
		assertEquals("Unexpected attribute type ", expected.getAttributeType(),
				actual.getAttributeType());

		if (expected instanceof TextDesc && actual instanceof TextDesc) {
			assertTextDesc((TextDesc) expected, (TextDesc) actual);
		} else if (expected instanceof ComboDesc && actual instanceof ComboDesc) {
			assertComboDesc((ComboDesc) expected, (ComboDesc) actual);
		}
	}

	private void assertTextDesc(TextDesc expected, TextDesc actual) {
		assertEquals("Unexpected isMath", expected.isMath(), actual.isMath());
		assertEquals("Unexpected style", expected.getStyle(), actual.getStyle());
		assertEquals("Unexpected foreground color preference", expected
				.getForegroundColor(), actual.getForegroundColor());
	}

	private void assertComboDesc(ComboDesc expected, ComboDesc actual) {
		assertEquals("Unexpected required", expected.isRequired(), actual
				.isRequired());
	}

	private TextDesc getCommentedDesc() {
		return new TextDesc(new CommentAttributeManipulation(), "//", "", true,
				false, TextDesc.Style.MULTI, COMMENT_ATTRIBUTE,
				"Comment foreground");
	}

	private TextDesc getIdentifierDesc() {
		return new TextDesc(new IdentifierAttributeManipulation(), "", "",
				false, false, TextDesc.Style.SINGLE, IDENTIFIER_ATTRIBUTE,
				"Text foreground");
	}

	private TextDesc getLabelDesc() {
		return new TextDesc(new LabelAttributeManipulation(), "", ":", false,
				false, TextDesc.Style.SINGLE, LABEL_ATTRIBUTE,
				"Text foreground");
	}

	// multi line, can be expand horizontally and is math
	private TextDesc getAssignmentDesc() {
		return new TextDesc(new AssignmentAttributeManipulation(), "", "",
				false, true, TextDesc.Style.MULTI, ASSIGNMENT_ATTRIBUTE,
				"Text foreground");
	}

	// multi line, can be expand horizontally and is math
	private TextDesc getPredicateDesc() {
		return new TextDesc(new PredicateAttributeManipulation(), "", "",
				false, true, TextDesc.Style.MULTI, PREDICATE_ATTRIBUTE,
				"Text foreground");
	}

	// multi line, can be expand horizontally and is math
	private TextDesc getExpressionDesc() {
		return new TextDesc(new ExpressionAttributeManipulation(), "", "",
				true, true, TextDesc.Style.MULTI, EXPRESSION_ATTRIBUTE,
				"Text foreground");
	}

	private ToggleDesc getTheoremDesc() {
		return new ToggleDesc(new TheoremAttributeManipulation(),
				THEOREM_ATTRIBUTE);
	}
}
