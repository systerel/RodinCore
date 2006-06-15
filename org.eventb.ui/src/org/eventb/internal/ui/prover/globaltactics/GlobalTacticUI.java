/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover.globaltactics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.prover.IGlobalTactic;

/**
 * @author htson
 *         <p>
 *         This class represent the proof tactic along with the UI information
 *         (image, tips, etc.)
 */
public class GlobalTacticUI {

	private String ID;

	private String image;

	private String tips;

	private String dropdown;

	private IGlobalTactic tactic;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param ID
	 *            The string ID of the tactic
	 * @param image
	 *            The image associated with the tactic
	 * @param tips
	 *            The tip string of the tactic
	 * @param dropdown
	 *            The dropdown ID that the tactic belongs to
	 * @param constructor
	 *            The constructor to create the tactic
	 * @throws IllegalArgumentException
	 *             exception when creating the new instance of the tactic
	 * @throws InstantiationException
	 *             exception when creating the new instance of the tactic
	 * @throws IllegalAccessException
	 *             exception when creating the new instance of the tactic
	 * @throws InvocationTargetException
	 *             exception when creating the new instance of the tactic
	 */
	public GlobalTacticUI(String ID, String image, String tips, String dropdown,
			Constructor constructor) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (ProverUI.DEBUG) {
			System.out.println("ID: " + ID);
			System.out.println("Image: " + image);
			System.out.println("Tips: " + tips);
			System.out.println("Constructor: " + constructor);
			System.out.println("Dropdown: " + dropdown);
		}
		this.ID = ID;
		this.image = image;
		this.tips = tips;
		this.dropdown = dropdown;
		tactic = (IGlobalTactic) constructor.newInstance(new Object[0]);
	}

	/**
	 * Get the image represents this tactic.
	 * <p>
	 * 
	 * @return the image associate with this tactic.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Return the actual tactic.
	 * <p>
	 * 
	 * @return a global proof tactic
	 */
	public IGlobalTactic getTactic() {
		return tactic;
	}

	/**
	 * Tips for this tactic.
	 * <p>
	 * 
	 * @return the tip strings for this tactic
	 */
	public String getTips() {
		return tips;
	}

	/**
	 * Get the dropdown information.
	 * <p>
	 * 
	 * @return the name of the dropdown this tactic belongs to or
	 *         <code>null</code>
	 */
	public String getDropdown() {
		return dropdown;
	}

}
