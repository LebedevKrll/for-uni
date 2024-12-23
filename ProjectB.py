
import asyncio
import logging
from aiogram import Bot, types
from aiogram import Dispatcher
from aiogram.filters import Command

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
/weather который после себя требует еще пару аргументов : Команда запрашивает начальную и конечную точки маршрута, временной интервал прогноза и поддерживает добавление промежуточных остановок.""")

# Хендлер на команду /weather
@dp.message(Command("weather"))
async def cmd_weather(message: types.Message, command: Command):
    # Проверяем наличие аргументов
    if command.args:
        await message.answer(f"Вы запросили погоду для: {command.args}")
    else:
        await message.answer("Ошибка: не указаны аргументы. Пример использования: /weather <город>")

# Запуск процесса поллинга новых обновлений
async def main():
    await dp.start_polling(bot)

if __name__ == "__main__":
    asyncio.run(main())
