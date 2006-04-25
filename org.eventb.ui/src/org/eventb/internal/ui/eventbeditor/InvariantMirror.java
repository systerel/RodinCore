/**
 * 
 */
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eventb.internal.ui.EventBUIPlugin;

/**
 * @author htson
 *
 */
public class InvariantMirror 
	extends EventBMirror
{
	/**
	 * The plug-in identifier of the Proof Tree UI (value
	 * <code>"org.eventb.ui.views.ProofTreeUI"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID +".views.InvariantMirror";

	private String defaultText = "A mirror of the invariants is not available";
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	@Override
	protected IPage createDefaultPage(PageBook book) {
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(defaultText);
        return page;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
//		 Try to get an obligation list page.
        Object obj = part.getAdapter(IInvariantMirrorPage.class);
        if (obj instanceof IInvariantMirrorPage) {
            IInvariantMirrorPage page = (IInvariantMirrorPage) obj;
            if (page instanceof IPageBookViewPage)
                initPage((IPageBookViewPage) page);
            page.createControl(getPageBook());
            return new PageRec(part, page);
        }
        // There is no content outline
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
        if (page != null)
            if (page.getActiveEditor() instanceof EventBMachineEditor)
            	return page.getActiveEditor();

		return null;
	}
}