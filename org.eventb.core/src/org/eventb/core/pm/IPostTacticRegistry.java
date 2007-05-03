package org.eventb.core.pm;

public interface IPostTacticRegistry {

	public abstract boolean isDeclared(String tacticID);

	public abstract String[] getTacticIDs();

}