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
package org.eventb.internal.core.tool.types;

import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IStateRepository;


/**
 * The <i>core tools</i>, static checker (<code>org.eventb.core.sc</code>) and 
 * proof obligation generator (<code>org.eventb.core.pog</code>), are composed 
 * of modules (<code>IModule</code>). Two kinds of module exist:
 * <ul>
 * <li>filter modules (<code>IFilterModule</code>)</li>
 * <li>processor modules (<code>IProcessorModule</code>)</li>
 * </ul>
 * Modules pass on information to other modules by means of a state repository 
 * ({@link IStateRepository}) using a simple access protocol.
 * <p>
 * All modules have the two methods:
 * <ul>
 * <li><code>initModule()</code>: initialisation of the module. The initialisation
 * usually requests state ({@link IState}) from the state repository 
 * ({@link IStateRepository}). This is done unconditionally, so that missing states 
 * are discovered quickly, and testing for problems with the state repository is
 * easier.
 * <li><code>endModule()</code>: termination of the module. The termination usually
 * frees resources or writes some output.</li>
 * </ul>
 * <p>
 * A filter module has an additional method
 * <ul>
 * <li><code>accept()</code> that evaluates a boolean condition.
 * </ul>
 * <p>
 * A processor module has an additional method
 * <ul>
 * <li><code>process()</code> that processes a list of elements
 * </ul>
 * <p>
 * Modules are arranged in a module tree. The root of the tree is a processor module.
 * A processor module may have filter and processor child modules. A filter module
 * can not have child modules.
 * When a core tool is started, a new instance of the associated module tree is
 * created and run. A module in the tree may be executed more than once during 
 * a single execution of a tool instance. The loading mechanism is hidden and can not
 * be incluenced.
 * <p>
 * Modules are called in a loop of the form
 * <pre>
 * initModule();
 * while (...) {
 * 	 accept() or process();
 * }
 * endModule();
 * </pre>
 * If a list of filter or processor modules is to be executed, then first
 * all modules are initialised, then <code>accept()</code> or <code>process()</code>
 * is called, then terminated; all in the same order. Filter modules and processor
 * modules are treated as separate lists. First all filter modules are executed,
 * then all processor modules.
 * <p>
 * This interface is not intended to be implemented by clients, only
 * <code>IFilterModule</code> and <code>IProcessorModule</code> are.
 * </p>
 * 
 * @see IFilterModule
 * @see IProcessorModule
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IModule {

	IModuleType<?> getModuleType();

}