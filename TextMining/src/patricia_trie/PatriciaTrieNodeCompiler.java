package patricia_trie;
import java.util.ArrayList;
import java.util.List;

public class PatriciaTrieNodeCompiler extends AbstractPatriciaTrieNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5941583576683489343L;
	List<PatriciaTrieNodeCompiler> sons;

	public PatriciaTrieNodeCompiler() {
		super(0,0,0);
		sons = new ArrayList<PatriciaTrieNodeCompiler>();
	}

	public PatriciaTrieNodeCompiler(int start, int length, int frequency) {
		super(start, frequency, length);
		sons = new ArrayList<PatriciaTrieNodeCompiler>();
	}

	String addSon(List<PatriciaTrieNodeCompiler> sons,
			String data, String word, int wordLen, int frequency) {
		int pos = data.length();
		if(data.contains(word))
			pos = data.indexOf(word);
		else
			data = data.concat(word);
		PatriciaTrieNodeCompiler node = new PatriciaTrieNodeCompiler(
				pos, wordLen, frequency);
		sons.add(node);
		return data;
	}

	String insert(String word, int wordLen, int frequency, String data) {
		// std::cout << "INSERT" << std::endl;
		//System.out.println(word + " " + sons.size() + " " + data);
		for (PatriciaTrieNodeCompiler p : sons) {
			if (data.length() > 0 && data.charAt(p.start) == word.charAt(0)) // The
																		// first
																		// letter
																		// is
																		// found
			{
				// We wanna know how far the keys are equal
				int keyLen = p.length;
				int pos;
				for (pos = 0; pos < wordLen // While the word is long enough
						&& pos < keyLen // While the key is long enough
						&& word.charAt(pos) == data.charAt(p.start
								+ pos); // While the chars are equal
				pos++) {
				}

				// The word already exists
				if (pos == keyLen && pos == wordLen) {
					p.frequency = frequency;
					return data;
				}

				if (pos >= keyLen) // The word is equal but longer than the key
				{
					// We insert in his son
					data = p.insert(word.substring(keyLen), wordLen - keyLen, frequency, data);
					return data;
				}

				// We have to split the current node
				PatriciaTrieNodeCompiler newNode = new PatriciaTrieNodeCompiler(
						p.start + pos, keyLen - pos, p.frequency);
				p.length = pos;
				newNode.sons = p.sons;
				p.frequency = frequency;
				p.sons.clear();
				p.sons.add(newNode);

				if (pos < wordLen) {
					// There has been a difference between the two
					p.frequency = 0;
					//System.out.println(pos);
					data = addSon(p.sons, data, word.substring(pos), wordLen - pos, frequency);
				}
				return data;
			}
			/*if (data.length() > 0)
				System.out.println(data.charAt(p.start));*/
		}
		// The first letter has not been found,
		// We create a new son
		data = addSon(sons, data, word, wordLen, frequency);
		//System.out.println(data);
		return data;
	}
}
