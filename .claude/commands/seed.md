# /project:seed — DB 시드 데이터 삽입

$ARGUMENTS 에 시드 파일명이 있으면 그것만 실행, 없으면 목록을 보여줘.

## 사용 가능한 시드 파일

| 파일 | 대상 테이블 | 설명 |
|------|------------|------|
| `faq_seed.sql` | `faqs` | FAQ 16건 |
| `coupon_seed.sql` | `coupons`, `user_coupons` | 쿠폰 9건 |
| `event_seed.sql` | `events` | 이벤트 6건 |

## 실행 방법

지정된 파일이 있으면:
```bash
mysql -u viber -pviber --default-character-set=utf8mb4 vibeshop < scripts/{파일명}
```

실행 후 해당 테이블 row 수 확인해서 몇 건 삽입됐는지 알려줘.

파일명 없이 호출되면 위 목록을 보여주고 어떤 파일을 실행할지 물어봐.
