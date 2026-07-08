import os
import zipfile

def unzip_all():
    current_dir = os.getcwd()

    for file_name in os.listdir(current_dir):
        if file_name.lower().endswith('.zip'):
            zip_path = os.path.join(current_dir, file_name)

            print(f"\n[Обработка] Начало распаковки архива: {file_name}")
            success = True

            try:
                with zipfile.ZipFile(zip_path, 'r') as zip_ref:
                    for member in zip_ref.infolist():
                        if member.is_dir():
                            continue

                        filename_only = os.path.basename(member.filename)
                        if not filename_only:
                            continue

                        name, ext = os.path.splitext(filename_only)
                        target_path = os.path.join(current_dir, filename_only)

                        counter = 1
                        while os.path.exists(target_path):
                            new_filename = f"{name}_{counter}{ext}"
                            target_path = os.path.join(current_dir, new_filename)
                            counter += 1

                        with zipfile.ZipFile.open(zip_ref, member) as source:
                            with open(target_path, "wb") as target:
                                target.write(source.read())

                        final_name = os.path.basename(target_path)
                        if final_name != filename_only:
                            print(f"  -> Извлечен: {final_name} (переименован из-за совпадения)")
                        else:
                            print(f"  -> Извлечен: {final_name}")

            except zipfile.BadZipFile:
                print(f"[Ошибка] Файл {file_name} поврежден или не является ZIP-архивом.")
                success = False
            except Exception as e:
                print(f"[Ошибка] Не удалось обработать {file_name}. Причина: {e}")
                success = False

            if success:
                try:
                    os.remove(zip_path)
                    print(f"[Успех] Архив {file_name} успешно удален.")
                except Exception as e:
                    print(f"[Предупреждение] Не удалось удалить архив {file_name}, хотя файлы извлечены: {e}")

    print("\n[Готово] Все архивы обработаны.")


if __name__ == "__main__":
    unzip_all()
    input("\nНажмите Enter для выхода...")
