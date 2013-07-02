package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class PatriciaTrieNode implements Externalizable {

	/**
	 * 
	 */
//	private static final long serialVersionUID = -6873191676473588179L;

	protected ArrayList<PatriciaTrieNode> s;
	protected CharSequence w;
	protected int f;

	
	/**
	 * 
	 */
	public PatriciaTrieNode() {
		super();
		s = new ArrayList<PatriciaTrieNode>();
		f = 0;
	}

	/**
	 *
	 * @param word
	 * @param frequency
	 */
	public PatriciaTrieNode(CharSequence word, int freq) {
		super();
		s = new ArrayList<PatriciaTrieNode>();
		w = word;
		f = freq;
	}

	/**
	 *
	 * @param word
	 * @param frequency
	 * @param sons
	 */
	public PatriciaTrieNode(CharSequence word, int freq, ArrayList<PatriciaTrieNode> sons) {
		super();
		s = new ArrayList<PatriciaTrieNode>(sons);
		w = word;
		f = freq;
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
	static void addSon(ArrayList<PatriciaTrieNode> nodes, String word, int frequency) {
		PatriciaTrieNode node = new PatriciaTrieNode(word, frequency);
		nodes.add(node);
	}

	/**
	 * 
	 * @param word
	 * @param wordLen
	 * @param frequency
	 * @param data
	 * @return
	 */
	void insert(String word, int freq) {
		for (PatriciaTrieNode p : s) {
			// if trie not empty
			// and word's first letter == current node first letter 
			if (p.w.charAt(0) == word.charAt(0)) {
				int keyLen = p.w.length();
				byte pos;
				
				// get the pos of first different char
				for (pos = 0; pos < word.length() && pos < keyLen
						&& word.charAt(pos) == p.w.charAt(pos); pos++)
					;
				
				// if position > end of word
				if (pos == keyLen && pos == word.length()) {
					p.f = freq;
					return;
				}

				// if current node's word is a prefix of the word
				if (pos >= keyLen) {
					p.insert(word.substring(keyLen), freq);
					return;
				}
				
				// current node's word and the word have the same prefix
				PatriciaTrieNode newNode = new PatriciaTrieNode(p.w.subSequence(pos, keyLen), p.f, p.s);
				p.w = word.subSequence(0, pos);
				p.f = freq;
				p.s.clear();
				p.s.add(newNode);

				if (pos < word.length()) {
					p.f = 0;
					addSon(p.s, word.substring(pos), freq);
				}
				return;
			}
		}
		addSon(s, word, freq);
		return;
	}

	public int getFrequency() {
		return f;
	}

	/**
	 * 
	 * @param prefix
	 * @param word
	 * @param maxDistance
	 * @param treeData
	 * @param collector
	 */
	void search(String prefix, StringBuilder word, int maxDistance,
			List<ResultSearch> collector) {
		/*ExecutorService executor = Executors.newCachedThreadPool();
		synchronized (collector) {*/
			for (PatriciaTrieNode n : s) {
				Minion m = new Minion();
				m.configure(n, word, -1, -1, maxDistance, prefix.length(), collector);
				if (maxDistance > 0)
					m.calculateDistance(0, prefix.length());
				/*executor.execute(m);
				executor.shutdown();*/
				m.browse();
			}
		/*}
		while (!executor.isTerminated())
			;*/
	}
	
	public void recursiveTrim() {
		if (s == null || s.isEmpty())
			return;
		s.trimToSize();
		for (PatriciaTrieNode son : s) {
			son.recursiveTrim();
		}
	}
	
	void print(String data)
	{
		System.out.print("{"+w+"("+s.size()+")"+" : ");
		if (s.size() > 0)
			for(PatriciaTrieNode n : s)
				n.print(data);
		System.out.print("}");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		s = (ArrayList<PatriciaTrieNode>)in.readObject();
		w = (String)in.readObject();
		f = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(s);
		out.writeObject(w);
		out.writeInt(f);
	}
}
