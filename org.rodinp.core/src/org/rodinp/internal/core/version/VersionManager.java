/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added support for testing failing converters
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.util.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public class VersionManager {
	
	public static boolean VERBOSE = false;
	public static boolean DEBUG = false;
	
	public static class VersionDesc {
		
		@Override
		public String toString() {
			return type.getId() + ":" + version;
		}
		
		protected final IInternalElementType<?> type;
		protected final long version;
		private String[] names;
		public VersionDesc(long version, IInternalElementType<?> type, String name) {
			this.version = version;
			this.type = type;
			names = new String[] { name };
		}
		void addName(String name) {
			String[] ns = new String[names.length+1];
			System.arraycopy(names, 0, ns, 0, names.length);
			ns[names.length] = name;
			names = ns;
		}
		boolean containsName(String name) {
			for (String n : names) {
				if (n.equals(name))
					return true;
			}
			return false;
		}
		void checkIfContributorDeclares() {
			
			String contributor = type.getId();
			contributor = contributor.substring(0, contributor.lastIndexOf('.'));
			
			for (String name : names) {
				if (name.equals(contributor))
					return;
			}
			throw new IllegalStateException("Contributor of file must declare a version if a client does");
		}
	}
	
	private List<VersionDesc> versionDescs;
	
	private static String CONVERSIONS_ID = "conversions";
	
	private static String FILE_ELEMENT_VERSIONS_ID = "fileElementVersions";
	
	private static VersionManager MANAGER = new VersionManager();
	
	Map<IInternalElementType<?>, Converter> converters;
	
	private final List<IConfigurationElement> invalidConverters = new ArrayList<IConfigurationElement>();
	
	private VersionManager() {
		// only one instance; lazily initialised
		
		// is overridden in the Unit tests to inject fake configurations
	}
	
	public static VersionManager getInstance() {
		return MANAGER;
	}
	
	public Converter getConverter(IInternalElementType<?> type) {
		if (converters == null) {
			computeConverters();
		}
		return converters.get(type);
	}
	
	public Map<IInternalElementType<?>, Converter> computeConverters(
			List<VersionDesc> descs, IConfigurationElement[] elements) {
		HashMap<IInternalElementType<?>, Converter> fc = 
			new HashMap<IInternalElementType<?>, Converter>(17);
		for (IConfigurationElement configElement : elements) {
			try {
				computeConverter(configElement, descs, fc);
			} catch (Throwable e) {
				Util.log(e, "When computing a version converter");
				invalidConverters.add(configElement);
			}
		}
		
		addMissingConverters(fc, descs);
		
		return fc;
	}

	private void computeConverter(IConfigurationElement configElement,
			List<VersionDesc> descs,
			HashMap<IInternalElementType<?>, Converter> fc) throws Exception {
		IInternalElementType<?> type = getRootElementType(configElement, "type");
		if (canConvert(descs, configElement.getContributor().getName(), type)) {

			Converter converter = fc.get(type);
			if (converter == null) {
				converter = new Converter();
				fc.put(type, converter);
			}

			ConversionSheet sheet = converter.addConversionSheet(configElement,
					type);

			if (DEBUG)
				System.out.println("LOADED " + sheet);
		} else {
			throw new IllegalStateException(
					"Conversion contributed by plugin without file element version");
		}
	}

	private void addMissingConverters(
			HashMap<IInternalElementType<?>, Converter> fc,
			List<VersionDesc> descs) {
		// TODO this could be added to computeConverters()
		// (but perhaps it would be nice to generate warnings)
		for (VersionDesc desc : descs) {
			if (fc.containsKey(desc.type))
				continue;
			else {
				fc.put(desc.type, new Converter());
			}
		}
	}

	private void computeConverters() {
		
		if (versionDescs == null) {
			computeVersionDescs();
		}
		
		if (converters == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = 
				registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, CONVERSIONS_ID);
			
			converters = computeConverters(versionDescs, elements);
						
			if (VERBOSE) {
				// TODO add debugging output
			}
			
		}
	}

	private IInternalElementType<?> getRootElementType(
			IConfigurationElement configElement, String id) {
		String typeString = configElement.getAttribute(id);
		return RodinCore.getInternalElementType(typeString);
	}

	private boolean canConvert(List<VersionDesc> descs, String name, IInternalElementType<?> type) {
		if (versionDescs == null) {
			computeVersionDescs();
		}
		VersionDesc desc = findVersionDesc(descs, type);
		if (desc == null)
			return false;
		else
			return desc.containsName(name);
	}
	
	public List<VersionDesc> computeVersionDescs(IConfigurationElement[] elements) {
		ArrayList<VersionDesc> descs = new ArrayList<VersionDesc>();
		
		for (IConfigurationElement confElement : elements) {
			addVersionDesc(descs, confElement);
		}
		descs.trimToSize();
		
		for (VersionDesc desc : descs)
			desc.checkIfContributorDeclares();
		
		return descs;
	}

	private void computeVersionDescs() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = 
			registry.getConfigurationElementsFor(RodinCore.PLUGIN_ID, FILE_ELEMENT_VERSIONS_ID);
		versionDescs = computeVersionDescs(elements);
	}

	public long parseVersion(IConfigurationElement confElement, IInternalElementType<?> type) {
		String versionString = confElement.getAttribute("version");
		long version;
		try {
			version = Long.parseLong(versionString);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(
					"Can't parse version number for root element type " + type, e);
		}
		if (version < 0) {
			throw new IllegalStateException(
					"Negative version number for root element type " + type);
		}
		return version;
	}

	private void addVersionDesc(List<VersionDesc> descs, IConfigurationElement element) {
		assert element.getName().equals("fileElementVersion");
		IInternalElementType<?> type = getRootElementType(element, "id");
		long version = parseVersion(element, type);
		VersionDesc desc = findVersionDesc(descs, type);
		String name = element.getContributor().getName();
		if (desc == null) {
			desc = new VersionDesc(version, type, name);
			descs.add(desc);
		} else {
			if (version != desc.version) {
				throw new IllegalStateException("Versions do not agree for " + type);
			}
			if (desc.containsName(name))
				return;
			desc.addName(name);
		}
	}
	
	public long getVersion(IElementType<?> type) {
		if (versionDescs == null) {
			computeVersionDescs();
		}
		VersionDesc desc = findVersionDesc(versionDescs, type);
		if (desc == null) {
			return 0;
		} else {
			return desc.version;
		}
	}

	private VersionDesc findVersionDesc(
			List<VersionDesc> descs, 
			IElementType<?> type) {
		for (VersionDesc desc : descs)
			if (desc.type == type)
				return desc;
		return null;
	}

	// For testing purposes only
	public List<IConfigurationElement> getInvalidConverters() {
		return invalidConverters;
	}

	// For testing purposes only
	public void clearInvalidConverters() {
		invalidConverters.clear();
	}
}
