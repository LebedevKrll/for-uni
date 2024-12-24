
import plotly.graph_objects as go
import requests as rq
import dash
from dash import dcc, html
from dash.dependencies import Input, Output, State
import dash_bootstrap_components as dbc

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
        print(f"Error fetching location key: {e}")
        return None

def forecast(city, key):
    loc_key = get_loc_key_by_city(city, key)
    if not loc_key:
        return []

    url = f'http://dataservice.accuweather.com/forecasts/v1/daily/5day/{loc_key}'
    params = {
        'apikey': key,
        'language': 'en-us',
        'details': True,
        'metric': True
    }
    try:
        response = rq.get(url, params=params)
        response.raise_for_status()
        data = response.json()

        forecast_data = []
        for day in data['DailyForecasts']:
            date = day['Date']
            temperature_min = day['Temperature']['Minimum']['Value']
            temperature_max = day['Temperature']['Maximum']['Value']
            wind_speed = day['Day']['Wind']['Speed']['Value']
            humidity_average = day['Day']['RelativeHumidity']['Average']
            forecast_data.append({
                "Date": date,
                "Temperature": {"Min": temperature_min, "Max": temperature_max},
                "Wind Speed (km/h)": wind_speed,
                "Humidity (%)": humidity_average
            })
        return forecast_data
    except Exception as e:
        print(f"Error fetching forecast: {e}")
        return []
    
 

app = dash.Dash(__name__, external_stylesheets=[dbc.themes.BOOTSTRAP])

countries = []

app.layout = html.Div([
    html.H1("Прогноз погоды на следующие 5 дней"),
    
    dcc.Input(id='city_start', type='text', value='hawaii', placeholder='точка начала'),
    dcc.Input(id='city_end', type='text', value='moscow', placeholder='точка конца'),

    dcc.Dropdown(
        id='parameter-dropdown1',
        options=[
            {'label': 'Минимальная температура (°C)', 'value': 'min_temp'},
            {'label': 'Максимальная температура (°C)', 'value': 'max_temp'},
            {'label': 'Скорость ветра (км/ч)', 'value': 'wind_speed'},
            {'label': 'Влажность (%)', 'value': 'humidity'},
        ],
        value='min_temp',
        clearable=False,
    ),

    dcc.Dropdown(
        id='parameter-dropdown2',
        options=[
            {'label': '5', 'value': 5},
            {'label': '4', 'value': 4},
            {'label': '3', 'value': 4},
            {'label': '2', 'value': 4},
            {'label': 'Выберете на склько дней прогноз', 'value': 5},
        ],
        value='Выберете на сколько дней прогноз',
        clearable=False,
    ),
    
    dbc.Button("Добавить промежуточную точку", id="open-modal", n_clicks=0),
    
    dbc.Modal(
        [
            dbc.ModalHeader("Добавить промежуточную точку"),
            dbc.ModalBody(dcc.Input(id='country-input', type='text', placeholder='Введите промежуточную точку')),
            dbc.ModalFooter(
                [
                    dbc.Button("Добавить", id="add-country", n_clicks=0),
                    dbc.Button("Закрыть", id="close-modal", className="ml-auto", n_clicks=0)
                ]
            ),
        ],
        id="modal",
        is_open=False,
    ),
    
    html.Div(id='country-list'),
    
    html.Div(id='graphs-container'),
    
    dcc.Graph(id='start_graph'),
    dcc.Graph(id='end_graph'),
])

@app.callback(
    Output("modal", "is_open"),
    [Input("open-modal", "n_clicks"),
     Input("close-modal", "n_clicks")],
    State("modal", "is_open"),
)
def toggle_modal(open_clicks, close_clicks, is_open):
    if open_clicks or close_clicks:
        return not is_open
    return is_open

@app.callback(
    Output('country-list', 'children'),
    Output('graphs-container', 'children'),
    Input('add-country', 'n_clicks'),
    State('country-input', 'value'),
)
def add_country(n_clicks, country_name):
    global countries
    if n_clicks > 0 and country_name:
        countries.append(country_name)
    
    country_list_display = f"Добавленные страны: {', '.join(countries)}"
    
    graphs = []
    for country in countries:
        graphs.append(dcc.Graph(id=f'graph-{country}', figure=create_figure_for_country(country)))

    return country_list_display, graphs

def create_figure_for_country(country):
    data = forecast(country, "3lBKXfV0pQAnQjBoshtMLSoDQGZkeyqr")
    
    if not data:
        return go.Figure() 

    dates = [day['Date'] for day in data]
    
    values = [day['Temperature']['Min'] for day in data] 
    
    fig = go.Figure()
    fig.add_trace(go.Scatter(x=dates, y=values, mode='lines+markers', name=f'Минимальная температура (°C)'))
    
    fig.update_layout(title=f'Прогноз погоды на следующие 5 дней для {country}',
                      xaxis_title='Дата',
                      yaxis_title='Температура (°C)',
                      template='plotly_white')
    
    return fig

@app.callback(
    [Output('start_graph', 'figure'),
     Output('end_graph', 'figure')],
    [Input('parameter-dropdown1', 'value'),
     Input('parameter-dropdown2', 'value'),
     Input('city_start', 'value'), 
     Input('city_end', 'value')]
)
def update_graph(selected_parameter, length, city_start, city_end):

    data1 = forecast(city_start, "3lBKXfV0pQAnQjBoshtMLSoDQGZkeyqr")
    data2 = forecast(city_end, "3lBKXfV0pQAnQjBoshtMLSoDQGZkeyqr")

    def create_figure(data, city):
        if not data:
            return go.Figure()

        dates = [day['Date'] for day in data[:length]]
        
        if selected_parameter == "min_temp":
            values = [day['Temperature']['Min'] for day in data[:length]]
            title_suffix = 'Минимальная температура (°C)'
        elif selected_parameter == "max_temp":
            values = [day['Temperature']['Max'] for day in data[:length]]
            title_suffix = 'Максимальная температура (°C)'
        elif selected_parameter == "wind_speed":
            values = [day['Wind Speed (km/h)'] for day in data[:length]]
            title_suffix = 'Скорость ветра (км/ч)'
        elif selected_parameter == "humidity":
            values = [day['Humidity (%)'] for day in data[:length]]
            title_suffix = 'Влажность (%)'
        
        fig = go.Figure()
        fig.add_trace(go.Scatter(x=dates, y=values, mode='lines+markers', name=title_suffix))
        
        fig.update_layout(title=f'Прогноз погоды на следующие {length} дней ({title_suffix}) для {city}',
                          xaxis_title='Дата',
                          yaxis_title='Значение',
                          legend_title='Параметры',
                          template='plotly_white')
        
        return fig

    fig1 = create_figure(data1, city_start)
    fig2 = create_figure(data2, city_end)

    return fig1, fig2

if __name__ == '__main__':
    app.run_server(debug=True)