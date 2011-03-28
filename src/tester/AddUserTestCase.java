package tester;

import p.P;
import server.iConServer;

public class AddUserTestCase extends TestCase{

	@Override
	public void execute() {
		P.print("Execute TestCase AddUser","Create iConServer and add to iConWeb");
		iConServer server = iConServer.getInstance();
		server.intialize(765, 4);
		server.setUrl("rigi-lab-03.cs.uvic.ca");
		iConWeb.getInstance().addServer(server);
		server.showhashmap();
		iConWeb.getInstance().addUser("rigi-lab-03.cs.uvic.ca", "142.104.35.43", 48.469, -123.318);
		P.print(this.toString(), "********add another user to same***********");
		iConWeb.getInstance().addUser("rigi-lab-03.cs.uvic.ca", "142.104.35.44", 48.469, -123.318);
	}

	@Override
	public void intialize() {
		// TODO Auto-generated method stub
		
	}
}
