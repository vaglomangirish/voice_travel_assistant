import requests
import json

from pprint import pprint
import googlemaps

text_msg_url = "http://textbelt.com/text"

driver_name = "Mangirish"
driver_phone = "8123695213"

passenger_name = "Gourav"
passenger_phone = "8123609729"

gmaps = googlemaps.Client(key='AIzaSyBKXtm4_ZK_JqlEQGuOOplSmFZSYS6BmXw')

def send_text_message(source_lat, source_lng, destination):

    """
    This is a REST method which requires json data in the following format:-

    {'number': '<number to send>', 'message': '<text to send>'}

    """

    # Look up an address with reverse geocoding
    source = gmaps.reverse_geocode(latlng=(source_lat, source_lng))
    # pprint(source)

    # hard-wire source for now
    source = "Hoosier Court Apts"

    message_to_driver = "You have a ride! Please pick up " + passenger_name + " at "\
                        + source + ". His destination is " + destination
    requests.post(text_msg_url, data={'number': driver_phone, 'message': message_to_driver})

    message_to_passenger = "Your driver's name is, Mr. " + driver_name + ". And your ride to, " \
              + destination + ", is on the way to pick you up at, " + source + ". Thank you for choosing UBER."
    requests.post(text_msg_url, data={'number': passenger_phone, 'message': message_to_passenger})

    return "SUCCESS"


def request_ride(request):
    """
    This is a REST method which requires json data in the following format:-

    {'source': '<source name>', 'destination': '<destination name>'}

    """
    json_data = json.loads(request.data)
    print(json_data["source_lat"])
    print(json_data["source_lng"])
    print(json_data["destination"])

    send_text_message(json_data["source_lat"], json_data["source_lng"], json_data["destination"])

    return "SUCCESS"