package org.eventb.core.indexer.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.indexer.EventBIndexUtil;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Occurrence;

/**
 * Stub for the indexing toolkit. Stores the actions performed by an indexer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ToolkitStub implements IIndexingToolkit {

	private static <T> String makeString(String listDesc, List<T> list) {
		return listDesc + ": " + list + "\n";
	}

	private static <T> String makeActExpString(List<T> expected, List<T> actual) {
		return makeString("act", actual) + makeString("exp", expected);
	}

	private static <T> void assertSameElements(List<T> expected,
			List<T> actual, String listEltDesc) {
		TestCase.assertEquals(listEltDesc + ": bad size in\n"
				+ makeActExpString(expected, actual), expected.size(), actual
				.size());
		TestCase.assertTrue(listEltDesc + ": incorrect items in"
				+ makeActExpString(expected, actual), actual
				.containsAll(expected));
	}

	private final IRodinFile file;
	private final List<IDeclaration> imports;
	private final List<IDeclaration> declarations;
	private final Map<IInternalElement, List<IOccurrence>> occurrences;
	private final List<IDeclaration> exports;
	private final IProgressMonitor monitor;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            the file to index.
	 * @param imports
	 *            imports for the current file.
	 * @param monitor
	 *            the monitor of the task
	 */
	public ToolkitStub(IRodinFile file, List<IDeclaration> imports,
			IProgressMonitor monitor) {
		this.file = file;
		this.imports = imports;
		this.monitor = monitor;
		this.declarations = new ArrayList<IDeclaration>();
		this.occurrences = new HashMap<IInternalElement, List<IOccurrence>>();
		this.exports = new ArrayList<IDeclaration>();
	}

	@SuppressWarnings("restriction")
	public IDeclaration declare(IInternalElement element, String name) {
		final IDeclaration declaration = new Declaration(element, name);
		declarations.add(declaration);
		return declaration;
	}

	@SuppressWarnings("restriction")
	public void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IRodinLocation location) {
		final IOccurrence occurrence = new Occurrence(kind, location);
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

	public IRodinFile getRodinFile() {
		return file;
	}

	public boolean isCancelled() {
		return monitor.isCanceled();
	}

	/**
	 * @param expected
	 */
	public void assertDeclarations(List<IDeclaration> expected) {
		assertSameElements(expected, declarations, "declarations");
	}

	/**
	 * @param element
	 * @param expected
	 */
	public void assertOccurrences(IInternalElement element,
			List<IOccurrence> expected) {
		assertSameElements(expected, occurrences.get(element), "occurrences");
	}

	/**
	 * @param element
	 * @param expected
	 */
	public void assertOccurrencesOtherThanDecl(IInternalElement element,
			List<IOccurrence> expected) {
		List<IOccurrence> allOccs = occurrences.get(element);
		if (allOccs == null) {
			allOccs = new ArrayList<IOccurrence>();
		}
		final List<IOccurrence> occsNoDecl = getOccsOtherThanDecl(allOccs);
		assertSameElements(expected, occsNoDecl,
				"occurrences other than declaration");
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

	/**
	 * @param expected
	 */
	public void assertExports(List<IDeclaration> expected) {
		assertSameElements(expected, exports, "exports");
	}

}
