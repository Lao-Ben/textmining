package patricia_trie;

import java.io.Serializable;
import java.util.List;

public class PatriciaTrie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5570840938703266587L;
	PatriciaTrieNode root;
	String data;

	public PatriciaTrie() {
		super();
		root = new PatriciaTrieNode();
		data = "";
	}
	
	public void insert(String word, int frequency)
	{
		data = root.insert(word, word.length(), frequency, data);
	}
	
	public String getData()
	{
		return data;
	}
	
	public void search(String word, int maxDistance, String treeData,
			List<ResultSearch> collector) {
		root.search("", word, maxDistance, treeData, collector);
	}
	
}
