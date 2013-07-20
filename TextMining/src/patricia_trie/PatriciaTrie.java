package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.ByteCharSequence;

public class PatriciaTrie implements Externalizable {

	private PatriciaTrieNode root;
	
	public transient HashMap<ByteCharSequence, PatriciaTrieNode> map;
	public transient int gcStep = 0; 
	public transient int count = 0;
	/**
	 * Constructeur
	 */
	public PatriciaTrie() {
		super();
		root = new PatriciaTrieNode();
		map = new HashMap<ByteCharSequence, PatriciaTrieNode>();
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
	 * 
	 * @return
	 */
	public PatriciaTrieNode getRoot() {
		return root;
	}
	
	/**
	 * Recherche le mot specifier dans le patricia trie
	 * @param word le mot a rechercher
	 * @param maxDistance la distance max de recherche
	 * @param treeData les donnees du patricia trie
	 * @param collector la liste des resultats de la recherche
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

	
	public void print()
	{
		// TODO: implement print method
		//root.print(getData().toString());
		root.print(0);
	}
	
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
}

