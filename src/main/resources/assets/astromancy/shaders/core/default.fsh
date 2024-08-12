#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {

    fragColor = vec4(texCoord0.x, texCoord0.y, 1.0, 1.0) * ColorModulator;
}