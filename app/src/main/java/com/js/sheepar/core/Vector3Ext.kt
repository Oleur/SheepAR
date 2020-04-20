package com.js.sheepar.core

import com.google.ar.sceneform.math.Vector3


// Operators
operator fun Vector3.plus(vector3: Vector3) = Vector3.add(this, vector3)

operator fun Vector3.times(vector3: Vector3) = Vector3.dot(this, vector3)

operator fun Vector3.minus(vector3: Vector3) = Vector3.min(this, vector3)

// Infix
infix fun Vector3.add(vector3: Vector3) = Vector3.add(this, vector3)
