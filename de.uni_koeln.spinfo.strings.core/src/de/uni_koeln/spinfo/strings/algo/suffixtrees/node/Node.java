package de.uni_koeln.spinfo.strings.algo.suffixtrees.node;


public interface Node {

	public static final int A_LEAF = -1;
	
	long getId();
	
	int getTextNumber();

	int getLabelStart();
    
    void setLabelStart(int s);

	int getLabelEnd();
    
    void setLabelEnd(int end);

	int[] getAdditionalLabels();
    
    void setAdditionalLabels(int[] labels);

	//HashMap<Long, Node> getChildren();

	int getSuffixIndex();

	int getDfs();
	
}


