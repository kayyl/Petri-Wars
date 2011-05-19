/*
 *  Configurable.h
 *  PetriWars
 *
 *  Created by Tim Pittman on 3/24/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

///These are defines for configuable values, like things that
///could be used for balacing the game

#ifndef __CONFIGURABLE__
#define __CONFIGURABLE__

//Maximum number of units selectable
#define UNIT_SELECT_MAX 12
#define UNIT_MAX 130

#define TASK_STOP 0
#define TASK_MOVE 1
#define TASK_ATTACKMOVE 2
#define TASK_UPGRADE_FLAGELLA 3
#define TASK_UPGRADE_CELLWALL 4
#define TASK_UPGRADE_CILLIA 5
#define TASK_UPGRADE_LYSOSOME 6
#define TASK_UPGRADE_CHLOROPLAST 7
#define TASK_DIVIDE 8
#define NO_TASK 9

#ifdef __APPLE__
#define SCREEN_HEIGHT_INIT 600
#define SCREEN_WIDTH_INIT 800
#else
#define SCREEN_HEIGHT_INIT 700
#define SCREEN_WIDTH_INIT 1366
#endif

#define CAM_HEIGHT -6.9

#ifdef __APPLE__
	#ifdef _DEBUG
		#define RESOURCE(name) "PetriWarsdAgl.app/Contents/Resources/" name
	#else
		#define RESOURCE(name) "PetriWarsAgl.app/Contents/Resources/" name
	#endif
#else
	#define RESOURCE(name) name
#endif

#endif