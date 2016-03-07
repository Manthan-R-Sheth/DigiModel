from bottle import route, run, template,response, get, post,request, static_file
import os
import time
import requests
import json
import csv


url = '192.168.0.56'

@route('/')
def home():
	msg="hello"
	return msg

@post('/data')
def receiveCo():
	x= request.forms.get('x')
	y= request.forms.get('y')
	z= request.forms.get('z')
	print x+" : "+y+" : "+z
	file= open('data.csv',"a")
	writer= csv.writer(file)
	writer.writerow([x,y,z])
	file.close()

@get('/userdata')
def sendData():
	try:
		readfile=open('data.csv',"rb")
	except:
		print "No file found"
	val=""
	reader=csv.reader(readfile)
	lines=[l for l in reader]
	for line in lines:
    		val=  val+line[0] +","+line[1] + ","+line[2] +"\n"                                             
	readfile.close()
	print val
	os.remove('data.csv')
	return val

run(host=url, port=8080, debug=True)

