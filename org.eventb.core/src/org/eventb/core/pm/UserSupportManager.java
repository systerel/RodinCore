/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;

public class UserSupportManager {

	private static Collection<IUSManagerListener> listeners = new HashSet<IUSManagerListener>();

	private static Collection<UserSupport> userSupports = new HashSet<UserSupport>();

	public static UserSupport newUserSupport() {
		UserSupport userSupport = new UserSupport();
		userSupports.add(userSupport);
		notifyUSManagerListener(userSupport, true);
		return userSupport;
	}

	public static void disposeUserSupport(UserSupport userSupport) {
		userSupports.remove(userSupport);
		notifyUSManagerListener(userSupport, false);
	}

	public static Collection<UserSupport> getUserSupports() {
		return userSupports;
	}

	public static void addUSManagerListener(IUSManagerListener listener) {
		listeners.add(listener);
	}

	public static void removeUSManagerListener(IUSManagerListener listener) {
		listeners.remove(listener);
	}

	private static void notifyUSManagerListener(UserSupport userSupport,
			boolean added) {
		for (IUSManagerListener listener : listeners) {
			listener.USManagerChanged(userSupport, added);
		}
	}

}
