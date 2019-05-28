#!/usr/bin/env python
import requests

response = requests.get('http://localhost:8080/item/42')
print(response.text)
