package org.eventb.core.prover;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.prover.reasoners.AllD;
import org.eventb.core.prover.reasoners.AllI;
import org.eventb.core.prover.reasoners.ConjE;
import org.eventb.core.prover.reasoners.ConjI;
import org.eventb.core.prover.reasoners.Contr;
import org.eventb.core.prover.reasoners.Contradiction;
import org.eventb.core.prover.reasoners.Cut;
import org.eventb.core.prover.reasoners.DisjE;
import org.eventb.core.prover.reasoners.DoCase;
import org.eventb.core.prover.reasoners.Eq;
import org.eventb.core.prover.reasoners.ExE;
import org.eventb.core.prover.reasoners.ExI;
import org.eventb.core.prover.reasoners.ExternalML;
import org.eventb.core.prover.reasoners.ExternalPP;
import org.eventb.core.prover.reasoners.Hyp;
import org.eventb.core.prover.reasoners.ImpE;
import org.eventb.core.prover.reasoners.ImpI;
import org.eventb.core.prover.reasoners.MngHyp;
import org.eventb.core.prover.reasoners.Review;
import org.eventb.core.prover.reasoners.RewriteGoal;
import org.eventb.core.prover.reasoners.RewriteHyp;
import org.eventb.core.prover.reasoners.Tautology;
import org.osgi.framework.Bundle;

public class ReasonerRegistry {
	
	private static Map<String,Reasoner> registry = new HashMap<String,Reasoner>();
	
	// Static initialization block for registry 
	static {
		Reasoner[] installedReasoners =	
		{
				// Add new reasoners here.
				new Hyp(),
				new Tautology(),
				new Contradiction(),
				new ConjI(),
				new Cut(),
				new DoCase(),
				new Contr(),
				new ConjE(),
				new DisjE(),
				new ImpI(),
				new ImpE(),
				new AllI(),
				new AllD(),
				new ExE(),
				new ExI(),
				new RewriteGoal(),
				new Eq(),
				new RewriteHyp(),
				new ExternalPP(),
				new ExternalML(),
				new Review(),
				new MngHyp()
		};
		
		for (Reasoner reasoner : installedReasoners)
		{
			// no duplicate ids
			assert ! registry.containsKey(reasoner.getReasonerID());
			registry.put(reasoner.getReasonerID(),reasoner);
			// System.out.println("Registered "+reasoner.getReasonerID()+" as "+reasoner);
		}
		
		
		String REASONER_EXTENTION_POINT_ID = SequentProver.PLUGIN_ID+ ".reasoner";
		
		IExtension[] extensions = 
			Platform.getExtensionRegistry().
			getExtensionPoint(REASONER_EXTENTION_POINT_ID).
			getExtensions();
		
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
			.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				String name = element.getName();
				
				if (name.equals("reasoner")) {
					Bundle bundle = Platform.getBundle(element
							.getNamespace());
					try {
						String reasonerID = element.getAttribute("reasonerID");
						Reasoner reasoner = loadReasoner(
								bundle,
								element.getAttribute("class"));
						
						assert reasonerID.equals(reasoner.getReasonerID());
						
						registry.put(reasonerID,reasoner);
						System.out.println("Added reasoner:"+reasoner.getReasonerID());
					} catch (Exception e) {
						// TODO Exception handle
						e.printStackTrace();
					}
				}
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	private static Reasoner loadReasoner(Bundle bundle, String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {	
		Class classObject = bundle.loadClass(className).asSubclass(Reasoner.class);
		Constructor constructor = classObject.getConstructor(new Class[0]);
		Reasoner reasoner = (Reasoner) constructor.newInstance(new Object[0]);
		return reasoner;
	}
	
	public static Reasoner getReasoner(String reasonerID){
		return registry.get(reasonerID);
	}
}
