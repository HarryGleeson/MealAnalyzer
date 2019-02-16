import requests
import json

''' get_nutritional_information.py:
This class retrieves the fat per gram of each food item passed to it from the Edamam nutrition analysis
API.
'''

def nutrients(food):
    urlString = "https://api.edamam.com/api/food-database/parser?ingr="
    urlString+= food;
    urlString+= "&app_id=e5ad1ee3&app_key=d2bd076a1151f62500c305f8bc7e9d9f"
    measureURI = "http://www.edamam.com/ontologies/edamam.owl#Measure_gram"
    resp = requests.get(urlString)
    if resp.status_code != 200:
        raise ApiError('GET /tasks/ {}'.format(resp.status_code))
    data = resp.json()
    foodURI = data['parsed'][0]['food']['uri']
    data = {    'yield':1,
                'ingredients': [
                    {
                        'quantity':1,
                        'measureURI':measureURI,
                        'foodURI':foodURI
                    }
                ]
            }    
    post = requests.post('https://api.edamam.com/api/food-database/nutrients?app_id=e5ad1ee3&app_key=d2bd076a1151f62500c305f8bc7e9d9f', json=data)
    postData = post.json()
    fat = postData['totalNutrients']['FAT']['quantity']
    return fat