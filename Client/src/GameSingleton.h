/*
 *  GameSingleton.h
 *  PetriWars
 *
 *  Created by Tim Pittman on 2/28/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */
 
#include "CommonTypes.h"
#include "Configurable.h"
#include "ServerMessage.h"

#define GAME_VERSION 1.0
/**
 * The GameInstance class is the class that stores all of the game variables for an on-going
 * game. The class is a singleton, meaning you cannot create a new one. You must get the 
 * instance of a game by calling:
 *		GameInstance::getInstance()
 * 
 * This will get the current game. To start a new game, call:
 * 		GameInstance::endGame();
 * 
 * This will delete the store instance and, on the next getInstance() call, will make a new
 * instance.
 */
class GameInstance {
private:
	GameInstance();
	
	static GameInstance* singleton;
	
public: //Game variables
	gametick_t currTick; //the current local game tick
	
	unsigned int latestunitnum;
	struct UnitInfo latestunitinfo[UNIT_MAX];
	int playerid;
	int winnerid;
	void ntohUnitList();
	
	static GameInstance* getInstance();
	static void endGame();
};