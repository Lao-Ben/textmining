package patricia_trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PatriciaTrie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5570840938703266587L;
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
	public List<ResultSearch> search(String word, int maxDistance, String treeData) {
		List<ResultSearch> results = new ArrayList<ResultSearch>();
		List<ResultSearch> results_synchr = Collections.synchronizedList(results);
		synchronized (results_synchr) {
			root.search("", word, maxDistance, treeData, results_synchr);
		}
		return results_synchr;
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
}

