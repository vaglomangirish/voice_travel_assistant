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

    pprint(next_bus_info)

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


@app.route('/next_bus_details/<destination>')
def get_next_bus_details(destination):
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
    bus_departing_at_time = None
    bus_arriving_at_time = None
    bus_departing_stop = None
    bus_arriving_stop = None
    bus_number_of_stops = None

    # final output string
    response_str = "The next bus to your destination, is number {}, {}. " \
                   "This bus departs from station {}, at time {}. " \
                   "After {} stops, it will arrive at station {}, at time {}. " \
                   "This bus is operated by {}."

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

                    # extract arrival time & stop
                    bus_arriving_stop = transit_details['arrival_stop']['name']
                    bus_arriving_at_time = transit_details['arrival_time']['text']

                    # extract departure time & stop
                    bus_departing_stop = transit_details['departure_stop']['name']
                    bus_departing_at_time = transit_details['departure_time']['text']

                    # extract num stops
                    bus_number_of_stops = transit_details['num_stops']

    response_str = response_str.format(bus_number,
                                       bus_name,
                                       bus_departing_stop,
                                       bus_departing_at_time,
                                       bus_number_of_stops,
                                       bus_arriving_stop,
                                       bus_arriving_at_time,
                                       bus_agency)
    print response_str
    return response_str


@app.route('/time_to_dest/<destination>')
def get_time_to_dest(destination):
    # extract lat, long from request
    latitude = request.args.get('lat')
    longitude = request.args.get('long')

    # construct origin & destination coordinates
    origin_coordinates = (latitude, longitude)
    destination_coordinates = ()

    # result pieces
    dist_by_bus = None
    time_by_bus = None
    dist_by_walk = None
    time_by_walk = None
    dist_by_car = None
    time_by_car = None

    # final output string
    response_str = "Okay, I have an answer. " \
                   "Your requested destination is {}. " \
                   "I calculated, that, if you take a bus, it will take you {} to travel a distance of {}. " \
                   "But, if you take an UBER, it will take you {} to travel a distance of {}. " \
                   "Also, if you decide to walk, it will take you {} to walk a distance of {}. " \
                   "Hope that answers your question."

    # use geocoding to get coordinates
    destination_info = gmaps.geocode(destination)
    if (destination_info.__len__() > 0):
        dest_lat = destination_info[0]['geometry']['location']['lat']
        dest_long = destination_info[0]['geometry']['location']['lng']
        destination_coordinates = (dest_lat, dest_long)
        pass

    # get next bus details
    bus_distance_matrix = gmaps.distance_matrix(origins=origin_coordinates,
                                                destinations=destination_coordinates,
                                                mode='transit',
                                                transit_mode='bus',
                                                units='imperial')
    if (bus_distance_matrix['status'] == 'OK'):
        if (bus_distance_matrix['rows'].__len__()):
            elements = bus_distance_matrix['rows'][0]['elements']
            if (elements.__len__() > 0):
                if(elements[0]['status'] == 'OK'):
                    dist_by_bus = elements[0]['distance']['text'].replace('mi', 'miles')
                    time_by_bus = elements[0]['duration']['text']


    car_distance_matrix = gmaps.distance_matrix(origins=origin_coordinates,
                                                destinations=destination_coordinates,
                                                mode='driving',
                                                units='imperial')
    if (car_distance_matrix['status'] == 'OK'):
        if (car_distance_matrix['rows'].__len__()):
            elements = car_distance_matrix['rows'][0]['elements']
            if (elements.__len__() > 0):
                if(elements[0]['status'] == 'OK'):
                    dist_by_car = elements[0]['distance']['text'].replace('mi', 'miles')
                    time_by_car = elements[0]['duration']['text']

    walk_distance_matrix = gmaps.distance_matrix(origins=origin_coordinates,
                                                 destinations=destination_coordinates,
                                                 mode='walking',
                                                 units='imperial')
    if (walk_distance_matrix['status'] == 'OK'):
        if (walk_distance_matrix['rows'].__len__()):
            elements = walk_distance_matrix['rows'][0]['elements']
            if (elements.__len__() > 0):
                if(elements[0]['status'] == 'OK'):
                    dist_by_walk = elements[0]['distance']['text'].replace('mi', 'miles')
                    time_by_walk = elements[0]['duration']['text']

    response_str = response_str.format(destination,
                                       time_by_bus, dist_by_bus,
                                       time_by_car, dist_by_car,
                                       time_by_walk, dist_by_walk)
    print response_str
    return response_str


def main():
    app.run()


if __name__ == "__main__":
    main()
