package patricia_trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PatriciaTrieNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5941583576683489343L;
	List<PatriciaTrieNode> sons;

	protected int start;
	protected int length;
	protected int frequency;

	/**
	 * 
	 */
	public PatriciaTrieNode() {
		super();
		sons = new ArrayList<PatriciaTrieNode>();
		start = 0;
		frequency = 0;
		length = 0;
	}

	/**
	 * 
	 * @param start
	 * @param length
	 * @param frequency
	 */
	public PatriciaTrieNode(int start, int length, int frequency) {
		super();
		sons = new ArrayList<PatriciaTrieNode>();
		this.start = start;
		this.frequency = frequency;
		this.length = length;
	}

	/**
	 * 
	 * @param sons
	 * @param data
	 * @param word
	 * @param wordLen
	 * @param frequency
	 * @return
	 */
	String addSon(List<PatriciaTrieNode> sons, String data, String word,
			int wordLen, int frequency) {
		PatriciaTrieNode node = new PatriciaTrieNode(data.length(), wordLen,
				frequency);
		data = data.concat(word);
		sons.add(node);
		return data;
	}

	/**
	 * 
	 * @param word
	 * @param wordLen
	 * @param frequency
	 * @param data
	 * @return
	 */
	String insert(final String word, final int wordLen, final int frequency,
			String data) {
		for (final PatriciaTrieNode p : sons) {
			if (data.length() > 0 && data.charAt(p.start) == word.charAt(0)) {
				final int keyLen = p.length;
				int pos;
				for (pos = 0; pos < wordLen && pos < keyLen
						&& word.charAt(pos) == data.charAt(p.start + pos); pos++)
					;
				if (pos == keyLen && pos == wordLen) {
					p.frequency = frequency;
					return data;
				}

				if (pos >= keyLen) {
					return p.insert(word.substring(keyLen), wordLen - keyLen,
							frequency, data);
				}
				PatriciaTrieNode newNode = new PatriciaTrieNode(p.start + pos,
						keyLen - pos, p.frequency);
				p.length = pos;
				newNode.sons = p.sons;
				p.frequency = frequency;
				p.sons.clear();
				p.sons.add(newNode);

				if (pos < wordLen) {
					p.frequency = 0;
					data = addSon(p.sons, data, word.substring(pos), wordLen
							- pos, frequency);
				}
				return data;
			}
		}
		data = addSon(sons, data, word, wordLen, frequency);
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

	/**
	 * 
	 * @param prefix
	 * @param word
	 * @param maxDistance
	 * @param treeData
	 * @param collector
	 */
	void search(String prefix, String word, int maxDistance, String treeData,
			List<ResultSearch> collector) {
		ExecutorService executor = Executors.newCachedThreadPool();
		synchronized (collector) {
			for (PatriciaTrieNode n : sons) {
				Minion m = new Minion();
				m.configure(n, word, maxDistance, prefix.length(), treeData,
						collector);
				if (maxDistance > 0)
					m.calculateDistance(0, prefix.length(), null, null);
				executor.execute(m);
				executor.shutdown();
			}
		}
		while (!executor.isTerminated())
			;
	}
}
