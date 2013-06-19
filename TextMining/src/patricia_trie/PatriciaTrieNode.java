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
	protected byte length;
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
	public PatriciaTrieNode(int start, byte length, int frequency) {
		super();
		sons = new ArrayList<PatriciaTrieNode>();
		this.start = start;
		this.frequency = frequency;
		this.length = length;
	}
	
	public PatriciaTrieNode(int start, byte length, int frequency, List<PatriciaTrieNode> sons) {
		super();
		this.sons = new ArrayList<PatriciaTrieNode>(sons);
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
			byte wordLen, int frequency) {
		int pos = data.length();
		PatriciaTrieNode node;
		if ((pos = data.indexOf(word)) != -1)
			node = new PatriciaTrieNode(pos, wordLen, frequency);
		else {
			node = new PatriciaTrieNode(data.length(), wordLen, frequency);
			data = data.concat(word);
		}
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
	String insert(String word, byte wordLen, int frequency, String data) {
		for (PatriciaTrieNode p : sons) {
			if (data.length() > 0 && data.charAt(p.start) == word.charAt(0)) {
				byte keyLen = p.length;
				byte pos;
				for (pos = 0; pos < wordLen && pos < keyLen
						&& word.charAt(pos) == data.charAt(p.start + pos); pos++)
					;
				if (pos == keyLen && pos == wordLen) {
					p.frequency = frequency;
					return data;
				}

				if (pos >= keyLen) {
					return p.insert(word.substring(keyLen), (byte) (wordLen - keyLen),
							frequency, data);
				}
				PatriciaTrieNode newNode = new PatriciaTrieNode(p.start + pos,
						(byte) (keyLen - pos), p.frequency, p.sons);
				p.length = pos;
				p.frequency = frequency;
				p.sons.clear();
				p.sons.add(newNode);

				if (pos < wordLen) {
					p.frequency = 0;
					data = addSon(p.sons, data, word.substring(pos), (byte) (wordLen
							- pos), frequency);
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
		/*ExecutorService executor = Executors.newCachedThreadPool();
		synchronized (collector) {*/
			for (PatriciaTrieNode n : sons) {
				Minion m = new Minion();
				m.configure(n, word, maxDistance, prefix.length(), treeData,
						collector);
				if (maxDistance > 0)
					m.calculateDistance(0, prefix.length(), null, null);
				/*executor.execute(m);
				executor.shutdown();*/
				m.run();
			}
		/*}
		while (!executor.isTerminated())
			;*/
	}
	
	void print(String data)
	{
		System.out.print("{"+data.substring(start, start+length)+"("+sons.size()+")"+" : ");
		if (sons.size() > 0)
			for(PatriciaTrieNode n : sons)
				n.print(data);
		System.out.print("}");
	}
}
