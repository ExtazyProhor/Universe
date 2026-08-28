import re
import os
import glob
import requests
from lxml import etree
import win32com.client as win32
import urllib3

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


def extract_style(file_path: str) -> str:
    stylesheets = []
    with open(file_path, 'r', encoding='windows-1251', errors='ignore') as f:
        header_chunk = f.read(8192)

    style_tags = re.findall(r'<\?xml-stylesheet.*?\?>', header_chunk)

    for tag in style_tags:
        match = re.search(r'href=["\'](.*?)["\']', tag)
        if match:
            url = match.group(1)
            if 'moex.com' in url and '_EN' not in url:
                stylesheets.append(url)

    if not stylesheets:
        raise ValueError("В заголовке XML не найдена подходящая интернет-ссылка на XSLT-стили MOEX.")

    print('Найдены стили:')
    for style in stylesheets:
        print("--- " + style)

    return stylesheets[0]


def map_xml_to_xlsx(file_path):
    file_base, _ = os.path.splitext(file_path)
    temp_html_path = file_base + ".html"

    formatted_name = format_file_name(file_base)
    final_xlsx_path = formatted_name + ".xlsx"
    i = 2
    while os.path.exists(final_xlsx_path):
        final_xlsx_path = formatted_name + "_" + str(i) + ".xlsx"
        i += 1

    print(f"\n[СТАРТ] Обработка файла: {os.path.basename(file_path)}")

    try:
        xslt_url = extract_style(file_path)
        print(f"  -> Найдена ссылка на стиль: {xslt_url}")

        print("  -> Скачивание XSLT-шаблона...")
        res = requests.get(xslt_url, timeout=10, verify=False)
        res.raise_for_status()

        print("  -> Парсинг XML и XSLT...")
        xml_tree = etree.parse(file_path)
        xslt_tree = etree.fromstring(res.content)

        print("  -> Применение XSLT-трансформации...")
        transform = etree.XSLT(xslt_tree)
        result_tree = transform(xml_tree)

        print(f"  -> Сохранение временного HTML: {os.path.basename(temp_html_path)}")
        with open(temp_html_path, "wb") as f:
            f.write(etree.tostring(result_tree, method="html", pretty_print=True))

        print("  -> Запуск Excel для конвертации...")
        excel = win32.Dispatch("Excel.Application")
        excel.Visible = False
        excel.DisplayAlerts = False

        wb = excel.Workbooks.Open(temp_html_path)

        wb.SaveAs(Filename=final_xlsx_path, FileFormat=51)
        wb.Close(SaveChanges=False)

        print(f"[УСПЕХ] Стилизованный файл сохранен: {os.path.basename(final_xlsx_path)}")

    except Exception as e:
        print(f"[ОШИБКА] Не удалось обработать файл {os.path.basename(file_path)}: {e}")

    finally:
        if 'excel' in locals():
            try:
                excel.Quit()
            except Exception:
                pass
        if os.path.exists(temp_html_path):
            try:
                os.remove(temp_html_path)
            except Exception:
                pass


pattern = r"^(.*).{7}_([a-zA-Z0-9]{1,12})_(.{3})_(\d{2})(\d{2})(\d{2})_.{9}$"


def format_file_name(file_name: str) -> str:
    print("### формирование имени для " + file_name)
    match = re.match(pattern, file_name)
    if not match:
        print('error: Имя файла не соответствует шаблону!')
        return file_name

    all_groups = match.groups()
    date = "20" + all_groups[5] + "." + all_groups[4] + "." + all_groups[3]
    doc_type = all_groups[1]
    identity = all_groups[2]
    print("### date=" + date + ", type=" + doc_type + ", id=" + identity)

    if doc_type == 'CCX10':
        name = 'Комиссии'
    elif doc_type == 'CCX43':
        name = 'Реестр сделок'
    elif doc_type == 'CCX84':
        name = 'Оценка обеспечения'
    elif doc_type == 'CCX99' and identity == 'EQ1':
        name = 'Движение средств ФР'
    elif doc_type == 'CCX99' and identity == 'CU1':
        name = 'Движение средств ВР'
    else:
        name = doc_type + "_" + identity
    return all_groups[0] + date + " " + name


if __name__ == "__main__":
    current_dir = os.path.dirname(os.path.abspath(__file__))
    print(f"Сканирование директории: {current_dir}")

    xml_files = glob.glob(os.path.join(current_dir, "*.xml")) + glob.glob(os.path.join(current_dir, "*.XML"))
    xml_files = list(set(xml_files))

    if not xml_files:
        print("В текущей папке не найдено ни одного файла с расширением .xml")
    else:
        print(f"Найдено файлов для обработки: {len(xml_files)}")

        for index, file in enumerate(xml_files, start=1):
            print(f"\n--- Файл {index} из {len(xml_files)} ---")
            map_xml_to_xlsx(file)

        print("\n[ЗАВЕРШЕНО] Все файлы успешно обработаны!")
        input("\nНажмите Enter для выхода...")
