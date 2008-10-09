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
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
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

	public static IOccurrence makeOccDecl(final IRodinElement element) {
		final IRodinLocation loc = RodinIndexer
				.getRodinLocation(element);
		return makeOcc(DECLARATION, loc);
	}

	public static IOccurrence makeOccRef(IAttributedElement element,
			IAttributeType.String attributeType, int start, int end) {
		final IRodinLocation loc = RodinIndexer
						.getRodinLocation(element, attributeType, start, end);
		return makeOcc(REFERENCE, loc);
	}

	public static IOccurrence makeOccRefPred(IPredicateElement pred, int start,
			int end) {
		return makeOccRef(pred, EventBAttributes.PREDICATE_ATTRIBUTE, start,
				end);
	}

	@SuppressWarnings("restriction")
	public static IDeclaration makeDecl(IInternalElement elt, String name) {
		final IDeclaration declCst1 = new Declaration(elt, name);
		return declCst1;
	}
	
	@SuppressWarnings("restriction")
	public static IOccurrence makeOcc(IOccurrenceKind kind,
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
