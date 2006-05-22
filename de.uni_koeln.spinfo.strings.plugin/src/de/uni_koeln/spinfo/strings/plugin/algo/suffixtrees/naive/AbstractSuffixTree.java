/**
 * 
 */
package de.uni_koeln.spinfo.strings.plugin.algo.suffixtrees.naive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.SuffixTreeNode;


/**
 * Abstract superclass for suffix trees. Compact and simple suffix trees have in
 * common a root node and a text they represent.
 * 
 * @author Fabian Steeg
 * 
 */
public abstract class AbstractSuffixTree {

	/**
	 * The text represented by this tree.
	 */
	protected List<String> text = null;

	/**
	 * The root node of this tree.
	 */
	protected SuffixTreeNode root = null;

	/**
	 * The size of the input alphabet.
	 */
	protected int inputAlphabetSize = -1;

	/**
	 * @param text
	 *            The text to be represented by this tree, a terminating "$" is
	 *            appended if not already present.
	 * @param forChars
	 *            Indicates whether the treee edges should be labeled with the
	 *            chars (or for a compact tree many chars) or the words (or many
	 *            words) in the input text
	 * @param reverse
	 *            Whether to reverse the text ("prefix tree")
	 */
	protected AbstractSuffixTree(String fullText, boolean forChars,
			boolean reverse, boolean generalized) {
		ArrayList<String> text = null;
		String[] tokenized = null;
		if (forChars) {
			if (generalized) {
				// split at any word:
				tokenized = fullText.split(" ", 0);
				for (int i = 0; i < tokenized.length; i++) {
					if (reverse)
						tokenized[i] = new StringBuffer(tokenized[i]).reverse()
								.toString();
					tokenized[i] = tokenized[i] + "$";
				}
			} else {
				// split at any char:
				tokenized = fullText.split("", 0);
			}
		} else {
			if (generalized) {
				// split only at full stops:
				tokenized = fullText.split("[\\.,;:!?]");

				for (int i = 0; i < tokenized.length; i++) {
					tokenized[i] = tokenized[i].trim();
					if (reverse) {
						tokenized[i] = "$ " + tokenized[i];
					} else
						tokenized[i] = tokenized[i] + " $";
				}
			} else {
				// split only at words:
				tokenized = fullText.split(" ");
			}
		}
		text = new ArrayList<String>();
		for (String string : tokenized) {
			if (string.length() != 0) {
				text.add(string);
			}

		}
		// if the text lacks the terminating "$", add it:
		if (generalized
				|| (text.size() > 0 && text.get(text.size() - 1).equals("$"))) {
			this.text = text;
		} else {
			text.add("$");
			this.text = text;
		}
		if (reverse && !forChars) {
			Collections.reverse(this.text);
		}
		inputAlphabetSize = computeInputAlphabetSize(text);
	}

	public AbstractSuffixTree(List<String> text) {
		this.text = text;
	}

	/**
	 * @param text
	 *            The text for which to compute the alphabet.
	 * @return Returns the size of the input alphabet, that is the number of
	 *         different characters in the text.
	 */
	protected int computeInputAlphabetSize(List<String> text) {
		Set<String> set = new TreeSet<String>();
		for (String string : text) {
			set.add(string);
		}
		// Sets contain no duplicate entries
		return set.size() - 1;
	}

}
