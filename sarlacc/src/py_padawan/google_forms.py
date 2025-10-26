import requests

form_url = 'https://docs.google.com/forms/d/e/...'

url_response = form_url + '/formResponse'
url_referer = form_url + '/viewform'

user_agent = {
    'Referer': url_referer,
    'User-Agent': 'Mozilla/5.O (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome(28.0.1500.52 '
                  'Safari/537.36'
}

request = {
    'entry.11111111': 'value 1',
    'entry.22222222': 'value 2',
    'entry.33333333': 'value 3'
}

response = requests.post(url_response, data=request, headers=user_agent)
print(request)
print(response)
