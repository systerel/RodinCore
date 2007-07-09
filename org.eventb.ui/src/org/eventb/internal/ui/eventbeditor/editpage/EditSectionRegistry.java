package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class EditSectionRegistry {

	public static EditSectionRegistry instance = null;

	public static String IDENTIFIER_ATTRIBUTE_ID = "org.eventb.ui.identifier";
	
	public static String LABEL_ATTRIBUTE_ID = "org.eventb.ui.label";
	
	private EditSectionRegistry() {
		// Singleton to hide contructor
	}

	public static EditSectionRegistry getDefault() {
		if (instance == null)
			instance = new EditSectionRegistry();
		return instance;
	}

	// Map from element types to list of sections
	private Map<IElementType<?>, SectionsInfo> sectionRegistry = null;

	private static final String EDITSECTIONS_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editSections";

	class SectionsInfo {
		// Map from element types to list of sections
		List<SectionInfo> unsortedSections;

		LinkedHashMap<IElementType<?>, SectionInfo> sections;

		public SectionsInfo() {
			unsortedSections = new ArrayList<SectionInfo>();
		}

		public void addSection(SectionInfo info) {
			unsortedSections.add(info);
		}

		public void sortSections() {
			boolean sorted = false;
			int size = unsortedSections.size();
			while (!sorted) {
				sorted = true;
				for (int i = 0; i < size - 1; i++) {
					SectionInfo curr = unsortedSections.get(i);
					SectionInfo next = unsortedSections.get(i + 1);
					if (curr.getPriority() > next.getPriority()) {
						// Swap element
						unsortedSections.set(i, next);
						unsortedSections.set(i + 1, curr);
						sorted = false;
					}
				}
			}

			sections = new LinkedHashMap<IElementType<?>, SectionInfo>(
					unsortedSections.size());
			for (SectionInfo info : unsortedSections) {
				sections.put(info.getType(), info);
			}
		}

		public IInternalElementType<?>[] getChildrenTypes(
				IElementType<?> parentType) {
			Set<IElementType<?>> types = sections.keySet();
			return types.toArray(new IInternalElementType<?>[types.size()]);
		}

		public String getPrefix(IElementType<?> type) {
			SectionInfo info = sections.get(type);
			return info.getPrefix();
		}

		public String getPostfix(IElementType<?> type) {
			SectionInfo info = sections.get(type);
			return info.getPostfix();
		}

		public boolean isEnable(IRodinElement parent, IElementType<?> type) {
			SectionInfo info = sections.get(type);
			return info.isEnable(parent);
		}
	}

	private class SectionInfo {
		IConfigurationElement config;

		private final String id;

		private int priority;

		private static final int DEFAULT_PRIORITY = 10000;

		public SectionInfo(IConfigurationElement config) {
			this.config = config;
			// TODO check that id is present.
			this.id = config.getAttribute("id");
			priority = readPriority();
		}

		public boolean isEnable(IRodinElement parent) {
			return true;
		}

		public String getPrefix() {
			return config.getAttribute("prefix");
		}

		public String getPostfix() {
			return config.getAttribute("postfix");
		}

		public IElementType<?> getType() {
			String typeStr = config.getAttribute("type");
			return RodinCore.getElementType(typeStr);
		}

		private int readPriority() {
			String priorityValue = config.getAttribute("priority");
			if (priorityValue == null) {
				UIUtils.log(null, "Missing priority attribute (using default),"
						+ " for editor page extension " + id);
				return DEFAULT_PRIORITY;
			}
			try {
				return Integer.parseInt(priorityValue);
			} catch (NumberFormatException e) {
				UIUtils.log(e, "Illegal priority " + priorityValue
						+ ", using default instead,"
						+ " for editor page extension " + id);
				return DEFAULT_PRIORITY;
			}
		}

		public int getPriority() {
			return this.priority;
		}

	}

	private synchronized void loadSectionRegistry() {
		if (sectionRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		sectionRegistry = new HashMap<IElementType<?>, SectionsInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITSECTIONS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			if (!configuration.getName().equals("section"))
				continue;

			SectionInfo info = new SectionInfo(configuration);
			String parentTypeStr = configuration.getAttribute("parentType");
			IElementType<?> parentType = RodinCore.getElementType(parentTypeStr);
			addSection(parentType, info);
		}

		sortSections();
	}

	private void sortSections() {
		assert sectionRegistry != null;

		for (IElementType<?> parentType : sectionRegistry.keySet()) {
			SectionsInfo infos = sectionRegistry.get(parentType);
			infos.sortSections();
		}
	}

	public String getSectionName(IConfigurationElement section) {
		return section.getAttribute("id");
	}

	private synchronized void addSection(IElementType<?> parentType,
			SectionInfo info) {
		assert sectionRegistry != null;

		SectionsInfo infos = sectionRegistry.get(parentType);
		if (infos == null) {
			infos = new SectionsInfo();
			sectionRegistry.put(parentType, infos);
		}
		infos.addSection(info);
	}

	public synchronized IInternalElementType<? extends IInternalElement>[] getChildrenTypes(
			IElementType<?> parentType) {
		if (sectionRegistry == null)
			loadSectionRegistry();

		SectionsInfo infos = sectionRegistry.get(parentType);
		if (infos != null) {
			return infos.getChildrenTypes(parentType);
		}
		return new IInternalElementType<?>[0];
	}

	public synchronized String getPrefix(IElementType<?> parentType,
			IElementType<?> type) {
		if (sectionRegistry == null)
			loadSectionRegistry();

		SectionsInfo infos = sectionRegistry.get(parentType);
		if (infos != null) {
			return infos.getPrefix(type);
		}
		return null;
	}

	public synchronized String getPostfix(IElementType<?> parentType,
			IElementType<?> type) {
		if (sectionRegistry == null)
			loadSectionRegistry();

		SectionsInfo infos = sectionRegistry.get(parentType);
		if (infos != null) {
			return infos.getPostfix(type);
		}
		return null;
	}

	public synchronized boolean isEnable(IRodinElement parent, IElementType<?> type) {
		if (sectionRegistry == null)
			loadSectionRegistry();

		SectionsInfo infos = sectionRegistry.get(parent.getElementType());
		if (infos != null) {
			return infos.isEnable(parent, type);
		}
		return false;
	}

	Map<IElementType<?>, AttributesInfo> attributeRegistry;

	class AttributesInfo {
		List<AttributeInfo> attributeInfos;

		public AttributesInfo() {
			attributeInfos = new ArrayList<AttributeInfo>();
		}

		public int getNumAttributes() {
			return attributeInfos.size();
		}

		public void addAttribute(AttributeInfo info) {
			attributeInfos.add(info);
		}

		public IEditComposite[] createAttributeComposites() {
			IEditComposite[] result = new IEditComposite[attributeInfos.size()];
			for (int i = 0; i < attributeInfos.size(); ++i) {
				result[i] = attributeInfos.get(i).createAttributeComposite();
			}
			return result;
		}

		public void createDefaultAttributes(IEventBEditor<?> editor,
				IInternalElement element, IProgressMonitor monitor) throws RodinDBException {
			for (AttributeInfo attributeInfo : attributeInfos) {
				attributeInfo.createDefaultAttribute(editor, element, monitor);
			}
		}

		public String getDefaultPrefix(IInternalElementType<?> type, String attributeID) {
			for (AttributeInfo attributeInfo : attributeInfos) {
				if (attributeID.equals(attributeInfo.getAttributeId())) {
					return attributeInfo.getDefaultPrefix(type); 
				}
			}
			return null;
		}

	}

	private class AttributeInfo {
		private IConfigurationElement config;
		private Map<IElementType<?>, IAttributeFactory> factories = null;
		private Map<IElementType<?>, String> defaultPrefixes = null;

		private IAttributeEditor attributeEditor = null;
		
		public AttributeInfo(IConfigurationElement config) {
			this.config = config;
		}

		public String getDefaultPrefix(IInternalElementType<?> type) {
			if (defaultPrefixes == null) {
				loadPrefixes();
			}
			
			return defaultPrefixes.get(type);
		}

		private void loadPrefixes() {
			defaultPrefixes = new HashMap<IElementType<?>, String>();
			
			IConfigurationElement[] typeIDs = config.getChildren("type");
			for (IConfigurationElement typeID : typeIDs) {
				String id = typeID.getAttribute("id");
				try {
					IElementType<?> type = RodinCore.getElementType(id);
					String defaultPrefix = typeID.getAttribute("defaultPrefix");
					defaultPrefixes.put(type, defaultPrefix);
				} catch (IllegalArgumentException e) {
					String message = "Illegal element type " + id
							+ ", ignore this configuration";
					UIUtils.log(e, message);
					if (UIUtils.DEBUG) {
						System.out.println(message);
					}
					continue;
				}
			}
		}

		public void createDefaultAttribute(IEventBEditor<?> editor,
				IInternalElement element,
				IProgressMonitor monitor) throws RodinDBException {
			if (factories == null) {
				loadFactory();
			}

			IAttributeFactory factory = factories.get(element.getElementType());
			if (factory != null)
				factory.createDefaulAttribute(editor, element, monitor);
		}

		private void loadFactory() {
			factories = new HashMap<IElementType<?>, IAttributeFactory>();
			
			IConfigurationElement[] typeIDs = config.getChildren("type");
			for (IConfigurationElement typeID : typeIDs) {
				String id = typeID.getAttribute("id");
				try {
					IElementType<?> type = RodinCore.getElementType(id);
					try {
						IAttributeFactory factory = (IAttributeFactory) typeID
								.createExecutableExtension("defaultValue");
						factories.put(type, factory);
					} catch (CoreException e) {
						continue;
					}
				} catch (IllegalArgumentException e) {
					String message = "Illegal element type " + id
							+ ", ignore this configuration";
					UIUtils.log(e, message);
					if (UIUtils.DEBUG) {
						System.out.println(message);
					}
					continue;
				}
			}
		}

		public String getAttributeName() {
			return config.getAttribute("name");
		}

		public String getAttributeId() {
			return config.getAttribute("id");
		}

		public IEditComposite createAttributeComposite() {
			try {
				IEditComposite editComposite;
				String prefix = config.getAttribute("prefix");
				String postfix = config.getAttribute("postfix");

				String widget = config.getAttribute("widget");
				if (attributeEditor == null) {
					attributeEditor = (IAttributeEditor) config
					.createExecutableExtension("class");					
				}
				if (widget.equals("text")) {
					editComposite = new TextEditComposite(attributeEditor);
				}
				else if (widget.equals("combo")) {
					editComposite = new CComboEditComposite(attributeEditor);
				}
				else {
					return null;
				}
				editComposite.setPrefix(prefix);
				editComposite.setPostfix(postfix);
				editComposite.setFillHorizontal(config.getAttribute(
						"horizontalExpand").equalsIgnoreCase("true"));
				return editComposite;
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private synchronized void loadAttributeRegistry() {
		if (attributeRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		attributeRegistry = new HashMap<IElementType<?>, AttributesInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITSECTIONS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			if (!configuration.getName().equals("attribute"))
				continue;
			AttributeInfo info = new AttributeInfo(configuration);
			IConfigurationElement[] types = configuration.getChildren("type");
			for (IConfigurationElement type : types) {
				String id = type.getAttribute("id");
				IElementType<?> elementType;
				try {
					elementType = RodinCore.getElementType(id);
				} catch (IllegalArgumentException e) {
					String message = "Illegal element type " + id
							+ ", ignore this configuration";
					UIUtils.log(e, message);
					if (UIUtils.DEBUG) {
						System.out.println(message);
					}
					continue;
				}
				addAttribute(elementType, info);
			}
		}
	}

	private void addAttribute(IElementType<?> elementType, AttributeInfo info) {
		assert attributeRegistry != null;

		AttributesInfo infos = attributeRegistry.get(elementType);
		if (infos == null) {
			infos = new AttributesInfo();
			attributeRegistry.put(elementType, infos);
		}
		infos.addAttribute(info);
	}

	public synchronized int getNumAttributes(IElementType<?> type) {
		if (attributeRegistry == null)
			loadAttributeRegistry();

		AttributesInfo info = attributeRegistry.get(type);
		if (info == null) {
			return 0;
		}

		return info.getNumAttributes();
	}

	public synchronized IEditComposite[] createAttributeComposites(IElementType<?> type) {
		if (attributeRegistry == null)
			loadAttributeRegistry();

		AttributesInfo info = attributeRegistry.get(type);
		if (info == null) {
			return new IEditComposite[0];
		}

		return info
				.createAttributeComposites();
	}

	private Map<IElementType<?>, ActionsInfo> actionRegistry;

	class ActionsInfo {
		Map<String, ActionInfo> actions;

		public ActionsInfo() {
			actions = new LinkedHashMap<String, ActionInfo>();
		}

		public void addAction(String id, ActionInfo info) {
			actions.put(id, info);
		}

		public String[] getActions() {
			Set<String> actionIDs = actions.keySet();
			return actionIDs.toArray(new String[actionIDs.size()]);
		}

		public void run(String actionID, IEventBEditor<?> editor,
				IInternalParent parent, IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			ActionInfo info = actions.get(actionID);
			info.run(editor, parent, element, type);
		}

		public String getToolTip(String actionID) {
			ActionInfo info = actions.get(actionID);
			return info.getToolTip();
		}

		public String getName(String actionID) {
			ActionInfo info = actions.get(actionID);
			return info.getName();
		}

		public boolean isApplicable(String actionID, IInternalParent parent,
				IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			ActionInfo info = actions.get(actionID);
			return info.isApplicable(parent, element, type);
		}

	}

	private synchronized void loadActionRegistry() {
		if (actionRegistry != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}

		actionRegistry = new HashMap<IElementType<?>, ActionsInfo>();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(EDITSECTIONS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		for (IConfigurationElement configuration : configurations) {
			if (!configuration.getName().equals("action"))
				continue;
			String actionID = configuration.getAttribute("id");
			ActionInfo info = new ActionInfo(configuration);
			IConfigurationElement[] types = configuration.getChildren("type");
			for (IConfigurationElement type : types) {
				String id = type.getAttribute("id");
				IElementType<?> elementType;
				try {
					elementType = RodinCore.getElementType(id);
				} catch (IllegalArgumentException e) {
					String message = "Illegal element type " + id
							+ ", ignore this configuration";
					UIUtils.log(e, message);
					if (UIUtils.DEBUG) {
						System.out.println(message);
					}
					continue;
				}
				addAction(elementType, actionID, info);
			}
		}
	}

	private class ActionInfo {
		private IConfigurationElement config;

		private IEditAction editAction = null;

		public ActionInfo(IConfigurationElement config) {
			this.config = config;
		}

		public boolean isApplicable(IInternalParent parent,
				IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			if (editAction == null)
				editAction = (IEditAction) config
						.createExecutableExtension("class");
			return editAction.isApplicable(parent, element, type);
		}

		public String getName() {
			return config.getAttribute("name");
		}

		public String getToolTip() {
			return config.getAttribute("tooltip");
		}

		public void run(IEventBEditor<?> editor, IInternalParent parent,
				IInternalElement element,
				IInternalElementType<IInternalElement> type)
				throws CoreException {
			if (editAction == null)
				editAction = (IEditAction) config
						.createExecutableExtension("class");
			editAction.run(editor, parent, element, type);
		}

		public String getId() {
			return config.getAttribute("id");
		}

	}

	public synchronized String[] getActions(IElementType<?> type) {
		if (actionRegistry == null)
			loadActionRegistry();

		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return new String[0];
		}

		return info.getActions();
	}

	private synchronized void addAction(IElementType<?> type, String id,
			ActionInfo info) {
		assert actionRegistry != null;

		ActionsInfo infos = actionRegistry.get(type);
		if (infos == null) {
			infos = new ActionsInfo();
			actionRegistry.put(type, infos);
		}
		infos.addAction(id, info);
	}

	public void run(String actionID, IEventBEditor<?> editor,
			IInternalParent parent, IInternalElement element,
			IInternalElementType<IInternalElement> type) throws CoreException {
		if (actionRegistry == null)
			loadActionRegistry();

		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return;
		}

		info.run(actionID, editor, parent, element, type);
	}

	public synchronized String getToolTip(String actionID, IElementType<?> type) {
		if (actionRegistry == null)
			loadActionRegistry();
		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return null;
		}

		return info.getToolTip(actionID);
	}

	public synchronized String getName(String actionID, IElementType<?> type) {
		if (actionRegistry == null)
			loadActionRegistry();
		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return null;
		}

		return info.getName(actionID);
	}

	public synchronized boolean isApplicable(String actionID,
			IInternalParent parent, IInternalElement element,
			IInternalElementType<IInternalElement> type) throws CoreException {
		if (actionRegistry == null)
			loadActionRegistry();
		ActionsInfo info = actionRegistry.get(type);
		if (info == null) {
			return false;
		}

		return info.isApplicable(actionID, parent, element, type);
	}

	public synchronized IRodinElement createElement(final IEventBEditor<?> editor,
			final IInternalParent parent,
			final IInternalElementType<? extends IInternalElement> type,
			final IInternalElement sibling) throws CoreException {
		if (attributeRegistry == null)
			loadAttributeRegistry();

		String newName = UIUtils.getFreeChildName(editor, parent, type);
		final IInternalElement newElement = parent.getInternalElement(
				(IInternalElementType<? extends IRodinElement>) type, newName);
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				newElement.create(sibling, monitor);
				AttributesInfo attributesInfo = attributeRegistry.get(type);
				attributesInfo.createDefaultAttributes(editor, newElement,
						monitor);
			}
			
		}, new NullProgressMonitor());

		return newElement;
	}

	public synchronized String getDefaultPrefix(IInternalElementType<?> type, String attributeID) {
		if (attributeRegistry == null)
			loadAttributeRegistry();

		AttributesInfo attributesInfo = attributeRegistry.get(type);
		if (attributesInfo != null)
			return attributesInfo.getDefaultPrefix(type, attributeID);
		return null;
	}

}
