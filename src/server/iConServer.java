package server;

import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

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
	
	public void intialize(int seed,int depthLevel){
		random=new Random(seed);
		addUserDepthLevel=depthLevel;
		int key=random.nextInt();
		iConAddress range1 = new iConAddress(url,48.470, -123.320);
		iConAddress range2 = new iConAddress(url,48.456, -123.306);
		rootNode = new iConLayeredNode(0,key,range1,range2);
		hashtable.put(key, rootNode);
		print("Created root LayeredNode "+key);
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
	
	public void createUserNode(iConNodeIdentifier nextPeer,int key){
		String peerUrl=nextPeer.getUrl();
		if(peerUrl.equals(this.url)){
			iConLayeredNode nextNode = (iConLayeredNode) this.hashtable.get(key);
			nextNode.createUserNode(key);
		}else{//need to perform a web call
			iConWeb.getInstance().createUserNode(nextPeer.getQuadrant(),nextPeer.getKey(),nextPeer.getUrl(),key);
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
		rootNode.createUserNode(userkey);
		rootNode.addUser(address,addUserDepthLevel,userkey);
		return userkey;
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
			if(userdepth==node.getLevel()){
				node.addUserkey(userkey);
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
	
	private void print(String msg){
		System.out.println("iConServer::"+url+"::"+msg);
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
