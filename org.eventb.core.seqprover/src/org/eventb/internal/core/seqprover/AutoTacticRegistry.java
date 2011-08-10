/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - implemented parameterized auto tactics
 *     Systerel - implemented tactic combinators
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.TacticDescriptors.AbstractTacticDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.CombinedTacticDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.ParamTacticDescriptor;
import org.eventb.internal.core.seqprover.TacticDescriptors.TacticDescriptor;
import org.eventb.internal.core.seqprover.paramTactics.ParameterDesc;

/**
 * Singeleton class implementing the auto tactic registry.
 * 
 * 
 * @see org.eventb.core.seqprover.IAutoTacticRegistry
 * 
 * 
 * @author Farhad Mehta
 */
public class AutoTacticRegistry implements IAutoTacticRegistry {
	
	private static String TACTICS_ID =
		SequentProver.PLUGIN_ID + ".autoTactics";
	private static String COMBINATORS_ID =
			SequentProver.PLUGIN_ID + ".tacticCombinators";

	private static IAutoTacticRegistry SINGLETON_INSTANCE = new AutoTacticRegistry();

	private static final String[] NO_STRING = new String[0];
	
	/**
	 * Debug flag for <code>TACTIC_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private Map<String, AbstractTacticDescriptor> registry;
	
	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private AutoTacticRegistry() {
		// Singleton implementation
	}
	
	public static IAutoTacticRegistry getTacticRegistry() {
		return SINGLETON_INSTANCE;
	}
	
	public synchronized boolean isRegistered(String id) {
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(id);
	}
	
	public synchronized String[] getRegisteredIDs(){
		if (registry == null) {
			loadRegistry();
		}
		return registry.keySet().toArray(NO_STRING);
	}
	
	@Deprecated
	public ITactic getTacticInstance(String id) throws IllegalArgumentException{
		return getTacticDescriptor(id).getTacticInstance();
	}
	
	@Deprecated
	public String getTacticName(String id) throws IllegalArgumentException{
		return getTacticDescriptor(id).getTacticName();
	}
	

	@Deprecated
	public String getTacticDescription(String id) throws IllegalArgumentException {
		return getTacticDescriptor(id).getTacticDescription();
	}

	public  synchronized AbstractTacticDescriptor getTacticDescriptor(String id) throws IllegalArgumentException{
		if (registry == null) {
			loadRegistry();
		}
		AbstractTacticDescriptor tacticDesc = registry.get(id);
		if (tacticDesc == null) {
			// Unknown tactic id, throw exception.
			throw new IllegalArgumentException("Tactic with id:" + id + " not registered.");
		}
		return tacticDesc;
	}
	
	/**
	 * Initializes the registry using extensions to the tactic extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two threads in parallel
			return;
		}
		registry = new HashMap<String, AbstractTacticDescriptor>();
		loadTacticDescriptors(TACTICS_ID);
		loadTacticDescriptors(COMBINATORS_ID);
	}

	private void loadTacticDescriptors(String extPointId) {
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(extPointId);
		for (IConfigurationElement element : xPoint.getConfigurationElements()) {
			try {
				final AbstractTacticDescriptor tacticDesc = loadTacticDescriptor(element);
				final String id = tacticDesc.getTacticID();
				if (id != null) {
					AbstractTacticDescriptor oldInfo = registry.put(id,
							tacticDesc);
					if (oldInfo != null) {
						registry.put(id, oldInfo);
						Util.log(null, "Duplicate tactic extension " + id
								+ " ignored");
					} else {
						if (DEBUG)
							System.out.println("Registered tactic extension "
									+ id);
					}
				}
			} catch (Exception e) {
				// logged before
				continue;
			}
		}
	}

	// configuration element can represent either:
	// - a simple auto tactic
	// - a parameterized auto tactic
	// - a tactic combinator
	// they share common attributes, while others are specific
	private static AbstractTacticDescriptor loadTacticDescriptor(IConfigurationElement element) {
		// common attributes
		final String id = checkAndMakeId(element);
		final String name = element.getAttribute("name");
		String description = element.getAttribute("description");
		if (description == null) description = "";
		
		// specific loading
		if (element.getDeclaringExtension().getExtensionPointUniqueIdentifier().equals(COMBINATORS_ID)) {
			return loadCombinator(element, id, name, description);
		}
		final IConfigurationElement[] children = element.getChildren("tacticParameter");
		if (children.length == 0) {
			return new TacticDescriptor(element, id, name, description);
		} else {
			final Collection<IParameterDesc> paramDescs = loadTacticParameters(children, id);
			return new ParamTacticDescriptor(element, id, name, description,
					paramDescs);
		}
	}

	private static Collection<IParameterDesc> loadTacticParameters(
			final IConfigurationElement[] children, String id) {
		final Collection<IParameterDesc> paramDescs = new ArrayList<IParameterDesc>(
				children.length);
		final Set<String> knownLabels = new HashSet<String>(children.length);
		for (IConfigurationElement paramConfig : children) {
			final IParameterDesc param = ParameterDesc.load(paramConfig);
			final String label = param.getLabel();
			final boolean newLabel = knownLabels.add(label);
			if (newLabel) {
				paramDescs.add(param);
			} else {
				final IllegalArgumentException e = new IllegalArgumentException(
						"duplicate tactic parameter label: " + label);
				Util.log(e, "while loading parameterized tactic " + id);
				throw e;
			}
		}
		return paramDescs;
	}

	private static CombinedTacticDescriptor loadCombinator(
			IConfigurationElement element, String id, String name,
			String description) {
		final String sMinArity = element.getAttribute("minArity");
		final int minArity = Integer.parseInt(sMinArity);
		if (minArity <= 0 ) {
			final IllegalArgumentException e = new IllegalArgumentException(
					"invalid arity: " + sMinArity
							+ " expected a number greater than or equal to 1");
			Util.log(e, "while loading tactic combinator " + id);
			throw e;
			
		}
		final String sBoundArity = element.getAttribute("boundArity");
		final boolean isArityBound = Boolean.parseBoolean(sBoundArity);
		return new CombinedTacticDescriptor(element, id, name, description,
				minArity, isArityBound);
	}

	private static String checkAndMakeId(IConfigurationElement element) {
		final String localId = element.getAttribute("id");
		final String id;
		if (localId.indexOf('.') != -1) {
			id = null;
			Util.log(null,
					"Invalid id: " + localId + " (must not contain a dot)");
		} else if (containsWhitespace(localId)) {
			id = null;
			Util.log(null,
					"Invalid id: " + localId + " (must not contain a whitespace)");
		} else {
			final String nameSpace = element.getNamespaceIdentifier();
			id = nameSpace + "." + localId;
		}
		return id;
	}
	
	/**
	 * Checks if a string contains a whitespace character
	 * 
	 * @param str
	 * 		String to check for.
	 * @return
	 * 		<code>true</code> iff the string contains a whitespace character.
	 */
	private static boolean containsWhitespace(String str){
		for (int i = 0; i < str.length(); i++) {
			if (Character.isWhitespace(str.charAt(i))) return true;
		}
		return false;
	}
}
