package org.eventb.core.indexer;

import static org.eventb.core.indexer.EventBIndexUtil.DECLARATION;
import static org.eventb.core.indexer.EventBIndexUtil.REFERENCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.DefaultFilter;
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

	private static final IRodinFile[] NO_DEPENDENCIES = new IRodinFile[0];

	private FormulaFactory ff = FormulaFactory.getDefault();

	private boolean isContextFile(IRodinFile file) {
		return file instanceof IContextFile;
	}

	public void index(IRodinFile file, IIndexingToolkit index) {

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
		IConstant[] constants = file.getConstants();

		// index declaration for each constant and export them
		for (IConstant constant : constants) {
			indexConstantDeclaration(constant, constant.getIdentifierString(),
					index);
			index.export(constant);
		}

		Map<String, IIdentifierElement> constantNames = extractIdentifiers(constants);

		// filter invalid axioms
		final IAxiom[] axioms = file.getAxioms();
		final List<IAxiom> validAxioms = filterInvalidAxioms(axioms);
		// index references for each axiom
		for (IAxiom axiom : validAxioms) {
			final String predicateString = axiom.getPredicateString();
			IParseResult result = ff.parsePredicate(predicateString);
			if (result.isSuccess()) {
				final Predicate pred = result.getParsedPredicate();
				final FreeIdentifier[] idents = pred.getFreeIdentifiers();
				final Map<FreeIdentifier, IIdentifierElement> matchingIdents = filterFreeIdentifiers(
						idents, constantNames);

				indexMatchingIdents(matchingIdents, index, axiom, pred);
			}
			// TODO Can we index anything when parsing does not succeed ?
		}

	}

	private List<IAxiom> filterInvalidAxioms(final IAxiom[] axioms)
			throws RodinDBException {
		final List<IAxiom> validAxioms = new ArrayList<IAxiom>();
		for (IAxiom axiom : axioms) {
			if (isValid(axiom)) {
				validAxioms.add(axiom);
			}
		}
		return validAxioms;
	}

	private boolean isValid(IAxiom axiom) throws RodinDBException {
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
			IIndexingToolkit index, IAxiom axiom, Predicate pred) {

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
							.getRodinLocation(axiom,
									EventBAttributes.PREDICATE_ATTRIBUTE,
									srcLoc);
					indexConstantReference(constant, identName, loc, index);
				}
			}
		}
	}

	private void indexConstantDeclaration(IConstant constant,
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

}
