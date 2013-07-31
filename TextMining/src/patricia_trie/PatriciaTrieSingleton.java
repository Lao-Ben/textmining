/**
 * 
 */
package patricia_trie;

/**
 * @author peyotll
 *
 */
public final class PatriciaTrieSingleton {
	/**
	 * The instance
	 */
	private static volatile PatriciaTrie instance = null;
	
	private PatriciaTrieSingleton() {
		super();
	}
	
	/**
	 * Create or load an instance of PatriciaTrie
	 * @return the instance of PatriciaTrie
	 */
	public final static PatriciaTrie getInstance() {
		if (PatriciaTrieSingleton.instance == null)
			synchronized (PatriciaTrieSingleton.class) {
				if (PatriciaTrieSingleton.instance == null)
					PatriciaTrieSingleton.instance = new PatriciaTrie();
			}
		return PatriciaTrieSingleton.instance;
	}
}
