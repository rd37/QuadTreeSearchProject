package server;

public class iConUser implements iConNode
{
	private int key;
	private String parentQuadrant="";
	private int level=0;
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

}
