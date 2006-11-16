package org.eventb.core.pm;

public interface IUSManagerListener {

	public static int REMOVED = 0x1;

	public static int ADDED = 0x2;

	public static int CHANGED = 0x4;
	
	public void USManagerChanged(IUserSupport userSupport, int status);
}
