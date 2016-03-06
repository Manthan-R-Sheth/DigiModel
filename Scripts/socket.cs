using UnityEngine;
using System.Collections;
using System;
using System.Net;

public class socket : MonoBehaviour {

    public static void Main(string[] args)
    {
        string name = (args.Length < 1) ? Dns.GetHostName() : args[0];
        try
        {
            IPAddress[] addrs = Dns.Resolve(name).AddressList;
            foreach (IPAddress addr in addrs)
            { 
                Console.WriteLine("{0}/{1}", name, addr);
                Debug.Log("Connected to client socket"+addr); }
        }
        catch (Exception e)
        {
            Console.WriteLine(e.Message);
            Debug.Log("Connected to client socket");
        }
    }

}
