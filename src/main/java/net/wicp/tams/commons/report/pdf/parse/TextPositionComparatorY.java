package net.wicp.tams.commons.report.pdf.parse;

import java.util.Comparator;

import org.apache.pdfbox.util.TextPosition;

public class TextPositionComparatorY implements Comparator<TextPosition> {

	/**
	 * {@inheritDoc}
	 */
	public int compare(TextPosition o1, TextPosition o2) {
		int retval = 0;
		TextPosition pos1 = (TextPosition) o1;
		TextPosition pos2 = (TextPosition) o2;

		/* Only compare text that is in the same direction. */
		if (pos1.getDir() < pos2.getDir()) {
			return -1;
		} else if (pos1.getDir() > pos2.getDir()) {
			return 1;
		}

		// Get the text direction adjusted coordinates
		float x1 = pos1.getXDirAdj();
		float x2 = pos2.getXDirAdj();

		float pos1YBottom = pos1.getYDirAdj();
		float pos2YBottom = pos2.getYDirAdj();
		// note that the coordinates have been adjusted so 0,0 is in upper left
		float pos1YTop = pos1YBottom - pos1.getHeightDir();
		float pos2YTop = pos2YBottom - pos2.getHeightDir();

		float yDifference = Math.abs(pos1YBottom - pos2YBottom);

		// we will do a simple tolerance comparison.
		if (yDifference < 8 || (pos2YBottom >= pos1YTop && pos2YBottom <= pos1YBottom)
				|| (pos1YBottom >= pos2YTop && pos1YBottom <= pos2YBottom)) {
			if (x1 < x2) {
				retval = -1;
			} else if (x1 > x2) {
				retval = 1;
			} else {
				retval = 0;
			}
		} else if (pos1YBottom < pos2YBottom) {
			retval = -1;
		} else {
			return 1;
		}
		return retval;
	}
}
