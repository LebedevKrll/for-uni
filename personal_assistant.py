import json
import csv
from datetime import datetime
import os


NOTE_FILE = 'notes.json'
TASK_FILE = 'tasks.json'
CONTACT_FILE = 'contacts.json'
FINANCE_FILE = 'finance.json'

class Note:
    def __init__(self, id, title, content):
        self.id = id
        self.title = title
        self.content = content
        self.timestamp = datetime.now().strftime('%d-%m-%Y %H:%M:%S')

class Task:
    def __init__(self, id, title, description='', done=False, priority='Низкий', due_date=None):
        self.id = id
        self.title = title
        self.description = description
        self.done = done
        self.priority = priority
        self.due_date = due_date

class Contact:
    def __init__(self, id, name, phone='', email=''):
        self.id = id
        self.name = name
        self.phone = phone
        self.email = email

class FinanceRecord:
    def __init__(self, id, amount, category, date=None, description=''):
        self.id = id
        self.amount = amount
        self.category = category
        self.date = date or datetime.now().strftime('%d-%m-%Y')
        self.description = description

def load_notes():
    if os.path.exists(NOTE_FILE):
        with open(NOTE_FILE) as f:
            return json.load(f)
    return []

def save_notes(notes):
    with open(NOTE_FILE, 'w') as f:
        json.dump(notes, f)

def add_note():
    notes = load_notes()
    title = input('Введите заголовок заметки: ')
    content = input('Введите содержимое заметки: ')
    note_id = len(notes) + 1
    note = Note(note_id, title, content)
    notes.append(note.__dict__)
    save_notes(notes)
    print('Заметка добавлена.')

def view_notes():
    notes = load_notes()
    for note in notes:
        print(f'{note['id']}: {note['title']} (Создано: {note['timestamp']})')

def load_tasks():
    if os.path.exists(TASK_FILE):
        with open(TASK_FILE) as f:
            return json.load(f)
    return []

def save_tasks(tasks):
    with open(TASK_FILE, 'w') as f:
        json.dump(tasks, f)

def add_task():
    tasks = load_tasks()
    title = input('Введите заголовок задачи: ')
    description = input('Введите описание задачи: ')
    priority = input('Введите приоритет (Высокий/Средний/Низкий): ')
    due_date = input('Введите срок выполнения (ДД-ММ-ГГГГ): ')
    
    task_id = len(tasks) + 1
    task = Task(task_id, title, description, False, priority, due_date)
    
    tasks.append(task.__dict__)
    save_tasks(tasks)
    print('Задача добавлена.')

def view_tasks():
    tasks = load_tasks()
    if not tasks:
        print('Нет задач для отображения.')
        return
    
    for task in tasks:
        status = 'Выполнена' if task['done'] else 'Не выполнена'
        print(f'{task['id']}: {task['title']} - {status} (Приоритет: {task['priority']}, Срок: {task['due_date']})')

def edit_task():
    tasks = load_tasks()
    task_id = int(input('Введите ID задачи для редактирования: '))
    
    for task in tasks:
        if task['id'] == task_id:
            task['title'] = input('Введите новый заголовок задачи: ') or task['title']
            task['description'] = input('Введите новое описание задачи: ') or task['description']
            task['priority'] = input('Введите новый приоритет (Высокий/Средний/Низкий): ') or task['priority']
            task['due_date'] = input('Введите новый срок выполнения (ДД-ММ-ГГГГ): ') or task['due_date']
            save_tasks(tasks)
            print('Задача обновлена.')
            return
    
    print('Задача не найдена.')

def delete_task():
    tasks = load_tasks()
    task_id = int(input('Введите ID задачи для удаления: '))
    
    for i, task in enumerate(tasks):
        if task['id'] == task_id:
            del tasks[i]
            save_tasks(tasks)
            print('Задача удалена.')
            return
    
    print('Задача не найдена.')

def mark_task_done():
    tasks = load_tasks()
    task_id = int(input('Введите ID задачи для отметки как выполненной: '))
    
    for task in tasks:
        if task['id'] == task_id:
            task['done'] = True
            save_tasks(tasks)
            print('Задача отмечена как выполненная.')
            return
    
    print('Задача не найдена.')

def export_tasks_to_csv():
    tasks = load_tasks()
    
    with open('tasks.csv', 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['id', 'title', 'description', 'done', 'priority', 'due_date']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        writer.writerows(tasks)
    
    print('Задачи экспортированы в tasks.csv.')

def import_tasks_from_csv():
    with open('tasks.csv', newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile)
        tasks = load_tasks()
        
        for row in reader:
            row['id'] = len(tasks) + 1
            row['done'] = row['done'] == 'True'
            tasks.append(row)
        
        save_tasks(tasks)
    
    print('Задачи импортированы из tasks.csv.')

def load_contacts():
    if os.path.exists(CONTACT_FILE):
        with open(CONTACT_FILE) as f:
            return json.load(f)
    return []

def save_contacts(contacts):
    with open(CONTACT_FILE, 'w') as f:
        json.dump(contacts, f)

def add_contact():
    contacts = load_contacts()
    name = input('Введите имя контакта: ')
    phone = input('Введите номер телефона: ')
    email = input('Введите адрес электронной почты: ')
    
    contact_id = len(contacts) + 1
    contact = Contact(contact_id, name, phone, email)
    
    contacts.append(contact.__dict__)
    save_contacts(contacts)
    print('Контакт добавлен.')

def search_contact():
    contacts = load_contacts()
    search_term = input('Введите имя или номер телефона для поиска: ')
    
    found_contacts = [contact for contact in contacts if search_term in contact['name'] or search_term in contact['phone']]
    
    if found_contacts:
        for contact in found_contacts:
            print(f'{contact['id']}: {contact['name']} - Телефон: {contact['phone']}, Email: {contact['email']}')
    else:
        print('Контакты не найдены.')

def edit_contact():
    contacts = load_contacts()
    contact_id = int(input('Введите ID контакта для редактирования: '))
    
    for contact in contacts:
        if contact['id'] == contact_id:
            contact['name'] = input('Введите новое имя контакта: ') or contact['name']
            contact['phone'] = input('Введите новый номер телефона: ') or contact['phone']
            contact['email'] = input('Введите новый адрес электронной почты: ') or contact['email']
            save_contacts(contacts)
            print('Контакт обновлён.')
            return
    
    print('Контакт не найден.')

def delete_contact():
    contacts = load_contacts()
    contact_id = int(input('Введите ID контакта для удаления: '))
    
    for i, contact in enumerate(contacts):
        if contact['id'] == contact_id:
            del contacts[i]
            save_contacts(contacts)
            print('Контакт удалён.')
            return
    
    print('Контакт не найден.')

def export_contacts_to_csv():
    contacts = load_contacts()
    
    with open('contacts.csv', 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['id', 'name', 'phone', 'email']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        writer.writerows(contacts)
    
    print('Контакты экспортированы в contacts.csv.')

def import_contacts_from_csv():
    with open('contacts.csv', newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile)
        contacts = load_contacts()
        
        for row in reader:
            row['id'] = len(contacts) + 1
            contacts.append(row)
        
        save_contacts(contacts)
    
    print('Контакты импортированы из contacts.csv.')

def load_finance_records():
    if os.path.exists(FINANCE_FILE):
        with open(FINANCE_FILE) as f:
            return json.load(f)
    return []

def save_finance_records(records):
    with open(FINANCE_FILE, 'w') as f:
        json.dump(records, f)

def add_finance_record():
    records = load_finance_records()
    amount = float(input('Введите сумму операции (положительное число для дохода, отрицательное для расхода): '))
    category = input('Введите категорию операции (например, "Еда", "Транспорт"): ')
    date = input('Введите дату операции (ДД-ММ-ГГГГ): ')
    description = input('Введите описание операции: ')
    
    record_id = len(records) + 1
    record = FinanceRecord(record_id, amount, category, date, description)
    
    records.append(record.__dict__)
    save_finance_records(records)
    print('Финансовая запись добавлена.')

def view_finance_records():
    records = load_finance_records()
    if not records:
        print('Нет финансовых записей для отображения.')
        return
    
    for record in records:
        print(f'{record['id']}: {record['description']} - Сумма: {record['amount']} ({record['category']}) - Дата: {record['date']}')

def edit_finance_record():
    records = load_finance_records()
    record_id = int(input('Введите ID финансовой записи для редактирования: '))
    
    for record in records:
        if record['id'] == record_id:
            record['amount'] = float(input('Введите новую сумму операции: ') or record['amount'])
            record['category'] = input('Введите новую категорию операции: ') or record['category']
            record['date'] = input('Введите новую дату операции (ДД-ММ-ГГГГ): ') or record['date']
            record['description'] = input('Введите новое описание операции: ') or record['description']
            save_finance_records(records)
            print('Финансовая запись обновлена.')
            return
    
    print('Финансовая запись не найдена.')

def delete_finance_record():
    records = load_finance_records()
    record_id = int(input('Введите ID финансовой записи для удаления: '))
    
    for i, record in enumerate(records):
        if record['id'] == record_id:
            del records[i]
            save_finance_records(records)
            print('Финансовая запись удалена.')
            return
    
    print('Финансовая запись не найдена.')

def export_finance_records_to_csv():
    records = load_finance_records()
    
    with open('finance.csv', 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['id', 'amount', 'category', 'date', 'description']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        writer.writerows(records)
    
    print('Финансовые записи экспортированы в finance.csv.')

def import_finance_records_from_csv():
    with open('finance.csv', newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile)
        records = load_finance_records()
        
        for row in reader:
            row['id'] = len(records) + 1
            row['amount'] = float(row['amount'])
            records.append(row)
        
        save_finance_records(records)
    
    print('Финансовые записи импортированы из finance.csv.')

def notes_menu():
    while True:
        print('''Управление заметками
1. Добавить заметку
2. Просмотреть все заметки
3. Вернуться в главное меню''')

        choice = input('Ваш выбор: ')
        
        if choice == '1':
            add_note()
        elif choice == '2':
            view_notes()
        elif choice == '3':
            break
        else:
            print('Некорректный ввод.')

def tasks_menu():
    while True:
        print('''Управление задачами:
1. Добавить задачу
2. Просмотреть все задачи
3. Редактировать задачу
4. Удалить задачу
5. Отметить задачу как выполненную
6. Экспортировать задачи в CSV
7. Импортировать задачи из CSV
8. Вернуться в главное меню''')

        choice = input('Ваш выбор: ')
        
        if choice == '1':
            add_task()
        elif choice == '2':
            view_tasks()
        elif choice == '3':
            edit_task()
        elif choice == '4':
            delete_task()
        elif choice == '5':
            mark_task_done()
        elif choice == '6':
            export_tasks_to_csv()
        elif choice == '7':
            import_tasks_from_csv()
        elif choice == '8':
            break
        else:
            print('Некорректный ввод.')

def contacts_menu():
    while True:
        print('''Управление контактами:
1. Добавить контакт
2. Поиск контакта
3. Редактировать контакт
4. Удалить контакт
5. Экспортировать контакты в CSV
6. Импортировать контакты из CSV
7. Вернуться в главное меню''')

        choice = input('Ваш выбор: ')
        
        if choice == '1':
            add_contact()
        elif choice == '2':
            search_contact()
        elif choice == '3':
            edit_contact()
        elif choice == '4':
            delete_contact()
        elif choice == '5':
            export_contacts_to_csv()
        elif choice == '6':
            import_contacts_from_csv()
        elif choice == '7':
            break
        else:
            print('Некорректный ввод.')

def finance_menu():
    while True:
        print('''Управление финансовыми записями:
1. Добавить финансовую запись
2. Просмотреть все записи
3. Редактировать запись
4. Удалить запись
5. Экспортировать записи в CSV
6. Импортировать записи из CSV
7. Вернуться в главное меню''')

        choice = input('Ваш выбор: ')
        
        if choice == '1':
            add_finance_record()
        elif choice == '2':
            view_finance_records()
        elif choice == '3':
            edit_finance_record()
        elif choice == '4':
            delete_finance_record()
        elif choice == '5':
            export_finance_records_to_csv()
        elif choice == '6':
            import_finance_records_from_csv()
        elif choice == '7':
            break
        else:
            print('Некорректный ввод.')

def calculator():
    while True:
        expression = input('Введите арифметическое выражение или "выход" для выхода: ')
        
        if expression.lower() == 'выход':
            break
        
        try:
            result = eval(expression)
            print(f'Результат: {result}')

        except Exception as e:
            print(f'Ошибка: {e}')


def main_menu():
    while True:
        print('''Добро пожаловать в Персональный помощник!
Выберите действие:
1. Управление заметками
2. Управление задачами
3. Управление контактами
4. Управление финансовыми записями
5. Калькулятор
6. Выход''')


        choice = input('Ваш выбор: ')
        
        if choice == '1':
            notes_menu()
        elif choice == '2':
            tasks_menu()
        elif choice == '3':
            contacts_menu()
        elif choice == '4':
            finance_menu()
        elif choice == '5':
            calculator()
        elif choice == '6':
            print('Выход из приложения.')
            break
        else:
            print('Некорректный ввод. Пожалуйста, попробуйте снова.')
