package tester;

import p.P;
import server.iConServer;

public class AddUserTestCase extends TestCase{

	@Override
	public void execute() {
		P.print("Execute TestCase AddUser","Create iConServer and add to iConWeb");
		iConServer server = iConServer.getInstance();
		server.setUrl("rigi-lab-03.cs.uvic.ca");
		iConWeb.getInstance().addServer(server);
	}

	@Override
	public void intialize() {
		// TODO Auto-generated method stub
		
	}
}
