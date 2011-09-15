/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import java.util.List;

/**
 * Represents a mechanism that produces new subgoals from a goal.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
public abstract class Generator {

	public abstract List<Goal> generate(Goal goal, MembershipGoalImpl impl);

}