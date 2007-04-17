package org.eventb.internal.ui.cachehypothesis;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         Implementation of the Proof Control View.
 */
public class CacheHypothesis extends PageBookView implements
		ISelectionProvider, ISelectionChangedListener,
		IUserSupportManagerChangedListener {

	/**
	 * The identifier of the Proof Control View (value
	 * <code>"org.eventb.ui.views.SearchHypothesis"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.CacheHypothesis";

	// The default text when it is not available (depend on the current editor)
	private String defaultText = "Cached Hypothesis is not available";

	public HashMap<IPage, IWorkbenchPart> fPagesToParts;

	public CacheHypothesis() {
		super();
		fPagesToParts = new HashMap<IPage, IWorkbenchPart>();

		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(
				this);
	}

	@Override
	public void dispose() {
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(defaultText);
		// DummyPart part = new DummyPart();
		// fPagesToParts.put(page, part);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a Search Hypothesis Page.
		Object obj = part.getAdapter(ICacheHypothesisPage.class);
		if (obj instanceof ICacheHypothesisPage) {
			ICacheHypothesisPage page = (ICacheHypothesisPage) obj;
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		// There is no content outline
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.ui.part.PageBookView.PageRec)
	 */
	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		ICacheHypothesisPage page = (ICacheHypothesisPage) pageRecord.page;
		page.dispose();
		pageRecord.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			IEditorPart activeEditor = page.getActiveEditor();
			if (activeEditor instanceof ProverUI)
				return activeEditor;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// We only care about Prover UI editors
		return (part instanceof ProverUI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionProvider().addSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		// get the selection from the selection provider
		return getSelectionProvider().getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		getSelectionProvider().removeSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		getSelectionProvider().setSelection(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		getSelectionProvider().selectionChanged(event);
	}

	/*
	 * (non-Javadoc) Method declared on IViewPart. Treat this the same as part
	 * activation.
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		partActivated(part);
	}

	/**
	 * The <code>ContentOutline</code> implementation of this
	 * <code>PageBookView</code> method extends the behavior of its parent to
	 * use the current page as a selection provider.
	 * 
	 * @param pageRec
	 *            the page record containing the page to show
	 */
	@Override
	protected void showPageRec(PageRec pageRec) {
		IPageSite pageSite = getPageSite(pageRec.page);
		ISelectionProvider provider = pageSite.getSelectionProvider();
		if (provider == null && (pageRec.page instanceof IContentOutlinePage))
			// This means that the page did not set a provider during its
			// initialization
			// so for backward compatibility we will set the page itself as the
			// provider.
			pageSite.setSelectionProvider((IContentOutlinePage) pageRec.page);
		super.showPageRec(pageRec);
	}

	private static int counter = 0;

	public void proofStateChanged(IProofStateDelta delta) {
		setContentDescription("Search " + counter++);

		// ISearchHypothesisPage page= null;

		// Object obj = part.getAdapter(ISearchHypothesisPage.class);
		// if (obj instanceof ISearchHypothesisPage) {
		// ISearchHypothesisPage page = (ISearchHypothesisPage) obj;
		// if (page instanceof IPageBookViewPage)
		// initPage((IPageBookViewPage) page);
		// page.createControl(getPageBook());
		// return new PageRec(part, page);
		// }
		// // There is no content outline
		// return null;
		//
		// // detach the previous page.
		// ISearchResultPage currentPage= (ISearchResultPage) getCurrentPage();
		// Object uiState= currentPage.getUIState();
		// if (fCurrentSearch != null) {
		// if (uiState != null)
		// fSearchViewStates.put(fCurrentSearch, uiState);
		// }
		// currentPage.setInput(null, null);
		//		
		// // switch to a new page
		// if (page != null && page != currentPage) {
		// IWorkbenchPart part= (IWorkbenchPart) fPagesToParts.get(page);
		// if (part == null) {
		// part= new DummyPart();
		// fPagesToParts.put(page, part);
		// fPartsToPages.put(part, page);
		// page.setViewPart(this);
		// }
		// partActivated(part);
		// }
		//		
		// // connect to the new pages
		// fCurrentSearch= search;
		// if (page != null)
		// page.setInput(search, fSearchViewStates.get(search));
		// updateLabel();
		// updateCancelAction();

	}

	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {

		// IUserSupportDelta affectedUserSupport = ProverUIUtils
		// .getUserSupportDelta(delta, userSupport);

		// ISearchHypothesisPage page= null;

		// Object obj = part.getAdapter(ISearchHypothesisPage.class);
		// if (obj instanceof ISearchHypothesisPage) {
		// ISearchHypothesisPage page = (ISearchHypothesisPage) obj;
		// if (page instanceof IPageBookViewPage)
		// initPage((IPageBookViewPage) page);
		// page.createControl(getPageBook());
		// return new PageRec(part, page);
		// }
		// // There is no content outline
		// return null;
		//
		// // detach the previous page.
		// ISearchResultPage currentPage= (ISearchResultPage) getCurrentPage();
		// Object uiState= currentPage.getUIState();
		// if (fCurrentSearch != null) {
		// if (uiState != null)
		// fSearchViewStates.put(fCurrentSearch, uiState);
		// }
		// currentPage.setInput(null, null);
		//		
		// // switch to a new page
		// if (page != null && page != currentPage) {
		// IWorkbenchPart part= (IWorkbenchPart) fPagesToParts.get(page);
		// if (part == null) {
		// part= new DummyPart();
		// fPagesToParts.put(page, part);
		// fPartsToPages.put(part, page);
		// page.setViewPart(this);
		// }
		// partActivated(part);
		// }
		//		
		// // connect to the new pages
		// fCurrentSearch= search;
		// if (page != null)
		// page.setInput(search, fSearchViewStates.get(search));
		// updateLabel();
		// updateCancelAction();

	}

	public Set<Predicate> getSelectedHyps() {
		ICacheHypothesisPage currentPage = ((ICacheHypothesisPage) this.getCurrentPage());
		if (currentPage != null)
			return currentPage.getSelectedHyps();
		return null;
	}

	public IUserSupport getUserSupport() {
		ICacheHypothesisPage currentPage = ((ICacheHypothesisPage) this.getCurrentPage());
		if (currentPage != null)
			return currentPage.getUserSupport();
		return null;
	}
}