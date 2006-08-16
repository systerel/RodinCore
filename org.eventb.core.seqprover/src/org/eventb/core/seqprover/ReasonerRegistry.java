package org.eventb.core.seqprover;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.core.prover.Util;

public class ReasonerRegistry {
	
	private static String REASONER_EXTENTION_POINT_ID =
		SequentProver.PLUGIN_ID + ".reasoners";

	private static ReasonerRegistry SINGLETON_INSTANCE = new ReasonerRegistry();
	
	public static ReasonerRegistry getReasonerRegistry() {
		return SINGLETON_INSTANCE;
	}
	
	private Map<String,IReasoner> registry;
	
	private ReasonerRegistry() {
		// Singleton implementation
	}
	
	private void loadRegistry() {
		registry = new HashMap<String,IReasoner>();		
		
		IExtensionPoint extensionPoint = 
			Platform.getExtensionRegistry().
			getExtensionPoint(REASONER_EXTENTION_POINT_ID);
		
		for (IConfigurationElement element:
				extensionPoint.getConfigurationElements()) {

			try {
				String reasonerID = element.getNamespace()+"."+element.getAttributeAsIs("id");
				IReasoner reasoner = 
					(IReasoner) element.createExecutableExtension("class");
				
				if (! reasonerID.equals(reasoner.getReasonerID())) {
					Util.log(
							new IllegalArgumentException("Mismatch between reasoner ids " + reasonerID + ", " + reasoner.getReasonerID()),
							"Reasoner id in extention point differs from that returned by the reasoner");
				}
				
				registry.put(reasoner.getReasonerID(),reasoner);
				System.out.println("Added reasoner:"+reasoner.getReasonerID());
			} catch (Exception e) {
				// TODO Exception handle
				e.printStackTrace();
			}
		}
	}

	public IReasoner getReasoner(String reasonerID){
		if (registry == null) {
			loadRegistry();
		}
		return registry.get(reasonerID);
	}
	
	
	public Set<String> installedReasoners(){
		if (registry == null) {
			loadRegistry();
		}
		return registry.keySet();
	}
}
