package server;

public class ServerManager extends Thread {
	
	private FTServer server;

	public ServerManager(FTServer server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		if (server != null) {
			this.server.listen();
		}
	}

	public FTServer getServer() {
		return server;
	}

	public void setServer(FTServer server) {
		this.server = server;
	}
}
