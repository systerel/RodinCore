package org.eventb.core.indexer;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.Occurrence;

public class EventBIndexUtil {

	private static int alloc;

	public static String getUniqueName(String identifierString) {
		if (alloc == Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		return "n" + alloc++ + "_" + identifierString;
	}

//	public static Object getUniqueKey() {
//		return "k" + alloc++;
//	}
	
	public static Object getUniqueKey(IInternalElement e) {
		return e.getHandleIdentifier();
	}
	
	public static Occurrence makeDeclaration(IRodinElement file, IIndexer indexer) {
		Occurrence ref = new Occurrence(EventBOccurrenceKind.DECLARATION, indexer);
		ref.setDefaultLocation(file);
		return ref;
	}


}
