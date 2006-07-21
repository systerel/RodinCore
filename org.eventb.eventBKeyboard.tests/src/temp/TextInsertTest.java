package temp;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextInsertTest extends TestCase {

	Text single, multi;

	@Override
	protected void setUp() throws Exception {
		Display display = Display.getDefault();
		Shell shell = display.getActiveShell();
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
		shell.open();

		super.setUp();
	}

	public void testSINGLE() {
		System.out.println("Test SINGLE");
		single.insert("a");
		single.insert("b");
		single.insert("c");
		single.insert("d");
		single.insert("e");
		assertEquals("SINGLE: ", "a*e", single.getText());
	}

	public void testMULTI() {
		System.out.println("Test MULTI");
		multi.insert("a");
		multi.insert("b");
		multi.insert("c");
		multi.insert("d");
		multi.insert("e");
		assertEquals("MULTI: ", "a*e", multi.getText());
	}

	private class TestListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.widget;
			 // Remove itself to avoid recursive call
			text.removeModifyListener(this);
			System.out.println("Text: \"" + text.getText() + "\"");
			
			System.out.println("Caret Position before:" + text.getCaretPosition());

			if (text.getText().length() >= 4) {
				text.setSelection(1,4);
				text.insert("*");
				text.setSelection(2);
				assertEquals("Text: ", "a*", text.getText());
				assertEquals("Position: ", 2, text.getCaretPosition());
			}
			// This set the position so that it can be checked within those test
			// methods
			System.out.println("Text after:" + text.getText());
			System.out.println("Caret Position after:" + text.getCaretPosition());
			text.addModifyListener(this);
		}

	}
}
