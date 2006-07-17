package org.eventb.core;

import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */

public interface IPRProofTree extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prProofTree"; //$NON-NLS-1$

		public enum Status {PENDING, REVIEWED, DISCHARGED};
		
		public Status getStatus() throws RodinDBException;
		
		public IPRProofTreeNode getRootProofTreeNode() throws RodinDBException;
		
		public Set<Predicate> getUsedHypotheses() throws RodinDBException;
		
		public Predicate getGoal() throws RodinDBException;
		
		public void initialize() throws RodinDBException;

		public boolean proofAttempted() throws RodinDBException;

		public ITypeEnvironment getUsedTypeEnvironment() throws RodinDBException;
		
		public ITypeEnvironment getIntroducedTypeEnvironment() throws RodinDBException;
		
		// TODO Reove this eventually
		// public void setStatus(Status status) throws RodinDBException;
}
