package org.rodinp.internal.core.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;

public class IndexingResult implements IIndexingResult {

	private final IRodinFile file;
	private Map<IInternalElement, IDeclaration> declarations;
	private final Set<IDeclaration> exports;
	private final Map<IInternalElement, Set<IOccurrence>> occurrences;

	public IndexingResult(IRodinFile file) {
		this.file = file;
		this.declarations = new HashMap<IInternalElement, IDeclaration>();
		this.exports = new HashSet<IDeclaration>();
		this.occurrences = new HashMap<IInternalElement, Set<IOccurrence>>();
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

	public void setDeclarations(Map<IInternalElement, IDeclaration> declarations) {
		this.declarations = declarations;
	}

		public Map<IInternalElement, IDeclaration> getDeclarations() {
		return declarations;
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
	
}
