package org.eventb.internal.core.seqprover;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;

/**
 * Singeleton class implementing the reasoner registry.
 * 
 * @see org.eventb.core.seqprover.IReasonerRegistry
 * 
 * TODO : Generate and log proper exceptions
 * 
 * @author Farhad Mehta
 */
public class ReasonerRegistry implements IReasonerRegistry {
	
	private static String REASONER_EXTENTION_POINT_ID =
		SequentProver.PLUGIN_ID + ".reasoners";

	private static IReasonerRegistry SINGLETON_INSTANCE = new ReasonerRegistry();

	/**
	 * Debug flag for <code>REASONER_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private Map<String,ReasonerExtentionInfo> registry;
	
	/**
	 * Private default constructor enforces that only one instance of this class
	 * is present.
	 */
	private ReasonerRegistry() {
		// Singleton implementation
	}
	
	public static IReasonerRegistry getReasonerRegistry() {
		return SINGLETON_INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerRegistry#isInstalled(java.lang.String)
	 */
	public boolean isPresent(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(reasonerID);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerRegistry#getInstalledReasoners()
	 */
	public Set<String> getReasonerIDs(){
		if (registry == null) {
			loadRegistry();
		}
		return Collections.unmodifiableSet(registry.keySet());
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerRegistry#getReasonerInstance(java.lang.String)
	 */
	public IReasoner getReasonerInstance(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		final ReasonerExtentionInfo reasonerExtentionInfo = registry.get(reasonerID);
		if (reasonerExtentionInfo == null) return null;
		return reasonerExtentionInfo.getReasonerInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerRegistry#getReasonerName(java.lang.String)
	 */
	public String getReasonerName(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		final ReasonerExtentionInfo reasonerExtentionInfo = registry.get(reasonerID);
		if (reasonerExtentionInfo == null) return null;
		return reasonerExtentionInfo.getReasonerName();
	}
	
	
	/**
	 * Initializes the registry using extentions to the reasoner extention point.
	 * 
	 */
	private void loadRegistry() {
		registry = new HashMap<String,ReasonerExtentionInfo>();		
		
		IExtensionPoint extensionPoint = 
			Platform.getExtensionRegistry().
			getExtensionPoint(REASONER_EXTENTION_POINT_ID);
		
		for (IConfigurationElement element:
				extensionPoint.getConfigurationElements()) {
				String reasonerID = element.getNamespace()+"."+element.getAttributeAsIs("id");
				
				if (registry.containsKey(reasonerID)){
					Util.log(
							new IllegalArgumentException("Reasoner registry already contains an extention with the id "+reasonerID),
							"Duplicate reasoner extention with the id "+ reasonerID +" ignored.");
					if (DEBUG) System.out.println("Duplicate reasoner extention "+ reasonerID + " ignored");
				}
				else{
					registry.put(reasonerID,new ReasonerExtentionInfo(element,reasonerID));
					if (DEBUG) System.out.println("Registered reasoner extention :"+reasonerID);
				}
		}
	}
	
	/**
	 * 
	 * Private helper class implementing lazy loading of reasoner instances
	 * 
	 * @author Farhad Mehta
	 *
	 */
	private class ReasonerExtentionInfo{

		private final IConfigurationElement configurationElement;
		private final String reasonerID;
		private final String reasonerName;
		
		/**
		 * Reasoner instances lazily loaded using <code>configurationElement</code>
		 */
		private IReasoner reasonerInstance;
		
		private ReasonerExtentionInfo(IConfigurationElement configurationElement) {
			this(configurationElement,
					configurationElement.getNamespace()+"."+
					configurationElement.getAttributeAsIs("id")
			);
		}
		
		private ReasonerExtentionInfo(IConfigurationElement configurationElement, String reasonerID) {
			this.configurationElement = configurationElement;
			this.reasonerID = reasonerID;
			this.reasonerName = configurationElement.getAttribute("name");
		}
		
		private IReasoner getReasonerInstance(){
			if (reasonerInstance != null) return reasonerInstance;
			
			Object instance = null;
			
			// Try creating an instance of the specified class
			try {
				instance = configurationElement.createExecutableExtension("class");
			} catch (CoreException e) {
				Util.log(
						e,
						"Class specified in the reasoner extention point for " + reasonerID + " cannot be loaded ");
				return null;
			}
			
			// Try casting the instance to IReasoner
			if (! (instance instanceof IReasoner)){	
				Util.log(
						new ClassCastException("Cannot cast " + instance + " to " + IReasoner.class ),
						"Class specified in the reasoner extention point for " + reasonerID + " does not implement" + IReasoner.class);
				return null;
			}
		
			reasonerInstance = (IReasoner) instance;
			
			// Check if the reasoner id from the extention point matches that 
			// returned by the class instance. 
			if (! reasonerID.equals(reasonerInstance.getReasonerID())) {
				Util.log(
						new IllegalArgumentException("Mismatch between reasoner ids " + reasonerID + ", " + reasonerInstance.getReasonerID()),
				"Reasoner id in extention point differs from that returned by the reasoner");
				return null;
			}

			if (DEBUG) System.out.println("Loaded reasoner instance:"+reasonerInstance);
			return reasonerInstance;
		}

		public String getReasonerName() {
			return reasonerName;
		}
	}
}
