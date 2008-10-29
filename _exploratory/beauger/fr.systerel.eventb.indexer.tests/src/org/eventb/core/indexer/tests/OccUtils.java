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

import static org.eventb.core.indexer.EventBIndexUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.IRefinesEvent;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Occurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class OccUtils {

	public static IOccurrence makeDecl(final IInternalElement element) {
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element);
		return newOcc(DECLARATION, loc);
	}

	public static IOccurrence makeRef(IInternalElement element,
			IAttributeType.String attributeType, int start, int end) {
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element,
				attributeType, start, end);
		return newOcc(REFERENCE, loc);
	}
	
	public static IOccurrence makeRef(IInternalElement element,
			IAttributeType.String attributeType) {
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element,
				attributeType);
		return newOcc(REFERENCE, loc);
	}
	
	public static IOccurrence makeRef(IInternalElement element) {
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element);
		return newOcc(REFERENCE, loc);
	}

	public static IOccurrence makeModif(IInternalElement element,
			IAttributeType.String attributeType, int start, int end) {
		final IRodinLocation loc = RodinIndexer.getRodinLocation(element,
				attributeType, start, end);
		return newOcc(MODIFICATION, loc);
	}
	
	public static IOccurrence makeRefPred(IPredicateElement pred, int start,
			int end) {
		return makeRef(pred, EventBAttributes.PREDICATE_ATTRIBUTE, start, end);
	}

	public static IOccurrence makeModifAssign(IAssignmentElement assign,
			int start, int end) {
		return makeModif(assign, EventBAttributes.ASSIGNMENT_ATTRIBUTE, start,
				end);
	}

	public static IOccurrence makeRefExpr(IExpressionElement expr,
			int start, int end) {
		return makeRef(expr, EventBAttributes.EXPRESSION_ATTRIBUTE, start,
				end);
	}

	public static IOccurrence makeRefLabel(ILabeledElement label) {
		return makeRef(label, EventBAttributes.LABEL_ATTRIBUTE);
	}
	
	public static IOccurrence makeRefIdent(IIdentifierElement ident) {
		return makeRef(ident, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}
	
	public static IOccurrence makeRefTarget(IRefinesEvent refines) {
		return makeRef(refines, EventBAttributes.TARGET_ATTRIBUTE);
	}
	
	

	@SuppressWarnings("restriction")
	public static IDeclaration newDecl(IInternalElement elt, String name) {
		final IDeclaration declCst1 = new Declaration(elt, name);
		return declCst1;
	}

	@SuppressWarnings("restriction")
	public static IOccurrence newOcc(IOccurrenceKind kind,
			IRodinLocation location) {
		return new Occurrence(kind, location);
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
