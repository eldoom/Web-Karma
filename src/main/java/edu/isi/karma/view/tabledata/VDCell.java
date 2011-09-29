/**
 * 
 */
package edu.isi.karma.view.tabledata;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONWriter;

import edu.isi.karma.rep.CellValue;
import edu.isi.karma.rep.TablePager;
import edu.isi.karma.view.Stroke;

/**
 * @author szekely
 * 
 */
public class VDCell {

	public enum Position {
		top, bottom, left, right;

		public Position getOpposite(/* Position position */) {
			switch (this) {
			case top:
				return bottom;
			case bottom:
				return top;
			case left:
				return right;
			case right:
				return left;
			}
			return null;
		}
	}

	static class MinMaxDepth {
		private final int minDepth;
		private final int maxDepth;

		MinMaxDepth(int minDepth, int maxDepth) {
			super();
			this.minDepth = minDepth;
			this.maxDepth = maxDepth;
		}

		int getMinDepth() {
			return minDepth;
		}

		int getMaxDepth() {
			return maxDepth;
		}

		int getDelta() {
			return maxDepth - minDepth;
		}

		static MinMaxDepth combine(List<MinMaxDepth> list) {
			int maxDepth = 0;
			int minDepth = Integer.MAX_VALUE;
			for (MinMaxDepth x : list) {
				maxDepth = Math.max(maxDepth, x.getMaxDepth());
				minDepth = Math.min(minDepth, x.getMinDepth());
			}
			return new MinMaxDepth(minDepth, maxDepth);
		}
	}

	private String fillHTableId;

	private int depth = -1;

	private CellValue value = null;

	private List<Stroke> topStrokes = new LinkedList<Stroke>();
	private List<Stroke> bottomStrokes = new LinkedList<Stroke>();
	private List<Stroke> leftStrokes = new LinkedList<Stroke>();
	private List<Stroke> rightStrokes = new LinkedList<Stroke>();

	private List<VDTriangle> triangles = new LinkedList<VDTriangle>();

	private List<TablePager> pagers = new LinkedList<TablePager>();

	VDCell() {
		super();
	}

	String getFillHTableId() {
		return fillHTableId;
	}

	void setFillHTableId(String fillHTableId) {
		this.fillHTableId = fillHTableId;
	}

	int getDepth() {
		return depth;
	}

	void setDepth(int depth) {
		this.depth = depth;
	}

	void setValue(CellValue value) {
		this.value = value;
	}

	List<Stroke> getTopStrokes() {
		return topStrokes;
	}

	List<Stroke> getBottomStrokes() {
		return bottomStrokes;
	}

	List<Stroke> getLeftStrokes() {
		return leftStrokes;
	}

	List<Stroke> getRightStrokes() {
		return rightStrokes;
	}

	void addTopStroke(Stroke stroke) {
		topStrokes.add(stroke);
	}

	void addBottomStroke(Stroke stroke) {
		bottomStrokes.add(stroke);
	}

	void addLeftStroke(Stroke stroke) {
		leftStrokes.add(stroke);
	}

	void addRightStroke(Stroke stroke) {
		rightStrokes.add(stroke);
	}

	void addTriangle(VDTriangle triangle) {
		triangles.add(triangle);
	}

	void addPager(TablePager pager) {
		pagers.add(pager);
	}

	/**
	 * @param depth
	 * @param position
	 * @return the stroke for the given position and depth. If no stroke is
	 *         defined at the given depth, return the one defined at the closest
	 *         lower depth.
	 */
	Stroke getStroke(int depth, Position position) {
		return getStroke(getStrokeList(position), depth);
	}

	/**
	 * @param position
	 * @return the list of strokes for the given position.
	 */
	List<Stroke> getStrokeList(Position position) {
		switch (position) {
		case top:
			return topStrokes;
		case bottom:
			return bottomStrokes;
		case left:
			return leftStrokes;
		case right:
			return rightStrokes;
		}
		return null;
	}

	/**
	 * @param position
	 * @return the max and min of the strokes defined in the given position.
	 */
	MinMaxDepth getMinMaxStrokeDepth(Position position) {
		List<Stroke> strokes = getStrokeList(position);
		int maxDepth = 0;
		int minDepth = Integer.MAX_VALUE;
		for (Stroke s : strokes) {
			maxDepth = Math.max(maxDepth, s.getDepth());
			minDepth = Math.min(minDepth, s.getDepth());
		}
		return new MinMaxDepth(minDepth, maxDepth);
	}

	/**
	 * @param list
	 * @param depth
	 * @return the stroke at the given depth, if there is one. If not, return
	 *         the stroke at the previous depth. This assumes the list is sorted
	 *         in increasing depth.
	 */
	private Stroke getStroke(List<Stroke> list, int depth) {
		Stroke previousStroke = null;
		for (Stroke s : list) {
			if (s.getDepth() == depth) {
				return s;
			}
			previousStroke = s;
		}
		return previousStroke;
	}

	/*****************************************************************
	 * 
	 * Debugging Support
	 * 
	 *****************************************************************/

	void prettyPrintJson(JSONWriter jw) throws JSONException {
		jw//
		.key("fillTableId").value(fillHTableId)//
				.key("depth").value(depth)//
				.key("value").value(value == null ? "null" : value.asString())//
				.key("strokes (top)").value(Stroke.toString(topStrokes))//
				.key("strokes (bottom)").value(Stroke.toString(bottomStrokes))//
				.key("strokes (left)").value(Stroke.toString(leftStrokes))//
				.key("strokes (right)").value(Stroke.toString(rightStrokes))//
		//
		;
		if (!triangles.isEmpty()) {
			jw.key("triangles").array();
			for (VDTriangle t : triangles) {
				t.prettyPrintJson(jw);
			}
			jw.endArray();
		}

		if (!pagers.isEmpty()) {
			jw.key("tablePagers").array();
			for (TablePager p : pagers) {
				p.prettyPrintJson(jw);
			}
			jw.endArray();
		}
	}
}