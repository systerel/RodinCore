/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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
package org.rodinp.core.tests.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Test;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.version.conf.AddAttribute;
import org.rodinp.core.tests.version.conf.Conversion;
import org.rodinp.core.tests.version.conf.Element;
import org.rodinp.core.tests.version.conf.FileElementVersion;
import org.rodinp.core.tests.version.conf.RenameAttribute;
import org.rodinp.core.tests.version.conf.RenameElement;
import org.rodinp.core.tests.version.conf.SimpleConversion;
import org.rodinp.internal.core.version.ConversionSheet;
import org.rodinp.internal.core.version.Converter;
import org.rodinp.internal.core.version.VersionManager;
import org.rodinp.internal.core.version.VersionManager.VersionDesc;

/**
 * @author Stefan Hallerstede
 *
 */
public class FaultyVersionTest extends ModifyingResourceTests {
	
	private static final String FILEA_TYPE = PLUGIN_ID + ".versionFileA";
	private static final String FILEB_TYPE = PLUGIN_ID + ".versionFileB";
	private static final String ELEMX_TYPE = PLUGIN_ID + ".versionEX";
	private static final String ELEMA_TYPE = PLUGIN_ID + ".versionEA";
	private static final String ATTRX_TYPE = PLUGIN_ID + ".versionXA";
	private static final String ATTRS_TYPE = PLUGIN_ID + ".versionStrAttr";
	private static final String OTHER_PLUGIN_ID = "org.other.plugin";

	private List<String> composeAttrs(String attr, String[] p) {
		List<String> r = new ArrayList<String>(p.length * p.length - 1);
		for (String x : p)
			for (String y : p) {
				if (x.equals("") && y.equals(""))
					continue;
				r.add(x + attr + y);
			}
		return r;
	}

	private static IConfigurationElement[] makeFileElementVersions(FileElementVersion... fileVersions) {
		return fileVersions;
	}
	
	private static IConfigurationElement[] makeConversions(Conversion... conversions) {
		return conversions;
	}
	
	private void correctFileVersions(IConfigurationElement[] elements) {
		try {
			VersionManager vm = VersionManager.getInstance();
			vm.computeVersionDescs(elements);
		} catch (IllegalStateException e) {
			fail("Correct configuration rejected");
		}
	}
	
	private static void faultyFileVersions(IConfigurationElement[] elements) {
		try {
			VersionManager vm = VersionManager.getInstance();
			vm.computeVersionDescs(elements);
		} catch (IllegalStateException e) {
			return;
		}
		fail("Faulty configuration undetected");
	}
	
	private static void faultyConversions(IConfigurationElement[] vElements,
			IConfigurationElement[] cElements,
			IConfigurationElement... expectedFailures) {
		VersionManager vm = VersionManager.getInstance();
		vm.clearInvalidConverters();
		List<VersionDesc> descs = vm.computeVersionDescs(vElements);
		Map<IInternalElementType<?>, Converter> cmap = vm.computeConverters(descs, cElements);
		final List<IConfigurationElement> invalidConverters = vm.getInvalidConverters();
		assertEquals(Arrays.asList(expectedFailures), invalidConverters);
		try {
			for (Converter converter : cmap.values()) {
				for (ConversionSheet sheet : converter.getConversionSheets()) {
					sheet.getTransformer();
				}
			}
		} catch (RodinDBException e) {
			fail("Faulty configuration undetected");
		}
	}

	/**
	 * File versions must be unique
	 */
	@Test
	public void testConv_00_uniqueFileVersion() throws Exception {
		
		IConfigurationElement[] elements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1"),
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "2")
			);
		
		faultyFileVersions(elements);
	}
	
	/**
	 * if any client declares a version for some file, 
	 * then the contributor of the file must declare one too
	 */
	@Test
	public void testConv_01_clientOnlyFileVersionForbidden() throws Exception {
		
		IConfigurationElement[] elements =
			makeFileElementVersions(
					new FileElementVersion("org.client", FILEA_TYPE, "1")
			);
		
		faultyFileVersions(elements);
	}	
	
	/**
	 * if any client and contributor declare a file version, then they must coincide
	 */
	@Test
	public void testConv_02_clientAndContributorFileVersion() throws Exception {
		
		IConfigurationElement[] elements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1"),
					new FileElementVersion("org.client", FILEA_TYPE, "2")
			);
		
		faultyFileVersions(elements);
	}
	
	/**
	 * if any client declares a version for some file, then contributor must declare one too
	 */
	@Test
	public void testConv_03_correctFileVersion() throws Exception {
		
		IConfigurationElement[] elements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		correctFileVersions(elements);
	}	

	/**
	 * if any client and contributor declare a file version, then they must coincide
	 */
	@Test
	public void testConv_04_uniqueConversion() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		SimpleConversion failingConversion = new SimpleConversion(PLUGIN_ID,
				FILEA_TYPE, "1");
		IConfigurationElement[] cElements =
			makeConversions(
					new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1"),
					failingConversion
			);
		
		faultyConversions(vElements, cElements, failingConversion);
	}

	/**
	 * paths in in each conversion must be unique
	 */
	@Test
	public void testConv_05_uniqueElementPath() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		IConfigurationElement[] cElements =
			makeConversions(
					new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1",
							new Element("/" + FILEA_TYPE),
							new Element("/" + FILEA_TYPE)
					)
			);
		
		faultyConversions(vElements, cElements, cElements);
	}

	/**
	 * paths must be absolute and not end with a "/"
	 */
	@Test
	public void testConv_06_absoluteElementPath() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] paths = new String[] {
				"//" + FILEA_TYPE, 
				"/" + FILEA_TYPE + "/", 
				FILEA_TYPE,
				"/" + FILEA_TYPE + "//" + ELEMA_TYPE,
				"/" + FILEA_TYPE + "/" + ELEMA_TYPE + "/",
				FILEA_TYPE + "/" + ELEMA_TYPE};
		
		for (String path : paths) {
			IConfigurationElement[] cElements =
				makeConversions(
						new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1",
								new Element(path)
						)
				);
			
			faultyConversions(vElements, cElements, cElements);
		}
	}
	
	/**
	 * paths must not refer to attributes
	 */
	@Test
	public void testConv_07_noAttributePath() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] paths = new String[] {
				"/" + FILEA_TYPE + "/@" + ELEMA_TYPE,
				"/" + FILEA_TYPE + "/" + ELEMA_TYPE + "@" + ELEMA_TYPE,
				"/" + FILEA_TYPE + "/" + ELEMA_TYPE + "/@",
				FILEA_TYPE + "/@" + ELEMA_TYPE};
		
		for (String path : paths) {
			IConfigurationElement[] cElements =
				makeConversions(
						new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1",
								new Element(path)
						)
				);
			
			faultyConversions(vElements, cElements, cElements);
		}
	}
	
	/**
	 * element ids must not contain "/" or "@" in rename element ops
	 */
	@Test
	public void testConv_08_bareRenameElementIds() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] elema = new String[] {
				"/" + ELEMA_TYPE,
				ELEMA_TYPE + "/",
				"/" + ELEMA_TYPE + "/",
				"//" + ELEMA_TYPE
		};
		
		for (String elem : elema) {
			IConfigurationElement[] cElements =
				makeConversions(
						new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1",
								new Element(
										"/" + FILEA_TYPE + "/" + ELEMX_TYPE, 
										new RenameElement(elem))
						)
				);
			
			faultyConversions(vElements, cElements, cElements);
		}
	}

	/**
	 * attribute ids must not contain "/" or "@" in rename attribute ops
	 */
	@Test
	public void testConv_09_bareRenameAttributeIds() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] p = new String[] { "", "/", "//", "@", "/@" };
		
		List<String> os = composeAttrs(ATTRX_TYPE, p);
		List<String> ns = composeAttrs(ATTRS_TYPE, p);
		
		for (String o : os)
			for (String n : ns) {
				IConfigurationElement[] cElements = 
					makeConversions(new SimpleConversion(
						PLUGIN_ID, FILEA_TYPE, "1", 
						new Element("/" + FILEA_TYPE, 
								new RenameAttribute(o, n))));

				faultyConversions(vElements, cElements, cElements);
			}
	}

	/**
	 * attribute ids must not contain "/" or "@" in add attribute ops
	 */
	@Test
	public void testConv_10_bareAddAttributeIds() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] p = new String[] { "", "/", "//", "@", "/@" };
		
		List<String> ns = composeAttrs(ATTRS_TYPE, p);
		
		for (String n : ns) {
			IConfigurationElement[] cElements =
				makeConversions(
						new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1",
								new Element(
										"/" + FILEA_TYPE, 
										new AddAttribute(n, "x"))
						)
				);
			
			faultyConversions(vElements, cElements, cElements);
		}
	}
	
	/**
	 * an element rename op must not be attached to a path only
	 * consisting of the file element type id
	 */
	@Test
	public void testConv_11_mustNotRenameFileElementTypeId() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		IConfigurationElement[] cElements =
			makeConversions(
					new SimpleConversion(PLUGIN_ID, FILEA_TYPE, "1",
							new Element(
									"/" + FILEA_TYPE, 
									new RenameElement(FILEB_TYPE))
						)
				);
			
		faultyConversions(vElements, cElements, cElements);
	}
	
	/**
	 * an element type can be renamed by the plugin that is contributing 
	 * that element type. Both the old id and the new id must belong to
	 * the same plugin.
	 */
	@Test
	public void testConv_12_onlyContributorCanRenameElement() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] els = new String[] { ELEMX_TYPE, OTHER_PLUGIN_ID + ".versionEX" };
		String[] fls = new String[] { ELEMA_TYPE, OTHER_PLUGIN_ID + ".versionEA" };
		
		for (int i=0; i<1; i++)
			for (int j=0; j<1; j++)
				if (i != 1 && j != 1) {
					IConfigurationElement[] cElements =
						makeConversions(
								new SimpleConversion(OTHER_PLUGIN_ID, FILEA_TYPE, "1",
										new Element(
												"/" + FILEA_TYPE + "/" + els[i], 
												new RenameElement(fls[j]))
								)
						);
			
					faultyConversions(vElements, cElements, cElements);
				}
	}
	
	/**
	 * an attribute type can be renamed by the plugin that is contributing 
	 * that attribute type. Both the old id and the new id must belong to
	 * the same plugin.
	 */
	@Test
	public void testConv_13_onlyContributorCanRenameAttribute() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		String[] als = new String[] { ATTRX_TYPE, OTHER_PLUGIN_ID + ".versionXA" };
		String[] bls = new String[] { ATTRS_TYPE, OTHER_PLUGIN_ID + ".versionStrAttr" };
		
		for (int i=0; i<1; i++)
			for (int j=0; j<1; j++)
				if (i != 1 && j != 1) {
					IConfigurationElement[] cElements =
						makeConversions(
								new SimpleConversion(OTHER_PLUGIN_ID, FILEA_TYPE, "1",
										new Element(
												"/" + FILEA_TYPE + "/" + ELEMA_TYPE, 
												new RenameAttribute(als[i], bls[j]))
								)
						);
			
					faultyConversions(vElements, cElements, cElements);
				}
	}
	
	/**
	 * an attribute type can be added by the plugin that is contributing 
	 * that attribute type
	 */
	@Test
	public void testConv_14_onlyContributorCanAddAttribute() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		IConfigurationElement[] cElements =
			makeConversions(
					new SimpleConversion(OTHER_PLUGIN_ID, FILEA_TYPE, "1",
							new Element(
									"/" + FILEA_TYPE + "/" + ELEMA_TYPE, 
									new AddAttribute(ATTRS_TYPE, "x"))
						)
				);
			
		faultyConversions(vElements, cElements, cElements);
	}
	
	
	/**
	 * conversion is only possible when a file version number has been contributed;
	 * this holds for contributing and non-contributing plugins
	 */
	@Test
	public void testConv_15_canConvertOnlyWithDeclaredVersionNumber() throws Exception {
		
		IConfigurationElement[] vElements =
			makeFileElementVersions(
					new FileElementVersion(PLUGIN_ID, FILEA_TYPE, "1"),
					new FileElementVersion(OTHER_PLUGIN_ID, FILEA_TYPE, "1")
			);
		
		IConfigurationElement[] cElements =
			makeConversions(new SimpleConversion(PLUGIN_ID, FILEB_TYPE, "1"));
			
		faultyConversions(vElements, cElements, cElements);
		
		cElements =
			makeConversions(new SimpleConversion(OTHER_PLUGIN_ID, FILEB_TYPE, "1"));
			
		faultyConversions(vElements, cElements, cElements);
	}
	

}
