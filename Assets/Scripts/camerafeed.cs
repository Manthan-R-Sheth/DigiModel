using UnityEngine;
using System.Collections;

public class camerafeed : MonoBehaviour {
    WebCamTexture cameraTexture;
	// Use this for initialization
	void Start () {
        cameraTexture = new WebCamTexture();
        GetComponent<Renderer>().material.mainTexture = cameraTexture;
        cameraTexture.Play();
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
