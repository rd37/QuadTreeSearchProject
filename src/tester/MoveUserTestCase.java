package tester;

import p.P;
import server.iConServer;

public class MoveUserTestCase extends TestCase{

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
		P.print("MoveUserTestCase", "now move user");
		iConWeb.getInstance().moveUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.464, -123.3135, userkey);
		server.showlocationtree();
		System.out.println("");
		/*P.print("MoveUserTestCase", "Now add another user to old position");
		int userkey2 =iConWeb.getInstance().addUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.4695, -123.3195);
		server.showlocationtree();
		P.print("MoveUserTestCase", "now move user just added across screen");
		iConWeb.getInstance().moveUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.457, -123.310, userkey2);
		server.showlocationtree();
		System.out.println("");*/
		
	}

	@Override
	public void intialize() {
		// TODO Auto-generated method stub
		
	}

}
