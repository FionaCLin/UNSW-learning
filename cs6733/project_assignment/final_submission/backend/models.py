from google.appengine.ext import ndb

class Sensor(ndb.Model):
    mac_address = ndb.StringProperty()
    name = ndb.StringProperty()
    enabled = ndb.BooleanProperty()
    motion_detected = ndb.BooleanProperty()
    x = ndb.IntegerProperty()
    y = ndb.IntegerProperty()
    r = ndb.IntegerProperty()
