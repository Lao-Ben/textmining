package compiler;

import java.io.*;

import patricia_trie.PatriciaTrie;

public class MainCompiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// Check parameters
		if (args.length < 2) {
			System.out
					.println("Usage: TextMiningCompiler /path/to/word/word.txt /path/to/output/dict.bin");
			return;
		}

		// Open txt dictionary
		String fichier = args[0];
		try {
			InputStream ips;
			ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;

			PatriciaTrie tree = new PatriciaTrie();
			int i = 0;

			long debut = System.currentTimeMillis();
			while ((ligne = br.readLine()) != null) {
				String[] tab = ligne.split("\\t");
				String word = tab[0];
				int freq = Integer.valueOf(tab[1]);
				tree.insert(word, freq);
				i++;
				if (i % 10000 == 0)
				{
					long fin = System.currentTimeMillis();
					long time = fin-debut;
					System.out.println(i+" "+time);
					debut = System.currentTimeMillis();
				}
			}
			
			br.close();
			ipsr.close();
			ips.close();

			FileOutputStream fich = new FileOutputStream(args[1]);
			ObjectOutputStream oos = new ObjectOutputStream(fich);
			oos.writeObject(tree);
			oos.flush();
			oos.close();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
