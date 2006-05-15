package org.rodinp.core.tests.builder;


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

public class SCTool implements IExtractor, IAutomaticTool {
	
	private static boolean DEBUG = false;
	
	// TODO see meaning of SC_XID.
	
	// Id of this tool
	private static String SC_ID = "org.rodinp.core.tests.testSC";
	
	// Id of the associated extractor
	private static String SC_XID = "org.rodinp.core.tests.xTestSC";
	
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException {
		ToolTrace.addTrace("SC", "clean", file);

		if (file.getFileExtension().equals("csc"))
			file.delete(true, monitor);
	}
	
	private void copyDataElements(IRodinFile ctx, ISCContext target) throws RodinDBException {
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " ...");
		IRodinElement[] datas = ctx.getChildrenOfType(IData.ELEMENT_TYPE);
		for (IRodinElement element : datas) {
			IData data = (IData) element;
			IData copy = (IData) target.createInternalElement(IData.ELEMENT_TYPE, null, null, null);
			copy.setContents(data.getContents());
		}
		if (DEBUG)
			System.out.println("Copying " + ctx.getElementName() +
					" -> " + target.getElementName() + " done.");
	}
	
	public void extract(IFile file, IGraph graph) throws CoreException {
		ToolTrace.addTrace("SC", "extract", file);
		
		IContext ctx = (IContext) RodinCore.create(file);
		
		ISCContext sctx = ctx.getCheckedVersion();
		IPath scPath = sctx.getResource().getFullPath();
		graph.addNode(scPath, SC_ID);
		graph.putToolDependency(ctx.getResource().getFullPath(), scPath, SC_ID, true);
		
		HashSet<IPath> newSources = new HashSet<IPath>(ctx.getUsedContexts().length * 4 / 3 + 1);
		for (IContext usedContext: ctx.getUsedContexts()) {
			IPath source = usedContext.getCheckedVersion().getResource().getFullPath();
			newSources.add(source);
		}
		for (IPath path : newSources)
			graph.putUserDependency(ctx.getResource().getFullPath(), path, scPath, SC_XID, false);
		
		graph.updateGraph();
	}
	
	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException {
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
