#version 450

in vec2 outTexCoord;
in vec3 outNormal;
in vec3 outPosition;
out vec4 fragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform vec3 camera_pos;

vec4 ambientColour;
vec4 diffuseColour;
vec4 specularColour;

void setupColours(Material material, vec2 texCoord)
{
    if(material.hasTexture == 1)
    {
        ambientColour = texture(texture_sampler, texCoord);
        diffuseColour = ambientColour;
        specularColour = ambientColour;
    }
    else
    {
        ambientColour = material.ambient;
        diffuseColour = material.diffuse;
        specularColour = material.specular;
    }
}

vec4 calculateLightColour(vec3 lightColour, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal)
{
    vec4 diffuse = vec4(0, 0, 0, 0);
    vec4 specular = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    diffuse = diffuseColour * vec4(lightColour, 1.0) * lightIntensity * diffuseFactor;

    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specular = specularColour * lightIntensity * specularFactor * material.reflectance * vec4(lightColour, 1.0);

    return(diffuse + specular);
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColour = calculateLightColour(light.colour, light.intensity, position, toLightDirection, normal);

    float distance = length(lightDirection);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * distance * distance;
    return lightColour / attenuationInv;
}

void main()
{
    setupColours(material, outTexCoord);

    vec4 diffuseSpecularComp = calculatePointLight(pointLight, outPosition, outNormal);

    fragColor = ambientColour * vec4(ambientLight, 1) + diffuseSpecularComp;
}