package server;

import java.util.LinkedList;

public class iConUser implements iConNode
{
	private int key;
	private String parentQuadrant="";
	private int level=0;
	private iConAddress address;
	private LinkedList<Integer> textmessagekeys = new LinkedList<Integer>();
	private LinkedList<Integer> audiomessagekeys = new LinkedList<Integer>();
	
	public void addMessage(int key,int type){
		if(type==0){
			textmessagekeys.add(key);
		}else if(type==1){
			audiomessagekeys.add(key);
		}
	}
	
	public int getMsgCount(int type){
		if(type==0){
			return textmessagekeys.size();
		}else if(type==1){
			return audiomessagekeys.size();
		}
		return -1;
	}
	
	public int getMessage(int type){
		if(type==0){
			if(!textmessagekeys.isEmpty()){
				return textmessagekeys.removeFirst();
			}
		}else if(type==1){
			if(!audiomessagekeys.isEmpty()){
				return audiomessagekeys.removeFirst();
			}
		}
		return -1;
	}
	@Override
	public int getKey() {
		return key;
	}

	@Override
	public void setKey(int key) {
		this.key = key;
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return level;
	}
	
	public void setLevel(int level){
		this.level=level;
	}

	@Override
	public String getParentQuadrant() {
		// TODO Auto-generated method stub
		return parentQuadrant;
	}
	
	public void setParentQuadrant(String quad){
		this.parentQuadrant=quad;
	}

	public void setAddress(iConAddress address) {
		this.address = address;
	}

	public iConAddress getAddress() {
		return address;
	}

}
