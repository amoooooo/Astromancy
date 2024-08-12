#version 330 core
#extension GL_NV_shadow_samplers_cube : enable

uniform samplerCube Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec3 texCoord0;

out vec4 fragColor;

void main() {
    fragColor = textureCube(Sampler0, texCoord0) * ColorModulator;
}