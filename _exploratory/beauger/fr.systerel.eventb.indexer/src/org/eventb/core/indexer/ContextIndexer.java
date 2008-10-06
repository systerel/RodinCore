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
package org.eventb.core.indexer;

import static org.eventb.core.indexer.EventBIndexUtil.DECLARATION;
import static org.eventb.core.indexer.EventBIndexUtil.REFERENCE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;

public class ContextIndexer implements IIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.context";

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	private FormulaFactory ff = FormulaFactory.getDefault();

	private boolean isContextFile(IRodinFile file) {
		return file instanceof IContextFile;
	}

	public void index(IIndexingToolkit index) {
		final IRodinFile file = index.getRodinFile();
		
		if (!isContextFile(file)) {
			return;
		}
		IContextFile ctxFile = (IContextFile) file;
		try {
			indexConstants(ctxFile, index);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Map<FreeIdentifier, IIdentifierElement> filterFreeIdentifiers(
			FreeIdentifier[] idents, Map<String, IIdentifierElement> names)
			throws RodinDBException {
		Map<FreeIdentifier, IIdentifierElement> result = new HashMap<FreeIdentifier, IIdentifierElement>();
		for (FreeIdentifier ident : idents) {
			IIdentifierElement element = names.get(ident.getName());
			if (element != null) {
				result.put(ident, element);
			}
		}
		return result;
	}

	private Map<String, IIdentifierElement> extractIdentifiers(
			IIdentifierElement[] elements) throws RodinDBException {
		Map<String, IIdentifierElement> result = new HashMap<String, IIdentifierElement>();
		for (IIdentifierElement element : elements) {
			result.put(element.getIdentifierString(), element);
		}
		return result;
	}

	private void indexConstants(IContextFile file, IIndexingToolkit index)
			throws RodinDBException {
		//processIdentifierElements(index, file.getCarrierSets());
		processIdentifierElements(index, file.getConstants());

		Map<String, IIdentifierElement> constantNames = extractIdentifiers(file.getConstants());

		processPredicateElements(file, index, constantNames, file.getAxioms());
		//processPredicateElements(file, index, constantNames, file.getTheorems());
	}

	private void processIdentifierElements(IIndexingToolkit index,
			IIdentifierElement[] elems) throws RodinDBException {
		// index declaration for each constant and export them
		for (IIdentifierElement elem : elems) {
			indexIdentifierDeclaration(elem, elem.getIdentifierString(),
					index);
			index.export(elem);
		}
	}

	private void processPredicateElements(IContextFile file, IIndexingToolkit index,
			Map<String, IIdentifierElement> constantNames, IPredicateElement[] elems)
			throws RodinDBException {
		for (IPredicateElement elem : elems) {
			processPredicateElement(elem, index, constantNames);
		}
	}

	private void processPredicateElement(IPredicateElement element,
			IIndexingToolkit index,
			Map<String, IIdentifierElement> constantNames)
			throws RodinDBException {
		if (!isValid(element))
			return;
		
		final String predicateString = element.getPredicateString();
		IParseResult result = ff.parsePredicate(predicateString);
		if (!result.isSuccess())
			return;

		final Predicate pred = result.getParsedPredicate();
		extractOccurrences(element, index, constantNames, pred);
	}

	private void extractOccurrences(IPredicateElement element,
			IIndexingToolkit index,
			Map<String, IIdentifierElement> constantNames,
			Formula<?> formula) throws RodinDBException {
		final FreeIdentifier[] idents = formula.getFreeIdentifiers();
		
		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the predicate and make an occurrence for each identifier
		// that belongs to the map.
		
		final Map<FreeIdentifier, IIdentifierElement> matchingIdents = filterFreeIdentifiers(
				idents, constantNames);

		indexMatchingIdents(matchingIdents, index, element, formula);
		// TODO Can we index anything when parsing does not succeed ?
	}

	private boolean isValid(IPredicateElement axiom) throws RodinDBException {
		if (!axiom.exists()) {
			return false;
		}
		if (!axiom.hasAttribute(EventBAttributes.PREDICATE_ATTRIBUTE)) {
			return false;
		}
		return true;
	}
	
	private void indexMatchingIdents(
			final Map<FreeIdentifier, IIdentifierElement> matchingIdents,
			IIndexingToolkit index, IPredicateElement element, Formula<?> pred) {

		for (FreeIdentifier ident : matchingIdents.keySet()) {

			final String identName = ident.getName();
			List<IPosition> positions = pred.getPositions(new DefaultFilter() {
				@Override
				public boolean select(FreeIdentifier identifier) {
					return identifier.getName().equals(identName);
				}
			});
			if (positions.size() > 0) {
				final IConstant constant = (IConstant) matchingIdents
						.get(ident);
				for (IPosition pos : positions) {
					final SourceLocation srcLoc = pred.getSubFormula(pos)
							.getSourceLocation();
					final IRodinLocation loc = EventBIndexUtil
							.getRodinLocation(element,
									EventBAttributes.PREDICATE_ATTRIBUTE,
									srcLoc);
					indexConstantReference(constant, identName, loc, index);
				}
			}
		}
	}

	private void indexIdentifierDeclaration(IIdentifierElement constant,
			String constantName, IIndexingToolkit index) {
		index.declare(constant, constantName);
		final IRodinLocation loc = RodinIndexer.getRodinLocation(constant
				.getRodinFile());
		index.addOccurrence(constant, DECLARATION, loc);
	}

	private void indexConstantReference(IConstant constant, String name,
			IRodinLocation loc, IIndexingToolkit index) {
		index.addOccurrence(constant, REFERENCE, loc);
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return NO_DEPENDENCIES;
	}

	public String getId() {
		return ID;
	}

}
