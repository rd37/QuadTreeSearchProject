package server;

public class iConUser implements iConNode
{
	private int key;
	@Override
	public int getKey() {
		return key;
	}

	@Override
	public void setKey(int key) {
		this.key = key;
	}

}
