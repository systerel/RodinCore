/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tool;


/**
 * Common protocol for modules.
 * The protocol has two variants:
 * <li>
 * <ul> The ONCE protocol. Method <code>run()</code> is called exactly once
 * as follows:
 * <p>
 * <code>
 * m.initModule(repository, monitor);
 * ...  // invocation of the body of the module
 *      // as declared in one of the interfaces
 *      // IAcceptorModule or IProcessorModule
 * m.endModule(repository, monitor);
 * </code>
 * </p>
 * </ul>
 * <ul> The LOOP protocol. Method <code>run()</code> is called in a loop traversing a list of elements
 * as follows:
 * <p>
 * <code>
 * m.initModule(repository, monitor);
 * while (more elements) {
 * ...  // invocation of the body of the module
 *      // as declared in one of the interfaces
 *      // IAcceptorModule or IProcessorModule
 * }
 * m.endModule(repository, monitor);
 * </code>
 * </p>
 * </ul>
 * </li>
 * <p>
 * It must be guaranteed by all implementors that the
 * methods are called in the specified order.
 * 
 * In a list of extensions a module may only rely on the order in which 
 * the body methods are called. Initialisations and terminations will usually
 * be invoked in batch manner before resp. after all body methods have been
 * invoked.
 * 
 * Module extensions of a module should be loaded in the constructor of the module.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IModule {

	// protocols are specified in the interfaces
	// IAcceptorModule and IProcessorModule

}