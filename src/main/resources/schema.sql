create table if not exists USERS
(
    USER_ID   integer      not null primary key auto_increment,
    LOGIN     varchar(255) not null,
    USER_NAME varchar(255) not null,
    EMAIL     varchar(255) not null,
    BIRTHDAY  date         not null
);

create table if not exists FRIENDS
(
    USER_ID   integer not null references USERS (USER_ID) on delete cascade,
    FRIEND_ID integer not null references USERS (USER_ID) on delete cascade,
    primary key (USER_ID, FRIEND_ID)
);

create table if not exists MPA
(
    MPA_ID   integer primary key auto_increment,
    MPA_NAME varchar(255) not null
);

create table if not exists GENRES
(
    GENRE_ID   integer primary key auto_increment,
    GENRE_NAME varchar(255) not null
);

create table if not exists FILMS
(
    FILM_ID      integer primary key auto_increment,
    FILM_NAME    varchar(255) not null,
    DESCRIPTION  varchar(200) not null,
    RELEASE_DATE date         not null,
    DURATION     integer      not null,
    MPA_ID       integer      not null references MPA (MPA_ID)
);

create table if not exists LIKES
(
    FILM_ID integer not null references FILMS (FILM_ID) on delete cascade,
    USER_ID integer not null references USERS (USER_ID) on delete cascade,
    primary key (FILM_ID, USER_ID)
);

create table if not exists FILM_GENRE
(
    FILM_ID  integer not null references FILMS (FILM_ID) on delete cascade,
    GENRE_ID integer not null references GENRES (GENRE_ID),
    primary key (FILM_ID, GENRE_ID)
);

create table if not exists REVIEWS
(
    REVIEW_ID   integer      primary key auto_increment,
    CONTENT     varchar(200) not null,
    IS_POSITIVE boolean      not null,
    USER_ID     integer      not null references USERS (USER_ID) on delete cascade,
    FILM_ID     integer      not null references FILMS (FILM_ID) on delete cascade,
    USEFULNESS  integer      not null
);

create table if not exists REVIEW_LIKES
(
    REVIEW_ID   integer not null references REVIEWS (REVIEW_ID) on delete cascade,
    USER_ID     integer not null references USERS (USER_ID) on delete cascade,
    IS_POSITIVE boolean not null,
    primary key (REVIEW_ID, USER_ID)
);

create table if not exists DIRECTORS
(
    DIRECTOR_ID   integer primary key auto_increment,
    DIRECTOR_NAME varchar(255) not null
);

create table if not exists FILM_DIRECTOR
(
    FILM_ID  integer not null references FILMS (FILM_ID) on delete cascade,
    DIRECTOR_ID integer not null references DIRECTORS (DIRECTOR_ID) on delete cascade,
    primary key (FILM_ID, DIRECTOR_ID)
);

create table if not exists EVENTS
(
    EVENT_ID integer not null primary key auto_increment,
    TIME_STAMP bigint not null,
    EVENT_TYPE varchar(255) not null,
    OPERATION varchar(255) not null,
    USER_ID integer not null references USERS (USER_ID) on delete cascade,
    ENTITY_ID integer not null
);

