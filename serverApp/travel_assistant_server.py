from flask import Flask
from flask import request

import json
import googlemaps

from pprint import pprint

app = Flask(__name__)
gmaps = googlemaps.Client(key='AIzaSyBKXtm4_ZK_JqlEQGuOOplSmFZSYS6BmXw')

@app.route('/nearest_bustop')
def get_nearest_bustops():
    # extract lat, long from request
    latitude = request.args.get('lat')
    longitude = request.args.get('long')

    # construct location object
    location = (latitude, longitude)

    bus_stops_nearby = gmaps.places_nearby(location=location,
                                           radius=805,
                                           type='bus_station')

    if(bus_stops_nearby['results'].__len__() > 0):
        pprint (bus_stops_nearby['results'][0]['name']) # get name of first result

    return bus_stops_nearby['results'][0]['name'].encode('utf-8')


def main():

    app.run()


if __name__ == "__main__":
    main()