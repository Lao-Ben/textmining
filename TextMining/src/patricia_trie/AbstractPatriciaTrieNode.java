package patricia_trie;
import java.io.Serializable;

public abstract class AbstractPatriciaTrieNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int start;
	protected int length;
	protected int frequency;
	

	public AbstractPatriciaTrieNode() {
		super();
	}

	public AbstractPatriciaTrieNode(int start, int frequency, int length) {
		super();
		this.start = start;
		this.frequency = frequency;
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public int getFrequency() {
		return frequency;
	}

	public int getLength() {
		return length;
	}

}
