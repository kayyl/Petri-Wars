/*
 *  SThread.h
 *  PetriWars
 *
 *  Created by Tim Pittman on 3/24/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#ifdef _WIN32
	#include <Windows.h>
#else
	#include <pthread.h>
#endif

class SThread {
private:
	#ifdef _WIN32
		HANDLE thread;
	#else
		pthread_t thread;
	#endif
	
public:
#ifdef _WIN32
	SThread(LPTHREAD_START_ROUTINE start_routine, void* arg);
#else
	SThread(void *(*start_routine) (void *), void* arg);
#endif
	~SThread();
	
};

#ifdef _WIN32
	#define CASTTHREAD (LPTHREAD_START_ROUTINE)
#else
	#define CASTTHREAD
#endif