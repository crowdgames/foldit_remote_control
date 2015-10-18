﻿using UnityEngine;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using UnityEngine.UI;
using System.Net.Sockets;
using System.Net;


public class NetworkConScript : MonoBehaviour {
	int port = 1230;
	int host = "169.254.226.223";

	int screenwidth = 1120;
	int screenheight = 630;
	Socket socket;
	byte[] bytes = new byte[10000000];
	int frame = 0;
	// Use this for initialization
	void Start() {
		Debug.Log("Start");
		socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
		socket.Connect(host, port);
		byte[] buf = { 88, 3, 0, 0, 0, 0, 0, screenwidth / 128, screenwidth % 128, screenheight / 128, screenheight % 128, 0 };
		Debug.Log(socket.Send(buf).ToString());
		Debug.Log(socket.Receive(bytes).ToString());
		dothing();
		Debug.Log("Connected");
	}

	// Update is called once per frame
	void Update() {
		frame = (frame + 1) % 10;
		if (frame == 0) {
			Debug.Log("Sending scroll");
			Debug.Log(socket.Send(new byte[] { 88, 3, 0, 100, 0, 100, 0 }).ToString());
			Debug.Log("Sent scroll");
			Debug.Log(socket.Receive(bytes).ToString());
			Debug.Log("Received screen");
			dothing();
		}
	}

	void dothing() {
		string s = "";
		for (int q = 0; q < 256; q++)
			s += ((int)(bytes[q])).ToString() + ", ";
		Debug.Log(s);
	}
}