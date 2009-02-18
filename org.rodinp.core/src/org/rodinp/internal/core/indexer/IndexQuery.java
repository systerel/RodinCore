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

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IOccurrenceKind;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexQuery implements IIndexQuery {

	public IDeclaration getDeclaration(IInternalElement element)
			throws InterruptedException {
		return IndexManager.getDefault().getDeclaration(element);
	}

	public IDeclaration[] getDeclarations(IRodinFile file)
			throws InterruptedException {
		return IndexManager.getDefault().getDeclarations(file);
	}

	public IDeclaration[] getVisibleDeclarations(IRodinFile file)
			throws InterruptedException {
		return IndexManager.getDefault().getVisibleDeclarations(file);
	}

	public IDeclaration[] getDeclarations(IRodinProject project, String name)
			throws InterruptedException {
		return IndexManager.getDefault().getDeclarations(project, name);
	}

	public IDeclaration[] getDeclarations(IRodinFile file, String name)
			throws InterruptedException {
		final IDeclaration[] declarations = getDeclarations(file);
		final NameFilter nameFilter = new NameFilter(name);
		final List<IDeclaration> result = nameFilter.filter(declarations);

		return result.toArray(new IDeclaration[result.size()]);
	}

	public IDeclaration[] getDeclarations(IRodinFile file,
			IInternalElementType<?> type) throws InterruptedException {
		final IDeclaration[] declarations = getDeclarations(file);
		final TypeFilter typeFilter = new TypeFilter(type);
		final List<IDeclaration> result = typeFilter.filter(declarations);

		return result.toArray(new IDeclaration[result.size()]);
	}

	public IOccurrence[] getOccurrences(IDeclaration declaration)
			throws InterruptedException {
		return IndexManager.getDefault().getOccurrences(declaration);
	}

	public IOccurrence[] getOccurrences(IDeclaration declaration,
			IOccurrenceKind kind) throws InterruptedException {

		final IOccurrence[] occurrences = getOccurrences(declaration);
		final KindFilter kindFilter = new KindFilter(kind);
		final List<IOccurrence> result = kindFilter.filter(occurrences);

		return result.toArray(new IOccurrence[result.size()]);
	}

	public IOccurrence[] getOccurrences(IDeclaration declaration,
			IRodinFile file) throws InterruptedException {
		final IOccurrence[] occurrences = getOccurrences(declaration);
		final FileFilter fileFilter = new FileFilter(file);
		final List<IOccurrence> result = fileFilter.filter(occurrences);

		return result.toArray(new IOccurrence[result.size()]);
	}

	public IOccurrence[] getOccurrences(IDeclaration declaration,
			IOccurrenceKind kind, IRodinFile file) throws InterruptedException {
		final IOccurrence[] occurrences = getOccurrences(declaration);
		final KindFileFilter kindFileFilter = new KindFileFilter(kind, file);
		final List<IOccurrence> result = kindFileFilter.filter(occurrences);

		return result.toArray(new IOccurrence[result.size()]);
	}

	public void waitUpToDate() throws InterruptedException {
		IndexManager.getDefault().waitUpToDate();
	}

	private static abstract class Filter<T> {
		abstract boolean keep(T t);

		public Filter() {
			// required to avoid synthetic accessor method emulation
		}

		public final List<T> filter(T[] toFilter) {
			final List<T> result = new ArrayList<T>();

			for (T t : toFilter) {
				if (keep(t)) {
					result.add(t);
				}
			}
			return result;
		}

	}

	private static class NameFilter extends Filter<IDeclaration> {
		private final String name;

		public NameFilter(String name) {
			this.name = name;
		}

		@Override
		public boolean keep(IDeclaration declaration) {
			return declaration.getName().equals(name);
		}
	}

	private static class TypeFilter extends Filter<IDeclaration> {
		private final IInternalElementType<?> type;

		public TypeFilter(IInternalElementType<?> type) {
			this.type = type;
		}

		@Override
		public boolean keep(IDeclaration declaration) {
			return declaration.getElement().getElementType() == type;
		}
	}

	private static class KindFilter extends Filter<IOccurrence> {
		private final IOccurrenceKind kind;

		public KindFilter(IOccurrenceKind kind) {
			this.kind = kind;
		}

		@Override
		public boolean keep(IOccurrence occurrence) {
			return occurrence.getKind().equals(kind);
		}
	}

	private static class FileFilter extends Filter<IOccurrence> {
		private final IRodinFile file;

		public FileFilter(IRodinFile file) {
			this.file = file;
		}

		@Override
		public boolean keep(IOccurrence occurrence) {
			return occurrence.getRodinFile().equals(file);
		}
	}

	private static class KindFileFilter extends Filter<IOccurrence> {
		private final FileFilter fileFilter;
		private final KindFilter kindFilter;

		public KindFileFilter(IOccurrenceKind kind, IRodinFile file) {
			this.fileFilter = new FileFilter(file);
			this.kindFilter = new KindFilter(kind);
		}

		@Override
		public boolean keep(IOccurrence occurrence) {
			return fileFilter.keep(occurrence) && kindFilter.keep(occurrence);
		}
	}
}
