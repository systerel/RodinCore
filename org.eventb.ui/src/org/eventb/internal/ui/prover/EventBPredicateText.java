package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.eventBKeyboard.EventBStyledTextModifyListener;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

public class EventBPredicateText implements IPropertyChangeListener {

	private final static int MAX_WIDTH = 300;

	private final static int MAX_HEIGHT = 120;

	Listener labelListener;

	private IUserSupport us;

	private Predicate hyp;

	StyledText text;

	ScrolledForm parent;

	final Cursor handCursor;

	final Cursor arrowCursor;

	private Collection<Point> boxes;

	Map<Point, TacticPositionUI> links;

	// Point[] links;

	// Runnable[] commands;

	Point current;

	private int currSize;

	Collection<Point> dirtyStates;

	final Color BLUE = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);

	final Color YELLOW = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);

	Point currentLink;

	Shell tipShell;

	public EventBPredicateText(FormToolkit toolkit, final ScrolledForm parent) {

		this.parent = parent;
		dirtyStates = new ArrayList<Point>();
		text = new StyledText(parent.getBody(), SWT.MULTI | SWT.FULL_SELECTION);
		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		text.setFont(font);
		handCursor = new Cursor(text.getDisplay(), SWT.CURSOR_HAND);
		arrowCursor = new Cursor(text.getDisplay(), SWT.CURSOR_ARROW);
		JFaceResources.getFontRegistry().addListener(this);
		text.addListener(SWT.MouseDown, new MouseDownListener());
		text.addListener(SWT.MouseMove, new MouseMoveListener());
		text.addListener(SWT.MouseHover, new MouseHoverListener());
		text.addListener(SWT.MouseExit, new MouseExitListener());
		text.addListener(SWT.MouseEnter, new MouseEnterListener());

		// Implement a "fake" tooltip
		labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					shell.dispose();
					text.getShell().setFocus();
					break;
				case SWT.MouseExit:
					shell.dispose();
					break;
				case SWT.MouseMove:
					shell.dispose();
					break;
				}
			}
		};

	}

	// This must be called after initialisation
	public void setText(String string, IUserSupport us, Predicate hyp,
			Collection<Point> boxes, Map<Point, TacticPositionUI> links) {
		this.hyp = hyp;
		this.us = us;
		this.links = links;
		this.boxes = boxes;
		currentLink = null;
		text.setText(string);
		text.pack();
		text.addModifyListener(new EventBStyledTextModifyListener());

		text.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				drawBoxes(event);
			}

		});

		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (current == null)
					return;
				dirtyStates.add(current);
				updateIndexes();
			}

		});

		text.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				checkModifiable(e);
			}

		});
		setStyle();
	}

	protected void checkModifiable(VerifyEvent e) {
		e.doit = false;
		Point pt = new Point(e.start, e.end);

		// Make sure the selection is from left to right
		if (pt.x > pt.y) {
			pt = new Point(pt.y, pt.x);
		}

		// It is only modify-able if it is within one subrange and the
		for (Point index : boxes) {

			// if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Event " + e);
			ProverUIUtils.debug("index.x " + index.x + ", index.y " + index.y);
			ProverUIUtils.debug("pt.x " + pt.x + ", pt.y " + pt.y);
			if (index.x > pt.x)
				continue;
			if (index.y < pt.y)
				continue;

			if (e.text.equals("")) { // deletion
				if (pt.x == pt.y - 1 && index.y == pt.x) // SWT.DEL at the
					// end
					continue;
				else if (pt.x == pt.y + 1 && index.x == pt.y) // SWT.BS at the
					// beginning
					continue;
			}

			e.doit = true;
			current = index;
			currSize = text.getText().length();
			break;
		}
	}

	protected void updateIndexes() {
		int offset = text.getText().length() - currSize;

		for (Point box : boxes) {
			if (box.x > current.y) {
				box.x = box.x + offset;
				box.y = box.y + offset;
			}
		}

		for (Point link : links.keySet()) {
			if (link.x > current.y) {
				link.x = link.x + offset;
				link.y = link.y + offset;
			}
		}

		current.y = current.y + offset;

		setStyle();
		text.pack();
		parent.reflow(true);
	}

	void drawBoxes(Event event) {
		// ProverUIUtils.debugProverUI("Draw boxes");
		if (boxes == null)
			return;
		String contents = text.getText();
		int lineHeight = text.getLineHeight();
		final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
		event.gc.setForeground(RED);
		for (Point index : boxes) {
			String str = contents.substring(index.x, index.y);
			int stringWidth = event.gc.stringExtent(str).x;
			Point topLeft = text.getLocationAtOffset(index.x);
			event.gc.drawRectangle(topLeft.x - 1, topLeft.y, stringWidth + 1,
					lineHeight - 1);
		}
	}

	private void setStyle() {
		if (boxes == null)
			return;
		for (Point index : boxes) {
			StyleRange style = new StyleRange();
			style.start = index.x;
			style.length = index.y - index.x;
			if (dirtyStates.contains(index))
				style.background = YELLOW;
			style.fontStyle = SWT.ITALIC;
			text.setStyleRange(style);
		}

		for (Point link : links.keySet()) {
			StyleRange style = new StyleRange();
			style.start = link.x;
			style.length = link.y - link.x;
			style.foreground = BLUE;
			text.setStyleRange(style);
		}
	}

	public StyledText getMainTextWidget() {
		return text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			text.setFont(font);
			text.pack();
		}
	}

	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		text.dispose();
	}

	public String[] getResults() {
		if (boxes == null)
			return new String[0];
		String[] results = new String[boxes.size()];
		int i = 0;
		for (Point index : boxes) {
			String str = text.getText().substring(index.x, index.y);
			results[i] = str;
			i++;
		}

		return results;
	}

	class MouseDownListener implements Listener {

		public void handleEvent(Event e) {
			if (currentLink != null) {
				TacticPositionUI tacticPositionUI = links.get(currentLink);
				String[] tacticIDs = tacticPositionUI.getTacticIDs();
				IPosition[] positions = tacticPositionUI.getPositions();
				if (tacticIDs.length == 1) {
					applyTactic(tacticIDs[0], positions[0]);
				} else {
					Point widgetPosition = new Point(e.x, e.y);
					final Menu menu = new Menu(text);

					for (int i = 0; i < tacticIDs.length; ++i) {
						final String tacticID = tacticIDs[i];
						final IPosition position = positions[i];

						MenuItem item = new MenuItem(menu, SWT.DEFAULT);
						TacticUIRegistry tacticUIRegistry = TacticUIRegistry
								.getDefault();
						item.setText(tacticUIRegistry.getTip(tacticID));
						item.addSelectionListener(new SelectionListener() {

							public void widgetDefaultSelected(SelectionEvent se) {
								widgetSelected(se);
							}

							public void widgetSelected(SelectionEvent se) {
								applyTactic(tacticID, position);
							}

						});
					}

					Point tipPosition = text.toDisplay(widgetPosition);
					setMenuLocation(menu, tipPosition);
					menu.setVisible(true);
				}

			}
			return;
		}

	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param menu
	 *            the menu
	 * @param position
	 *            the position of a widget to hover over
	 */
	void setMenuLocation(Menu menu, Point position) {
		Rectangle displayBounds = menu.getDisplay().getBounds();

		int x = Math.max(Math.min(position.x, displayBounds.width), 0);
		int y = Math.max(Math.min(position.y + 16, displayBounds.height), 0);
		menu.setLocation(new Point(x, y));
	}

	class MouseMoveListener implements Listener {

		public void handleEvent(Event e) {
			if (tipShell != null && !tipShell.isDisposed())
				tipShell.dispose();

			Point location = new Point(e.x, e.y);
			try {
				int offset = getCharacterOffset(location);
				Point index = getLink(offset);
				if (index != null) {
					if (!currentLink.equals(index)) {
						if (currentLink != null) {
							disableCurrentLink();
						}
						enableLink(index);
						currentLink = index;
						text.setCursor(handCursor);
					}
				} else {
					if (currentLink != null) {
						disableCurrentLink();
						text.setCursor(arrowCursor);
						currentLink = null;
					}
				}
				// if (ProverUIUtils.DEBUG)
				// ProverUIUtils.debug("Move Offset " + offset);
			} catch (IllegalArgumentException exception) {
				// if (ProverUIUtils.DEBUG)
				// ProverUIUtils.debug("Invalid");
			}
			return;
		}
	}

	class MouseEnterListener implements Listener {

		public void handleEvent(Event e) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Enter ");
			Point location = new Point(e.x, e.y);
			try {
				int offset = getCharacterOffset(location);
				Point index = getLink(offset);
				if (index != null) {
					if (currentLink != index) {
						if (currentLink != null) {
							disableCurrentLink();
						}
						enableLink(index);
						currentLink = index;
						text.setCursor(handCursor);
					}
				} else {
					if (currentLink != null) {
						disableCurrentLink();
						text.setCursor(arrowCursor);
						currentLink = null;
					}
				}
			} catch (IllegalArgumentException exception) {
				// if (ProverUIUtils.DEBUG)
				// ProverUIUtils.debug("Invalid");
			}
			return;
		}
	}

	class MouseHoverListener implements Listener {

		public void handleEvent(Event e) {
			if (currentLink != null) {
				TacticPositionUI tacticPositionUI = links.get(currentLink);
				String[] tacticIDs = tacticPositionUI.getTacticIDs();

				if (tipShell != null && !tipShell.isDisposed())
					tipShell.dispose();

				tipShell = new Shell(text.getShell(), SWT.ON_TOP | SWT.NO_FOCUS
						| SWT.TOOL);
				Display display = text.getDisplay();

				GridLayout gridLayout = new GridLayout();
				// gridLayout.numColumns = 1;
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				tipShell.setLayout(gridLayout);
				tipShell.setBackground(display
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

				for (int i = 0; i < tacticIDs.length; ++i) {
					Label tipLabel = new Label(tipShell, SWT.NONE);
					tipLabel.setForeground(display
							.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
					tipLabel.setBackground(display
							.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
					tipLabel.setLayoutData(new GridData(GridData.FILL_BOTH
							| GridData.VERTICAL_ALIGN_CENTER));
					// Create a new font for this label
					Font font = JFaceResources
							.getFont(PreferenceConstants.EVENTB_MATH_FONT);
					tipLabel.setFont(font);
					tipLabel.setText("Item ");
					tipLabel.setText(getToolTipText(tacticIDs[i]));
					tipLabel.addListener(SWT.MouseExit, labelListener);
					tipLabel.addListener(SWT.MouseDown, labelListener);

					// Create a separator
					Label separator = new Label(tipShell, SWT.SEPARATOR);
					GridData gd = new GridData(GridData.FILL_HORIZONTAL
							| GridData.VERTICAL_ALIGN_CENTER);
					gd.heightHint = 1;
					separator.setLayoutData(gd);
				}
				Point widgetPosition = new Point(e.x, e.y);

				Point tipPosition = text.toDisplay(widgetPosition);
				Point shellSize = tipShell
						.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = MAX_WIDTH < shellSize.x ? MAX_WIDTH : shellSize.x;
				Point pt = tipShell.computeSize(width, SWT.DEFAULT);
				int height = MAX_HEIGHT < pt.y ? MAX_HEIGHT : pt.y;
				// tipLabel.setSize(width, height);
				tipShell.setSize(width, height);
				// tipLabel.setSize(200, 40);
				setHoverLocation(tipShell, tipPosition);
				tipShell.setVisible(true);
			}
			return;
		}
	}

	class MouseExitListener implements Listener {

		public void handleEvent(Event event) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Exit ");
			if (currentLink != null) {
				disableCurrentLink();
				currentLink = null;
			}

		}

	}

	int getCharacterOffset(Point pt) {
		int offset = text.getOffsetAtLocation(pt);
		Point location = text.getLocationAtOffset(offset);

		// From the caret offset to the character offset.
		if (pt.x < location.x)
			offset = offset - 1;
		return offset;
	}

	public void enableLink(Point index) {
		StyleRange style = new StyleRange();
		style.start = index.x;
		style.length = index.y - index.x;
		style.foreground = BLUE;
		style.underline = true;
		text.setStyleRange(style);
	}

	public void disableCurrentLink() {
		assert currentLink != null;
		StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = BLUE;
		style.underline = false;
		text.setStyleRange(style);
	}

	public Point getLink(int offset) {
		Set<Point> keySet = links.keySet();
		for (Point index : keySet) {
			if (index.x <= offset && offset < index.y)
				return index;
		}
		return null;
	}

	void applyTactic(String tacticID, IPosition position) {
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		Set<Predicate> hypSet = new HashSet<Predicate>();
		hypSet.add(hyp);
		String[] inputs = this.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");

		ITacticProvider provider = tacticUIRegistry.getTacticProvider(tacticID);
		if (provider != null)
			try {
				us.applyTacticToHypotheses(provider.getTactic(us.getCurrentPO()
						.getCurrentNode(), hyp, position, inputs), hypSet,
						new NullProgressMonitor());
			} catch (RodinDBException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		else {
			IProofCommand command = tacticUIRegistry.getProofCommand(tacticID,
					TacticUIRegistry.TARGET_HYPOTHESIS);
			if (command != null) {
				try {
					command.apply(us, hyp, inputs, new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param shell
	 *            the object that is to hover
	 * @param position
	 *            the position of a widget to hover over
	 */
	void setHoverLocation(Shell shell, Point position) {
		Rectangle displayBounds = shell.getDisplay().getBounds();
		Rectangle shellBounds = shell.getBounds();
		shellBounds.x = Math.max(Math.min(position.x, displayBounds.width
				- shellBounds.width), 0);
		shellBounds.y = Math.max(Math.min(position.y + 16, displayBounds.height
				- shellBounds.height), 0);
		shell.setBounds(shellBounds);
	}

	protected String getToolTipText(String tacticID) {
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();

		String tip = tacticUIRegistry.getTip(tacticID);
		int i = tip.indexOf('.');
		int j = tip.indexOf('\n');
		if (i == -1) {
			if (j == -1)
				return tip; // One line comment
			else {
				return tip.substring(0, j); // Return the first line
			}
		} else {
			i++;
			if (j == -1)
				return tip.substring(0, i); // Return the first
			// sentence
			else {
				int k = i < j ? i : j; // k is the minimum
				return tip.substring(0, k);
			}
		}

	}
}
