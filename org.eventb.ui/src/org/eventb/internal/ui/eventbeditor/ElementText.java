package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.internal.ui.UIUtils;

public abstract class ElementText
//	implements IElementChangedListener
{
	private Leaf leaf;
	private int column;
	private TreeEditor editor;
	private Text text;
	private TreeItem item;
	private Tree tree;
	private int inset;
	
	public abstract void commit(Leaf leaf, int column, String contents);

	private class ElementTextListener implements Listener {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event) {
			final String contents = text.getText();
			switch (event.type) {
				case SWT.FocusOut:
					UIUtils.debug("FocusOut");
					commit(leaf, column, contents);
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
					UIUtils.debug("Editor layout --- Height: " + editor.minimumHeight + " Width: " + editor.minimumWidth);
					editor.layout();
					break;
				case SWT.Traverse:
					switch (event.detail) {
						case SWT.TRAVERSE_RETURN:
							UIUtils.debug("TraverseReturn");
							commit(leaf, column, contents);
							text.getParent().dispose();
							event.doit = false;
							break;
						case SWT.TRAVERSE_ESCAPE:
							text.getParent().dispose();
							event.doit = false;
					}
					break;
			}
		}
	}
	
	
	public ElementText(
			Text text, 
			TreeEditor editor,
			TreeItem item,
			Tree tree,
			Leaf leaf,
			int column) {
		this.text = text;
		this.leaf = leaf;
		this.editor = editor;
		this.column = column;
		this.tree = tree;
		this.item = item;
		boolean isCarbon = SWT.getPlatform ().equals ("carbon");
		inset = isCarbon ? 0 : 1;
		
		Listener textListener = new ElementTextListener();
		text.addListener (SWT.FocusOut, textListener);
		text.addListener (SWT.Traverse, textListener);
		text.addListener (SWT.Verify, textListener);
	}

}
