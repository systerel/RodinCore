package org.eventb.internal.core.seqprover;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;

/**
 * Singeleton class implementing the reasoner registry.
 * 
 * @see org.eventb.core.seqprover.IReasonerRegistry
 * 
 * @author Farhad Mehta
 */
public class ReasonerRegistry implements IReasonerRegistry {
	
	private static String REASONERS_ID =
		SequentProver.PLUGIN_ID + ".reasoners";

	private static IReasonerRegistry SINGLETON_INSTANCE = new ReasonerRegistry();

	private static final String[] NO_STRING = new String[0];
	
	/**
	 * Debug flag for <code>REASONER_REGISTRY_TRACE</code>
	 */
	public static boolean DEBUG;
	
	private Map<String,ReasonerInfo> registry;
	
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
	
	public IReasoner getReasonerInstance(String id){
		return getInfo(id).getReasonerInstance();
	}
	
	public String getReasonerName(String id){
		return getInfo(id).getReasonerName();
	}

	private synchronized ReasonerInfo getInfo(String id) {
		if (registry == null) {
			loadRegistry();
		}
		ReasonerInfo info = registry.get(id);
		if (info == null) {
			// Unknown reasoner, just create a dummy entry
			info = new ReasonerInfo(id);
			registry.put(id, info);
		}
		return info;
	}
	
	/**
	 * Initializes the registry using extensions to the reasoner extension point.
	 */
	private synchronized void loadRegistry() {
		if (registry != null) {
			// Prevents loading by two thread in parallel
			return;
		}
		registry = new HashMap<String, ReasonerInfo>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry.getExtensionPoint(REASONERS_ID);
		for (IConfigurationElement element: xPoint.getConfigurationElements()) {
			final ReasonerInfo info = new ReasonerInfo(element);
			final String id = info.getReasonerID();
			ReasonerInfo oldInfo = registry.put(id, info);
			if (oldInfo != null) {
				registry.put(id, oldInfo);
				Util.log(null,
						"Duplicate reasoner extension " + id + " ignored"
				);
				if (DEBUG) System.out.println(
						"Duplicate reasoner extension "+ id + " ignored");
			} else {
				if (DEBUG) System.out.println(
						"Registered reasoner extension " + id);
			}
		}
	}
	
	public boolean isDummyReasoner(IReasoner reasoner){
		return reasoner instanceof DummyReasoner;
	}
	
	/**
	 * Private helper class implementing lazy loading of reasoner instances
	 */
	private static class ReasonerInfo{

		private final IConfigurationElement configurationElement;
		private final String id;
		private final String name;
		
		/**
		 * Reasoner instance lazily loaded using <code>configurationElement</code>
		 */
		private IReasoner instance;
		
		public ReasonerInfo(IConfigurationElement element) {
			this.configurationElement = element;
			final String bundleName = element.getNamespace();
			final String localId = element.getAttributeAsIs("id");
			this.id = bundleName + "." + localId;
			this.name = element.getAttribute("name");
		}
		
		public ReasonerInfo(String id) {
			this.configurationElement = null;
			this.id = id;
			// TODO externalize name of dummy reasoner which is user-visible
			this.name = "Unknown reasoner " + id;
		}
		
		public synchronized IReasoner getReasonerInstance(){
			if (instance != null) {
				return instance;
			}

			if (configurationElement == null) {
				return instance = getDummyInstance(id);
			}
			
			// Try creating an instance of the specified class
			try {
				instance = (IReasoner) 
					configurationElement.createExecutableExtension("class");
			} catch (Exception e) {
				final String className = 
					configurationElement.getAttributeAsIs("class");
				Util.log(e,
						"Error instantiating class " + className +
						" for reasoner " + id);
				if (DEBUG) System.out.println(
						"Create a dummy instance for reasoner " + id);
				return instance = getDummyInstance(id);
			}
			
			// Check if the reasoner id from the extension point matches that 
			// returned by the class instance. 
			if (! id.equals(instance.getReasonerID())) {
				Util.log(null,
						"Reasoner instance says its id is " +
						instance.getReasonerID() +
						" while it was registered with id " +
						id);
				if (DEBUG) System.out.println(
						"Created a dummy instance for reasoner " + id);
				return instance = getDummyInstance(id);
			}

			if (DEBUG) System.out.println(
					"Successfully loaded reasoner " + id);
			return instance;
		}

		private static IReasoner getDummyInstance(String id) {
			return new DummyReasoner(id);
		}
		
		public String getReasonerID() {
			return id;
		}
		
		public String getReasonerName() {
			return name;
		}

	}
	
	/**
	 * 
	 * Protected helper class implementing dummy reasoners.
	 * <p>
	 * Used as a placeholder when we can't create the regular instance of a
	 * reasoner (wrong class, unknown id, ...).
	 * </p>
	 */
	protected static class DummyReasoner implements IReasoner{

		private final String reasonerID;
		
		private DummyReasoner(String reasonerID){
			this.reasonerID = reasonerID;
		}
		
		public String getReasonerID() {
			return reasonerID;
		}

		public IReasonerInput deserializeInput(
				IReasonerInputSerializer reasonerInputSerializer)
				throws SerializeException {

			return new EmptyInput();
		}

		public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
				IProgressMonitor progressMonitor) {
			
			return ProverFactory.reasonerFailure(
					this,
					input,
					"Reasoner " + reasonerID + " is not installed");
		}

	}

}
