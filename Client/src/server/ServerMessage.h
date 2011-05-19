/*
 *  ServerMessage.h
 *  PetriWars
 *
 *  Created by Tim Pittman on 2/26/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef _SERVER_MESSAGE_H_
#define _SERVER_MESSAGE_H_

#include "Configurable.h"

/************************************************************
 *                   Server Message Types                   *
 *                                                          *
 *  (Matching what is in IGameServer in the Server folder)  *
 ************************************************************/

#define SERVERPORT "12121"

#define MSGTYPE_SERVERERROR -2

#define MSGTYPE_DISCONNECT -1

/* No data. This is essentially a no-op, sent at intervals of no activity */
#define MSGTYPE_ACK 0

#define MSGTYPE_TIME 1

#define MSGTYPE_LOGIN 2

#define MSGTYPE_LOGOUT 3

#define MSGTYPE_DENY 4

/* Indicating to the other end that they are in an illegal state to do this */
#define MSGTYPE_CANNOT 5

/* The server is shutting down. */
#define MSGTYPE_SERVERSHUTDOWN 6

/* The server sends this with its info, and it requesting that the client sends
   a message of the same type with its user's info as soon as possible. 
   This actually is not sent at first connection, but is expected from by the
   server after the initial send. In this info, the client must tell the server
   whether it is a java client or not. If it is a java client, the server will be
   expecting the client to be sending serialized classes. Otherwise, it will be
   expecting to parse the data out of the format outlined below. */
#define MSGTYPE_CREDENTIALS 10

#define MSGTYPE_LISTUSERS 29

#define MSGTYPE_LISTGAMES 30

#define MSGTYPE_CREATEGAME 31

#define MSGTYPE_DESTROYGAME 32

#define MSGTYPE_JOINGAME 33

#define MSGTYPE_REQUESTJOIN 34

#define MSGTYPE_LEAVEGAME 35

#define MSGTYPE_JOINCHANNEL 36

#define MSGTYPE_LEAVECHANNEL 37

/* Data is the chat message. Also server commands use this, as the server 
   simply checks for a '/' as the first character */
#define MSGTYPE_CHAT 49


/********* Below are the game specific message types **********/

/* Used by the server to indicate that it is sending a game state update. */
#define MSGTYPE_GAMESTATE 50

/* Sends a command to the server. Data is a the struct MsgData_Command */
#define MSGTYPE_COMMAND 51

#define MSGTYPE_GAMESET 59

#define MSGTYPE_PLAYERID 60

typedef unsigned char u8; //8 bits
typedef unsigned short u16; //16 bits
typedef unsigned int u32; //32 bits
typedef unsigned long long u64; //64 bits

typedef signed char s8; //8 bits, signed
typedef signed short s16; //16 bits, signed
typedef signed int s32; //32 bits, signed
typedef signed long long s64; //64 bits, signed

/******** Upgrade mapping ************
 All upgrades sent in messages, even singular upgrades
 use this map into a unsigned 32 bit value

0000 0000 0000 0000 0000 0000 0000 0000 = No upgrades

0000 0000 0000 0000 0000 0000 0000 0001 = Flagella 1
0000 0000 0000 0000 0000 0000 0000 0010 = Flagella 2
0000 0000 0000 0000 0000 0000 0000 0100 = Flagella 3

0000 0000 0000 0000 0000 0000 0000 1000 = Cell Wall 1
0000 0000 0000 0000 0000 0000 0001 0000 = Cell Wall 2
0000 0000 0000 0000 0000 0000 0010 0000 = Cell Wall 3

0000 0000 0000 0000 0000 0000 0100 0000 = Cilia 1
0000 0000 0000 0000 0000 0000 1000 0000 = Cilia 2
0000 0000 0000 0000 0000 0001 0000 0000 = Cilia 3

0000 0000 0000 0000 0000 0010 0000 0000 = Lysosome 1
0000 0000 0000 0000 0000 0100 0000 0000 = Lysosome 2
0000 0000 0000 0000 0000 1000 0000 0000 = Lysosome 3

0000 0000 0000 0000 0001 0000 0000 0000 = Chloroplast 1
0000 0000 0000 0000 0010 0000 0000 0000 = Chloroplast 2
0000 0000 0000 0000 0100 0000 0000 0000 = Chloroplast 3

0000 0000 0000 0000 1000 0000 0000 0000 = Peroxixome
0000 0000 0000 0001 0000 0000 0000 0000 = Pilia

0000 0000 0000 0010 0000 0000 0000 0000 = [Unused]
0000 0000 0000 0100 0000 0000 0000 0000 = [Unused]
0000 0000 0000 1000 0000 0000 0000 0000 = [Unused]
0000 0000 0001 0000 0000 0000 0000 0000 = [Unused]
0000 0000 0010 0000 0000 0000 0000 0000 = [Unused]
0000 0000 0100 0000 0000 0000 0000 0000 = [Unused]
0000 0000 1000 0000 0000 0000 0000 0000 = [Unused]

0000 0001 0000 0000 0000 0000 0000 0000 = [Unused]
0000 0010 0000 0000 0000 0000 0000 0000 = [Unused]
0000 0100 0000 0000 0000 0000 0000 0000 = [Unused]
0000 1000 0000 0000 0000 0000 0000 0000 = [Unused]
0001 0000 0000 0000 0000 0000 0000 0000 = [Unused]
0010 0000 0000 0000 0000 0000 0000 0000 = [Unused]
0100 0000 0000 0000 0000 0000 0000 0000 = [Unused]
1000 0000 0000 0000 0000 0000 0000 0000 = [Unused]

*/

/*
 * All messages will be kept under 256 bytes in length.
 */

///////////////Client to Server/////////////////

struct MsgData_ServerCredentials {
	int userid;
	int username_length;
	char username[32];
};

#define CHAT_LENMAX 220
struct MsgData_Chat {
	int name_length;
	char name[34];
	int message_length;
	char message[CHAT_LENMAX];
};

//Note: more information about a unit is stored than what is transmitted
struct UnitInfo {
	u16 uid; //0 = NO UNIT!!
	u16 posx; 
	u16 posy;
	u16 energy;
	u8 player; //determines affilation more than player. 0 = neutral, 1 = player 1, ...
	u8 force_id;
	u8 dir; //directional facing, in degrees from (0 - 180) * 2
	u8 state; //current animation state: moving, eating, etc
	u32 upgrades; //see upgrade map above
};


/**
 * This is the message sent from the client or from the server over TCP.
 * This message class deals with commands that the player sends to his units
 * or messages sent from the server that do NOT deal with unit positions.
 */
class ServerMessage {
public:
	/** 
	 * Message type, see ServerMessage.MessageType (1 byte)
	 */
	u8 type;
	
	/**
	 * The data for this message. Content depends on the message type
	 */
	u8 data[511];
	
};

struct MsgData_Command {
	u64 tick; /** What game tick was this message sent. Commands are modified by latency once at the server. (8 bytes) */
	u8 command; //command types: attack, defend, move, bud, upgrade, ...
	u8 numunits; //
	u16 uid[UNIT_SELECT_MAX]; //unit ids this command is issued for
	u16 destx; //destination of movement commands -- also upgrade map
	u16 desty; //destination of movement commands
	u16 targetid; //the target of attack, et al commands
	
};



#endif