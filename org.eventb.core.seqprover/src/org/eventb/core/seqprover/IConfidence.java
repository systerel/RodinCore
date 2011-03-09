/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;



/**
 * Definition and interpretation of confidence of a rule, a proof tree node, or a
 * proof tree.
 * 
 * <p>
 * A confidence is an integer value associated to a rule or a proof tree node that 
 * characterises the <em>level of confidence</em> of the logical content of the related
 * entity. The integer value encoding a confidence level models a finite totally ordered
 * set. Greater the integer value, higher is the confidence level. The valid range and 
 * interpretaion of this integer value is set here. 
 * </p>
 * 
 * <p>
 * Along with its logical content, each rule also returns a confidence related to it. 
 * This rule confidence information is then used to compute confience for proof tree
 * nodes and entire proof trees.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IConfidence {

	/**
	 * The minimum confidence value for proof trees. (Reserved)
	 * <p>
	 * Ths confidence value is reserved for unattempted proof trees and may not be used
	 * as a confidence level for rules.
	 * </p>
	 * <p>
	 * A proof tree is considered unattempted if its root is open and is not commented.
	 * </p>
	 * <p>
	 * A confidence value below <code>UNATTEMPTED</code> is not considered valid.
	 * </p>
	 */
	final int UNATTEMPTED = -99;
	
	/**
	 * The minimum confidence value for proof tree nodes. (Reserved)
	 * <p>
	 * Ths confidence value is reserved for pending proof tree nodes and may not be used
	 * as a confidence level for rules.
	 * </p>
	 */
	final int PENDING = 0;
	
	/**
	 * Ths confidence value corresponds to the maximum confidence that a rule or proof
	 * tree node can give to the system and still make it count as reviewed.
	 * 
	 * <p>
	 * All confidence values in the range (<code>PENDING</code>,<code>REVIEWED_MAX</code>]
	 * are interpretted as reviewed.
	 * </p>
	 */
	final int REVIEWED_MAX = 500;
	
	/**
	 * The maximun confidence value.
	 * <p>
	 * Ths confidence value corresponds to the maximum confidence that a rule or proof
	 * tree node can give to the system.
	 * </p>
	 * <p>
	 * Confidence values in the range (<code>REVIEWED_MAX</code>,<code>DISCHARGED_MAX</code>]
	 * are interpretted as discharged.
	 * </p>
	 * <p>
	 * A confidence value above <code>DISCHARGED_MAX</code> is not considered valid.
	 * </p>
	 */
	final int DISCHARGED_MAX = 1000;
}
