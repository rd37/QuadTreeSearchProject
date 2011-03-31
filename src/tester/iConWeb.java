package tester;
/*
 * this is a class to represent the internet http layer interface between servers.
 */
import java.util.LinkedList;

import p.P;

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
		P.print("iConWeb","added server "+server.getUrl());
	}
	
	public int[] getMessageKey(String serverurl,int userkey){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				return serv.getMessageKey(userkey);
			}
		}
		return null;
	}
	
	public void addMessageKey(String serverurl, int userkey,int msgkey,int type){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				serv.addMessageKey(userkey,msgkey,type);
			}
		}
	}
	
	public void removeUser(String serverurl,int userkey){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				serv.removeUser(userkey);
			}
		}
	}
	
	public void removeUser(String serverurl, String nextNodeUrl2,
			int nextNodeKey, String quadrant, int userkey) {
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConNodeIdentifier nextNodeid = new iConNodeIdentifier(nextNodeUrl2,nextNodeKey,quadrant);
				serv.removeUser(nextNodeid, userkey);
			}
		}
	}
	
	public LinkedList<Integer> getCoverage(String url, int key,
			String quadrant, String address, double latitude, double longitude,
			double latlongrad) {
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(url)){
				iConAddress srcAddress = new iConAddress(address,latitude,longitude);
				iConAddress coverRng1 = new iConAddress(address,latitude-latlongrad,longitude-latlongrad);
				iConAddress coverRng2 = new iConAddress(address,latitude+latlongrad,longitude+latlongrad);
				iConNodeIdentifier nodeid = new iConNodeIdentifier(url,key,quadrant);
				return  serv.getCoverage(coverRng1,coverRng2,nodeid,srcAddress,latlongrad);
			}
		}
		return null;
	}
	
	public String getCoverage(String serverurl, String newip,double newlat, double newlong, int userkey,double latlongrad){
		LinkedList<Integer> list=null;
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConAddress srcAddress = new iConAddress(newip,newlat,newlong);
				iConAddress coverRng1 = new iConAddress(newip,newlat-latlongrad,newlong-latlongrad);
				iConAddress coverRng2 = new iConAddress(newip,newlat+latlongrad,newlong+latlongrad);
				//P.print("iConWeb", "cover setup , now get from server");
				list =  serv.getCoverage(coverRng1,coverRng2,null,srcAddress,latlongrad);
				//P.print("iConWeb","System returned "+list.size()+" users");
			}
		}
		String returnString="";
		for(int i=0;i<list.size();i++){
			returnString+=" "+list.get(i).intValue()+" ";
		}
		return returnString;
	}
	
	public void moveUser(String serverurl, String newip,double newlat, double newlong, int userkey){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConAddress newaddr = new iConAddress(newip,newlat,newlong);
				serv.moveUser(newaddr,userkey);
				serv.updateUserPosition(newaddr,userkey);
			}
		}
	}
	
	public void moveUser(String serverurl, String nextNodeUrl, int nextNodeKey,String nextNodeQuad, String newip,double newlat,double newlong, int userkey){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConAddress newaddr = new iConAddress(newip,newlat,newlong);
				iConNodeIdentifier nextNodeid = new iConNodeIdentifier(nextNodeUrl,nextNodeKey,nextNodeQuad);
				serv.moveUser(nextNodeid,newaddr,userkey);
			}
		}
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
	
	public void createUserNode(String quad,int key,String url,int userkey,double lat,double lng){
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(url)){
				iConNodeIdentifier nodeID = new iConNodeIdentifier(url,key,url);
				serv.createUserNode(nodeID, new iConAddress("",lat,lng),userkey);
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

	public String addAllUsersInNode(String serverurl, int nodekey, String quadrant) {
		for(int i=0;i<servers.size();i++){
			iConServer serv = servers.get(i);
			if(serv.getUrl().equals(serverurl)){
				iConNodeIdentifier peerNode = new iConNodeIdentifier(serverurl,nodekey,quadrant);
				LinkedList<Integer> users = serv.getAllUserKeys(peerNode);
				String returnString = new String();
				for(int k=0;k<users.size();k++){
					returnString+=" "+users.get(k).intValue()+" ";
				}
				return returnString;
			}
		}
		return null;
	}


	
}
