/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.tracing;

import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.Tracer;

/**
 * The interface for the origin of a clause.
 * <p>
 * Each clause has an origin, defining its level, its level dependencies
 * and whether it depends on the goal or not.
 *
 * @author François Terrier
 *
 */
public interface IOrigin {

	/**
	 * Traces this origin. Adds to the tracer the original hypotheses
	 * needed to get this origin, and whether it depends on the goal. 
	 * 
	 * @param tracer the tracer
	 */
	void trace(Tracer tracer);
	
	/**
	 * Puts in <code>dependencies</code> every level that contributes
	 * to this origin.
	 * 
	 * @param dependencies the set of dependencies to be populated
	 */
	void addDependenciesTo(Set<Level> dependencies);
	
	/**
	 * Returns <code>true</code> if this origin depends
	 * on the goal, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this origin depends
	 * on the goal, <code>false</code> otherwise.
	 */
	boolean dependsOnGoal();
	
	/**
	 * Returns <code>true</code> if this is a definition, <code>false</code>
	 * otherwise.
	 * 
	 * @return <code>true</code> if this is a definition, <code>false</code>
	 * otherwise
	 */
	boolean isDefinition();
	
	/**
	 * Returns the level of this origin.
	 * 
	 * @return the level of this origin
	 */
	Level getLevel();
	
	/**
	 * Returns the depth of this origin.
	 * <p>
	 * Corresponds to the number of inference steps needed to get
	 * to this origin.
	 * 
	 * @return the depth of this origin
	 */
	int getDepth();
	
	
//	void checkCorrectOrigin() throws IllegalStateException; 
}
