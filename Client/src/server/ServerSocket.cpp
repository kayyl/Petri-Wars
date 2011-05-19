/*
 *  Socket.cpp
 *  PetriWars
 *
 *  Created by Tim Pittman on 3/4/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#include "ServerSocket.h"
#include <cstring>
#include <stdio.h>

#ifdef _WIN32
	#ifndef WIN32_LEAN_AND_MEAN
	#define WIN32_LEAN_AND_MEAN
	#endif
	
	#include <winsock2.h> //core
	#include <ws2tcpip.h>
	#include <stdio.h>
	
	#pragma comment(lib, "Ws2_32.lib") //comment to the linker to include this file 
	
	#define SOCKET_WINSOCK
#else
	#include <sys/socket.h> //core
	#include <netinet/in.h> //internet addresses
	#include <sys/un.h> //stuff for localhost
	#include <arpa/inet.h> //minipulating ip addresses
	#include <netdb.h> //dns
	
	#define SOCKET_BERKLEY
#endif

#ifndef INVALID_SOCKET
#define INVALID_SOCKET -1
#endif

#ifndef SOCKET_ERROR
#define SOCKET_ERROR -1
#endif

#ifndef NULL
#define NULL 0
#endif

ServerSocket::ServerSocket(bool useUDP) : udp(useUDP) {
}

ServerSocket::~ServerSocket(){
}

bool ServerSocket::isOpen(){
	return (sockdesc != INVALID_SOCKET);
}

bool ServerSocket::openSocket(const char* ip, const char* port){
	#ifdef SOCKET_WINSOCK
		WSADATA wsaData;
	#endif
	int res = 0;
	sockdesc = INVALID_SOCKET;

	#ifdef SOCKET_WINSOCK	
		if ((res = WSAStartup(MAKEWORD(2,2), &wsaData))) {
			fprintf(stderr, "WSAStartup failed: %d\n", res);
			return false;
		}
	#endif
	
	if (!udp) {/////////////////TCP//////////////////////
		
		struct addrinfo *result = NULL, *ptr = NULL, hints;
		
		memset(&hints, 0, sizeof(hints));
		hints.ai_family = AF_INET;
		hints.ai_socktype = SOCK_STREAM;
		hints.ai_protocol = IPPROTO_TCP;
		
		if ((res = getaddrinfo(ip, port, &hints, &result)) ){ //if not 0
			fprintf(stderr, "Could not get address info!\n");
			#ifdef SOCKET_WINSOCK
				WSACleanup();
			#endif
			return false;
		}
		
		for (ptr = result; ptr != NULL; ptr = result->ai_next) {
			sockdesc = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);
			
			if (sockdesc == INVALID_SOCKET) continue;
			
			if ( (res = connect(sockdesc, ptr->ai_addr, ptr->ai_addrlen)) == SOCKET_ERROR){
				#ifdef SOCKET_WINSOCK
					closesocket(sockdesc);
				#else
					close(sockdesc);
				#endif
				sockdesc = INVALID_SOCKET;
			}
		}
		
		freeaddrinfo(result);
		
		if (sockdesc == INVALID_SOCKET){
			#ifdef SOCKET_WINSOCK
				fprintf(stderr, "Could not open socket: %ld\n", WSAGetLastError());
				WSACleanup();
			#else
				fprintf(stderr, "Could not open socket!\n");
			#endif
			return false;
		}
		
		return true;
	} else { /////////////////////////////UDP///////////////////////
		struct sockaddr_in sa;
		
		sockdesc = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
		if (sockdesc == INVALID_SOCKET){
			fprintf(stderr, "Could not get UDP socket!\n");
			#ifdef SOCKET_WINSOCK
				WSACleanup();
			#endif
			return false;
		}
		
		sa.sin_family = AF_INET;
		sa.sin_addr.s_addr = htonl(INADDR_ANY); //any address
		sa.sin_port = htons(12184);
		
		if ( bind(sockdesc, (struct sockaddr*) &sa, sizeof(struct sockaddr)) ){ //if fails
			fprintf(stderr, "Could not bind UDP socket!\n");
			#ifdef SOCKET_WINSOCK
				WSACleanup();
			#endif
		}
		
		if (sockdesc == INVALID_SOCKET){
			#ifdef SOCKET_WINSOCK
				fprintf(stderr, "Could not open socket: %ld\n", WSAGetLastError());
				WSACleanup();
			#else
				fprintf(stderr, "Could not open socket!\n");
			#endif
			return false;
		}
		printf("UDP IS NOW OPEN!");
		return true;
	}

}

void ServerSocket::closeSocket(){
	if (sockdesc == INVALID_SOCKET) return;
	
	#ifdef SOCKET_WINSOCK
		closesocket(sockdesc);
		WSACleanup();
	#else
		close(sockdesc);
	#endif
	sockdesc = INVALID_SOCKET;
}

bool ServerSocket::sendMessage(ServerMessage *msg){
	if (sockdesc == INVALID_SOCKET) throw "Socket not connected!";
	if (udp) throw "Cannot send over UDP socket!";
	
	int len = 255;
	int res = 0;

	printf("sendMessage - ");
	if ((res = send(sockdesc, (char*)msg, len, 0)) == SOCKET_ERROR){
//		throw "Error sending!";
		printf("[ERR ] res: %d\n", res);
		return false;
	}
	printf("[ OK ] res: %d\n", res);
	return true;
}

void* ServerSocket::recvMessage(){
	if (sockdesc == INVALID_SOCKET) throw "Socket not connected!";
	
	int res;	
	if (udp){ //UDP is done differently in this app than TCP
		u32 length;
		res = recvfrom(sockdesc, (char*)&length, 4, MSG_PEEK, NULL, 0); //grab the length of the incoming packet
//		printf("recvMessage (udp) -  res: %d\n", res);
		if (res > 0){
			length = ntohl(length); //swap the byte order
#ifdef _WIN32
		} else if (res == -1 && WSAGetLastError() == WSAEMSGSIZE) { //windows gives funky errors
			length = ntohl(length); //swap the byte order
#endif 
		} else {
			if (!res) sockdesc = INVALID_SOCKET; //socket has been closed
#ifdef _WIN32
			fprintf(stderr, "Error receiving udp message: %ld\n", WSAGetLastError());
#endif
			return NULL;
		}
//		printf("length:%x", length);
		
		char* data = new char[length+5];
		res = recvfrom(sockdesc, data, length+4, 0, NULL, 0); //pull in the data
//		printf("recvMessage2 (udp) -  res: %d\n", res);
		if (res > 0){
			return data; //data[] is leaked here if it is not delete[]'ed later
		} else {
			if (!res) sockdesc = INVALID_SOCKET; //socket has been closed
			delete[] data;
#ifdef _WIN32
			fprintf(stderr, "Error receiving udp message(2): %ld\n", WSAGetLastError());
#endif
			return NULL;
		}
	} else {
//		u8 type;
//		res = recv(sockdesc, (char*)&type, 1, MSG_PEEK); //grab the type of the incoming packet
//		if (res <= 0){
//			if (!res) sockdesc = INVALID_SOCKET; //socket has been closed
//			return NULL;
//		}
//		
//		int length = getRecvLengthForType(type);
		ServerMessage* data = new ServerMessage();
		
		res = recv(sockdesc, (char*)data, sizeof(ServerMessage), 0);
	//	printf("recvMessage (tcp) - res: %d\n", res);
		if (res > 0){
			return data; //data is leaked here is it is not delete'd later
		} else {
			if (!res) sockdesc = INVALID_SOCKET; //socket has been closed
			delete data;
			return NULL;
		}
	}
}

bool ServerSocket::sendRaw(int len, char* msg){
	if (sockdesc == INVALID_SOCKET) throw "Socket not connected!";
	if (udp) throw "Cannot send over UDP socket!";
	
	if (send(sockdesc, msg, len, 0) == SOCKET_ERROR){
//		throw "Error sending!";
		return false;
	}
	
	return true;
}

//*
bool ServerSocket::recvString(int maxlen, char* outmsg){
	int res;
	
	u32 length;
	res = recv(sockdesc, (char*)&length, 4, 0); //grab the length of the incoming packet
	if (res > 0){
		length = htonl(length); //swap the byte order
	} else {
		return false;
	}
	
	res = recv(sockdesc, outmsg, length, 0);
	if (res > 0){
		return true; 
	} else {
		return false;
	}
}//*/

int ServerSocket::getRecvLengthForType(int msgtype){
	switch (msgtype) {
		case MSGTYPE_CREDENTIALS: return 0;
		default: return 255;
	}
}

