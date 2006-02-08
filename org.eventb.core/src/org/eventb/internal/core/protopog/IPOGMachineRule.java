/**
 * 
 */
package org.eventb.internal.core.protopog;

import java.util.List;

import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public interface IPOGMachineRule {

	List<ProofObligation> get(SCMachineCache cache) throws RodinDBException;
	
}
