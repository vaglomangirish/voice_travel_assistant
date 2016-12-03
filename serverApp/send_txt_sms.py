import requests
import json

text_msg_url = "http://textbelt.com/text"

driver_name = "Mangirish"
driver_phone = "8123695213"

passenger_name = "Gourav"
passenger_phone = "8123609729"

def send_text_message(source, destination):

    """
    This is a REST method which requires json data in the following format:-

    {'number': '<number to send>', 'message': '<text to send>'}

    """

    message_to_driver = "You have a ride! Please pick up " + passenger_name + " at "\
                        + source + ". His destination is " + destination
    req = requests.post(text_msg_url, data={'number': driver_phone, 'message': message_to_driver})

    message_to_passenger = "Your drivers name is " + driver_name + " and your ride to " \
              + destination + " is on the way to pick you up at " + source + "."
    req = requests.post(text_msg_url, data={'number': passenger_phone, 'message': message_to_passenger})

    return "SUCCESS"


def request_ride(request):
    """
    This is a REST method which requires json data in the following format:-

    {'source': '<source name>', 'destination': '<destination name>'}

    """
    json_data = json.loads(request.data)
    print(json_data["source"])
    print(json_data["destination"])

    send_text_message(json_data["source"], json_data["destination"])

    return "SUCCESS"