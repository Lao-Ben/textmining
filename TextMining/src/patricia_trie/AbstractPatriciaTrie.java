package patricia_trie;
import java.io.Serializable;


public abstract class AbstractPatriciaTrie implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String data;
	
	public AbstractPatriciaTrie() {
		super();
		data = "";
	}
	
	public String getData()
	{
		return data;
	}
}
