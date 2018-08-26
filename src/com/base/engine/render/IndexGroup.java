package com.base.engine.render;

public class IndexGroup
{
    public static final int NO_VALUE = -1;

    public int vertex, texCoord, normal;

    public IndexGroup()
    {
        vertex = NO_VALUE;
        texCoord = NO_VALUE;
        normal = NO_VALUE;
    }
}