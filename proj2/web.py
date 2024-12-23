
from forecast import get_weather_parameters, get_loc_key_by_city, check_cond
from flask import Flask, request, render_template

key = "nhpcWpzckAXqYhdT5M989eUHdYLBwJTm"
lat = 55.7558
lon = 37.6173

app = Flask(__name__, template_folder='template')

@app.route('/')
def home():
    return render_template('html.html')

@app.route('/submit-route', methods=['POST'])
def submit_route():
    try:
        start = request.form.get('start')
        end = request.form.get('end')
        start_loc_key = get_loc_key_by_city(key=key, city=start)
        end_loc_key = get_loc_key_by_city(key=key, city=end)
        if not start_loc_key or not end_loc_key:
            return 'не удалось получить ключи точек'
        start_weather = get_weather_parameters(key=key, loc_key=start_loc_key)
        end_weather = get_weather_parameters(key=key, loc_key=end_loc_key)
        if not start_weather or not end_weather:
            return 'не удалось получить погоду'
        start_weather_cond = check_cond(start_weather)
        end_weather_cond = check_cond(end_weather)
        if not start_weather_cond and not end_weather_cond:
            return 'все отлично'
        elif start_weather_cond and not end_weather_cond:
            return f'плохо в {start}'
        elif not start_weather_cond and end_weather_cond:
            return f'плохо в {end}'
        return 'все плохо, бро, сиди дома'
    except Exception as e:
        return str(e)

if __name__ == '__main__':
    app.run(debug=True)

