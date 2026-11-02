const DATE = '2 ноября 2026 г. / November 2, 2026';

const pages = {
    '/terms-and-policies/privacy-policy': {
        ruTitle: 'Политика конфиденциальности',
        enTitle: 'Privacy Policy',
        ru: `
            <h1>Политика конфиденциальности</h1>
            <p class="legal-date">Дата вступления в силу: ${DATE}</p>
            <p>Настоящая политика описывает, как физическое лицо «Т. Прохоров» (далее — «оператор») обрабатывает данные пользователей сервиса аутентификации Scarif (далее — «Scarif»). Scarif является единым сервисом входа для сервисов Universe — группы сервисов, принадлежащих и управляемых оператором.</p>
            <h2>1. Контакт оператора</h2>
            <p>По вопросам обработки данных, доступа, исправления или удаления данных напишите на <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>.</p>
            <h2>2. Какие данные мы получаем</h2>
            <ul><li>данные Google-профиля: идентификатор Google, email, имя, фамилия, отображаемое имя, фотография и язык/locale;</li><li>внутренние идентификаторы Scarif, дату создания аккаунта и сведения о связанных внешних аккаунтах;</li><li>данные сессий: IP-адрес, User-Agent, идентификатор сессии, время создания, срок действия и статус сессии;</li><li>технические сведения запросов и события безопасности, необходимые для работы и защиты сервиса.</li></ul>
            <p>Scarif не запрашивает специальные категории персональных данных, не использует данные для рекламы и не принимает решения исключительно автоматически, которые имеют юридические или аналогичные существенные последствия.</p>
            <h2>3. Цели и основания обработки</h2>
            <p>Данные используются для входа через Google, создания и обслуживания аккаунта, отображения профиля, выпуска и проверки токенов, управления сессиями, завершения сессий по запросу пользователя, предотвращения злоупотреблений, обеспечения безопасности и связи с пользователем по вопросам сервиса.</p>
            <p>Обработка основана на согласии пользователя, необходимости предоставить запрошенную услугу, законных интересах оператора по обеспечению безопасности и, когда применимо, выполнении требований закона.</p>
            <h2>4. Google и сервисы Universe</h2>
            <p>Для авторизации пользователь перенаправляется в Google. Scarif получает данные, разрешённые OAuth-областью <code>openid email profile</code>, и использует их для создания или обновления профиля. Условия и политика Google применяются отдельно.</p>
            <p>Сервисы Universe могут получать сгенерированные Scarif идентификаторы пользователя и сессии для единого входа. В дальнейшем отдельным сервисам может передаваться email, если это необходимо для их работы и будет отражено в соответствующем уведомлении. Оператор управляет сервисами Universe, но конкретный сервис может иметь собственные условия и уведомления.</p>
            <h2>5. Cookies и хранение</h2>
            <p>Scarif использует одну строго необходимую HttpOnly-cookie <code>scarif_refresh_token</code> для поддержания авторизации и защиты сессии. Cookie действует до 90 дней, если сессия не завершена раньше. Рекламные, аналитические и иные необязательные cookies не используются.</p>
            <p>Данные хранятся на сервере в Нидерландах. Активные сессии действуют до 90 дней. Данные сессий удаляются в течение шести месяцев после истечения срока сессии. Остальные данные хранятся до удаления аккаунта или по иному основанию, предусмотренному законом.</p>
            <h2>6. Передача и защита</h2>
            <p>Доступ к данным имеет только оператор, за исключением Google и сервисов Universe в пределах, описанных выше, а также инфраструктурных поставщиков, если их привлечение необходимо для размещения и работы сервиса. Данные не продаются и не используются для таргетированной рекламы.</p>
            <p>Для защиты применяются HTTPS, HttpOnly-cookie, короткоживущие access-токены, ротация refresh-токенов и управление отдельными сессиями. Ни один способ передачи или хранения данных не может гарантировать абсолютную безопасность.</p>
            <h2>7. Права пользователя</h2>
            <p>Пользователь может запросить доступ к своим данным, их исправление, удаление, ограничение обработки или информацию о получателях. Запрос направляется на <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>; оператор может попросить подтвердить личность. Удаление аккаунта сейчас выполняется вручную по запросу; в дальнейшем будет доступна функция удаления на сайте. Ограничения могут применяться, если хранение необходимо по закону, для безопасности или защиты требований.</p>
            <p>Пользователь может прекратить использование Scarif в любое время, выйти из аккаунта и закрыть активные сессии. Отзыв согласия не влияет на законность обработки, выполненной до отзыва.</p>
            <h2>8. Изменения</h2>
            <p>При существенных изменениях оператор обновит дату и опубликует новую редакцию. Для продолжения использования Scarif после существенного изменения может потребоваться повторное согласие.</p>
        `,
        en: `
            <h1>Privacy Policy</h1>
            <p class="legal-date">Effective date: ${DATE}</p>
            <p>This policy explains how the individual “T. Prokhorov” (the “Operator”) processes users’ data in the Scarif authentication service (“Scarif”). Scarif provides single sign-on for Universe services, a group of services owned and operated by the Operator.</p>
            <h2>1. Operator contact</h2><p>For privacy, access, correction, or deletion requests, contact <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>.</p>
            <h2>2. Data we receive</h2><ul><li>Google profile data: Google identifier, email, first name, last name, display name, profile picture, and locale;</li><li>Scarif-generated identifiers, account creation date, and linked external-account details;</li><li>session data: IP address, User-Agent, session identifier, creation time, expiry time, and session status;</li><li>technical request and security-event data needed to operate and protect the service.</li></ul>
            <p>Scarif does not request special categories of personal data, use data for advertising, or make solely automated decisions producing legal or similarly significant effects.</p>
            <h2>3. Purposes and legal grounds</h2><p>Data is used to authenticate through Google, create and maintain an account, display a profile, issue and verify tokens, manage sessions, close sessions at the user’s request, prevent abuse, maintain security, and communicate about the service.</p><p>Processing is based on the user’s consent, the need to provide the requested service, the Operator’s legitimate interests in security, and, where applicable, legal obligations.</p>
            <h2>4. Google and Universe services</h2><p>Authentication redirects the user to Google. Scarif receives data permitted by the <code>openid email profile</code> OAuth scopes and uses it to create or update the profile. Google’s terms and privacy policy apply separately.</p><p>Universe services may receive Scarif-generated user and session identifiers for single sign-on. In the future, an email address may be shared with an individual service where needed for its operation and described in the applicable notice. The Operator manages Universe services, but a particular service may have its own terms and notices.</p>
            <h2>5. Cookies and retention</h2><p>Scarif uses one strictly necessary HttpOnly cookie, <code>scarif_refresh_token</code>, to maintain authentication and secure the session. It lasts up to 90 days unless the session is closed earlier. No advertising, analytics, or other optional cookies are used.</p><p>Data is stored on a server in the Netherlands. Active sessions last up to 90 days. Session data is deleted within six months after a session expires. Other data is retained until account deletion or another period required by law.</p>
            <h2>6. Sharing and security</h2><p>Only the Operator can access the data, except for Google and Universe services as described above, and infrastructure providers where needed to host and operate the service. Data is not sold or used for targeted advertising.</p><p>Safeguards include HTTPS, an HttpOnly cookie, short-lived access tokens, refresh-token rotation, and per-session management. No method of transmission or storage can guarantee absolute security.</p>
            <h2>7. User rights</h2><p>Users may request access, correction, deletion, restriction of processing, or information about recipients by emailing <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>. The Operator may request identity verification. Account deletion is currently handled manually on request; a self-service deletion feature will be added in the future. Legal, security, or claims-preservation requirements may limit deletion.</p><p>Users may stop using Scarif, log out, and close active sessions at any time. Withdrawal of consent does not affect processing already carried out lawfully.</p>
            <h2>8. Changes</h2><p>For material changes, the Operator will update the date and publish a new version. Continued use after a material change may require renewed consent.</p>
        `
    },
    '/terms-and-policies/user-agreement': {
        ruTitle: 'Пользовательское соглашение', enTitle: 'User Agreement / Terms of Use',
        ru: `<h1>Пользовательское соглашение</h1><p class="legal-date">Дата вступления в силу: ${DATE}</p><p>Настоящее соглашение регулирует использование сервиса аутентификации Scarif оператором — физическим лицом «Т. Прохоров». Scarif предоставляет единый вход в сервисы Universe.</p><h2>1. Принятие</h2><p>Нажимая «Войти через Google», пользователь подтверждает, что прочитал и принимает настоящее соглашение и Политику конфиденциальности. Если пользователь не согласен, он не должен использовать Scarif.</p><h2>2. Возраст и аккаунт</h2><p>Scarif предназначен для лиц от 16 лет. Пользователь отвечает за достоверность данных, безопасность своего устройства и действия, совершённые через его аккаунт. Авторизация осуществляется через Google; Scarif не хранит пароль Google.</p><h2>3. Разрешённое использование</h2><p>Scarif можно использовать только законным способом и для доступа к сервисам Universe. Запрещены обход защиты, попытки получить доступ к чужому аккаунту, автоматизированное злоупотребление, вмешательство в работу сервиса, распространение вредоносного кода и использование сервиса для нарушения прав третьих лиц или закона.</p><h2>4. Сессии и выход</h2><p>Сервис создаёт отдельную сессию для устройства, позволяет просматривать активные сессии и завершать другие сессии. Для безопасности оператор может закрыть сессию или ограничить доступ.</p><h2>5. Universe и Google</h2><p>Scarif — инфраструктура единого входа, а отдельные сервисы Universe могут иметь собственные условия. Google является независимым поставщиком авторизации; его услуги регулируются его собственными правилами.</p><h2>6. Доступность и ответственность</h2><p>Scarif предоставляется бесплатно и «как есть». Оператор не гарантирует бесперебойность, отсутствие ошибок или постоянную доступность конкретного сервиса Universe. В пределах, разрешённых законом, оператор не отвечает за косвенные убытки, действия Google, отдельного сервиса Universe или пользователя, а также за невозможность входа по причинам вне разумного контроля оператора.</p><h2>7. Приостановка и прекращение</h2><p>Пользователь может прекратить использование Scarif в любое время. Оператор может временно ограничить или прекратить доступ при нарушении соглашения, угрозе безопасности, технических работах или прекращении работы Scarif.</p><h2>8. Изменения и право</h2><p>Изменения публикуются на сайте. При существенном изменении для дальнейшего использования может потребоваться повторное согласие. К соглашению применяется право Российской Федерации, если императивные нормы применимого права не требуют иного.</p><h2>9. Контакт</h2><p>Вопросы направляйте на <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>.</p>`,
        en: `<h1>User Agreement / Terms of Use</h1><p class="legal-date">Effective date: ${DATE}</p><p>This agreement governs use of the Scarif authentication service by the Operator, the individual “T. Prokhorov”. Scarif provides single sign-on for Universe services.</p><h2>1. Acceptance</h2><p>By clicking “Sign in with Google”, the user confirms that they have read and accept these Terms and the Privacy Policy. If the user disagrees, they must not use Scarif.</p><h2>2. Age and account</h2><p>Scarif is intended for people aged 16 or older. Users are responsible for accurate information, device security, and activity through their account. Authentication is provided by Google; Scarif does not store the Google password.</p><h2>3. Permitted use</h2><p>Scarif may be used lawfully and to access Universe services. Circumventing security, accessing another person’s account, automated abuse, disrupting the service, distributing malicious code, and violating third-party rights or law are prohibited.</p><h2>4. Sessions and logout</h2><p>The service creates a separate session for a device, shows active sessions, and allows other sessions to be closed. The Operator may close a session or restrict access for security reasons.</p><h2>5. Universe and Google</h2><p>Scarif is single-sign-on infrastructure, while individual Universe services may have their own terms. Google is an independent authentication provider governed by its own rules.</p><h2>6. Availability and liability</h2><p>Scarif is provided free of charge and “as is”. The Operator does not guarantee uninterrupted operation, absence of errors, or continued availability of any particular Universe service. To the extent permitted by law, the Operator is not liable for indirect losses, Google’s or a Universe service’s actions, user actions, or failures caused outside the Operator’s reasonable control.</p><h2>7. Suspension and termination</h2><p>Users may stop using Scarif at any time. The Operator may restrict or terminate access for a breach, security risk, maintenance, or discontinuation of Scarif.</p><h2>8. Changes and governing law</h2><p>Changes are published on the website. Material changes may require renewed consent for continued use. These Terms are governed by the laws of the Russian Federation, subject to mandatory rules of applicable law.</p><h2>9. Contact</h2><p>Contact <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>.</p>`
    },
    '/terms-and-policies/cookie-policy': {
        ruTitle: 'Политика использования cookie', enTitle: 'Cookie Policy',
        ru: `<h1>Политика использования cookie</h1><p class="legal-date">Дата вступления в силу: ${DATE}</p><p>Scarif использует только одну строго необходимую cookie: <code>scarif_refresh_token</code>. Она содержит токен продолжения авторизации, устанавливается с атрибутами HttpOnly и Secure в рабочей среде и не используется для рекламы, аналитики или отслеживания поведения.</p><h2>Назначение</h2><p>Cookie необходима, чтобы Scarif мог распознать авторизованную сессию, обновлять короткоживущий access-токен и выполнять выход из аккаунта. Срок действия — до 90 дней, либо до завершения сессии.</p><h2>Управление</h2><p>Пользователь может удалить cookie в настройках браузера, но после этого потребуется войти снова. Поскольку cookie строго необходима для запрошенной функции авторизации, отказ от неё делает вход и поддержание сессии невозможными.</p><h2>Контакт</h2><p>Вопросы: <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>.</p>`,
        en: `<h1>Cookie Policy</h1><p class="legal-date">Effective date: ${DATE}</p><p>Scarif uses only one strictly necessary cookie: <code>scarif_refresh_token</code>. It contains a session refresh token, is set with HttpOnly and Secure attributes in production, and is not used for advertising, analytics, or behavioral tracking.</p><h2>Purpose</h2><p>The cookie is necessary for Scarif to recognize an authenticated session, refresh a short-lived access token, and log the user out. It lasts up to 90 days or until the session is closed.</p><h2>Controls</h2><p>Users can delete the cookie in their browser settings, but will then need to sign in again. Because it is strictly necessary for the requested authentication function, refusing it makes sign-in and session continuation unavailable.</p><h2>Contact</h2><p>Questions: <a href="mailto:prohor2568@gmail.com">prohor2568@gmail.com</a>.</p>`
    }
};

export function renderLegalPage(container, path) {
    const page = pages[path] || pages['/terms-and-policies/privacy-policy'];
    document.title = `Scarif - ${page.ruTitle}`;
    document.body.classList.remove('login-page', 'profile-page');
    document.body.classList.add('legal-page-body');
    container.innerHTML = `
<main class="legal-page">
    <header class="legal-header">
        <a href="/" class="legal-brand">
            <img src="/icon/scarif_icon_big.svg" alt="">
            <span>Scarif</span>
        </a>
        <a href="/">Вернуться к входу / Back to sign in</a>
    </header>
    <article class="legal-card">
        <div class="legal-toolbar">
            <div class="legal-tabs" role="tablist">
                <button type="button" class="legal-tab active" data-language="ru" role="tab" aria-selected="true">
                    Русский
                </button>
                <button type="button" class="legal-tab" data-language="en" role="tab" aria-selected="false">
                    English
                </button>
            </div>
        </div>
        <section class="legal-content" data-content-language="ru" lang="ru">${page.ru}</section>
        <section class="legal-content hidden" data-content-language="en" lang="en">${page.en}</section>
    </article>
    <footer class="legal-footer">
        <a href="/terms-and-policies/privacy-policy">Privacy / Конфиденциальность</a> ·
        <a href="/terms-and-policies/user-agreement">Terms / Соглашение</a> ·
        <a href="/terms-and-policies/cookie-policy">Cookies / Cookie Policy</a>
    </footer>
</main>`;
    container.querySelectorAll('.legal-tab').forEach(tab => tab.addEventListener('click', () => {
        const language = tab.dataset.language;
        container.querySelectorAll('.legal-tab').forEach(item => {
            const selected = item === tab;
            item.classList.toggle('active', selected);
            item.setAttribute('aria-selected', String(selected));
        });
        container.querySelectorAll('.legal-content').forEach(content => {
            content.classList.toggle('hidden', content.dataset.contentLanguage !== language);
        });
        document.title = `Scarif - ${language === 'ru' ? page.ruTitle : page.enTitle}`;
    }));
}
