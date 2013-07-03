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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
			String line;

	        ExecutorService executor = Executors.newFixedThreadPool(8);

	        final List<Worker> workers = new ArrayList<Worker>();
	        

			while ((line = br.readLine()) != null) {
				String[] tab = line.split(" ");
				final StringBuilder word = new StringBuilder(tab[2]);
				final int dist = Integer.valueOf(tab[1]);

				/*
				 * Future<List<ResultSearch>> future = Constant.executor
				 * .submit(new Callable<List<ResultSearch>>() {
				 * 
				 * @Override public List<ResultSearch> call() throws Exception {
				 * return tree.search(word, dist, tree.getData() .toString()); }
				 * }); listfuture.add(future);
				 */
				workers.add(new Worker(word, dist, tree));
			//	ResultSearch.exp final List<Worker> workersingle = new ArrayList<Worker>();ortJSon(tree.search(wo//rd, dist));

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
            try {
				executor.invokeAll(workers);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			executor.shutdown();
			while (!executor.isTerminated()) {}

			StringBuilder res = new StringBuilder();
			
			for (Worker worker : workers) {
				res.append(worker.res);
			}
	//		System.out.println(res);
			
			System.out.println("Search time : "
					+ (System.currentTimeMillis() - fin));
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

class Worker implements Callable<Void> {

    private StringBuilder word;
    private int dist;
    private PatriciaTrie tree;
    public StringBuilder res;
    
    public Worker(StringBuilder w, int d, PatriciaTrie t) {
        word = w;
        dist = d;
        tree = t;
    }

    @Override
    public Void call() {
    	res = ResultSearch.exportJSon(tree.search(word, dist));
    	return null;
    }
}
