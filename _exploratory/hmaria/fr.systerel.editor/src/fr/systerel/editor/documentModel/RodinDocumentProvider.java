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
package fr.systerel.editor.documentModel;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.lightcore.LightElement;

import fr.systerel.editor.editors.RodinConfiguration;
import fr.systerel.editor.editors.RodinEditor;

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

	private ComposedAdapterFactory adapterFactory;
	private AdapterFactoryEditingDomain editingDomain;
	private Resource inputResource;

	protected EContentAdapter elementPresentationChangeAdapter = new EContentAdapter() {
		@Override
		public void notifyChanged(Notification notification) {
			final Object oldObject = notification.getOldValue();
			final Object notifier = notification.getNotifier();
			if (notification.isTouch()) {
				return;
			}
			if (notifier instanceof ILElement && (!(oldObject instanceof ILElement))){
				documentMapper.elementChanged((ILElement) notifier);
			}
			if (oldObject instanceof ILElement) {
				documentMapper.elementChanged((ILElement) oldObject);
			}
		}
	};

	public RodinDocumentProvider(DocumentMapper mapper, RodinEditor editor) {
		this.documentMapper = mapper;
		initializeEditingDomain();
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		return new AnnotationModel();
	}
	
	protected void initializeEditingDomain() {
		// Create an adapter factory that yields item providers.
		adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		adapterFactory
				.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory
				.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		// Create the command stack that will notify this editor as commands are
		// executed.
		final BasicCommandStack commandStack = new BasicCommandStack();

		// Create the editing domain with a special command stack.
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory,
				commandStack, new HashMap<Resource, Boolean>());
	}

	/**
	 * This is the method called to load a resource into the editing domain's
	 * resource set based on the editor's input.
	 */
	public Resource getResource(IFile file) {
		final String projectName = file.getProject().getName();
		final URI resourceURI = URI.createPlatformResourceURI(projectName + "/"
				+ file.getName(), true);
		Exception exception = null;
		Resource resource = null;
		try {
			// Load the resource through the editing domain.
			resource = editingDomain.getResourceSet().getResource(resourceURI,
					true);
		} catch (Exception e) {
			exception = e;
			resource = editingDomain.getResourceSet().getResource(resourceURI,
					false);
			System.out
					.println("A problem occured when retrieving the resource of "
							+ resourceURI.toString()
							+ " : "
							+ exception.getMessage());
		}
		resource.eAdapters().add(elementPresentationChangeAdapter);
		return resource;
	}

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		document = new Document();
		if (element instanceof IEditorInput) {
			final IFile file = (IFile) ((IEditorInput) element)
					.getAdapter(IFile.class);
			editorInput = (IEditorInput) element;

			inputResource = getResource(file);
			inputRoot = ((ILFile) inputResource).getRoot();
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
		((LightElement)inputRoot).save();
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

	protected void doSynchronize(Object element, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("synchronizing");
		fireElementContentAboutToBeReplaced(element);
		document.set(textGenerator.createText(inputRoot));
		fireElementContentReplaced(element);
		fireElementDirtyStateChanged(element, false);
	}

	public boolean isReadOnly(Object element) {
		return false;
	}

	public Position[] getFoldingRegions() {
		return documentMapper.getFoldingPositions();
	}

	public ProjectionAnnotation[] getFoldingAnnotations() {
		return documentMapper.getFoldingAnnotations();
	}

	public IEventBRoot getInputRoot() {
		return (IEventBRoot) inputRoot.getElement();
	}

	public MarkerAnnotationPosition[] getMarkerAnnotations() {
		final ArrayList<MarkerAnnotationPosition> results = new ArrayList<MarkerAnnotationPosition>();
		if (inputRoot == null){
			return results.toArray(new MarkerAnnotationPosition[0]);
		}
		final IRodinElement iRoot = (IRodinElement) inputRoot.getElement();
		if (!(iRoot instanceof IEventBRoot)) {
			return results.toArray(new MarkerAnnotationPosition[0]);
		}
		final IResource file = ((IEventBRoot) iRoot).getResource();
		try {
			final IMarker[] markers = file.findMarkers(
					RodinMarkerUtil.RODIN_PROBLEM_MARKER, true,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				final SimpleMarkerAnnotation annotation = new SimpleMarkerAnnotation(
						marker);
				final Position position = findPosition(marker);
				if (position != null) {
					results.add(new MarkerAnnotationPosition(position,
							annotation));
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
		return results.toArray(new MarkerAnnotationPosition[results.size()]);
	}

	/**
	 * Finds the position in the document for a given marker.
	 * 
	 * @param marker
	 * @return the position of the element corresponding to the marker inside
	 *         the document.
	 */
	private Position findPosition(IMarker marker) {
		final IRodinElement element = RodinMarkerUtil.getElement(marker);
		final Interval interval = documentMapper.findInterval(element);

		if (interval != null) {
			return new Position(interval.getOffset(), interval.getLength());
		}

		return null;
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
	
}
