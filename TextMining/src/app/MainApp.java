package app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import patricia_trie.PatriciaTrie;
import patricia_trie.ResultSearch;

public class MainApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1)
		  {
		    System.out.println("Usage: TextMiningApp /path/to/compiled/dict.bin");
		    return;
		  }
		
		FileInputStream fich;
		try {
			fich = new FileInputStream(args[0]);
			GZIPInputStream gzipIn = new GZIPInputStream(fich);
			ObjectInputStream oos = new ObjectInputStream(gzipIn);
			
			System.out.println("Deserializing...");
			long debut = System.currentTimeMillis();
			PatriciaTrie tree = (PatriciaTrie) oos.readObject();
			long fin = System.currentTimeMillis();
			long time = fin-debut;
			System.out.println("Deserialization time : " + time);
			oos.close();
			gzipIn.close();
			fich.close();
			System.out.println("1");
			InputStreamReader ipsr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			System.out.println("2");			
			int i = 0;
			List<ResultSearch> results = new ArrayList<ResultSearch>();
			while ((ligne = br.readLine()) != null) {
				System.out.println("3");
				String[] tab = ligne.split(" ");
				String word = tab[2];
				System.out.println("4");
				int dist = Integer.valueOf(tab[1]);
				System.out.println("5");
				tree.search(word, dist, tree.getData().toString(), results);
				System.out.println("6");
				ResultSearch.exportJSon(results);
				System.out.println("7");
				results = new ArrayList<ResultSearch>();
				i++;
			}
			System.out.println("Global time : " + (System.currentTimeMillis() - debut));
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
