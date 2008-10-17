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

import static org.eventb.core.indexer.tests.ListAssert.assertSameElements;
import static org.eventb.core.indexer.tests.OccUtils.newDecl;
import static org.eventb.core.indexer.tests.OccUtils.newOcc;
import static org.eventb.core.indexer.tests.ResourceUtils.EMPTY_DECL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEventBRoot;
import org.eventb.core.indexer.EventBIndexUtil;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;

/**
 * Stub for the indexing toolkit. Stores the actions performed by an indexer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ToolkitStub implements IIndexingToolkit {

	private final IEventBRoot root;
	private final List<IDeclaration> imports;
	private final List<IDeclaration> declarations;
	private final Map<IInternalElement, List<IOccurrence>> occurrences;
	private final List<IDeclaration> exports;

	// TODO change constructor imports arg from List to ...
	/**
	 * Constructor.
	 * 
	 * @param root
	 *            the root to index.
	 * @param imports
	 *            imports for the current root.
	 */
	public ToolkitStub(IEventBRoot root, IDeclaration... imports) {
		this.root = root;
		this.imports = Arrays.asList(imports);
		this.declarations = new ArrayList<IDeclaration>();
		this.occurrences = new HashMap<IInternalElement, List<IOccurrence>>();
		this.exports = new ArrayList<IDeclaration>();
	}

	@SuppressWarnings("restriction")
	public IDeclaration declare(IInternalElement element, String name) {
		final IDeclaration declaration = newDecl(element, name);
		declarations.add(declaration);
		return declaration;
	}

	@SuppressWarnings("restriction")
	public void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IRodinLocation location) {
		final IOccurrence occurrence = newOcc(kind, location);
		final IInternalElement element = declaration.getElement();
		List<IOccurrence> list = occurrences.get(element);
		if (list == null) {
			list = new ArrayList<IOccurrence>();
			occurrences.put(element, list);
		}
		list.add(occurrence);
	}

	public void export(IDeclaration declaration) {
		exports.add(declaration);
	}

	public IDeclaration[] getImports() {
		return imports.toArray(new IDeclaration[imports.size()]);
	}

	public IInternalElement getRootToIndex() {
		return root;
	}

	public boolean isCancelled() {
		return false;
	}

	/**
	 * @param expected
	 */
	public void assertDeclarations(IDeclaration... expected) {
		final List<IDeclaration> expList = Arrays.asList(expected);
		assertSameElements(expList, declarations, "declarations");
	}

	public void assertDeclarations(IElementType<?> elementType,
			IDeclaration... expected) {
		final List<IDeclaration> expList = Arrays.asList(expected);
		final List<IDeclaration> declsOfType = getDeclsOfType(elementType,
				declarations);
		assertSameElements(expList, declsOfType, "declarations of type "
				+ elementType);
	}

	/**
	 * @param expected
	 */
	public void assertExports(IDeclaration... expected) {
		final List<IDeclaration> expList = Arrays.asList(expected);
		assertSameElements(expList, exports, "exports");
	}

	public void assertEmptyExports() {
		assertSameElements(EMPTY_DECL, exports, "exports");
	}

	public void assertExports(IElementType<?> elementType,
			IDeclaration... expected) {
		final List<IDeclaration> expList = Arrays.asList(expected);
		final List<IDeclaration> declsOfType = getDeclsOfType(elementType,
				exports);
		assertSameElements(expList, declsOfType, "exports of type "
				+ elementType);
	}

	public void assertEmptyOccurrences(IInternalElement element) {
		assertOccurrences(element);
	}

	/**
	 * @param element
	 * @param expected
	 */
	public void assertOccurrences(IInternalElement element,
			IOccurrence... expected) {
		final List<IOccurrence> expList = Arrays.asList(expected);
		List<IOccurrence> allOccs = getAllOccs(element);
		assertSameElements(expList, allOccs, "occurrences");
	}

	/**
	 * @param element
	 * @param expected
	 */
	public void assertOccurrencesOtherThanDecl(IInternalElement element,
			IOccurrence... expected) {
		final List<IOccurrence> expList = Arrays.asList(expected);
		List<IOccurrence> allOccs = getAllOccs(element);
		final List<IOccurrence> occsNoDecl = getOccsOtherThanDecl(allOccs);
		assertSameElements(expList, occsNoDecl,
				"occurrences other than declaration");
	}

	private List<IOccurrence> getAllOccs(IInternalElement element) {
		List<IOccurrence> allOccs = occurrences.get(element);
		if (allOccs == null) {
			allOccs = new ArrayList<IOccurrence>();
		}
		return allOccs;
	}

	private static List<IOccurrence> getOccsOtherThanDecl(List<IOccurrence> occs) {
		final List<IOccurrence> result = new ArrayList<IOccurrence>();
		for (IOccurrence occurrence : occs) {
			if (occurrence.getKind().equals(EventBIndexUtil.DECLARATION)) {
				continue;
			}
			result.add(occurrence);
		}
		return result;
	}

	private static List<IDeclaration> getDeclsOfType(
			IElementType<?> elementType, List<IDeclaration> declarations) {
		final List<IDeclaration> result = new ArrayList<IDeclaration>();
		for (IDeclaration declaration : declarations) {
			if (declaration.getElement().getElementType().equals(elementType)) {
				result.add(declaration);
			}
		}
		return result;
	}

}
