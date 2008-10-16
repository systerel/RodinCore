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

import static org.eventb.core.indexer.tests.OccUtils.newDecl;
import junit.framework.TestCase;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class CancelToolkitStub implements IIndexingToolkit {

	private final IRodinFile file;
	private final IDeclaration[] imports;

	private final int maxDecl;
	private final int maxOcc;
	private final int maxExp;

	private int numDecl;
	private int numOcc;
	private int numExp;
	
	private boolean cancel;

	/**
	 * A call to isCancelled returns true when the given number of declarations,
	 * occurrences and exports is reached.
	 * 
	 * @param maxDecl
	 * @param maxOcc
	 * @param maxExp
	 * @param cancel 
	 * @param file 
	 * @param imports 
	 */
	public CancelToolkitStub(int maxDecl, int maxOcc, int maxExp,
			boolean cancel, IRodinFile file, IDeclaration... imports) {

		this.file = file;
		this.imports = imports;
		
		this.maxDecl = maxDecl;
		this.maxOcc = maxOcc;
		this.maxExp = maxExp;

		this.numDecl = 0;
		this.numOcc = 0;
		this.numExp = 0;
		
		this.cancel = cancel;
	}

	public void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IRodinLocation location) {
		numOcc++;
		if (numOcc >= maxOcc) {
			cancel = true;
		}
	}

	public IDeclaration declare(IInternalElement element, String name) {
		numDecl++;
		if (numDecl >= maxDecl) {
			cancel = true;
		}
		return newDecl(element, name);
	}

	public void export(IDeclaration declaration) {
		numExp++;
		if (numExp >= maxExp) {
			cancel = true;
		}
	}

	public IDeclaration[] getImports() {
		return imports;
	}

	public IRodinFile getRodinFile() {
		return file;
	}

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

}
