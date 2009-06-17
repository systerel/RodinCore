/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" preference
 *     Systerel - used EventBSharedColor and EventBPreferenceStore
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *     Systerel - changed double click behavior
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBStyledText;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.TimerStyledText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.internal.ui.autocompletion.EventBContentProposalAdapter;
import org.eventb.internal.ui.markers.MarkerUIRegistry;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.location.IAttributeLocation;

public class TextEditComposite extends AbstractEditComposite {

	public static final String CONTEXT_ID = EventBUIPlugin.PLUGIN_ID
	+ ".contexts.textEditCompositeScope";
	
	// Activate a new context when actually editing this text composite
	private static class ContextActivator implements FocusListener {

		private final IWorkbenchPart part;
		private IContextActivation deactivationKey;

		public ContextActivator(IWorkbenchPart part) {
			this.part = part;
		}

		public void focusGained(FocusEvent e) {
			deactivationKey = getContextService().activateContext(CONTEXT_ID);
		}

		public void focusLost(FocusEvent e) {
			if (deactivationKey != null) {
				getContextService().deactivateContext(deactivationKey);
			}
		}

		private IContextService getContextService() {
			final IWorkbenchPartSite site = part.getSite();
			return (IContextService) site.getService(IContextService.class);
		}

	}

	protected StyledText text;
	private Button undefinedButton;
	protected final int style;
	private final boolean isMath;
	private final String foregroundColor;

	EventBContentProposalAdapter adapter;
	
	public TextEditComposite(TextDesc attrDesc) {
		super(attrDesc);
		this.isMath = attrDesc.isMath();
		this.foregroundColor = attrDesc.getForegroundColor();
		// TODO implement a listener on the preference store
		if (EventBPreferenceStore
				.getBooleanPreference(PreferenceConstants.P_BORDER_ENABLE)) {
			style = getStyle(attrDesc) | SWT.BORDER;
		} else {
			style = getStyle(attrDesc);
		}
	}

	private int getStyle(TextDesc textDesc) {
		if (textDesc.getStyle() == TextDesc.Style.SINGLE) {
			return SWT.SINGLE;
		} else {
			return SWT.MULTI;
		}
	}
	
	@Override
	public void initialise(boolean refreshMarker) {
		try {
			String value = manipulation.getValue(element, new NullProgressMonitor());
			displayValue(value);
			if (refreshMarker)
				displayMarkers();
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	private void displayValue(String value) {
		if (text == null) {
			if (undefinedButton != null) {
				undefinedButton.dispose();
				undefinedButton = null;
			}
			text = new StyledText(composite, style);

			final IAttributeType attType = attrDesc.getAttributeType();
			final IAttributeLocation location = RodinCore.getInternalLocation(element, attType);
			adapter = ContentProposalFactory.getContentProposal(location, text);
			text.addVerifyKeyListener(new VerifyKeyListener() {
				public void verifyKey(VerifyEvent event) {
					if (adapter.isProposalPopupOpen()
							&& event.character == SWT.CR)
						event.doit = false;
				}
			});

			setText(value);
			
			text.addFocusListener(new ContextActivator(fEditor));
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					text.setStyleRange(null);
				}

			});
			new EventBStyledText(text, isMath) {

				@Override
				protected void commit() {
					UIUtils.setStringAttribute(element, attrDesc.getManipulation(),
							text.getText(), new NullProgressMonitor());
					super.commit();
				}

			};
			// TODO implement a listener on the preference store
			text.setForeground(getForegroundColor());
			new TimerStyledText(text, 200) {
				@Override
				protected void response() {
					if (text.isFocusControl()) {
						UIUtils.setStringAttribute(element,
								attrDesc.getManipulation(), text.getText(),
								new NullProgressMonitor());
					}
				}
			};
			this.getFormToolkit().paintBordersFor(composite);
		}else{
			setText(value);
		}
	}

	private void setText(String value){
		if (!text.getText().equals(value))
			text.setText(value);
	}
	
	private Color getForegroundColor() {
		return EventBPreferenceStore.getColorPreference(foregroundColor);
	}

	private void displayMarkers() {
		// Clear the old style ranges
		text.setStyleRange(null);

		try {
			IMarker[] markers = MarkerUIRegistry.getDefault()
					.getAttributeMarkers(element, attrDesc.getAttributeType());
			for (IMarker marker : markers) {
				int charStart = RodinMarkerUtil.getCharStart(marker);
				int charEnd = RodinMarkerUtil.getCharEnd(marker);
				StyleRange styleRange = new StyleRange();
				int length = text.getText().length();
				if (charStart != -1 && charEnd != -1) {
					int start = charStart < length ? charStart : length;
					styleRange.start = start;
					int end = charEnd < length ? charEnd : length;
					styleRange.length = end - start;
				} else {
					styleRange.start = 0;
					styleRange.length = length;
				}
				int severityAttribute = marker.getAttribute(IMarker.SEVERITY,
						-1);
				if (severityAttribute == IMarker.SEVERITY_ERROR) {
					styleRange.background = RED;
					styleRange.foreground = YELLOW;
				} else if (severityAttribute == IMarker.SEVERITY_WARNING) {
					styleRange.background = YELLOW;
					styleRange.foreground = RED;
				}
				styleRange.fontStyle = SWT.ITALIC;
				styleRange.underline = true;
				text.setStyleRange(styleRange);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		text.redraw();
	}

	/**
	 * Set undefined value for the element.
	 */
	private void setUndefinedValue() {
		FormToolkit toolkit = this.getFormToolkit();
		if (undefinedButton != null)
			return;

		if (text != null)
			text.dispose();

		undefinedButton = toolkit
				.createButton(composite, "UNDEFINED", SWT.PUSH);
		undefinedButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				setDefaultValue();
			}

		});
	}

	@Override
	public void setSelected(boolean selection) {
		Control control = text == null ? undefinedButton : text;
		if (selection)
			control.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_GRAY));
		else {
			control.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	public void setDefaultValue() {
		try {
			manipulation.setDefaultValue(element, new NullProgressMonitor());
			if (text != null)
				text.setFocus();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
	}

	public void edit(int charStart, int charEnd) {
		if (charStart != -1 && charEnd != -1)
			text.setSelection(charStart, charEnd);
		else
			text.selectAll(); // Select all
		text.setFocus();
		FormToolkit.ensureVisible(text);
	}

}
