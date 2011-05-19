/*===============================================
         -= [url=http://www.poke646.com].:: Poke646 ::.&nbsp;&nbsp;&nbsp;A Half-Life Singleplayer Modification[/url] =-
The same old trouble in a brand new environment

MP3 player headerfile

TheTinySteini
===============================================*/

#include "PetriWars/sound/fmod.h"
#include "PetriWars/sound/fmod_errors.h"
#ifdef _WIN32
	#include "windows.h"
#endif

class Sound{  
	private:  
	//system, sound, channel, result  
	FMOD_SYSTEM *system;  
	FMOD_SOUND *sound[5]; //Array of sound files  
	FMOD_CHANNEL *channel; //multiple sounds overlapping  
	FMOD_RESULT result; //comes up with error - put into error check  
	int playing_;  
	public:  
	Sound(); //Constructor loads the sounds ready to be used in the program  
	~Sound(); //Destructor is important for releasing memory back into the system  
	int get_playing() {return playing_;} //Return playing  
	void play_sound(int a, bool b); //select the sound, and if paused is true/false  
	void is_playing(); //Check if a sound is playing  
	void update(); //Update the sound  
};

//class CMP3
//{
//private:
//	//signed char		(_stdcall * SCL)	(FSOUND_STREAM *stream);
//	signed char		(_stdcall * SOP)	(int outputtype);
//	signed char		(_stdcall * SBS)	(int len_ms);
//	signed char		(_stdcall * SDRV)	(int driver);
//	signed char		(_stdcall * INIT)	(int mixrate, int maxsoftwarechannels, unsigned int flags);
//	//FSOUND_STREAM*		(_stdcall * SOF)	(const char *filename, unsigned int mode, int memlength);
//	//int 			(_stdcall * SPLAY)	(int channel, FSOUND_STREAM *stream);
//	void			(_stdcall * CLOSE)	( void );
//	
//	//FSOUND_STREAM  *m_Stream;
//	int		m_iIsPlaying;
//	HINSTANCE	m_hFMod;
//
//public:
//	int		Initialize();
//	int		Shutdown();
//	int		PlayMP3( const char *pszSong );
//	int		StopMP3();
//};
//
//extern CMP3 gMP3;