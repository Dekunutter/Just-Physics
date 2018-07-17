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
