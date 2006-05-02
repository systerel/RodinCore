package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

public class EventBTreeLabelProvider
	implements	ITableLabelProvider,
				ITableFontProvider,
				ITableColorProvider
{
	private EventBEditor editor;
	
	public EventBTreeLabelProvider(EventBEditor editor) {
		this.editor = editor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		IRodinElement rodinElement = ((Leaf) element).getElement();
		if (columnIndex != 0) return null;
		return UIUtils.getImage(rodinElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		IRodinElement rodinElement = ((Leaf) element).getElement();
		
		if (columnIndex == 0) {
			if (rodinElement instanceof IUnnamedInternalElement) return "";
			if (rodinElement instanceof IInternalElement) return ((IInternalElement) rodinElement).getElementName();
			return rodinElement.toString();
		}
		
		if (columnIndex == 1) {
			try {
				if (rodinElement instanceof IInternalElement) return ((IInternalElement) rodinElement).getContents();
			}
			catch (RodinDBException e) {
				e.printStackTrace();
			}
			return rodinElement.toString();
		}
		
		return rodinElement.toString();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	public Color getBackground(Object element, int columnIndex) {
		 Display display = Display.getCurrent();
		 
		 if (editor.isNewElement(((Leaf) element).getElement()))
			 return display.getSystemColor(SWT.COLOR_YELLOW);
         return display.getSystemColor(SWT.COLOR_WHITE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
	 */
	public Color getForeground(Object element, int columnIndex) {
		Display display = Display.getCurrent();
		 if (editor.isNewElement(((Leaf) element).getElement()))
			 return display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
        return display.getSystemColor(SWT.COLOR_BLACK);
   }
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
	 */
	public Font getFont(Object element, int columnIndex) {
//		UIUtils.debug("Get fonts");
		return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
	}
		
}
