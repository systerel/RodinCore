package fr.systerel.explorer.navigator.actionProviders;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionConstants;

public class ElementActionProvider extends NavigatorActionProvider {

	/**
	 * Create the actions.
	 */
	@Override
	protected void makeActions() {
		makeDoubleClickAction();
		makeNewProjectAction();
		makeNewComponentAction();
	}

	/* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        // forward doubleClick to doubleClickAction
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
              doubleClickAction);
    }
	
    @Override
	public void fillContextMenu(IMenuManager menu) {
		MenuManager newMenu = new MenuManager("&New");
		newMenu.add(newProjectAction);
		newMenu.add(newComponentAction);
    	IContributionItem[] items = menu.getItems();
    	// put in front
    	if (items.length > 0) {
    		menu.insertBefore(items[1].getId(), newMenu);
    	} else	menu.add(newMenu);
		menu.add(new Separator());
    }	

}
