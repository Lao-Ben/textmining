package patricia_trie;

import java.util.ArrayList;
import java.util.List;

//public class Minion implements Runnable{
public class Minion {
	StringBuilder word;
	StringBuilder key;

	int[][] cmpTable;

	int keyBufferSize;
	int wordLen;
	
	int minDistance;
	int realDistance;
	int maxDistance;
	
	int cmpTableSize;
	int cmpTableActualSize;

	
	boolean isIdle;
	List<ResultSearch> collector;
	
	PatriciaTrieNode root;
	
	int lengthkey;

	Minion() {
		word = null;
		cmpTable = null;
		cmpTableSize = 0;
		cmpTableActualSize = 0;
		key = null;
		keyBufferSize = 0;
		minDistance = 0;
		realDistance = 0;
		isIdle = true;
		collector = new ArrayList<ResultSearch>();
		lengthkey = 0;
		root = null;
	}

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

	void configure(PatriciaTrieNode root, StringBuilder word, int minDistance, int realDistance, int maxDistance, int lengthkey, List<ResultSearch> collectors) {
		// log("being configured...");
		this.root = root;
		this.word = word;
		this.minDistance = minDistance;
		this.realDistance = realDistance;
		this.maxDistance = maxDistance;
		this.lengthkey = lengthkey;

		this.wordLen = word.length();
		
		this.collector = collectors;

		// Redim the key buffer if too small
		int keyBuff = wordLen + maxDistance;
		if (keyBuff > keyBufferSize) {
			key = new StringBuilder(keyBuff);
			keyBufferSize = keyBuff;
		}

		// If maxDistance is at least, we need a table
		if (maxDistance >= 1) {
			// Redim the distance calculation table if too small
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

	void calculateDistance(int oldKeyLen, int keyLen) {
		// Partial Damerau-Levenshtein distance
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
	
	int strncmp(String s1, String s2, int length)
	{
		return s1.substring(0, length).compareTo(s2.substring(0, length));
	}
	
	String copy(String s2, int length)
	{
		char[] d = new char[length];
		for (int i = 0; i < length; i++)
		{
			d[i] = s2.charAt(i);
		}
		return String.valueOf(d);
	}
	
	void browseNode0(PatriciaTrieNode node, int keyLen) {
		int nodeStrLength = node.w.length();
		if (keyLen + nodeStrLength > wordLen)
			return; // No chance to fit

		if (!word.substring(keyLen).startsWith(node.w.toString()))
			return; // The two strings are different

		System.out.println(key);
		
		key = new StringBuilder(key.substring(0,keyLen));
		key.append(node.w);
		keyLen += nodeStrLength;
		
		System.out.println(node.w + " " + key);
		
		System.out.println(wordLen + " " + keyLen);
		
		if (keyLen == wordLen) {
			System.out.println("found // f:" + node.f);
			int freq = node.f;
			if (freq > 0) {
				ResultSearch result = new ResultSearch(key, 0, freq);
				collector.add(result);
			}
		}
		for (PatriciaTrieNode n : node.s) {
			root = n;
			lengthkey = keyLen;
			//browseNode(n, keyLen);
			this.browse();
		}
	}

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
			return; // No chance to match
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

		for (PatriciaTrieNode n : node.s) {
			root = n;
			lengthkey = keyLen;
			//browseNode(n, keyLen);
			this.browse();
		}
		return;
	}

	public void browse() {
		if (maxDistance == 0) {
			browseNode0(root, lengthkey);
		} else {
			browseNode(root, lengthkey);
		}
	}
}
