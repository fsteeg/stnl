import java.util.ArrayList;

// The suffix tree is made up of edges connecting nodes.
// Each edge represents a string of characters starting
// at first_char_index and ending at last_char_index.
// Edges can be inserted and removed from a hash table,
// based on the Hash() function defined here.  The hash
// table indicates an unused slot by setting the
// start_node value to -1.

public class Edge {
		
	public int first_char_index;
	public int last_char_index;
	public int end_node;
	public int start_node;
	
	//The default ctor for Edge just sets start_node
	//to the invalid value.  This is done to guarantee
	//that the hash table is initially filled with unused
	//edges.
	
	public Edge(){
	    start_node = -1;
	}
	
	//I create new edges in the program while walking up
	//the set of suffixes from the active point to the
	//endpoint.  Each time I create a new edge, I also
	//add a new node for its end point.  The node entry
	//is already present in the Nodes[] array, and its
	//suffix node is set to -1 by the default Node() ctor,
	//so I don't have to do anything with it at this point.
	
	public Edge( int init_first, int init_last, int parent_node)
	{
	    first_char_index = init_first;
	    last_char_index = init_last;
	    start_node = parent_node;
	    end_node = Node.count++;
	}
	
	//		A given edge gets a copy of itself inserted into the table
	//		with this function.  It uses a linear probe technique, which
	//		means in the case of a collision, we just step forward through
	//		the table until we find the first unused slot.
			
	public void insert()
	{
	    int i = Suffixtree.hash( start_node, Application.t.charAt( first_char_index ) );
	    while ( Application.ukkonen.edges.get(i) != null /*|| Application.ukkonen.edges.get(i).start_node != -1 */)
	        i = ++i % Suffixtree.HASH_TABLE_SIZE;
	    Application.ukkonen.edges.put(i, this);
	    System.out.println("ADDED!");
	}
	
	//		Removing an edge from the hash table is a little more tricky.
	//		You have to worry about creating a gap in the table that will
	//		make it impossible to find other entries that have been inserted
	//		using a probe.  Working around this means that after setting
	//		an edge to be unused, we have to walk ahead in the table,
	//		filling in gaps until all the elements can be found.
	//
	//		Knuth, Sorting and Searching, Algorithm R, p. 527
			
	public void remove()
	{
	    int i = Suffixtree.hash( start_node, Application.t.charAt( first_char_index ) );
	    while ( Application.ukkonen.edges.get(i).start_node != start_node ||
	    		Application.ukkonen.edges.get(i).first_char_index != first_char_index )
	    		i = ++i % Suffixtree.HASH_TABLE_SIZE;
		for ( ; ; ) {
			Application.ukkonen.edges.get(i).start_node = -1;
			int j = i;
			for ( ; ; ) {
				i = ++i % Suffixtree.HASH_TABLE_SIZE;
				if ( Application.ukkonen.edges.get(i).start_node == -1 )
					return;
				int r = Suffixtree.hash( Application.ukkonen.edges.get(i).start_node, Application.t.charAt( Application.ukkonen.edges.get(i).first_char_index ) );
				if ( i >= r && r > j )
					continue;
				if ( r > j && j > i )
					continue;
				if ( j > i && i >= r )
					continue;
				break;
			}
			Application.ukkonen.edges.put(j, Application.ukkonen.edges.get(i));
	   }
	}
			
			
	//		When a suffix ends on an implicit node, adding a new character
	//		means I have to split an existing edge.  This function is called
	//		to split an edge at the point defined by the Suffix argument.
	//		The existing edge loses its parent, as well as some of its leading
	//		characters.  The newly created edge descends from the original
	//		parent, and now has the existing edge as a child.
			//
	//		Since the existing edge is getting a new parent and starting
	//		character, its hash table entry will no longer be valid.  That's
	//		why it gets removed at the start of the function.  After the parent
	//		and start char have been recalculated, it is re-inserted.
			//
	//		The number of characters stolen from the original node and given
	//		to the new node is equal to the number of characters in the suffix
	//		argument, which is last - first + 1;
			
	public int splitEdge( Suffix s )
	{
		remove();
		Edge new_edge = new Edge( first_char_index, first_char_index + s.last_char_index - s.first_char_index, s.origin_node);
		new_edge.insert();
		Suffixtree.nodes.get( new_edge.end_node ).suffix_node = s.origin_node;
		first_char_index += s.last_char_index - s.first_char_index + 1;
		start_node = new_edge.end_node;
		insert();
		return new_edge.end_node;
	}
}
