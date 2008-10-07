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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.EventBAttributes;
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
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.RodinIndexer;

/**
 * @author Nicolas Beauger
 *
 */
public class ContextIndexer implements IIndexer {

	private static final String ID = "fr.systerel.eventb.indexer.context";

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	private FormulaFactory ff = FormulaFactory.getDefault();

	private IIndexingToolkit index;

	private static boolean isContextFile(IRodinFile file) {
		return file instanceof IContextFile;
	}

	public void index(IIndexingToolkit index) {
		this.index = index;
		final IRodinFile file = index.getRodinFile();

		if (!isContextFile(file)) {
			return;
		}
		IContextFile ctxFile = (IContextFile) file;
		try {
			indexConstants(ctxFile);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Map<FreeIdentifier, IDeclaration> filterFreeIdentifiers(
			FreeIdentifier[] idents, Map<String, IDeclaration> constantNames)
			throws RodinDBException {
		Map<FreeIdentifier, IDeclaration> result = new HashMap<FreeIdentifier, IDeclaration>();
		for (FreeIdentifier ident : idents) {
			IDeclaration declaration = constantNames.get(ident.getName());
			if (declaration != null) {
				result.put(ident, declaration);
			}
		}
		return result;
	}

	private Map<String, IDeclaration> extractIdentifiers(
			List<IDeclaration> declarations) throws RodinDBException {
		Map<String, IDeclaration> result = new HashMap<String, IDeclaration>();
		for (IDeclaration declaration : declarations) {
			final IIdentifierElement element = (IIdentifierElement) declaration.getElement();
			result.put(element.getIdentifierString(), declaration);
		}
		return result;
	}

	private void indexConstants(IContextFile file) throws RodinDBException {
		// processIdentifierElements(index, file.getCarrierSets());
		final List<IDeclaration> declarations = processIdentifierElements(file
				.getConstants());

		Map<String, IDeclaration> constantNames = extractIdentifiers(declarations);

		processPredicateElements(file, constantNames, file.getAxioms());
		// processPredicateElements(file, index, constantNames,
		// file.getTheorems());
	}

	private List<IDeclaration> processIdentifierElements(
			IIdentifierElement[] elems) throws RodinDBException {
		final List<IDeclaration> declarations = new ArrayList<IDeclaration>();
		// index declaration for each constant and export them
		for (IIdentifierElement elem : elems) {
			final IDeclaration declaration = indexIdentifierDeclaration(elem,
					elem.getIdentifierString());
			index.export(declaration);
			declarations.add(declaration);
		}
		return declarations;
	}

	private void processPredicateElements(IContextFile file,
			Map<String, IDeclaration> constantNames,
			IPredicateElement[] elems) throws RodinDBException {
		for (IPredicateElement elem : elems) {
			processPredicateElement(elem, constantNames);
		}
	}

	private void processPredicateElement(IPredicateElement element,
			Map<String, IDeclaration> constantNames)
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
			Map<String, IDeclaration> constantNames, Formula<?> formula)
			throws RodinDBException {
		final FreeIdentifier[] idents = formula.getFreeIdentifiers();

		// Idea: filter idents that are indeed declared. Ignore those that are
		// not and at the same time build a map from ident to declaration.
		// Then visit the predicate and make an occurrence for each identifier
		// that belongs to the map.

		final Map<FreeIdentifier, IDeclaration> matchingIdents = filterFreeIdentifiers(
				idents, constantNames);

		indexMatchingIdents(matchingIdents, element, formula);
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
			final Map<FreeIdentifier, IDeclaration> matchingIdents,
			IPredicateElement element, Formula<?> pred) {

		for (FreeIdentifier ident : matchingIdents.keySet()) {

			final String identName = ident.getName();
			List<IPosition> positions = pred.getPositions(new DefaultFilter() {
				@Override
				public boolean select(FreeIdentifier identifier) {
					return identifier.getName().equals(identName);
				}
			});
			if (positions.size() > 0) {
				final IDeclaration declaration = matchingIdents
						.get(ident);
				for (IPosition pos : positions) {
					final SourceLocation srcLoc = pred.getSubFormula(pos)
							.getSourceLocation();
					final IRodinLocation loc = EventBIndexUtil
							.getRodinLocation(element,
									EventBAttributes.PREDICATE_ATTRIBUTE,
									srcLoc);
					indexConstantReference(declaration, loc);
				}
			}
		}
	}

	private IDeclaration indexIdentifierDeclaration(
			IIdentifierElement constant, String constantName) {
		final IDeclaration declaration = index.declare(constant, constantName);
		final IRodinLocation loc = RodinIndexer.getRodinLocation(constant
				.getRodinFile());
		index.addOccurrence(declaration, DECLARATION, loc);
		return declaration;
	}

	private void indexConstantReference(IDeclaration declaration,
			IRodinLocation loc) {
		index.addOccurrence(declaration, REFERENCE, loc);
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return NO_DEPENDENCIES;
	}

	public String getId() {
		return ID;
	}

}
