package org.rodinp.internal.core.index.tests;

import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.AbstractRodinDBTests;

public abstract class IndexTests extends AbstractRodinDBTests {

	public IndexTests(String name, boolean disableIndexing) {
		super(name);
		if (disableIndexing) {
			RodinIndexer.getDefault().disableIndexing();
		}
	}

}