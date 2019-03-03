"""`main` is the top level module for your Flask application."""
import json
# Import the Flask Framework
from flask import Flask, render_template, url_for, redirect, request, session, Response, request
from flask_cors import CORS
from google.appengine.ext import ndb

from models import Sensor

app = Flask(__name__)
CORS(app)
app.secret_key = b'xxxxxxxxxxxxxxxxxxxx'
# Note: We don't need to call run() since our application is embedded within
# the App Engine WSGI application server.


@app.route('/')
def home():
    return render_template("viewer.html")

@app.route('/api/sensors', methods=['GET'])
def api_sensor_list():
    sensors = json.dumps([p.to_dict() for p in Sensor.query().fetch()])
    return Response(sensors, mimetype='application/json')

@app.route('/api/sensors', methods=['POST'])
def api_sensor_create():
    data = request.get_json()
    sensor = Sensor(
            mac_address=data['mac_address'],
            name=data['name'],
            x=data['x'],
            y=data['y'],
            r=data['r'],
            enabled=data['enabled'],
            motion_detected = False
            )
    sensor.put()
    return "Created"

@app.route('/api/sensors/<mac_address>', methods=['DELETE'])
def api_sensor_delete(mac_address):
    sensor = Sensor.query(Sensor.mac_address == mac_address).get()
    sensor.key.delete()
    return "Deleted"

@app.route('/api/sensors/<mac_address>', methods=['PUT'])
def api_sensor_edit(mac_address):
    sensor = Sensor.query(Sensor.mac_address == mac_address).get()
    data = request.get_json()

    sensor.name = data['name']
    sensor.x = data['x']
    sensor.y = data['y']
    sensor.r = data['r']
    sensor.enabled = data['enabled']

    sensor.put()
    return "Updated"


@app.route('/api/sensors/<mac_address>/<motion_state>')
def api_sensor_update_motion(mac_address, motion_state):
    sensor = Sensor.query(Sensor.mac_address == mac_address).get()
    sensor.motion_detected = motion_state == '1'
    sensor.put()
    return "Complete"
