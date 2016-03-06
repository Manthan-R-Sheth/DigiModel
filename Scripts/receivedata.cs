using UnityEngine;
using System.Collections;
using System.IO;
using System;

public class receivedata : MonoBehaviour
{
    AndroidJavaClass jc;
    string javaMessage = "";

    void Start()
    {
        // Acces the android java receiver we made
        jc = new AndroidJavaClass("mars.co.in.digimodel.MyReceiver");
        // We call our java class function to create our MyReceiver java object
        jc.CallStatic("createInstance");
    }

    void Update()
    {
        try {
            // We get the text property of our receiver
            javaMessage = jc.GetStatic<string>("text"); }

            catch(Exception e) {
            Debug.Log("Error is yolo "+e);
            }
    }
    void OnGUI()
    {
        GUI.Label(new Rect(100, 100, 100, 20), javaMessage + "YOLO");
    }
}