/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.autocompletion;

import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.location.IAttributeLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class AutoCompletion {

	private static final AbstractFilter SET_CST_VAR_FILTER = new TypeFilter(
			ICarrierSet.ELEMENT_TYPE, IConstant.ELEMENT_TYPE,
			IVariable.ELEMENT_TYPE);

	/**
	 * Returns a list of completions for the given location. The list is sorted
	 * alphabetically
	 * 
	 * @param location
	 *            the location where completion is desired
	 * @return a sorted list of possible completions
	 */
	public static List<String> getCompletions(IAttributeLocation location) {
		try {
			RodinCore.makeIndexQuery().waitUpToDate();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		final Set<String> completionNames = getCompletionNames(location);
		final List<String> sortedNames = new ArrayList<String>(completionNames);
		Collections.sort(sortedNames);
		return sortedNames;
	}

	private static Set<String> getCompletionNames(IAttributeLocation location) {
		final IInternalElement locElem = location.getElement();
		final IEvent event = getRelativeEvent(locElem);
		final IRodinFile file = location.getRodinFile();
		if (event == null) { // axiom, theorem, invariant, variant
			return getVisibleSetCstVar(file);
		} else {
			return getEventCompletions(location, event);
		}
	}

	private static Set<String> getVisibleSetCstVar(IRodinFile file) {
		final Set<IDeclaration> decls = getVisibleDecls(file);
		SET_CST_VAR_FILTER.apply(decls);
		return getNames(decls);
	}

	private static Set<IDeclaration> getVisibleDecls(IRodinFile file) {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		final Set<IDeclaration> decls = query.getVisibleDeclarations(file);
		return decls;
	}

	private static Set<String> getEventCompletions(IAttributeLocation location,
			IEvent event) {
		if (isWitness(location)) { // witness
			return getWitnessCompletions(location, event);
		} else { // guard, action
			return getGrdActCompletions(event);
		}
	}

	private static Set<String> getGrdActCompletions(IEvent event) {
		final Set<IDeclaration> decls = getVisibleDecls(event.getRodinFile());
		final AbstractFilter concreteParams = new ParameterFilter(event);
		new CombinedFilter(SET_CST_VAR_FILTER, concreteParams).apply(decls);
		return getNames(decls);
	}

	private static Set<String> getWitnessCompletions(
			IAttributeLocation location, IEvent event) {

		final AbstractFilter abstractParams = new ParameterFilter(event);
		if (isLabel(location)) { // witness label
			return getWitnessLabelCompletions(event);
		} else { // witness predicate
			return getWitnessPredicateCompletions(event, abstractParams);
		}
	}

	private static Set<String> getWitnessPredicateCompletions(IEvent event,
			final AbstractFilter abstractParams) {
		final Set<String> compls = getGrdActCompletions(event);
		compls.addAll(getWitnessLabelCompletions(event));
		return compls;
	}

	private static Set<String> getWitnessLabelCompletions(IEvent event) {
		final Set<String> disapNames = getPrimedDisapVarNames(event);
		// TODO ¿ really get parameters for INITIALISATION ?

		try {
			if (event.isInitialisation()) {
				return disapNames;
			}
		} catch (RodinDBException e) {
			Util.log(e, "while getting completions in "+event);
			// consider as not initialisation => continue
		}
		final Set<IDeclaration> disapParams = CompletionUtil
				.getDisappearingParams(event);
		final Set<String> paramNames = getNames(disapParams);

		disapNames.addAll(paramNames);

		return disapNames;
	}

	private static Set<String> getPrimedDisapVarNames(IEvent event) {
		final Set<IDeclaration> vars = CompletionUtil.getDisappearingVars(event
				.getRodinFile());
		final Set<String> disapNames = getNames(vars);
		return getPrimedNames(disapNames);
	}

	private static IEvent getRelativeEvent(IInternalElement element) {
		return element.getAncestor(IEvent.ELEMENT_TYPE);
	}

	private static boolean isWitness(IAttributeLocation location) {
		final IInternalElement locElem = location.getElement();
		return locElem.getElementType() == IWitness.ELEMENT_TYPE;
	}

	private static boolean isLabel(IAttributeLocation location) {
		final IAttributeType attType = location.getAttributeType();
		return attType == LABEL_ATTRIBUTE;
	}

	private static Set<String> getPrimedNames(Set<String> names) {
		final Set<String> primed = new LinkedHashSet<String>();
		for (String name : names) {
			primed.add(name.concat("\'"));
		}
		return primed;
	}

	private static Set<String> getNames(Collection<IDeclaration> declarations) {
		final Set<String> names = new LinkedHashSet<String>();
		for (IDeclaration declaration : declarations) {
			names.add(declaration.getName());
		}
		return names;
	}
}
