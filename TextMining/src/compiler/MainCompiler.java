package compiler;

import java.io.*;
import java.util.zip.GZIPOutputStream;

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

			//final int MegaBytes = 1024*1024;
			
			long debut = System.currentTimeMillis();
			while ((ligne = br.readLine()) != null) {
				int index = ligne.indexOf("\t");
				String word = ligne.substring(0, index);
				int freq = Integer.valueOf(ligne.substring(index + 1));
				tree.insert(word, freq);
				i++;
				if (i % 10000 == 0)
				{
				//	long freeMemory = Runtime.getRuntime().freeMemory()/MegaBytes;
		        //    long maxMemory = Runtime.getRuntime().maxMemory()/MegaBytes;
		        //    long totalMemory = Runtime.getRuntime().totalMemory() / MegaBytes;
		            
//		  System.out.println("Used Memory in JVM: " + (maxMemory - freeMemory) + "/" + maxMemory + " " + totalMemory);

		            long fin = System.currentTimeMillis();
					long time = fin-debut;
					System.out.println(i+" "+time);
					debut = System.currentTimeMillis();
				}
			}
			
			br.close();
			ipsr.close();
			ips.close();

			// trim the StringBuilder's size
			System.out.println("Triming...");
			debut = System.currentTimeMillis();
			tree.trim();
			long fin = System.currentTimeMillis();
			long time = fin-debut;
			System.out.println(time);
	
			// force GC. Useless?
			System.gc();
			
			System.out.println("Serializing...");
			debut = System.currentTimeMillis();

			FileOutputStream file = new FileOutputStream(args[1]);
			GZIPOutputStream gzipOut = new GZIPOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(gzipOut);
			oos.writeObject(tree);
			oos.flush();
			oos.close();

			System.out.println("Serialization time : " + (System.currentTimeMillis() - debut));
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
