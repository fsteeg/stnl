package de.uni_koeln.spinfo.strings.algo.suffixtrees.node;


public interface Node {

	public static final int A_LEAF = -1;
	
	long getId();
	
	int getTextNumber();

	int getLabelStart();

	int getLabelEnd();

	int[] getAdditionalLabels();

	//HashMap<Long, Node> getChildren();

	int getSuffixIndex();

	int getDfs();
	
}


