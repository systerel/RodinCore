/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;

public class IndexingResult implements IIndexingResult, Cloneable {

	private final IRodinFile file;
	private Map<IInternalElement, IDeclaration> declarations;
	private final Set<IDeclaration> exports;
	private final Map<IInternalElement, Set<IOccurrence>> occurrences;
	private boolean success;

	public static IIndexingResult failed(IRodinFile f) {
		return new IndexingResult(f);
	}

	public IndexingResult(IRodinFile file) {
		this.file = file;
		this.declarations = new HashMap<IInternalElement, IDeclaration>();
		this.exports = new HashSet<IDeclaration>();
		this.occurrences = new HashMap<IInternalElement, Set<IOccurrence>>();
		this.success = false;
	}

	@Override
	public IIndexingResult clone() {

		final IndexingResult copy = new IndexingResult(file);
		copy.declarations =
				new HashMap<IInternalElement, IDeclaration>(declarations);
		copy.exports.addAll(exports);
		copy.occurrences.putAll(occurrences);
		copy.success = success;

		return copy;
	}

	public void addExport(IDeclaration declaration) {
		exports.add(declaration);
	}

	public void addOccurrence(IInternalElement element, IOccurrence occurrence) {
		Set<IOccurrence> set = occurrences.get(element);
		if (set == null) {
			set = new HashSet<IOccurrence>();
			occurrences.put(element, set);
		}
		set.add(occurrence);
	}

	public void putDeclaration(IDeclaration declaration) {
		declarations.put(declaration.getElement(), declaration);
	}
	
	public boolean isDeclared(IInternalElement element) {
		return declarations.containsKey(element);
	}
	
	public void setSuccess(boolean value) {
		this.success = value;
	}

	public void removeNonOccurringElements() {
		final Iterator<IInternalElement> iter = declarations.keySet().iterator();
		while(iter.hasNext()) {
			final IInternalElement element = iter.next();
			if (!occurrences.containsKey(element)) {
				exports.remove(declarations.get(element));
				iter.remove();
				
				if (IndexManager.DEBUG) {
					System.out.println("Indexing "
							+ file.getPath()
							+ ": Removed non occurring declaration of: "
							+ element);
				}
			}
		}
	}
	
	public Collection<IDeclaration> getDeclarations() {
		final Collection<IDeclaration> decls = declarations.values();
		return Collections.unmodifiableCollection(decls);
	}

	public Set<IDeclaration> getExports() {
		return exports;
	}

	public Map<IInternalElement, Set<IOccurrence>> getOccurrences() {
		return occurrences;
	}

	public IRodinFile getFile() {
		return file;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("file: " + file + "\n");
		sb.append("success: " + success + "\n");
		sb.append("declarations: " + declarations.values() + "\n");
		sb.append("occurrences: " + occurrences + "\n");
		sb.append("exports: " + exports + "\n");
		return sb.toString();
	}

}
