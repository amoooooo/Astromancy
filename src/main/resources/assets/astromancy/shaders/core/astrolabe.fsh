#version 150

#moj_import <fog.glsl>

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 InSize;
in vec2 UV0;
in vec2 texCoord0;

uniform float GameTime;
uniform int StarLayers;
in float vertexDistance;

// divisions of grid
const float repeats = 75.;

// number of layers
const float layers = 10.;

// star colours
const vec3 blue = vec3(51.,64.,195.)/255.;
const vec3 cyan = vec3(117.,250.,254.)/255.;
const vec3 white = vec3(255.,255.,255.)/255.;
const vec3 yellow = vec3(251.,245.,44.)/255.;
const vec3 red = vec3(247,2.,20.)/255.;

// spectrum function
vec3 spectrum(vec2 pos){
    pos.x *= 4.;
    vec3 outCol = vec3(0);
    if( pos.x > 0.){
        outCol = mix(blue, cyan, fract(pos.x));
    }
    if( pos.x > 1.){
        outCol = mix(cyan, white, fract(pos.x));
    }
    if( pos.x > 2.){
        outCol = mix(white, yellow, fract(pos.x));
    }
    if( pos.x > 3.){
        outCol = mix(yellow, red, fract(pos.x));
    }

    return 1.-(pos.y * (1.-outCol));
}

float N21(vec2 p){
    p = fract(p*vec2(233.34, 851.73));
    p+= dot(p, p+23.45);
    return fract(p.x*p.y);
}

vec2 N22 (vec2 p){
    float n = N21(p);
    return vec2 (n, N21(p+n));
}

mat2 scale(vec2 _scale){
    return mat2(_scale.x,0.0,
    0.0,_scale.y);
}

// 2D Noise based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise (in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Four corners in 2D of a tile
    float a = N21(i);
    float b = N21(i + vec2(1.0, 0.0));
    float c = N21(i + vec2(0.0, 1.0));
    float d = N21(i + vec2(1.0, 1.0));

    // Smooth Interpolation

    // Cubic Hermine Curve.  Same as SmoothStep()
    vec2 u = f*f*(3.0-2.0*f);

    // Mix 4 coorners percentages
    return mix(a, b, u.x) +
    (c - a)* u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

float perlin2(vec2 uv, int octaves, float pscale){
    float col = 1.;
    float initScale = 4.;
    for ( int l; l < octaves; l++){
        float val = noise(uv*initScale);
        if (col <= 0.01){
            col = 0.;
            break;
        }
        val -= 0.01;
        val *= 0.5;
        col *= val;
        initScale *= pscale;
    }
    return col;
}

vec3 stars(vec2 uv, float offset){

    float timeScale = -(((1 * 24000) * 0.0001) + offset) / layers;

    float trans = fract(timeScale);

    float newRnd = floor(timeScale);

    vec3 col = vec3(0.);


    // translate uv then scale for center
    uv -= vec2(0.5);
    uv = scale( vec2(trans) ) * uv;
    uv += vec2(0.5);

    // create square aspect ratio
    uv.x *= vec2(256,256).x / vec2(256,256).y;

    // add nebula colours
    float colR = N21(vec2(offset+newRnd));
    float colB = N21(vec2(offset+newRnd*123.));
    float colG = N21(vec2(offset+newRnd*123.));

    // generate perlin noise nebula on every third layer
    if (mod(offset,3.) == 0.){
        float perl = perlin2(uv+offset+newRnd,3,2.);
        col += vec3(perl*0,perl*0,perl*0);
    }

    // create boxes
    uv *= repeats;

    // get position
    vec2 ipos = floor(uv);

    // return uv as 0 to 1
    uv = fract(uv);

    // calculate random xy and size
    vec2 rndXY = N22(newRnd + ipos*(offset+1.))*0.9+0.05;
    float rndSize = N21(ipos)*75.+200.;


    vec2 j = (rndXY - uv)*rndSize;
    float sparkle = 1./dot(j,j);

    col += spectrum(fract(rndXY*newRnd*ipos)) * vec3(sparkle);


    // visualize layers
    /*if ((uv.x > 9. || uv.y > 0.99) && ipos.y == 8.){
        col += vec3(1.,0.,0.)*smoothstep(1.,0.5,trans);
    }
    if (mod(offset,3.) == 0.){
    	if (uv.x > 0.99 || uv.y > 0.99){
        	col += vec3(1.,0.,0.)*smoothstep(0.2,0.1,trans);
    	}
    }*/

    col *= smoothstep(1.,0.8,trans);
    col *= smoothstep(0.,0.1,trans);
    return col;

}

out vec4 fragColor;

void main()
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = texCoord0 / vec2(7,7);

    vec3 col = vec3(0.);

    for (float i = 0.; i < layers; i++ ){
        col += stars(uv, i);
    }


    // Output to screen
    fragColor = vec4(col, 1);
}