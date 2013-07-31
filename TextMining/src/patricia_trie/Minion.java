package patricia_trie;

import java.util.ArrayList;
import java.util.List;

public class Minion {
	/**
	 * The word to search
	 */
	StringBuilder word;
	/**
	 * The key we have
	 */
	StringBuilder key;

	/**
	 * The comparable table
	 */
	int[][] cmpTable;

	/**
	 * The size of key Buffer
	 */
	int keyBufferSize;
	
	/**
	 * The word length
	 */
	int wordLen;
	
	/**
	 * The minimal distance to the word
	 */
	int minDistance;
	/**
	 * The real distance to the word
	 */
	int realDistance;
	/**
	 * The maximum distance to search
	 */
	int maxDistance;
	
	/**
	 * The size of comparable table
	 */
	int cmpTableSize;
	
	/**
	 * The actual size of the comparable table
	 */
	int cmpTableActualSize;

	/**
	 * The list of result for search
	 */
	List<ResultSearch> collector;
	
	/**
	 * The node we use
	 */
	PatriciaTrieNode root;
	
	/**
	 * The length of the key
	 */
	int lengthkey;

	/**
	 * Create a new Minion with default values
	 */
	Minion() {
		word = null;
		cmpTable = null;
		cmpTableSize = 0;
		cmpTableActualSize = 0;
		key = null;
		keyBufferSize = 0;
		minDistance = 0;
		realDistance = 0;
		collector = new ArrayList<ResultSearch>();
		lengthkey = 0;
		root = null;
	}

	/**
	 * Show the table
	 * @param keyLen
	 */
	void tableDisplay(char keyLen) {
		char i;
		char j;
		for (i = 0; i < cmpTableSize; i++) {
			if (i == 0)
				System.out.print("      ");
			else
				System.out.print(" " + key.charAt(i - 1) + " ");
			if (i == keyLen)
				System.out.print(" ");
			else
				System.out.print("|");
		}
		System.out.println();
		System.out.print("  -");
		for (i = 0; i < cmpTableSize; i++)
			System.out.print("----");
		System.out.println();
		for (i = 0; i < cmpTableSize; i++) {
			if (i == 0)
				System.out.print("  ");
			else if (i < wordLen + 1)
				System.out.print(word.charAt(i - 1) + " ");
			else
				System.out.print("  ");
			for (j = 0; j < keyLen + 1 && j < cmpTableSize; j++) {
				System.out.print("|");
				if ((j + maxDistance >= i && i + maxDistance >= j) || i == 0
						|| j == 0) {
					if (cmpTable[i][j] < 10)
						System.out.print(" ");
					System.out.print((int) cmpTable[i][j] + " ");
				} else
					System.out.print("   ");
			}
			if (j < cmpTableSize) {
				System.out.print("| ");
				for (; j < cmpTableSize; j++) {
					System.out.print("  | ");
				}
			}
			System.out.println();
		}
		System.out.print("  -");
		for (i = 0; i < cmpTableSize; i++)
			System.out.print("----");
		System.out.println();
	}

	/**
	 * Configure the minion
	 * @param root the root node
	 * @param word the word
	 * @param minDistance the minimal distance
	 * @param realDistance the real distance
	 * @param maxDistance the maximal distance
	 * @param lengthkey the length key
 	 * @param collectors the list of ResultSearch
	 */
	public void configure(PatriciaTrieNode root, StringBuilder word, int minDistance, int realDistance, int maxDistance, int lengthkey, List<ResultSearch> collectors) {
		this.root = root;
		this.word = word;
		this.minDistance = minDistance;
		this.realDistance = realDistance;
		this.maxDistance = maxDistance;
		this.lengthkey = lengthkey;

		this.wordLen = word.length();
		
		this.collector = collectors;
		
		int keyBuff = wordLen + maxDistance;
		if (keyBuff > keyBufferSize) {
			key = new StringBuilder(keyBuff);
			keyBufferSize = keyBuff;
		}

		// If maxDistance is at least 1, we need a table
		if (maxDistance >= 1) {
			cmpTableSize = wordLen + maxDistance + 1;
			if (cmpTableSize > cmpTableActualSize) {
				cmpTable = new int[cmpTableSize][];
				for (char i = 0; i < cmpTableSize; i++) {
					cmpTable[i] = new int[cmpTableSize];
					cmpTable[0][i] = i;
					cmpTable[i][0] = i;
					for (char j = 1; j < cmpTableSize; j++)
						if (i != 0)
							cmpTable[i][j] = 0;
				}
				cmpTableActualSize = cmpTableSize;
			}
		}
	}

	/**
	 * Calculate the distance between the key and the word
	 * @param oldKeyLen
	 * @param keyLen
	 */
	void calculateDistance(int oldKeyLen, int keyLen) {
		int iStart;
		if (oldKeyLen < maxDistance + 1)
			iStart = 1;
		else
			iStart = oldKeyLen - maxDistance;

		int iEnd = Math.min(cmpTableSize, keyLen + maxDistance + 1);

		for (int j = oldKeyLen + 1; j <= keyLen; ++j)
			for (int i = iStart; i < iEnd; ++i) {
				int dist;
				if (i-1 < word.length() && word.charAt(i - 1) == key.charAt(j - 1))
					dist = 0;
				else
					dist = 1;

				cmpTable[i][j] = cmpTable[i - 1][j - 1] + dist;

				// We only calculate the diagonal
				if (i + maxDistance > j)
					cmpTable[i][j] = Math.min(cmpTable[i][j],
							cmpTable[i - 1][j] + 1);
				if (j + maxDistance > i)
					cmpTable[i][j] = Math.min(cmpTable[i][j],
							cmpTable[i][j - 1] + 1);
				if (i > 1 && i - 1 < word.length() && j > 1 && word.charAt(i - 1) == key.charAt(j - 2)
						&& word.charAt(i - 2) == key.charAt(j - 1))
					cmpTable[i][j] = Math.min(cmpTable[i][j],
							(cmpTable[i - 2][j - 2] + dist) // transposition
							);
			}
		if (minDistance != -1)
			minDistance = cmpTable[keyLen][keyLen];
		if (realDistance != -1)
			realDistance = cmpTable[wordLen][keyLen];
	}
	
	/**
	 * Search for a distance of zero
	 * @param node the node where we search
	 * @param keyLen the key length
	 */
	void browseNode0(PatriciaTrieNode node, int keyLen) {
		int nodeStrLength = node.w.length();
		if (keyLen + nodeStrLength > wordLen)
			return; // No chance to fit

		if (!word.substring(keyLen).startsWith(node.w.toString()))
			return; // The two strings are different

		key = new StringBuilder(key.substring(0,keyLen));
		key.append(node.w);
		keyLen += nodeStrLength;
				
		if (keyLen == wordLen) {
			int freq = node.f;
			if (freq > 0) {
				ResultSearch result = new ResultSearch(key, 0, freq);
				collector.add(result);
			}
		}
		
		if (node.s != null)
		for (PatriciaTrieNode n : node.s) {
			root = n;
			lengthkey = keyLen;
			//browseNode(n, keyLen);
			this.browse();
		}
	}

	/**
	 * Search for a distance different of zero
	 * @param node the node where we search
	 * @param keyLen the key length
	 */
	void browseNode(PatriciaTrieNode node, int keyLen) {
		if (keyLen + node.w.length() > wordLen + maxDistance) {
			return; // No chance to do better
		}

		int oldKeyLen = keyLen;
		int toBeCopied = Math.min(node.w.length(), wordLen + maxDistance
				- keyLen);
		key = new StringBuilder(key.substring(0,keyLen));
		key.append(node.w, 0, toBeCopied);
		keyLen += toBeCopied;

		// compute the distance
		minDistance = 0;
		realDistance = 0;
		calculateDistance(oldKeyLen, keyLen);

		if (minDistance > 2 * maxDistance) {
			return;
		}

		// Add to results if the node means a word

		if (keyLen >= wordLen - maxDistance
				&& realDistance <= maxDistance) {
			int freq = node.getFrequency();
			if (freq > 0) {
				ResultSearch result = new ResultSearch(key,
						realDistance, freq);
				collector.add(result);
			}
		}

		if (node.s != null)
		for (PatriciaTrieNode n : node.s) {
			root = n;
			lengthkey = keyLen;
			//browseNode(n, keyLen);
			this.browse();
		}
		return;
	}

	/**
	 * General function of search
	 */
	public void browse() {
		if (maxDistance == 0) {
			browseNode0(root, lengthkey);
		} else {
			browseNode(root, lengthkey);
		}
	}
}
