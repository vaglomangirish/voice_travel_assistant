import json
import requests

from pprint import pprint

fcm_token_file = 'fcm_token'
fcm_push_title = 'TravelAssistant_Push'
fcm_push_url = 'https://fcm.googleapis.com/fcm/send'

fcm_server_key = 'AAAAbjz1C3w:APA91bGRo1iLPILdOcBsbxwiUPZDrpciuiS8XKDC0Hcgs6-' \
                 'j9zYOAVkRVyOryUr3BGG8bBGFY1xMp64yHmO8p8UTX-hRA-qCILj9oZq-1V' \
                 'AkbMl7AdmkqfrAepBKfw_tBidBO_O2S0Df0uVWArNS506_9hm7ii5ohw'

fcm_auth_header = 'key={}'.format(fcm_server_key)

fcm_headers = {'content-type': 'application/json', 'Authorization': fcm_auth_header}

fcm_push_message = {'to': '{}', 'notification': {'title': '{}', 'body': '{}'}}

def send_push_notification(message_body):
    # read token value from file
    token_value = None
    with open(fcm_token_file, 'r') as fp:
        for row in fp:
            token_value = row
            break

    # construct push message
    fcm_push_message['to'] = token_value
    fcm_push_message['notification']['title'] = fcm_push_title
    fcm_push_message['notification']['body'] = message_body

    # send push
    response = requests.post(fcm_push_url,
                             data=json.dumps(fcm_push_message),
                             headers=fcm_headers)
    pprint(response.text)
    pass

def update_refresh_token(new_token):
    with open(fcm_token_file, 'w') as fp:
        fp.write(new_token)
    print ("Updated refresh token to: " + new_token)
    pass


