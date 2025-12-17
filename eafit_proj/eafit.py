import streamlit as st
import numpy as np
import matplotlib.pyplot as plt

st.title("Радиационная обработка мяса — расчёты и графики")

exp_doses = np.array([0, 250, 500, 750, 1000, 1500, 2000, 4000, 5000, 10000])
exp_bacteria = np.array([1000, 770, 530, 215, 53, 0, 0, 0, 0, 0])
exp_bacteria_err = exp_bacteria * 0.05

exp_protein = np.array([100, 97, 94, 89, 86, 82, 71, 50, 48, 22])
exp_protein_err = exp_protein * 0.05

exp_acid = np.array([90, 89, 87, 85, 83, 81, 79, 77, 74, 67])
exp_acid_err = exp_acid * 0.05

exp_fat = np.array([100, 98, 95, 90, 87, 83, 79, 60, 58, 30])
exp_fat_err = exp_fat * 0.05

products = {
    "Говядина": {
        "protein": 95.66,
        "acid_num": 81.43,
        "fat": 98.87,
        "start_contamination": 1000.0,
        "alpha": 0.0005,
        "beta_protein": 0.0001,
        "beta_fat": 0.0001,
        "N_lim": 10000,
        "_lambda": 1.0,
        "price_wholesale": 190.0,
        "price_retail": 1000.0,
        "processing_cost": 20.0,
        "batch_size": 10000
    },
    "Курица": {
        "protein": 94.56,
        "acid_num": 72.55,
        "fat": 97.18,
        "start_contamination": 1000.0,
        "alpha": 0.0006,
        "beta_protein": 0.00012,
        "beta_fat": 0.00011,
        "N_lim": 10000,
        "_lambda": 1.0,
        "price_wholesale": 150.0,
        "price_retail": 800.0,
        "processing_cost": 15.0,
        "batch_size": 10000
    },
    "Форель": {
        "protein": 99.66,
        "acid_num": 85.26,
        "fat": 100.0,
        "start_contamination": 1000.0,
        "alpha": 0.00055,
        "beta_protein": 0.00009,
        "beta_fat": 0.0001,
        "N_lim": 10000,
        "_lambda": 1.0,
        "price_wholesale": 250.0,
        "price_retail": 1200.0,
        "processing_cost": 25.0,
        "batch_size": 10000
    }
}

choice = st.radio("Выберите продукт", ["Говядина", "Курица", "Форель", "Свои данные"])

def input_custom_data():
    st.sidebar.header("Введите свои параметры")
    protein = st.sidebar.number_input("Белки, %", min_value=0.0, max_value=200.0, value=50.0)
    acid_num = st.sidebar.number_input("Кислотное число, %", min_value=0.0, max_value=200.0, value=1.0)
    fat = st.sidebar.number_input("Жиры, %", min_value=0.0, max_value=200.0, value=20.0)
    start_contamination = st.sidebar.number_input("Стартовая контаминация (N0), КОЕ/г", min_value=1.0, value=1000.0)
    alpha = st.sidebar.number_input("Скорость снижения микробиологии α, 1/Гр", min_value=0.0, format="%.7f", value=0.0005)
    beta_protein = st.sidebar.number_input("Скорость снижения белков β, 1/Гр", min_value=0.0, format="%.7f", value=0.0001)
    beta_fat = st.sidebar.number_input("Скорость снижения жиров β, 1/Гр", min_value=0.0, format="%.7f", value=0.0001)
    N_lim = st.sidebar.number_input("Предельная контаминация (N_lim), КОЕ/г", min_value=1.0, value=10000.0)
    _lambda = st.sidebar.number_input("λ (коэффициент списания), 1/день", min_value=0.0, format="%.5f", value=1.0)
    price_wholesale = st.sidebar.number_input("Оптовая цена, руб./кг", min_value=0.0, value=190.0)
    price_retail = st.sidebar.number_input("Розничная цена, руб./кг", min_value=0.0, value=1000.0)
    processing_cost = st.sidebar.number_input("Цена обработки 1 единицы, руб.", min_value=0.0, value=20.0)
    batch_size = st.sidebar.number_input("Размер партии, кг", min_value=1, value=10000)
    return {
        "protein": protein,
        "acid_num": acid_num,
        "fat": fat,
        "start_contamination": start_contamination,
        "alpha": alpha,
        "beta_protein": beta_protein,
        "beta_fat": beta_fat,
        "N_lim": N_lim,
        "_lambda": _lambda,
        "price_wholesale": price_wholesale,
        "price_retail": price_retail,
        "processing_cost": processing_cost,
        "batch_size": batch_size
    }

params = input_custom_data() if choice == "Свои данные" else products[choice]

def n_dose(D, alpha):
    return params["start_contamination"] * np.exp(-alpha * D)

def efficiency_target(D, alpha_param):
    N_D = params["start_contamination"] * np.exp(-alpha_param * D)
    return 1 - N_D / params["start_contamination"]

def efficiency_non_target(D, beta_param, Pc=95):
    P_D = Pc * np.exp(-beta_param * D) + (100 - Pc)
    return 1 - P_D / 100

def microorganism_growth(t, N0, eps=1.0, delt=0.5e-4):
    return (eps * N0) / ((eps - delt * N0) * np.exp(-eps * t) + delt * N0)

def H_function(D, alpha_param, beta_protein, beta_fat):
    eff_target = efficiency_target(D, alpha_param)
    eff_protein = efficiency_non_target(D, beta_protein, params["protein"])
    eff_fat = efficiency_non_target(D, beta_fat, params["fat"])
    return eff_target * (1 - eff_protein) * (1 - eff_fat)

dose_range = np.linspace(0, 10000, 1000)
H_vals = H_function(dose_range, params["alpha"], params["beta_protein"], params["beta_fat"])
D_opt_index = np.argmax(H_vals)
H_max = H_vals[D_opt_index]
threshold = 0.9 * H_max
valid_indices = np.where(H_vals >= threshold)[0]
D_min = dose_range[valid_indices[0]]
D_max = dose_range[valid_indices[-1]]

time_range = np.linspace(0, 10, 200)
N_no = params["start_contamination"]
N_Dmin = n_dose(D_min, params["alpha"])
N_Dmax = n_dose(D_max, params["alpha"])

growth_no = microorganism_growth(time_range, N_no)
growth_Dmin = microorganism_growth(time_range, N_Dmin)
growth_Dmax = microorganism_growth(time_range, N_Dmax)

def time_to_limit(growth_curve, threshold):
    indices = np.where(growth_curve >= threshold)[0]
    return time_range[indices[0]] if len(indices) > 0 else np.inf

T_no = time_to_limit(growth_no, params["N_lim"])
T_min = time_to_limit(growth_Dmin, params["N_lim"])
T_max = time_to_limit(growth_Dmax, params["N_lim"])

def expected_profit(T, lam, P_retail, P_wholesale):
    return (1 - np.exp(-lam * T)) * P_retail - P_wholesale

profit_no = expected_profit(T_no, params["_lambda"], params["price_retail"], params["price_wholesale"])
profit_min = expected_profit(T_min, params["_lambda"], params["price_retail"], params["price_wholesale"]) - params["processing_cost"]
profit_max = expected_profit(T_max, params["_lambda"], params["price_retail"], params["price_wholesale"]) - params["processing_cost"]

total_profit_no = profit_no * params["batch_size"]
total_profit_min = profit_min * params["batch_size"]
total_profit_max = profit_max * params["batch_size"]

st.header(f"Результаты для {choice}")
st.markdown(f"**Оптимальный диапазон дозы обработки:** от {D_min:.1f} Гр до {D_max:.1f} Гр")
st.markdown(f"**Срок хранения необработанного продукта:** {T_no:.2f} дней")
st.markdown(f"**Срок хранения после обработки (D_min):** {T_min:.2f} дней")
st.markdown(f"**Срок хранения после обработки (D_max):** {T_max:.2f} дней")
st.markdown(f"**Прибыль с необработки (на 1 кг):** {profit_no:.2f} руб.")
st.markdown(f"**Прибыль после обработки (D_min, на 1 кг):** {profit_min:.2f} руб.")
st.markdown(f"**Прибыль после обработки (D_max, на 1 кг):** {profit_max:.2f} руб.")
st.markdown(f"**Общая прибыль необработанного продукта:** {total_profit_no:.0f} руб.")
st.markdown(f"**Общая прибыль обработанного продукта (D_min):** {total_profit_min:.0f} руб.")
st.markdown(f"**Общая прибыль обработанного продукта (D_max):** {total_profit_max:.0f} руб.")

fig, axs = plt.subplots(1, 3, figsize=(18, 5))
axs[0].errorbar(exp_doses, exp_protein, yerr=exp_protein_err, fmt='o', capsize=5, color='mediumorchid')
axs[0].set_xlabel('Доза облучения, Гр')
axs[0].set_ylabel('Белок, %')
axs[0].set_title('Содержание белка от дозы облучения')
axs[0].grid(True)
axs[1].errorbar(exp_doses, exp_acid, yerr=exp_acid_err, fmt='o', capsize=5, color='deeppink')
axs[1].set_xlabel('Доза облучения, Гр')
axs[1].set_ylabel('Кислотное число, %')
axs[1].set_title('Кислотное число от дозы облучения')
axs[1].grid(True)
axs[2].errorbar(exp_doses, exp_fat, yerr=exp_fat_err, fmt='o', capsize=5, color='lightcoral')
axs[2].set_xlabel('Доза облучения, Гр')
axs[2].set_ylabel('Жир, %')
axs[2].set_title('Содержание жира от дозы облучения')
axs[2].grid(True)
st.pyplot(fig)

fig2, ax2 = plt.subplots(figsize=(7, 5))
ax2.errorbar(exp_doses, exp_bacteria, yerr=exp_bacteria_err, fmt='o', capsize=5, color='darkmagenta', label='Экспериментальные данные')
ax2.set_xlabel('Доза облучения, Гр')
ax2.set_ylabel('КМАиФАМ, КОЕ/г')
ax2.set_title('Зависимость концентрации бактерий от дозы облучения')
ax2.legend()
ax2.grid(True)
st.pyplot(fig2)

fig3, ax3 = plt.subplots(figsize=(10, 5))
ax3.plot(dose_range, H_vals, color='salmon', linewidth=2)
ax3.axvline(D_min, color='red', linestyle='--', label=f'D_min = {D_min:.1f} Гр')
ax3.axvline(D_max, color='red', linestyle='--', label=f'D_max = {D_max:.1f} Гр')
ax3.fill_between(dose_range, 0, H_vals, where=(dose_range >= D_min) & (dose_range <= D_max), color='lightcoral', alpha=0.4)
ax3.set_xlabel("Доза облучения, Гр")
ax3.set_ylabel("H(D)")
ax3.set_title("Функция оптимизации радиационной обработки")
ax3.legend()
ax3.grid(True)
st.pyplot(fig3)

fig4, ax4 = plt.subplots(figsize=(10, 5))
ax4.plot(time_range, growth_no, color='lightcoral', linewidth=2, label="Необработанный")
ax4.plot(time_range, growth_Dmin, color='darkorchid', linewidth=2, label=f"D_min={D_min:.1f} Гр")
ax4.plot(time_range, growth_Dmax, color='blue', linewidth=2, label=f"D_max={D_max:.1f} Гр")
ax4.axhline(params["N_lim"], color='red', linestyle='--', label="Предел контаминации")
ax4.set_xlabel("Время хранения, дни")
ax4.set_ylabel("КМАФАнМ, КОЕ/г")
ax4.set_title("Рост микроорганизмов во времени")
ax4.legend()
ax4.grid(True)
st.pyplot(fig4)
