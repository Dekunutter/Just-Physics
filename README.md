# Just-Physics
An ongoing side project attempting to build a cleaner game engine from the ground up with a focus on physics and collision detection

Totally a work in progress. We'll see if I decide to go anywhere with it


::Numerical Integrators::
I've added some numerical integrators to play around with and the basics 
of a plug-and-play architecture so that they can be easily specified by 
the launcher via enum types:
- Explicit Euler
- Semi-implicit Euler
- Implicit Euler
- Verlet
- Velocity Verlet
- RK4

So far I like semi-implicit and implicit euler the best since they 
properly deal with velocities, are stable and have reliable accuracy

::Time-steps and Game loops::
I've added some different game loop types in the Engine and Time classes:
- Variable
- Semi-Fixed
- Fixed
- Fixed with freed physics
- Interpolated fixed with freed physics

My favourite is a fixed time step with freed physics and interpolation in the game loop. It allows the physics to adjust to the framerate of the system without dealing with the possibility of floating point issues on variable time deltas AND it creates an interpolated copy of positional values for the renderer to render smoothly.
