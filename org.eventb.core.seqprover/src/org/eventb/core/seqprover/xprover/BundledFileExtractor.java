/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * Utility class for extracting a file bundled within a plug-in.
 * <p>
 * This class allows clients to easily extract files bundled within their
 * plug-ins.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class BundledFileExtractor {

	private BundledFileExtractor() {
		// Disabled constructor
	}
	
	private static class BundledFileDescriptor {
		final Bundle bundle;
		final IPath path;
		final boolean executable;
		
		// Cached hash code value
		final int hashCode;
		
		private static int computeHashCode(Bundle bundle, IPath path) {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + bundle.hashCode();
			result = PRIME * result + path.hashCode();
			return result;
		}
		
		public BundledFileDescriptor(Bundle bundle, IPath path, boolean exec) {
			this.bundle = bundle;
			this.path = path;
			this.executable = exec;
			this.hashCode = computeHashCode(bundle, path);
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final BundledFileDescriptor other = (BundledFileDescriptor) obj;
			if (!bundle.equals(other.bundle))
				return false;
			if (!path.equals(other.path))
				return false;
			return true;
		}
		
		public IPath extract() {
			URL url = FileLocator.find(bundle, path, null);
			if (url == null) {
				log("File " + path + " not found in plug-in "
						+ bundle.getSymbolicName(), null);
				return null;
			}
			try {
				url = FileLocator.toFileURL(url);
			} catch (IOException e) {
				log("Problem extracting file " + path + " from plug-in "
						+ bundle.getSymbolicName(), e);
				return null;
			}
			final IPath result = new Path(url.getPath());
			if (executable && !isWin32()) {
				makeExecutable(result);
			}
			return result;
		}
		
		/**
		 * Makes the given local file executable.
		 * 
		 * @param path
		 *     path to the file
		 */
		private void makeExecutable(IPath path) {
			try {
				final IFileSystem fs = EFS.getLocalFileSystem();
				final IFileStore store = fs.getStore(path);
				final IFileInfo info = store.fetchInfo();
				info.setAttribute(EFS.ATTRIBUTE_EXECUTABLE, true);
				store.putInfo(info, EFS.SET_ATTRIBUTES, null);
			} catch (Exception e) {
				log("Problem making file " + path + " executable", e);
			}
		}

		private void log(String message, Throwable exc) {
			final IStatus status= new Status(
					IStatus.ERROR, 
					bundle.getSymbolicName(), 
					0, 
					message, 
					exc); 
			Platform.getLog(bundle).log(status);
		}

	}
	
	private static HashMap<BundledFileDescriptor, IPath> descriptors = 
		new HashMap<BundledFileDescriptor, IPath>(); 
	
	/**
	 * Returns the path to the file with the given path relative to the given
	 * bundle, after it has been extracted from the bundle. Returns
	 * <code>null</code> and logs details of the error if the file cannot be
	 * extracted.
	 * <p>
	 * In addition, if the <code>executable</code> attribute is set, an
	 * attempt is made to set the executable attribute of the extracted file on
	 * Unic machines. Conversely, on Windows machine, a ".exe" file extension is
	 * appended to the given path.
	 * </p>
	 * 
	 * @param bundle
	 *            bundle from which to extract the file (this can be obtained
	 *            from a plug-in identifier using
	 *            <code>Platform.getBundle(PLUGIN_ID)</code>)
	 * @param path
	 *            path of the file to extract, relative to the root of the given
	 *            bundle
	 * @param exec
	 *            <code>true</code> iff the file should have is its executable
	 *            attribute set
	 * @return the path to the extracted file or <code>null</code>
	 */
	public static synchronized IPath extractFile(Bundle bundle, IPath path,
			boolean exec) {
		
		if (exec && isWin32()) {
			path = path.addFileExtension("exe");
		}
		
		final BundledFileDescriptor desc = new BundledFileDescriptor(bundle,
				path, exec);
		IPath result = descriptors.get(desc);
		if (result == null) {
			result = desc.extract();
			descriptors.put(desc, result);
		}
		return result;
	}

	private static boolean isWin32() {
		return Platform.getOS().equals(Platform.OS_WIN32);
	}

}
