import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectionThread implements Callable<String> {

	private omero.client client;

	private String host;
	private int port;
	private String user;
	private String pass;

	public ConnectionThread(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.client = new omero.client(host, port);
	}

	public String call() {
		try {
			client.createSession(user, pass);
		} catch (Exception e) {
			return e.getMessage();
		} finally {
			client.closeSession();
		}
		return null;
	}

	public static void main(String[] args) {
		final ExecutorService executorService1,executorService2;
		Set<Callable<String>> callables1 = new HashSet<Callable<String>>();
		Set<Callable<String>> callables2 = new HashSet<Callable<String>>();
		String host = "localhost";
		int port = 4064;
		String user = "root";
		String pass = "omero";

		if (args.length > 3) {
			host = args[0];
			port = Integer.parseInt(args[1]);
			user = args[2];
			pass = args[3];
		}

		for (int i=0; i<900; ++i) {
			callables1.add(new ConnectionThread(host, port, user, pass));
		}
		for (int i=0; i<900; ++i) {
			callables2.add(new ConnectionThread(host, port, user, pass));
		}


		executorService1 = Executors.newFixedThreadPool(900);
		executorService2 = Executors.newFixedThreadPool(900);

		try {
			List<Future<String>> futures1 = executorService1.invokeAll(callables1);
			List<Future<String>> futures2 = executorService2.invokeAll(callables2);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} finally {
			executorService2.shutdown();
			executorService1.shutdown();
		}
	}
}