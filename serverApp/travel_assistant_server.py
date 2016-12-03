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

    # get nearby bus-stops
    bus_stops_nearby = gmaps.places_nearby(location=location,
                                           radius=805,
                                           type='bus_station')

    if (bus_stops_nearby['results'].__len__() > 0):
        pprint(bus_stops_nearby['results'][0]['name'])  # get name of first result

    return bus_stops_nearby['results'][0]['name'].encode('utf-8')


@app.route('/next_bus_to_dest/<destination>')
def get_next_bus_to_dest(destination):
    # extract lat, long from request
    latitude = request.args.get('lat')
    longitude = request.args.get('long')

    # construct origin & destination coordinates
    origin_coordinates = (latitude, longitude)
    destination_coordinates = ()

    # result pieces
    bus_agency = None
    bus_number = None
    bus_name = None

    # final output string
    response_str = "The next bus to your destination, is number {}, {}. Operated by {}."

    # use geocoding to get coordinates
    destination_info = gmaps.geocode(destination)
    if (destination_info.__len__() > 0):
        dest_lat = destination_info[0]['geometry']['location']['lat']
        dest_long = destination_info[0]['geometry']['location']['lng']
        destination_coordinates = (dest_lat, dest_long)
        pass

    # get next bus details
    next_bus_info = gmaps.directions(origin=origin_coordinates,
                                     destination=destination_coordinates,
                                     mode='transit',
                                     transit_mode='bus')

    # pprint(next_bus_info)

    if (next_bus_info.__len__() > 0):
        for leg_dict in next_bus_info[0]['legs']:
            for steps_dict in leg_dict['steps']:
                if steps_dict['travel_mode'] == 'TRANSIT':
                    transit_details = steps_dict['transit_details']
                    line = transit_details['line']

                    # extract response pieces
                    bus_agency = line['agencies'][0]['name']  # value = Bloomington Transit
                    bus_number = line['short_name']
                    bus_name = line['name']

    response_str = response_str.format(bus_number,
                                       bus_name,
                                       bus_agency)
    print response_str
    return response_str


def main():
    app.run()


if __name__ == "__main__":
    main()
