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
package org.eventb.core.tests.indexers;

import static org.eventb.core.EventBAttributes.*;
import static org.eventb.core.EventBPlugin.*;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IAssignmentElement;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesEvent;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.Occurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class OccUtils {

	public static IOccurrence makeDecl(IIdentifierElement ident,
			IDeclaration declaration) {
		return makeDecl(declaration, ident, IDENTIFIER_ATTRIBUTE);
	}

	public static IOccurrence makeDecl(ILabeledElement label,
			IDeclaration declaration) {
		return makeDecl(declaration, label, LABEL_ATTRIBUTE);
	}

	private static IOccurrence makeDecl(IDeclaration declaration,
			IInternalElement element, IAttributeType.String attribute) {
		if (!element.equals(declaration.getElement())) {
			throw new IllegalArgumentException(
					"declaration and element do not match.");
		}
		final IInternalLocation loc = RodinCore.getInternalLocation(element,
				attribute);
		return newOcc(DECLARATION, loc, declaration);
	}

	public static IOccurrence makeRef(IInternalElement element,
			IAttributeType.String attributeType, int start, int end,
			IDeclaration declaration) {
		final IInternalLocation loc =
				RodinCore.getInternalLocation(element, attributeType, start,
						end);
		return newOcc(REFERENCE, loc, declaration);
	}

	public static IOccurrence makeRef(IInternalElement element,
			IAttributeType.String attributeType, IDeclaration declaration) {
		final IInternalLocation loc =
				RodinCore.getInternalLocation(element, attributeType);
		return newOcc(REFERENCE, loc, declaration);
	}

	public static IOccurrence makeRef(IInternalElement element,
			IDeclaration declaration) {
		final IInternalLocation loc = RodinCore.getInternalLocation(element);
		return newOcc(REFERENCE, loc, declaration);
	}

	public static IOccurrence makeModif(IInternalElement element,
			IAttributeType.String attributeType, int start, int end,
			IDeclaration declaration) {
		final IInternalLocation loc =
				RodinCore.getInternalLocation(element, attributeType, start,
						end);
		return newOcc(MODIFICATION, loc, declaration);
	}

	public static IOccurrence makeRedecl(IInternalElement element,
			IAttributeType.String attributeType,
			IDeclaration declaration) {
		final IInternalLocation loc =
				RodinCore.getInternalLocation(element, attributeType);
		return newOcc(REDECLARATION, loc, declaration);
	}

	public static IOccurrence makeRefPred(IPredicateElement pred, int start,
			int end, IDeclaration declaration) {
		return makeRef(pred, PREDICATE_ATTRIBUTE, start, end, declaration);
	}

	public static IOccurrence makeModifAssign(IAssignmentElement assign,
			int start, int end, IDeclaration declaration) {
		return makeModif(assign, ASSIGNMENT_ATTRIBUTE, start, end, declaration);
	}

	public static IOccurrence makeRefAssign(IAssignmentElement assign,
			int start, int end, IDeclaration declaration) {
		return makeRef(assign, ASSIGNMENT_ATTRIBUTE, start, end, declaration);
	}

	public static IOccurrence makeRefExpr(IExpressionElement expr, int start,
			int end, IDeclaration declaration) {
		return makeRef(expr, EXPRESSION_ATTRIBUTE, start, end, declaration);
	}

	public static IOccurrence makeRefLabel(ILabeledElement label,
			IDeclaration declaration) {
		return makeRef(label, LABEL_ATTRIBUTE, declaration);
	}

	public static IOccurrence makeRedeclIdent(IIdentifierElement ident,
			IDeclaration declaration) {
		return makeRedecl(ident, IDENTIFIER_ATTRIBUTE, declaration);
	}

	public static IOccurrence makeRedeclTarget(IRefinesEvent refines,
			IDeclaration declaration) {
		return makeRedecl(refines, TARGET_ATTRIBUTE, declaration);
	}

	public static IOccurrence makeRedeclLabel(ILabeledElement element,
			IDeclaration declaration) {
		return makeRedecl(element, LABEL_ATTRIBUTE, declaration);
	}

	public static IDeclaration newDecl(IInternalElement elt, String name) {
		return new Declaration(elt, name);
	}

	public static IOccurrence newOcc(IOccurrenceKind kind,
			IInternalLocation location, IDeclaration declaration) {
		return new Occurrence(kind, location, declaration);
	}

	public static List<IDeclaration> makeDeclList(IDeclaration... declarations) {
		final List<IDeclaration> expected = new ArrayList<IDeclaration>();
		for (IDeclaration declaration : declarations) {
			expected.add(declaration);
		}
		return expected;
	}

	public static List<IOccurrence> makeOccList(IOccurrence... occurrences) {
		final List<IOccurrence> expected = new ArrayList<IOccurrence>();
		for (IOccurrence occurrence : occurrences) {
			expected.add(occurrence);
		}
		return expected;
	}

}
