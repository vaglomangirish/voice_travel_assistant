from flask import Flask
from flask import request

import requests
import json

app = Flask(__name__)
google_search_api_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/output?"

@app.route('/nearest_bustop/<location>')
def get_nearest_bustops(location):
    print (location)
    return "Success"


def main():

    app.run()


if __name__ == "__main__":
    main()