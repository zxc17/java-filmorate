# java-filmorate
Template repository for Filmorate project.
![Схема БД](https://github.com/zxc17/java-filmorate/blob/add-friends-likes/DB.png)
Добавление друзей выполнено по схеме соцсети ВКонтакте.

При запросе на дружбу запрос сохраняется в таблице request_friend, на этот момент никто
никому не друг. При подтверждении/отклонении вторым пользователем запроса на дружбу
запись из request_friend удаляется. При подтверждении в таблицу friends добавляются
соответствующие записи.
