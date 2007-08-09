package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class EditPage extends EventBEditorPage implements ISelectionProvider,
		IElementChangedListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".edit"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_edit_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_edit_tabTitle;

	List<ISectionComposite> sectionComps;

	// The scrolled form
	ScrolledForm form;
	
	private IHandlerActivation moveUpHandlerActivation;

	private IHandlerActivation moveDownHandlerActivation;
	
	IContextActivation activateContext;
	
	// Comment at file level
	IEventBInputText commentText;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public EditPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
		listenerList = new ListenerList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
		((IEventBEditor<?>) editor).addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		Composite body = form.getBody();
		GridLayout gLayout = new GridLayout();
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		body.setLayout(gLayout);

		if (EventBEditorUtils.DEBUG) {
			body.setBackground(form.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		}
		
		createDeclaration(body);

		createSections(body);
		form.reflow(true);

		IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		activateContext = contextService
				.activateContext("org.eventb.ui.keybindings.contexts.editpage");

		activateHandlers();
	}

	private void activateHandlers() {
		IHandlerService handlerService = (IHandlerService) EventBUIPlugin.getDefault()
				.getWorkbench().getAdapter(IHandlerService.class);
		if (handlerService == null)
			return;
		
		// Move Up Handler
		IHandler moveUpHandler= new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				move(true);
				return null;
			}
		};
		moveUpHandlerActivation = handlerService.activateHandler(
				"org.eventb.ui.keybindings.moveUp", moveUpHandler);

		// Move Down Handler
		IHandler moveDownHandler= new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				move(false);
				return null;
			}
		};
		moveDownHandlerActivation = handlerService.activateHandler(
				"org.eventb.ui.keybindings.moveDown", moveDownHandler);
	}

	protected void move(boolean up) {
		// Assume that the global selection contain the list of element of the
		// same type and has the same parent
		if (globalSelection instanceof StructuredSelection
				&& !globalSelection.isEmpty()) {
			StructuredSelection ssel = (StructuredSelection) globalSelection;
			Object[] elements = ssel.toArray();
			IInternalElement firstElement = (IInternalElement) elements[0];
			IInternalElement lastElement = (IInternalElement) elements[elements.length - 1];
			IRodinElement parent = firstElement.getParent();
			IInternalElementType<?> type = firstElement.getElementType();

			if (parent != null && parent instanceof IInternalParent) {
				try {
					IInternalElement[] children = ((IInternalParent) parent)
							.getChildrenOfType(type);
					assert (children.length > 0);
					IInternalElement prevElement = null;
					for (int i = 0; i < children.length; ++i) {
						if (children[i].equals(firstElement))
							break;
						prevElement = children[i];
					}
					IInternalElement nextElement = null;
					for (int i = children.length - 1; i >= 0; --i) {
						if (children[i].equals(lastElement))
							break;
						nextElement = children[i];
					}
					if (up) {
						if (prevElement != null) {
							prevElement.move(parent, nextElement, null, false,
									new NullProgressMonitor());
						}
					} else {
						if (nextElement != null) {
							nextElement.move(parent, firstElement, null, false,
									new NullProgressMonitor());
						}
					}
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void createDeclaration(Composite parent) {
		FormToolkit toolkit = this.getManagedForm().getToolkit();
		EventBEditor<?> editor = (EventBEditor<?>) this.getEditor();
		final Composite comp = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG) {
			comp.setBackground(comp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		comp.setLayout(gridLayout);
		FormText widget = toolkit.createFormText(comp, true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		final IRodinFile rodinInput = editor.getRodinInput();
		String declaration = "";
		if (rodinInput instanceof IMachineFile)
			declaration = "MACHINE";
		else if (rodinInput instanceof IContextFile)
			declaration = "CONTEXT";

		Label label = toolkit.createLabel(comp, "//");
		label.setLayoutData(new GridData());
		
		String text = "<form><li style=\"text\" bindent = \"-20\"><b>"
				+ declaration + "</b> "
				+ EventBPlugin.getComponentName(rodinInput.getElementName())
				+ "</li></form>";
		widget.setText(text, true, true);

		final Text commentWidget = toolkit.createText(comp, "", SWT.MULTI);
		commentWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		commentText = new EventBText(commentWidget);
		new TimerText(commentWidget, 1000) {

			@Override
			protected void response() {
				if (rodinInput instanceof ICommentedElement) {
					ICommentedElement cElement = (ICommentedElement) rodinInput;
					try {
						if (!cElement.hasComment()
								|| !cElement.getComment().equals(
										commentWidget.getText())) {
							cElement.setComment(commentWidget.getText(),
									new NullProgressMonitor());
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		if (rodinInput instanceof ICommentedElement) {
			ICommentedElement cElement = (ICommentedElement) rodinInput;
			try {
				if (cElement.hasComment()) {
					commentWidget.setText(cElement.getComment());
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		toolkit.paintBordersFor(comp);
	}

	private void createSections(final Composite parent) {
		EventBEditor<?> editor = (EventBEditor<?>) this.getEditor();
		IRodinFile rodinInput = editor.getRodinInput();
		IElementRelUISpecRegistry editSectionRegistry = ElementRelUISpecRegistry
				.getDefault();
		FormToolkit toolkit = this.getManagedForm().getToolkit();

		List<IElementRelationship> rels = editSectionRegistry
				.getElementRelationships(rodinInput.getElementType());

		sectionComps = new ArrayList<ISectionComposite>(rels.size());
		for (IElementRelationship rel : rels) {

			SectionComposite sectionComp = new SectionComposite(this, toolkit,
					form, parent, rodinInput, rel, 0);
			// Create the section composite
			sectionComps.add(sectionComp);
		}
	}

	public static Map<IRodinElement, Collection<IEditComposite>> addToMap(
			Map<IRodinElement, Collection<IEditComposite>> map,
			IRodinElement element, IEditComposite editComposite) {
		Collection<IEditComposite> editComposites = map.get(element);
		if (editComposites == null) {
			editComposites = new ArrayList<IEditComposite>();
			map.put(element, editComposites);
		}
		editComposites.add(editComposite);
		return map;
	}

	Set<IRodinElement> isChanged;

	Set<IRodinElement> isRemoved;

	Set<IRodinElement> isAdded;

	Set<Pair<IRodinElement, IElementType<?>>> childrenHasChanged;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		long beforeTime = System.currentTimeMillis();
		isChanged = new HashSet<IRodinElement>();
		isRemoved = new HashSet<IRodinElement>();
		isAdded = new HashSet<IRodinElement>();
		childrenHasChanged = new HashSet<Pair<IRodinElement, IElementType<?>>>();
		processDelta(event.getDelta());
		postRefresh();
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
		updateComment();
	}

	private void updateComment() {
		final Text commentWidget = commentText.getTextWidget();
		String text = commentWidget.getText();
		IRodinFile rodinInput = this.getEventBEditor().getRodinInput();
		if (rodinInput instanceof ICommentedElement) {
			final ICommentedElement cElement = (ICommentedElement) rodinInput;
			try {
				if (cElement.hasComment()) {
					final String comment = cElement.getComment();
					if (!comment.equals(text)) {
						Display display = commentWidget.getDisplay();
						if (!display.isDisposed()) {
							display.asyncExec(new Runnable() {

								public void run() {
									commentWidget.setText(comment);
								}

							});
						}
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			internalPack(commentWidget.getParent());
		}
	}

	private void postRefresh() {
		if (form == null || form.isDisposed())
			return;

		Display display = form.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				form.getBody().setRedraw(false);

				for (IRodinElement element : isRemoved) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.elementRemoved(element);
					}
					if (isSelected(element)) {
						deselect(globalSelection);
						setSelection(new StructuredSelection());
					}
				}

				
				for (IRodinElement element : isAdded) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.elementAdded(element);
					}
				}

				for (IRodinElement element : isChanged) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.refresh(element);
					}
				}
			
				for (Pair<IRodinElement, IElementType<?>> pair : childrenHasChanged) {
					for (ISectionComposite sectionComp : sectionComps) {
						sectionComp.childrenChanged(pair.getFirst(), pair
								.getSecond());
					}
				}

				form.getBody().setRedraw(true);
			}

		});
	}

	protected boolean isSelected(IRodinElement element) {
		if (globalSelection instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection) globalSelection;
			for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
				if (it.next().equals(element))
					return true;
			}
		}
		return false;
	}

	void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();
		int kind = delta.getKind();
		if (element instanceof IRodinFile && kind == IRodinElementDelta.CHANGED) {
			for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
				processDelta(subDelta);
			}
			return;
		}

		if (kind == IRodinElementDelta.ADDED) {
			isAdded.add(element);
			childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
					element.getParent(), element.getElementType()));
			return;
		}
		if (kind == IRodinElementDelta.REMOVED) {
			isRemoved.add(element);
			childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
					element.getParent(), element.getElementType()));
			return;
		} else { // kind == CHANGED
			int flags = delta.getFlags();
			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("REORDERED");
				childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
						element.getParent(), element.getElementType()));
				return;
			} else if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
					processDelta(subDelta);
				}
			} else if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				isChanged.add(element);
				childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
						element.getParent(), element.getElementType()));
			}
			return;
		}

	}

	@Override
	public void dispose() {
		IEventBEditor<?> editor = this.getEventBEditor();
		editor.removeElementChangedListener(this);
		if (activateContext != null)
			activateContext.getContextService().deactivateContext(activateContext);
		deactivateHandlers();
		if (commentText != null) {
			commentText.dispose();
		}
		super.dispose();
	}

	private void deactivateHandlers() {
		IHandlerService handlerService = (IHandlerService) EventBUIPlugin
				.getDefault().getWorkbench().getAdapter(IHandlerService.class);

		if (handlerService != null) {
			if (moveUpHandlerActivation != null)
				handlerService.deactivateHandler(moveUpHandlerActivation);
			if (moveDownHandlerActivation != null)
				handlerService.deactivateHandler(moveDownHandlerActivation);
		}
		moveUpHandlerActivation = null;
		moveDownHandlerActivation = null;
	}

	private ListenerList listenerList;

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listenerList.add(listener);
	}

	ISelection globalSelection;

	public ISelection getSelection() {
		return globalSelection;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listenerList.remove(listener);
	}

	public void setSelection(ISelection selection) {
		this.globalSelection = selection;
		fireSelectionChanged(new SelectionChangedEvent(this, globalSelection));
		IEventBEditor<?> editor = (IEventBEditor<?>) this.getEditor();
		ISelectionProvider selectionProvider = editor.getSite()
				.getSelectionProvider();
		selectionProvider.setSelection(selection);
	}

	/**
	 * Notifies all registered selection changed listeners that the editor's
	 * selection has changed. Only listeners registered at the time this method
	 * is called are notified.
	 * 
	 * @param event
	 *            the selection changed event
	 */
	public void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = this.listenerList.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	private IRodinElement lastSelectedElement;

	public void selectionChanges(IRodinElement element, boolean shiftPressed) {
		long beginTime = System.currentTimeMillis();
		if (globalSelection instanceof StructuredSelection
				&& ((StructuredSelection) globalSelection).size() == 1
				&& ((StructuredSelection) globalSelection).getFirstElement()
						.equals(element)) {
			select(element, false);
			setSelection(new StructuredSelection());
			return;

		} else {
			deselect(globalSelection);
			if (shiftPressed
					&& lastSelectedElement != null
					&& element.getParent().equals(
							lastSelectedElement.getParent())
					&& element.getElementType().equals(
							lastSelectedElement.getElementType())) {
				selectRange(lastSelectedElement, element);
			} else {
				select(element, true);
				setSelection(new StructuredSelection(element));
				lastSelectedElement = element;
			}
		}
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG) {
			EventBEditorUtils.debug("Duration " + (afterTime - beginTime)
					+ " ms");
		}
	}

	private void selectRange(IRodinElement firstElement,
			IRodinElement secondElement) {
		assert firstElement.getParent().equals(secondElement.getParent());
		assert firstElement.getElementType().equals(
				secondElement.getElementType());
		IRodinElement parent = firstElement.getParent();
		IElementType<? extends IRodinElement> type = firstElement
				.getElementType();
		assert parent instanceof IInternalParent;
		try {
			IRodinElement[] children = ((IInternalParent) parent)
					.getChildrenOfType(type);
			boolean found = false;
			List<IRodinElement> selected = new ArrayList<IRodinElement>();
			for (IRodinElement child : children) {
				if (child.equals(firstElement) || child.equals(secondElement)) {
					select(child, true);
					selected.add(child);
					if (found)
						break;
					else
						found = true;
				} else {
					if (found) {
						select(child, true);
						selected.add(child);
					}
				}
			}
			setSelection(new StructuredSelection(selected));
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void deselect(ISelection ssel) {
		if (ssel instanceof StructuredSelection) {
			for (Iterator<?> it = ((StructuredSelection) ssel).iterator(); it
					.hasNext();) {
				Object obj = it.next();
				if (obj instanceof IRodinElement) {
					select((IRodinElement) obj, false);
				}
			}
		}
	}

	private void select(IRodinElement element, boolean select) {
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.select(element, select);
		}
	}

	public void recursiveExpand(IRodinElement element) {
		for (ISectionComposite sectionComp : sectionComps) {
			sectionComp.recursiveExpand(element);
		}
	}

	void internalPack(Composite c) {
		if (c.equals(form.getBody())) {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Full resize");
			form.reflow(true);			
		}
		Rectangle bounds = c.getBounds();
		Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			internalPack(c.getParent());
		} else {
			c.layout(true);
			c.setBounds(bounds);
		}
	}

}
