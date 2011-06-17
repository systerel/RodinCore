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
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.editors.EditPos.computeLength;
import static fr.systerel.editor.internal.editors.EditPos.newPosOffLen;
import static fr.systerel.editor.internal.editors.EditPos.newPosStartEnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;

import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * Partitions a document according to the intervals. Parts of this class are
 * copied from {@link FastPartitioner}
 */
public class RodinPartitioner extends FastPartitioner {

	public static boolean DEBUG;
	
	/**
	 * The document mapper providing information for partition computing
	 */
	private DocumentMapper mapper;

	/** To correct positions */
	protected int fNewOffSet;
	protected int fNewLength;
	protected int correctionIndex;

	public RodinPartitioner(DocumentMapper documentMapper, String[] contentTypes) {
		super(new RuleBasedPartitionScanner(), contentTypes);
		this.mapper = documentMapper;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// Do nothing
	}

	@Override
	public String getContentType(int offset) {
		return getContentType(offset, false);
	}
	
	@Override
	public ITypedRegion[] computePartitioning(final int offset,
			final int length, boolean includeZeroLengthPartitions) {
		checkInitialization();
		final EditPos enclosing = newPosOffLen(offset, length);
		final List<ITypedRegion> list = new ArrayList<ITypedRegion>();
		try {
			final int endOffset = enclosing.getEnd();
			final Position[] category = getPositions();
			EditPos previous = null;
			EditPos current = null;

			final int startIndex = getFirstIndexEndingAfterOffset(category, offset);
			final int endIndex = getFirstIndexStartingAfterOffset(category, endOffset);
			for (int i = startIndex; i < endIndex; i++) {
				final TypedPosition curr = (TypedPosition) category[i];
				current = newPosOffLen(curr.getOffset(), curr.getLength());
				final int gapOffset;
				if (previous == null) {
					gapOffset = 0;
				} else {
					gapOffset = previous.getEnd() + 1;
				}
				if (current.getOffset() >= gapOffset) {
					final int gapEnd = current.getOffset() - 1;
					addGap(gapOffset, gapEnd, enclosing, list,
							includeZeroLengthPartitions);
				}
				if (current.overlapsWith(enclosing)) {
					final EditPos regionPos = getValidPos(current, enclosing);
					if (regionPos.getLength() > 0
							|| includeZeroLengthPartitions) {
						list.add(new TypedRegion(regionPos.getOffset(),
								regionPos.getLength(), curr.getType()));
					}
				}
				previous = current;
			}
			if (previous != null) {
				final int docLast = fDocument.getLength() - 1;
				final int gapOffset = previous.getEnd() + 1;
				addGap(gapOffset, docLast, enclosing, list,
						includeZeroLengthPartitions);
			}
			if (list.isEmpty()) {
				list.add(new TypedRegion(offset, length,
						IDocument.DEFAULT_CONTENT_TYPE));
			}
		} catch (BadPositionCategoryException ex) {
			ex.printStackTrace();
			// Make sure we clear the cache
			clearPositionCache();
		} catch (RuntimeException ex) {
			// Make sure we clear the cache
			ex.printStackTrace();
			clearPositionCache();
			throw ex;
		}
		if (DEBUG)
			System.out.println("partitioning: " + list);
		return list.toArray(new TypedRegion[list.size()]);
	}

	private static void addGap(int gapOffset, int gapEnd, EditPos enclosing,
			List<ITypedRegion> list, boolean includeZeroLengthPartitions) {
		if (gapEnd < gapOffset) {
			gapEnd = gapOffset;
		}
		final EditPos gap = newPosStartEnd(gapOffset, gapEnd);
		if ((includeZeroLengthPartitions && overlapsOrTouches(gap, enclosing))
				|| (gap.getLength() > 0 && gap.overlapsWith(enclosing))) {
			list.add(makeRegion(gap, enclosing, IDocument.DEFAULT_CONTENT_TYPE));
		}
	}

	private static TypedRegion makeRegion(EditPos suggested, EditPos enclosing, String type) {
		final EditPos validPos = getValidPos(suggested, enclosing);
		return new TypedRegion(validPos.getOffset(), validPos.getLength(), type);
	}

	private static EditPos getValidPos(EditPos suggested, EditPos enclosing) {
		final int regionStart = Math.max(enclosing.getStart(), suggested.getStart());
		final int regionEnd = Math.min(enclosing.getEnd(), suggested.getEnd());
		return newPosStartEnd(regionStart, regionEnd);
	}
	
	@Override
	public String getContentType(int offset, boolean preferOpenPartitions) {
		final ITypedRegion partition = getPartition(offset,
				preferOpenPartitions);
		if (partition != null) {
			return partition.getType();
		}
		return null;
	}

	private String getPositionCategory() {
		return getManagingPositionCategories()[0];
	}

	public ITypedRegion getPartition(int offset, boolean preferOpenPartitions) {
		checkInitialization();
		try {
			final Position[] category = getPositions();
			if (category == null || category.length == 0)
				return new TypedRegion(0, fDocument.getLength(),
						IDocument.DEFAULT_CONTENT_TYPE);
			int index = fDocument.computeIndexInCategory(getPositionCategory(),
					offset);
			if (index < category.length) {
				TypedPosition next = (TypedPosition) category[index];
				if (preferOpenPartitions) {
					// check if there is a non zero open partition ending at
					// offset:
					if (index > 0) {
						final TypedPosition previous = (TypedPosition) category[index - 1];
						if (previous.getOffset() + previous.getLength() == offset) {
							final String prevTypeName = previous.getType();
							final ContentType prevType = RodinConfiguration
									.getContentType(prevTypeName);
							// the editable types are considered open partitions
							if (prevType.isEditable()) {
								return new TypedRegion(previous.getOffset(),
										previous.getLength(), prevTypeName);
							}
						}
					}
					// check if there is a open partition starting at offset:
					if (next.getOffset() == offset) {
						final String nextTypeName = next.getType();
						final ContentType nextType = RodinConfiguration
								.getContentType(nextTypeName);
						// the editable types are considered open partitions
						if (nextType.isEditable()) {
							return new TypedRegion(next.getOffset(),
									next.getLength(), next.getType());
						}
					}
					if (index + 1 < category.length) {
						next = (TypedPosition) category[index + 1];
						if (next.getOffset() == offset) {
							final String nextTypeName = next.getType();
							final ContentType nextType = RodinConfiguration
									.getContentType(nextTypeName);
							// the editable types are considered open partitions
							if (nextType.isEditable()) {
								return new TypedRegion(next.getOffset(),
										next.getLength(), next.getType());
							}
						}
					}
					next = (TypedPosition) category[index];
				}
				if (offset == next.offset || offset < next.offset + next.length)
					return new TypedRegion(next.getOffset(), next.getLength(),
							next.getType());
				if (index == 0)
					return new TypedRegion(0, next.offset,
							IDocument.DEFAULT_CONTENT_TYPE);
				final TypedPosition previous = (TypedPosition) category[index - 1];
				if (previous.includes(offset))
					return new TypedRegion(previous.getOffset(),
							previous.getLength(), previous.getType());
				int endOffset = previous.getOffset() + previous.getLength();
				return new TypedRegion(endOffset, next.getOffset() - endOffset,
						IDocument.DEFAULT_CONTENT_TYPE);
			}
			final TypedPosition previous = (TypedPosition) category[category.length - 1];
			if (previous.includes(offset))

				return new TypedRegion(previous.getOffset(),
						previous.getLength(), previous.getType());
			final int endOffset = previous.getOffset() + previous.getLength();

			return new TypedRegion(endOffset,
					fDocument.getLength() - endOffset,
					IDocument.DEFAULT_CONTENT_TYPE);
		} catch (BadPositionCategoryException x) {
			x.printStackTrace();
		} catch (BadLocationException x) {
			x.printStackTrace();
		}
		return new TypedRegion(0, fDocument.getLength(),
				IDocument.DEFAULT_CONTENT_TYPE);
	}

	/**
	 * There should never be more than one partition affected.
	 */
	@Override
	public IRegion documentChanged2(DocumentEvent event) {
		checkInitialization();
		Assert.isTrue(event.getDocument() == fDocument);
		// if the document changed from the beginning, restart from scratch by
		// initializing
		if (event.getOffset() == 0) {
			try {
				final String positionCategory = getPositionCategory();
				fDocument.removePositionCategory(positionCategory);
				fDocument.addPositionCategory(positionCategory);
				initialize();
			} catch (BadPositionCategoryException e) {
				// cannot happen if document has been connected before
				e.printStackTrace();
			}
		} else {
			try {

				boolean needsCorrection = calculateCorrection(event);
				// this handles changes of existing partitions
				// currently there can be no new partitions.
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
	 * Performs the initial partitioning of the partitioner's document.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		final ArrayList<Interval> intervals = mapper.getIntervals();
		int last_end = 0;
		for (Interval interval : intervals) {
			try {
				final int offset = interval.getOffset();
				final int length = interval.getLength();
				
				final String contentTypeName = interval.getContentType()
						.getName();
				
				final int gapStart = last_end + 1;
				final int gapEnd = offset - 1;
				if (gapStart <= gapEnd) {
					final int gapLength = computeLength(gapStart, gapEnd);
					final TypedPosition gap = new TypedPosition(gapStart,
							gapLength, RodinConfiguration.LABEL_TYPE.getName());
					fDocument.addPosition(getPositionCategory(), gap);
				}
				final TypedPosition position = new TypedPosition(offset,
						length, contentTypeName);
				last_end = interval.getLastIndex();
				fDocument.addPosition(getPositionCategory(), position);
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (BadPositionCategoryException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Corrects a position according to the values given in
	 * <code>correctionIndex</code>, <code>fNewOffSet</code> and
	 * <code>fNewLength</code>. Those are calculated by
	 * <code>calculatePosition()</code>
	 */
	private void correctPosition() {
		// if inserted directly before or after a position, adapt position
		// accordingly
		try {
			final Position[] positions = getPositions();
			final Position changing = positions[correctionIndex];
			changing.setOffset(fNewOffSet);
			changing.setLength(fNewLength);
			// the order of the positions in the document may have changed.
			// remove and add it again freshly to solve this problem.
			final String positionCategory = getPositionCategory();
			fDocument.removePosition(positionCategory, changing);
			fDocument.addPosition(positionCategory, changing);
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the correction to positions that have changed. The default
	 * positionUpdate works fine generally, but not at the beginning and end of
	 * editable intervals. The results needed for correction are stored in
	 * <code>correctionIndex</code>, <code>fNewOffSet</code> and
	 * <code>fNewLength</code>.
	 * 
	 * @param event
	 *            The event that reports the changes to the document.
	 * @return <code>true</code>, if correction is needed, <code>false</code>
	 *         otherwise.
	 */
	private boolean calculateCorrection(DocumentEvent event) {
		// only in case of insertions
		if (event.getText().length() > 0 && event.getLength() == 0) {
			try {
				final Position[] category = getPositions();
				// insertion at beginning
				int first = fDocument.computeIndexInCategory(
						getPositionCategory(), event.getOffset());
				for (int i = first; i < category.length; i++) {
					final TypedPosition affected = (TypedPosition) category[i];
					if (affected.getOffset() > event.getOffset()) {
						break;
					}
					final ContentType affectedType = RodinConfiguration
							.getContentType(affected.getType());
					if (affected.getType().equals(
							IDocument.DEFAULT_CONTENT_TYPE))
						continue;
					if (affectedType.isEditable() || affectedType.isImplicit()) {
						if (affected.getOffset() == event.getOffset()) {
							fNewOffSet = event.getOffset();
							fNewLength = event.getText().length()
									+ affected.getLength();
							correctionIndex = i;
							return true;
						}
					}
				}
				// insertion at end
				for (int i = first - 1; i >= 0; i--) {
					final TypedPosition affected = (TypedPosition) category[i];
					if (affected.getOffset() + affected.getLength() < event
							.getOffset()) {
						break;
					}
					final ContentType affectedType = RodinConfiguration
							.getContentType(affected.getType());
					if (affectedType.isEditable()
							&& affectedType == RodinConfiguration.IMPLICIT_COMMENT_TYPE) {
						if (affected.getOffset() + affected.getLength() == event
								.getOffset()) {
							fNewOffSet = affected.getOffset();
							fNewLength = event.getText().length()
									+ affected.getLength();
							correctionIndex = i;
							return true;
						}
					}
				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (BadPositionCategoryException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**************************************************************************
	 * UTILITY METHODS COPIED FROM {@link FastPartitioner} as they are private
	 */

	/**
	 * Returns <code>true</code> if the given ranges overlap with or touch each
	 * other.
	 * 
	 * @param first
	 *            the first range
	 * @param offset
	 *            the offset of the second range
	 * @param length
	 *            the length of the second range
	 * @return <code>true</code> if the given ranges overlap with or touch each
	 *         other
	 */
	private static boolean overlapsOrTouches(EditPos first, EditPos second) {
		return first.getOffset() <= second.getEnd() + 1
				&& second.getOffset() <= first.getEnd() + 1;
	}

	/**
	 * Returns the index of the first position which ends after the given
	 * offset.
	 * 
	 * @param positions
	 *            the positions in linear order
	 * @param offset
	 *            the offset
	 * @return the index of the first position which ends after the offset
	 */
	private static int getFirstIndexEndingAfterOffset(Position[] positions,
			int offset) {
		int i = -1, j = positions.length;
		while (j - i > 1) {
			int k = (i + j) >> 1;
			Position p = positions[k];
			if (p.getOffset() + p.getLength() > offset)
				j = k;
			else
				i = k;
		}
		return j;
	}

	/**
	 * Returns the index of the first position which starts at or after the
	 * given offset.
	 * 
	 * @param positions
	 *            the positions in linear order
	 * @param offset
	 *            the offset
	 * @return the index of the first position which starts after the offset
	 */
	private static int getFirstIndexStartingAfterOffset(Position[] positions,
			int offset) {
		int i = -1, j = positions.length;
		while (j - i > 1) {
			int k = (i + j) >> 1;
			Position p = positions[k];
			if (p.getOffset() >= offset)
				j = k;
			else
				i = k;
		}
		return j;
	}

}
