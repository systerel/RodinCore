package org.eventb.core.indexer;

import org.eventb.core.ast.SourceLocation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.Occurrence;
import org.rodinp.core.index.RodinIndexer;

public class EventBIndexUtil {

	private static int alloc;

	public static String getUniqueName(String identifierString) {
		if (alloc == Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		return "n" + alloc++ + "_" + identifierString;
	}

	public static Occurrence makeDeclaration(IRodinElement file,
			IIndexer indexer) {
		final IRodinLocation loc = RodinIndexer.getRodinLocation(file);
		return new Occurrence(EventBOccurrenceKind.DECLARATION, loc, indexer);
	}

	/**
	 * When extracting a location from a SourceLocation, using that method is
	 * mandatory, as long as SourceLocation and RodinLocation do not share the
	 * same range convention.
	 * 
	 * @param element
	 * @param attributeType
	 * @param location
	 * @return
	 */
	public static IRodinLocation getRodinLocation(IRodinElement element,
			IAttributeType attributeType, SourceLocation location) {
		return RodinIndexer.getRodinLocation(element, attributeType, location
				.getStart(), location.getEnd() + 1);
		// Concerning the end character,
		// SourceLocation is INclusive whereas
		// RodinLocation is EXclusive
	}
}
