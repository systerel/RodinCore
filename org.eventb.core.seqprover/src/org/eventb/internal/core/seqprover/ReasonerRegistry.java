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

public class ReasonerRegistry implements IReasonerRegistry {
	
	private static String REASONER_EXTENTION_POINT_ID =
		SequentProver.PLUGIN_ID + ".reasoners";

	private static IReasonerRegistry SINGLETON_INSTANCE = new ReasonerRegistry();
	
	public static IReasonerRegistry getReasonerRegistry() {
		return SINGLETON_INSTANCE;
	}
	
	private Map<String,ReasonerExtentionInfo> registry;
	
	
	private ReasonerRegistry() {
		// Singleton implementation
	}
	
	private void loadRegistry() {
		registry = new HashMap<String,ReasonerExtentionInfo>();		
		
		IExtensionPoint extensionPoint = 
			Platform.getExtensionRegistry().
			getExtensionPoint(REASONER_EXTENTION_POINT_ID);
		
		for (IConfigurationElement element:
				extensionPoint.getConfigurationElements()) {
				String reasonerID = element.getNamespace()+"."+element.getAttributeAsIs("id");
				
				registry.put(reasonerID,new ReasonerExtentionInfo(element,reasonerID));
				System.out.println("Added reasoner:"+reasonerID);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerRegistry#getReasonerInstance(java.lang.String)
	 */
	public IReasoner getReasonerInstance(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		return registry.get(reasonerID).getReasonerInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerRegistry#getReasonerName(java.lang.String)
	 */
	public String getReasonerName(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		return registry.get(reasonerID).getReasonerName();
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
	 * @see org.eventb.core.seqprover.IReasonerRegistry#isInstalled(java.lang.String)
	 */
	public boolean isPresent(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		return registry.containsKey(reasonerID);
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
		
		private IReasoner reasonerInstance;
		
		private ReasonerExtentionInfo(IConfigurationElement configurationElement) {
			this.configurationElement = configurationElement;
			this.reasonerID = configurationElement.getNamespace()+"."+configurationElement.getAttributeAsIs("id");
			this.reasonerName = configurationElement.getAttribute("name");
		}
		
		private ReasonerExtentionInfo(IConfigurationElement configurationElement, String reasonerID) {
			this.configurationElement = configurationElement;
			this.reasonerID = reasonerID;
			this.reasonerName = configurationElement.getAttribute("name");
		}
		
		private IReasoner getReasonerInstance(){
			if (reasonerInstance != null) return reasonerInstance;
			
			Object instance = null;
			
			try {
				instance = configurationElement.createExecutableExtension("class");
			} catch (CoreException e) {
				Util.log(
						e,
						"Class specified in the reasoner extention point for " + reasonerID + " cannot be loaded ");
			}
			if (! (instance instanceof IReasoner))
				Util.log(
						new ClassCastException("Cannot cast " + instance + " to " + IReasoner.class ),
						"Class specified in the reasoner extention point for " + reasonerID + " does not implement" + IReasoner.class);
			reasonerInstance = (IReasoner) instance;
			
			
			if (! reasonerID.equals(reasonerInstance.getReasonerID())) {
				Util.log(
						new IllegalArgumentException("Mismatch between reasoner ids " + reasonerID + ", " + reasonerInstance.getReasonerID()),
				"Reasoner id in extention point differs from that returned by the reasoner");
			}
			System.out.println("Loaded reasoner:"+reasonerInstance);
			return reasonerInstance;
		}

		public String getReasonerName() {
			return reasonerName;
		}
	}
}
