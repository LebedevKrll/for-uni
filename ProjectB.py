
import asyncio
import logging
from aiogram import Bot, types
from aiogram import Dispatcher
from aiogram.filters import Command
from forecast import get_loc_key_by_lat_lon, get_weather_parameters

def forecast(lat, lon, time_period, add_stops=None):
    wkey = 'nhpcWpzckAXqYhdT5M989eUHdYLBwJTm'
    mkey = ''

# Настройка логирования
logging.basicConfig(level=logging.INFO)

# Создание объекта бота
bot = Bot(token="7872451386:AAH3QMBY5sXfE9FNEklxOloXL8SMieJXO2E")
dp = Dispatcher()

# Хендлер на команду /start
@dp.message(Command("start"))
async def cmd_start(message: types.Message):
    await message.answer("привет, этот бот выдает погоду. Бот должен давать прогноз погоды на несколько дней вперёд для начальной, конечной и промежуточных точек маршрута.")

# Хендлер на команду /help
@dp.message(Command("help"))
async def cmd_start(message: types.Message):
    await message.answer("""есть три команды:
/help который ты читаешь сейчас;
/start который запускает бота и 
/weather который после себя требует еще пару аргументов : Команда запрашивает начальную и конечную точки маршрута (lat; lon),
временной интервал прогноза, поддерживает добавление промежуточных остановок.
Именно в таком порядке, все через пробел""")

# Хендлер на команду /weather
@dp.message(Command("weather"))
async def cmd_weather(message: types.Message, command: Command):
    # Проверяем наличие аргументов
    if command.args:
        args = command.args.split()
        if len(args) >= 3:
            if len(args) > 3:
                await message.answer(forecast(args[0], args[1], args[2], args[3:]))
            else:
                await message.answer(forecast(args[0], args[1], args[2]))
        else:
            await message.answer("Ошибка: не достаточно аргументов. Пример использования: /weather <lat> <lon> <time>")
    else:
        await message.answer("Ошибка: не указаны аргументы. Пример использования: /weather <lat> <lon> <time>")

# Запуск процесса поллинга новых обновлений
async def main():
    await dp.start_polling(bot)

if __name__ == "__main__":
    asyncio.run(main())
