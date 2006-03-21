package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public abstract class TimerText 
	implements ModifyListener
{
	int lastModify;
	Text text;
	
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
				if (!text.isDisposed()) commit();
			}
		}
	}
	
	public TimerText(Text text) {
		this.text = text;
		text.addModifyListener(this);
	}
	
	abstract public void commit();

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		lastModify = e.time;
		text.getDisplay().timerExec(1000, new TimeRunnable(e.time));
	}
}
