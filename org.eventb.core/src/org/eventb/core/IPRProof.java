/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - updated Javadoc
 *     Systerel - moved used reasoners to proof root
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B Proof elements stored in the RODIN Database.
 * 
 * <p>
 * This is intended to be the top-most interface for serializing and
 * deserializing proof data structures. This means that it is not intended that
 * database elements beyond this point be seen or manipulated. Note also that
 * this interface provides a more abstract view of the database by hiding the
 * internal database representation.
 * </p>
 * <p>
 * This interface is meant to be used as follows. An instance of IProofTree may
 * be serialized into the database using the
 * {@link #setProofTree(IProofTree, IProgressMonitor)} method. This serializes
 * the formula factory, the proof skeleton ({@link IProofSkeleton}) and proof
 * dependencies {@link IProofDependencies} of this proof tree which may later be
 * deserialized using the {@link #getFormulaFactory(IProgressMonitor)},
 * {@link #getSkeleton(FormulaFactory, IProgressMonitor)} and
 * {@link #getProofDependencies(FormulaFactory, IProgressMonitor)} methods.
 * </p>
 * <p>
 * The proof dependencies are used to check if the proof is applicable to any
 * proof obligation. The proof skeleton can be used to rebuild the proof tree.
 * </p>
 * <p>
 * All predicates and expressions used in a proof are gathered in this element.
 * Hence this element contains also the following children:
 * <ul>
 * <li>Identifiers occurring free in a predicate or expression in the proof.</li>
 * <li>Predicates used in the proof.</li>
 * <li>Expressions used in the proof.</li>
 * </ul>
 * Moreover, the proof also carries an attribute called
 * <code>org.eventb.core.prSets</code> which contains a list of all carrier
 * sets used in the proof (set names separated by commas).
 * </p>
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @see IProofTree
 * @see IProofDependencies
 * @see IProofSkeleton
 * @see IProofManager
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IPRProof extends IInternalElement, IPRProofInfoElement {

	IInternalElementType<IPRProof> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProof"); //$NON-NLS-1$

	/**
	 * Returns the confidence of proof tree stored in this proof element.
	 * <p>
	 * Note that this method returns <code>IConfidence.UNATTEMPTED</code> if
	 * no proof is currently stored in this proof element.
	 * </p>
	 * 
	 * @return the confidence of this proof tree
	 * 
	 * @throws RodinDBException
	 */
	@Override
	int getConfidence() throws RodinDBException;

	/**
	 * Returns whether this proof obligation has been discharged manually. A
	 * proof obligation is considered as manually discharged if the end user
	 * entered manually its associated proof (even partially).
	 * <p>
	 * The returned value is <code>true</code> iff the corresponding attribute
	 * contains <code>true</code>. Hence, if the attribute is absent,
	 * <code>false</code> is returned.
	 * </p>
	 * 
	 * @return <code>true</code> if the user contributed to the proof of this
	 *         proof obligation
	 * 
	 * @throws RodinDBException
	 * @see #setHasManualProof(boolean, IProgressMonitor)
	 */
	@Override
	boolean getHasManualProof() throws RodinDBException;

	/**
	 * Sets whether this proof obligation has been discharged manually.
	 * 
	 * 
	 * @param value
	 *            The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see #getHasManualProof()
	 */
	@Override
	void setHasManualProof(boolean value, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Sets the proof tree of this proof element by serializing the given proof
	 * tree into the database.
	 * 
	 * <p>
	 * This method also increments the PR stamp for this element, as well as the
	 * file that contains it.
	 * </p>
	 * 
	 * @param proofTree
	 *            The proof tree to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 */
	public void setProofTree(IProofTree proofTree, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns a proof tree for this proof. The proof tree is built only with
	 * information local to this proof. The root sequent of the proof tree is
	 * made of the needed hypotheses and goal (if any) of this proof. Then, all
	 * rules contained in this proof are applied successively to reconstruct the
	 * proof tree saved in this proof.
	 * <p>
	 * No attempt is made to access the corresponding proof obligation. No
	 * reasoner is run.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the proof tree, or <code>null</code> if the proof tree could not
	 *         be rebuilt.
	 * @throws CoreException
	 *             if there was a problem accessing the Rodin database
	 */
	public IProofTree getProofTree(IProgressMonitor monitor)
			throws CoreException;
	
	/**
	 * Returns the formula factory of this proof. The returned factory can be
	 * used subsequently to parse the proof dependencies and proof skeleton of
	 * this proof.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the formula factory to use for parsing this proof
	 * @throws CoreException
	 *             if a problem occurs while deserializing
	 * @since 3.0
	 */
	FormulaFactory getFormulaFactory(IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Returns the proof dependencies for proof tree stored in this proof
	 * element. Normally, the given factory should be the result of
	 * {@link #getFormulaFactory(IProgressMonitor)}. Optionally, another factory
	 * can be used but it is up to the caller to ensure that it will be
	 * able to parse the stored proof.
	 * <p>
	 * In case no proof tree is stored in this proof element, this method
	 * returns the broadest result (i.e. without any dependency)
	 * </p>
	 * 
	 * @param factory
	 *            the formula factory to use
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return The proof dependencies for proof tree stored in this proof
	 *         element
	 * @throws RodinDBException
	 */
	IProofDependencies getProofDependencies(FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the proof skeleton of the proof tree stored in this proof
	 * element. Normally, the given factory should be the result of
	 * {@link #getFormulaFactory(IProgressMonitor)}. Optionally, another factory
	 * can be used but it is up to the caller to ensure that it will be
	 * able to parse the stored proof.
	 * 
	 * @param factory
	 *            the formula factory to use
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the stored proof skeleton
	 * 
	 * @throws RodinDBException
	 */
	IProofSkeleton getSkeleton(FormulaFactory factory, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns a handle to the identifier child with the given name. That child
	 * element describes an identifier that occurs free in the proof.
	 * <p>
	 * This is a handle-only method. The identifier element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            name of the child
	 * 
	 * @return a handle to the child identifier with the given name
	 * @see #getIdentifiers()
	 */
	IPRIdentifier getIdentifier(String name);

	/**
	 * Returns all children identifier elements. Those child elements describe
	 * all identifiers that occur free in the proof (and are not introduced as
	 * fresh identifiers by the proof itself).
	 * 
	 * @return an array of all chidren element of type identifier
	 * @throws RodinDBException
	 * @see #getIdentifier(String)
	 */
	IPRIdentifier[] getIdentifiers() throws RodinDBException;

	/**
	 * Returns a handle to the expression child with the given name.
	 * <p>
	 * This is a handle-only method. The expression element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the child
	 * 
	 * @return a handle to the child expression with the given element name
	 * @see #getExpressions()
	 */
	IPRStoredExpr getExpression(String name);

	/**
	 * Returns all children expression elements.
	 * 
	 * @return an array of all chidren element of type expression
	 * @throws RodinDBException
	 * @see #getExpression(String)
	 */
	IPRStoredExpr[] getExpressions() throws RodinDBException;

	/**
	 * Returns a handle to the predicate child with the given name.
	 * <p>
	 * This is a handle-only method. The predicate element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the child
	 * 
	 * @return a handle to the child predicate with the given element name
	 * @see #getPredicates()
	 */
	IPRStoredPred getPredicate(String name);

	/**
	 * Returns all children predicate elements.
	 * 
	 * @return an array of all chidren element of type predicate
	 * @throws RodinDBException
	 * @see #getPredicate(String)
	 */
	IPRStoredPred[] getPredicates() throws RodinDBException;

	/**
	 * Returns the carrier sets that are used in this proof.
	 * 
	 * @return an array of the carrier set names of this proof
	 * @throws RodinDBException
	 * @see #setSets(String[], IProgressMonitor)
	 */
	String[] getSets() throws RodinDBException;

	/**
	 * Sets the carrier sets that are used in this proof.
	 * 
	 * @param sets
	 *            the carrier set names to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see #getSets()
	 */
	void setSets(String[] sets, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns a handle to the proof rule child with the given name.
	 * <p>
	 * This is a handle-only method. The proof rule element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the child
	 * 
	 * @return a handle to the child proof rule with the given element name
	 * @see #getProofRules()
	 */
	IPRProofRule getProofRule(String name);

	/**
	 * Returns all children proof rule elements. In a well-formed file, this
	 * array should contain only one element.
	 * 
	 * @return an array of all chidren element of type proof rule
	 * @throws RodinDBException
	 * @see #getProofRule(String)
	 */
	IPRProofRule[] getProofRules() throws RodinDBException;

	/**
	 * Returns a handle to the reasoner child with the given name.
	 * <p>
	 * This is a handle-only method. The reasoner element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the child
	 * @return a handle to the child reasoner with the given element name
	 * @since 2.2
	 */
	IPRReasoner getReasoner(String name);

	/**
	 * Returns all reasoner child elements.
	 * 
	 * @return an array of all used reasoners
	 * @throws RodinDBException
	 * @see #getPredicate(String)
	 * @since 2.2
	 */
	IPRReasoner[] getReasoners() throws RodinDBException;
}
