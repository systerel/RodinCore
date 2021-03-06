/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added formula extensions
 *******************************************************************************/
package org.eventb.internal.core.sc;


import static org.eventb.internal.core.Util.addExtensionDependencies;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.sc.StaticChecker;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ContextStaticChecker extends StaticChecker {

	@Override
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor)
			throws CoreException {

		try {

			monitor.beginTask(Messages.bind(Messages.build_extracting, file
					.getName()), 1);

			IRodinFile source = RodinCore.valueOf(file);
			IContextRoot root = (IContextRoot) source.getRoot();
			final ISCContextRoot targetRoot = root.getSCContextRoot();
			IRodinFile target = targetRoot.getRodinFile();

			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(),
					true);

			addExtensionDependencies(graph, targetRoot);
			
			IExtendsContext[] extendsContexts = root.getExtendsClauses();
			for (IExtendsContext extendsContext : extendsContexts) {
				if (extendsContext.hasAbstractContextName()) {
					IRodinFile abstractSCContext = extendsContext
							.getAbstractSCContext().getRodinFile();
					graph.addUserDependency(
							source.getResource(), 
							abstractSCContext.getResource(), 
							target.getResource(), false);
				}
			}

		} finally {
			monitor.done();
		}

	}

}
