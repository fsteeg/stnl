import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Suffixtree {
	
	public Suffixtree(){
		for (int i = 0; i < 2000; i++) {
			nodes.add(new Node());
		}
	}

	// The maximum input string length this program
	// will handle is defined here. A suffix tree
	// can have as many as 2N edges/nodes. The edges
	// are stored in a hash table, whose size is also
	// defined here.
	public static int MAX_LENGTH = 1000;

	public static int HASH_TABLE_SIZE = 2179; // A prime roughly 10% larger

	// The array of defined nodes. The count is 1 at the
	// start because the initial tree has the root node
	// defined, with no children.
	// ==> class Nodes sets count = 1
	public static ArrayList<Node> nodes = new ArrayList<Node>();
	

	// This is the hash table where all the currently
	// defined edges are stored. You can dump out
	// all the currently defined edges by iterating
	// through the table and finding edges whose start_node
	// is not -1.

	public Map<Integer, Edge> edges = new HashMap<Integer, Edge>();

	// The validation code consists of two routines. All it does
	// is traverse the entire tree. walk_tree() calls itself
	// recursively, building suffix strings up as it goes. When
	// walk_tree() reaches a leaf node, it checks to see if the
	// suffix derived from the tree matches the suffix starting
	// at the same point in the input text. If so, it tags that
	// suffix as correct in the GoodSuffixes[] array. When the tree
	// has been traversed, every entry in the GoodSuffixes array should
	// have a value of 1.
	//
	// In addition, the BranchCount[] array is updated while the tree is
	// walked as well. Every count in the array has the
	// number of child edges emanating from that node. If the node
	// is a leaf node, the value is set to -1. When the routine
	// finishes, every node should be a branch or a leaf. The number
	// of leaf nodes should match the number of suffixes (the length)
	// of the input string. The total number of branches from all
	// nodes should match the node count.
	//

	char[] currentString = new char[1000];

	char[] goodSuffixes = new char[1000];

	char[] branchCount = new char[2000];

	public void validate() {
		for (int i = 0; i < Application.n; i++)
			goodSuffixes[i] = 0;
		walk_tree(0, 0);
		int error = 0;
		for (int i = 0; i < Application.n; i++)
			if (goodSuffixes[i] != 1) {
				System.out.println("Suffix " + i + " count wrong!\n");
				error++;
			}
		if (error == 0)
			System.out.println("All Suffixes present!\n");
		int leaf_count = 0;
		int branch_count = 0;
		for (int i = 0; i < Node.count; i++) {
			if (branchCount[i] == 0)
				System.out.println("Logic error on node " + i
						+ ", not a leaf or internal node!\n");
			else if (branchCount[i] == -1)
				leaf_count++;
			else
				branch_count += branchCount[i];
		}
		System.out.println("Leaf count : " + leaf_count);
		if (leaf_count == (Application.n + 1)) {
			System.out.println("OK");
		} else {
			System.out.println("Error!");
		}
		System.out.println("Branch count : " + branch_count);
		if (branch_count == (Node.count - 1)) {
			System.out.println("OK");
		} else {
			System.out.println("Error!");
		}
	}

	// The whole reason for storing edges in a hash table is that it
	// makes this function fairly efficient. When I want to find a
	// particular edge leading out of a particular node, I call this
	// function. It locates the edge in the hash table, and returns
	// a copy of it. If the edge isn't found, the edge that is returned
	// to the caller will have start_node set to -1, which is the value
	// used in the hash table to flag an unused entry.

	public Edge find(int node, int c) {
		int i = hash(node, c);
		for (;;) {
			Map<Integer, Edge> hashMap = this.edges;
			Edge edge = hashMap.get(i);
			if (edge != null && edge.start_node == node)
				if (c == Application.t.charAt(edge.first_char_index))
					return edge;
			if (edge==null /* edge.start_node == -1 */ ){
				edge = new Edge();
				edge.start_node = -1;
				return edge;
			}
			i = ++i % Suffixtree.HASH_TABLE_SIZE;
		}
	}

	// This routine prints out the contents of the suffix tree
	// at the end of the program by walking through the
	// hash table and printing out all used edges. It
	// would be really great if I had some code that will
	// print out the tree in a graphical fashion, but I don't!

	public void dump_edges(int current_n) {
		System.out.println(" Start  End  Suf  First Last  String\n");
		for (int j = 0; j < Suffixtree.HASH_TABLE_SIZE; j++) {
			// Edge *s = Edges + j;
			Edge s = this.edges.get(j);
			if ( s == null /* s.start_node == -1 */)
				continue;
			int end = s.end_node;
			System.out.println(s.start_node + "  " + end + "  "
					+ Suffixtree.nodes.get(end).suffix_node + "  "
					+ s.first_char_index + "  " + s.last_char_index);
			int top;
			if (current_n > s.last_char_index)
				top = s.last_char_index;
			else
				top = current_n;
			for (int l = s.first_char_index; l <= top; l++)
				System.out.println(Application.t.charAt(l));
		}
	}

	public int walk_tree(int start_node, int last_char_so_far) {
		int edges = 0;
		for (int i = 0; i < 256; i++) {
			Edge edge = this.find(start_node, i);
			if (edge.start_node != -1) {
				if (branchCount[edge.start_node] < 0)
					System.out
							.println("Logic error on node " + edge.start_node);
				branchCount[edge.start_node]++;
				edges++;
				int l = last_char_so_far;
				for (int j = edge.first_char_index; j <= edge.last_char_index; j++)
					currentString[l++] = Application.t.charAt(j);
				currentString[l] = '\0';
				if (walk_tree(edge.end_node, l) == 1) {
					if (branchCount[edge.end_node] > 0)
						System.out.println("Logic error on node "
								+ edge.end_node);
					branchCount[edge.end_node]--;
				}
			}
		}
		//
		// If this node didn't have any child edges, it means we
		// are at a leaf node, and can check on this suffix. We
		// check to see if it matches the input string, then tick
		// off it's entry in the GoodSuffixes list.
		//
		if (edges == 0) {
			System.out.println("Suffix : ");
			for (int m = 0; m < last_char_so_far; m++)
				System.out.println(currentString[m]);
			goodSuffixes[currentString.length - 1]++;
			// System.out.println("comparing: " << ( T + Application.n -
			// currentString.length + 1 )
			// << " to " << currentString << endl;
			// if ( strcmp(T + Application.n - currentString.length + 1,
			// currentString ) != 0 )
			// System.out.println("Comparison failure!\n");
			return 1;
		} else
			return 0;
	}

	// Edges are inserted into the hash table using this hashing
	// function.

	public static int hash(int node, int c) {
		return ((node << 8) + c) % Suffixtree.HASH_TABLE_SIZE;
	}
}
