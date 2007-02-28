/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.tool.state.IStateType;

/**
 * This state (sub-) component provides access to information about an abstract event.
 * It is only accessible by way of {@link IAbstractEventTable}.
 * <p>
 * In comparisons only event labels are considered, i.e., two 
 * abstract event infos are considered equal if their labels are equal.
 * </P>
 * 
 * @see IAbstractEventTable
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventInfo extends ISCState, IConvergenceInfo, Comparable {
	
	final static IStateType<IAbstractEventInfo> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".abstractEventInfo");

	/**
	 * Returns a handle to the SC event corresponding to this abstract event info.
	 * 
	 * @return a handle to the SC event corresponding to this abstract event info
	 */
	ISCEvent getEvent();
	
	/**
	 * Returns the label of the SC event corresponding to this abstract event info.
	 * <p>
	 * Calling this method should be prefered over
	 * <code>getEvent().getLabel()</code> because it is more efficient.
	 * </p>
	 * 
	 * @return the label of the SC event corresponding to this abstract event info
	 */
	String getEventLabel();
	
	/**
	 * Returns a typed free variable with the specified name contained in this 
	 * abstract event info, or <code>null</code> if no variable with this name
	 * is contained in this abstract event info.
	 * 
	 * @param name the name of the free identifier to look up
	 * @return the corresponding typed free identifier, or <code>null</code> if none
	 */
	FreeIdentifier getVariable(String name);
	
	/**
	 * Returns the array of typed free variables contained in this abstract event info.
	 * <p>
	 * The free identifiers correspond to event variables.
	 * </p>
	 * 
	 * @return the array of typed free identifiers contained in this abstract event info
	 */
	List<FreeIdentifier> getVariables();
	
	/**
	 * Returns the array of parsed and type-checked predicates corresponding to the guards
	 * of the corresponding event.
	 * 
	 * @return the array of parsed and type-checked predicates corresponding to the guards
	 * of the corresponding event
	 */
	List<Predicate> getGuards();
	
	/**
	 * Returns the array of parsed and type-checked assignments corresponding to the actions
	 * of the corresponding event.
	 * 
	 * @return the array of parsed and type-checked assignments corresponding to the actions
	 * of the corresponding event
	 */
	List<Assignment> getActions();
	
	/**
	 * Returns the event symbol info the event that implicitly refines the event corresponding
	 * to this abstract event info, or <code>null</code> if the abstract event is not implicitly
	 * refined.
	 * <p>
	 * Implicit refinement refers to refined initialisations and inherited events that are 
	 * <b>not</b> allowed to have <b>explicit</b> refinement clauses. Hence, they are referred
	 * to as implicitly refined here. 
	 * </p>
	 * <b>Attention:</b>
	 * Reliable information about refinement relationships can only be detrmined via the 
	 * refinining, i.e. concrete, event. If <code>hasRefineError()</code> is <code>true</code>,
	 * the refinement information available in this abstract event info will be inconsistent.
	 * </p>
	 * 
	 * @return the event symbol info the event that implicitly refines the event corresponding
	 * to this abstract event info, or <code>null</code> if the abstract event is not implicitly
	 * refined
	 */
	IEventSymbolInfo getImplicit();
	
	/**
	 * returns whether the event corresponding to this abstract event info is refined by some event.
	 * 
	 * @return whether the event corresponding to this abstract event info is refined by some event
	 */
	boolean isRefined();
	
	/**
	 * Returns the event symbol infos that merge refine the abstract event corresponding
	 * to this abstract event info.
	 * <p>
	 * <b>Attention:</b>
	 * Reliable information about refinement relationships can only be detrmined via the 
	 * refinining, i.e. concrete, event. If <code>hasRefineError()</code> is <code>true</code>,
	 * the refinement information available in this abstract event info will be inconsistent.
	 * </p>
	 * 
	 * @return the event symbol infos that merge refine the abstract event corresponding
	 * to this abstract event info
	 */
	List<IEventSymbolInfo> getMergeSymbolInfos();
	
	/**
	 * Returns the event symbol infos that split refine the abstract event corresponding
	 * to this abstract event info. This should usually be a list with exactly one element.
	 * See, however, the remark below.
	 * <p>
	 * <b>Attention:</b>
	 * Reliable information about refinement relationships can only be detrmined via the 
	 * refinining, i.e. concrete, event. If <code>hasRefineError()</code> is <code>true</code>,
	 * the refinement information available in this abstract event info will be inconsistent.
	 * </p>
	 * 
	 * @return the event symbol infos that merge refine the abstract event corresponding
	 * to this abstract event info
	 */
	List<IEventSymbolInfo> getSplitSymbolInfos();
	
	/**
	 * Returns the refine error of this abstract event info.
	 * 
	 * @return the refine error of this abstract event info
	 */
	boolean hasRefineError();

}
