/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.documentModel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitionerExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;

import fr.systerel.editor.editors.RodinConfiguration;
import fr.systerel.editor.editors.RodinConfiguration.ContentType;

/**
 * Partitions a document according to the intervals.
 * Parts of this class are copied from {@link FastPartitioner}
 */
public class RodinPartitioner implements IDocumentPartitioner, IDocumentPartitionerExtension,IDocumentPartitionerExtension2, IDocumentPartitionerExtension3  {

	private DocumentMapper mapper;
	/** The partitioner's document */
	protected IDocument fDocument;

	/**
	 * The position category this partitioner uses to store the document's partitioning information.
	 */
	private static final String CONTENT_TYPES_CATEGORY= "__content_types_category"; //$NON-NLS-1$
	/** The legal content types of this partitioner */
	protected final String[] fLegalContentTypes;
	/** The document length before a document change occurred */
	protected int fPreviousDocumentLength;
	/** The position updater used to for the default updating of partitions */
	protected final DefaultPositionUpdater fPositionUpdater;
	/** The offset at which the first changed partition starts */
	protected int fStartOffset;
	/** The offset at which the last changed partition ends */
	protected int fEndOffset;
	/**The offset at which a partition has been deleted */
	protected int fDeleteOffset;
	
	/** for correcting positions */
	protected int fNewOffSet;
	protected int fNewLength;
	protected int correctionIndex;
	
	/**
	 * The position category this partitioner uses to store the document's partitioning information.
	 */
	private final String fPositionCategory;
	/**
	 * The active document rewrite session.
	 */
	private DocumentRewriteSession fActiveRewriteSession;
	/**
	 * Flag indicating whether this partitioner has been initialized.
	 */
	private boolean fIsInitialized= false;
	/**
	 * The cached positions from our document, so we don't create a new array every time
	 * someone requests partition information.
	 */
	private Position[] fCachedPositions= null;
	
	
	public RodinPartitioner(DocumentMapper documentMapper,  ContentType[] contentTypes) {
		this.mapper = documentMapper;
		fLegalContentTypes= new String[contentTypes.length];
		for (int i = 0; i < contentTypes.length; i++) {
			fLegalContentTypes[i] = contentTypes[i].getName();
		}
		fPositionCategory= CONTENT_TYPES_CATEGORY + hashCode();
		fPositionUpdater= new DefaultPositionUpdater(fPositionCategory);
	}

	public ITypedRegion[] computePartitioning(int offset, int length) {
		return computePartitioning(offset, length, false);
	}

	public void connect(IDocument document) {
		connect(document, false);
		
	}

	public void disconnect() {
		try {
			fDocument.removePositionCategory(fPositionCategory);
		} catch (BadPositionCategoryException x) {
			// do nothing
		}
		
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		
	}

	public boolean documentChanged(DocumentEvent event) {
		if (fIsInitialized) {
			IRegion region= documentChanged2(event);
			return (region != null);
		}
		return false;
	}

	public String getContentType(int offset) {
		return getContentType(offset, false);
	}

	public String[] getLegalContentTypes() {
		return TextUtilities.copy(fLegalContentTypes);
	}

	public ITypedRegion getPartition(int offset) {
		return getPartition(offset, false);
	}

	public ITypedRegion[] computePartitioning(int offset, int length,
			boolean includeZeroLengthPartitions) {
		checkInitialization();
		List<ITypedRegion> list= new ArrayList<ITypedRegion>();

		try {

			int endOffset= offset + length;

			Position[] category= getPositions();

			TypedPosition previous= null, current= null;
			int start, end, gapOffset;
			Position gap= new Position(0);

			int startIndex= getFirstIndexEndingAfterOffset(category, offset);
			int endIndex= getFirstIndexStartingAfterOffset(category, endOffset);
			for (int i= startIndex; i < endIndex; i++) {

				current= (TypedPosition) category[i];

				gapOffset= (previous != null) ? previous.getOffset() + previous.getLength() : 0;
				if (current.getOffset() - gapOffset >= 0) {
					gap.setOffset(gapOffset);
					gap.setLength(current.getOffset() - gapOffset);
					if ((includeZeroLengthPartitions && overlapsOrTouches(gap, offset, length)) ||
							(gap.getLength() > 0 && gap.overlapsWith(offset, length))) {
						start= Math.max(offset, gapOffset);
						end= Math.min(endOffset, gap.getOffset() + gap.getLength());
						list.add(new TypedRegion(start, end - start, IDocument.DEFAULT_CONTENT_TYPE));
					}
				}

				if (current.overlapsWith(offset, length)) {
					start= Math.max(offset, current.getOffset());
					end= Math.min(endOffset, current.getOffset() + current.getLength());
					if (end- start > 0 || includeZeroLengthPartitions) {
						list.add(new TypedRegion(start, end - start, current.getType()));
					}
				}

				previous= current;
			}

			if (previous != null) {
				gapOffset= previous.getOffset() + previous.getLength();
				gap.setOffset(gapOffset);
				gap.setLength(fDocument.getLength() - gapOffset);
				if ((includeZeroLengthPartitions && overlapsOrTouches(gap, offset, length)) ||
						(gap.getLength() > 0 && gap.overlapsWith(offset, length))) {
					start= Math.max(offset, gapOffset);
					end= Math.min(endOffset, fDocument.getLength());
					list.add(new TypedRegion(start, end - start, IDocument.DEFAULT_CONTENT_TYPE));
				}
			}

			if (list.isEmpty())
				list.add(new TypedRegion(offset, length, IDocument.DEFAULT_CONTENT_TYPE));

		} catch (BadPositionCategoryException ex) {
			// Make sure we clear the cache
			clearPositionCache();
		} catch (RuntimeException ex) {
			// Make sure we clear the cache
			clearPositionCache();
			throw ex;
		}

		TypedRegion[] result= new TypedRegion[list.size()];
		list.toArray(result);
		return result;
	}

	public String getContentType(int offset, boolean preferOpenPartitions) {
		ITypedRegion partition = getPartition(offset, preferOpenPartitions);
		if (partition != null) {
			return partition.getType();
		}
		return null;
	}

	public String[] getManagingPositionCategories() {
		// TODO Auto-generated method stub
		return new String[] {fPositionCategory};
	}

	public ITypedRegion getPartition(int offset, boolean preferOpenPartitions) {
		
		checkInitialization();

		try {

			Position[] category = getPositions();

			if (category == null || category.length == 0)
				return new TypedRegion(0, fDocument.getLength(), IDocument.DEFAULT_CONTENT_TYPE);

			int index= fDocument.computeIndexInCategory(fPositionCategory, offset);

			if (index < category.length) {
				TypedPosition next= (TypedPosition) category[index];

				if (preferOpenPartitions) {
					//check if there is a non zero open partition ending at offset:
					if (index > 0) {
						TypedPosition previous= (TypedPosition) category[index-1];
						if (previous.getOffset() + previous.getLength() == offset){
							final String prevTypeName = previous.getType();
							final ContentType prevType = RodinConfiguration
									.getContentType(prevTypeName);
							//the editable types are considered open partitions
							if (prevType.isEditable())  {
								return new TypedRegion(previous.getOffset(), previous.getLength(), prevTypeName);
							}
						}
					}
					
					//check if there is a open partition starting at offset:
					if (next.getOffset() == offset){
						final String nextTypeName = next.getType();
						final ContentType nextType = RodinConfiguration
								.getContentType(nextTypeName);
						//the editable types are considered open partitions
						if (nextType.isEditable())  {
							return new TypedRegion(next.getOffset(), next.getLength(), next.getType());
						}
					}
					if (index +1 < category.length) {
						next= (TypedPosition) category[index +1];
						if (next.getOffset() == offset){
							final String nextTypeName = next.getType();
							final ContentType nextType = RodinConfiguration
									.getContentType(nextTypeName);
							//the editable types are considered open partitions
							if (nextType.isEditable())  {
								return new TypedRegion(next.getOffset(), next.getLength(), next.getType());
							}
						}
					}
					next= (TypedPosition) category[index];
					
				}
				
				
				if (offset == next.offset)
					return new TypedRegion(next.getOffset(), next.getLength(), next.getType());

				if (index == 0)
					return new TypedRegion(0, next.offset, IDocument.DEFAULT_CONTENT_TYPE);

				TypedPosition previous= (TypedPosition) category[index - 1];
				if (previous.includes(offset))
					return new TypedRegion(previous.getOffset(), previous.getLength(), previous.getType());

				int endOffset= previous.getOffset() + previous.getLength();
				return new TypedRegion(endOffset, next.getOffset() - endOffset, IDocument.DEFAULT_CONTENT_TYPE);
			}

			TypedPosition previous= (TypedPosition) category[category.length - 1];
			if (previous.includes(offset))
				return new TypedRegion(previous.getOffset(), previous.getLength(), previous.getType());

			int endOffset= previous.getOffset() + previous.getLength();
			return new TypedRegion(endOffset, fDocument.getLength() - endOffset, IDocument.DEFAULT_CONTENT_TYPE);

		} catch (BadPositionCategoryException x) {
		} catch (BadLocationException x) {
		}

		return new TypedRegion(0, fDocument.getLength(), IDocument.DEFAULT_CONTENT_TYPE);
		
	}

	public void connect(IDocument document, boolean delayInitialization) {
		fDocument = document;
		fDocument.addPositionCategory(fPositionCategory);

		fIsInitialized= false;
		if (!delayInitialization)
			checkInitialization();
		
	}

	public DocumentRewriteSession getActiveRewriteSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public void startRewriteSession(DocumentRewriteSession session)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		
	}

	public void stopRewriteSession(DocumentRewriteSession session) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * There should never be more than one partition affected.
	 */
	public IRegion documentChanged2(DocumentEvent event) {
		if (!fIsInitialized)
			return null;

		Assert.isTrue(event.getDocument() == fDocument);
		//if the document changed from the beginning, restart from scratch by initializing
		if (event.getOffset() == 0) {
			try {
				fDocument.removePositionCategory(fPositionCategory);
				fDocument.addPositionCategory(fPositionCategory);
				initialize();
			} catch (BadPositionCategoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
	
				boolean needsCorrection = calculateCorrection(event);
				
				//this handles changes of existing partitions
				//currently there can be no new partitions.
				fPositionUpdater.update(event);

				if (needsCorrection) {
					correctPosition();
				}

				
			} finally {
				clearPositionCache();
			}
		}
		return new Region(event.getOffset(), event.getText().length());
		
	}


	/**
	 * Calls {@link #initialize()} if the receiver is not yet initialized.
	 */
	protected final void checkInitialization() {
		if (!fIsInitialized)
			initialize();
	}
	
	/**
	 * Performs the initial partitioning of the partitioner's document.
	 * <p>
	 * May be extended by subclasses.
	 * </p>
	 */
	protected void initialize() {
		fIsInitialized= true;
		clearPositionCache();
		
		ArrayList<Interval> intervals = mapper.getIntervals();
		int last_end = 0;
		for (Interval interval : intervals) {
			try {
				TypedPosition p;
				if (last_end < interval.getOffset() ) {
					p = new TypedPosition(last_end, interval.getOffset()-(last_end), RodinConfiguration.LABEL_TYPE.getName());
					fDocument.addPosition(fPositionCategory, p);
				}
				p = new TypedPosition(interval.getOffset(), interval.getLength(), interval.getContentType().getName());
				last_end = interval.getOffset() +interval.getLength();
				fDocument.addPosition(fPositionCategory, p);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPositionCategoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}

	}
	
	/**
	 * Clears the position cache. Needs to be called whenever the positions have
	 * been updated.
	 */
	protected final void clearPositionCache() {
		if (fCachedPositions != null) {
			fCachedPositions= null;
		}
	}

	
	/**
	 * Returns the partitioners positions.
	 *
	 * @return the partitioners positions
	 * @throws BadPositionCategoryException if getting the positions from the
	 *         document fails
	 */
	protected final Position[] getPositions() throws BadPositionCategoryException {
		if (fCachedPositions == null) {
			fCachedPositions= fDocument.getPositions(fPositionCategory);
		} 
		return fCachedPositions;
	}
	
	/**
	 * Returns <code>true</code> if the given ranges overlap with or touch each other.
	 *
	 * @param gap the first range
	 * @param offset the offset of the second range
	 * @param length the length of the second range
	 * @return <code>true</code> if the given ranges overlap with or touch each other
	 */
	private boolean overlapsOrTouches(Position gap, int offset, int length) {
		return gap.getOffset() <= offset + length && offset <= gap.getOffset() + gap.getLength();
	}

	/**
	 * Returns the index of the first position which ends after the given offset.
	 *
	 * @param positions the positions in linear order
	 * @param offset the offset
	 * @return the index of the first position which ends after the offset
	 */
	private int getFirstIndexEndingAfterOffset(Position[] positions, int offset) {
		int i= -1, j= positions.length;
		while (j - i > 1) {
			int k= (i + j) >> 1;
			Position p= positions[k];
			if (p.getOffset() + p.getLength() > offset)
				j= k;
			else
				i= k;
		}
		return j;
	}

	/**
	 * Returns the index of the first position which starts at or after the given offset.
	 *
	 * @param positions the positions in linear order
	 * @param offset the offset
	 * @return the index of the first position which starts after the offset
	 */
	private int getFirstIndexStartingAfterOffset(Position[] positions, int offset) {
		int i= -1, j= positions.length;
		while (j - i > 1) {
			int k= (i + j) >> 1;
			Position p= positions[k];
			if (p.getOffset() >= offset)
				j= k;
			else
				i= k;
		}
		return j;
	}

	/**
	 * Corrects a position according to the values given in
	 * <code>correctionIndex</code>, <code>fNewOffSet</code> and
	 * <code>fNewLength</code>. Those are calculated by
	 * <code>calculatePosition()</code>
	 */
	private void correctPosition() {
		//if inserted directly before or after a position, adapt position accordingly
		try {
			Position[] category = getPositions();
			Position changing = category[correctionIndex];
			changing.setOffset(fNewOffSet);
			changing.setLength(fNewLength);
			// the order of the positions in the document may have changed.
			// remove and add it again freshly to solve this problem.
			fDocument.removePosition(fPositionCategory, changing);
			fDocument.addPosition(fPositionCategory, changing);
			
		} catch (BadPositionCategoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Calculates the correction to positions that have changed. The default
	 * positionUpdate works fine generally, but not at the beginning and end of editable
	 * intervals. The results needed for correction are stored in
	 * <code>correctionIndex</code>, <code>fNewOffSet</code> and
	 * <code>fNewLength</code>.
	 * 
	 * @param event
	 *            The event that reports the changes to the document.
	 * @return <code>true</code>, if correction is needed, <code>false</code>
	 *         otherwise.
	 */
	private boolean calculateCorrection(DocumentEvent event) {
		//only in case of insertions
		if (event.getText().length() > 0 && event.getLength() == 0) {
			try {
				Position[] category= getPositions();
				//insertion at beginning
				int first= fDocument.computeIndexInCategory(fPositionCategory, event.getOffset());
				for (int i = first; i < category.length; i++) {
					TypedPosition affected = (TypedPosition) category[i];
					if (affected.getOffset() > event.getOffset()) {
						break;
					}
					final ContentType affectedType = RodinConfiguration
							.getContentType(affected.getType());
					if (affectedType.isEditable())  {
						if (affected.getOffset() == event.getOffset() ) {
							fNewOffSet = event.getOffset();
							fNewLength = event.getText().length() + affected.getLength();
							correctionIndex = i;
							return true;
						} 					
					}
					
				}
				
				//insertion at end
				for (int i = first-1; i >= 0; i--) {
					TypedPosition affected = (TypedPosition) category[i];
					if (affected.getOffset() +affected.getLength() < event.getOffset()) {
						break;
					}
					final ContentType affectedType = RodinConfiguration
					.getContentType(affected.getType());
					if (affectedType.isEditable())  {
						if ( affected.getOffset() +affected.getLength() == event.getOffset()) {
							fNewOffSet = affected.getOffset();
							fNewLength = event.getText().length() + affected.getLength();
							correctionIndex = i;
							return true;
						}
					}
				}
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPositionCategoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return false;
	}
}
