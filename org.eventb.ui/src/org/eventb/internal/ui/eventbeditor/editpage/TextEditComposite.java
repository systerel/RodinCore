package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBStyledText;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.TimerStyledText;
import org.eventb.internal.ui.markers.MarkerUIRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

public class TextEditComposite extends AbstractEditComposite {

	protected StyledText text;
	private Button undefinedButton;
	protected int style = SWT.MULTI | SWT.BORDER;

	public TextEditComposite(IAttributeUISpec uiSpec) {
		super(uiSpec);
	}

	@Override
	public void initialise(boolean refreshMarker) {
		try {
			String value = uiSpec.getAttributeFactory().getValue(element,
					new NullProgressMonitor());
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
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					text.setStyleRange(null);
				}
				
			});
			new EventBStyledText(text) {

				@Override
				protected void commit() {
					try {
						uiSpec.getAttributeFactory().setValue(element,
								text.getText(), new NullProgressMonitor());
					} catch (RodinDBException e) {
						EventBUIExceptionHandler.handleSetAttributeException(e);
					}
					super.commit();
				}

			};
			text.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_DARK_GREEN));
			new TimerStyledText(text, 200) {
				@Override
				protected void response() {
					if (text.isFocusControl())
						try {
							uiSpec.getAttributeFactory().setValue(element,
									text.getText(), new NullProgressMonitor());
						} catch (RodinDBException e) {
							EventBUIExceptionHandler
									.handleSetAttributeException(e);
						}
				}

			};
			this.getFormToolkit().paintBordersFor(composite);
		}
		if (!text.getText().equals(value))
			text.setText(value);
	}

	private void displayMarkers() {
		// Clear the old style ranges
		text.setStyleRange(null);
		
		try {
			Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
			Color YELLOW = Display.getDefault()
					.getSystemColor(SWT.COLOR_YELLOW);
			IMarker[] markers = MarkerUIRegistry.getDefault()
					.getAttributeMarkers(element, uiSpec.getAttributeType());
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
				setDefaultValue(fEditor);
			}

		});
	}

	@Override
	public void setSelected(boolean selection) {
		Control control = text == null ? undefinedButton : text;
		if (selection)
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		else {
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	public void setDefaultValue(IEventBEditor<?> editor) {
		try {
			uiSpec.getAttributeFactory().setDefaultValue(editor, element,
					new NullProgressMonitor());
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
