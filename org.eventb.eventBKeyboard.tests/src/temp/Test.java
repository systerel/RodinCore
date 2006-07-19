package temp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Test {
	
	public static void main(String [] args) {
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		Text single = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 20;
		gd.widthHint = 300;
		single.setLayoutData(gd);
		single.addModifyListener(new TestListener());
		Text multi = new Text(shell, SWT.MULTI | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.widthHint = 300;
		multi.setLayoutData(gd);
		multi.addModifyListener(new TestListener());
		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	private static class TestListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.widget;
			System.out.println(e.toString());
			System.out.println("Text: \"" + text.getText() +"\"");
			System.out.println("Caret Position:" + text.getCaretPosition());
			System.out.println("Caret Line Number: " + text.getCaretLineNumber());
			System.out.println("Caret Location: " + text.getCaretLocation());
		}
		
	}
}
