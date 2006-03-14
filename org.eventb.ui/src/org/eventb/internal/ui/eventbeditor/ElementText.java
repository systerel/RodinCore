package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;
import org.rodinp.core.IRodinElement;

public abstract class ElementText
{
	protected Text text;
	protected IRodinElement element;
	
	public ElementText(Text text, IRodinElement element) {
		this.text = text;
		this.element = element;
		
		text.addFocusListener(new FocusListener() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
			 */
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
			 */
			public void focusLost(FocusEvent e) {
				commit();
			}
			
		});
	}
	
	public abstract void commit();
	
	protected void setElement(IRodinElement element) {
		this.element = element;
	}
}
