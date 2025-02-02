#ifndef PLAYER2OBJECT_H
#define PLAYER2OBJECT_H

#include "Image.h"

#include "SceneObject.h"
#include "Vector2.h"

class Player2Object : public SceneObject
{
private:
	Image* _image;
	Vector2 _position;
	Vector2 _destination;

	int x, y, R, w;
	bool Right, Left, Up;
	float dx, dy;
	int ground;

public:
	Player2Object();
	~Player2Object();

	virtual void Draw ();
	virtual void Update ();
	virtual Vector2 GetPosition();
	virtual void SetPosition(Vector2 aux_position);
	virtual float Getdx();
	virtual float Getdy();
	virtual void Setdx(float aux_dx);
	virtual void Setdy(float aux_dy);
};

#endif