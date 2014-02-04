class ConnectionThread implements Runnable {

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

	public void run() {
		try {
			client.createSession(user, pass);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			client.closeSession();
		}
	}

}

public class ConnectionTest {
	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String user = args[2];
		String pass = args[3];

		for (int i=0; i<10000; ++i) {
			ConnectionThread t = new ConnectionThread(host, port, user, pass);
			t.run();
			if (i%1000 == 0) {
				System.out.println(i);
			}
		}		 
	}
}