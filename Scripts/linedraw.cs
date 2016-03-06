using UnityEngine;
using System.Collections;

public class linedraw : MonoBehaviour
{
    public Material mat;
    private Vector3 startVertex;
    void Update()
    {
        if (!mat)
        {
            Debug.LogError("Please Assign a material on the inspector");
            return;
        }
        GL.PushMatrix();
        mat.SetPass(0);
        GL.LoadOrtho();
        GL.Begin(GL.LINES);
        GL.Color(Color.red);
        GL.Vertex(startVertex);
        GL.Vertex(new Vector3(255, 2, 1));
        GL.End();
        GL.PopMatrix();

    }
    void start()
    {
        startVertex = new Vector3(0, 0, 0);
    }
}