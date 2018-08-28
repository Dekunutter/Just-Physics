#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

out vec2 outTexCoord;
out vec3 outNormal;
out vec3 outPosition;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 modelViewPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * modelViewPosition;
    outTexCoord = texCoord;
    outNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
    outPosition = modelViewPosition.xyz;
}