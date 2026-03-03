import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

login = 'login@gmail.com'
password = 'pwd'
receiver = 'receiver@gmail.com'

code = "123456"
html = f"""
<html>
  <body>
    <p style="font-size: 24px;">Ваш код подтверждения:</p>
    <p style="font-size: 48px; font-weight: bold; color: red;">{code}</p>
  </body>
</html>
"""

message = MIMEMultipart("alternative")
message["From"] = login
message["To"] = receiver
message["Subject"] = "Код подтверждения"

text = f"Ваш код подтверждения: {code}"
message.attach(MIMEText(text, "plain"))
message.attach(MIMEText(html, "html"))

try:
    server = smtplib.SMTP("smtp.gmail.com", 587)
    server.starttls()
    server.login(login, password)
    server.sendmail(login, receiver, message.as_string())
    server.quit()
    print("Письмо успешно отправлено.")
except Exception as e:
    print("Ошибка при отправке письма: ", str(e))
