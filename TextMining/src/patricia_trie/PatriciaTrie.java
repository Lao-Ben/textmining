package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.ByteCharSequence;

public class PatriciaTrie implements Externalizable {

	private PatriciaTrieNode root;
	
	public transient TreeMap<ByteCharSequence, ByteCharSequence> map;
	public transient int gcStep = 0;
	/**
	 * Constructeur
	 */
	public PatriciaTrie() {
		super();
		root = new PatriciaTrieNode();
		map = new TreeMap<ByteCharSequence, ByteCharSequence>();
	}
	
	/**
	 * Create a new PatriciaTrie with a node
	 * @param r the node
	 */
	public PatriciaTrie(PatriciaTrieNode r)
	{
		super();
		root = r;
		map = null;
		gcStep = 0;
	}
	
	/**
	 * Insert le mot avec sa frequence dans le patricia trie
	 * @param word le mot a inserer
	 * @param frequency la frequence du mot
	 */
	public void insert(ByteCharSequence word, int frequency)
	{
		root.insert(word, frequency);
	}
	
	/**
	 * Return the root node
	 * @return the root node
	 */
	public PatriciaTrieNode getRoot() {
		return root;
	}
	
	/**
	 * Recherche le mot specifier dans le patricia trie
	 * @param word le mot a rechercher
	 * @param maxDistance la distance max de recherche
	 */
	public List<ResultSearch> search(StringBuilder word, int maxDistance) {
		List<ResultSearch> results = new ArrayList<ResultSearch>();
//		List<ResultSearch> results_synchr = Collections.synchronizedList(results);
		//synchronized (results_synchr) {
			root.search("", word, maxDistance, results);
		//}
		return results;
	}
	
	public void trim()
	{
		root.recursiveTrim();
	}

	/**
	 * Print the PatriciaTrie
	 */
	public void print()
	{
		// TODO: implement print method
		//root.print(getData().toString());
		root.print(0);
	}
	
	/**
	 * Count the number of word in PatriciaTrie
	 * @return the number of word
	 */
	public int countword()
	{
		return root.countword();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		root = (PatriciaTrieNode)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(root);
	}
	
	public void write(ByteBuffer byteBuffer)
	{
		root.write(byteBuffer);
	}
	
	public static PatriciaTrie read(ByteBuffer byteBuffer)
	{
		PatriciaTrie trie = PatriciaTrieSingleton.getInstance();
		PatriciaTrieNode root = PatriciaTrieNode.read(byteBuffer);
		trie.root = root;
		return trie;
	}
	
}

