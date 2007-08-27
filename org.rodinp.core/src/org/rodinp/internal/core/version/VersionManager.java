/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * @author Stefan Hallerstede
 *
 */
public class VersionManager {
	
	public static boolean VERBOSE = false;
	public static boolean DEBUG = false;
	
	public static class VersionDesc {
		
		protected final IFileElementType<IRodinFile> type;
		protected final long version;
		private String[] names;
		public VersionDesc(long version, IFileElementType<IRodinFile> type, String name) {
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
	
	Map<IFileElementType<IRodinFile>, Converter> converters;
	
	private VersionManager() {
		// only one instance; lazily initialised
		
		// is overridden in the Unit tests to inject fake configurations
	}
	
	public static VersionManager getInstance() {
		return MANAGER;
	}
	
	public Converter getConverter(IFileElementType<? extends IRodinFile> type) {
		if (converters == null) {
			computeConverters();
		}
		return converters.get(type);
	}
	
	public Map<IFileElementType<IRodinFile>, Converter> computeConverters(
			List<VersionDesc> descs, IConfigurationElement[] elements) {
		HashMap<IFileElementType<IRodinFile>, Converter> fc = 
			new HashMap<IFileElementType<IRodinFile>, Converter>(17);
		for (IConfigurationElement configElement : elements) {
			IFileElementType<IRodinFile> type = getFileElementType(configElement, "type");
			
			if (canConvert(descs, configElement.getContributor().getName(), type)) {

				Converter converter = fc.get(type);
				if (converter == null) {
					converter = new Converter();
					fc.put(type, converter);
				}

				ConversionSheet sheet = converter.addConversionSheet(configElement, type);

				if (DEBUG)
					System.out.println("LOADED " + sheet);
			} else {
				throw new IllegalStateException(
						"Conversion contributed by plugin without file element version");
			}
		}
		return fc;
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

	private IFileElementType<IRodinFile> getFileElementType(
			IConfigurationElement configElement, String id) {
		String typeString = configElement.getAttribute(id);
		IFileElementType<IRodinFile> type = RodinCore.getFileElementType(typeString);
		return type;
	}

	private boolean canConvert(List<VersionDesc> descs, String name, IFileElementType<IRodinFile> type) {
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

	public long parseVersion(IConfigurationElement confElement, IFileElementType<IRodinFile> type) {
		String versionString = confElement.getAttribute("version");
		long version;
		try {
			version = Long.parseLong(versionString);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(
					"Can't parse version number for file element type " + type, e);
		}
		if (version < 0) {
			throw new IllegalStateException(
					"Negative version number for file element type " + type);
		}
		return version;
	}

	private void addVersionDesc(List<VersionDesc> descs, IConfigurationElement element) {
		assert element.getName().equals("fileElementVersion");
		IFileElementType<IRodinFile> type = getFileElementType(element, "id");
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
	
	public long getVersion(IFileElementType<? extends IRodinFile> type) {
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
			IFileElementType<? extends IRodinFile> type) {
		for (VersionDesc desc : descs)
			if (desc.type == type)
				return desc;
		return null;
	}

}
