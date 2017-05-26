/*
 * This source code was published under GPL v3
 *
 * Copyright (C) 2017 Too-Naive
 *
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class GameServer extends NetworkFather{
	int playerCount;
	String[] clientAddressStrings;
	ReentrantLock threadLock;
	//String[] syncStrings;
	SocketSendMsg[] socketSendMsg;
	Hand[] hand;
	static OnlineUser onlineUser;
	static CardStore cardStore = null;
	GameServer(int _playerCount){
		this.playerCount = _playerCount;
		this.clientAddressStrings = new String[this.playerCount];
		this.threadLock = new ReentrantLock();
		//this.syncStrings = new String[this.playerCount];
		this.socketSendMsg = new SocketSendMsg[this.playerCount];
		this.hand = new Hand[this.playerCount];
		if (cardStore == null)
			cardStore = new CardStore(this.playerCount);
	}
	public void listenRequest(){
		ServerSocket serverSocket = null;
		ExecutorService threadExecuteor = Executors.newCachedThreadPool();
		try{
			serverSocket = new ServerSocket(serverPort);
			while (true){
				Socket socket = serverSocket.accept();
				int clientID;
				for (clientID = -1;clientID<this.playerCount &&
					clientAddressStrings[++clientID]!=socket.getInetAddress().getHostAddress(););
				if (clientID == this.playerCount)
					continue;
				onlineUser.write(socket);
				/*{
					IndexOutOfBoundsException indexOutOfBoundsException = 
						new IndexOutOfBoundsException();
					throw indexOutOfBoundsException;
				}*/
				threadExecuteor.execute(new RequestThread(
					socket,socketSendMsg[clientID]
					));
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally{
			if (threadExecuteor != null)
				threadExecuteor.shutdown();
			if (serverSocket != null)
				try{
					serverSocket.close();
				}
				catch (IOException e){
					e.printStackTrace();
				}
		}
	}

	public void insertClient(String clientAddress){
		for (int i = 0;i < this.playerCount ; i++)
			if (this.clientAddressStrings[i] == null){
				this.clientAddressStrings[i] = clientAddress;
				return ;
			}
		IndexOutOfBoundsException indexOutOfBoundsException = 
			new IndexOutOfBoundsException();
		throw indexOutOfBoundsException;
	}
	public void insertClient(String szClientAddress[]){
		for (String i : szClientAddress)
			this.insertClient(i);
	}
	
	String __getMsgString__(Hand h){
		String str = String.format("%d\\n%d\\n", h.getPoint(),h.getEachPointVector().size());
		for (Integer x:h.getEachPointVector())
			str += String.format("%d\\n", x);
		return str;
	}

	public void createServer(){
		while (true){
			for (int i = 0;i < this.playerCount; i++ ){
				while (hand[i].addPoint(cardStore.getNextPoint()));
				this.socketSendMsg[i].setMsg(this.__getMsgString__(hand[i]));
			}
			/*for (Hand h:hand){
				while(h.addPoint(cardStore.getNextPoint()));
			}*/
		}
	}

	class RequestThread implements Runnable{
		private Socket clientSocket;
		//private OnlineUser onlineUser;
		//private int clientID;
		SocketSendMsg socketSendMsg;
		//ReentrantLock threadLock;
		public RequestThread(
			//int _clientID,
			Socket _clientSocket,
			//OnlineUser _onlineUser,
			//ReentrantLock _threadLock
			SocketSendMsg _socketSendMsg
			)
			throws IndexOutOfBoundsException {
				//this.clientID = _clientID;
				this.clientSocket = _clientSocket;
				//this.onlineUser = _onlineUser;
				//this.threadLock = _threadLock;
				this.socketSendMsg = _socketSendMsg;
		}

		@Override
		public void run(){
			DataInputStream dataInputStream = null;
			DataOutputStream dataOutputStream = null;
			try {
				dataInputStream = new DataInputStream(this.clientSocket.getInputStream());
				dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());
				if (dataInputStream.readUTF() == "SYN"){
					dataOutputStream.writeUTF("ACK");
					while (true){
						//this.threadLock.lock();
						
					}
				}
			}
			catch (IOException e){
				e.printStackTrace();
			}
			finally{
				try{
					if (dataInputStream != null) dataInputStream.close();
					if (dataOutputStream != null) dataOutputStream.close();
					if (this.clientSocket != null && !this.clientSocket.isClosed())
						this.clientSocket.close();
				}
				catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}


class Hand{
	int point;
	Vector<Integer> eachPoint;
	Hand(){
		eachPoint = new Vector<Integer>();
		this.point = 0;
	}
	public boolean addPoint(int _point){
		this.point+= _point;
		eachPoint.add(new Integer(_point));
		return this.point>17?true:false;
	}
	public void clearPoint(){
		this.point = 0;
		eachPoint.clear();
		return ;
	}
	public Vector<Integer> getEachPointVector(){
		return this.eachPoint;
	}
	public int getPoint(){
		return this.point;
	}
}


class CardStore{
	static int card[] = null,step,player;
	static Random rand;
	//Remove Ghost because 21 point no need
	private void initCard(){
		step = 0;
		boolean tmpsz[] = new boolean[52];
		for (int i=0,tmp;i<52;i++){
			while (tmpsz[tmp = rand.nextInt(52)]);
			card[i] = tmp;
			tmpsz[tmp] = true;
		}
	}
	public CardStore(int players){
		// https://goo.gl/XVQGpD
		if (card != null)
			return ;
		card = new int[52];
		rand = new Random();
		player = players;
		this.initCard();
	}
	public int getNextPoint(){
		if (step+player*3+4>52)
			this.initCard();
		return ((card[step++]+1)%13)>10?10:card[step-1];
	}
	public void Debug_showCardSz(){
		Arrays.sort(card);
		for (int i=0;i<52;i++)
			System.out.printf("%d ",card[i]);
	}
}

class OnlineUser{
	int onlineUserCount;
	String[] onlineUserStrings;
	int playerLimit;
	OnlineUser(int totalPlayer){
		this.onlineUserCount = 0;
		this.playerLimit = totalPlayer;
		this.onlineUserStrings = new String[this.playerLimit];
	}
	public void write(String remoteAddress){
		boolean is_repeat = false;
		for (String x:this.onlineUserStrings)
			if (remoteAddress == x){
				is_repeat = true;
				break;
			}
		if (is_repeat)
			return ;
		onlineUserStrings[onlineUserCount] = remoteAddress;
		onlineUserCount++;
	}
	public void write(Socket remoteSocket){
		this.write(remoteSocket.getInetAddress().getHostAddress());
	}
	public int getOnlineUserCount() {
		return onlineUserCount;
	}
	public String[] getOnlineUserStrings() {
		return onlineUserStrings;
	}

}

class SocketSendMsg{
	String msg;
	boolean needSend;
	SocketSendMsg(){
		this.msg = "";
		this.needSend = false;
	}
	void reset(){
		this.msg = "";
		this.needSend = false;
	}
	public void setMsg(String _msg){
		this.msg = _msg;
		this.needSend = true;
	}
	public String getMsg(){
		String tmpMsg = this.msg;
		this.reset();
		return tmpMsg;
	}
}
