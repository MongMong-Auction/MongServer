drop table if exists user;
create table user(
    email			varchar(50)							comment '사용자 아이디',
    oauth			varchar(10)							comment 'OAuth 서비스 이름',
    password		varchar(100)						comment '비밀번호',
    user_name		varchar(50)		not null			comment	'사용자 이름',
    role			varchar(20)		default 'USER'		comment	'사용자 권한',
    point			bigint			default	100			comment '포인트',
    theme			int				default	0			comment '테마 설정 값',
    fail_cnt		int				default	0			comment '로그인 실패 횟수',
    lock_yn		    varchar(1)		default	'N'			comment '잠금 여부',
    last_login		date			default now()		comment '최근 로그인 시간',
    create_date	    datetime		default now()		comment '생성 시간',
    modified_date	datetime							comment '수정 시간',

    primary key (email)
) comment '사용자 관리 테이블' DEFAULT CHARSET=utf8mb4;

drop table if exists auth_token;
create table auth_token(
    email			varchar(50)		comment '사용자 아이디',
    refresh_token	varchar(100)	comment '인증 토큰',

    primary key (email, refresh_token),

    foreign key (email) references user (email) on delete cascade
) comment '토큰 관리 테이블' DEFAULT CHARSET=utf8mb4;

drop table if exists item;
create table item(
    id			bigint			auto_increment		comment '식별 값',
    email		varchar(50)							comment	'등록자 아이디',
    title		varchar(100)	not null			comment	'경매 제목',
    content	    varchar(3000)	not null			comment	'경매 소개',
    price		bigint			default	0			comment	'시작 가격',
    str_date	datetime		not null			comment	'경매 시작 시간',
    win_price	bigint			default 0			comment '낙찰 가격',
    create_date datetime		default now()		comment '등록 시간',

    primary key (id),

    foreign key (email) references user (email) on delete cascade
) comment '경매 관리 테이블' DEFAULT CHARSET=utf8mb4;

drop table if exists item_img;
create table item_img(
    item_id		bigint			comment '경매 식별 값',
    sort_no		bigint			comment '이미지 순서',
    img			varchar(1000),

    primary key (item_id, sort_no),

    foreign key (item_id) 	references item (id) on delete cascade
) comment '경매 이미지 관리 테이블' DEFAULT CHARSET=utf8mb4;

drop table if exists auction;
create table auction(
    item_id		bigint							comment	'경매 식별 값',
    sort_no		bigint			not null		comment '입찰 순서',
    email		varchar(50)						comment	'입찰자 아이디',
    bid			bigint			not null		comment '입찰가',
    bid_date	datetime		default now()	comment	'입찰 시간',

    primary key (item_id, sort_no),

    foreign key (item_id) 	references item (id)    on delete cascade,
    foreign key (email) 	references user (email) on delete cascade
) comment '경매 진행 관리 테이블' DEFAULT CHARSET=utf8mb4;

drop table if exists item_likes;
create table item_likes(
    id			bigint		auto_increment		comment	'관심목록 식별 값',
    email		varchar(50)						comment	'사용자',
    item_id		bigint							comment	'경매 식별 값',

    primary key (id),
    
    foreign key (email) 	references user (email) on delete cascade,
    foreign key (item_id) 	references item (id)    on delete cascade
) comment '관심목록 관리 테이블' DEFAULT CHARSET=utf8mb4;