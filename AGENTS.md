# Timely API Agent Instructions

## DDL Query Conventions

When creating database DDL for this project, follow these rules unless the user explicitly asks for a different style.

- Target database: MySQL.
- Use `InnoDB`, `utf8mb4`, and `utf8mb4_unicode_ci`.
- Table names use lower snake case with the `tb_` prefix.
  - Example: `tb_company`, `tb_department`, `tb_user`
- Column names use upper snake case.
  - Example: `COMPANY_SN`, `CREATE_DT`, `UPDATE_DT`
- Primary key columns use the `{DOMAIN}_SN` suffix and `BIGINT NOT NULL AUTO_INCREMENT`.
  - Example: `COMPANY_SN BIGINT NOT NULL AUTO_INCREMENT`
- Put common audit columns immediately after the primary key column in every table:
  - `CREATE_DT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `UPDATE_DT DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP`
- Put business columns after `CREATE_DT` and `UPDATE_DT`.
- Use `USE_YN CHAR(1) NOT NULL DEFAULT 'Y'` for soft-use flags.
- Prefer soft delete/use flags over physical deletes in schema design unless the user asks otherwise.
- Use explicit indexes for FK columns.
  - Example: `KEY IDX_USER_COMPANY_SN (COMPANY_SN)`
- Use explicit constraint names.
  - Example: `CONSTRAINT FK_USER_COMPANY FOREIGN KEY (COMPANY_SN) REFERENCES tb_company (COMPANY_SN)`
- Use explicit unique key names.
  - Example: `UNIQUE KEY UK_USER_LOGIN_ID (LOGIN_ID)`

## DDL Output Format

When the user asks for DDL, provide both:

1. `CREATE TABLE` statements.
2. Separate table and column comment statements.

Use `ALTER TABLE ... COMMENT = ...` for table comments and `ALTER TABLE ... MODIFY ... COMMENT ...` for column comments.

Example:

```sql
CREATE TABLE tb_company (
    COMPANY_SN BIGINT NOT NULL AUTO_INCREMENT,
    CREATE_DT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATE_DT DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    COMPANY_NM VARCHAR(200) NOT NULL,
    USE_YN CHAR(1) NOT NULL DEFAULT 'Y',
    PRIMARY KEY (COMPANY_SN)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE tb_company COMMENT = '회사';
ALTER TABLE tb_company MODIFY COMPANY_SN BIGINT NOT NULL AUTO_INCREMENT COMMENT '회사 일련번호';
ALTER TABLE tb_company MODIFY CREATE_DT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시';
ALTER TABLE tb_company MODIFY UPDATE_DT DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시';
ALTER TABLE tb_company MODIFY COMPANY_NM VARCHAR(200) NOT NULL COMMENT '회사명';
ALTER TABLE tb_company MODIFY USE_YN CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '사용 여부';
```

## Common Code Values

Use these code values by default for user-related schemas.

- `POSITION`: `STAFF`, `SENIOR_STAFF`, `ASSISTANT_MANAGER`, `MANAGER`, `DEPUTY_GENERAL_MANAGER`, `GENERAL_MANAGER`, `DIRECTOR`, `CEO`
- `USER_STATUS`: `ACTIVE`, `PENDING`, `LOCKED`, `WITHDRAWN`

## Kotlin/JPA Mapping Notes

When implementing entities from DDL:

- Match table and column names exactly using `@Table` and `@Column`.
- Keep entity packages aligned by domain under `io.github.timely.timelyapi`.
- Use nullable Kotlin types only for nullable DB columns.
- For database-generated audit columns, prefer `insertable = false, updatable = false` for `CREATE_DT`, and `insertable = false` or application-managed updates for `UPDATE_DT` depending on the existing pattern.
- Follow the existing `Menu` entity style unless a domain needs a stronger abstraction.
