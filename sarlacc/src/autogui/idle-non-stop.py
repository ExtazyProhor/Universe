from typing import Any
import pyautogui
import keyboard
import threading
import time
from PIL import ImageGrab
import itertools
import random
import sys
import requests
import json
import base64
from io import BytesIO
from datetime import datetime

pyautogui.PAUSE = 0

chests_cols = [416, 610, 804, 997, 1187, 1365, 1563, 1750, 1944, 2132]
chests_rows = [528, 728, 915]
white_pixels_chests = [
    (910, 352),
    (1126, 360),
    # (1488, 358),
    (1604, 358)
]
other_pixels_chests = [
    (28, 822),
    (30, 1000),
    (29, 1204),
    (28, 299),
    (347, 27),
    (777, 28),
    (1145, 28),
    (1854, 27),
    (2338, 28)
]
red_loss_pixels_chests = [
    (1237, 360),
    (1029, 360),
    (905, 359),
    (1338, 372)
]

green_x2_pixels_chests = [
    (1350, 354),
    (1647, 354),
    (1105, 375),
    (1623, 350),
    (1409, 361),
    (1020, 371),
    (895, 349),
    (1034, 364),
    (1183, 356),
    (859, 332),
    (832, 361),
    (851, 373),
    (919, 361),
    (970, 360),
    (1009, 377),
    (1033, 367),
    (1049, 376),
    (1097, 364),
    (1143, 357),
    (1207, 365),
    (1323, 347),
    (1357, 328),
    (1393, 363),
    (1449, 345),
    (1505, 352),
    (1528, 374),
    (1541, 348),
    (1715, 339),
    (1720, 351),
    (1720, 381),
    (1674, 329),
    (1682, 374),
    (911, 348)
]

white_pixels_bonus_level = [
    (892, 261),
    (1004, 271),
    (1099, 271),
    (1195, 270),
    (1391, 272),
    (1487, 270),
    (1583, 270),
    (1278, 549),
    (1085, 539)
]
other_pixels_bonus_level = [
    (785, 266),
    (784, 447),
    (784, 622),
    (786, 899),
    (1051, 158),
    (1238, 160),
    (1457, 161)
]

sucker_punch_white_pixels = [
    (1015, 345),
    (927, 366),
    (1343, 368),
    (1436, 348),
    (1602, 374),
    (1608, 328),
    (1260, 377),
    (1101, 355),
    (1076, 379),
    (1016, 381),
    (1358, 352),
    (1307, 361),
    (1271, 362),
    (1235, 359),
    (1525, 350),
    (1579, 352),
    (1600, 353),
    (1054, 359),
    (1007, 359),
    (942, 358),
    (894, 352)
]

sucker_punch_dark_pixels = [
    (845, 327),
    (881, 320),
    (967, 339),
    (1058, 338),
    (1027, 358),
    (1022, 393),
    (1131, 368),
    (1143, 338),
    (1173, 319),
    (1217, 336),
    (1248, 336),
    (1377, 348),
    (1426, 345),
    (1464, 319),
    (1510, 360),
    (1709, 327),
    (1713, 365),
    (1707, 376),
    (1278, 377),
    (1234, 381),
    (1202, 382),
    (1216, 336),
    (1299, 353),
    (1369, 316),
    (1357, 312),
    (1400, 381),
    (1376, 320),
    (1684, 357),
    (875, 343)
]


def get_pixel_color(x_crd: int, y_crd: int) -> tuple[int, int, int]:
    return get_pixel_color_tuple((x_crd, y_crd))


def get_pixel_color_tuple(xy_crds: tuple[int, int]) -> Any:
    return ImageGrab.grab().getpixel(xy_crds)


def is_in_range(number: int, color_range: tuple[int, int]):
    value_from, value_to = color_range
    return value_from <= number <= value_to


def all_pixels_are_white(pixels: list[tuple[int, int]]) -> bool:
    for pixel in pixels:
        x_crd, y_crd = pixel
        if get_pixel_color(x_crd, y_crd) != (255, 255, 255):
            return False
    return True


def all_pixels_in_rgb_range(
        pixels: list[tuple[int, int]],
        red_range: tuple[int, int],
        green_range: tuple[int, int],
        blue_range: tuple[int, int]
) -> bool:
    for pixel in pixels:
        x_crd, y_crd = pixel
        r, g, b = get_pixel_color(x_crd, y_crd)
        if not is_in_range(r, red_range) or not is_in_range(g, green_range) or not is_in_range(b, blue_range):
            return False
    return True


def is_pixel_red(x_crd: int, y_crd: int) -> bool:
    r, g, b = get_pixel_color(x_crd, y_crd)
    return is_in_range(r, (160, 190)) and g < 2 and b < 2


def is_pixel_green(x_crd: int, y_crd: int) -> bool:
    r, g, b = get_pixel_color(x_crd, y_crd)
    return r < 12 and is_in_range(g, (120, 180)) and b < 12


def is_chests_started() -> bool:
    return all_pixels_in_rgb_range(other_pixels_chests, (205, 210), (190, 195), (112, 116))


def is_chests_lost() -> bool:
    for red_loss_pixel in red_loss_pixels_chests:
        r, g, b = get_pixel_color_tuple(red_loss_pixel)
        if r == 255 and g == 0 and b == 0:
            return True
    return False


def is_found_x2() -> bool:
    for green_x2_pixel in green_x2_pixels_chests:
        r, g, b = get_pixel_color_tuple(green_x2_pixel)
        if r == 0 and g == 255 and b == 0:
            return True
    return False


def is_mimic_found() -> bool:
    for i in range(0, 5):
        time.sleep(0.4)
        if is_chests_lost():
            for j in range(0, 3):
                pyautogui.click()
            time.sleep(8)
            return True
    return False


def open_chests():
    time.sleep(8)
    combinations = list(itertools.product(chests_cols, chests_rows))
    random.shuffle(combinations)
    opened_chests = 0
    found_mimics = 0
    active_guards = guardians_count

    guards_positions = []
    for i in range(guardians_count):
        guard_position = get_guard_position()
        combinations.remove(guard_position)
        guards_positions.append(guard_position)
    for i in range(first_mimics_skip_count):
        combination = combinations.pop()
        pyautogui.click(x=combination[0], y=combination[1])
        opened_chests += 1
        if is_mimic_found():
            found_mimics += 1
        elif is_found_x2() and active_guards > 0:
            active_guards += 1
            break
    for guard_position in guards_positions:
        pyautogui.click(x=guard_position[0], y=guard_position[1])
        opened_chests += 1
        time.sleep(2)
    is_win = False

    for combination in combinations:
        if 26 == opened_chests - found_mimics:
            is_win = True
            break
        pyautogui.click(x=combination[0], y=combination[1])
        opened_chests += 1
        if is_mimic_found():
            found_mimics += 1
            if active_guards == 0:
                if is_sucker_punch_used():
                    log('punch used')
                    share_screen('sucker punch used')
                    continue
                break
            active_guards -= 1

    share_screen(f'chests with prize: {opened_chests - found_mimics}')
    if is_win:
        sys.exit()
    pyautogui.click(x=1026, y=1317)


def get_guard_position() -> tuple[int, int]:
    for i in range(0, 10):
        for j in range(0, 3):
            x_crd = 357 + i * 190
            y_crd = 590 + j * 190
            if get_pixel_color(x_crd, y_crd) == (255, 235, 4):
                return chests_cols[i], chests_rows[j]
    send_text_message('cannot find guard')
    sys.exit()


def send_text_message(message: str):
    response = request({
        'chatId': 923043436,
        'message': message
    })
    if not response:
        print('error', response)


def share_screen(message: str):
    screenshot = ImageGrab.grab()
    buffered = BytesIO()
    screenshot.save(buffered, format='PNG')

    response = request({
        'chatId': 923043436,
        'image': base64.b64encode(buffered.getvalue()).decode('utf-8'),
        'message': [
            message
        ]
    })
    if not response:
        timestamp = datetime.now().strftime('screenshot-%Y-%m-%d-%H-%M-%S.png')
        screenshot.save(timestamp, format='PNG')


def request(data: dict) -> bool:
    if not server_available:
        print('request:', data)
        return True
    url = 'http://192.168.1.136:8083/send_to_telegram'
    headers = {
        'Content-Type': 'application/json'
    }
    return requests.post(url, data=json.dumps(data), headers=headers).status_code == 200


def is_bonus_level_started() -> bool:
    return all_pixels_are_white(white_pixels_bonus_level) and all_pixels_in_rgb_range(
        other_pixels_bonus_level,
        (212, 227),
        (158, 163),
        (115, 120)
    )


def is_sucker_punch_used() -> bool:
    return all_pixels_are_white(sucker_punch_white_pixels) and all_pixels_in_rgb_range(
        sucker_punch_dark_pixels,
        (15, 25),
        (7, 17),
        (23, 33)
    )


last_minion_claim_time = 0
minion_claim_interval = 15 * 60  # 15 minutes in seconds

minion_claim_buttons_positions = [
    (188, 137),
    (633, 1314),
    (642, 296),
    (642, 296),
    (1112, 1317)
]


def minions_claim():
    global last_minion_claim_time
    current_time = time.time()
    if current_time - last_minion_claim_time < minion_claim_interval:
        return
    for position in minion_claim_buttons_positions:
        pyautogui.click(x=position[0], y=position[1])
        time.sleep(0.3)
    last_minion_claim_time = current_time
    log('minions claimed')


def is_bonus_level_exit_button_active() -> bool:
    return is_pixel_red(1572, 1074) and is_pixel_red(1310, 1077)


def swipe(from_crd: tuple[int, int], to_crd: tuple[int, int]):
    from_x, from_y = from_crd
    to_x, to_y = to_crd
    pyautogui.moveTo(x=from_x, y=from_y, duration=0.2)
    pyautogui.mouseDown()
    pyautogui.moveTo(x=to_x, y=to_y, duration=0.5)
    pyautogui.mouseUp()


def skip_bonus_level():
    up_green = 0
    down_green = 0
    for y_crd in range(1000, 1350, 10):
        if is_pixel_green(1280, y_crd):
            if up_green == 0:
                up_green = y_crd
            down_green = y_crd
    middle_y_crd = (up_green + down_green) // 2

    swipe_position_1 = (1626, middle_y_crd)
    swipe_position_2 = (920, middle_y_crd)
    swipe(swipe_position_1, swipe_position_2)
    if is_bonus_level_started():
        swipe(swipe_position_2, swipe_position_1)
    while not is_bonus_level_exit_button_active():
        time.sleep(2)
    pyautogui.click(1572, 1074)
    time.sleep(5)


def default_boost_jumps_and_shoots():
    pyautogui.click(x=240, y=1231)  # speed boost button
    pyautogui.sleep(0.4)

    pyautogui.press('space')  # jump

    pyautogui.sleep(0.1)
    pyautogui.press('space')  # up shot
    pyautogui.sleep(0.55)
    pyautogui.press('space')  # jump
    pyautogui.sleep(0.45)  # before bow speed boost - 0.35
    pyautogui.press('space')  # down shot
    pyautogui.sleep(0.3)


def boost_jumps_and_shoots_with_following():
    if minions_unlocked:
        minions_claim()
    if rage_unlocked:
        pyautogui.click(x=2186, y=233)  # rage
    time.sleep(0.15)
    pyautogui.click(x=240, y=1231)  # speed boost button
    pyautogui.sleep(0.2)

    # long jump
    pyautogui.keyDown('space')
    time.sleep(0.3)
    pyautogui.keyUp('space')

    if bow_speed == 0:
        jump_timings = [0.15, 0.55, 0.35]
    elif bow_speed == 1:
        jump_timings = [0.25, 0.45, 0.5]
    else:
        jump_timings = [0.25, 0.15, 0.3, 0.5]

    for timing in jump_timings:
        pyautogui.sleep(timing)
        pyautogui.press('space')

    pyautogui.sleep(0.15)


def actions_with_jumps():
    if is_chests_unlocked and is_chests_started():
        log('chests started')
        open_chests()
        log('chests opened')
        return
    if is_bonus_level_started():
        log('bonus level started')
        if can_i_play_bonus_level:
            send_text_message('bonus level started!')
            toggle_clicking()
            return
        skip_bonus_level()
        log('bonus level skipped')
        return
    boost_jumps_and_shoots_with_following()


def autoclicker():
    global is_active
    while True:
        if is_active:
            if do_i_have_a_bow:
                actions_with_jumps()
        else:
            time.sleep(0.1)


is_active = False


def toggle_clicking():
    global is_active
    is_active = not is_active
    log(f'game mod switched, is_active={is_active}')


def game():
    log('cheat started')
    keyboard.add_hotkey('F6', toggle_clicking)
    click_thread = threading.Thread(target=autoclicker, daemon=True)
    click_thread.start()
    keyboard.wait('shift+esc')


def log(message: str):
    print(datetime.now(), message)


server_available = False
minions_unlocked = True
rage_unlocked = True
bow_speed = 1  # 2
guardians_count = 1  # 1
first_mimics_skip_count = 2
is_chests_unlocked = True
do_i_have_a_bow = True
can_i_play_bonus_level = False

game()
