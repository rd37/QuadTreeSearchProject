package server;

public class iConNodeIdentifier {
	private String quadrant;
	private String url;
	private int key;
	
	public String toString(){
		return url+" "+key+" "+quadrant;
	}
	
	public iConNodeIdentifier(String url,int key,String quadrant){
		this.url=url;
		this.key=key;
		this.quadrant=quadrant;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public int getKey() {
		return key;
	}

	public void setQuadrant(String quadrant) {
		this.quadrant = quadrant;
	}

	public String getQuadrant() {
		return quadrant;
	}
}
