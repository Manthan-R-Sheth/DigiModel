using UnityEngine;
using System.Collections;
using System.Net;
using System.Net.Sockets;
using System;
using System.IO;
using System.Net.Sockets;
using System.Text;

public class line : MonoBehaviour
{
    public Color c1 = Color.yellow;
    public Color c2 = Color.red;
    private static Material lineMaterial;
    Char[] ch;
    string str = null;
    public void Main()
    {

        string url = "http://192.168.150.1:8000/data.txt";

        HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

        HttpWebResponse response = (HttpWebResponse)request.GetResponse();

        Stream resStream = response.GetResponseStream();
        Encoding encode = System.Text.Encoding.GetEncoding("utf-8");

        // Pipe the stream to a higher level stream reader with the required encoding format. 
        StreamReader readStream = new StreamReader(resStream, encode);
        Char[] read = new Char[256];

        // Read 256 charcters at a time.    
        int count = readStream.Read(read, 0, 256);

        while (count > 0)
        {
            // Dump the 256 characters on a string and display the string onto the console.
            str = new String(read, 0, count);
            Debug.Log(str);
            count = readStream.Read(read, 0, 256);           
        }

        calculatePoints(str);

       
    }

    private void calculatePoints(string str)
    {
        LineRenderer lineRenderer = GetComponent<LineRenderer>();
        lineRenderer.material.SetColor("_SpecColor", c1);
        Vector3[] points = new Vector3[str.Length/3];
        int i = 0;
        ch= str.ToCharArray();
        while (i < (str.Length/3))
        {
            points[i] = new Vector3((float)(ch[3*i]),(float)(ch[3*i+1]),(float)(ch[3*i+2]));
            i++;
        }
        lineRenderer.SetPositions(points); //we have to give points that are to be plotted
    }

    /*void OnGUI() {
        GUI.Label(new Rect(100,100,1000,200), (float)(ch[0])+(float)(ch[1])+(float)(ch[2]) + "YOLO");
    }*/


    void Start()
    {
        
        CreateLineMaterial();
        LineRenderer lineRenderer = gameObject.AddComponent<LineRenderer>();
        //lineRenderer.material = new Material(Shader.Find("Custom/lineshader"));
        lineRenderer.material = lineMaterial;
        lineRenderer.material.SetColor("_SpecColor", c1);
        lineRenderer.SetColors(c1, c2);
        lineRenderer.SetWidth(0.2F, 0.2F);
        lineRenderer.SetVertexCount(100);
    }
    void Update()
    {
        Main();
       
    }
    static void CreateLineMaterial()
    {
        if (!lineMaterial)
        {
            lineMaterial = new Material("Shader \"Lines/Colored Blended\" {" +
                "SubShader { Pass { " +
                "    Blend SrcAlpha OneMinusSrcAlpha " +
                "    ZWrite Off Cull Off Fog { Mode Off } " +
                "    BindChannels {" +
                "      Bind \"vertex\", vertex Bind \"color\", color }" +
                "} } }");
            lineMaterial.hideFlags = HideFlags.HideAndDontSave;
            lineMaterial.shader.hideFlags = HideFlags.HideAndDontSave;
        }
    }
}
