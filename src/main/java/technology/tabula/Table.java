package technology.tabula;

import technology.tabula.algorithms.extractors.ExtractionAlgorithm;
import technology.tabula.text.RectangularTextContainer;
import technology.tabula.text.TextChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class Table extends Rectangle {

	private int rowCount = 0;
	private int colCount = 0;

	private final String extractionMethod;

	/* visible for testing */
	final TreeMap<CellPosition, RectangularTextContainer> cells = new TreeMap<>();

	public static final Table empty() { return new Table(""); }

	private Table(String extractionMethod) {
		this.extractionMethod = extractionMethod;
	}

	public Table(ExtractionAlgorithm extractionAlgorithm) {
		this(extractionAlgorithm.toString());
	}

	public int getRowCount() { return rowCount; }
	public int getColCount() { return colCount; }

	public String getExtractionMethod() { return extractionMethod; }

	public void add(RectangularTextContainer chunk, int row, int col) {
		this.merge(chunk);
		
		rowCount = Math.max(rowCount, row + 1);
		colCount = Math.max(colCount, col + 1);
		
		CellPosition cp = new CellPosition(row, col);
		
		RectangularTextContainer old = cells.get(cp);
		if (old != null) chunk.merge(old);
		cells.put(cp, chunk);

		this.memoizedRows = null;
	}

	private List<List<RectangularTextContainer>> memoizedRows = null;

	public List<List<RectangularTextContainer>> getRows() {
		if (this.memoizedRows == null) this.memoizedRows = computeRows();
		return this.memoizedRows;
	}

	private List<List<RectangularTextContainer>> computeRows() {
		List<List<RectangularTextContainer>> rows = new ArrayList<>();
		for (int i = 0; i < rowCount; i++) {
			List<RectangularTextContainer> lastRow = new ArrayList<>();
			rows.add(lastRow);
			for (int j = 0; j < colCount; j++) {
				RectangularTextContainer cell = cells.get(new CellPosition(i,j)); // JAVA_8 use getOrDefault()
				lastRow.add(cell != null ? cell : TextChunk.EMPTY);
			}
		}
		return rows;
	}
	
	public RectangularTextContainer getCell(int i, int j) {
		RectangularTextContainer cell = cells.get(new CellPosition(i,j)); // JAVA_8 use getOrDefault()
		return cell != null ? cell : TextChunk.EMPTY;
	}

}

class CellPosition implements Comparable<CellPosition> {

	final int row, col;

	CellPosition(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	public int hashCode() {
		return row + 101 * col;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CellPosition other = (CellPosition) obj;
		return row == other.row && col == other.col;
	}

	@Override
	public int compareTo(CellPosition other) {
		int rowDiff = row - other.row;
		return rowDiff != 0 ? rowDiff : col - other.col;
	}

}
