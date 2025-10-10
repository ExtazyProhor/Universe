import tkinter as tk
from tkinter import filedialog
from pdfrw import PdfWriter, PdfReader
import os


def select_single_pdf():
    filename = filedialog.askopenfilename(filetypes=[("PDF files", "*.pdf")])
    single_pdf_entry.delete(0, tk.END)
    single_pdf_entry.insert(0, filename)


def select_folder():
    folder_name = filedialog.askdirectory()
    folder_entry.delete(0, tk.END)
    folder_entry.insert(0, folder_name)


def process_files():
    single_pdf_path = single_pdf_entry.get()
    folder_path = folder_entry.get()

    if not single_pdf_path or not folder_path:
        result_label.config(text="Выберите файл с титульным листом и папку с файлами для замены")
        return

    title_reader = PdfReader(single_pdf_path)
    if len(title_reader.pages) < 1:
        result_label.config(text="Выбранный PDF файл пустой!")
        return

    new_folder_path = os.path.join(folder_path, 'title')
    if not os.path.exists(new_folder_path):
        os.makedirs(new_folder_path)
    for filename in os.listdir(folder_path):
        if filename.endswith(".pdf"):
            reader = PdfReader(os.path.join(folder_path, filename))
            writer = PdfWriter()
            writer.addpage(title_reader.getPage(0))
            for i in range(1, len(reader.pages)):
                writer.addpage(reader.getPage(i))
            writer.write(os.path.join(new_folder_path, filename))

    result_label.config(text="Обработка завершена")


root = tk.Tk()
root.title("Замена титульного листа")

single_pdf_label = tk.Label(root, text="Выберите PDF файл с титульным листом:")
single_pdf_label.grid(row=0, column=0, padx=5, pady=5)
single_pdf_entry = tk.Entry(root, width=50)
single_pdf_entry.grid(row=0, column=1, padx=5, pady=5)
single_pdf_button = tk.Button(root, text="Поиск файла", command=select_single_pdf)
single_pdf_button.grid(row=0, column=2, padx=5, pady=5)

folder_label = tk.Label(root, text="Выберите папку с PDF файлами для замены титульного листа")
folder_label.grid(row=1, column=0, padx=5, pady=5)
folder_entry = tk.Entry(root, width=50)
folder_entry.grid(row=1, column=1, padx=5, pady=5)
folder_button = tk.Button(root, text="Поиск папки", command=select_folder)
folder_button.grid(row=1, column=2, padx=5, pady=5)

process_button = tk.Button(root, text="Заменить", command=process_files)
process_button.grid(row=2, column=1, padx=5, pady=5)

result_label = tk.Label(root, text="Готово!")
result_label.grid(row=3, column=1, padx=5, pady=5)

root.mainloop()
