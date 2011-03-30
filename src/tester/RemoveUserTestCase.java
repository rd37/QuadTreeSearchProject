package tester;

import p.P;
import server.iConServer;

public class RemoveUserTestCase extends TestCase{

	@Override
	public void execute() {
		P.print("Execute TestCase RemoveUser","Create iConServer and add to iConWeb");
		iConServer server = iConServer.getInstance();
		server.intialize(765, 4);
		server.setUrl("rigi-lab-03.cs.uvic.ca");
		iConWeb.getInstance().addServer(server);
		//server.showhashmap();
		int userkey = iConWeb.getInstance().addUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.469, -123.318);
		server.showlocationtree();
		P.print(this.toString(), "********now remove user***********");
		iConWeb.getInstance().removeUser("rigi-lab-03.cs.uvic.ca", userkey);
		server.showlocationtree();
	}

	@Override
	public void intialize() {
		// TODO Auto-generated method stub
		
	}

}
