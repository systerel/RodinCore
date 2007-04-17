package org.eventb.internal.core.seqprover;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;

/**
 * Singeleton class implementing the tactic registry.
 * 
 * Implimentation identical to {@link ReasonerRegistry}.
 * 
 * @see org.eventb.core.seqprover.ITacticRegistry
 * 
 * TODO : see if a dummy tactic is really needed in this setting.
 * 
 * @author Farhad Mehta
 */
public class TacticRegistry implements ITacticRegistry {
	
	private static String TACTICS_ID =
		SequentProver.PLUGIN_ID + ".tactics";

	private static ITacticRegistry SINGLETON_INSTANCE = new TacticRegistry();

	private static final String[] NO_STRING = new String[0];
	
	/**
	 * Debug flag for <code>TACTIC_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private Map<String,TacticInfo> registry;
	
	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private TacticRegistry() {
		// Singleton implementation
	}
	
	public static ITacticRegistry getTacticRegistry() {
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
	
	public ITactic getTacticInstance(String id){
		return getInfo(id).getTacticInstance();
	}
	
	public String getTacticName(String id){
		return getInfo(id).getTacticName();
	}

	private synchronized TacticInfo getInfo(String id) {
		if (registry == null) {
			loadRegistry();
		}
		TacticInfo info = registry.get(id);
		if (info == null) {
			// Unknown tactic, just create a dummy entry
			info = new TacticInfo(id);
			registry.put(id, info);
		}
		return info;
	}
	
	/**
	 * Initializes the registry using extensions to the tactic extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, TacticInfo>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(TACTICS_ID);
		for (IConfigurationElement element: xPoint.getConfigurationElements()) {
			final TacticInfo info = new TacticInfo(element);
			final String id = info.getTacticID();
			if (id != null) {
				TacticInfo oldInfo = registry.put(id, info);
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
	
	public boolean isDummyTactic(ITactic tactic){
		return tactic instanceof DummyTactic;
	}
	
	/**
	 * Private helper class implementing lazy loading of tactic instances
	 */
	private static class TacticInfo{

		private final IConfigurationElement configurationElement;
		private final String id;
		private final String name;
		
		/**
		 * Tactic instance lazily loaded using <code>configurationElement</code>
		 */
		private ITactic instance;
		
		public TacticInfo(IConfigurationElement element) {
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
		
		public TacticInfo(String id) {
			this.configurationElement = null;
			this.id = id;
			// TODO externalize name of dummy tactic which is user-visible
			this.name = "Unknown tactic " + id;
		}
		
		public synchronized ITactic getTacticInstance(){
			if (instance != null) {
				return instance;
			}

			if (configurationElement == null) {
				return instance = getDummyInstance(id);
			}
			
			// Try creating an instance of the specified class
			try {
				instance = (ITactic) 
					configurationElement.createExecutableExtension("class");
			} catch (Exception e) {
				final String className = 
					configurationElement.getAttribute("class");
				Util.log(e,
						"Error instantiating class " + className +
						" for tactic " + id);
				if (DEBUG) System.out.println(
						"Create a dummy instance for tactic " + id);
				return instance = getDummyInstance(id);
			}
			
			// Check if the tactic id from the extension point matches that 
			// returned by the class instance. 
//			if (! id.equals(instance.getTacticID())) {
//				Util.log(null,
//						"Tactic instance says its id is " +
//						instance.getTacticID() +
//						" while it was registered with id " +
//						id);
//				if (DEBUG) System.out.println(
//						"Created a dummy instance for tactic " + id);
//				return instance = getDummyInstance(id);
//			}

			if (DEBUG) System.out.println(
					"Successfully loaded tactic " + id);
			return instance;
		}

		private static ITactic getDummyInstance(String id) {
			return new DummyTactic();
		}
		
		public String getTacticID() {
			return id;
		}
		
		public String getTacticName() {
			return name;
		}

	}
	
	/**
	 * 
	 * Protected helper class implementing dummy tactics.
	 * <p>
	 * Used as a placeholder when we can't create the regular instance of a
	 * tactic (wrong class, unknown id, ...).
	 * </p>
	 */
	protected static class DummyTactic implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return "Dummy Tactic";
		}

	}

}
