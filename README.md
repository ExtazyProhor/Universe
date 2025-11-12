# Universe

![](assets/universe.jpg)
Монорепозиторий для всех локальных разработок. Делится на
поддиректории по используемым языкам или предметным областям.

### Структура

##### [Jawa](jawa)

Java + Kotlin

##### [C3PO](c3po)

Bash + Batch

##### [Sarlacc](sarlacc)

Python

##### [Sabacc](sabacc)

Игры на Unity или LibGDX + текстуры и заготовки для них

### Настройка окружения

##### Клонирование репозитория

```sh
git clone --branch main https://github.com/ExtazyProhor/Universe.git $UNIVERSE_HOME
```

##### Переменные окружения

`~/.profile` для bash или `~/.zprofile` для zsh

```sh
export UNIVERSE_HOME="path/to/repository/root"
export UNIVERSE_WORKSPACE="path/to/workspace/dir"
export HOLOCRON_JSON="path/to/holocron.json"
export SPRING_PROFILES_ACTIVE=stable # для сервера

source ~/.profile # для bash
source ~/.zprofile # для zsh
```

##### `alias`-ы

`~/.bashrc` для bash или `~/.zshrc` для zsh

```sh
alias venator="$UNIVERSE_HOME/jawa/venator/venator.sh"
alias uni="$UNIVERSE_HOME/c3po/uni/uni.sh"

chmod +x "$UNIVERSE_HOME/jawa/venator/venator.sh"
chmod +x "$UNIVERSE_HOME/c3po/uni/uni.sh"

source ~/.bashrc # для bash
source ~/.zshrc # для zsh
```

##### Дополнительно для `bash`

Создать или открыть файл `~/.bash_profile`, в который написать:

```sh
if [ -f ~/.profile ]; then
    source ~/.profile
fi

if [ -f ~/.bashrc ]; then
    source ~/.bashrc
fi
```

##### Для IDEA

`Edit Configurations...` -> `Environment variables`:

```sh
UNIVERSE_HOME=path/to/repository/root; UNIVERSE_WORKSPACE=path/to/workspace/dir; HOLOCRON_JSON=path/to/holocron.json
```
