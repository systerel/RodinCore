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
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticParameterizer;
import org.eventb.core.seqprover.SequentProver;

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
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(TACTICS_ID);
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

	private static AbstractTacticDescriptor loadTacticDescriptor(IConfigurationElement element) {
		final String id = checkAndMakeId(element);
		final String name = element.getAttribute("name");
		String description = element.getAttribute("description");
		if (description == null) description = "";
		final IConfigurationElement[] children = element.getChildren("tacticParameter");
		if (children.length == 0) {
			return new TacticDescriptor(element, id, name, description);
		} else {
			final Collection<IParameterDesc> paramDescs = loadTacticParameters(children);
			return new ParamTacticDescriptor(element, id, name, description,
					paramDescs);
		}
	}

	private static Collection<IParameterDesc> loadTacticParameters(
			final IConfigurationElement[] children) {
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
				throw new IllegalArgumentException(
						"duplicate tactic parameter label: " + label);
			}
		}
		return paramDescs;
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

	/**
	 * Private helper class implementing lazy loading of tactic instances
	 */
	private abstract static class AbstractTacticDescriptor implements ITacticDescriptor {

		protected final IConfigurationElement configurationElement;
		protected final String id;
		private final String name;
		private final String description;
		
		public AbstractTacticDescriptor(IConfigurationElement element, String id, String name, String description) {
			this.configurationElement = element;
			this.id = id;
			this.name = name;
			this.description = description;
		}

		public synchronized String getTacticDescription() throws IllegalArgumentException{
			return description;
		}
		
		public String getTacticID() {
			return id;
		}
		
		public String getTacticName() {
			return name;
		}

		protected Object loadInstance() {
			if (configurationElement == null) {
				throw new IllegalArgumentException("Null configuration element");
			}

			// Try creating an instance of the specified class
			try {
				final Object loadedInstance = configurationElement.createExecutableExtension("class");
				if (!checkInstance(loadedInstance)) {
					throw new IllegalArgumentException("unexpected instance");
				}
				if (DEBUG) System.out.println(
					"Successfully loaded tactic " + id);

				return loadedInstance;
			} catch (Exception e) {
				final String className = 
					configurationElement.getAttribute("class");
				final String errorMsg = "Error instantiating class " + className +
										" for tactic " + id;
				Util.log(e, errorMsg);
				if (DEBUG)
					System.out.println(errorMsg);
				throw new IllegalArgumentException(errorMsg,e);
			}

		}

		protected abstract boolean checkInstance(Object instance);

	}

	private static class TacticDescriptor extends AbstractTacticDescriptor {
		
		/**
		 * Tactic instance lazily loaded using <code>configurationElement</code>
		 */
		private ITactic instance;
		
		public TacticDescriptor(IConfigurationElement element, String id,
				String name, String description) {
			super(element, id, name, description);
		}

		public synchronized ITactic getTacticInstance() {
			if (instance != null) {
				return instance;
			}
			instance = (ITactic) loadInstance();
			return instance;
		}

		@Override
		protected boolean checkInstance(Object instance) {
			return instance instanceof ITactic;
		}
		
	}
	
	private static class ParamTacticDescriptor extends AbstractTacticDescriptor
			implements IParamTacticDescriptor {

		/**
		 * Tactic instance lazily loaded using <code>configurationElement</code>
		 */
		private ITacticParameterizer parameterizer;
		private final Collection<IParameterDesc> parameterDescs;

		public ParamTacticDescriptor(IConfigurationElement element,
				String id, String name,
				String description, Collection<IParameterDesc> parameterDescs) {
			super(element, id, name, description);
			this.parameterDescs = parameterDescs;
		}

		@Override
		public ITactic getTacticInstance() throws IllegalArgumentException {
			return getTacticInstance(makeParameterSetting());
		}

		@Override
		public Collection<IParameterDesc> getParameterDescs() {
			return Collections.unmodifiableCollection(parameterDescs);
		}

		@Override
		public IParameterSetting makeParameterSetting() {
			return new ParameterSetting(parameterDescs);
		}

		@Override
		public ITactic getTacticInstance(IParameterValuation valuation) {
			if (parameterizer == null) {
				parameterizer = (ITacticParameterizer) loadInstance();
			}
			// FIXME can return null
			return parameterizer.getTactic(valuation);
		}

		@Override
		protected boolean checkInstance(Object instance) {
			return instance instanceof ITacticParameterizer;
		}

	}
	
	private static abstract class ParameterValue<T> {
		private T value;
		
		public ParameterValue(IParameterDesc desc) {
			setValue(desc.getDefaultValue());
		}

		protected abstract T asValue(Object o);
		
		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = asValue(value);
		}
		
	}
	
	private static class BoolParameterValue extends ParameterValue<Boolean> {

		public BoolParameterValue(IParameterDesc desc) {
			super(desc);
		}

		@Override
		protected Boolean asValue(Object o) {
			return (Boolean) o;
		}
		
	}
	
	private static class IntParameterValue extends ParameterValue<Integer> {

		public IntParameterValue(IParameterDesc desc) {
			super(desc);
		}

		@Override
		protected Integer asValue(Object o) {
			return (Integer) o;
		}
	}
	private static class LongParameterValue extends ParameterValue<Long> {

		public LongParameterValue(IParameterDesc desc) {
			super(desc);
		}

		@Override
		protected Long asValue(Object o) {
			return (Long) o;
		}
	}
	
	private static class StringParameterValue extends ParameterValue<String> {

		public StringParameterValue(IParameterDesc desc) {
			super(desc);
		}

		@Override
		protected String asValue(Object o) {
			return (String) o;
		}
	}
	
	private static class ParameterSetting implements IParameterSetting {

		private final Map<String, ParameterValue<?>> valuation = new LinkedHashMap<String, ParameterValue<?>>();

		public ParameterSetting(Collection<IParameterDesc> paramDescs) {
			ParameterValue<?> value = null;
			for (IParameterDesc desc : paramDescs) {
				switch (desc.getType()) {
				case BOOL:
					value = new BoolParameterValue(desc);
					break;
				case INT:
					value = new IntParameterValue(desc);
					break;
				case LONG:
					value = new LongParameterValue(desc);
					break;
				case STRING:
					value = new StringParameterValue(desc);
					break;
				default:
					assert false;
				}
				valuation.put(desc.getLabel(), value);
			}
		}

		private ParameterValue<?> checkAndGet(String label, ParameterType expectedType) {
			final ParameterValue<?> paramValue = valuation.get(label);
			if (paramValue == null) {
				throw new IllegalArgumentException("unknown label "+label);
			}
			if (!expectedType.check(paramValue.getValue())) {
				throw new IllegalArgumentException("parameter " + label
						+ " does not have type " + expectedType);
			}
			return paramValue;
		}

		private void checkAndSet(String label, ParameterType expectedType, Object value) {
			final ParameterValue<?> paramValue = checkAndGet(label, expectedType);
			paramValue.setValue(value);
		}

		@Override
		public void setBoolean(String label, Boolean value) {
			checkAndSet(label, ParameterType.BOOL, value);
		}

		@Override
		public void setInt(String label, Integer value) {
			checkAndSet(label, ParameterType.INT, value);
		}

		@Override
		public void setLong(String label, Long value) {
			checkAndSet(label, ParameterType.LONG, value);
		}

		@Override
		public void setString(String label, String value) {
			checkAndSet(label, ParameterType.STRING, value);
		}

		@Override
		public boolean getBoolean(String label) {
			final ParameterValue<?> paramValue = checkAndGet(label,
					ParameterType.BOOL);
			return (Boolean) paramValue.getValue();
		}

		@Override
		public int getInt(String label) {
			final ParameterValue<?> paramValue = checkAndGet(label,
					ParameterType.INT);
			return (Integer) paramValue.getValue();
		}

		@Override
		public long getLong(String label) {
			final ParameterValue<?> paramValue = checkAndGet(label,
					ParameterType.LONG);
			return (Long) paramValue.getValue();
		}

		@Override
		public String getString(String label) {
			final ParameterValue<?> paramValue = checkAndGet(label,
					ParameterType.STRING);
			return (String) paramValue.getValue();
		}
		
	}
	
	private static class ParameterDesc implements IParameterDesc {

		private final String label;
		private final ParameterType type;
		private final Object defaultValue;
		private final String description;

		private ParameterDesc(String label, ParameterType type,
				Object defaultValue, String description) {
			this.label = label;
			this.type = type;
			this.defaultValue = defaultValue;
			this.description = description;
		}

		public static IParameterDesc load(IConfigurationElement element) {
			final String label = element.getAttribute("label");
			final String sType = element.getAttribute("type");
			final ParameterType type = getType(sType);
			final String sDefault = element.getAttribute("default");
			final Object defaultValue = type.parse(sDefault);
			String description = element.getAttribute("description");
			if (description == null) description = "";
			return new ParameterDesc(label, type, defaultValue, description);
		}

		private static ParameterType getType(String typeName) {
			if (typeName.equals("Boolean")) {
				return ParameterType.BOOL;
			}
			if (typeName.equals("Integer")) {
				return ParameterType.INT;
			}
			if (typeName.equals("Long")) {
				return ParameterType.LONG;
			}
			if (typeName.equals("String")) {
				return ParameterType.STRING;
			}
			throw new IllegalArgumentException(
					"invalid tactic parameter type name: " + typeName);
		}
		
		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public ParameterType getType() {
			return type;
		}

		@Override
		public Object getDefaultValue() {
			return defaultValue;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
	}
}
