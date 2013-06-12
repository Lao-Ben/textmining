package patricia_trie;

public class PatriciaTrieCompiler extends AbstractPatriciaTrie {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5570840938703266587L;
	PatriciaTrieNodeCompiler root;

	public PatriciaTrieCompiler() {
		super();
		root = new PatriciaTrieNodeCompiler();
	}
	
	public void insert(String word, int frequency)
	{
		data = root.insert(word, word.length(), frequency, data);
	}
	
}
