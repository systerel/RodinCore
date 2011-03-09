/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser.operators;

/**
 * Describes the relationship between two operators: left (on the left) and
 * right (on the right).
 */
public enum OperatorRelationship {
	LEFT_PRIORITY,       // priority(left)  > priority(right)
	RIGHT_PRIORITY,      // priority(right) > priority(left)
	COMPATIBLE,          // left then right is allowed w/o parentheses
	INCOMPATIBLE,        // no combination is allowed
}