/*
 *  ServerInterface.h
 *  PetriWars
 *
 *  Created by Tim Pittman on 2/26/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef _SERVER_INTERFACE_H_
#define _SERVER_INTERFACE_H_

#include "ServerMessage.h"
#include "ServerSocket.h"
#include "CommonTypes.h"
#include "Configurable.h"
#include "SThread.h"
#include "ChatGUI.h"
#include <queue>
#include <string>

using namespace std;

class ServerInterface {
//singleton
private:
	static ServerInterface* singleton;
public:
	static ServerInterface* getInstance();
	static void cleanup();

//the rest
protected:
/*	class UnitCommand {
		int units[UNIT_SELECT_MAX]; //up to UNIT_SELECT_MAX unit ids
		int command; //which command to issue
		int posx, posy; //location, if applicable
		int targetid; //target unit id, if applicable
	};
	
	queue<UnitCommand> commandQueue; //*/
	
	ServerSocket* tcpsock;
	ServerSocket* udpsock;
	SThread* tcpThread;
	SThread* udpThread;
	
	// -- GameServer Credentials -- //
	string username;
	// -------- //
	
	
	bool send(ServerMessage *msg);
	ServerMessage* recv();
	
public:
	ChatGUI* chatgui; //refrence only, does not own
	
	ServerInterface();
	~ServerInterface();
	
	void setUsername(const string& username);
	
	bool tcpIsOpen();
	bool udpIsOpen();
	
	bool connectTo(const char* servername, u16 port);
//	bool connectUDP(const char* ip, const char* port);
	bool disconnect();
	
	bool sendCommand(int command, int numUnits, int *unitArray, float posx, float posy, int targetid);
	bool sendChat(string* msg);
	bool sendCredentials();
	bool sendMeta(int type, char* data = NULL);
	
	bool recvMessage(ServerMessage* msg);
	void* recvUDP();
};

u64 htonll(const u64 l);
u64 ntohll(const u64 l);

void* TCPResponderThread(void* arg);
void* UDPUpdateThread(void* arg);

#endif
