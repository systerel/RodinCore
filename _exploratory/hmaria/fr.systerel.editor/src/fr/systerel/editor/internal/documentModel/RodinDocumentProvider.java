/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.api.itf.ILFileFactory;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.updaters.ImplicitPresentationUpdater;
import fr.systerel.editor.internal.presentation.updaters.PresentationUpdater;

/**
 * This is a document provider for rodin machines and contexts. It is intended
 * that for each editor there is used a new instance of this class.
 */
public class RodinDocumentProvider extends AbstractDocumentProvider {

	private IDocument document;
	protected DocumentMapper documentMapper;
	private ILElement inputRoot;
	private IEditorInput editorInput;
	private RodinTextGenerator textGenerator;

	private ILFile inputResource;
	private final PresentationUpdater elementPresentationChangeAdapter;
	private final ImplicitPresentationUpdater implicitPresentationUpdater;

	public RodinDocumentProvider(DocumentMapper mapper, RodinEditor editor) {
		this.documentMapper = mapper;
		elementPresentationChangeAdapter = new PresentationUpdater(editor, mapper);
		implicitPresentationUpdater = new ImplicitPresentationUpdater();
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		if (element instanceof IFileEditorInput)
			return new ResourceMarkerAnnotationModel(
					((IFileEditorInput) element).getFile());
		return new AnnotationModel();
	}
	
	/**
	 * This is the method called to load a resource into the editing domain's
	 * resource set based on the editor's input.
	 */
	public ILFile getResource(IFile file) {
		final ILFile resource = ILFileFactory.INSTANCE.createILFile(file);
		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		resource.addAdapter(implicitPresentationUpdater);
		resource.addEContentAdapter(elementPresentationChangeAdapter);
		return resource;
	}

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		document = new Document();
		if (element instanceof IEditorInput) {
			final IFile file = (IFile) ((IEditorInput) element)
					.getAdapter(IFile.class);
			editorInput = (IEditorInput) element;

			inputResource = (ILFile) getResource(file);
			inputRoot = inputResource.getRoot();
			documentMapper.setRoot(inputRoot);
			textGenerator = new RodinTextGenerator(documentMapper);
			document.set(textGenerator.createText(inputRoot));
			documentMapper.setDocument(document);
			documentMapper.setDocumentProvider(this);

		}
		final RodinPartitioner partitioner = new RodinPartitioner(
				documentMapper, new String[] { //
				RodinConfiguration.IDENTIFIER_TYPE.getName(),
						RodinConfiguration.COMMENT_TYPE.getName(),
						RodinConfiguration.LABEL_TYPE.getName(),
						RodinConfiguration.CONTENT_TYPE.getName() });
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document, false);
		return document;
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		((ILFile)inputResource).save();
	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}

	public boolean isModifiable(Object element) {
		return false;
	}

	public void changed(Object element) {
		// do nothing
	}

	public void doSynchronize(Object element, IProgressMonitor monitor) {
		//System.out.println("synchronizing");
		fireElementContentAboutToBeReplaced(element);
		document.set(textGenerator.createText(inputRoot));
		fireElementContentReplaced(element);
		fireElementDirtyStateChanged(element, true);
	}
	
	public void synchronizeRoot(IProgressMonitor monitor) {
		if (inputRoot != null)
		doSynchronize(inputRoot, monitor);
	}

	public boolean isReadOnly(Object element) {
		return false;
	}

	public IEventBRoot getInputRoot() {
		return (IEventBRoot) inputRoot.getElement();
	}
	
	public IDocument getDocument() {
		return document;
	}

	public IEditorInput getEditorInput() {
		return editorInput;
	}

	protected void replaceTextInDocument(Interval interval, String text) {
		replaceTextInDocument(interval.getOffset(), interval.getLength(), text);
	}
	
	protected void replaceTextInDocument(int offset, int length, String text) {
		if (document != null) {
			try {
				fireElementContentAboutToBeReplaced(document);
				document.replace(offset, length, text);
				fireElementContentReplaced(document);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	public void unloadResource() {
		inputResource.unloadResource();
	}
	
}
