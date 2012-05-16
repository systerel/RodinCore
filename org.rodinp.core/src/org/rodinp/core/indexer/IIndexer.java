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
package org.rodinp.core.indexer;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

/**
 * Interface for Rodin indexers. Indexers must be able to index one IRodinFile
 * at a time, declaring elements found in it and adding occurrences through the
 * given {@link IIndexingBridge}.
 * <p>
 * Indexers declare themselves as being able to index file whose root is of a
 * given type. Thus, they will only be asked to index files of the type they
 * support.
 * </p>
 * <p>
 * Files can export elements to other files, making them visible outside their
 * declaration file. This feature induces file dependencies, in the sense that
 * file1 depends on file2 if file1 is allowed to contain occurrences of elements
 * exported by file2. Therefore, indexers must be able to tell on which files a
 * given file depends.
 * </p>
 * <p>
 * The export relation is not inherently transitive, so if an element is to be
 * visible through a series of files, every file has to export it in order to
 * allow the next one to import it (that is, to have occurrences of it and
 * export it itself).
 * </p>
 * <p>
 * The same file will undoubtedly be indexed several times during its lifetime
 * in the database, either because of local changes or because files on which it
 * depends have been modified. Each time a file gets indexed anew, indexers can
 * assume that everything is just as if it was the first indexing. For example,
 * any element declared by a previous indexing is to be declared again;
 * similarly, previous occurrences are forgotten and exports must be entirely
 * given anew.
 * </p>
 * <p>
 * Please note the following indexing constraints:
 * <ul>
 * <li>an element cannot have an occurrence if it has not been declared before</li>
 * <li>an element cannot be declared more than once</li>
 * <li>declared elements must be local to the file</li>
 * <li>occurring elements must be either local or imported</li>
 * <li>declared elements should have at least one occurrence when indexing
 * completes</li>
 * </ul>
 * The first constraint is implicitly held by the fact that occurrences are
 * added by giving the declaration of the occurring element. <br>
 * Breaking one of the three next constraints will result in a
 * {@link IllegalArgumentException} being thrown. <br>
 * Finally, when file indexing completes, declarations with no occurrence are
 * simply ignored (not entered into the index tables).
 * </p>
 * 
 * @see IIndexingBridge
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface IIndexer {

	/**
	 * Computes and returns the dependencies of the given file.
	 * <p>
	 * For instance, if file1 depends on file2 and file3 (i.e file1 is allowed
	 * to contain occurrences of elements exported by file2 and file3), then a
	 * call to getDependencies(file1) will return { file2, file3 }.
	 * </p>
	 * 
	 * @param root
	 *            the root element of the file for which dependencies have to be
	 *            extracted
	 * @return an array containing the file dependencies.
	 */
	public IRodinFile[] getDependencies(IInternalElement root);

	/**
	 * Returns the unique id of this indexer. The string returned must be the
	 * full id of the indexer i.e., its plug-in id + its local id as declared in
	 * extension point <code>org.rodinp.core.indexers</code>.
	 * 
	 * @return the unique id of this indexer
	 */
	public String getId();

	/**
	 * Indexes the given file and sends results through calls to the given
	 * IIndexingBridge. The return value indicates whether indexing was
	 * successful.
	 * 
	 * @param bridge
	 *            the indexing facility to which to send results
	 * 
	 * @return whether indexing was successful
	 * @see IIndexingBridge
	 */
	public boolean index(IIndexingBridge bridge);

}