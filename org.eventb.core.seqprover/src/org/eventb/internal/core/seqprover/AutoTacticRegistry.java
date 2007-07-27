package org.eventb.internal.core.seqprover;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.IAutoTacticRegistry;
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
	
	private Map<String,TacticDescriptor> registry;
	
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

	public  synchronized TacticDescriptor getTacticDescriptor(String id) throws IllegalArgumentException{
		if (registry == null) {
			loadRegistry();
		}
		TacticDescriptor tacticDesc = registry.get(id);
		if (tacticDesc == null) {
			// Unknown tactic id, throw exception.
			throw new IllegalArgumentException("Tactic with id:" + id + "not registered.");
		}
		return tacticDesc;
	}
	
	/**
	 * Initializes the registry using extensions to the tactic extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, TacticDescriptor>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(TACTICS_ID);
		for (IConfigurationElement element: xPoint.getConfigurationElements()) {
			final TacticDescriptor tacticDesc = new TacticDescriptor(element);
			final String id = tacticDesc.getTacticID();
			if (id != null) {
				TacticDescriptor oldInfo = registry.put(id, tacticDesc);
				if (oldInfo != null) {
					registry.put(id, oldInfo);
					Util.log(null,
							"Duplicate tactic extension " + id + " ignored"
					);
				} else {
					if (DEBUG) System.out.println(
							"Registered tactic extension " + id);
				}
			}
		}
	}
	
	/**
	 * Private helper class implementing lazy loading of tactic instances
	 */
	private static class TacticDescriptor implements ITacticDescriptor{

		private final IConfigurationElement configurationElement;
		private final String id;
		private final String name;
		
		/**
		 * Tactic instance and description lazily loaded using <code>configurationElement</code>
		 */
		private ITactic instance;
		private String description;
		
		protected TacticDescriptor(IConfigurationElement element) {
			this.configurationElement = element;
			final String localId = element.getAttribute("id");
			if (localId.indexOf('.') != -1) {
				this.id = null;
				Util.log(null,
						"Invalid id: " + localId + " (must not contain a dot)");
			} else if (containsWhitespace(localId)) {
				this.id = null;
				Util.log(null,
						"Invalid id: " + localId + " (must not contain a whitespace)");
			} else {
				final String nameSpace = element.getNamespaceIdentifier();
				this.id = nameSpace + "." + localId;
			}
			this.name = element.getAttribute("name");
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
		
		public synchronized ITactic getTacticInstance() throws IllegalArgumentException{
			if (instance != null) {
				return instance;
			}

			if (configurationElement == null) {
				throw new IllegalArgumentException("Null configuration element");
			}
			
			// Try creating an instance of the specified class
			try {
				instance = (ITactic) 
					configurationElement.createExecutableExtension("class");
			} catch (Exception e) {
				final String className = 
					configurationElement.getAttribute("class");
				final String errorMsg = "Error instantiating class " + className +
										" for tactic " + id;
				Util.log(e,
						errorMsg);
				if (DEBUG) System.out.println(
						errorMsg);
				throw new IllegalArgumentException(errorMsg,e);
			}

			if (DEBUG) System.out.println(
					"Successfully loaded tactic " + id);
			return instance;
		}
		
		public synchronized String getTacticDescription() throws IllegalArgumentException{
			if (description != null) {
				return description;
			}

			if (configurationElement == null) {
				throw new IllegalArgumentException("Null configuration element");
			}
			
			description = configurationElement.getAttribute("description");
			if (description == null) description = "";
			return description;
			
		}
		
		public String getTacticID() {
			return id;
		}
		
		public String getTacticName() {
			return name;
		}

	}

}
