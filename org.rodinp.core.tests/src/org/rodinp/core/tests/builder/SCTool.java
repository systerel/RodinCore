package org.rodinp.core.tests.builder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;
import org.rodinp.core.builder.IInterrupt;

public class SCTool implements IExtractor, IAutomaticTool {
	
	// TODO see meaning of SC_XID.
	
	// Id of this tool
	private static String SC_ID = "org.rodinp.core.tests.testSC";
	
	// Id of the associated extractor
	private static String SC_XID = "org.rodinp.core.tests.xTestSC";
	
	public void clean(IFile file, IInterrupt progress, IProgressMonitor monitor) throws CoreException {
		ToolTrace.addTrace("SC", "clean", file);

		if (file.getFileExtension().equals("csc"))
			file.delete(true, monitor);
	}
	
	private void copyDataElements(IRodinFile ctx, ISCContext target) throws RodinDBException {
		System.out.println("Copying " + ctx.getElementName() + " -> " + target.getElementName() + " ...");
		IRodinElement[] datas = ctx.getChildrenOfType(IData.ELEMENT_TYPE);
		for (IRodinElement element : datas) {
			IData data = (IData) element;
			IData copy = (IData) target.createInternalElement(IData.ELEMENT_TYPE, null, null, null);
			copy.setContents(data.getContents());
		}
		System.out.println("Copying " + ctx.getElementName() + " -> " + target.getElementName() + " done.");
	}
	
	public void extract(IFile file, IGraph graph) throws CoreException {
		ToolTrace.addTrace("SC", "extract", file);
		
		IContext ctx = (IContext) RodinCore.create(file);
		
		ISCContext sctx = ctx.getCheckedVersion();
		IPath scPath = sctx.getResource().getFullPath();
		if (! graph.containsNode(scPath)) {
			graph.addNode(scPath, SC_ID);
			graph.addToolDependency(ctx.getResource().getFullPath(), scPath, SC_ID, true);
		}
		
		HashSet<IPath> oldSources = new HashSet<IPath>(Arrays.asList(graph.getDependencies(scPath, SC_XID)));
		HashSet<IPath> newSources = new HashSet<IPath>(oldSources.size() * 4 / 3 + 1);
//		graph.removeDependencies(scPath, SC_XID);
		for (IContext usedContext: ctx.getUsedContexts()) {
			IPath source = usedContext.getCheckedVersion().getResource().getFullPath();
			oldSources.remove(source);
			newSources.add(source);
		}
		if(!oldSources.isEmpty())
			graph.removeDependencies(scPath, SC_XID);
		for (IPath path : newSources)
			graph.addUserDependency(ctx.getResource().getFullPath(), path, scPath, SC_XID, false);;
	}
	
	public boolean run(IFile file, IInterrupt progress, IProgressMonitor monitor) throws CoreException {
		ToolTrace.addTrace("SC", "run", file);

		ISCContext target = (ISCContext) RodinCore.create(file);
		IContext ctx = target.getUncheckedVersion(); 
		
		// First clean up target
		if (target.exists()) {
			target.delete(true, null);
		}
		target = (ISCContext) target.getRodinProject().createRodinFile(target.getElementName(), true, null);
		
		// Populate with a copy of inputs
		copyDataElements(ctx, target);
		for (IContext usedContext: ctx.getUsedContexts()) {
			copyDataElements(usedContext.getCheckedVersion(), target);
		}
		
		target.save(null, true);
		return true;
	}
	
}
