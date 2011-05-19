/*
 *  SThread.cpp
 *  PetriWars
 *
 *  Created by Tim Pittman on 3/24/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#include "SThread.h"


#ifdef _WIN32
	SThread::SThread(LPTHREAD_START_ROUTINE start_routine, void* arg){
		thread = CreateThread(NULL, 0, start_routine, arg, 0, 0);
	}
#else
	SThread::SThread(void *(*start_routine) (void *), void* arg){
		pthread_create(&thread, NULL, start_routine, arg);
	}
#endif

SThread::~SThread(){
	
}
