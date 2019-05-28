#!/usr/bin/env python
import requests

headers = {
    'Content-Type': 'application/json',
}

data = '{"items":[{"name":"hhgtg","id":42}]}'
response = requests.post('http://localhost:8080/create-order', headers=headers, data=data)
print(response.text)
