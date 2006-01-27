package org.rodinp.core.tests.builder;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
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
	
	private void copyDataElements(IContext ctx, ISCContext target) throws RodinDBException {
		IRodinElement[] datas = ctx.getChildrenOfType(IData.ELEMENT_TYPE);
		for (IRodinElement element : datas) {
			IData data = (IData) element;
			IData copy = (IData) target.createInternalElement(IData.ELEMENT_TYPE, null, null, null);
			copy.setContents(data.getContents());
		}
	}
	
	public void extract(IFile file, IGraph graph) throws CoreException {
		ToolTrace.addTrace("SC", "extract", file);
		
		IContext ctx = (IContext) RodinCore.create(file);
		
		ISCContext sctx = ctx.getCheckedVersion();
		IPath scPath = sctx.getPath();
		if (! graph.containsNode(scPath)) {
			graph.addNode(scPath, SC_ID);
			graph.addToolDependency(ctx.getPath(), scPath, SC_ID, true);
		}
		
		graph.removeDependencies(scPath, SC_XID);
		for (IContext usedContext: ctx.getUsedContexts()) {
			graph.addUserDependency(ctx.getPath(), usedContext.getPath(), scPath, SC_XID, false);
		}
	}
	
	public boolean run(IFile file, IInterrupt progress, IProgressMonitor monitor) throws CoreException {
		ToolTrace.addTrace("SC", "run", file);

		ISCContext target = (ISCContext) RodinCore.create(file);
		IContext ctx = target.getUncheckedVersion(); 
		
		// First clean up target
		if (target.exists()) {
			target.delete(true, null);
		}
		target.getRodinProject().createRodinFile(target.getElementName(), true, null);
		
		// Populate with a copy of inputs
		copyDataElements(ctx, target);
		for (IContext usedContext: ctx.getUsedContexts()) {
			copyDataElements(usedContext, target);
		}
		
		target.save(null, true);
		return true;
	}
	
}
