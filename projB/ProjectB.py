import asyncio
import logging
from aiogram.filters import Command
from aiogram import Bot, Dispatcher, types
import requests as rq

# Настройка логирования
logging.basicConfig(level=logging.INFO)

# С этой функций мы уже все знакомы
def get_loc_key_by_city(city, key):
    url = "http://dataservice.accuweather.com/locations/v1/cities/search"
    params = {
        "apikey": key,
        "q": city,
        "language": "en-us"
    }
    try:
        response = rq.get(url, params=params)
        response.raise_for_status()  # Проверка на ошибки HTTP
        data = response.json()
        if data:
            return data[0].get("Key", "Not Found")
        else:
            return None
    except Exception as e:
        logging.error(f"Error fetching location key: {e}")
        return None

# Функция для получения прогноза погоды на несколько дней
def forecaster(start, end, time, additional=[]):
    key = '3lBKXfV0pQAnQjBoshtMLSoDQGZkeyqr'
    forecast_data = {}

    cities = [start, end] + list(additional)
    try:
        for city in cities:
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
            response = rq.get(url, params=params)
            response.raise_for_status()  # Проверка на ошибки HTTP
            data = response.json()

            forecast_for_point = []
            for day in data['DailyForecasts'][:int(time)]:
                date = day['Date']
                temperature_min = day['Temperature']['Minimum']['Value']
                temperature_max = day['Temperature']['Maximum']['Value']
                wind_speed = day['Day']['Wind']['Speed']['Value']
                humidity_average = day['Day']['RelativeHumidity']['Average']
                
                # Форматирование строки с прогнозом
                forecast_for_point.append(f'''
Дата: {date}
Max температура: {temperature_max}
Min температура: {temperature_min}
Скорость ветра: {wind_speed} км/ч
Влажность: {humidity_average}%
''')
            forecast_data[city] = forecast_for_point
        return forecast_data
    except Exception as e:
        logging.error(f"Error fetching forecast: {e}")
        return []

# Инициализация бота и диспетчера
bot = Bot()
dp = Dispatcher()

# Обработчик команды /start
@dp.message(Command("start"))
async def cmd_start(message: types.Message):
    await message.answer("""Привет! Этот бот выдает погоду. Бот должен давать прогноз погоды на несколько дней вперёд для начальной, конечной и промежуточных точек маршрута.
Для более подробной информации используйте /help.""")

# Обработчик команды /help
@dp.message(Command("help"))
async def cmd_help(message: types.Message):
    await message.answer("""Есть три команды:
/help - который ты читаешь сейчас;
/start - который запускает бота и 
/weather - который после себя требует еще пару аргументов. Команда запрашивает начальную и конечную точки маршрута,
прогноз на сколько дней (вплоть до 5, больше API не позволяет), поддерживает добавление промежуточных остановок.
Именно в таком порядке, все через пробел.""")

# Обработчик команды /weather
@dp.message(Command("weather"))
async def cmd_weather(message: types.Message, command: Command):
    if command.args:
        args = command.args.split()
        
        # Проверка количества аргументов
        if len(args) >= 3:
            ans_info = forecaster(args[0], args[1], args[2], args[3:])
            
            # Отправка прогноза для каждого города
            for city in ans_info.keys():
                for forecast in ans_info[city]:  
                    await message.answer(f'''{city}:
{forecast}''')
        else:
            await message.answer("Ошибка: недостаточно аргументов. Пример использования: /weather hawaii moscow 3")
    else:
        await message.answer("Ошибка: не указаны аргументы. Пример использования: /weather hawaii moscow 3 new-york")

# Основная функция для запуска бота
async def main():
    await dp.start_polling(bot)

# Запуск бота
if __name__ == "__main__":
    asyncio.run(main())
