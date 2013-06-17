package patricia_trie;

import java.util.Collections;
import java.util.List;

public class ResultSearch implements Comparable<ResultSearch> {
	String word;
	int distance;
	int frequence;

	public ResultSearch(String word, int distance, int frequence) {
		super();
		this.word = word;
		this.distance = distance;
		this.frequence = frequence;
	}

	public String getWord() {
		return word;
	}

	public int getDistance() {
		return distance;
	}

	public int getFrequence() {
		return frequence;
	}

	public static void exportJSon(List<ResultSearch> resultCollector) {
		Collections.sort(resultCollector);
		int size = resultCollector.size();
		System.out.print("[");
		for (ResultSearch r : resultCollector) {
			System.out.print("{\"word\":\"" + r.getWord() + "\",\"freq\":"
					+ r.getFrequence() + ",\"distance\":" + r.getDistance()
					+ "}");
			if (size-- > 1)
				System.out.print(",");
		}
		System.out.println("]");
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
		return (this.getWord().compareTo(o.getWord()));
	}

}
