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

public class TextTest extends TestCase {

	Text single, multi;

	int pos;

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
		assertEquals("Modify Event (SINGLE): ", 1, pos);

		assertEquals("After inserting (SINGLE): ", 1, multi.getCaretPosition());
	}

	public void testMULTI() {
		System.out.println("Test MULTI");
		multi.insert("a");
		assertEquals("Modify Event (MULTI): ", 1, pos);

		assertEquals("After inserting (MULTI): ", 1, multi.getCaretPosition());

	}

	private class TestListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.widget;
			System.out.println("Text: \"" + text.getText() + "\"");
			System.out.println("Caret Position:" + text.getCaretPosition());

			// This set the position so that it can be checked within those test
			// methods
			pos = text.getCaretPosition();
		}

	}
}
