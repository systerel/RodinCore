package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;

public abstract class EditRow {

	Composite composite;

	FormToolkit fToolkit;

	IInternalParent parent;

	IInternalElement element;

	IEventBEditor fEditor;

	IEditComposite[] editComposites;
	
	Button cButton;
	
	EditPage fPage;

	public EditRow(EditPage page, IInternalParent parent,
			FormToolkit toolkit, Composite compParent,
			IInternalElement element, IElementType type, ScrolledForm fForm, int level) {
		this.fToolkit = toolkit;
		this.parent = parent;
		this.element = element;
		this.fPage = page;
		this.fEditor = (IEventBEditor) page.getEditor();
		composite = toolkit.createComposite(compParent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent e) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Variable Section Composite Key event " + e);
			}
			
		});
		
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		int numColumns = 1 + 3 * sectionRegistry.getNumColumns(type);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns + 1;
		composite.setLayout(gridLayout);

		createButtons(type, level);
		if (element != null)
			editComposites = sectionRegistry.createColumns(fForm, fToolkit,
					composite, element);
		toolkit.paintBordersFor(composite);
	}

	public void createButtons(IElementType type, int level) {
		Composite buttonComp = fToolkit.createComposite(composite);
		buttonComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		GridLayout gridLayout = new GridLayout();
		buttonComp.setLayout(gridLayout);
		gridLayout.numColumns = 4;
		gridLayout.marginTop = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;

		Composite tmp = fToolkit.createComposite(buttonComp);
		GridData gridData = new GridData();
		gridData.widthHint = level * 60;
		gridData.heightHint = 0;
		tmp.setLayoutData(gridData);
		
		if (element != null) {
			cButton = fToolkit.createButton(buttonComp, "",
					SWT.CHECK);
			cButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
			cButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					for (IEditComposite editComposite : editComposites) {
						editComposite.select(cButton.getSelection());
					}
					fPage.selectionChanges();
				}

			});
		}

		ImageHyperlink add = fToolkit.createImageHyperlink(buttonComp, SWT.TOP);
		add.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		add.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				Add();
			}

		});

		if (element != null) {
			ImageHyperlink remove = fToolkit.createImageHyperlink(buttonComp,
					SWT.TOP);
			remove.setImage(EventBImage
					.getImage(IEventBSharedImages.IMG_REMOVE));
			remove.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					Remove();
				}

			});
		}
	}

	public void refresh() {
		for (IEditComposite editComposite : editComposites) {
			editComposite.refresh();
		}
	}

	public abstract void Add();

	public abstract void Remove();

	public boolean isSelected() {
		return cButton.getSelection();
	}
}
