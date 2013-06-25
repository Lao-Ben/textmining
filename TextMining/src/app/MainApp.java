package app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import patricia_trie.PatriciaTrie;
import patricia_trie.ResultSearch;

public class MainApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("Usage: TextMiningApp /path/to/compiled/dict.bin");
			return;
		}

		FileInputStream fich;
		try {
			fich = new FileInputStream(args[0]);
			GZIPInputStream gzipIn = new GZIPInputStream(fich);
			BufferedInputStream bfin = new BufferedInputStream(gzipIn);
			ObjectInputStream oos = new ObjectInputStream(bfin);

			System.out.println("Deserializing...");
			long debut = System.currentTimeMillis();
			final PatriciaTrie tree = (PatriciaTrie) oos.readObject();
			long fin = System.currentTimeMillis();
			long time = fin - debut;
			System.out.println("Deserialization time : " + time);
			oos.close();
			bfin.close();
			gzipIn.close();
			fich.close();
			InputStreamReader ipsr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;

			List<Future<List<ResultSearch>>> listfuture = new ArrayList<Future<List<ResultSearch>>>();
			// List<ResultSearch> results_synchr =
			// Collections.synchronizedList(results);
			// ExecutorService executorService =
			// Executors.newCachedThreadPool();
			// synchronized (results_synchr) {
			while ((ligne = br.readLine()) != null) {
				String[] tab = ligne.split(" ");
				final String word = tab[2];
				final int dist = Integer.valueOf(tab[1]);
				/*
				 * Future<List<ResultSearch>> future = Constant.executor
				 * .submit(new Callable<List<ResultSearch>>() {
				 * 
				 * @Override public List<ResultSearch> call() throws Exception {
				 * return tree.search(word, dist, tree.getData() .toString()); }
				 * }); listfuture.add(future);
				 */
				ResultSearch.exportJSon(tree.search(word, dist, tree.getData()
						.toString()));
				// results_synchr = Collections.synchronizedList(results);
			}
			/*
			 * boolean testFinish = false; while (!testFinish) { int i = 0; for
			 * (Future<?> f : listfuture) if (f.isDone()) i++; if
			 * (listfuture.size() == i) testFinish = true; }
			 * Constant.executor.shutdown(); for (Future<List<ResultSearch>> f :
			 * listfuture) try { ResultSearch.exportJSon(f.get()); } catch
			 * (InterruptedException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (ExecutionException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			System.out.println("Global time : "
					+ (System.currentTimeMillis() - debut));
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
