/*******************************************************************************
 * Copyright (c) 2008, 2023 Systerel and others.
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
import static org.eventb.internal.core.autocompletion.CompletionUtil.getDeterministicallyAssignedVars;
import static org.eventb.internal.core.autocompletion.CompletionUtil.getDisappearingVars;
import static org.eventb.internal.core.autocompletion.CompletionUtil.getParameters;
import static org.eventb.internal.core.indexers.IdentTable.getPrimedName;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
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
	 * Returns a set of identifier proposals for the given location.
	 * 
	 * @param location
	 *            the location where completion is desired
	 * @param waitUpToDate
	 *            <code>true</code> iff method call shall be blocked until the
	 *            underlying indexing system is up to date
	 * @return a set of proposals
	 */
	public static Set<String> getProposals(IAttributeLocation location,
			boolean waitUpToDate) {
		if (waitUpToDate) {
			try {
				RodinCore.makeIndexQuery().waitUpToDate();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return getCompletionNames(location);
	}

	private static Set<String> getCompletionNames(IAttributeLocation location) {
		final IInternalElement locElem = location.getElement();
		final IEvent event = getRelativeEvent(locElem);
		final IRodinFile file = location.getRodinFile();
		if (event == null) { // axiom, invariant, variant
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
		if (isEventLabel(location)) {
			return getAbstractEventNames(event);
		} else if (isWitness(location)) { // witness
			return getWitnessCompletions(location, event);
		} else { // guard, action
			return getGrdActCompletions(event);
		}
	}

	private static boolean isEventLabel(IAttributeLocation location) {
		return location.getElement().getElementType() == IEvent.ELEMENT_TYPE
				&& location.getAttributeType() == LABEL_ATTRIBUTE;
	}

	private static Set<String> getAbstractEventNames(IEvent event) {
		final Set<IDeclaration> seenEvents = CompletionUtil
				.getSeenEvents(event.getRodinFile());
		return getNames(seenEvents);
	}

	private static Set<String> getGrdActCompletions(IEvent event) {
		final Set<IDeclaration> decls = getVisibleDecls(event.getRodinFile());
		final Set<IDeclaration> parameters = getParameters(event);
		final AbstractFilter concreteParams = new EnumeratedFilter(parameters);
		new CombinedFilter(SET_CST_VAR_FILTER, concreteParams).apply(decls);
		removeDisappearingVars(decls, event.getRodinFile());
		return getNames(decls);
	}

	private static void removeDisappearingVars(final Set<IDeclaration> decls,
			IRodinFile file) {
		final Set<IDeclaration> disapVars = getDisappearingVars(file);
		decls.removeAll(disapVars);
	}

	private static Set<String> getWitnessCompletions(
			IAttributeLocation location, IEvent event) {

		if (isLabel(location)) { // witness label
			return getWitnessLabelCompletions(event);
		} else { // witness predicate
			return getWitnessPredicateCompletions(event);
		}
	}

	private static Set<String> getWitnessPredicateCompletions(IEvent event) {
		final Set<String> compls = getGrdActCompletions(event);
		compls.addAll(getDisapVarNames(event));
		compls.addAll(getWitnessLabelCompletions(event));
		return compls;
	}

	private static Set<String> getWitnessLabelCompletions(IEvent event) {
		final Set<String> disapNames = getPrimedDisapVarNames(event);

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

	private static Set<String> getDisapVarNames(IEvent event) {
		final Set<IDeclaration> vars = CompletionUtil.getDisappearingVars(event
				.getRodinFile());
		removeDeterministicallyAssigned(vars, CompletionUtil
				.getAbstractEvents(event));
		return getNames(vars);
	}
	
	private static Set<String> getPrimedDisapVarNames(IEvent event) {
		final Set<String> disapNames = getDisapVarNames(event);
		final IEventBRoot root = (IEventBRoot) event.getRoot();
		FormulaFactory ff;
		try {
			ff = root.getSafeFormulaFactory();
		} catch (CoreException e) {
			// Since we only want to get a primed identifier, we can try to keep going with
			// the default factory.
			ff = FormulaFactory.getDefault();
			// It's probably better not to log the exception to avoid cluttering the error
			// log with a message on each completion attempt. Anyway, if the factory can't
			// be loaded, static checking surely failed and the error was reported then.
		}
		return getPrimedNames(disapNames, ff);
	}

	private static void removeDeterministicallyAssigned(Set<IDeclaration> vars,
			Set<IEvent> abstractEvents) {
		for (IEvent event : abstractEvents) {
			vars.removeAll(getDeterministicallyAssignedVars(event));
		}
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

	private static Set<String> getPrimedNames(Set<String> names,
			FormulaFactory ff) {
		final Set<String> primed = new LinkedHashSet<String>();
		for (String name : names) {
			primed.add(getPrimedName(name, ff));
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
