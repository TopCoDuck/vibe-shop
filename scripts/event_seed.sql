-- =============================================================
-- 이벤트 시드 데이터
-- 실행: mysql -u viber -pviber vibeshop < scripts/event_seed.sql
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE events;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO events
  (badge, title, subtitle, description, background, text_color, accent_color, link, start_date, end_date, sort_order, active, created_at, updated_at)
VALUES
  ('🚀 로켓배송 특가',
   '여름 대특가\n최대 50% OFF',
   '전 상품 여름 할인 이벤트',
   '로켓배송 상품 한정 파격 할인! 오늘만 이 가격',
   'linear-gradient(135deg, #ef4444 0%, #f97316 100%)',
   '#ffffff', '#ffffff',
   '/products',
   '2026-06-01', '2026-06-30', 1, 1, NOW(), NOW()),

  ('👋 신규 회원 전용',
   '웰컴 혜택\n3종 패키지',
   '가입 즉시 쿠폰 자동 지급',
   '첫 구매 3,000원 할인 + 무료배송 + 포인트 추가 적립',
   'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
   '#ffffff', '#ffffff',
   '/signup',
   '2026-01-01', '2026-12-31', 2, 1, NOW(), NOW()),

  ('⚡ 타임딜',
   '오늘만!\n전자기기 20%',
   '재고 소진 시 조기 종료',
   '전자기기 카테고리 당일 한정 특가. 최대 20,000원 할인 쿠폰 증정',
   'linear-gradient(135deg, #0ea5e9 0%, #0074e4 100%)',
   '#ffffff', '#ffffff',
   '/events',
   '2026-06-01', '2026-06-07', 3, 1, NOW(), NOW()),

  ('🌿 식품 특가',
   '신선식품\n15% 할인전',
   '로켓프레시 · 냉장 · 냉동 전 품목',
   '식품 카테고리 한정, 15% 할인 쿠폰 다운받고 신선하게!',
   'linear-gradient(135deg, #10b981 0%, #059669 100%)',
   '#ffffff', '#ffffff',
   '/events',
   '2026-06-01', '2026-06-30', 4, 1, NOW(), NOW()),

  ('🎁 생활용품',
   '생활용품\n전 품목 세일',
   '주방·청소·욕실 인기 상품 특가',
   '생활용품 8,000원 이상 구매 시 2,000원 즉시 할인 쿠폰 적용',
   'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
   '#ffffff', '#ffffff',
   '/events',
   '2026-05-25', '2026-07-31', 5, 1, NOW(), NOW()),

  ('👗 패션 위크',
   '여름 패션\n최대 70% 세일',
   '여름 의류·잡화 인기 브랜드 특가',
   '여름 시즌 의류 대규모 할인! 20% 쿠폰 추가 증정',
   'linear-gradient(135deg, #ec4899 0%, #db2777 100%)',
   '#ffffff', '#ffffff',
   '/events',
   '2026-06-10', '2026-06-25', 6, 1, NOW(), NOW());
