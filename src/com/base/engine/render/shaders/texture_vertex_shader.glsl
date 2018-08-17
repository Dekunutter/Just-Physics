#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTextureCoords;

out vec2 exTextureCoords;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main() {
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);
    exTextureCoords = inTextureCoords;
}