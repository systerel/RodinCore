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
public interface IPOGContextRule {
	
	List<ProofObligation> get(SCContextCache cache) throws RodinDBException;
}
