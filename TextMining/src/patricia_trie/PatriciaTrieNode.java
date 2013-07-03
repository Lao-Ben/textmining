package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import utils.ByteCharSequence;

public class PatriciaTrieNode implements Externalizable {

	protected ArrayList<PatriciaTrieNode> s;
	protected ByteCharSequence w;
	protected int f;
	
	/**
	 * 
	 */
	public PatriciaTrieNode() {
		super();
		s = new ArrayList<PatriciaTrieNode>();
		w = null;
		f = 0;
	}

	/**
	 *
	 * @param word
	 * @param frequency
	 */
	public PatriciaTrieNode(ByteCharSequence word, int freq) {
		super();
		s = new ArrayList<PatriciaTrieNode>();
		w = word;
		f = freq;
		replaceByCachedSequence();
	}

	/**
	 *
	 * @param word
	 * @param frequency
	 * @param sons
	 */
	public PatriciaTrieNode(ByteCharSequence word, int freq, ArrayList<PatriciaTrieNode> sons) {
		super();
		s = new ArrayList<PatriciaTrieNode>(sons);
		w = word;
		f = freq;
		replaceByCachedSequence();
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
	static void addSon(ArrayList<PatriciaTrieNode> nodes, ByteCharSequence word, int frequency) {
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
	void insert(ByteCharSequence word, int freq) {
		for (PatriciaTrieNode p : s) {
			// if trie not empty
			// and word's first letter == current node first letter 
			if (p.w.charAt(0) == word.charAt(0)) {
				int keyLen = p.w.length();
				int wordLen = word.length();
				byte pos;
				
				// get the pos of first different char
				for (pos = 0; pos < wordLen && pos < keyLen
						&& word.charAt(pos) == p.w.charAt(pos); pos++)
					;
				
				// if position > end of word
				if (pos == keyLen && pos == wordLen) {
					p.f = freq;
					return;
				}

				// if current node's word is a prefix of the word
				if (pos >= keyLen) {
					p.insert(word.byteSubSequence(keyLen, wordLen), freq); //##
					return;
				}
				
				// current node's word and the word have the same prefix
				PatriciaTrieNode newNode = new PatriciaTrieNode(p.w.byteSubSequence(pos, keyLen), p.f, p.s);
				p.w = word.byteSubSequence(0, pos);
				p.f = freq;
				p.s.clear();
				p.s.add(newNode);

				if (pos < wordLen) {
					p.f = 0;
					addSon(p.s, word.byteSubSequence(pos, wordLen), freq); //##
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
		
		if (s.isEmpty())
			s = null;
		else
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

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int size = in.read();
		s = new ArrayList<PatriciaTrieNode>();
		for (int i = 0; i < size; i++) {
			s.add((PatriciaTrieNode)in.readObject());
		}
//		s = (ArrayList<PatriciaTrieNode>)in.readObject();
//		Object array = in.readObject();
//		System.out.println(this.getClass().getName());
//		s = new ArrayList<PatriciaTrieNode>(Arrays.asList((PatriciaTrieNode[])array));
		w = (ByteCharSequence)in.readObject();
		f = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(s.size());
		for (PatriciaTrieNode son : s) {
			out.writeObject(son);
		}
//		out.writeObject(s.toArray());
		out.writeObject(w);
		out.writeInt(f);
		w = null;
	
		for (PatriciaTrieNode son : s) {
			son = null;
		}
		s = null;
		if (PatriciaTrieSingleton.getInstance().gcStep++ >= 2430000) {
			Runtime runtime = Runtime.getRuntime();
//			System.out.println(PatriciaTrieSingleton.getInstance().gcStep + " " + runtime.freeMemory() / (1024*1024));
//			System.out.println("max " + runtime.maxMemory() / (1024*1024));
//			System.out.println("tot " + runtime.totalMemory() / (1024*1024));
			if (runtime.freeMemory() < 80000000) {
				System.out.println("gc " + runtime.freeMemory() / (1024*1024));
				System.gc();
			}
		}
	}
	
	private void replaceByCachedSequence() {
		PatriciaTrieNode node = PatriciaTrieSingleton.getInstance().map.get(w);
		if (node != null) {
			w = null;
			w = node.w;
		} else
			PatriciaTrieSingleton.getInstance().map.put(w, this);
	}
}


