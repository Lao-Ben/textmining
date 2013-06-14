package patricia_trie;

import java.util.List;

public class ResultSearch {
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
	
	public boolean resultCompare(ResultSearch first, ResultSearch second)
	{
	  if (first.getDistance() < second.getDistance())
	    return true;
	  if (first.getDistance() > second.getDistance())
	    return false;

	  // equal distance
	  if (first.getFrequence() > second.getFrequence())
	    return true;
	  if (first.getFrequence() < second.getFrequence())
	    return false;

	  // equal frequency
	  return (first.getWord().compareTo(second.getWord()) < 0);
	}


	public static void exportJSon(List<ResultSearch> resultCollector)
	{
	  int size = resultCollector.size();
	  System.out.print("[");
	  for (ResultSearch r : resultCollector)
	  {
		  System.out.print( "{\"word\":\""
		+ r.getWord()
		+ "\",\"freq\":"
		+ r.getFrequence()
		+ ",\"distance\":"
		+ r.getDistance()
		+ "}");
	    if (size-- > 1)
	    	System.out.print(",");
	  }
	  System.out.println("]");
	}
	
}
