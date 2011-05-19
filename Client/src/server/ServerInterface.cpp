/*
 *  ServerInterface.cpp
 *  PetriWars
 *
 *  Created by Tim Pittman on 2/26/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#include <cstdio>
#include <iostream>
#include "ServerInterface.h"
#include "GameSingleton.h"
#include "Configurable.h"

ServerInterface* ServerInterface::singleton = 0;
ServerInterface* ServerInterface::getInstance(){
	if (singleton == 0) {
		singleton = new ServerInterface();
	}
	return singleton;
}

void ServerInterface::cleanup(){
	delete singleton;
}

//////////////////////////////////////////

bool ServerInterface::send(ServerMessage *msg){
	try {
		bool res = tcpsock->sendMessage(msg);
		return res;
	} catch (string e) {
		std::cerr << "Error while sending: " << e << std::endl;
		return false;
	}
}

ServerMessage* ServerInterface::recv(){
	try {
		ServerMessage* msg = (ServerMessage*) tcpsock->recvMessage(); //returned point is now ours
		return msg;
	} catch (string e) {
		std::cerr << "Error while receiving: "<< e << std::endl;
		return NULL;
	}
}

void* ServerInterface::recvUDP(){
	try {
		void* msg = udpsock->recvMessage(); //returned point is now ours
		return msg;
	} catch (string e) {
		std::cerr << "Error while receiving: "<< e << std::endl;
		return NULL;
	}
}

//host to network (byte order) long long
u64 htonll(const u64 l){
	u32 high = (u32)(l >> 32);
	u32 low = (u32)(l);
	
	u32 chigh = htonl(high);
	u32 clow = htonl(low);
	
	if (high == chigh && low == clow) //nothing happened
		return l;
	
	u64 cl = (((u64)chigh) & 0xFFFFFFFF) | ((u64)clow) << 32;
	return cl;
}
//network to host (byte order) long long
u64 ntohll(const u64 l){
	u32 high = (u32)(l >> 32);
	u32 low = (u32)(l);
	
	u32 chigh = ntohl(high);
	u32 clow = ntohl(low);
	
	if (high == chigh) //nothing happened
		return l;
	
	u64 cl = (((u64)chigh) & 0xFFFFFFFF) | ((u64)clow) << 32;
	return cl;
}

u16 floatToShort(const float f){
	return (short)(f * 100.0f);
//	float ff = f * 100.0f;
//	short s = (short)ff;
//	return s;
}

float shortToFloat(const u16 s){
	return (float)(s * 0.01f);
}

/////////////////////////////////////////////////////

ServerInterface::ServerInterface(){
	tcpsock = new ServerSocket(false);
	udpsock = new ServerSocket(true);
}

ServerInterface::~ServerInterface(){
	delete tcpsock;
	delete udpsock;
	delete tcpThread;
	delete udpThread;
}

void ServerInterface::setUsername(const string& username){
	this->username = username;
}

bool ServerInterface::tcpIsOpen(){
	return tcpsock->isOpen();
}
bool ServerInterface::udpIsOpen(){
	return udpsock->isOpen();
}

bool ServerInterface::connectTo(const char* servername, u16 port){
	char portstr[6];
	sprintf(portstr, "%d", port);
	std::cout << "Connecting to " << servername << ":" << port << "...\t" << std::endl;
	std::cout.flush();
	if (!tcpsock->openSocket(servername, portstr)){
		std::cout << "[ERROR]" << std::endl;
		return false;
	}
	std::cout << "[ OK  ]" << std::endl;
	
	char msg[256];
	tcpsock->recvString(255, msg);
	
	std::cout << msg << std::endl;
	
	memset(msg, 0, 256);
	#if defined(_WIN32) || defined(_WIN64)
		const char* os = "Win32";
	#elif defined(__APPLE__)
		const char* os = "MacOS";
	#elif defined(__linux) || defined(__unix)
		const char* os = "Unix";
	#else
		const char* os = "???OS";
	#endif
	int len = sprintf(msg, "PetriWars %1.1f C++ %s %d\n", GAME_VERSION, os, sizeof(void*)*8);
	
	tcpsock->sendRaw(len, msg);
	
	udpsock->openSocket(NULL, "12184");
	
	tcpThread = new SThread(CASTTHREAD TCPResponderThread, this);
	udpThread = new SThread(CASTTHREAD UDPUpdateThread, this);
	
	return true;
}

bool ServerInterface::disconnect(){
	tcpsock->closeSocket();
	udpsock->closeSocket();
	return true;
}
	
bool ServerInterface::sendCommand(int command, int numUnits, int *unitArray, float posx, float posy, int targetid){
	if (numUnits > UNIT_SELECT_MAX) throw "Trying to send too many units in one message!";
	if (numUnits > 0 && unitArray == NULL) throw "Passing null array when number of units is more than 0!";
	
	struct MsgData_Command cmd;
	
	cmd.tick = htonll(GameInstance::getInstance()->currTick);
	cmd.command = command;
	cmd.numunits = numUnits;
	for (int i = 0; i < numUnits; i++) {
		cmd.uid[i] = htons(unitArray[i]);
	}
//	for (int i = 0; i < UNIT_SELECT_MAX; i++) {
//		cmd.uid[i] = htons(0xA1B2);
//	}
	cmd.targetid = htons(targetid);
	cmd.destx = htons(floatToShort(posx));
	cmd.desty = htons(floatToShort(posy));
	
	ServerMessage* msg = new ServerMessage();
	msg->type = MSGTYPE_COMMAND;
//	msg->length = sizeof(struct MsgData_Command);
	memcpy(msg->data, &cmd, sizeof(struct MsgData_Command));
	
	bool res = this->send(msg);
	
	delete msg;
	return res;
}

bool ServerInterface::sendChat(string *str){
	if (str->length() > CHAT_LENMAX) throw "Chat message too long!";
	
/*	struct MsgData_Chat chat;
	chat.name_length = 34;
	memset(chat.name, 0, 34);
	chat.message_length = htonl(str->length());
	memcpy(chat.message, str->c_str(), str->length());*/
	
	ServerMessage* msg = new ServerMessage();
	msg->type = MSGTYPE_CHAT;
//	memcpy(msg->data, &chat, sizeof(struct MsgData_Chat));
	u32 namelen = 0;
	memcpy(msg->data, &namelen, 4);
	u32 charlen = htonl(str->length());
	memcpy(msg->data+4, &charlen, 4);
	strncat((char*)msg->data+8, str->c_str(), CHAT_LENMAX);
	
	bool res = this->send(msg);
	
	delete msg;
	return res;
}

bool ServerInterface::sendCredentials(){
	struct MsgData_ServerCredentials cred;
	cred.userid = -1;
	cred.username_length = htonl(this->username.length());
	strncpy(cred.username, this->username.c_str(), this->username.length());
	
	ServerMessage* msg = new ServerMessage();
	msg->type = MSGTYPE_CREDENTIALS;
	memcpy(msg->data, &cred, sizeof(struct MsgData_ServerCredentials));
	
	bool res = this->send(msg);
	
	delete msg;
	return res;
}

bool ServerInterface::sendMeta(int type, char* data){
	return false;
}


bool ServerInterface::recvMessage(ServerMessage* msg){
	ServerMessage* m = this->recv(); //returned pointer is now ours
	if (m == NULL) return false;
	
	memcpy(msg, m, sizeof(ServerMessage));
	delete m; //now m is not leaked
	return true;
}


////////////////////////////////////////

void* TCPResponderThread(void* arg){
	ServerInterface* si = (ServerInterface*) arg;
	ServerMessage themsg;
	
	while (si->tcpIsOpen()) {
		if (!si->recvMessage(&themsg)) break;
		printf("msgtype: %d\n", themsg.type);
		switch (themsg.type) {
			case MSGTYPE_DISCONNECT:{
				u32 len;
				memcpy(&len, themsg.data, 4);
				len = ntohl(len);
				
				char* reason = new char[len+1];
				memcpy(reason, themsg.data+4, len);
				
				std::cerr << "DISCONNECTED FROM SERVER: " << reason << std::endl;
				delete[] reason;
				throw "Disconnected by Server";
			} break;
			case MSGTYPE_CANNOT:{
				u32 len;
				memcpy(&len, themsg.data, 4);
				len = ntohl(len);
				
				char* reason = new char[len+1];
				memcpy(reason, themsg.data+4, len);
				
				std::cerr << "CANNOT MESSAGE RECEIVED: " << reason << std::endl;
				delete[] reason;
			} break;
			case MSGTYPE_CREDENTIALS:{
				si->sendCredentials();
			} break;
			case MSGTYPE_CHAT:{
				int namelen = 0;
				memcpy(&namelen, themsg.data, 4);
				namelen = ntohl(namelen);
				char* name = new char[namelen];
				memcpy(name, themsg.data+4, namelen);
				
				int msglen = 0;
				memcpy(&msglen, themsg.data+4+namelen, 4);
				msglen = ntohl(msglen);
				char* msgar = new char[msglen+1];
				memcpy(msgar, themsg.data+8+namelen, msglen);
				msgar[msglen] = 0;
				
				si->chatgui->pushMessage(name, 0, msgar);
				delete[] name;
				delete[] msgar;
			} break;
			case MSGTYPE_PLAYERID:{
				char id = (char)(*themsg.data);
				GameInstance::getInstance()->playerid = id;
				printf("player: %d", GameInstance::getInstance()->playerid);
			} break;
			case MSGTYPE_GAMESET:{
				char id = (char)(*themsg.data);
				GameInstance::getInstance()->winnerid = id;
				printf("GAME SET MATCH! %d", GameInstance::getInstance()->winnerid);
			} break;
			default: break;
		}
	}
	std::cerr << "TCP THREAD STOPPING!" << std::endl;
	return NULL;
}

void* UDPUpdateThread(void* arg){
	ServerInterface* si = (ServerInterface*) arg;
	while (si->udpIsOpen()) {
		u8* data = (u8*) si->recvUDP();
		if (data == NULL) continue;

//		printf("%x %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x %x\n", data[0], data[1], data[2], data[3], data[4], data[5],
//		data[6], data[7], data[8], data[9], data[10], data[11], data[12], data[13], data[14], data[15], data[16], data[17]);

		u32 len = 0;
		memcpy(&len, data, sizeof(u32));
		len = htonl(len);
		
		u64 incomingtick = *(u64*)(data+4);
//		memcpy(&incomingtick, data+4, sizeof(u64));
		incomingtick = ntohll(incomingtick);
		
//		printf("tick!: %d\n", incomingtick);
		
		if (GameInstance::getInstance()->currTick < incomingtick) {
			//only update if the tick this packet updates to is greater than the current tick
			
			//TODO mutex start!
			
			memset(GameInstance::getInstance()->latestunitinfo, 0, sizeof(struct UnitInfo)*UNIT_MAX);
			memcpy(GameInstance::getInstance()->latestunitinfo, data+4+8, len);
			GameInstance::getInstance()->latestunitnum = len / sizeof(struct UnitInfo);
			//update the tick last, to try and quell race conditions
			GameInstance::getInstance()->currTick = incomingtick;
			
			GameInstance::getInstance()->ntohUnitList();
			
			//TODO mutex end!
		}
		delete[] data;
	}
	
	return NULL;
}

