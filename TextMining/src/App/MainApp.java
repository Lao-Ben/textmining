package App;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import patricia_trie.PatriciaTrie;
import patricia_trie.ResultSearch;

public class MainApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1)
		  {
		    System.out.println("Usage: App /path/to/compiled/dict.bin");
		    return;
		  }
		
		FileInputStream fich;
		try {
			fich = new FileInputStream(args[0]);
			ObjectInputStream oos = new ObjectInputStream(fich);
			
			PatriciaTrie tree = (PatriciaTrie) oos.readObject();
			oos.close();
			
			InputStreamReader ipsr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			
			int i = 0;
			List<ResultSearch> results = new ArrayList<ResultSearch>();
			while ((ligne = br.readLine()) != null) {
				String[] tab = ligne.split(" ");
				String word = tab[2];
				int dist = Integer.valueOf(tab[1]);
				tree.search(word, dist, tree.getData(), results);
				i++;
				if (i % 1000 == 0)
					System.out.println(i);
			}
			ResultSearch.exportJSon(results);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
