import java.io.IOException;
import java.io.InputStreamReader;

public class Application {
	
	//The input buffer and character count.  Please note that N
	//is the length of the input string -1, which means it
	//denotes the maximum index in the input buffer.
	public static char[] t = new char[100];
	public static int n;
	
	public static Suffixtree ukkonen;
	
	//Necessary forward references
	//	void validate();
	//	int walk_tree( int start_node, int last_char_so_far );
	
	public static void main(String[] args)
	{
		ukkonen = new Suffixtree();
	    System.out.println("Normally, suffix trees require that the last\n"
	         + "character in the input string be unique.  If\n"
	         + "you don't do this, your tree will contain\n"
	         + "suffixes that don't end in leaf nodes.  This is\n"
	         + "often a useful requirement. You can build a tree\n"
	         + "in this program without meeting this requirement,\n"
	         + "but the validation code will flag it as being an\n"
	         + "invalid tree\n\n"
	         + "Enter string: ");
	    InputStreamReader reader = new InputStreamReader(System.in);
	    try {
			reader.read(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    // Eingabe soll eigentlich in der Länge beschränkt sein
	    //cin.getline( T, MAX_LENGTH - 1 );
	    n = t.length - 1;
	    
		//	 The active point is the first non-leaf suffix in the
		//	 tree.  We start by setting this to be the empty string
		//	 at node 0.  The AddPrefix() function will update this
		//	 value after every new prefix is added.
		
	    Suffix active = new Suffix( 0, 0, -1 );  // The initial active prefix
	    for ( int i = 0 ; i <= n ; i++ )
	        Suffix.addPrefix( active, i );
			
		//	 Once all N prefixes have been added, the resulting table
		//	 of edges is printed out, and a validation step is
		//	 optionally performed.
			
	    ukkonen.dump_edges( n );
	    System.out.println("Would you like to validate the tree? (y/n)");
	    char c = ' ';
		try {
			c = (char)reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    if ( c == 'Y' || c == 'y' )
	    	ukkonen.validate();
	}
}
