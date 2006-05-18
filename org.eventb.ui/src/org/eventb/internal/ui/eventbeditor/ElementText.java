package org.eventb.internal.ui.eventbeditor;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;

public abstract class ElementText
	implements ModifyListener, IElementMovedListener
{
	int lastModify;

	private class TimeRunnable implements Runnable {
		private int time;
		
		TimeRunnable(int time) {
			this.time = time;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// TODO Auto-generated method stub
			if (lastModify == time) {
				if (!text.isDisposed()) commit(element, column, text.getText());
			}
		}
	}
	
	private IRodinElement element;
	private int column;
	private TreeEditor editor;
	private Text text;
	private TreeItem item;
	private Tree tree;
	private int inset;
	private String original;
	
	public abstract void commit(IRodinElement element, int column, String contents);

	public abstract void nextEditableCell();
	
	public abstract void prevEditableCell();
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		lastModify = e.time;
		text.getDisplay().timerExec(1000, new TimeRunnable(e.time));
	}
	
	private class ElementTextListener implements Listener {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event) {
			final String contents = text.getText();
			switch (event.type) {
			case SWT.FocusOut:
//				UIUtils.debug("FocusOut");
				commit(element, column, contents);
				text.getParent().dispose();
				break;
			case SWT.Verify:
//					UIUtils.debug("Verify");
				String newText = text.getText();
				String leftText = newText.substring (0, event.start);
				String rightText = newText.substring (event.end, newText.length ());
				GC gc = new GC (text);
				Point size = gc.textExtent (leftText + event.text + rightText);
				gc.dispose ();
				size = text.computeSize (size.x, SWT.DEFAULT);
				editor.horizontalAlignment = SWT.LEFT;
				Rectangle itemRect = item.getBounds (), rect = tree.getClientArea ();
//					UIUtils.debug("ItemRect: " + itemRect);
//					UIUtils.debug("Rect: " + rect);
//					UIUtils.debug("Size: " + size.x);
				editor.minimumWidth = Math.max (size.x, itemRect.width) + inset * 2;
				int left = itemRect.x, right = rect.x + rect.width;
				editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
				editor.minimumHeight = size.y + inset * 2;
//				UIUtils.debug("Editor layout --- Height: " + editor.minimumHeight + " Width: " + editor.minimumWidth);
				editor.layout();
				break;
			case SWT.Traverse:
				switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
					UIUtils.debug("TraverseReturn");
					commit(element, column, contents);
					text.getParent().dispose();
					event.doit = false;
					break;
				case SWT.TRAVERSE_ESCAPE:
					commit(element, column, original);
					text.getParent().dispose();
					event.doit = false;
					break;
				case SWT.TRAVERSE_TAB_NEXT:
					commit(element, column, contents);
					text.getParent().dispose();
					nextEditableCell();
					event.doit = false;
					break;
				case SWT.TRAVERSE_TAB_PREVIOUS:
					commit(element, column, contents);
					text.getParent().dispose();
					prevEditableCell();
					event.doit = false;
					break;
//				case SWT.TRAVERSE_ARROW_NEXT:
//					commit(leaf, column, original);
//					text.getParent().dispose();
//					nextEditableCell();
//					event.doit = false;
//					break;
//				case SWT.TRAVERSE_ARROW_PREVIOUS:
//					commit(leaf, column, original);
//					text.getParent().dispose();
//					prevEditableCell();
//					event.doit = false;
//					break;
//				case SWT.TRAVERSE_PAGE_NEXT:
//					commit(leaf, column, original);
//					text.getParent().dispose();
//					nextEditableCell();
//					event.doit = false;
//					break;
//				case SWT.TRAVERSE_PAGE_PREVIOUS:
//					commit(leaf, column, original);
//					text.getParent().dispose();
//					prevEditableCell();
//					event.doit = false;
//					break;
				}
			}
		}
	}
	
	
	public ElementText(
			EventBEditableTreeViewer viewer,
			Text text, 
			TreeEditor editor,
			TreeItem item,
			Tree tree,
			IRodinElement element,
			int column) {
		this.text = text;
		this.element = element;
		this.editor = editor;
		this.column = column;
		this.tree = tree;
		this.item = item;
		this.original = item.getText(column);
		boolean isCarbon = SWT.getPlatform ().equals ("carbon");
		inset = isCarbon ? 0 : 1;
		viewer.addElementMovedListener(this);
		Listener textListener = new ElementTextListener();
		text.addListener (SWT.FocusOut, textListener);
		text.addListener (SWT.Traverse, textListener);
		text.addListener (SWT.Verify, textListener);
		text.addModifyListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.IElementMovedListener#elementMoved(java.util.HashMap)
	 */
	public void elementMoved(HashMap<IRodinElement, IRodinElement> moved) {
		if (moved.containsKey(element)) {
			UIUtils.debug("Element moved, update from " + element.getElementName() + " to " + moved.get(element).getElementName());
			element = moved.get(element);
		}
	}

	
}
