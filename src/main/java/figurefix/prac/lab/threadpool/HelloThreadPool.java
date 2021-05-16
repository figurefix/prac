package figurefix.prac.lab.threadpool;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HelloThreadPool {

	public static void main(String[] args) throws Exception {
		execute();
	//	submit();
	}
	
	public static void execute() throws Exception {
		LinkedBlockingQueue<Runnable> qu = new LinkedBlockingQueue<Runnable>(3);
		ExecutorService es = new ThreadPoolExecutor(4, 5, 1, TimeUnit.SECONDS, qu);
		try {
			for(int i=0; i<30; i++) { //超出LinkedBlockingQueue队列容量，会触发异常
				es.execute(() -> {
					double rd = Math.random();
					try {
						System.out.println("start: "+rd);
						Thread.sleep(1000);
						System.out.println("  end: "+rd);
					} catch (Exception e) {
						System.err.println("  err: "+rd);
						e.printStackTrace();
					}
				});	
				Thread.sleep(2000);
			}
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		es.shutdown();
		System.out.println("pool shutdown!");
	}
	
	public static void submit() throws Exception {
		LinkedBlockingQueue<Runnable> qu = new LinkedBlockingQueue<Runnable>(10);
		ExecutorService es = new ThreadPoolExecutor(4, 5, 1, TimeUnit.SECONDS, qu);
		ArrayList<Future<Double>> li = new ArrayList<Future<Double>>();
		for(int i=0; i<3; i++) {
			Future<Double> fu = es.submit(new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					double rd = Math.random();
					System.out.println("start: "+rd);
					Thread.sleep(2000);
					System.out.println("  end: "+rd);
					return rd;
				}
			});
			li.add(fu);
		}
		Thread.sleep(10000);
		for(Future<Double> fu : li) {
			try {
				Double db = fu.get();
				System.out.println("future: "+db);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		es.shutdown();
		System.out.println("pool shutdown!");
	}
}
