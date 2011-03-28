package server;

import java.util.HashMap;
import java.util.Set;
//import java.util.LinkedList;

import p.P;

public class iConLayeredNode implements iConNode{
	private int hashkey;
	private HashMap<Integer,Object> userkeys = new HashMap<Integer,Object>();
	//private LinkedList<Integer> userkeys = new LinkedList<Integer>();
	private int level;
	private iConAddress range1;
	private iConAddress range2;
	private iConAddress shiftedrange1=new iConAddress("",0,0);
	private iConAddress shiftedrange2=new iConAddress("",0,0);
	private iConNodeIdentifier[] addrrouting = new iConNodeIdentifier[4];
	private iConNodeIdentifier nextPeer=null;
	private iConNodeIdentifier prevPeer=null;
	private double latShift=0;
	private double longShift=0;
	private String parentQuadrant="";
	
	public String toString(){
		return "iConLayeredNode:"+hashkey+":lvl:"+level+":rng1:"+range1.toString()+":rng2:"+range2.toString();
	}
	
	/*public String toString2(){
		return "iConLayeredNode:"+hashkey+":lvl:"+level;
	}*/
	
	public iConLayeredNode(int level,int key,iConAddress range1,iConAddress range2,String parentQuadrant){
		this.level = level;
		this.hashkey = key;
		this.range1=range1;
		this.range2=range2;
		this.parentQuadrant=parentQuadrant;
		this.createShiftedRangeLatitude(range1, range2);
		this.createShiftedRangeLongitude(range1, range2);
		nextPeer=prevPeer=new iConNodeIdentifier(iConServer.getInstance().getUrl(),this.getKey(),"");
		//P.print(this.toString2(), "rng1:"+range1.getLatitude()+":"+range1.getLongitude()+" rng2:"+range2.getLatitude()+":"+range2.getLongitude()+" s_rng1:"+shiftedrange1.getLatitude()+":"+shiftedrange1.getLongitude()+" s_rng2:"+shiftedrange2.getLatitude()+":"+shiftedrange2.getLongitude()+" lat_off:"+this.latShift+" long_off:"+this.longShift);
	}
	
	private void createShiftedRangeLatitude(iConAddress rng1,iConAddress rng2){
		if(rng1.getLatitude()>=0 && rng2.getLatitude()>=0){
			latShift=-rng1.getLatitude();
			shiftedrange1.setLatitude(0);
			shiftedrange2.setLatitude(rng2.getLatitude()+latShift);
		}else if(rng1.getLatitude()<=0 && rng2.getLatitude()>=0) {
			latShift=-rng1.getLatitude();
			shiftedrange1.setLatitude(0);
			shiftedrange2.setLatitude(rng2.getLatitude()+latShift);
		}else if(rng1.getLatitude()<=0 && rng2.getLatitude()<=0) {
			latShift=-rng1.getLatitude();
			shiftedrange1.setLatitude(0);
			shiftedrange2.setLatitude(rng2.getLatitude()+latShift);
		}
	}
	
	private void createShiftedRangeLongitude(iConAddress rng1,iConAddress rng2){
		if(rng1.getLongitude()>=0 && rng2.getLongitude()>=0){
			longShift=-rng1.getLongitude();
			shiftedrange1.setLongitude(0);
			shiftedrange2.setLongitude(rng2.getLongitude()+longShift);
		}else if(rng1.getLongitude()>=0 && rng2.getLongitude()<=0) {
			longShift=-rng1.getLongitude();
			shiftedrange1.setLongitude(0);
			double longwidth=180-rng1.getLongitude()+(180+rng2.getLongitude());
			shiftedrange2.setLongitude(longwidth);
		}else if(rng1.getLongitude()<=0 && rng2.getLongitude()>=0) {
			longShift=-rng1.getLongitude();
			shiftedrange1.setLongitude(0);
			shiftedrange2.setLongitude(rng2.getLongitude()+longShift);
		}else if(rng1.getLongitude()<=0 && rng2.getLongitude()<=0) {
			longShift=-rng1.getLongitude();
			shiftedrange1.setLongitude(0);
			shiftedrange2.setLongitude(rng2.getLongitude()+longShift);
		}
	}
	
	public void createUserNode(int key,int depth){
		int prevKey=this.hashkey;
		if(prevPeer!=null){
			prevKey=prevPeer.getKey();
		}
		if(this.hashkey==prevKey){//this is the only node at this level
			/*
			 * Create a new node and add to this iConServer
			 */
			//P.print("iconLayeredNode_"+this.level+"_"+this.hashkey,	 "is the only node, so create user and add to this hash space ");
			iConUser userNode = new iConUser();
			userNode.setKey(key);
			userNode.setLevel(depth);
			iConServer.getInstance().addNodeToHashTable(userNode, key);
		}else if(key<this.hashkey&&key>prevKey){ // key is OK to belong to this node
			/*
			 * Create a new node and add to this iConServer
			 */
			iConUser userNode = new iConUser();
			userNode.setKey(key);
			userNode.setLevel(depth);
			iConServer.getInstance().addNodeToHashTable(userNode, key);
		}else{//key is out of range of this node. resend request to either next or prev node
			iConServer.getInstance().createUserNode(nextPeer,key);
		}
	}
	
	public iConNodeIdentifier addChildNode(int key,iConAddress subRange1,iConAddress subRange2,String addrPartition){
		iConNodeIdentifier alr=null;
		//check if within this Nodes key Range
		int prevKey=this.hashkey;
		if(prevPeer!=null){
			prevKey=prevPeer.getKey();
		}
		if(this.hashkey==prevKey){//this is the only node at this level
			/*
			 * Create a new node and add to this iConServer
			 */
			iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2,addrPartition);
			iConServer.getInstance().addNodeToHashTable(newNode, key);
			alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
		}else if(key<this.hashkey&&key>prevKey){ // key is OK to belong to this node
			/*
			 * Create a new node and add to this iConServer
			 */
			iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2,addrPartition);
			iConServer.getInstance().addNodeToHashTable(newNode, key);
			alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
		}else{//key is out of range of this node. resend request to either next or prev node
			iConNodeIdentifier nextNode = new iConNodeIdentifier(nextPeer.getUrl(),nextPeer.getKey(),nextPeer.getQuadrant());
			alr = iConServer.getInstance().createChildNode(nextNode,key,subRange1,subRange2,addrPartition);
		}
		return alr;
	}
	
	public iConNodeIdentifier getChildNode(iConAddress address){
		double latitude=address.getLatitude()+latShift;
		double longitude=address.getLongitude()+longShift;
		//P.print(this.toString2(), "user lat "+latitude+" user long:"+longitude);
		if( shiftedrange1.getLongitude()<longitude && shiftedrange2.getLongitude()>longitude ){
			if( shiftedrange1.getLatitude()<latitude && shiftedrange2.getLatitude()>latitude ){
				//P.print(this.toString2(), "added user fits in this nodes range, find sub range");
				iConNodeIdentifier alr;
				String addrPartition="";
				int addrPartitionIndex=-1;
				iConAddress subRange1;
				iConAddress subRange2;
				if(shiftedrange1.getLongitude()<longitude && (shiftedrange2.getLongitude()/2)>longitude ){
					if( shiftedrange1.getLatitude()<latitude && (shiftedrange2.getLatitude()/2)<latitude ){
						//check A
						//P.print(this.toString2(), "added user fits in this sub-range A,chk if child-node exists");
						alr = addrrouting[0];
						addrPartition="A";
						addrPartitionIndex=0;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLatitude()+(shiftedrange2.getLatitude()/2),this.range1.getLongitude());
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLatitude(),this.range2.getLongitude()+(shiftedrange2.getLongitude()/2));
					}else{
						//check C
						//P.print(this.toString2(), "added user fits in this sub-range C,chk if child-node exists");
						alr = addrrouting[2];
						addrPartition="C";
						addrPartitionIndex=2;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLatitude(),this.range1.getLongitude());
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLatitude()-shiftedrange2.getLatitude()/2,this.range2.getLongitude()+(shiftedrange2.getLongitude()/2));
					}
				}else{
					if( shiftedrange1.getLatitude()<latitude && (shiftedrange2.getLatitude()/2) <latitude ){
						//check B
						//P.print(this.toString2(), "added user fits in this sub-range B,chk if child-node exists");
						alr = addrrouting[1];
						addrPartition="B";
						addrPartitionIndex=1;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLatitude()+(shiftedrange2.getLatitude()/2),this.range1.getLongitude()-(shiftedrange2.getLongitude()/2));
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLatitude(),this.range2.getLongitude());
					}else{
						//check D
						//P.print(this.toString2(), "added user fits in this sub-range D,chk if child-node exists");
						alr = addrrouting[3];
						addrPartition="D";
						addrPartitionIndex=3;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLatitude(),this.range1.getLongitude()-(shiftedrange2.getLongitude()/2));
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLatitude()-shiftedrange2.getLatitude()/2,this.range2.getLongitude());
					
					}
				}
				if(alr==null){
					//P.print(this.toString2(), "child node does not exist, create key then create new node");
					
					/*
					 * there is no child node, so generate a key and request iConServer
					 * to create a new child at a parent node in charge of the key.
					 */
					int key=iConServer.getInstance().getKey();
					//check if within this Nodes key Range
					int prevKey=this.hashkey;
					if(prevPeer!=null){
						prevKey=prevPeer.getKey();
					}
					if(this.hashkey==prevKey){//this is the only node at this level
						/*
						 * Create a new node and add to this iConServer
						 */
						//P.print(this.toString2(), "new child key can belong to this parent node");
						
						iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2,addrPartition);
						iConServer.getInstance().addNodeToHashTable(newNode, key);
						alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
					}else if(key<this.hashkey&&key>prevKey){ // key is OK to belong to this node
						/*
						 * Create a new node and add to this iConServer
						 */
						//P.print(this.toString2(), "new child key can belong to this parent node as well");
						
						iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2,addrPartition);
						iConServer.getInstance().addNodeToHashTable(newNode, key);
						alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
					}else{//key is out of range of this node. resend request to either next or prev node
						//P.print(this.toString2(), "new child key is oout of range, try next peer node");
						
						iConNodeIdentifier nextNode = new iConNodeIdentifier(nextPeer.getUrl(),nextPeer.getKey(),nextPeer.getQuadrant());
						alr = iConServer.getInstance().createChildNode(nextNode,key,subRange1,subRange2,addrPartition);
					}
					addrrouting[addrPartitionIndex]=alr;
				}else{
					//P.print(this.toString2(), "child node exists so return its node identifier");
				}
				return alr;
			}
		}else{
			P.print(this.toString(), "child node did not fit in this range");
		}
		return null;
	}

	public iConNodeIdentifier addUser(iConAddress userAddress,int addUserDepthLevel,int userkey){
		//P.print(this.toString2(), "Entered Node, now find the child node ");
		iConNodeIdentifier childNode = this.getChildNode(userAddress);
		this.userkeys.put(userkey,childNode.getQuadrant());
		return iConServer.getInstance().addUser(userAddress, childNode,addUserDepthLevel,userkey);
	}
	
	public void addUserkey(int userkey,String quadrant){
		this.userkeys.put(userkey,quadrant);
	}
	
	public void moveUser(iConAddress newaddr,int userkey,int addUserDepthLevel){
		String quadrant = (String) this.userkeys.get(userkey);
		iConNodeIdentifier nextNodeid=null;
		if(quadrant.equals("A")){
			nextNodeid=addrrouting[0];
		}else if(quadrant.equals("B")){
			nextNodeid=addrrouting[1];
		}else if(quadrant.equals("C")){
			nextNodeid=addrrouting[2];
		}else if(quadrant.equals("D")){
			nextNodeid=addrrouting[3];
		}
		
		/*
		 * check quadrant, if different quadrant than one user is in, call 
		 * remove user on remainder of nodes.
		 * then add user with new address
		 */
		String newquadrant = this.getQuadrant(newaddr);
		if(quadrant.equals(newquadrant)){//quadrants are the same so no changes here
			iConServer.getInstance().moveUser(nextNodeid, newaddr,userkey);
		}else{//quadrants are different.  so make change from here
			//remove user from system
			iConServer.getInstance().removeUser(nextNodeid,userkey);
			//add user to system
			iConNodeIdentifier childNode = this.getChildNode(newaddr);
			iConServer.getInstance().addUser(newaddr, childNode,addUserDepthLevel,userkey);
		}
	}
	
	public int removeKey(int userkey){
		userkeys.remove(userkey);
		return userkeys.size();
	}
	
	public int getUsersRemaining(){
		return userkeys.size();
	}
	
	public void removeUser(int userkey){
		String oldQuadrant = (String) this.userkeys.get(userkey);
		iConNodeIdentifier nextNodeid=null;
		if(oldQuadrant.equals("A")){
			nextNodeid=addrrouting[0];
		}else if(oldQuadrant.equals("B")){
			nextNodeid=addrrouting[1];
		}else if(oldQuadrant.equals("C")){
			nextNodeid=addrrouting[2];
		}else if(oldQuadrant.equals("D")){
			nextNodeid=addrrouting[3];
		}
		iConServer.getInstance().removeUser(nextNodeid, userkey);
		userkeys.remove(userkey);
	}
	@Override
	public int getKey() {
		return hashkey;
	}

	public int getLevel(){
		return level;
	}
	
	@Override
	public void setKey(int key) {
		this.hashkey = key;
	}

	public void setParentQuadrant(String parentQuadrant) {
		this.parentQuadrant = parentQuadrant;
	}

	public String getParentQuadrant() {
		return parentQuadrant;
	}

	public Set<Integer> getRegisteredUserKeys(){
		return this.userkeys.keySet();
	}
	
	public String getQuadrant(iConAddress address){
		double latitude=address.getLatitude()+latShift;
		double longitude=address.getLongitude()+longShift;
		//P.print(this.toString2(), "user lat "+latitude+" user long:"+longitude);
		if( shiftedrange1.getLongitude()<longitude && shiftedrange2.getLongitude()>longitude ){
			if( shiftedrange1.getLatitude()<latitude && shiftedrange2.getLatitude()>latitude ){
				//P.print(this.toString2(), "added user fits in this nodes range, find sub range");
				
				if(shiftedrange1.getLongitude()<longitude && (shiftedrange2.getLongitude()/2)>longitude ){
					if( shiftedrange1.getLatitude()<latitude && (shiftedrange2.getLatitude()/2)<latitude ){
						return "A";
					}else{
						return "C";
					}
				}else{
					if( shiftedrange1.getLatitude()<latitude && (shiftedrange2.getLatitude()/2) <latitude ){
						return "B";
					}else{
						return "D";
					}
				}
				
			}
		}else{
			P.print(this.toString(), "address did not fit in this range");
		}
		return null;
	}
}
