package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.FREE_MEM;

import utils.ByteCharSequence;

public class PatriciaTrieNode implements Serializable {

	protected ArrayList<PatriciaTrieNode> s;
	protected ByteCharSequence w;
	protected int f;
	
	/**
	 * 
	 */
	public PatriciaTrieNode() {
		super();
		s = null;
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
		s = null;
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
		if (sons != null)
		{
			s = new ArrayList<PatriciaTrieNode>(sons);
			s.trimToSize();
		}
		else
			s = null;
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
		nodes.trimToSize();
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
		if (s != null)
		{
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
					if (p.s == null)
						p.s = new ArrayList<PatriciaTrieNode>();
					p.insert(word.byteSubSequence(keyLen, wordLen), freq); //##
					return;
				}
				
				// current node's word and the word have the same prefix
				if (p.s == null)
					p.s = new ArrayList<PatriciaTrieNode>();
				PatriciaTrieNode newNode = new PatriciaTrieNode(p.w.byteSubSequence(pos, keyLen), p.f, p.s);
				p.w = word.byteSubSequence(0, pos);
				p.f = freq;
				p.s.clear();
				p.s.add(newNode);

				if (pos < wordLen) {
					p.f = 0;
					if (p.s == null)
						p.s = new ArrayList<PatriciaTrieNode>();
					addSon(p.s, word.byteSubSequence(pos, wordLen), freq); //##
				}
				return;
			}
		}
		}
		if (s == null)
			s = new ArrayList<PatriciaTrieNode>();
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
	
	void print(int level)
	{
		for (int i = 0; i < level; i++)
			System.out.print("	");
		if (s != null)
			System.out.print("{"+w+"("+s.size()+")("+f+")"+" : ");
		else
			System.out.print("{"+w+"(0)("+f+")"+" : ");
		if (s != null && s.size() > 0)
		{
			System.out.print("\n");
			if (s != null)
				for(PatriciaTrieNode n : s)
					n.print(level + 1);
			for (int i = 0; i < level; i++)
				System.out.print("	");
		}
		System.out.println("}");
	}

	/*@Override
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
		int len = in.readInt();
		if (len > 0) {
			byte[] bytes = new byte[len];
			w = new ByteCharSequence(bytes, 0, len);
		}
//		w = (ByteCharSequence)in.readObject();
		f = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(s.size());
		for (PatriciaTrieNode son : s) {
			out.writeObject(son);
		}
//		out.writeObject(s.toArray());
//		out.writeObject(w);
		int len = (w == null) ? 0 : w.length();
		out.writeInt(len);
//		for (int i = 0; i < len; i++) {
if (len > 0)			out.write(w.getBytes());
//		}
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
	}*/
	
	private void replaceByCachedSequence() {
		ByteCharSequence node = PatriciaTrieSingleton.getInstance().map.get(w);
		if (node != null) {
			w = null;
			w = node;
		} else
			PatriciaTrieSingleton.getInstance().map.put(w, w);
	}
	
	public int countword()
	{
		int c = 0;
		if (f > 0)
			c++;
		if (s != null && s.size() > 0)
		{
				for(PatriciaTrieNode n : s)
					c = c + n.countword();
		}
		return c;
	}
}


