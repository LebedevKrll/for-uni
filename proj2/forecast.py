
import requests as rq

def get_loc_key_by_city(city, key):
    url = "http://dataservice.accuweather.com/locations/v1/cities/search"
    params = {
        "apikey": key,
        "q": city,
        "language": "en-us"
    }
    try:
        response = rq.get(url, params=params)
        response.raise_for_status()
        data = response.json()
        if data:
            return data[0].get("Key", "Not Found")
        else:
            return None
    except Exception as e:
        return e

def get_loc_key_by_lat_lon(key, lat, lon):
    try:
        url = f"http://dataservice.accuweather.com/locations/v1/cities/geoposition/search"
        params = {
            "apikey": key,
            "q": f"{lat},{lon}"
        }
        location_response = rq.get(url, params=params)
        location_response.raise_for_status()
        data = location_response.json()
        return data["Key"]
    except Exception as e:
        return e

def get_weather_parameters(key, loc_key):
    weather_dict = {}
    try:
        url = f"http://dataservice.accuweather.com/currentconditions/v1/{loc_key}"
        params = {
            "apikey": key,
            "details": "true"
        }
        response = rq.get(url, params=params)
        response.raise_for_status()
        data = response.json()[0]
        humidity = data["RelativeHumidity"]
        temp = data["Temperature"]["Metric"]["Value"]
        wind_speed = data["Wind"]["Speed"]["Metric"]["Value"]
        rain_probability = data.get("PrecipitationProbability", 0)
        weather_dict['wind_speed'] = wind_speed
        weather_dict['rain_probability'] = rain_probability
        weather_dict['temperature'] = temp
        weather_dict['humidity'] = humidity
        return weather_dict
    except Exception as e:
        return e

def check_cond(weather_params):
    if (weather_params['temperature'] < 0) or (weather_params['temperature'] > 35) or (weather_params['wind_speed'] > 50) or (weather_params['rain_probability'] > 70):
        return True
    return False


key = "nhpcWpzckAXqYhdT5M989eUHdYLBwJTm"
lat = 55.7558
lon = 37.6173

