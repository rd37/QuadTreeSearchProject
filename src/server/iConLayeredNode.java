package server;

import java.util.LinkedList;

public class iConLayeredNode implements iConNode{
	private int hashkey;
	private LinkedList<Integer> userkeys = new LinkedList<Integer>();
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
	
	public iConLayeredNode(int level,int key,iConAddress range1,iConAddress range2){
		this.level = level;
		this.hashkey = key;
		this.range1=range1;
		this.range2=range2;
		this.createShiftedRangeLatitude(range1, range2);
		this.createShiftedRangeLongitude(range1, range2);
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
	
	public void createUserNode(int key){
		int prevKey=this.hashkey;
		if(prevPeer!=null){
			prevKey=prevPeer.getKey();
		}
		if(this.hashkey==prevKey){//this is the only node at this level
			/*
			 * Create a new node and add to this iConServer
			 */
			iConUser userNode = new iConUser();
			userNode.setKey(key);
			iConServer.getInstance().addNodeToHashTable(userNode, key);
		}else if(key<this.hashkey&&key>prevKey){ // key is OK to belong to this node
			/*
			 * Create a new node and add to this iConServer
			 */
			iConUser userNode = new iConUser();
			userNode.setKey(key);
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
			iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2);
			iConServer.getInstance().addNodeToHashTable(newNode, key);
			alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
		}else if(key<this.hashkey&&key>prevKey){ // key is OK to belong to this node
			/*
			 * Create a new node and add to this iConServer
			 */
			iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2);
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
		if( shiftedrange1.getLongitude()<longitude && shiftedrange2.getLongitude()>longitude ){
			if( shiftedrange1.getLatitude()<latitude && shiftedrange2.getLatitude()>latitude ){
				iConNodeIdentifier alr;
				String addrPartition="";
				int addrPartitionIndex=-1;
				iConAddress subRange1;
				iConAddress subRange2;
				if(shiftedrange1.getLongitude()<longitude && (shiftedrange2.getLongitude()/2)>longitude ){
					if( shiftedrange1.getLatitude()<latitude && (shiftedrange2.getLatitude()/2) >latitude ){
						//check A
						alr = addrrouting[0];
						addrPartition="A";
						addrPartitionIndex=0;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLongitude(),this.range1.getLatitude()+(shiftedrange2.getLatitude()/2));
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLongitude()+(shiftedrange2.getLongitude()/2),this.range2.getLatitude());
					}else{
						//check C
						alr = addrrouting[2];
						addrPartition="C";
						addrPartitionIndex=2;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLongitude(),this.range1.getLatitude());
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLongitude()+(shiftedrange2.getLongitude()/2),this.range2.getLatitude()-shiftedrange2.getLatitude()/2);
					}
				}else{
					if( shiftedrange1.getLatitude()<latitude && (shiftedrange2.getLatitude()/2) >latitude ){
						//check B
						alr = addrrouting[1];
						addrPartition="B";
						addrPartitionIndex=1;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLongitude()-(shiftedrange2.getLongitude()/2),this.range1.getLatitude()+(shiftedrange2.getLatitude()/2));
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLongitude(),this.range2.getLatitude());
					}else{
						//check D
						alr = addrrouting[3];
						addrPartition="D";
						addrPartitionIndex=3;
						subRange1 = new iConAddress(range1.getAddress(),this.range1.getLongitude()-(shiftedrange2.getLongitude()/2),this.range1.getLatitude());
						subRange2 = new iConAddress(range2.getAddress(),this.range2.getLongitude(),this.range2.getLatitude()-shiftedrange2.getLatitude()/2);
					
					}
				}
				if(alr==null){
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
						iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2);
						iConServer.getInstance().addNodeToHashTable(newNode, key);
						alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
					}else if(key<this.hashkey&&key>prevKey){ // key is OK to belong to this node
						/*
						 * Create a new node and add to this iConServer
						 */
						iConLayeredNode newNode = new iConLayeredNode(level+1,key,subRange1,subRange2);
						iConServer.getInstance().addNodeToHashTable(newNode, key);
						alr=new iConNodeIdentifier(iConServer.getInstance().getUrl(),key,addrPartition);
					}else{//key is out of range of this node. resend request to either next or prev node
						iConNodeIdentifier nextNode = new iConNodeIdentifier(nextPeer.getUrl(),nextPeer.getKey(),nextPeer.getQuadrant());
						alr = iConServer.getInstance().createChildNode(nextNode,key,subRange1,subRange2,addrPartition);
					}
					addrrouting[addrPartitionIndex]=alr;
				}
				return alr;
			}
		}
		return null;
	}

	public iConNodeIdentifier addUser(iConAddress userAddress,int addUserDepthLevel,int userkey){
		iConNodeIdentifier childNode = this.getChildNode(userAddress);
		this.userkeys.add(userkey);
		return iConServer.getInstance().addUser(userAddress, childNode,addUserDepthLevel,userkey);
	}
	
	public void addUserkey(int userkey){
		this.userkeys.add(userkey);
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

	
}
