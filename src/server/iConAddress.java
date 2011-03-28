package server;

public class iConAddress {
	private String ipaddress;
	private double latitude;
	private double longitude;
	
	public String toString(){
		return "lat:"+latitude+":long:"+longitude;
	}
	
	public iConAddress(String ipaddress,double latitude,double longitude){
		this.ipaddress=ipaddress;
		this.latitude=latitude;
		this.longitude=longitude;
	}
	
	public String getAddress() {
		return ipaddress;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setAddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
}
