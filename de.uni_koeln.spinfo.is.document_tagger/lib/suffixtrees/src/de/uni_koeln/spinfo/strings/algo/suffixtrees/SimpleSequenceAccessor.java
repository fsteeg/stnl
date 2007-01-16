package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.util.ArrayList;
import java.util.List;

public class SimpleSequenceAccessor extends ArrayList<Long> implements SequenceAccessor {

	private static final long serialVersionUID = -396809574132369635L;
	
	public SimpleSequenceAccessor() {
		
	}
	
	public SimpleSequenceAccessor(int initialCapacity) {
		super(initialCapacity);
	}


	public void addAll(List<Long> seq) {
		super.addAll(seq);
	}

	


}


