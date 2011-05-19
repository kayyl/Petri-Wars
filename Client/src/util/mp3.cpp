/*===============================================
         -= [url=http://www.poke646.com].:: Poke646 ::.&nbsp;&nbsp;&nbsp;A Half-Life Singleplayer Modification[/url] =-
The same old trouble in a brand new environment

MP3 player sourcefile

TheTinySteini
===============================================*/

#include <iostream>
#include "Configurable.h"
#include "mp3.h"
  
using namespace std;  
  
//class Sound{  
//private:  
////system, sound, channel, result  
//FMOD_SYSTEM *system;  
//FMOD_SOUND *sound[5]; //Array of sound files  
//FMOD_CHANNEL *channel; //multiple sounds overlapping  
//FMOD_RESULT result; //comes up with error - put into error check  
//int playing_;  
//public:  
//Sound(); //Constructor loads the sounds ready to be used in the program  
//~Sound(); //Destructor is important for releasing memory back into the system  
//int get_playing() {return playing_;} //Return playing  
//void play_sound(int a, bool b); //select the sound, and if paused is true/false  
//void is_playing(); //Check if a sound is playing  
//void update(); //Update the sound  
//};  
  
Sound::Sound()  
{  
	//Create the fmod system  
	result = FMOD_System_Create(&system);  
	  
	//Initialise system, set max channels,  
	FMOD_System_Init(system, 32, FMOD_INIT_NORMAL, 0);  
	  
	//Create sounds, and put them into the array  
	//FMOD_System_CreateSound(system, "Sounds/phone_button.wav", FMOD_HARDWARE, 0, &sound[0]); //CreateSound puts the sound into memory  
	result = FMOD_System_CreateStream(system, RESOURCE("sound/music/BG_music.mp3"), FMOD_LOOP_NORMAL, 0, &sound[0]); //Stream is used for larger sounds - not putting the whole sound into memory, and playing it as it goes. This song will loop, because of FMOD_LOOP_NORMAL.  
	if(result != FMOD_OK) {
		printf("Error in FMOD: (err=%d) %s\n", result, FMOD_ErrorString(result));
	}
	FMOD_System_CreateSound(system, RESOURCE("sound/music/attack1.wav"), FMOD_HARDWARE, 0, &sound[1]);
	FMOD_System_CreateSound(system, RESOURCE("sound/music/attack2.wav"), FMOD_HARDWARE, 0, &sound[2]);
	FMOD_System_CreateSound(system, RESOURCE("sound/music/upgrade.wav"), FMOD_HARDWARE, 0, &sound[3]);
}  
  
Sound::~Sound()  
{  
	//release all memory back to the system  
	FMOD_System_Release(system);  
}  
  
//Play a sound of choice  
void Sound::play_sound(int a, bool b)  
{  
	FMOD_System_PlaySound(system, FMOD_CHANNEL_FREE, sound[a], b, &channel); //a is the sound selection, b is whether it is playing or not  
}  
  
//Check if the sound is playing  
void Sound::is_playing()  
{  
	FMOD_Channel_IsPlaying(channel, &playing_); //keep checking is playing  
}  
  
//Update the system when a sound is played  
void Sound::update()  
{  
	FMOD_System_Update(system);  
}  