package compiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import patricia_trie.PatriciaTrie;
import patricia_trie.PatriciaTrieSingleton;
import utils.ByteCharSequence;

public class MainCompiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 2) {
			System.out
					.println("Usage: TextMiningCompiler /path/to/word/word.txt /path/to/output/dict.bin");
			return;
		}
		String fichier = args[0];
		try {
			InputStream ips;
			ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;

			PatriciaTrie tree = PatriciaTrieSingleton.getInstance();
			while ((line = br.readLine()) != null) {
				int index = line.indexOf("\t");
				ByteCharSequence word = new ByteCharSequence(line.substring(0, index));
				int freq = Integer.parseInt(line.substring(index + 1));
				tree.insert(word, freq);
			}
			
			br.close();
			ipsr.close();
			ips.close();
			tree.trim();

			tree.map.clear();
			tree.map = null;
			
			// force GC. Useless?
			//System.gc();

			FileOutputStream file = new FileOutputStream(args[1]);
			FileChannel chan = file.getChannel();
			ByteBuffer buff = ByteBuffer.allocateDirect(70000000).order(ByteOrder.nativeOrder());
			tree.write(buff);
			buff.flip();
			chan.write(buff);
			chan.close();
			file.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
