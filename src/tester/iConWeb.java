package tester;

import java.util.LinkedList;

import server.iConAddress;
import server.iConNodeIdentifier;
import server.iConServer;

public class iConWeb {
	private static iConWeb web = new iConWeb();
	private LinkedList<iConServer> servers = new LinkedList<iConServer>();
	
	private iConWeb(){}
	
	public static iConWeb getInstance(){return web;}
	
	public void addServer(iConServer server){
		servers.add(server);
	}
	
	/*
	 * add user (addr)
	 */
	public int addUser(String serverurl,String ip,double latitude,double longitude){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConAddress addr = new iConAddress(ip,latitude,longitude);
				return serv.addUser(addr);
			}
		}
		return -1;
	}
	
	public void createUserNode(String quad,int key,String url,int userkey){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(url)){
				iConNodeIdentifier nodeID = new iConNodeIdentifier(url,key,url);
				serv.createUserNode(nodeID, userkey);
			}
		}
	}
	/*
	 * udd user to nodekey using user address
	 */
	public String addUser(String serverurl,String nextNodeUrl,int nextNodeKey,String nextNodeLoc, String ip,double latitude,double longitude,int userdepth,int userkey){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){//found correct server
				iConAddress userAddress = new iConAddress(ip,latitude,longitude);
				iConNodeIdentifier nextNode = new iConNodeIdentifier(nextNodeUrl,nextNodeKey,nextNodeLoc) ;
				return serv.addUser(userAddress, nextNode, userdepth,userkey).toString();
			}
		}
		return null;
	}
	
	public String createChild(String serverurl,String peerNodeUrl,int peerNodekey,String quadrantLoc,int childkey,double latrng1,double longrng1,double latrng2,double longrng2,String childQuad){
		iConNodeIdentifier nodeid=null;
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConNodeIdentifier peerNode = new iConNodeIdentifier(peerNodeUrl,peerNodekey,quadrantLoc);
				iConAddress subRange1 = new iConAddress("",latrng1,longrng1);
				iConAddress subRange2 = new iConAddress("",latrng2,longrng2);
				nodeid = serv.createChildNode(peerNode, childkey, subRange1, subRange2, childQuad);
			}
		}
		if(nodeid!=null)
			return nodeid.toString();
		return "";
	}
	
}
