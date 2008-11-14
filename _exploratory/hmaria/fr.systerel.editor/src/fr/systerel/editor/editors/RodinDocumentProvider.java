/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.editors;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinMarkerUtil;

import fr.systerel.editor.EditorUtils;

/**
 * This is a document provider for rodin machines and contexts. It is intended
 * that for each editor there is used a new instance of this class.
 */
public class RodinDocumentProvider extends AbstractDocumentProvider {

	private IDocument doc;
	private DocumentMapper documentMapper;
	private IEventBRoot inputRoot;
	private IEditorInput editorInput;
	private RodinTextGenerator textGenerator;
	
	public RodinDocumentProvider(DocumentMapper mapper, RodinEditor editor) {
		this.documentMapper = mapper;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	@Override
	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {

		return new AnnotationModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(java.lang.Object)
	 */
	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		doc = new Document();
		if (element instanceof IEditorInput) {
			IFile file =  (IFile) ((IEditorInput) element).getAdapter(IFile.class);
			editorInput = (IEditorInput) element;
			IRodinProject project = EditorUtils.getRodinProject(file.getProject());
			inputRoot = (IEventBRoot) project.getRodinFile(file.getName()).getRoot();
			documentMapper.setRoot(inputRoot);
			
			textGenerator = new RodinTextGenerator(documentMapper);
			doc.set(textGenerator.createText(inputRoot));
			
			
		}
		RodinPartitioner partitioner = new RodinPartitioner(documentMapper, new String[]{
				RodinConfiguration.IDENTIFIER_TYPE,
				RodinConfiguration.COMMENT_TYPE,
				RodinConfiguration.LABEL_TYPE,
				RodinConfiguration.CONTENT_TYPE });
		doc.setDocumentPartitioner(partitioner);
		partitioner.connect(doc, false);
		
		return doc;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		
		documentMapper.getRoot().getRodinFile().save(null, true);
//		ArrayList<Interval> intervals = documentMapper.getIntervals();
//		for (Interval interval : intervals) {
//			if (interval.isChanged()) {
//				IRodinElement affected = interval.getElement();
//				affected.getOpenable().save(null, false);
//				
//			}
//		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#getOperationRunner(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isModifiable(Object element) {
		return false;
	}

	public void changed(Object element) {
	}
	
	
	
	
//	public void databaseChanged() {
//		documentMapper.resetIntervals();
//		doc.set(textGenerator.createText(inputRoot));
//	}
	
	protected void doSynchronize(Object element, IProgressMonitor monitor) throws CoreException {
		System.out.println("synchronizing");
		documentMapper.resetIntervals();
		doc.set(textGenerator.createText(inputRoot));
		fireElementDirtyStateChanged(element, false);
	}

	
	/*
	 * @see IDocumentProviderExtension#isReadOnly(Object)
	 * @since 2.0
	 */
	public boolean isReadOnly(Object element) {
		return false;
	}

	public Position[] getFoldingRegions() {
		return textGenerator.getFoldingRegions();
	}

	public IEventBRoot getInputRoot() {
		return inputRoot;
	}

	public MarkerAnnotationPosition[] getMarkerAnnotations() {
		IResource file =  inputRoot.getResource();
		ArrayList<MarkerAnnotationPosition> results = new ArrayList<MarkerAnnotationPosition>();
		try {
			IMarker[] markers = file.findMarkers(RodinMarkerUtil.RODIN_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				SimpleMarkerAnnotation annotation = new SimpleMarkerAnnotation(marker);
				Position position = findPosition(marker);
				if (position != null) {
					results.add(new MarkerAnnotationPosition(position, annotation));
				}
				
			}
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results.toArray(new MarkerAnnotationPosition[results.size()]);
	}

	/**
	 * Finds the position in the document for a given marker.
	 * @param marker
	 * @return the position of the element corresponding to the marker inside the document.
	 */
	private Position findPosition(IMarker marker) {
		IRodinElement element = RodinMarkerUtil.getElement(marker);
		Interval interval = documentMapper.findInterval(element);
		
		if (interval != null) {
			return new Position(interval.getOffset(), interval.getLength());
		}
		
		return null;
	}

	public IEditorInput getEditorInput() {
		return editorInput;
	}

	
}
