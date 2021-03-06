# Just-Physics
An ongoing side project attempting to build a cleaner game engine from the ground up with a focus on physics and collision detection

Totally a work in progress. We'll see if I decide to go anywhere with it


## Numerical Integrators
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

## Time-steps and Game loops
I've added some different game loop types in the Engine and Time classes:
- Variable
- Semi-Fixed
- Fixed
- Fixed with freed physics
- Interpolated fixed with freed physics

My favourite is a fixed time step with freed physics and interpolation in the game loop. It allows the physics to adjust to the framerate of the system without dealing with the possibility of floating point issues on variable time deltas AND it creates an interpolated copy of positional values for the renderer to render smoothly.

## Collision Detection
Currently the engine just has two collision detection algorithms implemented that need further testing but seem to offer some pretty good results so far:
- Seperating Axis Thereom
- GJK and EPA

I currently have these algorithms running in world space, but I could optimize it to run in body space, performing all calculations in the space of one body relative to the other. The GJK implementation successfully runs through EPA to generate some basic collision manifold information but currently just returns one contact point. Persistent contacts would need to be implemented to improve this design.

## Collision Response
The state of this currently is pretty rudimentary. Currently there is a working linear and angular response functions in the collision resolver class, looking out for collisions between two objects in zero gravity. It is using some work arounds and dirty code for the time being as I have no sequential solver implemented for multiple contact points.
The plan is to implement a full impulse-based collision resolution system utilizing sequential solvers for contact points in the future

## Other stuff
Additional necessary logic in this project I coded to create a basic engine to support my physics work:
- Basic GLSL based rendering in OpenGL 4.5
- OBJ file loading to create 3D meshes on load
- Creation and storage of display settings using .dat file saved to an AppData folder
- Texture application
- Material properties
- Basic lighting shaders for ambient, directional, point and spot lights
- Basic camera logic and control
