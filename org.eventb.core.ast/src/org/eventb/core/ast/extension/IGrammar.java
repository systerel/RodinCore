/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;

/**
 * Common protocol for grammars.
 * <p>
 * Instances of this interface are provided by
 * {@link FormulaFactory#getGrammarView()}. The purpose is to give a view of a
 * grammar. Objects returned by the various methods are not intended to be
 * modified. Anyway, modifications would have no effect on the described
 * grammar.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IGrammar {

	/**
	 * Returns a set of all operator groups in this grammar.
	 * 
	 * @return a set of operator groups
	 * @see IOperatorGroup
	 */
	Set<IOperatorGroup> getGroups();

	/**
	 * Returns a map describing the priorities between operator groups of this
	 * grammar.
	 * <p>
	 * The meaning is that the key group has a lower priority than all groups in
	 * the value set.
	 * </p>
	 * 
	 * @return a map representation of group priorities
	 * @see IPriorityMediator#addGroupPriority(String, String)
	 */
	Map<IOperatorGroup, Set<IOperatorGroup>> getGroupPriorities();
}
