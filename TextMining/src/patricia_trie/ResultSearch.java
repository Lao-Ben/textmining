package patricia_trie;

import java.util.Collections;
import java.util.List;

public class ResultSearch implements Comparable<ResultSearch> {
	StringBuilder word;
	int distance;
	int frequence;

	public ResultSearch(StringBuilder word, int distance, int frequence) {
		super();
		this.word = word;
		this.distance = distance;
		this.frequence = frequence;
	}

	public StringBuilder getWord() {
		return word;
	}

	public int getDistance() {
		return distance;
	}

	public int getFrequence() {
		return frequence;
	}

	public static StringBuilder exportJSon(List<ResultSearch> resultCollector) {
		Collections.sort(resultCollector);
		int size = resultCollector.size();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for (ResultSearch r : resultCollector) {
			sb.append("{\"word\":\"" + r.getWord() + "\",\"freq\":"
					+ r.getFrequence() + ",\"distance\":" + r.getDistance()
					+ "}");
			if (size-- > 1)
				sb.append(",");
		}
		sb.append("]");
		sb.trimToSize();
		return sb;
	}

	@Override
	public int compareTo(ResultSearch o) {
		if (this.getDistance() < o.getDistance())
			return -1;
		if (this.getDistance() > o.getDistance())
			return 1;

		// equal distance
		if (this.getFrequence() > o.getFrequence())
			return -1;
		if (this.getFrequence() < o.getFrequence())
			return 1;

		// equal frequency
		return (sbcmp(word,o.getWord()));
	}
	
	public static int sbcmp(StringBuilder sb1, StringBuilder sb2) {
		if (sb1.length() < sb2.length())
			return -1;
		else if (sb1.length() < sb2.length())
			return 1;
		else
			for (int i = 0; i < sb1.length(); i++) {
				if (sb1.charAt(i) < sb2.charAt(i))
					return -1;
				else if (sb1.charAt(i) > sb2.charAt(i))
					return 1;
			}
		return 0;
	}

}
