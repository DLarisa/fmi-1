#include <SDL.h>    
#include <SDL_video.h> 
#include "InputDefinitions.h"//Toate tastele sunt definite aici

//Constante pentru imagini
enum KeyPressSurfaces
{
	KEY_PRESS_SURFACE_DEFAULT_1,
	KEY_PRESS_SURFACE_W,
	KEY_PRESS_SURFACE_S,
	KEY_PRESS_SURFACE_A,
	KEY_PRESS_SURFACE_D,
	KEY_PRESS_SURFACE_DEFAULT_2,
	KEY_PRESS_SURFACE_I,
	KEY_PRESS_SURFACE_K,
	KEY_PRESS_SURFACE_J,
	KEY_PRESS_SURFACE_L,
	KEY_PRESS_SURFACE_DEFAULT_3,
	KEY_PRESS_SURFACE_CTRL_Z,
	KEY_PRESS_SURFACE_SHIFT_Z,
	KEY_PRESS_SURFACE_ALT_Z,
	KEY_PRESS_SURFACE_DEFAULT_4,
	KEY_PRESS_SURFACE_CTRL_SLASH,
	KEY_PRESS_SURFACE_SHIFT_SLASH,
	KEY_PRESS_SURFACE_ALT_SLASH,
	KEY_PRESS_SURFACE_DEFAULT_5,
	KEY_PRESS_SURFACE_MOUSE1,
	KEY_PRESS_SURFACE_MOUSE2,
	KEY_PRESS_SURFACE_DEFAULT_6,
	KEY_PRESS_SURFACE_CTRL_SHIFT_ALT_B,
	KEY_PRESS_SURFACE_Input_Locked,
	KEY_PRESS_SURFACE_TOTAL
};
SDL_Window* gWindow = NULL; //window
SDL_Surface* gScreenSurface = NULL; //surface pentru window
SDL_Surface* gCurrentSurface = NULL;

void init()
{
	//window
	gWindow = SDL_CreateWindow("POO_Proiect2: PRESS Q or ESC for EXIT", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, 640, 480, SDL_WINDOW_SHOWN);
	//window surface
	gScreenSurface = SDL_GetWindowSurface(gWindow);
}
void close()
{
	SDL_DestroyWindow(gWindow);
	gWindow = NULL;
	//Iesire SDL
	SDL_Quit();
}