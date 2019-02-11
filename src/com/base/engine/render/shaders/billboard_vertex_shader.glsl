#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

out vec4 exColour;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform vec4 colour;

void main()
{
    mat4 modelView = modelViewMatrix;
    modelView[0][0] = 1.0;
    modelView[0][1] = 0.0;
    modelView[0][2] = 0.0;

    modelView[1][0] = 0.0;
    modelView[1][1] = 1.0;
    modelView[1][2] = 0.0;

    modelView[2][0] = 0.0;
    modelView[2][1] = 0.0;
    modelView[2][2] = 1.0;

    gl_Position = projectionMatrix * modelView * vec4(position, 1.0);
    exColour = colour;
}
