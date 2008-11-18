/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.contentAssist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.editors.OverlayEditor;

public class RodinContentAssistProcessor implements IContentAssistProcessor {

	private CompletionCalculator calculator;
	
	
	/**
	 * Simple content assist tip closer. The tip is valid in a range
	 * of 5 characters around its popup location.
	 */
	protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

		protected int fInstallOffset;

		/*
		 * @see IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int offset) {
			return Math.abs(fInstallOffset - offset) < 5;
		}

		/*
		 * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer, int offset) {
			fInstallOffset= offset;
		}
		

		public boolean updatePresentation(int offset, TextPresentation presentation) {
			return false;
		}
	}
	
	public RodinContentAssistProcessor(DocumentMapper documentMapper, OverlayEditor overlayEditor) {
		calculator = new CompletionCalculator(documentMapper, overlayEditor);
	}
	
	protected IContextInformationValidator fValidator= new Validator();
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		
		String[] completions = calculator.calculateCompletions(offset);
		ArrayList<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		for (String comp : completions) {
			if (comp.length() > 0) {
				result.add(new CompletionProposal(comp, offset, 0, comp.length()));
			}
		}
//		ICompletionProposal[] result= new ICompletionProposal[1];
//			result[0]= new CompletionProposal("test", offset, 0, 4);
		return result.toArray(new ICompletionProposal[result.size()]);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return new char[] {'.'};
	}

	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return new char[] {'.'};
	}

	public IContextInformationValidator getContextInformationValidator() {
//		return fValidator;
		return null;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}


}
