#version 330

uniform float GameTime;
uniform vec2 InSize;

in vec4 vertexColor;
in vec2 texCoord0;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float hermite(float t)
{
    return t * t * (3.0 - 2.0 * t);
}

float dot3(vec3 a, vec3 b)
{
    return a.x * b.x + a.y * b.y + a.z * b.z;
}

float rand(vec3 co){
    return fract(sin(dot3(co.xyz ,vec3(12.9898,78.233, 33.87637))) * 43758.5453);
}

float noise(vec3 co, float freq)
{
    vec3 v = co * vec3(freq, freq, freq);
    float ix1 = floor(v.x);
    float iy1 = floor(v.y);
    float iz1 = floor(v.z);
    float ix2 = floor(v.x + 1.0);
    float iy2 = floor(v.y + 1.0);
    float iz2 = floor(v.z + 1.0);

    float fx = hermite(fract(v.x));
    float fy = hermite(fract(v.y));
    float fz = hermite(fract(v.z));

    float mix1 = mix(mix(rand(vec3(ix1, iy1, iz1)), rand(vec3(ix2, iy1, iz1)), fx), mix(rand(vec3(ix1, iy2, iz1)), rand(vec3(ix2, iy2, iz1)), fx), fy);
    float mix2 = mix(mix(rand(vec3(ix1, iy1, iz2)), rand(vec3(ix2, iy1, iz2)), fx), mix(rand(vec3(ix1, iy2, iz2)), rand(vec3(ix2, iy2, iz2)), fx), fy);

    return mix(mix1, mix2, fz);
}

float pnoise(vec3 co, float freq, int steps, float persistence)
{
    float value = 0.0;
    float ampl = 1.0;
    float sum = 0.0;
    for(int i=0 ; i<steps ; i++)
    {
        sum += ampl;
        value += noise(co, freq) * ampl;
        freq *= 2.0;
        ampl *= persistence;
    }
    return value / sum;
}

float noise(vec2 co, float frequency)
{
    vec2 v = vec2(co.x * frequency, co.y * frequency);

    float ix1 = floor(v.x);
    float iy1 = floor(v.y);
    float ix2 = floor(v.x + 1.0);
    float iy2 = floor(v.y + 1.0);

    float fx = hermite(fract(v.x));
    float fy = hermite(fract(v.y));

    float fade1 = mix(rand(vec2(ix1, iy1)), rand(vec2(ix2, iy1)), fx);
    float fade2 = mix(rand(vec2(ix1, iy2)), rand(vec2(ix2, iy2)), fx);

    return mix(fade1, fade2, fy);
}

float pnoise(vec2 co, float freq, int steps, float persistence)
{
    float value = 0.0;
    float ampl = 1.0;
    float sum = 0.0;
    for(int i=0 ; i<steps ; i++)
    {
        sum += ampl;
        value += noise(co, freq) * ampl;
        freq *= 2.0;
        ampl *= persistence;
    }
    return value / sum;
}

out vec4 fragColor;

void main(){
    float time = GameTime * 120.0F;
    vec2 inSize = vec2(1.0, 1.0);
    vec2 position = texCoord0 * (inSize * vec2(2.,2.));
    float colorNoise = pnoise(vec3(position, time), 10.0, 5, 0.5);
    vec4 mainCol = vec4(colorNoise * 0.42424, colorNoise * 0.124, colorNoise * 0.59153, colorNoise);

    vec2 modPos = texCoord0 * (inSize * vec2(-2.,-2.));
    float modNoise = pnoise(vec3(modPos, time), 7.0, 2, 0.8);
    vec4 modCol = vec4(modNoise* 0.0, modNoise* 0.324, modNoise* 0.69153, 0.2);
    mainCol = mix(mainCol, modCol, modNoise);
    mainCol = mix(mainCol * 0.15, mainCol, colorNoise);
    mainCol.a = 1.;
    fragColor = mainCol;
}