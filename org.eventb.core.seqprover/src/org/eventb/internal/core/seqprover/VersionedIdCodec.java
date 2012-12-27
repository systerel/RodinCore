/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static org.eventb.core.seqprover.IReasonerDesc.NO_VERSION;

import org.eventb.core.seqprover.IReasonerDesc;

/**
 * Utility class that manages the encoding and decoding of a reasoner id with
 * the version of the reasoner.
 * 
 * @author "Nicolas Beauger"
 * 
 */
public class VersionedIdCodec {

	private static final String VERSION_SEPARATOR = ":";

	/**
	 * Returns an id encoding both given baseId and version. Encoded id and
	 * version can subsequently be retrieved from the returned String through
	 * {@link #decodeId(String)} and {@link #decodeVersion(String)} methods.
	 * 
	 * @param baseId
	 * @param version
	 * @return
	 */
	public static String encodeVersionInId(String baseId, int version) {
		if (version == NO_VERSION) {
			return baseId;
		}
		return baseId + VERSION_SEPARATOR + version;
	}

	/**
	 * Returns the bare id (with no version encoded) from the given id, which
	 * potentially encodes a version.
	 * 
	 * @param id
	 *            an id with potentially a version encoded in it
	 * @return the bare id (with no version encoded in it)
	 */
	public static String decodeId(String versionedId) {
		final String[] split = versionedId.split(VERSION_SEPARATOR, 2);
		return split[0];

	}

	/**
	 * Returns the version encoded in the given id. If no version is encoded in
	 * the given id, or the encoded version is a negative number, or there is a
	 * problem fetching the encoded version, then
	 * {@value IReasonerDesc#NO_VERSION} is returned.
	 * 
	 * @param versionedID
	 * @return a non negative integer, or {@value IReasonerDesc#NO_VERSION}
	 */
	public static int decodeVersion(String versionedId) {
		final String[] split = versionedId.split(VERSION_SEPARATOR, 2);
		if (split.length < 2) {
			return NO_VERSION;
		}
		final String versionString = split[1];
		try {
			final int parsed = Integer.parseInt(versionString);
			return filterNegative(parsed);
		} catch (NumberFormatException e) {
			Util.log(e, "while getting reasoner version from " + versionedId);
			return NO_VERSION;
		}
	}

	private static int filterNegative(int version) {
		if (version < 0) {
			return NO_VERSION;
		}
		return version;
	}

}
