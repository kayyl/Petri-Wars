package gameserver;

import gameserver.util.ByteBuilder;

import java.io.Serializable;

public final class ServerGram {
	private static final long serialVersionUID = -1306578177842521747L;
	
	private int type;
	private Class<?> bodytype;
	private Serializable body;
	
	protected ServerGram(){}
	public ServerGram(int messageType, Serializable messageBody){
		type = messageType; body = messageBody;
		if (messageBody != null)
			bodytype = messageBody.getClass();
	}
	
	//public void setType(int type) {this.type = type;}
	public int getType() {return type;}
	public void setBody(Serializable body) {this.body = body;}
	public Serializable getBody() {return body;}
	public Class<?> getBodyClass() {return bodytype;}
	
	public byte[] toByteStructure() {
		ByteBuilder bb = new ByteBuilder();
		bb.appendSpecial(type, 1, false);
		if (body != null)
			bb.append(body);
		
		return bb.toByteArray();
	}
	
}
