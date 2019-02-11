package com.base.engine.render;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Colour {
    public float red, green, blue, alpha;

    public Colour() {
        red = 1;
        green = 1;
        blue = 1;
        alpha = 1;
    }

    public Colour(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        alpha = 1;
    }

    public Colour(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Colour(Colour other) {
        this.red = other.red;
        this.green = other.green;
        this.blue = other.blue;
        this.alpha = other.alpha;
    }

    public Vector3f toVector3f() {
        return new Vector3f(red, green, blue);
    }

    public Vector4f toVector4f() {
        return new Vector4f(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return red + " " + green + " " + blue + " " + alpha;
    }
}
