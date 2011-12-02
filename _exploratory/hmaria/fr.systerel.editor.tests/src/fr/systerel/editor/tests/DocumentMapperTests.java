/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.tests;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.jface.text.Position;
import org.eventb.core.IAxiom;
import org.junit.Before;
import org.junit.Test;

import fr.systerel.editor.internal.documentModel.DocumentMapper;

/**
 * @author Nicolas Beauger
 * 
 */
public class DocumentMapperTests {
	private final DocumentMapper mapper = new DocumentMapper();

	private static Position pos(int offset, int length) {
		return new Position(offset, length);
	}

//	private static Position[] posArray(int... offLens) {
//		assertTrue(offLens.length % 2 == 0);
//		final Position[] positions = new Position[offLens.length / 2];
//		for (int i = 0; i < positions.length; i++) {
//			final int offset = offLens[2 * i];
//			final int length = offLens[2 * i + 1];
//			positions[i] = pos(offset, length);
//		}
//		return positions;
//	}

	private void assertFoldingPositions(Position... expected) {
		final Position[] foldingPositions = mapper.getFoldingPositions();
		assertArrayEquals(expected, foldingPositions);
	}

	@Before
	public void init() {
		mapper.reinitialize();
	}

	@Test
	public void testAddEditorSection() {
		mapper.addEditorSection(IAxiom.ELEMENT_TYPE, 0, 10);
		assertFoldingPositions(pos(0, 10));
	}
	
	
}
