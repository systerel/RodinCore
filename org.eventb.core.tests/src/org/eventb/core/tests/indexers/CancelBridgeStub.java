/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import static org.eventb.core.tests.indexers.OccUtils.newDecl;
import junit.framework.TestCase;

import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class CancelBridgeStub implements IIndexingBridge {

	private final IEventBRoot root;
	private final IDeclaration[] imports;

	private final int maxDecl;
	private final int maxOcc;
	private final int maxExp;

	private int numDecl;
	private int numOcc;
	private int numExp;
	
	private boolean cancel;
	public static final int NO_LIMIT = Integer.MAX_VALUE;

	/**
	 * A call to isCancelled returns true when the given number of declarations,
	 * occurrences and exports is reached.
	 * 
	 * @param maxDecl
	 * @param maxOcc
	 * @param maxExp
	 * @param cancel 
	 * @param root 
	 * @param imports 
	 */
	public CancelBridgeStub(int maxDecl, int maxOcc, int maxExp,
			boolean cancel, IEventBRoot root, IDeclaration... imports) {

		this.root = root;
		this.imports = imports;
		
		this.maxDecl = maxDecl;
		this.maxOcc = maxOcc;
		this.maxExp = maxExp;

		this.numDecl = 0;
		this.numOcc = 0;
		this.numExp = 0;
		
		this.cancel = cancel;
	}

	@Override
	public void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IInternalLocation location) {
		numOcc++;
		if (numOcc >= maxOcc) {
			cancel = true;
		}
	}

	@Override
	public IDeclaration declare(IInternalElement element, String name) {
		numDecl++;
		if (numDecl >= maxDecl) {
			cancel = true;
		}
		return newDecl(element, name);
	}

	@Override
	public void export(IDeclaration declaration) {
		numExp++;
		if (numExp >= maxExp) {
			cancel = true;
		}
	}

	@Override
	public IDeclaration[] getImports() {
		return imports;
	}

	@Override
	public IInternalElement getRootToIndex() {
		return root;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	public void assertNumDecl(int expected) {
		TestCase.assertEquals("bad number of declarations", expected, numDecl);
	}

	public void assertNumOcc(int expected) {
		TestCase.assertEquals("bad number of occurrences", expected, numOcc);
	}

	public void assertNumExp(int expected) {
		TestCase.assertEquals("bad number of exports", expected, numExp);
	}

	@Override
	public IDeclaration[] getDeclarations() {
		return new IDeclaration[0];
	}

}
