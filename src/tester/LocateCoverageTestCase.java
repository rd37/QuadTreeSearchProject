package tester;

import p.P;
import server.iConServer;

public class LocateCoverageTestCase extends TestCase{

	@Override
	public void execute() {
		P.print("MoveUserTestCase","Create server, add user");
		iConServer server = iConServer.getInstance();
		server.intialize(765, 4);
		server.setUrl("rigi-lab-03.cs.uvic.ca");
		iConWeb.getInstance().addServer(server);
		//server.showhashmap();
		//int userkey =iConWeb.getInstance().addUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.46915, -123.3192);
		int userkey =iConWeb.getInstance().addUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.4613, -123.3104);
		server.showlocationtree();
		System.out.println("");
	}

	@Override
	public void intialize() {
		// TODO Auto-generated method stub
		
	}

}
