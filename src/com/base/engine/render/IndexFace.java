package com.base.engine.render;

public class IndexFace
{
    private IndexGroup[] indexGroups;

    public IndexFace(String[] tokens)
    {
        indexGroups = new IndexGroup[tokens.length - 1];

        for(int i = 0; i < indexGroups.length; i++)
        {
            indexGroups[i] = parseLine(tokens[i + 1]);
        }
    }

    private IndexGroup parseLine(String line)
    {
        IndexGroup indexGroup = new IndexGroup();

        String[] lineTokens = line.split("/");
        int length = lineTokens.length;
        indexGroup.vertex = Integer.parseInt(lineTokens[0]) - 1;
        if(length > 1)
        {
            String texCoord = lineTokens[1];
            indexGroup.texCoord = texCoord.length() > 0 ? Integer.parseInt(texCoord) - 1 : IndexGroup.NO_VALUE;
            if(length > 2)
            {
                indexGroup.normal = Integer.parseInt(lineTokens[2]) - 1;
            }
        }
        return indexGroup;
    }

    public IndexGroup[] getFaceIndices()
    {
        return indexGroups;
    }
}
