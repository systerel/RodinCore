package org.eventb.internal.ui;


public class ElementUI {

	private Class clazz;

	private String pluginID;
	
	private String path;
	
	public ElementUI(String pluginID, String path, Class clazz) {
		this.pluginID = pluginID;
		this.path = path;
		this.clazz = clazz;
	}

	public Class getElementClass() {
		return clazz;
	}

	public String getPluginID() {
		return pluginID;
	}

	public String getPath() {
		return path;
	}
}