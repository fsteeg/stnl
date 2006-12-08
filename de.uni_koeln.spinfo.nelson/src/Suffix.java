//
// When a new tree is added to the table, we step
// through all the currently defined suffixes from
// the active point to the end point.  This structure
// defines a Suffix by its final character.
// In the canonical representation, we define that last
// character by starting at a node in the tree, and
// following a string of characters, represented by
// first_char_index and last_char_index.  The two indices
// point into the input string.  Note that if a suffix
// ends at a node, there are no additional characters
// needed to characterize its last character position.
// When this is the case, we say the node is Explicit,
// and set first_char_index > last_char_index to flag
// that.

public class Suffix {

	public int origin_node;

	public int first_char_index;

	public int last_char_index;

	public Suffix(int node, int start, int stop) {
		this.origin_node = node;
		this.first_char_index = start;
		this.last_char_index = stop;
	}

	public int explicit() {
		if (first_char_index > last_char_index) {
			return 1;
		} else {
			return 0;
		}
	}

	public int implicit() {
		if (last_char_index >= first_char_index) {
			return 1;
		} else {
			return 0;
		}
	}

	// A suffix in the tree is denoted by a Suffix structure
	// that denotes its last character. The canonical
	// representation of a suffix for this algorithm requires
	// that the origin_node by the closest node to the end
	// of the tree. To force this to be true, we have to
	// slide down every edge in our current path until we
	// reach the final node.

	public void canonize() {
		if (this.explicit() != 1) {
			Edge edge = Application.ukkonen.find(origin_node, Application.t
					.charAt(first_char_index));
			int edge_span = edge.last_char_index - edge.first_char_index;
			while (edge_span <= (last_char_index - first_char_index)) {
				first_char_index = first_char_index + edge_span + 1;
				origin_node = edge.end_node;
				if (first_char_index <= last_char_index) {
					edge = Application.ukkonen.find(edge.end_node,
							Application.t.charAt(first_char_index));
					edge_span = edge.last_char_index - edge.first_char_index;
				}
				;
			}
		}
	}

	// This routine constitutes the heart of the algorithm.
	// It is called repetitively, once for each of the prefixes
	// of the input string. The prefix in question is denoted
	// by the index of its last character.
	//
	// At each prefix, we start at the active point, and add
	// a new edge denoting the new last character, until we
	// reach a point where the new edge is not needed due to
	// the presence of an existing edge starting with the new
	// last character. This point is the end point.
	//
	// Luckily for use, the end point just happens to be the
	// active point for the next pass through the tree. All
	// we have to do is update it's last_char_index to indicate
	// that it has grown by a single character, and then this
	// routine can do all its work one more time.
	//

	public static void addPrefix(Suffix active, int last_char_index) {
		int parent_node;
		int last_parent_node = -1;

		while (true) {
			Edge edge;
			parent_node = active.origin_node;

			// Step 1 is to try and find a matching edge for the given node.
			// If a matching edge exists, we are done adding edges, so we break
			// out of this big loop.

			// if (Application.ukkonen.edges.size() > 0) {
			if (active.explicit() == 1) {
				edge = Application.ukkonen.find(active.origin_node,
						Application.t.charAt(last_char_index));
				if (edge.start_node != -1)
					break;
			} else { // implicit node, a little more complicated
				edge = Application.ukkonen.find(active.origin_node,
						Application.t.charAt(active.first_char_index));
				int span = active.last_char_index - active.first_char_index;
				if (Application.t.charAt(edge.first_char_index + span + 1) == Application.t
						.charAt(last_char_index))
					break;
				parent_node = edge.splitEdge(active);
			}
			// }

			// We didn't find a matching edge, so we create a new one, add
			// it to the tree at the parent node position, and insert it
			// into the hash table. When we create a new node, it also
			// means we need to create a suffix link to the new node from
			// the last node we visited.

			Edge new_edge = new Edge(last_char_index, Application.n,
					parent_node);
			new_edge.insert();
			if (last_parent_node > 0){
				System.out.println("1 Setting suffix node to: " + parent_node);
				Suffixtree.nodes.get(last_parent_node).suffix_node = parent_node;
			}
			last_parent_node = parent_node;

			// This final step is where we move to the next smaller suffix

			if (active.origin_node == 0)
				active.first_char_index++;
			else
				active.origin_node = Suffixtree.nodes.get(active.origin_node).suffix_node;
			active.canonize();
		}
		if (last_parent_node > 0){
			System.out.println("2 Setting suffix node to: " + parent_node);
			Suffixtree.nodes.get(last_parent_node).suffix_node = parent_node;
		}
		active.last_char_index++; // Now the endpoint is the next active point
		active.canonize();
	}
}
