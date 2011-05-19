/*
 *  Socket.h
 *  PetriWars
 *
 *  Created by Tim Pittman on 3/4/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef _SOCKET_H_
#define _SOCKET_H_

#include "ServerMessage.h"

class ServerSocket {
private:
	bool udp;

#ifdef SOCKET_WINSOCK
	SOCKET sockdesc; //probably the same as below, but whatever
#else
	int sockdesc;
#endif

public:
	ServerSocket(bool useUDP = false);
	~ServerSocket();
	
	bool isOpen();
	
	bool openSocket(const char* ip, const char* port = SERVERPORT);
	void closeSocket();
	
	bool sendMessage(ServerMessage *msg);
	void* recvMessage();
	
	bool sendRaw(int len, char* msg);
	bool recvString(int maxlen, char* outmsg);
	
	int getRecvLengthForType(int msgtype);
};	


#endif
