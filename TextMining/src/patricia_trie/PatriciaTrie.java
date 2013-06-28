package patricia_trie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class PatriciaTrie implements Externalizable {

	/**
	 * 
	 */
//	private static final long serialVersionUID = -5570840938703266587L;
	PatriciaTrieNode root;
	StringBuilder data;

	/**
	 * Constructeur
	 */
	public PatriciaTrie() {
		super();
		root = new PatriciaTrieNode();
		data = new StringBuilder();
	}
	
	/**
	 * Insert le mot avec sa frequence dans le patricia trie
	 * @param word le mot a inserer
	 * @param frequency la frequence du mot
	 */
	public void insert(String word, int frequency)
	{
		root.insert(word, (byte) word.length(), frequency, data);
	}
	
	/**
	 * Retourne les donnees du patricia trie
	 * @return les donnees du tableau pour le patricia trie
	 */
	public StringBuilder getData()
	{
		return data;
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
			root.search("", word, maxDistance, data, results);
		//}
		return results;
	}
	
	public void trim()
	{
		root.recursiveTrim();
		data.trimToSize();
	}

	
	public void print()
	{
		root.print(getData().toString());
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		root = (PatriciaTrieNode)in.readObject();
		data = (StringBuilder)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(root);
		out.writeObject(data);
	}
}

