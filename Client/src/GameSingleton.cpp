/*
 *  GameSingleton.cpp
 *  PetriWars
 *
 *  Created by Tim Pittman on 2/28/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#include "GameSingleton.h"

#ifdef _WIN32
	#ifndef WIN32_LEAN_AND_MEAN
	#define WIN32_LEAN_AND_MEAN
	#endif
	
	#include <winsock2.h> //core
	#include <ws2tcpip.h>
#else
	#include <sys/socket.h> //core
#endif

GameInstance* GameInstance::singleton = 0;

GameInstance::GameInstance():currTick(0){}

GameInstance* GameInstance::getInstance(){
	if (singleton == 0) {
		singleton = new GameInstance();
	}
	return singleton;
}
void GameInstance::endGame(){
	delete singleton;
	singleton = 0;
}

void GameInstance::ntohUnitList(){
	for (unsigned int i = 0; i < UNIT_MAX /*this->latestunitnum*/; i++){
		struct UnitInfo* cinfo = &this->latestunitinfo[i];
		cinfo->uid = ntohs(cinfo->uid);
		cinfo->posx = ntohs(cinfo->posx);
		cinfo->posy = ntohs(cinfo->posy);
		cinfo->energy = ntohs(cinfo->energy);
		cinfo->upgrades = ntohl(cinfo->upgrades);
	}
}