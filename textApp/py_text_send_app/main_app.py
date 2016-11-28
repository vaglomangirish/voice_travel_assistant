from flask import Flask
from flask import request

import requests
import json

app = Flask(__name__)
text_msg_url = "http://textbelt.com/text"

class MainApp:

    text_msg_url = "http://textbelt.com/text"

    def __init__(self):
        pass

@app.route("/sendmsg", methods=['POST'])
def send_text_message():

    """
    This is a REST method which requires json data in the following format:-

    {'number': '<number to send>', 'message': '<text to send>'}

    :param number:
    :param text:
    :return:
    """

    json_data = json.loads(request.data)
    print(json_data["number"])
    print(json_data["message"])

    req = requests.post(text_msg_url, data={'number': json_data["number"], 'message': json_data["message"]})

    return "SUCCESS"


def main():

    app.run()


if __name__ == "__main__":
    main()