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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.indexer.IPropagator;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexQuery implements IIndexQuery {

	public void waitUpToDate() throws InterruptedException {
		IndexManager.getDefault().waitUpToDate();
	}

	public IDeclaration getDeclaration(IInternalElement element) {
		return IndexManager.getDefault().getDeclaration(element);
	}

	public Set<IDeclaration> getDeclarations(IRodinFile file) {
		return IndexManager.getDefault().getDeclarations(file);
	}

	public Set<IDeclaration> getVisibleDeclarations(IRodinFile file) {
		return IndexManager.getDefault().getVisibleDeclarations(file);
	}

	public Set<IDeclaration> getDeclarations(IRodinProject project, String name) {
		return IndexManager.getDefault().getDeclarations(project, name);
	}

	public Set<IOccurrence> getOccurrences(IDeclaration declaration) {
		return IndexManager.getDefault().getOccurrences(declaration);
	}

	private void addOccurrences(IDeclaration declaration,
			IPropagator propagator, Set<IOccurrence> set) {
		final Set<IOccurrence> occurrences = getOccurrences(declaration);
		for (IOccurrence occ : occurrences) {
			final boolean added = set.add(occ);
			if (added) {
				final IDeclaration declRelativeElem = propagator
						.getRelativeDeclaration(occ, this);
				if (declRelativeElem != null) {
					addOccurrences(declRelativeElem, propagator, set);
				}
			}
		}
	}

	public Set<IOccurrence> getOccurrences(IDeclaration declaration,
			IPropagator propagator) {
		final Set<IOccurrence> result = new LinkedHashSet<IOccurrence>();
		addOccurrences(declaration, propagator, result);
		return result;
	}

	public Set<IOccurrence> getOccurrences(Collection<IDeclaration> declarations) {
		final Set<IOccurrence> result = new LinkedHashSet<IOccurrence>();
		for (IDeclaration declaration : declarations) {
			result.addAll(getOccurrences(declaration));
		}
		return result;
	}

	public Set<IDeclaration> getDeclarations(Collection<IOccurrence> occurrences) {
		final Set<IDeclaration> result = new LinkedHashSet<IDeclaration>();
		for (IOccurrence occurrence : occurrences) {
			result.add(occurrence.getDeclaration());
		}
		return result;
	}

	public void filterName(Set<IDeclaration> declarations, String name) {
		new NameFilter(name).filter(declarations);
	}

	public void filterType(Set<IDeclaration> declarations,
			IInternalElementType<?> type) {
		new TypeFilter(type).filter(declarations);
	}

	public void filterFile(Set<IOccurrence> occurrences, IRodinFile file) {
		new FileFilter(file).filter(occurrences);
	}

	public void filterKind(Set<IOccurrence> occurrences, IOccurrenceKind kind) {
		new KindFilter(kind).filter(occurrences);
	}

	public void filterLocation(Set<IOccurrence> occurrences,
			IInternalLocation location) {
		new LocationFilter(location).filter(occurrences);
	}

	private static abstract class Filter<T> {

		public Filter() {
			// required to avoid synthetic accessor method emulation
		}

		abstract boolean keep(T t);

		public final void filter(Set<T> toFilter) {
			final Iterator<T> iter = toFilter.iterator();
			while (iter.hasNext()) {
				final T t = iter.next();
				if (!keep(t)) {
					iter.remove();
				}
			}
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

	private static class LocationFilter extends Filter<IOccurrence> {
		private final IInternalLocation location;

		public LocationFilter(IInternalLocation location) {
			this.location = location;
		}

		@Override
		public boolean keep(IOccurrence occurrence) {
			return occurrence.getLocation().isIncludedIn(location);
		}
	}
}
