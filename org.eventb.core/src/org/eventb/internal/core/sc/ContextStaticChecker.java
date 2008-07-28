/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.StaticChecker;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ContextStaticChecker extends StaticChecker {

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor)
			throws CoreException {

		try {

			monitor.beginTask(Messages.bind(Messages.build_extracting, file
					.getName()), 1);

			IContextFile source = (IContextFile) RodinCore.valueOf(file);
			ISCContextFile target = source.getSCContextFile();

			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(),
					true);

			IExtendsContext[] extendsContexts = source.getExtendsClauses();
			for (IExtendsContext extendsContext : extendsContexts) {
				if (extendsContext.hasAbstractContextName()) {
					ISCContextFile abstractSCContext = extendsContext.getAbstractSCContext();
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
