package server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import p.P;

import tester.iConWeb;



/*
 * iConServer provides the boot strap service into iCon.
 * users are added into iCon through the services provided
 * by iConServer.  iConServer maintains a distributed hash of 
 * all top level iConNodes. iConServer can conceptually be viewed as
 * a distributed root iConNode.
 */
public class iConServer {
	private static iConServer server  = new iConServer();
	private String url;
	private HashMap<Integer,iConNode> hashtable = new HashMap<Integer,iConNode>();
	private iConLayeredNode rootNode;
	private Random random;
	private int addUserDepthLevel=0;
	
	private iConServer(){}
	
	public static iConServer getInstance(){return server;};
	
	public int[] getMessageKey(int userkey){
		iConUser userNode = (iConUser) hashtable.get(userkey);
		int msgkey = userNode.getMessage(0);
		int msgcnt = userNode.getMsgCount(0);
		int[] ret = new int[2];
		ret[0]=msgcnt;
		ret[1]=msgkey;
		return ret;
	}
	
	public void addMessageKey(int userkey,int msgkey,int type){
		//assuem key belongs here
		iConUser userNode = (iConUser) this.hashtable.get(userkey);
		userNode.addMessage(msgkey, type);
	}
	
	public iConAddress getUserAddress(int key){
		//TODO this needs to be distributed
		return ((iConUser)this.hashtable.get(key)).getAddress();
	}
	
	public double metersToDegree(double meters){
		return 0.00001*meters;
	}
	public void intialize(int seed,int depthLevel){
		random=new Random(seed);
		addUserDepthLevel=depthLevel;
		int key=random.nextInt();
		iConAddress range1 = new iConAddress(url,48.456, -123.320);
		iConAddress range2 = new iConAddress(url,48.470, -123.306);
		rootNode = new iConLayeredNode(0,key,range1,range2,"all");
		hashtable.put(key, rootNode);
		//print("Created root LayeredNode "+key);
	}
	
	/*
	 * add a node to the hashtable
	 */
	
	public void addNodeToHashTable(iConNode node,int key){
		this.hashtable.put(key, node);
	}
	
	public iConNode getNodeFromHashTable(int key){
		return hashtable.get(key);
	}
	
	public void createUserNode(iConNodeIdentifier nextPeer,iConAddress address, int key){
		String peerUrl=nextPeer.getUrl();
		if(peerUrl.equals(this.url)){
			iConLayeredNode nextNode = (iConLayeredNode) this.hashtable.get(key);
			nextNode.createUserNode(key,address, this.addUserDepthLevel);
		}else{//need to perform a web call
			iConWeb.getInstance().createUserNode(nextPeer.getQuadrant(),nextPeer.getKey(),nextPeer.getUrl(),key,address.getLatitude(),address.getLongitude());
		}
	}
	
	public iConNodeIdentifier createChildNode(iConNodeIdentifier peerNode,int childkey,iConAddress subRange1,iConAddress subRange2,String childQuad){
		int peerNodekey = peerNode.getKey();
		String peerURL = peerNode.getUrl();
		iConNodeIdentifier ident;
		if(peerURL.equals(this.url)){
			iConLayeredNode iConPeerNode = (iConLayeredNode) this.hashtable.get(peerNodekey);
			return iConPeerNode.addChildNode(childkey,subRange1,subRange2,childQuad);
		}else{//create http call to createChildto url specified.
			String nodeid=iConWeb.getInstance().createChild(peerURL, peerURL, peerNodekey, peerNode.getQuadrant(), childkey, subRange1.getLatitude(), subRange1.getLongitude(), subRange2.getLatitude(), subRange2.getLongitude(), childQuad);
			StringTokenizer st = new StringTokenizer(nodeid);
			ident = new iConNodeIdentifier("",0,"");
			String url = st.nextToken();
			String key = st.nextToken();
			String loc = st.nextToken();
			ident.setUrl(url);
			ident.setKey(new Integer(key));
			ident.setQuadrant(loc);
		}
		return ident;
	}
	/*
	 * The user some how had access to this iConServer
	 * and is attempting to bootstrap into the 
	 * iCon overlay.
	 * 
	 * it is up to the iConServer to route the user to the 
	 * appropriate top level iConNode.
	 */
	public int addUser(iConAddress address){
		int userkey=this.getKey();
		P.print("iConServer", "Create a new User to add to system "+userkey);
		rootNode.createUserNode(userkey, address,this.addUserDepthLevel);
		P.print("iConServer", "User is created, now add to location data structure");
		rootNode.addUser(address,addUserDepthLevel,userkey);
		return userkey;
	}
	public LinkedList<Integer> getAllUserKeys(iConNodeIdentifier childnode){
		iConLayeredNode childNode = (iConLayeredNode) this.hashtable.get(childnode.getKey());
		Set<Integer> keys = childNode.getRegisteredUserKeys();
		Iterator<Integer> it = keys.iterator();
		LinkedList<Integer> userkeys = new LinkedList<Integer>();
		while(it.hasNext()){
			userkeys.add(it.next());
		}
		return userkeys;
	}
	
	public LinkedList<Integer> addAllUsersInNode(iConNodeIdentifier childNodeid){
		if(childNodeid.getUrl().equals(this.url)){//child node is local
			LinkedList<Integer> users = new LinkedList<Integer>();
			iConLayeredNode childNode = (iConLayeredNode) this.hashtable.get(childNodeid.getKey());
			Set<Integer> keys = childNode.getRegisteredUserKeys();
			Iterator<Integer> it = keys.iterator();
			while(it.hasNext()){
				users.add(it.next());
			}
			//P.print("iConServer","Add users from this server "+users.size());
			
			return users;
		}else{//child node is remote
			LinkedList<Integer> users = new LinkedList<Integer>();
			String userkeys = iConWeb.getInstance().addAllUsersInNode(childNodeid.getUrl(),childNodeid.getKey(),childNodeid.getQuadrant());
			StringTokenizer st = new StringTokenizer(userkeys);
			while(st.hasMoreTokens()){
				users.add(new Integer(st.nextToken()));
			}
			return users;
		}
		
	}
	
	public LinkedList<Integer> getCoverage(iConAddress coverRng1,iConAddress coverRng2,iConNodeIdentifier nodeid,iConAddress srcAddress,double latlongrad){
		if(nodeid==null){//assume to use root node then
			//P.print("iConServer", "node id was null, so use root");
			return this.rootNode.getCoverage(coverRng1,coverRng2,srcAddress,latlongrad);
		}else{//must be recursive call back
			if(nodeid.getUrl().equals(this.url)){
				iConLayeredNode node = (iConLayeredNode) this.hashtable.get(nodeid.getKey());//reteive node from this hash table
				return node.getCoverage(coverRng1,coverRng2,srcAddress,latlongrad);
			}else{//node must be on another machine
				return iConWeb.getInstance().getCoverage(nodeid.getUrl(),nodeid.getKey(),nodeid.getQuadrant(),srcAddress.getAddress(),srcAddress.getLatitude(),srcAddress.getLongitude(),latlongrad);
			}
		}
	}
	
	public void removeUser(int userkey){
		iConNodeIdentifier rootid = new iConNodeIdentifier(this.url,this.rootNode.getKey(),"");
		removeUser(rootid,userkey);
		this.hashtable.remove(userkey);
	}
	
	public void removeUser(iConNodeIdentifier nextNodeid,int userkey){
		if(nextNodeid==null)
			return;
		String nextNodeUrl=nextNodeid.getUrl();
		int nextNodeKey = nextNodeid.getKey();
		String quadrant = nextNodeid.getQuadrant();
		if(nextNodeUrl.equals(this.url)){//next node is local to this server
			iConLayeredNode nextNode = (iConLayeredNode) this.hashtable.get(nextNodeKey);
			nextNode.removeUser(userkey);
			int usersremaining=-1;
			P.print("iConServer", "remove user at level "+nextNode.getLevel());
			if(this.addUserDepthLevel==(nextNode.getLevel())){
				 usersremaining = nextNode.removeKey(userkey);
			}else{
				usersremaining=nextNode.getUsersRemaining();
			}
			if(usersremaining==0){
				//this.hashtable.remove(nextNode.getKey());
			}
		}else{//next node is remote
			iConWeb.getInstance().removeUser(nextNodeUrl,nextNodeUrl,nextNodeKey,quadrant,userkey);
		}
	}
	
	public void updateUserPosition(String url, iConAddress newaddr, int userkey){
		//this.rootNode.updateUserPosition(newaddr,userkey);
		if(url.equals(this.url)){
			iConUser user = ((iConUser)this.hashtable.get(userkey));
			//System.out.println("Change Address from "+user.getAddress().getLatitude()+" "+user.getAddress().getLongitude());
			((iConUser)this.hashtable.get(userkey)).setAddress(newaddr);
			//System.out.println("Change Address  To "+user.getAddress().getLatitude()+" "+user.getAddress().getLongitude());
		}
	}
	
	public void updateUserPosition(iConAddress newaddr, int userkey){
		this.rootNode.updateUserPosition(newaddr,userkey);
	}
	
	public void moveUser(iConAddress newaddr,int userkey){
		this.rootNode.moveUser(newaddr,userkey,this.addUserDepthLevel);
	}
	
	public void moveUser(iConNodeIdentifier nextNodeid, iConAddress newaddr,
			int userkey) {
		String nextNodeUrl = nextNodeid.getUrl();
		int nextNodeKey = nextNodeid.getKey();
		String nextNodeQuad = nextNodeid.getQuadrant();
		
		if(nextNodeUrl.equals(this.url)){//object is local
			iConLayeredNode nextNode = (iConLayeredNode) this.hashtable.get(nextNodeKey);
			nextNode.moveUser(newaddr, userkey,this.addUserDepthLevel);
		}else{//object is remote
			//perform a web servlet call to move user
			iConWeb.getInstance().moveUser(nextNodeUrl, nextNodeUrl, nextNodeKey,nextNodeQuad, newaddr.getAddress(),newaddr.getLatitude(),newaddr.getLongitude(), userkey);
		}
	}
	public iConNodeIdentifier addUser(iConAddress userAddress,iConNodeIdentifier nextNode,int userdepth,int userkey){
		int nextNodeKey=nextNode.getKey();
		String nextNodeUrl=nextNode.getUrl();
		if(url.equals(nextNodeUrl)){
			iConLayeredNode node = (iConLayeredNode) this.hashtable.get(nextNodeKey);
			/*
			 * if at correct depth, create iConUser node and addit to this hashtable
			 * 
			 * 
			 */
			if(userdepth==(node.getLevel())){
				node.addUserkey(userkey,node.getParentQuadrant());
				//P.print(this.toString(), "should stop now************");
				return nextNode;
			}
		    return node.addUser(userAddress, userdepth,userkey);
		}else{
			//perform http call to another server
			String res = iConWeb.getInstance().addUser(nextNodeUrl, nextNodeUrl, nextNodeKey, nextNode.getQuadrant(), userAddress.getAddress(), userAddress.getLatitude(), userAddress.getLongitude(), userdepth,userkey);
			StringTokenizer st = new StringTokenizer(res);
			iConNodeIdentifier ident = new iConNodeIdentifier("",0,"");
			String url = st.nextToken();
			String key = st.nextToken();
			String loc = st.nextToken();
			ident.setUrl(url);
			ident.setKey(new Integer(key));
			ident.setQuadrant(loc);
			return ident;
		}
	}
	
	public int getKey(){
		return random.nextInt();
	}
	
	public String getUrl(){
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void showhashmap(){
		Set<Integer> keys = this.hashtable.keySet();
		Iterator<Integer> it =keys.iterator();
		while(it.hasNext()){
			Integer key = it.next();
			iConNode node = hashtable.get(key);
			P.print("", node.toString());
		}
	}
	
	public void showlocationtree(){
		Set<Integer> keys = this.hashtable.keySet();
		iConNode[] list = new iConNode[keys.size()];
		Iterator<Integer> it =keys.iterator();
		int index=0;
		while(it.hasNext()){
			Integer key = it.next();
			iConNode node = hashtable.get(key);
			list[index]=node;
			index++;
		}
		//order list
		boolean done=false;
		boolean changemade=false;
		while(!done){
			changemade=false;
			for(int i=0;i<list.length-1;i++){
				if(list[i].getLevel()>list[i+1].getLevel()){
					iConNode tmp=list[i];
					list[i]=list[i+1];
					list[i+1]=tmp;
					changemade=true;
				}
			}
			if(!changemade)
				done=true;
		}
		//print list
		int currlevel=0;
		for(int i=0;i<list.length;i++){
			iConNode node = list[i];
			if(node.getLevel()==currlevel){
				System.out.print("NL:"+node.getLevel()+":"+node.getParentQuadrant());
				if(node instanceof server.iConLayeredNode){
					Set<Integer> nodesRegUsrKeys = ((iConLayeredNode)node).getRegisteredUserKeys();
					Iterator<Integer> it2 = nodesRegUsrKeys.iterator();
					while(it2.hasNext()){
						System.out.print(":"+it2.next()+":");
					}
				}
			}else{
				currlevel=node.getLevel();
				System.out.println("");
				System.out.print("NL:"+node.getLevel()+":"+node.getParentQuadrant());
				if(node instanceof server.iConLayeredNode){
					Set<Integer> nodesRegUsrKeys = ((iConLayeredNode)node).getRegisteredUserKeys();
					Iterator<Integer> it2 = nodesRegUsrKeys.iterator();
					while(it2.hasNext()){
						System.out.print(":"+it2.next()+":");
					}
				}
			}
			if(node instanceof server.iConUser){
				System.out.print("o:"+node.getKey()+" ");
			}else{
				System.out.print(" ");
			}
		}
	}

	
}
