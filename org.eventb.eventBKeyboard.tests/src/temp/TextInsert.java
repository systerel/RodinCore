package temp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextInsert {

	private static Text single, multi;

	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		single = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 20;
		gd.widthHint = 300;
		single.setLayoutData(gd);
		single.addModifyListener(new TestListener());
		multi = new Text(shell, SWT.MULTI | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.widthHint = 300;
		multi.setLayoutData(gd);
		multi.addModifyListener(new TestListener());
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {

			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	private static class TestListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.widget;
			// Remove itself to avoid recursive call
			text.removeModifyListener(this);
			System.out.println("Text: \"" + text.getText() + "\"");

			System.out.println("Caret Position before:"
					+ text.getCaretPosition());

			if (text.getText().length() >= 4) {
				text.setSelection(1, 4);
				text.insert("*");
				text.setSelection(2);
			}
			System.out.println("Text after:" + text.getText());
			System.out.println("Caret Position after:"
					+ text.getCaretPosition());
			text.addModifyListener(this);
		}

	}
}
