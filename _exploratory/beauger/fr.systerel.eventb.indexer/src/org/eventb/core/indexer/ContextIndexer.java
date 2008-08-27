package org.eventb.core.indexer;

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
import org.rodinp.core.index.IDescriptor;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinIndex;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.index.RodinIndexer;

public class ContextIndexer implements IIndexer {

	private FormulaFactory ff = FormulaFactory.getDefault();

	public boolean canIndex(IRodinFile file) {
		return (file instanceof IContextFile);
	}

	public void index(IRodinFile file, IRodinIndex index) {

		if (!canIndex(file)) {
			return;
		}
		IContextFile ctxFile = (IContextFile) file;
		try {
			indexConstants(ctxFile, index);
			// for (IConstant c : ctxFile.getConstants()) {
			// indexConstant(ctxFile, c, index);
			// }
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ------------------------------------------------- //

	private IDescriptor makeConstantDescriptor(IConstant constant,
			IRodinIndex index) {
		return index.makeDescriptor(constant.getElementName(), constant);
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

	private void indexConstants(IContextFile file, IRodinIndex index)
			throws RodinDBException {
		IConstant[] constants = file.getConstants();

		// index declaration for each constant
		for (IConstant constant : constants) {
			indexConstantDeclaration(file, makeConstantDescriptor(constant,
					index));
		}
		Map<String, IIdentifierElement> constantNames = extractIdentifiers(constants);

		// index references for each axiom
		for (IAxiom axiom : file.getAxioms()) {
			IParseResult result = ff.parsePredicate(axiom.getPredicateString());
			if (result.isSuccess()) {
				final Predicate pred = result.getParsedPredicate();
				final FreeIdentifier[] idents = pred.getFreeIdentifiers();
				final Map<FreeIdentifier, IIdentifierElement> matchingIdents = filterFreeIdentifiers(
						idents, constantNames);

				indexMatchingIdents(matchingIdents, index, axiom, pred);
			} // TODO: try to index something even if parsing fails … ? seems
				// quite difficult
		}

	}

	private void indexMatchingIdents(
			final Map<FreeIdentifier, IIdentifierElement> matchingIdents,
			IRodinIndex index, IAxiom axiom, final Predicate pred) {

		for (FreeIdentifier ident : matchingIdents.keySet()) {

			final String identName = ident.getName();
			List<IPosition> positions = pred.getPositions(new DefaultFilter() {
				@Override
				public boolean select(FreeIdentifier identifier) {
					if (identifier.getName().equals(identName)) {
						return true;
					}
					return false;
				}
			});
			if (positions.size() > 0) {
				IDescriptor descriptor = index.getDescriptor(EventBIndexUtil
						.getUniqueKey((IConstant) matchingIdents.get(ident)));
				for (IPosition pos : positions) {
					final SourceLocation srcLoc = pred.getSubFormula(pos)
							.getSourceLocation();
					final IRodinLocation loc = RodinIndexer.getRodinLocation(
							axiom,
							EventBAttributes.PREDICATE_ATTRIBUTE,
							srcLoc.getStart(), srcLoc.getEnd());
					final Occurrence occ = new Occurrence(
							EventBOccurrenceKind.REFERENCE, loc, this);
					descriptor.addOccurrence(occ);
				}
			}
		}
	}

	// private Object getKey(IConstant c) {
	// return c.getHandleIdentifier();
	// }

	private void indexConstantDeclaration(IContextFile file,
			IDescriptor descriptor) {
		Occurrence ref = EventBIndexUtil.makeDeclaration(file, this);
		descriptor.addOccurrence(ref);
	}

	// ------------------------------------------------- //
	// private void indexConstant(IContextFile file, IConstant constant,
	// IRodinIndex index) throws RodinDBException {
	// Object id = elementUniqueId(constant);
	//
	// IDescriptor descriptor = index.makeDescriptor(constant
	// .getIdentifierString(), constant, id);
	//
	// indexConstantDeclaration(file, constant, descriptor);
	//
	// indexConstantReferences(file, constant, descriptor);
	//
	// }
	//
	// private void indexConstantReferences(IContextFile file, IConstant
	// constant,
	// IDescriptor descriptor) {
	//
	// // find references
	// IAxiom[] axioms;
	// try {
	// final String cstId = constant.getIdentifierString();
	// axioms = file.getAxioms();
	// for (IAxiom axm : axioms) {
	// IParseResult result = ff.parsePredicate(axm
	// .getPredicateString());
	// if (result.isSuccess()) { // TODO: try to index something even
	// // if parsing fails …
	// Predicate pred = result.getParsedPredicate();
	// FreeIdentifier[] idents = pred.getFreeIdentifiers();
	//
	// // FIXME: algo particulièrement inefficace, faire l'inverse
	// // lister les freeIdents une fois pour toutes puis chercher
	// // si on peut indexer des constantes à partir de ça
	// if (containsIdentifier(idents, cstId)) {
	// List<IPosition> positions = pred
	// .getPositions(new DefaultFilter() {
	// @Override
	// public boolean select(
	// FreeIdentifier identifier) {
	// if (identifier.getName().equals(cstId)) {
	// return true;
	// }
	// return super.select(identifier);
	// }
	// });
	// for (IPosition pos : positions) {
	// final SourceLocation srcLoc = pred.getSubFormula(
	// pos).getSourceLocation();
	// Occurrence occ = new Occurrence(
	// EventBOccurrenceKind.REFERENCE, this);
	// occ.setLocation(axm,
	// EventBAttributes.PREDICATE_ATTRIBUTE
	// .getId(), srcLoc.getStart(), srcLoc
	// .getEnd());
	// descriptor.addReference(occ);
	// }
	// }
	// }
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // Occurrence ref = new Occurrence(EventBOccurrenceKind.REFERENCE);
	// // ref.setLocation(constant.getRodinFile());
	// // descriptor.addReference(ref);
	//
	// }
	//
	// private boolean containsIdentifier(FreeIdentifier[] freeIdents,
	// String identifier) {
	// for (FreeIdentifier id : freeIdents) {
	// if (id.getName().equals(identifier)) {
	// return true;
	// }
	// }
	// return false;
	// }

}
