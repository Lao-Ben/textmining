package patricia_trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PatriciaTrieNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5941583576683489343L;
	List<PatriciaTrieNode> sons;

	protected int start;
	protected int length;
	protected int frequency;

	public PatriciaTrieNode() {
		super();
		sons = new ArrayList<PatriciaTrieNode>();
		start = 0;
		frequency = 0;
		length = 0;
	}

	public PatriciaTrieNode(int start, int length, int frequency) {
		super();
		sons = new ArrayList<PatriciaTrieNode>();
		this.start = start;
		this.frequency = frequency;
		this.length = length;
	}

	String addSon(List<PatriciaTrieNode> sons, String data, String word,
			int wordLen, int frequency) {
		PatriciaTrieNode node = new PatriciaTrieNode(data.length(), wordLen,
				frequency);
		data = data.concat(word);
		sons.add(node);
		return data;
	}

	String insert(String word, int wordLen, int frequency, String data) {
		// std::cout << "INSERT" << std::endl;
		System.out.println(word + " " + sons.size() + " " + data);
		for (PatriciaTrieNode p : sons) {
			System.out.println(p.start);
			if (data.length() > 0 && data.charAt(p.start) == word.charAt(0)) // The
			// first
			// letter
			// is
			// found
			{
				// We wanna know how far the keys are equal
				int keyLen = p.length;
				int pos;
				for (pos = 0; pos < wordLen // While the word is long enough
						&& pos < keyLen // While the key is long enough
						&& word.charAt(pos) == data.charAt(p.start + pos); // While
																			// the
																			// chars
																			// are
																			// equal
				pos++) {
				}

				// The word already exists
				if (pos == keyLen && pos == wordLen) {
					p.frequency = frequency;
					return data;
				}

				if (pos >= keyLen) // The word is equal but longer than the key
				{
					// We insert in his son
					data = p.insert(word.substring(keyLen), wordLen - keyLen,
							frequency, data);
					return data;
				}

				// We have to split the current node
				PatriciaTrieNode newNode = new PatriciaTrieNode(p.start + pos,
						keyLen - pos, p.frequency);
				p.length = pos;
				newNode.sons = p.sons;
				p.frequency = frequency;
				p.sons.clear();
				p.sons.add(newNode);

				if (pos < wordLen) {
					// There has been a difference between the two
					p.frequency = 0;
					// System.out.println(pos);
					data = addSon(p.sons, data, word.substring(pos), wordLen
							- pos, frequency);
				}
				return data;
			}
			/*
			 * if (data.length() > 0) System.out.println(data.charAt(p.start));
			 */
		}
		// The first letter has not been found,
		// We create a new son
		data = addSon(sons, data, word, wordLen, frequency);
		// System.out.println(data);
		return data;
	}

	public int getStart() {
		return start;
	}

	public int getFrequency() {
		return frequency;
	}

	public int getLength() {
		return length;
	}

	void search(String prefix, String word, int maxDistance, String treeData,
			List<ResultSearch> collector) {
		for (PatriciaTrieNode n : sons) {
			Minion m = new Minion();
			m.configure(word, maxDistance, treeData, collector);
			if (maxDistance == 0) {
				m.browseNode0(n, prefix.length());
			} else {
				m.calculateDistance(0, prefix.length(), null, null);
				m.browseNode(n, prefix.length());
			}
		}
	}
}
