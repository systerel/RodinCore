/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.registry.TacticApplicationProxy.TacticApplicationFactory;

/**
 * Implementation of a list of UI tactic descriptions which is used for local
 * tactics (applicable to hypotheses and goals).
 * 
 * @author Laurent Voisin
 * 
 */
public class TacticProviderInfoList implements Iterable<TacticProviderInfo> {

	private final List<TacticProviderInfo> infos;

	public TacticProviderInfoList(List<TacticProviderInfo> infos) {
		this.infos = infos;
	}

	public <T extends TacticApplicationProxy<?>> List<T> getTacticApplications(
			IUserSupport us, Predicate hyp, TacticApplicationFactory<T> factory) {
		final List<T> result = new ArrayList<T>();
		for (final TacticProviderInfo info : infos) {
			result.addAll(info.getLocalApplications(us, hyp, factory));
		}
		return result;
	}

	@Override
	public Iterator<TacticProviderInfo> iterator() {
		return infos.iterator();
	}

}
