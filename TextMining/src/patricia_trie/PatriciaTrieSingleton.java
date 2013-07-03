/**
 * 
 */
package patricia_trie;

/**
 * @author peyotll
 *
 */
public final class PatriciaTrieSingleton {
	private static volatile PatriciaTrie instance = null;
	
	private PatriciaTrieSingleton() {
		super();
	}
	
	public final static PatriciaTrie getInstance() {
		if (PatriciaTrieSingleton.instance == null)
			synchronized (PatriciaTrieSingleton.class) {
				if (PatriciaTrieSingleton.instance == null)
					PatriciaTrieSingleton.instance = new PatriciaTrie();
			}
		return PatriciaTrieSingleton.instance;
	}
}
