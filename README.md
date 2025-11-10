# Universe

![](assets/universe.jpg)
Монорепозиторий для всех локальных разработок. Делится на
поддиректории по используемым языкам.

### Структура

##### [Jawa](jawa)

Java + Kotlin

##### [C3PO](c3po)

Bash + Batch

### Настройка окружения

##### Переменные окружения

`~/.profile` для bash или `~/.zprofile` для zsh

```sh
export UNIVERSE_HOME="path/to/repository/root"
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

source ~/.bashrc # для bash
source ~/.zshrc # для zsh
```
