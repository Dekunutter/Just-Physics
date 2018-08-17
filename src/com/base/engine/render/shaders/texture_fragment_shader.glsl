#version 450

in vec2 exTextureCoords;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main() {
    fragColor = texture(texture_sampler, exTextureCoords);
}
