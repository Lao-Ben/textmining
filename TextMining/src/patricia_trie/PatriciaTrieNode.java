package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import utils.ByteCharSequence;

public class PatriciaTrieNode implements Externalizable {

	/**
	 * The list of sons
	 */
	protected ArrayList<PatriciaTrieNode> s;
	/**
	 * The word
	 */
	protected ByteCharSequence w;
	/**
	 * The frequency
	 */
	protected int f;
	//transient ByteArrayOutputStream out;

	/**
	 * The default constructor for PatricieTrieNode
	 */
	public PatriciaTrieNode() {
		super();
		s = null;
		w = null;
		f = 0;
	}

	/**
	 * Create a new PatriciaTrieNode with a word and a frequency
	 * @param word the word
	 * @param frequency the frequency
	 */
	public PatriciaTrieNode(ByteCharSequence word, int frequency) {
		super();
		s = null;
		w = word;
		f = frequency;
		replaceByCachedSequence();
	}

	/**
	 * Create a new PatriciaTrieNode with a word, a frequency and a list of sons
	 * @param word the word
	 * @param frequency the frequency
	 * @param sons the list of sons
	 */
	public PatriciaTrieNode(ByteCharSequence word, int frequency,
			ArrayList<PatriciaTrieNode> sons, boolean replace) {
		super();
		if (sons != null) {
			s = new ArrayList<PatriciaTrieNode>(sons);
			s.trimToSize();
		} else
			s = null;
		w = word;
		f = frequency;
		if (replace)
			replaceByCachedSequence();
	}

	/**
	 * Add a son to the list of PatriciaTrieNode given in argument
	 * @param nodes the list of sons
	 * @param word the word
	 * @param frequency the frequency
	 */
	static void addSon(ArrayList<PatriciaTrieNode> nodes,
			ByteCharSequence word, int frequency) {
		PatriciaTrieNode node = new PatriciaTrieNode(word, frequency);
		nodes.add(node);
		nodes.trimToSize();
	}

	/**
	 * Insert a word in the PatriciaTrie
	 * @param word the word
	 * @param freq the frequency
	 */
	void insert(ByteCharSequence word, int freq) {
		if (s != null) {
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
						p.insert(word.byteSubSequence(keyLen, wordLen), freq); // ##
						return;
					}

					// current node's word and the word have the same prefix
					if (p.s == null)
						p.s = new ArrayList<PatriciaTrieNode>();
					PatriciaTrieNode newNode = new PatriciaTrieNode(
							p.w.byteSubSequence(pos, keyLen), p.f, p.s, true);
					p.w = word.byteSubSequence(0, pos);
					p.f = freq;
					p.s.clear();
					p.s.add(newNode);

					if (pos < wordLen) {
						p.f = 0;
						if (p.s == null)
							p.s = new ArrayList<PatriciaTrieNode>();
						addSon(p.s, word.byteSubSequence(pos, wordLen), freq); // ##
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
	 * Launch a search for a word
	 * @param prefix the prefix
	 * @param word the word
	 * @param maxDistance the maximal distance
	 * @param collector the list of result search
	 */
	void search(String prefix, StringBuilder word, int maxDistance,
			List<ResultSearch> collector) {
		/*
		 * ExecutorService executor = Executors.newCachedThreadPool();
		 * synchronized (collector) {
		 */
		for (PatriciaTrieNode n : s) {
			Minion m = new Minion();
			m.configure(n, word, -1, -1, maxDistance, prefix.length(),
					collector);
			if (maxDistance > 0)
				m.calculateDistance(0, prefix.length());
			/*
			 * executor.execute(m); executor.shutdown();
			 */
			m.browse();
		}
		/*
		 * } while (!executor.isTerminated()) ;
		 */
	}

	/**
	 * Trim the list of sons
	 */
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

	/**
	 * Print the node
	 * @param level the level in the Trie
	 */
	void print(int level) {
		for (int i = 0; i < level; i++)
			System.out.print("	");
		if (s != null)
			System.out.print("{" + w + "(" + s.size() + ")(" + f + ")" + " : ");
		else
			System.out.print("{" + w + "(0)(" + f + ")" + " : ");
		if (s != null && s.size() > 0) {
			System.out.print("\n");
			if (s != null)
				for (PatriciaTrieNode n : s)
					n.print(level + 1);
			for (int i = 0; i < level; i++)
				System.out.print("	");
		}
		System.out.println("}");
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int size = in.readInt();
		s = null;
		if (size > 0)
		{
			s = new ArrayList<PatriciaTrieNode>();
			for (int i = 0; i < size; i++) {
				s.add((PatriciaTrieNode) in.readObject());
			}
		}
		// s = (ArrayList<PatriciaTrieNode>)in.readObject();
		// Object array = in.readObject();
		// System.out.println(this.getClass().getName());
		// s = new
		// ArrayList<PatriciaTrieNode>(Arrays.asList((PatriciaTrieNode[])array));
		int len = in.readInt();
		if (len > 0) {
			byte[] bytes = new byte[len];
			in.read(bytes);
			w = new ByteCharSequence(bytes, 0, len);
		}
		// w = (ByteCharSequence)in.readObject();
		f = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (s != null) {
			out.writeInt(s.size());
			for (PatriciaTrieNode son : s) {
				out.writeObject(son);
				son = null;
			}
			s.clear();
			s = null;
		} else
			out.writeInt(0);
		// out.writeObject(s.toArray());
		// out.writeObject(w);
		int len = (w == null) ? 0 : w.length();
		out.writeInt(len);
		// for (int i = 0; i < len; i++) {
		if (len > 0)
			out.write(w.getBytes());
		// }
		out.writeInt(f);
		w = null;
		out.flush();
	}

	private void replaceByCachedSequence() {
		ByteCharSequence node = PatriciaTrieSingleton.getInstance().map.get(w);
		if (node != null)
		{
			w = null;
			w = node;
		}
		else
			PatriciaTrieSingleton.getInstance().map.put(w, w);
		/*ByteArrayOutputStream node = PatriciaTrieSingleton.getInstance().map.get(w);
		if (node != null) {
			try{
				w = null;
				ByteArrayInputStream arr = new ByteArrayInputStream(node.toByteArray());
				BufferedInputStream bfin = new BufferedInputStream(arr);
				int len = bfin.read();
				byte[] bytes = new byte[len];
				bfin.read(bytes);
				w = new ByteCharSequence(bytes);
				bfin.close();
				arr.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		} 
		else
		{
			try {
				ByteArrayOutputStream arr = new ByteArrayOutputStream();
				BufferedOutputStream bfout = new BufferedOutputStream(arr);
				bfout.write(w.length());
				bfout.write(w.getBytes());
				bfout.flush();
				bfout.close();
				arr.flush();
				arr.close();
				PatriciaTrieSingleton.getInstance().map.put(w, arr);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}*/
	}

	/**
	 * Count the number of word
	 * @return the number of word
	 */
	public int countword() {
		int c = 0;
		if (f > 0)
			c++;
		if (s != null && s.size() > 0) {
			for (PatriciaTrieNode n : s)
				c = c + n.countword();
		}
		return c;
	}

	/*private void serialize() {
		try {
			out = new ByteArrayOutputStream();
			ObjectOutputStream obj = new ObjectOutputStream(out);
			int len = (w == null) ? 0 : w.length();
			obj.writeInt(len);
			// for (int i = 0; i < len; i++) {
			if (len > 0)
				obj.write(w.getBytes());
			// }
			obj.writeInt(f);
			w = null;
			obj.flush();
			obj.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deserialize() {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(
					out.toByteArray());
			ObjectInputStream obj = new ObjectInputStream(in);
			int len = obj.readInt();
			if (len > 0) {
				byte[] bytes = new byte[len];
				obj.read(bytes);
				w = new ByteCharSequence(bytes, 0, len);
			}
			// w = (ByteCharSequence)in.readObject();
			f = obj.readInt();
			obj.close();
			in.close();
			out.reset();
			out = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void write(ByteBuffer byteBuffer)
	{
		if (s != null)
		{
			byteBuffer.putInt(s.size());
			for(PatriciaTrieNode son : s)
				son.write(byteBuffer);
		}
		else
			byteBuffer.putInt(0);
		int len = (w == null) ? 0 : w.length();
		byteBuffer.putInt(len);
		// for (int i = 0; i < len; i++) {
		if (len > 0)
		{
			byteBuffer.put(w.getBytes());
		}
		// }
		byteBuffer.putInt(f);
		w = null;
	}
	
	public static PatriciaTrieNode read(ByteBuffer buff)
	{
		int size = buff.getInt();
		ArrayList<PatriciaTrieNode> sons = null;
		if (size > 0)
		{
			sons = new ArrayList<PatriciaTrieNode>();
			for (int i = 0; i < size; i++)
				sons.add(read(buff));
		}
		int length = buff.getInt();
		ByteCharSequence word = null;
		if (length > 0)
		{
			byte[] bytes = new byte[length];
			buff.get(bytes);
			word = new ByteCharSequence(bytes);
		}
		int freq = buff.getInt();
		/*if (word != null)
			return new PatriciaTrieNode(word, freq, sons, true);*/
		return new PatriciaTrieNode(word, freq, sons, false);
	}
}
