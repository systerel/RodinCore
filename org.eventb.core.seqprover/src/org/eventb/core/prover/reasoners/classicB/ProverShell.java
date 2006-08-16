/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.prover.reasoners.classicB;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.prover.Util;
import org.osgi.framework.Bundle;

public abstract class ProverShell {
	
	private static final String BUNDLE_NAME = SequentProver.PLUGIN_ID;
	
	private static Bundle bundle;
	
	private static boolean toolsPresent;
	private static boolean cached;
	private static String krtPath;
	private static String pkPath;
	private static String MLKinPath;
	private static String PPKinPath;
	private static String MLSTPath;
	private static String PPSTPath;
	
	private static Bundle getBundle() {
		if (bundle == null) {
			bundle = Platform.getBundle(BUNDLE_NAME);
		}
		return bundle;
	}
	
	/*
	 * Returns a resolved local path for a file distributed as part of this
	 * plugin or a fragment of it.
	 */
	private static String getLocalPath(IPath relativePath) {
		URL url = Platform.find(getBundle(), relativePath);
		if (url == null) {
			// Not found.
			if (ClassicB.DEBUG)
				System.out.println("Can't find relative path '" + relativePath
						+ "' in plugin");
			return null;
		}
		try {
			url = Platform.asLocalURL(url);
		} catch (IOException e1) {
			if (ClassicB.DEBUG)
				System.out.println("I/O exception while resolving URL '" + url
						+ "'");
			return null;
		}
		IPath path = new Path(url.getFile());
		return path.toOSString();
	}
	
	/*
	 * Returns a resolved local path for an OS-dependent tool distributed as part
	 * of this plugin or a fragment of it.
	 */
	private static String getToolPath(String fileName) {
		IPath relativePath = new Path(
				"os/" + 
				Platform.getOS() + "/" +
				Platform.getOSArch() + "/" +
				fileName
		);
		String pathString = getLocalPath(relativePath);
		if (ClassicB.DEBUG)
			System.out.println("Path of tool " + fileName + " is '" +
					pathString + "'");
		return pathString;
	}

	/*
	 * Returns a resolved local path for an OS-dependent tool distributed as part
	 * of this plugin or a fragment of it.
	 */
	private static String getExecutablePath(String fileName) {
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			fileName = fileName + ".exe";
		}
		String pathString = getToolPath(fileName);
		makeExecutable(pathString);
		return pathString;
	}

	/**
	 * Makes the given file executable.
	 * 
	 * @param pathString
	 *     path to the file as a String.
	 */
	private static void makeExecutable(String pathString) {
		if (Platform.getOS().equals(Platform.OS_LINUX)) {
			try {
				Process process = Runtime.getRuntime().exec("chmod +x " + pathString);
				process.waitFor();
			} catch (Exception e) {
				Util.log(e, "when changing file permission");
			}
		}
	}
	
	/*
	 * Returns a resolved local path for a symbol table distributed as part of
	 * this plugin or a fragment of it.
	 */
	private static String getSymPath(String fileName) {
		IPath relativePath = new Path(
				"sym/" + 
				fileName
		);
		final String pathString = getLocalPath(relativePath);
		if (ClassicB.DEBUG)
			System.out.println("Path of symbol table " + fileName + " is '" +
					pathString + "'");
		return pathString;
	}
	
	private static void computeCache() {
		if (cached) return;
		if (ClassicB.DEBUG) {
			System.out.println("Computing tool path cache");
		}
		krtPath = getExecutablePath("krt");
		pkPath = getExecutablePath("pk");
		MLKinPath = getToolPath("ML.kin");
		PPKinPath = getToolPath("PP.kin");
		MLSTPath = getSymPath("ML_ST");
		PPSTPath = getSymPath("PP_ST");
		toolsPresent =
			krtPath != null &&
			pkPath != null &&
			MLKinPath != null &&
			PPKinPath != null &&
			MLSTPath != null &&
			PPSTPath != null;
		cached = true;
	}
	
	public static String[] getMLParserCommand(File input) {
		computeCache();
		if (! toolsPresent) return null;
		return new String[] { 
			pkPath,
			"-3",
			"-s",
			MLSTPath,
			input.getAbsolutePath(),
		};
	}

	public static String[] getPPParserCommand(File input) {
		computeCache();
		if (! toolsPresent) return null;
		return new String[] { 
			pkPath,
			"-3",
			"-s",
			PPSTPath,
			input.getAbsolutePath(),
		};
	}

	public static String[] getMLCommand(File input) {
		computeCache();
		if (! toolsPresent) return null;
		return new String[] { 
			krtPath,
			"-a",
			"m1500000",
			"-p",
			"rmcomm",
			"-b",
			MLKinPath,
			input.getAbsolutePath(),
		};
	}

	public static String[] getPPCommand(File input) {
		computeCache();
		if (! toolsPresent) return null;
		return new String[] { 
			krtPath,
			"-p",
			"rmcomm",
			"-b",
			PPKinPath,
			input.getAbsolutePath(),
		};
	}

	public static boolean areToolsPresent() {
		computeCache();
		return toolsPresent;
	}
	
}
