package com.base.engine;

import com.base.engine.render.IndexFace;
import com.base.engine.render.IndexGroup;
import com.base.engine.render.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {
    public static Mesh loadMesh(String filename) throws Exception {
        List<String> lines = readAllLinesFromFile(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<IndexFace> faces = new ArrayList<>();

        populateDataLists(lines, vertices, texCoords, normals, faces);

        return reorderLists(vertices, texCoords, normals, faces);
    }

    private static void populateDataLists(List<String> lines, List<Vector3f> vertices, List<Vector2f> texCoords, List<Vector3f> normals, List<IndexFace> faces) {
        for(String line : lines) {
            String[] tokens = line.split("\\s+");
            switch(tokens[0]) {
                case "v":
                    Vector3f vertex = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    vertices.add(vertex);
                    break;
                case "vt":
                    Vector2f texCoord = new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                    texCoords.add(texCoord);
                    break;
                case "vn":
                    Vector3f normal = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    normals.add(normal);
                    break;
                case "f":
                    IndexFace face = new IndexFace(tokens);
                    faces.add(face);
                    break;
                default:
                    break;
            }
        }
    }

    private static Mesh reorderLists(List<Vector3f> vertices, List<Vector2f> texCoords, List<Vector3f> normals, List<IndexFace> faces)
    {
        List<Integer> indices = new ArrayList<>();

        float[] vertexArray = new float[vertices.size() * 3];
        int i = 0;
        for(Vector3f v : vertices)
        {
            vertexArray[i * 3] = v.x;
            vertexArray[i * 3 + 1] = v.y;
            vertexArray[i * 3 + 2] = v.z;
            i++;
        }
        float[] texCoordArray = new float[vertices.size() * 2];
        float[] normalArray = new float[vertices.size() * 3];

        for(IndexFace face : faces)
        {
            IndexGroup[] faceIndices = face.getFaceIndices();
            for(IndexGroup index : faceIndices)
            {
                processFaceVertex(index, texCoords, normals, indices, texCoordArray, normalArray);
            }
        }
        int[] indexArray = new int[indices.size()];
        for(int j = 0; j < indexArray.length; j++)
        {
            indexArray[j] = indices.get(j);
        }
        return new Mesh(vertexArray, texCoordArray, normalArray, indexArray);
    }

    private static void processFaceVertex(IndexGroup indexValue, List<Vector2f> texCoords, List<Vector3f> normals, List<Integer> indices, float[] texCoordArray, float[] normalArray)
    {
        int vertexIndex = indexValue.vertex;
        indices.add(vertexIndex);

        if(indexValue.texCoord >= 0)
        {
            Vector2f texCoord = texCoords.get(indexValue.texCoord);
            texCoordArray[vertexIndex * 2] = texCoord.x;
            texCoordArray[vertexIndex * 2 + 1] = 1 - texCoord.y;
        }
        if(indexValue.normal >= 0)
        {
            Vector3f normal = normals.get(indexValue.normal);
            normalArray[vertexIndex * 3] = normal.x;
            normalArray[vertexIndex * 3 + 1] = normal.y;
            normalArray[vertexIndex * 3 + 2] = normal.z;
        }
    }

    private static List<String> readAllLinesFromFile(String filename) throws Exception
    {
        List<String> list = new ArrayList<>();
        BufferedReader br = FileLoader.loadFileFromResources(filename);
        String line;
        while((line = br.readLine()) != null)
        {
            list.add(line);
        }
        br.close();

        return list;
    }
}
