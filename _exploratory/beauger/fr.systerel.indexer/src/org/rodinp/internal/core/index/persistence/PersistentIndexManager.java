/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.persistence;

import java.util.Collection;

import org.rodinp.internal.core.index.IIndexDelta;
import org.rodinp.internal.core.index.PerProjectPIM;

/**
 * @author Nicolas Beauger
 *
 */
public class PersistentIndexManager {

    private final PerProjectPIM pppim;
    private final Collection<IIndexDelta> deltas;
    public PersistentIndexManager(PerProjectPIM pppim, Collection<IIndexDelta> deltas) {
	this.pppim = pppim;
	this.deltas = deltas;
    }
    
    public PerProjectPIM getPPPIM() {
        return pppim;
    }
    public Collection<IIndexDelta> getDeltas() {
        return deltas;
    }
    
    
}
