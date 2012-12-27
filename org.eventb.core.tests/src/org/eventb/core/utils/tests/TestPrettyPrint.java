/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.utils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.eventb.core.tests.BuilderTest.PLUGIN_ID;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the XSL pretty printer for machines and contexts.
 *
 * @author François Terrier
 *
 */
public class TestPrettyPrint {

	private static final File XSLT_FILE = getLocalFile("utils/prettyprint.xslt",
			EventBPlugin.PLUGIN_ID);
	
	private static final String TEST_DIR = "xml-tests/";
	
	private static File getLocalFile(String relative, String pluginID) {
		Path relativePath = new Path(relative);
		URL url = FileLocator.find(Platform.getBundle(pluginID), relativePath, null);
		if (url == null) {
			// Not found.
			fail();
		}
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e1) {
			fail();
		}
		Path path = new Path(url.getFile());
		return new File(path.toOSString());
	}
	
	@Test
	public void testContext() throws Exception {
		doTest("c",".buc");
	}
	
	@Test
	public void testMachine() throws Exception {
		doTest("m",".bum");
	}
	
	private void doTest(String name, String ext) throws Exception {
		final Source source = new StreamSource(XSLT_FILE);
		final Transformer transformer =
			TransformerFactory.newInstance().newTransformer(source);
		final File file = File.createTempFile("xsl", "");
		try {
			final Result result = new StreamResult(file);

			transformer.setParameter("name", name);
			transformer.transform(new StreamSource(
					getLocalFile(TEST_DIR + name + ext, PLUGIN_ID)), result);
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setIgnoringComments(true);
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document expectedDocument = builder.parse(
					getLocalFile(TEST_DIR + name + ".html", PLUGIN_ID));
			Document actualDocument = builder.parse(file);
			
			if (! actualDocument.isEqualNode(expectedDocument)) {
				assertEquals(getString(expectedDocument), getString(actualDocument));
			}
		} finally {
			file.delete();
		}		
	}
	
	private String getString(Document source) throws TransformerException {
		OutputStream expectedString = new ByteArrayOutputStream();
		Result expectedResult = new StreamResult(expectedString);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(source), expectedResult);
		String result = expectedString.toString();
		result = result.replace(" ", "");
		result = result.replace("\n", "");
		result = result.replace("\r", "");
		result = result.replace("\t", "");
		return result;
	}
	
}
