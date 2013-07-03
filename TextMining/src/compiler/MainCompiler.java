package compiler;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import patricia_trie.PatriciaTrie;
import patricia_trie.PatriciaTrieSingleton;
import utils.ByteCharSequence;

public class MainCompiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		long start = System.currentTimeMillis();

		// Check parameters
		if (args.length != 2) {
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
			String line;

			PatriciaTrie tree = PatriciaTrieSingleton.getInstance();
			int i = 0;

			//final int MegaBytes = 1024*1024;
			
			long debut = System.currentTimeMillis();
			while ((line = br.readLine()) != null) {
				int index = line.indexOf("\t");
				ByteCharSequence word = new ByteCharSequence(line.substring(0, index));
				int freq = Integer.valueOf(line.substring(index + 1));
				tree.insert(word, freq);
				i++;
				if (i % 10000 == 0)
				{
				//	long freeMemory = Runtime.getRuntime().freeMemory()/MegaBytes;
		        //    long maxMemory = Runtime.getRuntime().maxMemory()/MegaBytes;
		        //    long totalMemory = Runtime.getRuntime().totalMemory() / MegaBytes;
		            
//		  System.out.println("Used Memory in JVM: " + (maxMemory - freeMemory) + "/" + maxMemory + " " + totalMemory);

//		            long fin = System.currentTimeMillis();
//					long time = fin-debut;
//					System.out.println(i+" "+time);
//					debut = System.currentTimeMillis();
				}
			}
			
			br.close();
			ipsr.close();
			ips.close();

			debut = System.currentTimeMillis();
			tree.trim();
			System.out.println("trimming time : " + (System.currentTimeMillis() - debut));


			tree.map = null;
			
			// force GC. Useless?
			System.gc();
			
			debut = System.currentTimeMillis();

			FileOutputStream file = new FileOutputStream(args[1]);
			GZIPOutputStream gzipOut = new GZIPOutputStream(file);
			BufferedOutputStream bfout = new BufferedOutputStream(gzipOut);
			ObjectOutputStream oos = new ObjectOutputStream(bfout);
			oos.writeObject(tree);
			oos.flush();
			oos.close();
			bfout.close();
			gzipOut.close();
			file.close();

			System.out.println("Serialization time : " + (System.currentTimeMillis() - debut));
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Global time : " + (System.currentTimeMillis() - start));
	}
}
