using UnityEngine;
using System.Collections;
using System.Net;
using System.Net.Sockets;
using System;
using System.IO;

using System.Text;

public class line : MonoBehaviour
{
	public String str_coordi;
	public float[] coordi;
	public Color c1 = Color.yellow;
	public Color c2 = Color.red;
	private static Material lineMaterial;
	char[] ch;
	string str = null;
	int c=0;
	public void Main()
	{
		for (int i = 0; i < 1000; i++) {
			;
		}
		string url = "http://172.25.15.180:8000/data.txt";
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
		    count = readStream.Read(read, 0, 256);           
		}

		calculatePoints(str);


	}

	private void calculatePoints(string str)
	{
		LineRenderer lineRenderer = GetComponent<LineRenderer>();
		lineRenderer.material.SetColor("_SpecColor", c1);
		Vector3[] points = new Vector3[str.Length];
		int l = 0;
		int k = 0;
		ch = str.ToCharArray();

		try{
			for (l = k; l < str.Length; l++)
			{
				if (ch[l] == ':')
				{
					   
					if(str_coordi.IndexOf('-')==0)
						{
						char[] chs=str_coordi.ToCharArray();
						chs[0]='0';
						coordi[c] = (-1)*float.Parse(new string(chs));
						Debug.Log(coordi[c]+"yo");
						}
						else
						{
							coordi[c] = float.Parse(str_coordi);
						Debug.Log(coordi[c]);
						}
					c++;
					k=l+1;
					break;

				}
					
				else
				{
					str_coordi+=ch[l];
				}
			}
			
			int i=0;
			while (i < (c/3))
			{

				points[i] = new Vector3(coordi[3*i],coordi[(3*i)+1],coordi[(3*i)+2]);
				lineRenderer.SetPosition(i, points[i]);
				Debug.Log(coordi[3*i]+"   :   "+coordi[(3*i)+1]+"   :   "+coordi[(3*i)+2]);
				i++;

			}

		}
		catch (IOException e)
		{
			Debug.Log(e);
			Debug.Log("Yolo");
		}
		/*void OnGUI() {
            GUI.Label(new Rect(100,100,1000,200), (float)(ch[0])+(float)(ch[1])+(float)(ch[2]) + "YOLO");
        }*/
	}

	void Start()
	{

		CreateLineMaterial();
		coordi = new float[250];
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