/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
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

	protected final DocumentMapper documentMapper;
	private final PresentationUpdater elementPresentationChangeAdapter;
	private final ImplicitPresentationUpdater implicitPresentationUpdater;
	private final RodinEditor editor;
	
	private IDocument document;
	private ILElement inputRoot;
	private IEditorInput editorInput;
	private RodinTextGenerator textGenerator;
	private ILFile inputResource;
	
	protected boolean synchronizing = false;
	private ILFile resource;

	public RodinDocumentProvider(DocumentMapper mapper, RodinEditor editor) {
		this.documentMapper = mapper;
		this.editor = editor;
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
	 * This is the method called to load a resource.
	 */
	public ILFile getResource(IFile file) {
		resource = ILFileFactory.INSTANCE.createILFile(file);
		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		resource.addAdapter(implicitPresentationUpdater);
		resource.addEContentAdapter(elementPresentationChangeAdapter);
		return resource;
	}
	
	/**
	 * This is the method called to retrieve a resource, returns <code>null</code>
	 * if the resource has not been created.
	 */
	public ILFile getResource() {
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

	/**
	 * Saves the document.
	 * <p>
	 * Note: if the overlay editor is currently open (i.e. the user is currently
	 * editing an attribute), then the overlay editor is closed and it's
	 * contents are saved.
	 * </p>
	 */
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		final ILFile inputFile = (ILFile) inputResource;
		if (editor.isOverlayActive()) {
			editor.getOverlayEditor().saveAndExit(true);
			waitForUpdate(); // waiting for the overlay closing updates
		}
		inputFile.save();
	}

	private static void waitForUpdate() {
		Display display = Display.findDisplay(Thread.currentThread());
		while (display.readAndDispatch()) {
			// waiting
		}
	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}

	@Override
	public boolean isModifiable(Object element) {
		return false;
	}

	@Override
	public void changed(Object element) {
		// do nothing
	}

	@Override
	public void doSynchronize(Object element, IProgressMonitor monitor) {
		document.set(textGenerator.createText(inputRoot));
	}
	
	public void synchronizeRoot(IProgressMonitor monitor, boolean silent) {
		if (silent)
			synchronizing = true;
		try {
			synchronizeRoot(monitor);
		} finally {
			synchronizing = false;
		}
	}
	
	public void synchronizeRoot(IProgressMonitor monitor) {
		if (inputRoot != null)
			doSynchronize(inputRoot, monitor);
	}

	@Override
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
	
	protected void replaceTextInDocument(final int offset, final int length,
			final String text) {
		if (document != null) {
			final StyledText styledText = editor.getStyledText();
			if (styledText == null || styledText.isDisposed()) {
				return;
			}
			styledText.getDisplay().syncExec(new Runnable() {
				
				@Override
				public void run() {
					if (document != null) {
						try {
							document.replace(offset, length, text);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				}

			});
		}
	}

	public void unloadResource() {
		inputResource.unloadResource();
	}

	/**
	 * Creates a new element info object for the given Rodin Editor Input.
	 * Overrides the default ElementInfo class.(see
	 * <code>RodinEditorElementInfo</code> class)
	 */
	@Override
	protected ElementInfo createElementInfo(Object element)
			throws CoreException {
		return new RodinEditorElementInfo(createDocument(element),
				createAnnotationModel(element));
	}

	
	/**
	 * The ElementInfo class contains a flag <code>fCanBeSaved</code> indicating
	 * that the document can be saved of not. By default, the ElementInfo class
	 * listens to document changes and sets this 'dirty' state flag to
	 * <code>true</code>(see <code>documentChanged()</code> method). In our
	 * case, this is cumbersome, as refreshing the editor shall not modify this
	 * flag. Indeed, we want to update the document (i.e. set its text) when the
	 * editor is refreshed (i.e. synchronized), without modifying the 'dirty'
	 * state flag, as the refreshment is just supposed to change the
	 * presentation of our document in the editor. Thus, we provide here a way
	 * to avoid propagating the 'change' info during a refresh.
	 */
	protected class RodinEditorElementInfo extends ElementInfo {

		public RodinEditorElementInfo(IDocument document, IAnnotationModel model) {
			super(document, model);
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			if (!synchronizing)
				super.documentChanged(event);
		}
		
	}
	
}
